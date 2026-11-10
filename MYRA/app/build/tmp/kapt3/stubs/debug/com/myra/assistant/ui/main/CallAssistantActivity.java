package com.myra.assistant.ui.main;

/**
 * FIXED CallAssistantActivity — ALL ISSUES RESOLVED:
 * 1. ✅ WebSocket natural voice for call announcement (no robotic TTS)
 * 2. ✅ Proper call ANSWER — uses acceptRingingCall() correctly
 * 3. ✅ Proper call REJECT — uses endCall() correctly
 * 4. ✅ WhatsApp call announcement support
 * 5. ✅ State properly reset after call flow
 * 6. ✅ Commands work after call ends
 * 7. ✅ Multi-language command support
 */
@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000d\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u001b\u0018\u00002\u00020\u00012\u00020\u0002B\u0007\u00a2\u0006\u0004\b\u0003\u0010\u0004J\u0012\u0010\'\u001a\u00020(2\b\u0010)\u001a\u0004\u0018\u00010*H\u0014J\b\u0010+\u001a\u00020(H\u0002J\u0010\u0010,\u001a\u00020(2\u0006\u0010-\u001a\u00020.H\u0016J\b\u0010/\u001a\u00020(H\u0002J\b\u00100\u001a\u00020\u0013H\u0002J\b\u00101\u001a\u00020(H\u0002J\u0010\u00102\u001a\u00020(2\u0006\u00103\u001a\u00020\u0013H\u0002J\u0010\u00104\u001a\u00020(2\u0006\u00103\u001a\u00020\u0013H\u0002J\b\u00105\u001a\u00020(H\u0002J\u0010\u00106\u001a\u00020(2\u0006\u00107\u001a\u00020\u0013H\u0002J\b\u00108\u001a\u00020(H\u0002J\b\u00109\u001a\u00020\rH\u0002J\b\u0010:\u001a\u00020\rH\u0002J\b\u0010;\u001a\u00020(H\u0002J\b\u0010<\u001a\u00020\rH\u0002J\b\u0010=\u001a\u00020\rH\u0002J\b\u0010>\u001a\u00020(H\u0002J\b\u0010?\u001a\u00020(H\u0002J\b\u0010@\u001a\u00020(H\u0002J\b\u0010A\u001a\u00020(H\u0002J\u0010\u0010B\u001a\u00020\r2\u0006\u0010C\u001a\u00020\u0013H\u0002J\u0018\u0010D\u001a\u00020\u00132\u0006\u0010E\u001a\u00020\u00132\u0006\u0010F\u001a\u00020\u0013H\u0002J\b\u0010G\u001a\u00020(H\u0016J\b\u0010H\u001a\u00020(H\u0014R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\u001eX\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001f\u001a\u00020 X\u0082.\u00a2\u0006\u0004\n\u0002\u0010!R\u000e\u0010\"\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010#\u001a\u00020\u0013X\u0082D\u00a2\u0006\u0002\n\u0000R\u0010\u0010$\u001a\u00020%X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010&\u00a8\u0006I"}, d2 = {"Lcom/myra/assistant/ui/main/CallAssistantActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "Landroid/speech/tts/TextToSpeech$OnInitListener;", "<init>", "()V", "callerNameText", "Landroid/widget/TextView;", "statusText", "waveformView", "Lcom/myra/assistant/ui/main/WaveformView;", "tts", "Landroid/speech/tts/TextToSpeech;", "isTtsReady", "", "speechRecognizer", "Landroid/speech/SpeechRecognizer;", "handler", "Landroid/os/Handler;", "callerName", "", "phoneNumber", "userName", "personality", "isWhatsAppCall", "isDecisionMade", "isListening", "announcementPlayed", "isSpeaking", "isCallAnswered", "liveClient", "Lcom/myra/assistant/ai/GeminiLiveClient;", "liveAudioManager", "error/NonExistentClass", "Lerror/NonExistentClass;", "isLiveConnected", "TAG", "callStateReceiver", "Landroid/content/BroadcastReceiver;", "Landroid/content/BroadcastReceiver;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "initViews", "onInit", "status", "", "startAnnouncement", "buildAnnouncementText", "setupGeminiLive", "speakViaWebSocket", "text", "speakTTS", "startListening", "processCommand", "spoken", "performAnswer", "answerNormalCall", "answerWhatsAppCall", "performReject", "rejectNormalCall", "rejectWhatsAppCall", "repeatAnnouncement", "stopListening", "startCallStateMonitor", "safeFinish", "isNumberLike", "str", "getPrefsValue", "key", "default", "onBackPressed", "onDestroy", "app_debug"})
public final class CallAssistantActivity extends androidx.appcompat.app.AppCompatActivity implements android.speech.tts.TextToSpeech.OnInitListener {
    private android.widget.TextView callerNameText;
    private android.widget.TextView statusText;
    @org.jetbrains.annotations.Nullable()
    private com.myra.assistant.ui.main.WaveformView waveformView;
    @org.jetbrains.annotations.Nullable()
    private android.speech.tts.TextToSpeech tts;
    private boolean isTtsReady = false;
    @org.jetbrains.annotations.Nullable()
    private android.speech.SpeechRecognizer speechRecognizer;
    @org.jetbrains.annotations.NotNull()
    private final android.os.Handler handler = null;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String callerName = "Unknown Caller";
    @org.jetbrains.annotations.NotNull()
    private java.lang.String phoneNumber = "";
    @org.jetbrains.annotations.NotNull()
    private java.lang.String userName = "Sir";
    @org.jetbrains.annotations.NotNull()
    private java.lang.String personality = "gf";
    private boolean isWhatsAppCall = false;
    private boolean isDecisionMade = false;
    private boolean isListening = false;
    private boolean announcementPlayed = false;
    private boolean isSpeaking = false;
    private boolean isCallAnswered = false;
    private com.myra.assistant.ai.GeminiLiveClient liveClient;
    private error.NonExistentClass liveAudioManager;
    private boolean isLiveConnected = false;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String TAG = "MYRA_CALL_UI";
    @org.jetbrains.annotations.NotNull()
    private final android.content.BroadcastReceiver callStateReceiver = null;
    
