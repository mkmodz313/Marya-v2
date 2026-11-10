# MYRA – Backend & Admin Panel (project: mkmodzboy2026)

Config is already filled in (`CloudConfig.kt`, `backend/admin/firebase-config.js`, `.firebaserc`).
The admin password is NOT stored anywhere in this project or in the APK.

## Steps you must do in the Firebase console (https://console.firebase.google.com → mkmodzboy2026)
1. **Authentication → Sign-in method → Email/Password → Enable.**
2. **Authentication → Users → Add user** → email `umarmalang496@gmail.com` + your password (type it yourself there).
3. **Authentication → Settings → User actions → turn OFF "Enable create (sign-up)"** so nobody else can register accounts.
4. **Firestore Database → Create database** (production mode).
5. **Storage → Get started** (Google may ask for the Blaze plan).

## Deploy rules + Admin Panel website
```bash
npm i -g firebase-tools
firebase login            # log in with the Google account that owns the project
cd backend
firebase deploy --only firestore:rules,storage,hosting
```
Admin Panel: https://mkmodzboy2026.web.app (also reachable from the app: Settings → Admin Panel, last card).

## Who can log in
Only `umarmalang496@gmail.com`: the page signs everybody else out, and the Firestore/Storage rules reject writes from any other account (server-side).

## Sync
Publish in the panel → app gallery refreshes automatically (on open, every 45 s while open, refresh button). Gallery loads thumbnails only; video is downloaded only when a user taps Set Wallpaper.
