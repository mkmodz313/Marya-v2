package com.myra.assistant.security;

/**
 * SecurityManager — AES-256 Encryption + PIN + Voice + App Lock per package
 *
 * ADDED: isPackageLocked() — AccessibilityHelperService ke liye
 */
@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0016\n\u0002\u0010\"\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001JB\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0018\u001a\u00020\u0019H\u0002J\u000e\u0010\u001a\u001a\u00020\u00052\u0006\u0010\u001b\u001a\u00020\u0005J\u000e\u0010\u001c\u001a\u00020\u00052\u0006\u0010\u001d\u001a\u00020\u0005J\u0016\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020\u0005J\u0016\u0010#\u001a\u00020$2\u0006\u0010 \u001a\u00020!2\u0006\u0010%\u001a\u00020\u0005J\u0010\u0010&\u001a\u00020\'2\u0006\u0010 \u001a\u00020!H\u0002J\u000e\u0010(\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!J\u000e\u0010)\u001a\u00020*2\u0006\u0010 \u001a\u00020!J\u000e\u0010+\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!J\u000e\u0010,\u001a\u00020\n2\u0006\u0010 \u001a\u00020!J\u000e\u0010-\u001a\u00020\n2\u0006\u0010 \u001a\u00020!J\u0010\u0010.\u001a\u00020\u00052\u0006\u0010\"\u001a\u00020\u0005H\u0002J\u0016\u0010/\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!2\u0006\u00100\u001a\u00020\u0005J\u0016\u00101\u001a\u00020*2\u0006\u0010 \u001a\u00020!2\u0006\u00102\u001a\u00020\u0005J\u000e\u00103\u001a\u00020*2\u0006\u0010 \u001a\u00020!J\u0010\u00104\u001a\u00020\u00052\u0006\u00100\u001a\u00020\u0005H\u0002J\u0016\u00105\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!2\u0006\u00106\u001a\u00020*J\u000e\u00107\u001a\u00020*2\u0006\u0010 \u001a\u00020!J\u000e\u00108\u001a\u00020*2\u0006\u0010 \u001a\u00020!J\u0016\u00109\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!2\u0006\u00106\u001a\u00020*J\u000e\u0010:\u001a\u00020*2\u0006\u0010 \u001a\u00020!J\u0016\u0010;\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!2\u0006\u00106\u001a\u00020*J\u0016\u0010<\u001a\u00020*2\u0006\u0010 \u001a\u00020!2\u0006\u0010=\u001a\u00020\u0005J\u0016\u0010>\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!2\u0006\u0010=\u001a\u00020\u0005J\u0016\u0010?\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!2\u0006\u0010=\u001a\u00020\u0005J\u0014\u0010@\u001a\b\u0012\u0004\u0012\u00020\u00050A2\u0006\u0010 \u001a\u00020!J\u001e\u0010B\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!2\f\u0010C\u001a\b\u0012\u0004\u0012\u00020\u00050AH\u0002J\u000e\u0010D\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!J\u000e\u0010E\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!J\u000e\u0010F\u001a\u00020*2\u0006\u0010 \u001a\u00020!J\u0018\u0010G\u001a\n I*\u0004\u0018\u00010H0H2\u0006\u0010 \u001a\u00020!H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\nX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006K"}, d2 = {"Lcom/myra/assistant/security/SecurityManager;", "", "<init>", "()V", "TAG", "", "KEYSTORE_PROVIDER", "KEY_ALIAS", "AES_MODE", "GCM_TAG_LENGTH", "", "PREFS_NAME", "KEY_PIN_HASH", "KEY_VOICE_PHRASE", "KEY_APP_LOCK_ON", "KEY_BIOMETRIC_ON", "KEY_DEVICE_LOCK_ON", "KEY_PRIVATE_MODE", "KEY_WRONG_ATTEMPTS", "KEY_LOCKOUT_TIME", "KEY_LOCKED_PACKAGES", "MAX_ATTEMPTS", "LOCKOUT_MS", "", "getOrCreateKey", "Ljavax/crypto/SecretKey;", "encrypt", "plainText", "decrypt", "cipherText", "setPin", "", "context", "Landroid/content/Context;", "pin", "verifyPin", "Lcom/myra/assistant/security/SecurityManager$PinResult;", "inputPin", "handleWrongAttempt", "Lcom/myra/assistant/security/SecurityManager$PinResult$WRONG;", "resetAttempts", "hasPin", "", "removePin", "getWrongAttempts", "getRemainingAttempts", "hashPin", "setVoicePassphrase", "phrase", "verifyVoicePassphrase", "spoken", "hasVoicePassphrase", "normalise", "setAppLockEnabled", "enabled", "isAppLockEnabled", "isBiometricEnabled", "setBiometricEnabled", "isDeviceLockEnabled", "setDeviceLockEnabled", "isPackageLocked", "packageName", "addLockedPackage", "removeLockedPackage", "getLockedPackages", "", "saveLockedPackages", "packages", "enablePrivateMode", "disablePrivateMode", "isPrivateModeActive", "getPrefs", "Landroid/content/SharedPreferences;", "kotlin.jvm.PlatformType", "PinResult", "app_debug"})
public final class SecurityManager {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "MYRA_SECURITY";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEYSTORE_PROVIDER = "AndroidKeyStore";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_ALIAS = "MYRA_MASTER_KEY";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String AES_MODE = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS_NAME = "myra_security_prefs";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_PIN_HASH = "pin_hash";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_VOICE_PHRASE = "voice_passphrase";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_APP_LOCK_ON = "app_lock_enabled";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_BIOMETRIC_ON = "biometric_lock_enabled";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_DEVICE_LOCK_ON = "device_lock_enabled";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_PRIVATE_MODE = "private_mode_active";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_WRONG_ATTEMPTS = "wrong_attempts";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_LOCKOUT_TIME = "lockout_until";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_LOCKED_PACKAGES = "locked_packages";
    private static final int MAX_ATTEMPTS = 3;
    private static final long LOCKOUT_MS = 30000L;
    @org.jetbrains.annotations.NotNull()
    public static final com.myra.assistant.security.SecurityManager INSTANCE = null;
    
