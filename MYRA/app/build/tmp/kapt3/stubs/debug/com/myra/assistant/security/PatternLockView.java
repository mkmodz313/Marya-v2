package com.myra.assistant.security;

/**
 * PatternLockView — Custom 3x3 Grid Pattern Lock
 */
@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000p\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0010 \n\u0002\b\u0007\u0018\u0000 B2\u00020\u0001:\u0003BCDB\'\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0004\b\b\u0010\tJ(\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020\u00072\u0006\u0010\'\u001a\u00020\u00072\u0006\u0010(\u001a\u00020\u00072\u0006\u0010)\u001a\u00020\u0007H\u0014J\u0010\u0010*\u001a\u00020%2\u0006\u0010+\u001a\u00020,H\u0014J\u0010\u0010-\u001a\u00020.2\u0006\u0010/\u001a\u000200H\u0016J \u00101\u001a\u00020%2\u0006\u00102\u001a\u00020\r2\u0006\u00103\u001a\u00020\r2\u0006\u00104\u001a\u00020\rH\u0002J\'\u00105\u001a\u0004\u0018\u00010\u00072\u0006\u00102\u001a\u00020\r2\u0006\u00103\u001a\u00020\r2\u0006\u00106\u001a\u00020\rH\u0002\u00a2\u0006\u0002\u00107J\b\u00108\u001a\u00020%H\u0002J\u0006\u00109\u001a\u00020%J\u0006\u0010:\u001a\u00020%J\u0006\u0010;\u001a\u00020%J\b\u0010<\u001a\u00020%H\u0002J\f\u0010=\u001a\b\u0012\u0004\u0012\u00020\u00070>J\u0018\u0010?\u001a\u00020%2\u0006\u0010@\u001a\u00020\u00072\u0006\u0010A\u001a\u00020\u0007H\u0014R\"\u0010\n\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\r0\f0\u000bX\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u000eR\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00070\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u0015\u001a\u0004\u0018\u00010\u0016X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0017\u0010\u0018\"\u0004\b\u0019\u0010\u001aR\u000e\u0010\u001b\u001a\u00020\u001cX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\u001cX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u001cX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020\u001cX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020\u001cX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\u001cX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020\u001cX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010#\u001a\u00020\u001cX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006E"}, d2 = {"Lcom/myra/assistant/security/PatternLockView;", "Landroid/view/View;", "context", "Landroid/content/Context;", "attrs", "Landroid/util/AttributeSet;", "defStyleAttr", "", "<init>", "(Landroid/content/Context;Landroid/util/AttributeSet;I)V", "dotCenters", "", "Lkotlin/Pair;", "", "[Lkotlin/Pair;", "selectedDots", "", "currentX", "currentY", "state", "Lcom/myra/assistant/security/PatternLockView$PatternState;", "listener", "Lcom/myra/assistant/security/PatternLockView$PatternListener;", "getListener", "()Lcom/myra/assistant/security/PatternLockView$PatternListener;", "setListener", "(Lcom/myra/assistant/security/PatternLockView$PatternListener;)V", "dotPaintNormal", "Landroid/graphics/Paint;", "dotPaintSelected", "dotPaintError", "dotPaintSuccess", "ringPaintSelected", "linePaint", "lineErrorPaint", "lineSuccessPaint", "onSizeChanged", "", "w", "h", "oldw", "oldh", "onDraw", "canvas", "Landroid/graphics/Canvas;", "onTouchEvent", "", "event", "Landroid/view/MotionEvent;", "handleTouch", "x", "y", "touchRadius", "findNearestDot", "radius", "(FFF)Ljava/lang/Integer;", "finishPattern", "showError", "showSuccess", "clearPattern", "clearPatternInternal", "getPattern", "", "onMeasure", "widthMeasureSpec", "heightMeasureSpec", "Companion", "PatternState", "PatternListener", "app_debug"})
public final class PatternLockView extends android.view.View {
    public static final int GRID_SIZE = 3;
    public static final int DOT_COUNT = 9;
    public static final int MIN_PATTERN_LENGTH = 4;
    public static final float DOT_RADIUS_RATIO = 0.06F;
    public static final float TOUCH_RADIUS_RATIO = 0.12F;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Pair<java.lang.Float, java.lang.Float>[] dotCenters = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.Integer> selectedDots = null;
    private float currentX = -1.0F;
    private float currentY = -1.0F;
    @org.jetbrains.annotations.NotNull()
    private com.myra.assistant.security.PatternLockView.PatternState state = com.myra.assistant.security.PatternLockView.PatternState.NORMAL;
    @org.jetbrains.annotations.Nullable()
    private com.myra.assistant.security.PatternLockView.PatternListener listener;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint dotPaintNormal = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint dotPaintSelected = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint dotPaintError = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint dotPaintSuccess = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint ringPaintSelected = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint linePaint = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint lineErrorPaint = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint lineSuccessPaint = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.myra.assistant.security.PatternLockView.Companion Companion = null;
    