    public CallAssistantActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void initViews() {
    }
    
    @java.lang.Override()
    public void onInit(int status) {
    }
    
    /**
     * ✅ FIXED: WebSocket natural voice for announcement
     * No more robotic TTS — uses Gemini Live audio streaming
     */
    private final void startAnnouncement() {
    }
    
    private final java.lang.String buildAnnouncementText() {
        return null;
    }
    
    /**
     * ✅ FIXED: Setup Gemini Live with proper prompt for call scenario
     */
    private final void setupGeminiLive() {
    }
    
    /**
     * ✅ NEW: Speak via WebSocket (natural voice)
     */
    private final void speakViaWebSocket(java.lang.String text) {
    }
    
    /**
     * Fallback TTS (only used if WebSocket fails)
     */
    private final void speakTTS(java.lang.String text) {
    }
    
    private final void startListening() {
    }
    
    /**
     * ✅ FIXED: Proper answer/reject detection with multi-language support
     */
    private final void processCommand(java.lang.String spoken) {
    }
    
    /**
     * ✅ FIXED: Call ANSWER — proper logic, no confusion with reject
     */
    private final void performAnswer() {
    }
    
    /**
     * ✅ NEW: Separate method for normal call answer
     */
    private final boolean answerNormalCall() {
        return false;
    }
    
    /**
     * ✅ NEW: Answer WhatsApp call via accessibility
     */
    private final boolean answerWhatsAppCall() {
        return false;
    }
    
    /**
     * ✅ FIXED: Call REJECT — proper endCall(), no confusion
     */
    private final void performReject() {
    }
    
    /**
     * ✅ NEW: Separate method for normal call reject
     */
    private final boolean rejectNormalCall() {
        return false;
    }
    
    /**
     * ✅ NEW: Reject WhatsApp call via accessibility
     */
    private final boolean rejectWhatsAppCall() {
        return false;
    }
    
    private final void repeatAnnouncement() {
    }
    
    private final void stopListening() {
    }
    
    private final void startCallStateMonitor() {
    }
    
    private final void safeFinish() {
    }
    
    private final boolean isNumberLike(java.lang.String str) {
        return false;
    }
    
    private final java.lang.String getPrefsValue(java.lang.String key, java.lang.String p1_772401952) {
        return null;
    }
    
    @java.lang.Override()
    public void onBackPressed() {
    }
    
    @java.lang.Override()
    protected void onDestroy() {
    }
}