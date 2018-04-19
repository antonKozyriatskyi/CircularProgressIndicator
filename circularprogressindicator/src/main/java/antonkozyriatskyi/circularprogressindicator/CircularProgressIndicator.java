package antonkozyriatskyi.circularprogressindicator;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by Anton on 03.03.2018.
 */

@SuppressWarnings("FieldCanBeLocal")
public class CircularProgressIndicator extends View {

    private static int ANGLE_START_PROGRESS = 270;
    private static int ANGLE_START_PROGRESS_BACKGROUND = 0;
    private static int ANGLE_END_PROGRESS_BACKGROUND = 360;

    private static int DESIRED_WIDTH_DP = 150;

    private static String DEFAULT_PROGRESS_COLOR = "#3F51B5";
    private static int DEFAULT_TEXT_SIZE_SP = 24;
    private static int DEFAULT_STROKE_WIDTH_DP = 8;
    private static String DEFAULT_PROGRESS_BACKGROUND_COLOR = "#e0e0e0";

    private static int DEFAULT_ANIMATION_DURATION = 1_000;

    private static String DEFAULT_PROGRESS_TEXT_DELIMITER = ",";

    private static String PROPERTY_ANGLE = "angle";
    private static String PROPERTY_PROGRESS_TEXT = "progress_text";


    private Paint progressPaint;
    private Paint progressBackgroundPaint;
    private Paint dotPaint;
    private Paint textPaint;

    private int sweepAngle = 0;

    private RectF circleBounds;

    private String progressText;
    private String progressTextDelimiter;
    private String progressTextPrefix;
    private String progressTextSuffix;
    private float textX;
    private float textY;

    private float radius;

    private float drawingCenterX;
    private float drawingCenterY;

    private boolean shouldDrawDot = true;
    private boolean shouldUseDelimiter = true;

    private int maxProgressValue = 100;
    private int progressValue = 0;

    private ValueAnimator progressAnimator;

    public CircularProgressIndicator(Context context) {
        super(context);
        init(context, null);
    }

    public CircularProgressIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircularProgressIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircularProgressIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {

        int progressColor = Color.parseColor(DEFAULT_PROGRESS_COLOR);
        int progressBackgroundColor = Color.parseColor(DEFAULT_PROGRESS_BACKGROUND_COLOR);
        int progressStrokeWidth = dp2px(DEFAULT_STROKE_WIDTH_DP);
        int textColor = progressColor;
        int textSize = sp2px(DEFAULT_TEXT_SIZE_SP);

        shouldDrawDot = true;
        int dotColor = progressColor;
        int dotWidth = progressStrokeWidth;

        shouldUseDelimiter = true;
        progressTextDelimiter = DEFAULT_PROGRESS_TEXT_DELIMITER;

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircularProgressIndicator);

            progressColor = a.getColor(R.styleable.CircularProgressIndicator_progressColor, progressColor);
            progressBackgroundColor = a.getColor(R.styleable.CircularProgressIndicator_progressBackgroundColor, progressBackgroundColor);
            progressStrokeWidth = a.getDimensionPixelSize(R.styleable.CircularProgressIndicator_progressStrokeWidth, progressStrokeWidth);
            textColor = a.getColor(R.styleable.CircularProgressIndicator_textColor, progressColor);
            textSize = a.getDimensionPixelSize(R.styleable.CircularProgressIndicator_textSize, textSize);

            shouldDrawDot = a.getBoolean(R.styleable.CircularProgressIndicator_drawDot, shouldDrawDot);
            dotColor = a.getColor(R.styleable.CircularProgressIndicator_dotColor, progressColor);
            dotWidth = a.getDimensionPixelSize(R.styleable.CircularProgressIndicator_dotWidth, progressStrokeWidth);

