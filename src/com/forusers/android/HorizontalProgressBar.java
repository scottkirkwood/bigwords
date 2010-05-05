/**
 * 
 */
package com.forusers.android;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Region;

import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.forusers.android.bigwords.R;

/**
 * A widget that draws a horizontal progress bar with text in the center.
 * 
 * @author scottkirkwood
 */
public class HorizontalProgressBar extends View {
    public HorizontalProgressBar(Context context) {
        super(context);
        initProgressBar();
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initProgressBar();
        TypedArray a = context.obtainStyledAttributes(attrs,
               R.styleable.HorizontalProgressBar);

        setTextColor(a.getColor(R.styleable.HorizontalProgressBar_textColor, DEFAULT_TEXT_COLOR));
        setSecondTextColor(a.getColor(R.styleable.HorizontalProgressBar_secondTextColor,
                           DEFAULT_TEXT_COLOR2));

        setBackgroundColor(a.getColor(R.styleable.HorizontalProgressBar_backgroundColor,
                           DEFAULT_BK_COLOR));

        setForegroundColor(a.getColor(R.styleable.HorizontalProgressBar_foregroundColor,
                           DEFAULT_FG_COLOR));

        String formatText = a.getString(R.styleable.HorizontalProgressBar_textFormat);
        if (formatText != null) {
            setFormatText(formatText);
        }

        setMin(a.getInt(R.styleable.HorizontalProgressBar_min, DEFAULT_MIN));
        setMax(a.getInt(R.styleable.HorizontalProgressBar_max, DEFAULT_MAX));
        setTextSize(a.getDimensionPixelSize(R.styleable.HorizontalProgressBar_textSize,
                    DEFAULT_TEXT_SIZE));

        a.recycle();
    }

    private void setFormatText(String newFormatText) {
        formatText = newFormatText;
        requestLayout();
        invalidate();
    }

    private void setTextSize(int textSize) {
        paint.setTextSize(textSize);
        requestLayout();
        invalidate();
    }

    private void setTextColor(int color) {
        textColor = color;
        invalidate();          
    }
    
    private void setSecondTextColor(int color) {
       secondTextColor = color;
       invalidate();
    }

    private void initProgressBar() {
        paint = new Paint();
        paint.setTextSize(DEFAULT_TEXT_SIZE);
        paint.setColor(DEFAULT_BK_COLOR);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStyle(Paint.Style.FILL);
        setPadding(2, 2, 2, 2);
        min = DEFAULT_MIN;
        max = DEFAULT_MAX;
        backgroundColor = DEFAULT_BK_COLOR;
        foregroundColor = DEFAULT_FG_COLOR;
        textColor = DEFAULT_TEXT_COLOR;
        secondTextColor = DEFAULT_TEXT_COLOR2;
        pos = 0;
        textX = 0;
        textY = 0;
        formatText = "%d%% done";
    }

    /**
     * Change the position.
     * @param pos
     */
    public void setPosition(int newPos) {
        if (newPos > max) {
            newPos = max;
        }
        if (newPos < min) {
            newPos = min;
        }
        if (calculateXPos(pos) != calculateXPos(newPos)) {
          // draw only if theres at least one pixel changed.
          invalidate();
        }
        pos = newPos;
    }

    public void setMin(int newMin) {
        if (newMin > max) {
            newMin = 0;
        }
        min = newMin;
         
        Log.i(TAG, "setting min: " + min);
        // May move position if it's out of bounds.
        setPosition(pos);
    }
    
    public void setMax(int newMax) {
        if (newMax < min) {
            newMax = min + 1;
        }
        max = newMax;
        Log.i(TAG, "setting max: " + max);
         
        // May move position if it's out of bounds.
        setPosition(pos);
    }
    
    public String getText() {
        return String.format(formatText, pos);
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

        int ascent = (int) paint.ascent();
        int descent = (int) paint.descent();

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            int heightText = descent - ascent + getPaddingTop() + getPaddingBottom();
            result = Math.max(getSuggestedMinimumHeight(), Math.min(specSize, heightText));
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        textY = -ascent + getPaddingBottom();
        return result;
    }
    
    private int measureWidth(int widthMeasureSpec) {
        int result = 0;
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        int specMode = MeasureSpec.getMode(widthMeasureSpec);

        String text = getText();
        int textWidth = (int) paint.measureText(text);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            int widthText = textWidth + getPaddingLeft() + getPaddingRight();
            result = Math.max(getSuggestedMinimumWidth(), Math.min(widthText, specSize));
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        textX = getPaddingLeft() + (result - getPaddingLeft() - getPaddingRight()) / 2;
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

    private int calculateXPos(int newPos) {
        float barWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        return getPaddingLeft() + (int) ((barWidth * (newPos - min)) / (max - min) );
    }

    /* (non-Javadoc)
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
       
        int xpos = calculateXPos(pos);
        int bottom = getHeight() - getPaddingBottom();
        int right = getWidth() - getPaddingRight();
        String text = getText();

        paint.setColor(backgroundColor);
        canvas.drawRect(xpos, getPaddingTop(), 
                        right, bottom, paint);

        paint.setColor(foregroundColor);        
        canvas.drawRect(getPaddingLeft(), getPaddingTop(),
                        xpos, bottom, paint);

        paint.setColor(secondTextColor);
        canvas.clipRect(xpos, getPaddingTop(),
                        right, bottom, Region.Op.REPLACE);
        canvas.drawText(text, textX, textY, paint);
        
        paint.setColor(textColor);
        canvas.clipRect(getPaddingLeft(), getPaddingTop(), xpos,
                        bottom, Region.Op.REPLACE);
        canvas.drawText(text, textX, textY, paint);
    }

    private static int DEFAULT_BK_COLOR = 0xFF668800;
    private static int DEFAULT_FG_COLOR = 0xFF9977FF;
    private static int DEFAULT_TEXT_COLOR = 0xFFFFFFFF;
    private static int DEFAULT_TEXT_COLOR2 = 0xFF000000;
    private static int DEFAULT_TEXT_SIZE = 10;
    private static int DEFAULT_MIN = 0;
    private static int DEFAULT_MAX = 100;
    private static String TAG = "HorizontalProgressBar";
    private int backgroundColor;
    private int foregroundColor;
    private int textColor;
    private int secondTextColor;
    private Paint paint;
    private int max;
    private int min;
    private int pos;
    private float textY;
    private float textX;
    private String formatText;

}
