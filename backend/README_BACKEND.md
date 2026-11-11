# MYRA – Backend & Admin Panel (Firebase project: mkmodzboy2026)

## Admin Panel is INSIDE the app
Settings (very bottom) → **Admin Panel** → email + password login → wallpaper upload / manage page.
No website, no `firebase deploy` and no computer are needed for this. (`backend/admin/` is an optional web version; ignore it.)

The password is never stored in the APK or on the phone. It is checked by Google's sign-in server, and the
Firestore/Storage rules (below) only let `umarmalang496@gmail.com` write - even someone who modifies the APK cannot.

## One-time setup in the Firebase console (phone browser is fine)
1. Authentication → Sign-in method → **Email/Password enabled** (done).
2. Authentication → Users → **Add user**: `umarmalang496@gmail.com` + your password.
3. Authentication → Settings → User actions → turn OFF "Enable create (sign-up)".
4. **Firestore Database → Create database** (production mode) → Rules tab → paste `backend/firestore.rules` → Publish.
5. **Storage → Get started** → Rules tab → paste `backend/storage.rules` → Publish.
   (Google may ask for the Blaze plan before it creates a Storage bucket.)

## Daily use
Open app → Settings → Admin Panel → login → choose video (+ optional thumbnail; if none, it is made from the video)
→ Title, Description, Category → "Turant publish" → UPLOAD & SAVE. Keep the app open until it says done.
Published wallpapers appear for every user (gallery refreshes on open, every 45 s while open, and via the refresh button).
Unpublish / Edit / Delete are on the same page. Statistics (sets / views) update automatically.
