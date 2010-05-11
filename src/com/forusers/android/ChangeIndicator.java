/**
 * 
 */
package com.forusers.android;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.forusers.android.bigwords.R;

/**
 * A widget that gives feedback that a tilt was registered.
 *
 * @author scottkirkwood
 */
public class ChangeIndicator extends View {
    private static int DEFAULT_BK_COLOR = 0xFF000000;
    private static int DEFAULT_FG_COLOR = 0xFFEEEEEE;
    private static int DEFAULT_MAX = 100;
    private static int DEFAULT_WIDTH_DP = 16;
    private static int DEFAULT_HEIGHT_DP = 16;
    private static String TAG = "ChangeIndicator";
    private int backgroundColor;
    private int foregroundColor;
    private float displayDensity;
    private int pxWidth;
    private int pxHeight;
    private int pos;  // Position, postive goes up, negative goes down.
    private int max;  // Largest positive or negative value pos can attain
    private Paint paint;

    public ChangeIndicator(Context context) {
        super(context);
        initProgressBar();
    }

    public ChangeIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        initProgressBar();
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ChangeIndicator);

        setBackgroundColor(a.getColor(R.styleable.HorizontalProgressBar_backgroundColor,
                DEFAULT_BK_COLOR));

        setForegroundColor(a.getColor(R.styleable.HorizontalProgressBar_foregroundColor,
                DEFAULT_FG_COLOR));
        setMax(a.getInt(R.styleable.HorizontalProgressBar_max, DEFAULT_MAX));

        a.recycle();
    }

    private void initProgressBar() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        displayDensity = metrics.density;
        pxWidth = (int) (DEFAULT_WIDTH_DP * displayDensity);
        pxHeight = (int) (DEFAULT_HEIGHT_DP * displayDensity);

        paint = new Paint();
        paint.setColor(DEFAULT_BK_COLOR);
        paint.setStyle(Paint.Style.FILL);
        setPadding(2, 0, 2, 0);
        backgroundColor = DEFAULT_BK_COLOR;
        foregroundColor = DEFAULT_FG_COLOR;
        pos = -100;
    }

    /**
     * Change the position.
     * @param pos
     */
    public void setPosition(int newPos) {
        if (newPos > max) {
            newPos = max;
        } else if (newPos < -max) {
            newPos = -max;
        }
        pos = newPos;
        Log.i(TAG, "setting pos: " + pos);
        invalidate();
    }

    public void setMax(int newMax) {
        max = newMax;
        Log.i(TAG, "setting max: " + max);
         
        // May move position if it's out of bounds.
        setPosition(pos);
    }
    
    /* (non-Javadoc)
     * @see android.view.View#onMeasure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        int specMode = MeasureSpec.getMode(heightMeasureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            int totalHeight = pxHeight;
            result = Math.max(getSuggestedMinimumHeight(), Math.min(specSize, totalHeight));
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }
    
    private int measureWidth(int widthMeasureSpec) {
        int result = 0;
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        int specMode = MeasureSpec.getMode(widthMeasureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            int totalWidth = pxWidth;
            result = Math.max(getSuggestedMinimumWidth(), Math.min(totalWidth, specSize));
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }
    
    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        backgroundColor = color;
    }
    
    private void setForegroundColor(int color) {
        foregroundColor = color;
        invalidate();
    }

    private int calculateYPos(int newPos) {
        float interiorHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        return (int) ((interiorHeight * newPos) / (2.0 * max));
    }

    /* (non-Javadoc)
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
       
        int interiorWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int interiorHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        int vMiddle = getPaddingTop() + interiorHeight / 2;
        int hMiddle = getPaddingLeft() + interiorWidth / 2;
        int vThickTop = 0;
        int vThickBottom = 1;
        int hThickLeft = 1;
        int hThickRight = 1;

        int ypos = calculateYPos(pos);

        paint.setColor(backgroundColor);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        paint.setColor(foregroundColor);        
        if (ypos > 0) {
            canvas.drawRect(hMiddle - hThickLeft, vMiddle - ypos,
                            hMiddle + hThickRight, vMiddle, paint);          
        } else if (ypos < 0) {
            canvas.drawRect(hMiddle - hThickLeft, vMiddle,
                            hMiddle + hThickRight, vMiddle - ypos, paint);          
        }
        paint.setColor(foregroundColor);
        canvas.drawRect(getPaddingLeft(), vMiddle - vThickTop,
                        getWidth() - getPaddingRight(), vMiddle + vThickBottom, paint);          
    }
}
