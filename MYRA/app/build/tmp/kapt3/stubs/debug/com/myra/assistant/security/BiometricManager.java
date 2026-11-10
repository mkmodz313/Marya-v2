package com.myra.assistant.security;

/**
 * BiometricManager - Fingerprint authentication for MYRA
 * Requirements:
 * 1. ✅ Fingerprint auth on launch
 * 2. ✅ Block automation until success
 * 3. ✅ Fallback PIN if unavailable
 * 4. ✅ Secure session timeout
 */
@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0010\u001a\u00020\r2\u0006\u0010\u0011\u001a\u00020\u0012J\u000e\u0010\u0013\u001a\u00020\r2\u0006\u0010\u0011\u001a\u00020\u0012J\u0016\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0016\u001a\u00020\rJ>\u0010\u0017\u001a\u00020\u00152\u0006\u0010\u0011\u001a\u00020\u00122\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00150\u00192\u0012\u0010\u001a\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00150\u001b2\f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00150\u0019J@\u0010\u001d\u001a\u00020\u00152\u0006\u0010\u0011\u001a\u00020\u00122\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00150\u00192\u0012\u0010\u001a\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00150\u001b2\f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00150\u0019H\u0002J\u000e\u0010\u001e\u001a\u00020\r2\u0006\u0010\u0011\u001a\u00020\u0012J\u0006\u0010\u001f\u001a\u00020\u0015J\u0010\u0010 \u001a\u00020\u00152\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\u000e\u0010!\u001a\u00020\r2\u0006\u0010\u0011\u001a\u00020\u0012J\u0016\u0010\"\u001a\u00020\u00152\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010#\u001a\u00020\u0005J\u0016\u0010$\u001a\u00020\r2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010#\u001a\u00020\u0005J\u0010\u0010%\u001a\u00020\u00052\u0006\u0010#\u001a\u00020\u0005H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006&"}, d2 = {"Lcom/myra/assistant/security/BiometricManager;", "", "<init>", "()V", "TAG", "", "SESSION_TIMEOUT_MS", "", "PREFS_NAME", "KEY_LAST_AUTH", "KEY_BIOMETRIC_ENABLED", "KEY_PIN_SET", "isAuthenticated", "", "lastAuthTime", "", "isBiometricAvailable", "context", "Landroid/content/Context;", "isBiometricEnabled", "setBiometricEnabled", "", "enabled", "authenticate", "onSuccess", "Lkotlin/Function0;", "onError", "Lkotlin/Function1;", "onFallback", "showBiometricPrompt", "isSessionValid", "resetAuth", "saveAuthTime", "isPinSet", "setPin", "pin", "verifyPin", "hashPin", "app_debug"})
public final class BiometricManager {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "MYRA_BIOMETRIC";
    private static final int SESSION_TIMEOUT_MS = 300000;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS_NAME = "myra_security";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_LAST_AUTH = "last_auth_time";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_BIOMETRIC_ENABLED = "biometric_enabled";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_PIN_SET = "pin_set";
    private static boolean isAuthenticated = false;
    private static long lastAuthTime = 0L;
    @org.jetbrains.annotations.NotNull()
    public static final com.myra.assistant.security.BiometricManager INSTANCE = null;
    
    private BiometricManager() {
        super();
    }
    
    /**
     * Check if biometric is available on device
     */
    public final boolean isBiometricAvailable(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    /**
     * Check if user has enabled biometric in settings
     */
    public final boolean isBiometricEnabled(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    /**
     * Enable/disable biometric
     */
    public final void setBiometricEnabled(@org.jetbrains.annotations.NotNull()
    android.content.Context context, boolean enabled) {
    }
    
    /**
     * Main authentication method
     */
    public final void authenticate(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSuccess, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onError, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onFallback) {
    }
    
    private final void showBiometricPrompt(android.content.Context context, kotlin.jvm.functions.Function0<kotlin.Unit> onSuccess, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onError, kotlin.jvm.functions.Function0<kotlin.Unit> onFallback) {
    }
    
    /**
     * Check if current session is valid (not timed out)
     */
    public final boolean isSessionValid(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    /**
     * Reset authentication state
     */
    public final void resetAuth() {
    }
    
    /**
     * Save authentication time
     */
    private final void saveAuthTime(android.content.Context context) {
    }
    
    /**
     * Check if PIN is set
     */
    public final boolean isPinSet(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    /**
     * Set PIN
     */
    public final void setPin(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String pin) {
    }
    
    /**
     * Verify PIN
     */
    public final boolean verifyPin(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String pin) {
        return false;
    }
    
    private final java.lang.String hashPin(java.lang.String pin) {
        return null;
    }
}