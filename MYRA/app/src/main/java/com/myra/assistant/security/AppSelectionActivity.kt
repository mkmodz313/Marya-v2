package com.myra.assistant.security

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myra.assistant.R
import kotlinx.coroutines.*

class AppSelectionActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val activityScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_selection)

        recyclerView = findViewById(R.id.appsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadApps()
    }

    private fun loadApps() {
        activityScope.launch {
            val apps = withContext(Dispatchers.IO) {
                val pm = packageManager
                val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                val lockedApps = SecurityManager.getLockedPackages(this@AppSelectionActivity)
                
                packages.filter { 
                    ((it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 && it.packageName != packageName)
                }.map { info ->
                    AppInfo(
                        name = info.loadLabel(pm).toString(),
                        packageName = info.packageName,
                        icon = info.loadIcon(pm),
                        isLocked = lockedApps.contains(info.packageName)
                    )
                }.sortedBy { it.name }
            }
            recyclerView.adapter = AppAdapter(apps)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityScope.cancel()
    }

    data class AppInfo(
        val name: String,
        val packageName: String,
        val icon: Drawable,
        var isLocked: Boolean
    )

    inner class AppAdapter(private val apps: List<AppInfo>) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

        inner class AppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val icon: ImageView = view.findViewById(R.id.appIcon)
            val name: TextView = view.findViewById(R.id.appName)
            val checkbox: CheckBox = view.findViewById(R.id.appCheckbox)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app_selection, parent, false)
            return AppViewHolder(view)
        }

        override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
            val app = apps[position]
            holder.icon.setImageDrawable(app.icon)
            holder.name.text = app.name
            holder.checkbox.isChecked = app.isLocked

            holder.itemView.setOnClickListener {
                app.isLocked = !app.isLocked
                holder.checkbox.isChecked = app.isLocked
                if (app.isLocked) {
                    SecurityManager.addLockedPackage(this@AppSelectionActivity, app.packageName)
                } else {
                    SecurityManager.removeLockedPackage(this@AppSelectionActivity, app.packageName)
                }
            }
        }

        override fun getItemCount() = apps.size
    }
}
