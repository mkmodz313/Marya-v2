package com.myra.assistant.ui.settings;

/**
 * FIXED SettingsActivity — ALL ORIGINAL FEATURES PRESERVED + NEW PERMISSION MANAGEMENT
 * Original: API Key, TTS Key, User Name, Prime Contact, Personality, Voice Engine,
 *          Live Mode, Device Admin, Security Settings, Contact Picker
 * NEW: Call Announce toggle, Permission grant button, Default Assistant button,
 *     Permissions status display
 */
@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\u0088\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0015\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u0000 ?2\u00020\u0001:\u0001?B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010&\u001a\u00020\'2\b\u0010(\u001a\u0004\u0018\u00010)H\u0014J\b\u0010*\u001a\u00020\'H\u0002J\b\u0010+\u001a\u00020\'H\u0002J\b\u0010,\u001a\u00020\'H\u0002J\b\u0010-\u001a\u00020\'H\u0002J-\u0010.\u001a\u00020\'2\u0006\u0010/\u001a\u0002002\u000e\u00101\u001a\n\u0012\u0006\b\u0001\u0012\u00020$0#2\u0006\u00102\u001a\u000203H\u0016\u00a2\u0006\u0002\u00104J\u0010\u00105\u001a\u00020\'2\u0006\u00106\u001a\u000207H\u0002J\b\u00108\u001a\u00020\'H\u0002J\u0010\u00109\u001a\u00020\'2\u0006\u0010:\u001a\u00020;H\u0002J\b\u0010<\u001a\u00020\'H\u0002J\b\u0010=\u001a\u00020\'H\u0002J\b\u0010>\u001a\u00020\'H\u0014R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u000bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0012X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u000eX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0012X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0010X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0010X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u0012X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u001cX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\u001eX\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020!0 X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\"\u001a\b\u0012\u0004\u0012\u00020$0#X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010%\u00a8\u0006@"}, d2 = {"Lcom/myra/assistant/ui/settings/SettingsActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "<init>", "()V", "apiKeyInput", "Landroid/widget/EditText;", "ttsApiKeyInput", "userNameInput", "primeNameInput", "primeNumberInput", "personalityGroup", "Landroid/widget/RadioGroup;", "voiceTypeGroup", "liveModeSwitch", "Landroid/widget/Switch;", "saveBtn", "Landroid/widget/Button;", "accessibilityStatus", "Landroid/widget/TextView;", "adminStatusText", "pickContactBtn", "Landroid/widget/ImageButton;", "callAnnounceSwitch", "callAnnounceStatusText", "grantPermissionsBtn", "setDefaultAssistantBtn", "permissionsStatusText", "devicePolicyManager", "Landroid/app/admin/DevicePolicyManager;", "componentName", "Landroid/content/ComponentName;", "contactPickerLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "Landroid/content/Intent;", "allPermissions", "", "", "[Ljava/lang/String;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "initViews", "loadPreferences", "setupListeners", "checkAndRequestPermissions", "onRequestPermissionsResult", "requestCode", "", "permissions", "grantResults", "", "(I[Ljava/lang/String;[I)V", "updateCallAnnounceStatus", "enabled", "", "updatePermissionsStatus", "handleContactResult", "uri", "Landroid/net/Uri;", "savePreferences", "updateStatus", "onResume", "Companion", "app_debug"})
public final class SettingsActivity extends androidx.appcompat.app.AppCompatActivity {
    private android.widget.EditText apiKeyInput;
    private android.widget.EditText ttsApiKeyInput;
    private android.widget.EditText userNameInput;
    private android.widget.EditText primeNameInput;
    private android.widget.EditText primeNumberInput;
    private android.widget.RadioGroup personalityGroup;
    private android.widget.RadioGroup voiceTypeGroup;
    private android.widget.Switch liveModeSwitch;
    private android.widget.Button saveBtn;
    private android.widget.TextView accessibilityStatus;
    private android.widget.TextView adminStatusText;
    private android.widget.ImageButton pickContactBtn;
    private android.widget.Switch callAnnounceSwitch;
    private android.widget.TextView callAnnounceStatusText;
    private android.widget.Button grantPermissionsBtn;
    private android.widget.Button setDefaultAssistantBtn;
    private android.widget.TextView permissionsStatusText;
    private android.app.admin.DevicePolicyManager devicePolicyManager;
    private android.content.ComponentName componentName;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<android.content.Intent> contactPickerLauncher = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String[] allPermissions = {"android.permission.RECORD_AUDIO", "android.permission.READ_CONTACTS", "android.permission.WRITE_CONTACTS", "android.permission.READ_PHONE_STATE", "android.permission.READ_CALL_LOG", "android.permission.CALL_PHONE", "android.permission.SEND_SMS", "android.permission.ANSWER_PHONE_CALLS", "android.permission.CAMERA", "android.permission.MODIFY_AUDIO_SETTINGS"};
    private static final int PERMISSIONS_REQUEST_CODE = 200;
    @org.jetbrains.annotations.NotNull()
    public static final com.myra.assistant.ui.settings.SettingsActivity.Companion Companion = null;
    
    public SettingsActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void initViews() {
    }
    
    private final void loadPreferences() {
    }
    
    private final void setupListeners() {
    }
    
    /**
     * NEW: Check and request all required permissions
     */
    private final void checkAndRequestPermissions() {
    }
    
    @java.lang.Override()
    public void onRequestPermissionsResult(int requestCode, @org.jetbrains.annotations.NotNull()
    java.lang.String[] permissions, @org.jetbrains.annotations.NotNull()
    int[] grantResults) {
    }
    
    private final void updateCallAnnounceStatus(boolean enabled) {
    }
    
    private final void updatePermissionsStatus() {
    }
    
    private final void handleContactResult(android.net.Uri uri) {
    }
    
    private final void savePreferences() {
    }
    
    private final void updateStatus() {
    }
    
    @java.lang.Override()
    protected void onResume() {
    }
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lcom/myra/assistant/ui/settings/SettingsActivity$Companion;", "", "<init>", "()V", "PERMISSIONS_REQUEST_CODE", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}