package com.myra.assistant.security;

/**
 * SecuritySettingsActivity — MYRA Security Configuration
 */
@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000l\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000e\n\u0002\b\u0006\u0018\u0000 E2\u00020\u00012\u00020\u0002:\u0001EB\u0007\u00a2\u0006\u0004\b\u0003\u0010\u0004J\u0012\u0010)\u001a\u00020*2\b\u0010+\u001a\u0004\u0018\u00010,H\u0014J\u0010\u0010-\u001a\u00020*2\u0006\u0010.\u001a\u00020\"H\u0016J\b\u0010/\u001a\u00020*H\u0002J\b\u00100\u001a\u00020*H\u0002J\b\u00101\u001a\u00020*H\u0002J\b\u00102\u001a\u00020*H\u0002J\b\u00103\u001a\u00020*H\u0002J\"\u00104\u001a\u00020*2\u0006\u00105\u001a\u00020\"2\u0006\u00106\u001a\u00020\"2\b\u00107\u001a\u0004\u0018\u000108H\u0014J\b\u00109\u001a\u00020*H\u0002J\b\u0010:\u001a\u00020*H\u0002J\b\u0010;\u001a\u00020*H\u0002J\b\u0010<\u001a\u00020\u001bH\u0002J\b\u0010=\u001a\u00020*H\u0014J\u0018\u0010>\u001a\u00020*2\u0006\u0010?\u001a\u00020@2\u0006\u0010A\u001a\u00020\u001bH\u0002J\u0010\u0010B\u001a\u00020*2\u0006\u0010C\u001a\u00020@H\u0002J\b\u0010D\u001a\u00020*H\u0014R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0018\u001a\u0004\u0018\u00010\u0019X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u001bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001c\u001a\u0004\u0018\u00010\u001dX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u001fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010 \u001a\n\u0012\u0004\u0012\u00020\"\u0018\u00010!X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010#\u001a\u0004\u0018\u00010$X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010%\u001a\u00020&X\u0082\u000e\u00a2\u0006\u0004\n\u0002\u0010\'R\u000e\u0010(\u001a\u00020\u001bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006F"}, d2 = {"Lcom/myra/assistant/security/SecuritySettingsActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "Landroid/speech/tts/TextToSpeech$OnInitListener;", "<init>", "()V", "appLockSwitch", "Landroid/widget/Switch;", "appLockStatusText", "Landroid/widget/TextView;", "setPinBtn", "Landroid/widget/Button;", "pinStatusText", "setPatternBtn", "patternStatusText", "setVoiceBtn", "voiceStatusText", "selectAppsBtn", "usageStatsBtn", "overlayPermissionBtn", "fingerprintSwitch", "deviceLockSwitch", "privateModeSwitch", "privateModeStatusText", "encryptionStatusText", "tts", "Landroid/speech/tts/TextToSpeech;", "isTtsReady", "", "speechRecognizer", "Landroid/speech/SpeechRecognizer;", "handler", "Landroid/os/Handler;", "firstPattern", "", "", "geminiClient", "Lcom/myra/assistant/ai/GeminiLiveClient;", "liveAudioManager", "error/NonExistentClass", "Lerror/NonExistentClass;", "isGeminiConnected", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "onInit", "status", "initViews", "loadCurrentStatus", "setupListeners", "showPinSetupDialog", "startPatternSetup", "onActivityResult", "requestCode", "resultCode", "data", "Landroid/content/Intent;", "showVoiceSetupDialog", "initGemini", "checkPermissions", "isUsageStatsEnabled", "onResume", "speak", "text", "", "hindi", "toast", "msg", "onDestroy", "Companion", "app_debug"})
public final class SecuritySettingsActivity extends androidx.appcompat.app.AppCompatActivity implements android.speech.tts.TextToSpeech.OnInitListener {
    private android.widget.Switch appLockSwitch;
    private android.widget.TextView appLockStatusText;
    private android.widget.Button setPinBtn;
    private android.widget.TextView pinStatusText;
    private android.widget.Button setPatternBtn;
    private android.widget.TextView patternStatusText;
    private android.widget.Button setVoiceBtn;
    private android.widget.TextView voiceStatusText;
    private android.widget.Button selectAppsBtn;
    private android.widget.Button usageStatsBtn;
    private android.widget.Button overlayPermissionBtn;
    private android.widget.Switch fingerprintSwitch;
    private android.widget.Switch deviceLockSwitch;
    private android.widget.Switch privateModeSwitch;
    private android.widget.TextView privateModeStatusText;
    private android.widget.TextView encryptionStatusText;
    @org.jetbrains.annotations.Nullable()
    private android.speech.tts.TextToSpeech tts;
    private boolean isTtsReady = false;
    @org.jetbrains.annotations.Nullable()
    private android.speech.SpeechRecognizer speechRecognizer;
    @org.jetbrains.annotations.NotNull()
    private final android.os.Handler handler = null;
    @org.jetbrains.annotations.Nullable()
    private java.util.List<java.lang.Integer> firstPattern;
    @org.jetbrains.annotations.Nullable()
    private com.myra.assistant.ai.GeminiLiveClient geminiClient;
    @org.jetbrains.annotations.Nullable()
    private error.NonExistentClass liveAudioManager;
    private boolean isGeminiConnected = false;
    @org.jetbrains.annotations.NotNull()
    public static final com.myra.assistant.security.SecuritySettingsActivity.Companion Companion = null;
    
    public SecuritySettingsActivity() {
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
    
    private final void loadCurrentStatus() {
    }
    
    private final void setupListeners() {
    }
    
    private final void showPinSetupDialog() {
    }
    
    private final void startPatternSetup() {
    }
    
    @java.lang.Override()
    protected void onActivityResult(int requestCode, int resultCode, @org.jetbrains.annotations.Nullable()
    android.content.Intent data) {
    }
    
    private final void showVoiceSetupDialog() {
    }
    
    private final void initGemini() {
    }
    
    private final void checkPermissions() {
    }
    
    private final boolean isUsageStatsEnabled() {
        return false;
    }
    
    @java.lang.Override()
    protected void onResume() {
    }
    
    private final void speak(java.lang.String text, boolean hindi) {
    }
    
    private final void toast(java.lang.String msg) {
    }
    
    @java.lang.Override()
    protected void onDestroy() {
    }
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007\u00a8\u0006\b"}, d2 = {"Lcom/myra/assistant/security/SecuritySettingsActivity$Companion;", "", "<init>", "()V", "launch", "", "context", "Landroid/content/Context;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        public final void launch(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
        }
    }
}