package com.myra.assistant.security;

/**
 * PatternManager — Pattern save, verify aur clear karo
 */
@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\'B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u001c\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\f0\u0014J\u001c\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0011\u001a\u00020\u00122\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\f0\u0014J\u0010\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\u000e\u0010\u001a\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012J\u000e\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u0011\u001a\u00020\u0012J\u000e\u0010\u001d\u001a\u00020\u001c2\u0006\u0010\u0011\u001a\u00020\u0012J\u000e\u0010\u001e\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012J\u000e\u0010\u001f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012J\u000e\u0010 \u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012J\u000e\u0010!\u001a\u00020\f2\u0006\u0010\u0011\u001a\u00020\u0012J\u000e\u0010\"\u001a\u00020\f2\u0006\u0010\u0011\u001a\u00020\u0012J\u000e\u0010#\u001a\u00020\u000e2\u0006\u0010\u0011\u001a\u00020\u0012J\u0018\u0010$\u001a\n &*\u0004\u0018\u00010%0%2\u0006\u0010\u0011\u001a\u00020\u0012H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006("}, d2 = {"Lcom/myra/assistant/security/PatternManager;", "", "<init>", "()V", "TAG", "", "PREFS_NAME", "KEY_PATTERN", "KEY_PATTERN_ON", "KEY_WRONG_COUNT", "KEY_LOCKOUT_TILL", "MAX_ATTEMPTS", "", "LOCKOUT_MS", "", "savePattern", "", "context", "Landroid/content/Context;", "pattern", "", "verify", "Lcom/myra/assistant/security/PatternManager$PatternResult;", "inputPattern", "handleWrong", "Lcom/myra/assistant/security/PatternManager$PatternResult$WRONG;", "resetAttempts", "isPatternSet", "", "isPatternLockEnabled", "enablePatternLock", "disablePatternLock", "removePattern", "getWrongAttempts", "getRemainingAttempts", "getLockoutRemaining", "getPrefs", "Landroid/content/SharedPreferences;", "kotlin.jvm.PlatformType", "PatternResult", "app_debug"})
public final class PatternManager {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "MYRA_PATTERN";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS_NAME = "myra_security_prefs";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_PATTERN = "unlock_pattern";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_PATTERN_ON = "pattern_lock_enabled";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_WRONG_COUNT = "pattern_wrong_count";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_LOCKOUT_TILL = "pattern_lockout_until";
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCKOUT_MS = 30000L;
    @org.jetbrains.annotations.NotNull()
    public static final com.myra.assistant.security.PatternManager INSTANCE = null;
    
    private PatternManager() {
        super();
    }
    
    public final void savePattern(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Integer> pattern) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.myra.assistant.security.PatternManager.PatternResult verify(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Integer> inputPattern) {
        return null;
    }
    
    private final com.myra.assistant.security.PatternManager.PatternResult.WRONG handleWrong(android.content.Context context) {
        return null;
    }
    
    public final void resetAttempts(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    public final boolean isPatternSet(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    public final boolean isPatternLockEnabled(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    public final void enablePatternLock(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    public final void disablePatternLock(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    public final void removePattern(@org.jetbrains.annotations.NotNull()
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
    
    public final long getLockoutRemaining(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return 0L;
    }
    
    private final android.content.SharedPreferences getPrefs(android.content.Context context) {
        return null;
    }
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0005\u0004\u0005\u0006\u0007\bB\t\b\u0004\u00a2\u0006\u0004\b\u0002\u0010\u0003\u0082\u0001\u0005\t\n\u000b\f\r\u00a8\u0006\u000e"}, d2 = {"Lcom/myra/assistant/security/PatternManager$PatternResult;", "", "<init>", "()V", "CORRECT", "NOT_SET", "TOO_SHORT", "WRONG", "LOCKED_OUT", "Lcom/myra/assistant/security/PatternManager$PatternResult$CORRECT;", "Lcom/myra/assistant/security/PatternManager$PatternResult$LOCKED_OUT;", "Lcom/myra/assistant/security/PatternManager$PatternResult$NOT_SET;", "Lcom/myra/assistant/security/PatternManager$PatternResult$TOO_SHORT;", "Lcom/myra/assistant/security/PatternManager$PatternResult$WRONG;", "app_debug"})
    public static abstract class PatternResult {
        
        private PatternResult() {
            super();
        }
        
        @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/myra/assistant/security/PatternManager$PatternResult$CORRECT;", "Lcom/myra/assistant/security/PatternManager$PatternResult;", "<init>", "()V", "app_debug"})
        public static final class CORRECT extends com.myra.assistant.security.PatternManager.PatternResult {
            @org.jetbrains.annotations.NotNull()
            public static final com.myra.assistant.security.PatternManager.PatternResult.CORRECT INSTANCE = null;
            
            private CORRECT() {
            }
        }
        
        @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0014\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u00d6\u0083\u0004J\n\u0010\u000e\u001a\u00020\u000fH\u00d6\u0081\u0004J\n\u0010\u0010\u001a\u00020\u0011H\u00d6\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0012"}, d2 = {"Lcom/myra/assistant/security/PatternManager$PatternResult$LOCKED_OUT;", "Lcom/myra/assistant/security/PatternManager$PatternResult;", "secondsRemaining", "", "<init>", "(J)V", "getSecondsRemaining", "()J", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
        public static final class LOCKED_OUT extends com.myra.assistant.security.PatternManager.PatternResult {
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
            public final com.myra.assistant.security.PatternManager.PatternResult.LOCKED_OUT copy(long secondsRemaining) {
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
        
        @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/myra/assistant/security/PatternManager$PatternResult$NOT_SET;", "Lcom/myra/assistant/security/PatternManager$PatternResult;", "<init>", "()V", "app_debug"})
        public static final class NOT_SET extends com.myra.assistant.security.PatternManager.PatternResult {
            @org.jetbrains.annotations.NotNull()
            public static final com.myra.assistant.security.PatternManager.PatternResult.NOT_SET INSTANCE = null;
            
            private NOT_SET() {
            }
        }
        
        @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/myra/assistant/security/PatternManager$PatternResult$TOO_SHORT;", "Lcom/myra/assistant/security/PatternManager$PatternResult;", "<init>", "()V", "app_debug"})
        public static final class TOO_SHORT extends com.myra.assistant.security.PatternManager.PatternResult {
            @org.jetbrains.annotations.NotNull()
            public static final com.myra.assistant.security.PatternManager.PatternResult.TOO_SHORT INSTANCE = null;
            
            private TOO_SHORT() {
            }
        }
        
        @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\r\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0014\u0010\u000f\u001a\u00020\u00052\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u00d6\u0083\u0004J\n\u0010\u0012\u001a\u00020\u0003H\u00d6\u0081\u0004J\n\u0010\u0013\u001a\u00020\u0014H\u00d6\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\u0015"}, d2 = {"Lcom/myra/assistant/security/PatternManager$PatternResult$WRONG;", "Lcom/myra/assistant/security/PatternManager$PatternResult;", "attempts", "", "lockedOut", "", "<init>", "(IZ)V", "getAttempts", "()I", "getLockedOut", "()Z", "component1", "component2", "copy", "equals", "other", "", "hashCode", "toString", "", "app_debug"})
        public static final class WRONG extends com.myra.assistant.security.PatternManager.PatternResult {
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
            public final com.myra.assistant.security.PatternManager.PatternResult.WRONG copy(int attempts, boolean lockedOut) {
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