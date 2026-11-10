package com.myra.assistant.ai;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\b\u0018\u00002\u00020\u0001:\u0001\u0019B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0006\u0010\u0011\u001a\u00020\u0012J\b\u0010\u0013\u001a\u00020\u0012H\u0002J\u000e\u0010\u0014\u001a\u00020\u00122\u0006\u0010\u0015\u001a\u00020\u0003J\u0010\u0010\u0016\u001a\u00020\u00122\u0006\u0010\u0017\u001a\u00020\u0003H\u0002J\u0006\u0010\u0018\u001a\u00020\u0012R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0003X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001a"}, d2 = {"Lcom/myra/assistant/ai/GeminiLiveClient;", "", "apiKey", "", "systemPrompt", "callback", "Lcom/myra/assistant/ai/GeminiLiveClient$LiveListener;", "<init>", "(Ljava/lang/String;Ljava/lang/String;Lcom/myra/assistant/ai/GeminiLiveClient$LiveListener;)V", "TAG", "isSetupComplete", "", "webSocket", "Lokhttp3/WebSocket;", "client", "Lokhttp3/OkHttpClient;", "URL", "start", "", "sendSetup", "sendTextMessage", "text", "handleResponse", "jsonString", "disconnect", "LiveListener", "app_debug"})
public final class GeminiLiveClient {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String apiKey = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String systemPrompt = null;
    @org.jetbrains.annotations.NotNull()
    private final com.myra.assistant.ai.GeminiLiveClient.LiveListener callback = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String TAG = "MYRA_LIVE";
    private boolean isSetupComplete = false;
    @org.jetbrains.annotations.Nullable()
    private okhttp3.WebSocket webSocket;
    @org.jetbrains.annotations.NotNull()
    private final okhttp3.OkHttpClient client = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String URL = null;
    
    public GeminiLiveClient(@org.jetbrains.annotations.NotNull()
    java.lang.String apiKey, @org.jetbrains.annotations.NotNull()
    java.lang.String systemPrompt, @org.jetbrains.annotations.NotNull()
    com.myra.assistant.ai.GeminiLiveClient.LiveListener callback) {
        super();
    }
    
    public final void start() {
    }
    
    private final void sendSetup() {
    }
    
    public final void sendTextMessage(@org.jetbrains.annotations.NotNull()
    java.lang.String text) {
    }
    
    private final void handleResponse(java.lang.String jsonString) {
    }
    
    public final void disconnect() {
    }
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\u0010\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\bH&J\b\u0010\t\u001a\u00020\u0003H&J\b\u0010\n\u001a\u00020\u0003H&J\u0010\u0010\u000b\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\bH&\u00a8\u0006\r\u00c0\u0006\u0003"}, d2 = {"Lcom/myra/assistant/ai/GeminiLiveClient$LiveListener;", "", "onAudioReceived", "", "data", "", "onTextReceived", "text", "", "onConnected", "onTurnComplete", "onError", "msg", "app_debug"})
    public static abstract interface LiveListener {
        
        public abstract void onAudioReceived(@org.jetbrains.annotations.NotNull()
        byte[] data);
        
        public abstract void onTextReceived(@org.jetbrains.annotations.NotNull()
        java.lang.String text);
        
        public abstract void onConnected();
        
        public abstract void onTurnComplete();
        
        public abstract void onError(@org.jetbrains.annotations.NotNull()
        java.lang.String msg);
    }
}