package com.cesspoollife.sunday;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ThreadView extends SurfaceView implements SurfaceHolder.Callback {
	
	private Thread drawThread;
		
	public ThreadView(Context context) {
		super(context);
		init(context);
	}
	public ThreadView (Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ThreadView (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    
    private void init(Context context){
    	SurfaceHolder holder = getHolder();
    	holder.addCallback(this);
    	drawThread = new DrawThread(context, holder);
    }
    
    public void setPath(Path p){
    	((DrawThread) drawThread).setPath(p);
    }
    
    public void setBoomPosition(int[] p1, int[] p2){
    	((DrawThread) drawThread).setBoomPosition(p1, p2);
    }
    
    public int getRemainTime(){
    	return ((DrawThread) drawThread).getRemainTime();
    }
    
    public void setRestart(){
    	((DrawThread) drawThread).setRestart();
    }
    
    public void setPause(){
    	((DrawThread) drawThread).setPause();
    }

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		drawThread.start();
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		drawThread.interrupt();
	}
	
	class DrawThread extends Thread{
		final private Long timeLimit = (long) 60;
		final private int FRAME = 1000/60;
		private boolean PAUSE;
		private Long pauseTime;
		private Context mContext;
    	private SurfaceHolder mHolder;
    	private Path mPath;
    	private Paint paintPath;
    	private Long startTime;
    	private Paint paintRemain;
    	private Paint paintFigure;
    	private Bitmap mBitmap;
    	private Resources r;
    	private float roundCircle;
    	private float xCircle;
    	private float yCircle;
    	private float textSize;
    	private float xText;
    	private float yText;
    	private float botRec;
    	private float topRec;
    	private float leftRec;
    	private float rightRec;
    	private int[] pBoom1;
    	private int[] pBoom2;
    	private int orderBoom;
    	private int boomSize;
    	
    	public DrawThread(Context context, SurfaceHolder surfaceHolder){
    		setFigureSize();
    		PAUSE = false;
    		mContext = context;
    		mHolder = surfaceHolder;
    		mPath = null;
    		paintPath = new Paint();
    		paintPath.setStyle(Paint.Style.STROKE);
    		paintPath.setColor(Color.WHITE);
    		paintPath.setStrokeWidth(3);
    		startTime = System.currentTimeMillis();
    		paintFigure  = new Paint();
    		paintFigure.setStyle(Paint.Style.FILL);
    		paintFigure.setColor(Color.WHITE);
    		paintRemain  = new Paint();
    		paintRemain.setColor(Color.BLACK);
    		paintRemain.setTextSize(textSize);
    		mBitmap = null;
    		orderBoom=0;
    		Bitmap bm = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier("b_1", "drawable", mContext.getPackageName()));
    		boomSize = bm.getHeight()/2;
    	}
    	private void setFigureSize(){
    		r = getResources();
    		roundCircle = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());
    		xCircle = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());
    		yCircle = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());
    		textSize  = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, r.getDisplayMetrics());
    		xText = (float) (xCircle-(textSize/2));
    		yText = (float) (yCircle+(textSize/4));
    		topRec = yCircle-TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics());
    		botRec = yCircle+TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics());
    		leftRec = xCircle+roundCircle;
    	}
    	
    	public void setPause(){
    		PAUSE = true;
    		pauseTime = System.currentTimeMillis();
    	}
    	
    	public void setPath(Path p){
    		mPath = p;
    	}
    	
    	public int getRemainTime(){
    		return (int)(System.currentTimeMillis()-startTime)/1000;
    	}
    	
    	public void setRestart(){
    		startTime = startTime + System.currentTimeMillis() - pauseTime;
    		PAUSE = false;
    	}
    	
    	private boolean isTimeOver(){
    		if((System.currentTimeMillis()-startTime)/1000<60)
    			return false;
    		((GameActivity)mContext).timeOver();
    		return true;
    	}
    	
    	public void setBoomPosition(int[] p1, int[] p2){
    		pBoom1 = p1;
    		pBoom2 = p2;
    		orderBoom=1;
    	}
    	
    	private void setBoom(){
    		if(orderBoom==0)
    			return;
    		if(orderBoom>32){
    			mBitmap = null;
    			return;
    		}
    		String name = "b_"+String.valueOf(orderBoom++/4);
    		Bitmap bm = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(name, "drawable", mContext.getPackageName()));
    		mBitmap = bm;
    	}

		@Override
		public void run() {
			super.run();
			Long pre = System.currentTimeMillis();
			Long cur = null;
			while(true){
				cur = System.currentTimeMillis();
				if(cur-pre<FRAME||PAUSE)
					continue;
				pre = System.currentTimeMillis();
				Canvas c = null;
				try{
					c = mHolder.lockCanvas();
					synchronized ( mHolder ){
						setBoom();
						doDraw(c);
						isTimeOver();
					}
				}catch (Exception e){
					Log.e("ThreadVeiw", "Thread Exception", e);
					break;
				}finally {
					if (c!=null){
						mHolder.unlockCanvasAndPost(c);
					}
				}
				
				if(isTimeOver())
					break;
			}
		}
		
		private void doDraw(Canvas canvas){
			float remain= (float)timeLimit - (float)(System.currentTimeMillis()-startTime)/1000;
			canvas.drawColor(0, PorterDuff.Mode.CLEAR);
			if(mPath!=null){
		    	canvas.drawPath(mPath, paintPath);
	    	}
	    	if(remain >= 0){
	    		rightRec = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4*remain, r.getDisplayMetrics());
	    		canvas.drawCircle(xCircle, yCircle, roundCircle, paintFigure);
	    		canvas.drawRect(leftRec, topRec, leftRec+rightRec, botRec, paintFigure);
	    		canvas.drawText(String.valueOf((int)Math.ceil(remain)), xText, yText, paintRemain);
	    	}
	    	if(mBitmap != null){
	    		canvas.drawBitmap(mBitmap, pBoom1[0]-boomSize, pBoom1[1]-boomSize, null);
	    		canvas.drawBitmap(mBitmap, pBoom2[0]-boomSize, pBoom2[1]-boomSize, null);
	    	}
		}
    }
}
