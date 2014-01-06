package com.cesspoollife.sunday;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class PathView extends View {
	private Path mPath = null;
	private int[] position = null;
	private int remain = -1;
	private Paint p = new Paint();
	private Bitmap mBitmap = null;

    public PathView (Context context) {
        super(context);
    }

    public PathView (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PathView (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Set the coordinates of the line to be drawn. The origin (0, 0) is the
     * top left of the View.
     * @param x1 the starting x coordinate
     * @param y1 the starting y coordinate
     * @param x2 the ending x coordinate
     * @param y2O the ending y coordinate
     */
    public void setCoordinates (Path path) {
    	this.mPath = path;
        invalidate();
    }
    
    public void setTime(int remain){
    	this.remain = remain;
    	invalidate();
    }
    
    public void setPosition(int[] p1, int[] p2){
    	position = new int[4];
    	position[0] = p1[0]-50;
    	position[1] = p1[1]-50;
    	position[2] = p2[0]-50;
    	position[3] = p2[1]-50;
    }
    
    public void setBitmap(Bitmap bm){
    	this.mBitmap = bm;
    	invalidate();
    }

    /*
     * 캐버스를 그려준다(path, 타이머, boom)
     */
    @Override
    protected void onDraw (Canvas canvas) {
    	if(mPath!=null){
    		p.setStyle(Paint.Style.STROKE);
        	p.setColor(Color.WHITE);
        	p.setStrokeWidth(3);
	    	canvas.drawPath(mPath, p);
    	}
    	if(remain != -1){
    		p.setStyle(Paint.Style.FILL);
    		p.setColor(Color.WHITE);
    		canvas.drawCircle(55, 70, 50, p);
    		canvas.drawRect(105, 65, 105+remain*5/10, 75, p);
    		p.setColor(Color.BLACK);
    		p.setTextSize(70);
    		canvas.drawText(String.valueOf(remain/10), 20, 80, p);
    	}
    	if(mBitmap != null){
    		canvas.drawBitmap(mBitmap, position[0], position[1], null);
    		canvas.drawBitmap(mBitmap, position[2], position[3], null);
    	}
    }
}