            shouldUseDelimiter = a.getBoolean(R.styleable.CircularProgressIndicator_useProgressTextDelimiter, shouldUseDelimiter);
            progressTextDelimiter = a.getString(R.styleable.CircularProgressIndicator_progressTextDelimiter);
            progressTextPrefix = a.getString(R.styleable.CircularProgressIndicator_progressTextPrefix);
            progressTextSuffix = a.getString(R.styleable.CircularProgressIndicator_progressTextSuffix);

            if (progressTextDelimiter == null) {
                progressTextDelimiter = DEFAULT_PROGRESS_TEXT_DELIMITER;
            }

            progressText = formatProgressText(progressValue);

            a.recycle();
        }

        progressPaint = new Paint();
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeWidth(progressStrokeWidth);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setColor(progressColor);
        progressPaint.setAntiAlias(true);

        progressBackgroundPaint = new Paint();
        progressBackgroundPaint.setStyle(Paint.Style.STROKE);
        progressBackgroundPaint.setStrokeWidth(progressStrokeWidth);
        progressBackgroundPaint.setColor(progressBackgroundColor);
        progressBackgroundPaint.setAntiAlias(true);

        dotPaint = new Paint();
        dotPaint.setStrokeCap(Paint.Cap.ROUND);
        dotPaint.setStrokeWidth(dotWidth);
        dotPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        dotPaint.setColor(dotColor);
        dotPaint.setAntiAlias(true);

        textPaint = new TextPaint();
        textPaint.setStrokeCap(Paint.Cap.ROUND);
        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);

        circleBounds = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        Rect textRect = new Rect();
        textPaint.getTextBounds(progressText, 0, progressText.length(), textRect);

        float strokeOffset = (shouldDrawDot) ? Math.max(dotPaint.getStrokeWidth(), progressPaint.getStrokeWidth()) : progressPaint.getStrokeWidth(); // to prevent progress or dot from drawing over the bounds
        int desiredSize = ((int) strokeOffset) + dp2px(DESIRED_WIDTH_DP) +
                Math.max(paddingBottom + paddingTop, paddingLeft + paddingRight);

        desiredSize += Math.max(textRect.width(), textRect.height()) + desiredSize * .1f;

        int finalWidth;
        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                finalWidth = measuredWidth;
                break;
            case MeasureSpec.AT_MOST:
                finalWidth = Math.min(desiredSize, measuredWidth);
                break;
            default:
                finalWidth = desiredSize;
                break;
        }

        int finalHeight;
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                finalHeight = measuredHeight;
                break;
            case MeasureSpec.AT_MOST:
                finalHeight = Math.min(desiredSize, measuredHeight);
                break;
            default:
                finalHeight = desiredSize;
                break;
        }

        int widthWithoutPadding = finalWidth - paddingLeft - paddingRight;
        int heightWithoutPadding = finalHeight - paddingTop - paddingBottom;

        int circleDiameter = Math.min(heightWithoutPadding, widthWithoutPadding);

        radius = circleDiameter / 2;

        drawingCenterX = paddingLeft + widthWithoutPadding / 2f;
        drawingCenterY = paddingTop + heightWithoutPadding / 2f;

        float halfOffset = strokeOffset / 2f;
        circleBounds.left = drawingCenterX - radius + halfOffset;
        circleBounds.top = drawingCenterY - radius + halfOffset;
        circleBounds.right = drawingCenterX + radius - halfOffset;
        circleBounds.bottom = drawingCenterY + radius - halfOffset;

        radius = circleBounds.width() / 2;

        calculateTextBounds();

        setMeasuredDimension(finalWidth, finalHeight);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (progressAnimator != null) {
            progressAnimator.cancel();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawProgressBackground(canvas);
        drawProgress(canvas);
        if (shouldDrawDot) drawPoint(canvas);
        drawText(canvas);
    }

    private void drawProgressBackground(Canvas canvas) {
        canvas.drawArc(circleBounds, ANGLE_START_PROGRESS_BACKGROUND, ANGLE_END_PROGRESS_BACKGROUND,
                false, progressBackgroundPaint);
    }

    private void drawProgress(Canvas canvas) {
        canvas.drawArc(circleBounds, ANGLE_START_PROGRESS, sweepAngle, false, progressPaint);
    }

    private void drawPoint(Canvas canvas) {
        double angleRadians = Math.toRadians(-sweepAngle);
        float cos = (float) Math.cos(angleRadians);
        float sin = (float) Math.sin(angleRadians);
        float x = circleBounds.centerX() - radius * sin;
        float y = circleBounds.centerY() - radius * cos;

        canvas.drawPoint(x, y, dotPaint);
    }

    private void drawText(Canvas canvas) {
        canvas.drawText(progressText, textX, textY, textPaint);
    }

    public void setMaxProgress(int maxProgress) {
        maxProgressValue = maxProgress;
        if (maxProgressValue < progressValue) {
            setCurrentProgress(maxProgress);
        }
        invalidate();
    }

    public void setCurrentProgress(int progress) {
        if (progress > maxProgressValue) {
            maxProgressValue = progress;
        }

        setProgress(progress, maxProgressValue);
    }

    public void setProgress(int current, int max) {
        final float finalAngle = (float) current / max * 360;

        final PropertyValuesHolder angleProperty = PropertyValuesHolder.ofInt(PROPERTY_ANGLE, -sweepAngle, (int) finalAngle);

        progressText = formatProgressText(current);
        maxProgressValue = max;
        progressValue = current;

        calculateTextBounds();

        if (progressAnimator != null) {
            progressAnimator.cancel();
        }

        progressAnimator = ValueAnimator.ofInt(progressValue, current);
        progressAnimator.setDuration(DEFAULT_ANIMATION_DURATION);
        progressAnimator.setValues(angleProperty);
        progressAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                sweepAngle = -(int) animation.getAnimatedValue(PROPERTY_ANGLE);
                invalidate();
            }
        });
        progressAnimator.addListener(new DefaultAnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {
                sweepAngle = (int) -finalAngle;

                progressAnimator = null;
            }
        });
        progressAnimator.start();
    }

    // Adds delimiter, prefix and suffix if needed
    private String formatProgressText(int currentProgress) {

        StringBuilder sb = new StringBuilder(String.valueOf(currentProgress));

        // apply delimiter
        if (shouldUseDelimiter && progressTextDelimiter != null) {
            char[] chars = String.valueOf(Math.abs(currentProgress)).toCharArray();

            char[] charsReversed = new char[chars.length];

            for (int i = 0; i < chars.length; i++) {
                charsReversed[i] = chars[chars.length - 1 - i];
            }

            sb.append(charsReversed[0]);
            for (int i = 1; i < charsReversed.length; i++) {
                if (i % 3 == 0) {
                    sb.append(progressTextDelimiter);
                }

                sb.append(charsReversed[i]);
            }

            if (currentProgress < 0) {
                sb.append("-"); // add minus if value was negative
            }

            sb.reverse();
        }

        // apply prefix
        if (progressTextPrefix != null) {
            sb.insert(0, progressTextPrefix);
        }

        // apply suffix
        if (progressTextSuffix != null) {
            sb.append(progressTextSuffix);
        }

        return sb.toString();
    }

    private Rect calculateTextBounds() {
        Rect textRect = new Rect();
        textPaint.getTextBounds(progressText, 0, progressText.length(), textRect);
        textX = circleBounds.centerX() - textRect.width() / 2f;
        textY = circleBounds.centerY() + textRect.height() / 2f;

        return textRect;
    }

    private int dp2px(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    private int sp2px(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dp, metrics);
    }

    public void setProgressColor(@ColorInt int color) {
        progressPaint.setColor(color);
        invalidate();
    }

    public void setProgressBackgroundColor(@ColorInt int color) {
        progressBackgroundPaint.setColor(color);
        invalidate();
    }

    public void setProgressStrokeWidthDp(@Dimension int strokeWidth) {
        setProgressStrokeWidthPx(dp2px(strokeWidth));
    }

    public void setProgressStrokeWidthPx(@Dimension final int strokeWidth) {
        progressPaint.setStrokeWidth(strokeWidth);
        progressBackgroundPaint.setStrokeWidth(strokeWidth);

        requestLayout();
        invalidate();
    }

    public void setTextColor(@ColorInt int color) {
        textPaint.setColor(color);

        Rect textRect = new Rect();
        textPaint.getTextBounds(progressText, 0, progressText.length(), textRect);

        invalidate(textRect);
    }

    public void setTextSizeSp(@Dimension int size) {
        setTextSizePx(sp2px(size));
    }

    public void setTextSizePx(@Dimension int size) {
        float currentSize = textPaint.getTextSize();

        float factor = textPaint.measureText(progressText) / currentSize;

        float offset = (shouldDrawDot) ? Math.max(dotPaint.getStrokeWidth(), progressPaint.getStrokeWidth()) : progressPaint.getStrokeWidth();
        float maximumAvailableTextWidth = circleBounds.width() - offset;

        if (size * factor >= maximumAvailableTextWidth) {
            size = (int) (maximumAvailableTextWidth / factor);
        }

        textPaint.setTextSize(size);

        Rect textBounds = calculateTextBounds();
        invalidate(textBounds);
    }

    public void setShouldDrawDot(boolean shouldDrawDot) {
        this.shouldDrawDot = shouldDrawDot;

        if (dotPaint.getStrokeWidth() > progressPaint.getStrokeWidth()) {
            requestLayout();
            return;
        }

        invalidate();
    }

    public void setDotColor(@ColorInt int color) {
        dotPaint.setColor(color);

        invalidate();
    }

    public void setDotWidthDp(@Dimension int width) {
        setDotWidthPx(dp2px(width));
    }

    public void setDotWidthPx(@Dimension final int width) {
        dotPaint.setStrokeWidth(width);

        requestLayout();
        invalidate();
    }

    public void setShouldUseDelimiter(boolean shouldUseDelimiter) {
        this.shouldUseDelimiter = shouldUseDelimiter;

        if (!shouldUseDelimiter) {
            progressTextDelimiter = null;
        }

        calculateTextBounds();

        requestLayout();
        invalidate();
    }

    public void setProgressTextDelimiter(@Nullable String delimiter) {
        progressTextDelimiter = delimiter;

        progressText = formatProgressText(progressValue);

        requestLayout();
        invalidate();
    }


    public void setProgressTextPrefix(String prefix) {
        progressTextPrefix = prefix;

        progressText = formatProgressText(progressValue);

        requestLayout();
        invalidate();
    }

    public void setProgressTextSuffix(String suffix) {
        progressTextSuffix = suffix;

        progressText = formatProgressText(progressValue);

        requestLayout();
        invalidate();
    }

    @ColorInt
    public int getProgressColor() {
        return progressPaint.getColor();
    }

    @ColorInt
    public int getProgressBackgroundColor() {
        return progressBackgroundPaint.getColor();
    }

    public float getProgressStrokeWidth() {
        return progressPaint.getStrokeWidth();
    }


    @ColorInt
    public int getTextColor() {
        return textPaint.getColor();
    }

    public float getTextSize() {
        return textPaint.getTextSize();
    }


    public boolean isTextDelimiterEnabled() {
        return this.shouldUseDelimiter;
    }

    public String getProgressTextDelimiter() {
        return progressTextDelimiter;
    }


    public boolean isDotEnabled() {
        return shouldDrawDot;
    }

    @ColorInt
    public int getDotColor() {
        return dotPaint.getColor();
    }

    public float getDotWidth() {
        return dotPaint.getStrokeWidth();
    }


    public int getProgress() {
        return progressValue;
    }

    public int getMaxProgress() {
        return maxProgressValue;
    }

    @Nullable
    public String getProgressTextPrefix() {
        return progressTextPrefix;
    }

    @Nullable
    public String getProgressTextSuffix() {
        return progressTextSuffix;
    }
}
