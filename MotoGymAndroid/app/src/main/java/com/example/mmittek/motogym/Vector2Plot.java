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

public class Vector2Plot extends View {


    protected Paint mPaint;
    protected double[] mVector;
    protected int[] mColors;

    public Vector2Plot(Context context) {
        super(context);
        init();
    }

    public Vector2Plot(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Vector2Plot(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public Vector2Plot(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    protected void init() {
        mPaint = new Paint();
        mVector = new double[2];
        mVector[0] = 1;
        mVector[1] = 1;
        mColors = new int[]{Color.RED, Color.GREEN};
    }

    public void setVector(double[] vector) {
        if(vector.length >=2) {
            mVector[0] = vector[0];
            mVector[1] = vector[1];
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


        double[] vectorN = mVector;
        double magnitude = Math.sqrt((mVector[0]*mVector[0]) + (mVector[1]*mVector[1]));
        for(int i=0; i<2; i++) {
            vectorN[i] /= magnitude;
        }

        mPaint.setStyle(Paint.Style.STROKE);

    mPaint.setStrokeWidth(10);
        mPaint.setColor(Color.RED);
        canvas.drawLine(0,0,(float)(vectorN[0]*canvasWidth*0.49f),0,mPaint);
        mPaint.setColor(Color.BLUE);
        canvas.drawLine(0,0,0,(float)(vectorN[1]*canvasHeight*0.49f),mPaint);


    }
}
