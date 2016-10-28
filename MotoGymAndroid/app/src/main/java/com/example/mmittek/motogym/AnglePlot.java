package com.example.mmittek.motogym;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by mmittek on 10/28/16.
 */

public class AnglePlot extends View {

    float mAngle = 0;
    Paint mPaint;

    public AnglePlot(Context context) {
        super(context);
        init();
    }

    public AnglePlot(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnglePlot(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public AnglePlot(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    protected void init() {
        mPaint = new Paint();
    }

    public void setAngle(float angle) {
        if(mAngle != angle) {
            mAngle = angle;
            invalidate();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        mPaint.reset();
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(0,0,canvasWidth,canvasHeight, mPaint);

        canvas.translate( canvasWidth/2, canvasHeight/2 );
        canvas.rotate( mAngle );
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.RED);
        canvas.drawRect(-canvasWidth*0.3f,-canvasHeight*0.2f,canvasWidth*0.3f,canvasHeight*0.2f, mPaint);

    }
}
