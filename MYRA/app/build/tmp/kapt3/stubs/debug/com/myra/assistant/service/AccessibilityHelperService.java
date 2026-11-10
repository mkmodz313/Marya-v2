package com.myra.assistant.service;

/**
 * Stub Accessibility Service - Automation removed, only voice assistant remains
 */
@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 \u000b2\u00020\u0001:\u0001\u000bB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H\u0014J\u0012\u0010\u0006\u001a\u00020\u00052\b\u0010\u0007\u001a\u0004\u0018\u00010\bH\u0016J\b\u0010\t\u001a\u00020\u0005H\u0016J\b\u0010\n\u001a\u00020\u0005H\u0016\u00a8\u0006\f"}, d2 = {"Lcom/myra/assistant/service/AccessibilityHelperService;", "Landroid/accessibilityservice/AccessibilityService;", "<init>", "()V", "onServiceConnected", "", "onAccessibilityEvent", "event", "Landroid/view/accessibility/AccessibilityEvent;", "onInterrupt", "onDestroy", "Companion", "app_debug"})
public final class AccessibilityHelperService extends android.accessibilityservice.AccessibilityService {
    @org.jetbrains.annotations.Nullable()
    private static com.myra.assistant.service.AccessibilityHelperService instance;
    @org.jetbrains.annotations.NotNull()
    public static final com.myra.assistant.service.AccessibilityHelperService.Companion Companion = null;
    
    public AccessibilityHelperService() {
        super();
    }
    
    @java.lang.Override()
    protected void onServiceConnected() {
    }
    
    @java.lang.Override()
    public void onAccessibilityEvent(@org.jetbrains.annotations.Nullable()
    android.view.accessibility.AccessibilityEvent event) {
    }
    
    @java.lang.Override()
    public void onInterrupt() {
    }
    
    @java.lang.Override()
    public void onDestroy() {
    }
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rJ\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fR\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\t\u00a8\u0006\u0010"}, d2 = {"Lcom/myra/assistant/service/AccessibilityHelperService$Companion;", "", "<init>", "()V", "instance", "Lcom/myra/assistant/service/AccessibilityHelperService;", "getInstance", "()Lcom/myra/assistant/service/AccessibilityHelperService;", "setInstance", "(Lcom/myra/assistant/service/AccessibilityHelperService;)V", "isEnabled", "", "context", "Landroid/content/Context;", "getRoot", "Landroid/view/accessibility/AccessibilityNodeInfo;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.Nullable()
        public final com.myra.assistant.service.AccessibilityHelperService getInstance() {
            return null;
        }
        
        public final void setInstance(@org.jetbrains.annotations.Nullable()
        com.myra.assistant.service.AccessibilityHelperService p0) {
        }
        
        public final boolean isEnabled(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return false;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final android.view.accessibility.AccessibilityNodeInfo getRoot() {
            return null;
        }
    }
}