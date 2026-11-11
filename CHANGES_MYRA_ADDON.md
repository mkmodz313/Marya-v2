# MYRA add-on: modified / new files
(Original project is untouched where not listed. A copy of the original zip is the backup.)

## Modified (existing files)
- app/src/main/AndroidManifest.xml – new activities, VideoWallpaperService, ACCESS_NETWORK_STATE, live_wallpaper feature
- app/src/main/java/com/myra/assistant/services/ForegroundVoiceService.kt – transcripts→chat, reconnect/backoff, network watcher, phone-control hook, prompt lines
- app/src/main/java/com/myra/assistant/websocket/GeminiWebSocketClient.kt – shared OkHttp client, transcription, session resumption, stale-socket guard, goAway/close handling (same model, voice, key, audio format)
- app/src/main/java/com/myra/assistant/voice/AudioPlayer.kt – blocking queue (no 10 ms sleep), low-latency AudioTrack mode, dropPending(); lip-sync counters untouched
- app/src/main/java/com/myra/assistant/ui/main/MainActivity.kt – 3-button nav wiring (Chat/Voice/Settings)
- app/src/main/java/com/myra/assistant/ui/settings/SettingsActivity.kt – Live Wallpapers entry
- app/src/main/res/layout/activity_main.xml – bottom area replaced by the 3D glass 3-button bar (avatar/background untouched)
- app/src/main/res/layout/activity_settings.xml – Live Wallpapers card
- app/src/main/res/values/strings.xml – wallpaper description

## New
- data/ChatRepository.kt (shared persistent voice+text history)
- control/PhoneControl.kt (app launching, WhatsApp by number, confirm-before-send, websites, search)
- ui/chat/ChatActivity.kt, ChatMessagesAdapter.kt + layouts activity_chat, item_msg_*
- wallpaper/ CloudConfig, WallpaperItem, WallpaperRepository, ThumbLoader, VideoWallpaperService, WallpaperActivity, WallpaperDetailActivity + layouts
- assets/samples/ (2 real mp4 + thumbnails), res/xml/myra_wallpaper.xml, neon drawables/icons, drawable-nodpi/myra_avatar_thumb.png
- backend/ (admin web panel, firestore.rules, storage.rules, firebase.json, README_BACKEND.md)

## Later additions
- ui/main/SplashActivity (launcher) + splash_bg.jpg
- admin/AdminApi.kt, AdminLoginActivity.kt, AdminPanelActivity.kt (in-app password-protected admin panel, REST: Firebase Auth + Firestore + Storage)
- PhoneControl.executeTool + Gemini function calling (open_app, play_on_youtube, open_website, search_web, whatsapp_*, run_command) so voice commands really execute
- Settings & home 3D redesign, 4-button bottom card
