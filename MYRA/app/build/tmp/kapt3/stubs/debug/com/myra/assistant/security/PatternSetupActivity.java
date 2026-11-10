package com.myra.assistant.security;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u000e\n\u0002\b\u0003\u0018\u00002\u00020\u00012\u00020\u0002B\u0007\u00a2\u0006\u0004\b\u0003\u0010\u0004J\u0012\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018H\u0014J\b\u0010\u0019\u001a\u00020\u0016H\u0002J\u0016\u0010\u001a\u001a\u00020\u00162\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fH\u0002J\b\u0010\u001c\u001a\u00020\u0016H\u0002J\u0010\u0010\u001d\u001a\u00020\u00162\u0006\u0010\u001e\u001a\u00020\u0010H\u0016J\u0018\u0010\u001f\u001a\u00020\u00162\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020\u0012H\u0002J\b\u0010#\u001a\u00020\u0016H\u0014R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000e\u001a\n\u0012\u0004\u0012\u00020\u0010\u0018\u00010\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006$"}, d2 = {"Lcom/myra/assistant/security/PatternSetupActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "Landroid/speech/tts/TextToSpeech$OnInitListener;", "<init>", "()V", "patternLockView", "Lcom/myra/assistant/security/PatternLockView;", "instructionText", "Landroid/widget/TextView;", "retryBtn", "Landroid/widget/Button;", "cancelBtn", "tts", "Landroid/speech/tts/TextToSpeech;", "firstPattern", "", "", "isConfirming", "", "handler", "Landroid/os/Handler;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "initViews", "handlePattern", "pattern", "resetSetup", "onInit", "status", "speak", "text", "", "flush", "onDestroy", "app_debug"})
public final class PatternSetupActivity extends androidx.appcompat.app.AppCompatActivity implements android.speech.tts.TextToSpeech.OnInitListener {
    private com.myra.assistant.security.PatternLockView patternLockView;
    private android.widget.TextView instructionText;
    private android.widget.Button retryBtn;
    private android.widget.Button cancelBtn;
    @org.jetbrains.annotations.Nullable()
    private android.speech.tts.TextToSpeech tts;
    @org.jetbrains.annotations.Nullable()
    private java.util.List<java.lang.Integer> firstPattern;
    private boolean isConfirming = false;
    @org.jetbrains.annotations.NotNull()
    private final android.os.Handler handler = null;
    
    public PatternSetupActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void initViews() {
    }
    
    private final void handlePattern(java.util.List<java.lang.Integer> pattern) {
    }
    
    private final void resetSetup() {
    }
    
    @java.lang.Override()
    public void onInit(int status) {
    }
    
    private final void speak(java.lang.String text, boolean flush) {
    }
    
    @java.lang.Override()
    protected void onDestroy() {
    }
}