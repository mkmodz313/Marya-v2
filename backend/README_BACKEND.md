# MYRA – Backend setup (Firebase) & Admin Panel

The wallpaper catalog uses **Firebase** (Authentication + Firestore + Storage + Hosting).
Nothing below is "working" until YOU create the project and paste the config. Until then the app only shows the
two bundled sample wallpapers and says the cloud catalog is not configured.

## 1. Create the project (≈10 min)
1. https://console.firebase.google.com → **Add project**.
2. **Build → Authentication → Get started → Email/Password → Enable.**
3. **Build → Firestore Database → Create database** (production mode).
4. **Build → Storage → Get started.**
   NOTE: Google may require the **Blaze (pay-as-you-go) plan** to create a Storage bucket on new projects.
   If you do not want that, tell me and the same design can be done with Supabase.
5. **Project settings → Your apps → Add Web app** → copy the `firebaseConfig` values.

## 2. Create the administrator (this is the password protection)
1. Authentication → **Users → Add user**: your email + a strong password (only YOU know it; it is stored by Google, never in the APK or in these files).
2. Copy that user's **UID**.
3. Firestore → **Start collection** `admins` → Document ID = **that UID** → add any field (e.g. `role: "owner"`).
Only UIDs that have a document in `/admins` can write. The rules enforce this on the server; hiding the page is not relied upon.
Nobody can add themselves to `/admins` (rules: `allow write: if false`).

## 3. Deploy rules + admin site
```bash
npm i -g firebase-tools
firebase login
cd backend
firebase use --add            # choose your project
# edit admin/firebase-config.js with your web config first
firebase deploy --only firestore:rules,storage,hosting
```
Admin Panel URL: `https://<project-id>.web.app` → log in with the admin email/password.
(Optional: Authentication → Settings → Authorized domains; add a custom domain if you wish.)

## 4. Connect the Android app
Open `app/src/main/java/com/myra/assistant/wallpaper/CloudConfig.kt` and set
`FIREBASE_API_KEY` and `FIREBASE_PROJECT_ID` (same values as the web config). Rebuild the APK.
Use **Project settings → API key restrictions** to limit the key to Firestore API if you like.

## 5. How sync works
* Admin uploads video + thumbnail to Storage, writes a `wallpapers/{id}` document (`published: true`).
* The app reads `published == true` documents through the Firestore REST API. The open Wallpapers page
  **re-checks every 45 s** (+ on open + refresh button). This is periodic refresh, not push.
* The gallery loads **thumbnails only**. The video is streamed in the preview and downloaded **only** when
  the user taps *Set Wallpaper*.
* Unpublish/Delete in the admin panel removes it from users' galleries at the next refresh.
* Statistics: app sends `+1` to `downloads` (on set) and `views` (on preview). Rules allow only +1 on those two fields of published items.

## 6. Removing the sample wallpapers
Delete the two entries in `WallpaperItem.SAMPLES` (and `app/src/main/assets/samples`) when you have real content.

## 7. Security notes
* No admin password or secret is in the APK. The Firebase API key / project id are public identifiers.
* Storage objects are readable by anyone who has the download URL (needed so apps can stream thumbnails/videos);
  an *unpublished* item's URL is not listed anywhere but is not secret either.
* Change your admin password in the Firebase console at any time; add a 2nd admin by adding another UID to `/admins`.