    private SecurityManager() {
        super();
    }
    
    private final javax.crypto.SecretKey getOrCreateKey() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String encrypt(@org.jetbrains.annotations.NotNull()
    java.lang.String plainText) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String decrypt(@org.jetbrains.annotations.NotNull()
    java.lang.String cipherText) {
        return null;
    }
    
    public final void setPin(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String pin) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.myra.assistant.security.SecurityManager.PinResult verifyPin(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String inputPin) {
        return null;
    }
    
    private final com.myra.assistant.security.SecurityManager.PinResult.WRONG handleWrongAttempt(android.content.Context context) {
        return null;
    }
    
    public final void resetAttempts(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    public final boolean hasPin(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    public final void removePin(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    public final int getWrongAttempts(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return 0;
    }
    
    public final int getRemainingAttempts(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return 0;
    }
    
    private final java.lang.String hashPin(java.lang.String pin) {
        return null;
    }
    
    public final void setVoicePassphrase(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String phrase) {
    }
    
    public final boolean verifyVoicePassphrase(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String spoken) {
        return false;
    }
    
    public final boolean hasVoicePassphrase(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    private final java.lang.String normalise(java.lang.String phrase) {
        return null;
    }
    
    public final void setAppLockEnabled(@org.jetbrains.annotations.NotNull()
    android.content.Context context, boolean enabled) {
    }
    
    public final boolean isAppLockEnabled(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    public final boolean isBiometricEnabled(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    public final void setBiometricEnabled(@org.jetbrains.annotations.NotNull()
    android.content.Context context, boolean enabled) {
    }
    
    public final boolean isDeviceLockEnabled(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    public final void setDeviceLockEnabled(@org.jetbrains.annotations.NotNull()
    android.content.Context context, boolean enabled) {
    }
    
    /**
     * ✅ FIXED: Checks if any lock method is active + package is in list
     */
    public final boolean isPackageLocked(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String packageName) {
        return false;
    }
    
    public final void addLockedPackage(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String packageName) {
    }
    
    public final void removeLockedPackage(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String packageName) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> getLockedPackages(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    private final void saveLockedPackages(android.content.Context context, java.util.Set<java.lang.String> packages) {
    }
    
    public final void enablePrivateMode(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    public final void disablePrivateMode(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    public final boolean isPrivateModeActive(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    private final android.content.SharedPreferences getPrefs(android.content.Context context) {
        return null;
    }
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0004\u0004\u0005\u0006\u0007B\t\b\u0004\u00a2\u0006\u0004\b\u0002\u0010\u0003\u0082\u0001\u0004\b\t\n\u000b\u00a8\u0006\f"}, d2 = {"Lcom/myra/assistant/security/SecurityManager$PinResult;", "", "<init>", "()V", "CORRECT", "NOT_SET", "WRONG", "LOCKED_OUT", "Lcom/myra/assistant/security/SecurityManager$PinResult$CORRECT;", "Lcom/myra/assistant/security/SecurityManager$PinResult$LOCKED_OUT;", "Lcom/myra/assistant/security/SecurityManager$PinResult$NOT_SET;", "Lcom/myra/assistant/security/SecurityManager$PinResult$WRONG;", "app_debug"})
    public static abstract class PinResult {
        
        private PinResult() {
            super();
        }
        
        @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/myra/assistant/security/SecurityManager$PinResult$CORRECT;", "Lcom/myra/assistant/security/SecurityManager$PinResult;", "<init>", "()V", "app_debug"})
        public static final class CORRECT extends com.myra.assistant.security.SecurityManager.PinResult {
            @org.jetbrains.annotations.NotNull()
            public static final com.myra.assistant.security.SecurityManager.PinResult.CORRECT INSTANCE = null;
            
            private CORRECT() {
            }
        }
        
        @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0014\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u00d6\u0083\u0004J\n\u0010\u000e\u001a\u00020\u000fH\u00d6\u0081\u0004J\n\u0010\u0010\u001a\u00020\u0011H\u00d6\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0012"}, d2 = {"Lcom/myra/assistant/security/SecurityManager$PinResult$LOCKED_OUT;", "Lcom/myra/assistant/security/SecurityManager$PinResult;", "secondsRemaining", "", "<init>", "(J)V", "getSecondsRemaining", "()J", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
        public static final class LOCKED_OUT extends com.myra.assistant.security.SecurityManager.PinResult {
            private final long secondsRemaining = 0L;
            
            public LOCKED_OUT(long secondsRemaining) {
            }
            
            public final long getSecondsRemaining() {
                return 0L;
            }
            
            public final long component1() {
                return 0L;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.myra.assistant.security.SecurityManager.PinResult.LOCKED_OUT copy(long secondsRemaining) {
                return null;
            }
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
        }
        
        @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/myra/assistant/security/SecurityManager$PinResult$NOT_SET;", "Lcom/myra/assistant/security/SecurityManager$PinResult;", "<init>", "()V", "app_debug"})
        public static final class NOT_SET extends com.myra.assistant.security.SecurityManager.PinResult {
            @org.jetbrains.annotations.NotNull()
            public static final com.myra.assistant.security.SecurityManager.PinResult.NOT_SET INSTANCE = null;
            
            private NOT_SET() {
            }
        }
        
        @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\r\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0014\u0010\u000f\u001a\u00020\u00052\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u00d6\u0083\u0004J\n\u0010\u0012\u001a\u00020\u0003H\u00d6\u0081\u0004J\n\u0010\u0013\u001a\u00020\u0014H\u00d6\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\u0015"}, d2 = {"Lcom/myra/assistant/security/SecurityManager$PinResult$WRONG;", "Lcom/myra/assistant/security/SecurityManager$PinResult;", "attempts", "", "lockedOut", "", "<init>", "(IZ)V", "getAttempts", "()I", "getLockedOut", "()Z", "component1", "component2", "copy", "equals", "other", "", "hashCode", "toString", "", "app_debug"})
        public static final class WRONG extends com.myra.assistant.security.SecurityManager.PinResult {
            private final int attempts = 0;
            private final boolean lockedOut = false;
            
            public WRONG(int attempts, boolean lockedOut) {
            }
            
            public final int getAttempts() {
                return 0;
            }
            
            public final boolean getLockedOut() {
                return false;
            }
            
            public final int component1() {
                return 0;
            }
            
            public final boolean component2() {
                return false;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.myra.assistant.security.SecurityManager.PinResult.WRONG copy(int attempts, boolean lockedOut) {
                return null;
            }
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
        }
    }
}