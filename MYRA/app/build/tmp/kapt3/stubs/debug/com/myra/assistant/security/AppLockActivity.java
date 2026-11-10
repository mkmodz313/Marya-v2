package com.myra.assistant.security;

/**
 * AppLockActivity — MYRA Complete Lock Screen
 */
@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\u009a\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\b\n\u0002\u0010\t\n\u0002\b\u0010\u0018\u0000 a2\u00020\u00012\u00020\u0002:\u0002`aB\u0007\u00a2\u0006\u0004\b\u0003\u0010\u0004J\u0012\u00103\u001a\u0002042\b\u00105\u001a\u0004\u0018\u000106H\u0014J\u0010\u00107\u001a\u0002042\u0006\u00108\u001a\u000209H\u0016J\b\u0010:\u001a\u000204H\u0002J\b\u0010;\u001a\u000204H\u0002J\u0010\u0010<\u001a\u0002042\u0006\u0010=\u001a\u000202H\u0002J\b\u0010>\u001a\u000204H\u0002J\u0010\u0010?\u001a\u0002042\u0006\u0010@\u001a\u00020 H\u0002J\b\u0010A\u001a\u000204H\u0002J\b\u0010B\u001a\u000204H\u0002J\u0010\u0010C\u001a\u0002042\u0006\u0010D\u001a\u00020EH\u0002J\b\u0010F\u001a\u000204H\u0002J\u0016\u0010G\u001a\u0002042\f\u0010H\u001a\b\u0012\u0004\u0012\u0002090IH\u0002J\b\u0010J\u001a\u000204H\u0002J\b\u0010K\u001a\u000204H\u0002J\u0010\u0010L\u001a\u0002042\u0006\u0010M\u001a\u00020)H\u0002J\b\u0010N\u001a\u000204H\u0002J\b\u0010O\u001a\u000204H\u0002J\u0010\u0010P\u001a\u0002042\u0006\u0010Q\u001a\u00020RH\u0002J\b\u0010S\u001a\u000204H\u0002J \u0010T\u001a\u0002042\u0006\u0010U\u001a\u0002092\u0006\u0010V\u001a\u00020)2\u0006\u0010W\u001a\u00020 H\u0002J\b\u0010X\u001a\u000204H\u0002J\u0018\u0010Y\u001a\u0002042\u0006\u0010Z\u001a\u00020 2\u0006\u0010[\u001a\u00020)H\u0002J\u0010\u0010\\\u001a\u0002042\u0006\u0010]\u001a\u00020\u000bH\u0002J\b\u0010^\u001a\u000204H\u0016J\b\u0010_\u001a\u000204H\u0014R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u000bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u0017X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u001dX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020 X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010!\u001a\u0004\u0018\u00010\"X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010#\u001a\u0004\u0018\u00010$X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010%\u001a\u00020&X\u0082\u000e\u00a2\u0006\u0004\n\u0002\u0010\'R\u000e\u0010(\u001a\u00020)X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010*\u001a\u00020)X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010+\u001a\u0004\u0018\u00010,X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010-\u001a\u00020.X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010/\u001a\u0004\u0018\u000100X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u00101\u001a\u000202X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006b"}, d2 = {"Lcom/myra/assistant/security/AppLockActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "Landroid/speech/tts/TextToSpeech$OnInitListener;", "<init>", "()V", "tabPin", "Landroid/widget/TextView;", "tabPattern", "tabVoice", "tabFinger", "pinSection", "Landroid/view/View;", "patternSection", "voiceSection", "fingerSection", "pinDisplay", "pinStatusText", "pinAttemptsText", "patternLockView", "Lcom/myra/assistant/security/PatternLockView;", "patternInstructionText", "patternAttemptsText", "voiceBtn", "Landroid/widget/ImageButton;", "voiceStatusText", "voiceInstructionText", "fingerBtn", "fingerStatusText", "lockoutOverlay", "Landroid/widget/LinearLayout;", "lockoutTimerText", "enteredPin", "", "tts", "Landroid/speech/tts/TextToSpeech;", "geminiClient", "Lcom/myra/assistant/ai/GeminiLiveClient;", "liveAudioManager", "error/NonExistentClass", "Lerror/NonExistentClass;", "isGeminiConnected", "", "isTtsReady", "speechRecognizer", "Landroid/speech/SpeechRecognizer;", "handler", "Landroid/os/Handler;", "lockoutRunnable", "Ljava/lang/Runnable;", "currentTab", "Lcom/myra/assistant/security/AppLockActivity$Tab;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "onInit", "status", "", "initViews", "setupTabs", "switchTab", "tab", "setupPinPad", "appendPin", "d", "backspacePin", "submitPin", "onPinWrong", "r", "Lcom/myra/assistant/security/SecurityManager$PinResult$WRONG;", "setupPattern", "verifyPattern", "pattern", "", "setupVoice", "setupFinger", "launchBiometric", "allowDeviceCredential", "startVoiceListen", "onSuccess", "startLockout", "seconds", "", "checkLockout", "speakWrong", "attempts", "lockedOut", "type", "initGemini", "speak", "text", "hindi", "shakeView", "v", "onBackPressed", "onDestroy", "Tab", "Companion", "app_debug"})
public final class AppLockActivity extends androidx.appcompat.app.AppCompatActivity implements android.speech.tts.TextToSpeech.OnInitListener {
    private android.widget.TextView tabPin;
    private android.widget.TextView tabPattern;
    private android.widget.TextView tabVoice;
    private android.widget.TextView tabFinger;
    private android.view.View pinSection;
    private android.view.View patternSection;
    private android.view.View voiceSection;
    private android.view.View fingerSection;
    private android.widget.TextView pinDisplay;
    private android.widget.TextView pinStatusText;
    private android.widget.TextView pinAttemptsText;
    private com.myra.assistant.security.PatternLockView patternLockView;
    private android.widget.TextView patternInstructionText;
    private android.widget.TextView patternAttemptsText;
    private android.widget.ImageButton voiceBtn;
    private android.widget.TextView voiceStatusText;
    private android.widget.TextView voiceInstructionText;
    private android.widget.ImageButton fingerBtn;
    private android.widget.TextView fingerStatusText;
    private android.widget.LinearLayout lockoutOverlay;
    private android.widget.TextView lockoutTimerText;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String enteredPin = "";
    @org.jetbrains.annotations.Nullable()
    private android.speech.tts.TextToSpeech tts;
    @org.jetbrains.annotations.Nullable()
    private com.myra.assistant.ai.GeminiLiveClient geminiClient;
    @org.jetbrains.annotations.Nullable()
    private error.NonExistentClass liveAudioManager;
    private boolean isGeminiConnected = false;
    private boolean isTtsReady = false;
    @org.jetbrains.annotations.Nullable()
    private android.speech.SpeechRecognizer speechRecognizer;
    @org.jetbrains.annotations.NotNull()
    private final android.os.Handler handler = null;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Runnable lockoutRunnable;
    @org.jetbrains.annotations.NotNull()
    private com.myra.assistant.security.AppLockActivity.Tab currentTab = com.myra.assistant.security.AppLockActivity.Tab.PATTERN;
    private static boolean isUnlockedThisSession = false;
    @org.jetbrains.annotations.NotNull()
    public static final com.myra.assistant.security.AppLockActivity.Companion Companion = null;
    
    public AppLockActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    public void onInit(int status) {
    }
    
    private final void initViews() {
    }
    
    private final void setupTabs() {
    }
    
    private final void switchTab(com.myra.assistant.security.AppLockActivity.Tab tab) {
    }
    
    private final void setupPinPad() {
    }
    
    private final void appendPin(java.lang.String d) {
    }
    
    private final void backspacePin() {
    }
    
    private final void submitPin() {
    }
    
    private final void onPinWrong(com.myra.assistant.security.SecurityManager.PinResult.WRONG r) {
    }
    
    private final void setupPattern() {
    }
    
    private final void verifyPattern(java.util.List<java.lang.Integer> pattern) {
    }
    
    private final void setupVoice() {
    }
    
    private final void setupFinger() {
    }
    
    private final void launchBiometric(boolean allowDeviceCredential) {
    }
    
    private final void startVoiceListen() {
    }
    
    private final void onSuccess() {
    }
    
    private final void startLockout(long seconds) {
    }
    
    private final void checkLockout() {
    }
    
    private final void speakWrong(int attempts, boolean lockedOut, java.lang.String type) {
    }
    
    private final void initGemini() {
    }
    
    private final void speak(java.lang.String text, boolean hindi) {
    }
    
    private final void shakeView(android.view.View v) {
    }
    
    @java.lang.Override()
    public void onBackPressed() {
    }
    
    @java.lang.Override()
    protected void onDestroy() {
    }
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fR\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0004\u0010\u0006\"\u0004\b\u0007\u0010\b\u00a8\u0006\r"}, d2 = {"Lcom/myra/assistant/security/AppLockActivity$Companion;", "", "<init>", "()V", "isUnlockedThisSession", "", "()Z", "setUnlockedThisSession", "(Z)V", "launch", "", "context", "Landroid/content/Context;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        public final boolean isUnlockedThisSession() {
            return false;
        }
        
        public final void setUnlockedThisSession(boolean p0) {
        }
        
        public final void launch(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
        }
    }
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0007\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007\u00a8\u0006\b"}, d2 = {"Lcom/myra/assistant/security/AppLockActivity$Tab;", "", "<init>", "(Ljava/lang/String;I)V", "PIN", "PATTERN", "VOICE", "FINGER", "app_debug"})
    public static enum Tab {
        /*public static final*/ PIN /* = new PIN() */,
        /*public static final*/ PATTERN /* = new PATTERN() */,
        /*public static final*/ VOICE /* = new VOICE() */,
        /*public static final*/ FINGER /* = new FINGER() */;
        
        Tab() {
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<com.myra.assistant.security.AppLockActivity.Tab> getEntries() {
            return null;
        }
    }
}