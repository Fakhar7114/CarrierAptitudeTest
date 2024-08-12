package com.alphacoder.carrieraptitudetest.progresssBar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.animation.ValueAnimator;
import android.util.TypedValue;

import com.alphacoder.carrieraptitudetest.R;


public class CustomProgressBar extends View {
    private Paint paint;
    private int dotCount = 14;
    private float dotRadius = 8f;
    private float rotationAngle = 0f;
    private int dotColor;

    public CustomProgressBar(Context context) {
        super(context);
        init(context);
    }

    public CustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotColor = getResources().getColor(R.color.secondary_variant);
        paint.setColor(dotColor);
        paint.setStyle(Paint.Style.FILL);


        // Animation to rotate the dots
        ValueAnimator animator = ValueAnimator.ofFloat(0, 360);
        animator.setDuration(6000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(animation -> {
            rotationAngle = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        float centerX = (width - getPaddingLeft() - getPaddingRight()) / 2f + getPaddingLeft();
        float centerY = (height - getPaddingTop() - getPaddingBottom()) / 2f + getPaddingTop();
        float radius = 60f + dotRadius; // Adjust the radius to include spacing

        for (int i = 0; i < dotCount; i++) {
            float angle = (float) (i * 2 * Math.PI / dotCount);
            float x = (float) (centerX + Math.cos(angle) * radius);
            float y = (float) (centerY + Math.sin(angle) * radius);
            canvas.save();
            canvas.rotate(rotationAngle, centerX, centerY);
            canvas.drawCircle(x, y, dotRadius, paint);
            canvas.restore();
        }
    }

    // Method to set the number of dots
    public void setDotCount(int dotCount) {
        this.dotCount = dotCount;
        invalidate();
    }

    // Method to set the radius of dots
    public void setDotRadius(float dotRadius) {
        this.dotRadius = dotRadius;
        invalidate();
    }



    // Method to set the color of the dots
    public void setDotColor(int color) {
        this.dotColor = color;
        paint.setColor(dotColor);
        invalidate();
    }
}
