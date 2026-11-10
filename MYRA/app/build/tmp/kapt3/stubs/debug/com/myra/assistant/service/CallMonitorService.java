package com.myra.assistant.service;

/**
 * FIXED CallMonitorService — ALL CALL ANNOUNCE ISSUES RESOLVED:
 * 1. ✅ Contact name lookup properly working
 * 2. ✅ TTS properly initialized before speaking (fallback only)
 * 3. ✅ CallAssistantActivity launch with proper flags
 * 4. ✅ No number announced — only name or "Unknown Caller"
 * 5. ✅ Broadcast sent properly for MainActivity
 * 6. ✅ WhatsApp call detection added
 * 7. ✅ Proper permission checks
 */
@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 ,2\u00020\u00012\u00020\u0002:\u0001,B\u0007\u00a2\u0006\u0004\b\u0003\u0010\u0004J\b\u0010\u0011\u001a\u00020\u0012H\u0016J\u0010\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0014\u001a\u00020\u000eH\u0016J\b\u0010\u0015\u001a\u00020\u0012H\u0002J\u001a\u0010\u0016\u001a\u00020\u00122\b\u0010\u0017\u001a\u0004\u0018\u00010\u00182\u0006\u0010\u0019\u001a\u00020\fH\u0002J0\u0010\u001a\u001a\u00020\u00122\u0006\u0010\u001b\u001a\u00020\u00182\u0006\u0010\u001c\u001a\u00020\u00182\u0006\u0010\u001d\u001a\u00020\u00182\u0006\u0010\u001e\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\fH\u0002J\u0012\u0010\u001f\u001a\u00020\u00182\b\u0010\u001c\u001a\u0004\u0018\u00010\u0018H\u0002J\b\u0010 \u001a\u00020\u0012H\u0002J\b\u0010!\u001a\u00020\u0012H\u0002J\b\u0010\"\u001a\u00020#H\u0002J\"\u0010$\u001a\u00020\u000e2\b\u0010%\u001a\u0004\u0018\u00010&2\u0006\u0010\'\u001a\u00020\u000e2\u0006\u0010(\u001a\u00020\u000eH\u0016J\u0014\u0010)\u001a\u0004\u0018\u00010*2\b\u0010%\u001a\u0004\u0018\u00010&H\u0016J\b\u0010+\u001a\u00020\u0012H\u0016R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006-"}, d2 = {"Lcom/myra/assistant/service/CallMonitorService;", "Landroid/app/Service;", "Landroid/speech/tts/TextToSpeech$OnInitListener;", "<init>", "()V", "telephonyManager", "Landroid/telephony/TelephonyManager;", "phoneStateListener", "Landroid/telephony/PhoneStateListener;", "tts", "Landroid/speech/tts/TextToSpeech;", "isTtsReady", "", "lastState", "", "hasAnnouncedThisRing", "isProcessingCall", "onCreate", "", "onInit", "status", "setupPhoneListener", "handleIncomingCall", "rawNumber", "", "isWhatsAppCall", "launchCallAssistant", "callerName", "number", "userName", "personality", "resolveCallerName", "startForegroundCompat", "createNotificationChannel", "buildNotification", "Landroid/app/Notification;", "onStartCommand", "intent", "Landroid/content/Intent;", "flags", "startId", "onBind", "Landroid/os/IBinder;", "onDestroy", "Companion", "app_debug"})
public final class CallMonitorService extends android.app.Service implements android.speech.tts.TextToSpeech.OnInitListener {
    @org.jetbrains.annotations.Nullable()
    private android.telephony.TelephonyManager telephonyManager;
    @org.jetbrains.annotations.Nullable()
    private android.telephony.PhoneStateListener phoneStateListener;
    @org.jetbrains.annotations.Nullable()
    private android.speech.tts.TextToSpeech tts;
    private boolean isTtsReady = false;
    private int lastState = android.telephony.TelephonyManager.CALL_STATE_IDLE;
    private boolean hasAnnouncedThisRing = false;
    private boolean isProcessingCall = false;
    private static boolean isRunning = false;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_CALL_ACTIVE = "com.myra.assistant.CALL_ACTIVE";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_CALL_ENDED = "com.myra.assistant.CALL_ENDED";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_CALL_RINGING = "com.myra.assistant.CALL_RINGING";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "MYRA_CALL";
    @org.jetbrains.annotations.NotNull()
    public static final com.myra.assistant.service.CallMonitorService.Companion Companion = null;
    
    public CallMonitorService() {
        super();
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
    
    @java.lang.Override()
    public void onInit(int status) {
    }
    
    private final void setupPhoneListener() {
    }
    
    /**
     * FIXED: Proper call handling with name lookup
     * Uses WebSocket natural voice via CallAssistantActivity
     */
    private final void handleIncomingCall(java.lang.String rawNumber, boolean isWhatsAppCall) {
    }
    
    private final void launchCallAssistant(java.lang.String callerName, java.lang.String number, java.lang.String userName, java.lang.String personality, boolean isWhatsAppCall) {
    }
    
    /**
     * FIXED: Contact name resolution — NEVER returns number
     */
    private final java.lang.String resolveCallerName(java.lang.String number) {
        return null;
    }
    
    private final void startForegroundCompat() {
    }
    
    private final void createNotificationChannel() {
    }
    
    private final android.app.Notification buildNotification() {
        return null;
    }
    
    @java.lang.Override()
    public int onStartCommand(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent, int flags, int startId) {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public android.os.IBinder onBind(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent) {
        return null;
    }
    
    @java.lang.Override()
    public void onDestroy() {
    }
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0004\u0010\u0006\"\u0004\b\u0007\u0010\bR\u000e\u0010\t\u001a\u00020\nX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\nX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\nX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\nX\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/myra/assistant/service/CallMonitorService$Companion;", "", "<init>", "()V", "isRunning", "", "()Z", "setRunning", "(Z)V", "ACTION_CALL_ACTIVE", "", "ACTION_CALL_ENDED", "ACTION_CALL_RINGING", "TAG", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        public final boolean isRunning() {
            return false;
        }
        
        public final void setRunning(boolean p0) {
        }
    }
}