    @kotlin.jvm.JvmOverloads()
    public PatternLockView(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super(null);
    }
    
    @kotlin.jvm.JvmOverloads()
    public PatternLockView(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.Nullable()
    android.util.AttributeSet attrs) {
        super(null);
    }
    
    @kotlin.jvm.JvmOverloads()
    public PatternLockView(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.Nullable()
    android.util.AttributeSet attrs, int defStyleAttr) {
        super(null);
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.myra.assistant.security.PatternLockView.PatternListener getListener() {
        return null;
    }
    
    public final void setListener(@org.jetbrains.annotations.Nullable()
    com.myra.assistant.security.PatternLockView.PatternListener p0) {
    }
    
    @java.lang.Override()
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    }
    
    @java.lang.Override()
    protected void onDraw(@org.jetbrains.annotations.NotNull()
    android.graphics.Canvas canvas) {
    }
    
    @java.lang.Override()
    public boolean onTouchEvent(@org.jetbrains.annotations.NotNull()
    android.view.MotionEvent event) {
        return false;
    }
    
    private final void handleTouch(float x, float y, float touchRadius) {
    }
    
    private final java.lang.Integer findNearestDot(float x, float y, float radius) {
        return null;
    }
    
    private final void finishPattern() {
    }
    
    public final void showError() {
    }
    
    public final void showSuccess() {
    }
    
    public final void clearPattern() {
    }
    
    private final void clearPatternInternal() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.Integer> getPattern() {
        return null;
    }
    
    @java.lang.Override()
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    }
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\tX\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/myra/assistant/security/PatternLockView$Companion;", "", "<init>", "()V", "GRID_SIZE", "", "DOT_COUNT", "MIN_PATTERN_LENGTH", "DOT_RADIUS_RATIO", "", "TOUCH_RADIUS_RATIO", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\b\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\u0016\u0010\u0004\u001a\u00020\u00032\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H&J\b\u0010\b\u001a\u00020\u0003H&\u00a8\u0006\t\u00c0\u0006\u0003"}, d2 = {"Lcom/myra/assistant/security/PatternLockView$PatternListener;", "", "onPatternStarted", "", "onPatternComplete", "pattern", "", "", "onPatternCleared", "app_debug"})
    public static abstract interface PatternListener {
        
        public abstract void onPatternStarted();
        
        public abstract void onPatternComplete(@org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.Integer> pattern);
        
        public abstract void onPatternCleared();
    }
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/myra/assistant/security/PatternLockView$PatternState;", "", "<init>", "(Ljava/lang/String;I)V", "NORMAL", "SUCCESS", "ERROR", "app_debug"})
    public static enum PatternState {
        /*public static final*/ NORMAL /* = new NORMAL() */,
        /*public static final*/ SUCCESS /* = new SUCCESS() */,
        /*public static final*/ ERROR /* = new ERROR() */;
        
        PatternState() {
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<com.myra.assistant.security.PatternLockView.PatternState> getEntries() {
            return null;
        }
    }
}