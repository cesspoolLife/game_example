package com.cesspoollife.sunday;

import java.util.List;

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
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/*
 * SurfaceView를 상속받은 Custom View
 * Canvas를 위한 View
 */
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
    
    /*
     * Surface View의 초기화.
     * Callback 설정과 Thread를 지정.
     */
    private void init(Context context){
    	SurfaceHolder holder = getHolder();
    	holder.addCallback(this);
    	drawThread = new DrawThread(context, holder);
    }
    
    /*
     * Path
     */
    public void setPathPosition(List<int[]> p){
    	((DrawThread) drawThread).setPathPosition(p);
    }
    
    /*
     * 폭탄의 위치
     */
    public void setBoomPosition(int[] p1, int[] p2){
    	((DrawThread) drawThread).setBoomPosition(p1, p2);
    }
    
    /*
     * 사용시간
     */
    public int getUseTime(){
    	return ((DrawThread) drawThread).getUseTime();
    }
    
    /* 
     * 재시작
     */
    public void setRestart(){
    	((DrawThread) drawThread).setRestart();
    }
    
    /*
     * 일시중지
     */
    public void setPause(){
    	((DrawThread) drawThread).setPause();
    }
    
    /*
     * 종료
     */
    public void setStop(){
    	((DrawThread) drawThread).setStop();
    	drawThread.interrupt();
    }

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}
	
	/*
	 *  CallBack 함수로 View가 생성될때 Thread가 시작된다.
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		drawThread.start();
	}

	/*
	 * Thread안에 run이 죽지 않을 경우를 대비해 항상 stop함수를 호출.
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		((DrawThread) drawThread).setStop();
		drawThread.interrupt();
	}
	
	/*
	 * Canvas를 그리기 위한 별도의 Thread
	 */
	class DrawThread extends Thread{
		
		final private Long timeLimit = (long) 60;
		final private int FRAME = 1000/60;
		private boolean PAUSE;
		private boolean STOP;
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
    	private float boomXPosition;
    	private float boomYPosition;
    	private float dx;
    	private float dy;
    	private float length;
    	private List<int[]> path;
    	private int pathSize;
    	private int orderPath;
    	
    	/*
    	 * DrawThread 생성자
    	 * 각종 필요한 변수들의 값을 지정 해준다.
    	 */
    	public DrawThread(Context context, SurfaceHolder surfaceHolder){
    		PAUSE = false;
    		STOP = false;
    		mContext = context;
    		setFigureSize();
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
    		path = null;
    		orderPath = -1;
    		pathSize = 0;
    	}
    	
    	/*
    	 * 도형들의 크기와 위치를 지정하는 함수(기기 호환을 위해 dip에 맞게 pixel의 사이즈를 설정해준다.)
    	 */
    	private void setFigureSize(){
    		r = getResources();
    		roundCircle = mContext.getResources().getDimensionPixelSize(R.dimen.round_circle);
    		xCircle = mContext.getResources().getDimensionPixelSize(R.dimen.x_circle);
    		yCircle = mContext.getResources().getDimensionPixelSize(R.dimen.y_circle);
    		textSize  = mContext.getResources().getDimensionPixelSize(R.dimen.timer_size);
    		xText = (float) (xCircle-(textSize/2));
    		yText = (float) (yCircle+(textSize/4));
    		topRec = yCircle-mContext.getResources().getDimensionPixelSize(R.dimen.tick_rec);
    		botRec = yCircle+mContext.getResources().getDimensionPixelSize(R.dimen.tick_rec);
    		leftRec = xCircle+roundCircle;
    		Bitmap bm = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier("b_1", "drawable", mContext.getPackageName()));
    		dx = mContext.getResources().getDimensionPixelSize(R.dimen.block_width)/2;
    		dy = mContext.getResources().getDimensionPixelSize(R.dimen.block_height)/2;
    		boomXPosition = bm.getWidth()/2-dx;
    		boomYPosition = bm.getHeight()/2-dy;
    		length = (r.getDisplayMetrics().widthPixels-((int)xCircle*2)-((int)roundCircle*2))/60;
    	}
    	

    	/*
    	 * 폭탄 위치를 지정해준다.
    	 * 포탄을 그릴수 있게 orderBoom변수를 1로 바꿔준다.
    	 */
    	public void setBoomPosition(int[] p1, int[] p2){
    		pBoom1 = p1;
    		pBoom2 = p2;
    		orderBoom=1;
    	}
    	
    	/*
    	 * 위치를 넘겨 받는다.
    	 * 폭탄의 순서를 0부터 시작
    	 * 사이즈 만큼만 그릴수 있도록 변수로 저장했다.
    	 */
    	public void setPathPosition(List<int[]> p){
    		orderPath = 0;
    		path = p;
    		pathSize = p.size();
    	}
    	
    	/*
    	 * 중지 함수
    	 */
    	public void setPause(){
    		PAUSE = true;
    		pauseTime = System.currentTimeMillis();
    	}
    	
    	/*
    	 * 재시작 함수
    	 * 시작시간에 중지되어 있던 시간만큼을 더해줌.
    	 */
    	public void setRestart(){
    		startTime = startTime + System.currentTimeMillis() - pauseTime;
    		PAUSE = false;
    	}
    	
    	/*
    	 * 종료
    	 */
    	public void setStop(){
    		mPath = null;
    		mBitmap = null;
    		STOP = true;
    	}
    	
    	/*
    	 * 남은 시간을 반환.
    	 */
    	public int getUseTime(){
    		return (int)(System.currentTimeMillis()-startTime)/1000;
    	}
    	
    	/*
    	 * 시간초과를 체크 하는 함수
    	 * 기존에 있던 path와 폭탄은 바로 화면에서 제거함
    	 */
    	private boolean isTimeOver(){
    		if(((System.currentTimeMillis()-startTime)/1000<60)||PAUSE)
    			return false;
    		mPath = null;
    		mBitmap = null;
    		((GameActivity)mContext).timeOver();//GameActivity클래스의 timeOver함수를 호출해서 게임 종료를 알린다..
    		return true;
    	}
    	
    	/*
    	 * 폭탄을 그려야 한다면 폭탄 이미지를 순서대로 불러온다.
    	 * 4프래임마다 이미지를 교체 해준다.
    	 */
    	private void setBoom(){ 
    		if(orderBoom==0)
    			return;
    		if(orderBoom>35){ // 폭탄을 다 그렸다면 초기화.
    			orderBoom=0;
    			mBitmap = null;
    			return;
    		}
    		if(orderBoom%4!=1){ // 4프래임마다 그리기위한 if문
    			orderBoom++;
    			return;
    		}
    		String name = "b_"+String.valueOf(orderBoom++/4);
    		Bitmap bm = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(name, "drawable", mContext.getPackageName()));
    		mBitmap = bm;
    	}
    	
    	/*
    	 * Path 지정 함수
    	 * 한프레임마다 path가 한칸씩 길어짐.
    	 */    	
    	private void setPath(){
    		if(orderPath<0)
    			return;
    		if(orderPath>=pathSize){
    			orderPath = -1;
    			mPath = null;
    			return;
    		}
    		Path p = new Path();
    		for(int i=0;i<=orderPath;i++){
    			if(p.isEmpty())
    				p.moveTo(path.get(i)[0], path.get(i)[1]);
    			else{
    				p.lineTo(path.get(i)[0], path.get(i)[1]);
    			}
    		}
    		mPath = p;
    		orderPath++;
    		mPath.offset(dx, dy);
    	}

    	/*
    	 * Thread를 start했을때 호출되는 함수
    	 * Canvas를 그리는 작업이 메인이다.
    	 */
		@Override
		public void run() {
			super.run();
			Long pre = System.currentTimeMillis();//프레임 계산을 위한 시간 변수
			Long cur = null;
			while(true){
				if(isTimeOver()||STOP)//시간이 종료되거나 게임을 종료한다면 무한 루프를 빠져나온다.
					break;
				cur = System.currentTimeMillis();
				if(cur-pre<FRAME||PAUSE)//60 fps 로 동작함, 일시 정지시 중지 
					continue;
				pre = System.currentTimeMillis();
				Canvas c = null;
				try{
					c = mHolder.lockCanvas();
					synchronized ( mHolder ){
						setBoom();//폭탄 설정
						setPath();//Path 설정
						doDraw(c);//캔버스를 그려준다.
					}
				}catch (Exception e){
					Log.e("ThreadVeiw", "Thread Exception", e);
					break;
				}finally {
					if (c!=null){
						mHolder.unlockCanvasAndPost(c);
					}
				}
			}
			Canvas c = null;//종료되었을시 캔버스의 잔여물을 제거 하기 위해 한번더 호출
			try{
				c = mHolder.lockCanvas();
				synchronized ( mHolder ){
					doDraw(c);
				}
			}catch (Exception e){
				Log.e("ThreadVeiw", "Thread Exception", e);
			}finally {
				if (c!=null){
					mHolder.unlockCanvasAndPost(c);
				}
			}
		}
				
		/*
		 * 캔버스로 화면에 그려주는 함수
		 * Path, 남은 시간, 폭탄을 그려준다.
		 */
		private void doDraw(Canvas canvas){
			float remain= (float)timeLimit - (float)(System.currentTimeMillis()-startTime)/1000;//남은 시간 계산.
			canvas.drawColor(0, PorterDuff.Mode.CLEAR);//기존 canvas를 클리어 해준다.
			if(mPath!=null){//Path가 있을 때만
		    	canvas.drawPath(mPath, paintPath);
	    	}
	    	if(remain >= 0){//시간이 남아 있을때만
	    		rightRec = length*remain;//시간 바의 길이를 계산
	    		canvas.drawCircle(xCircle, yCircle, roundCircle, paintFigure);//원을 그리고
	    		canvas.drawRect(leftRec, topRec, leftRec+rightRec, botRec, paintFigure);//시간 바를 그리고
	    		canvas.drawText(String.valueOf((int)Math.ceil(remain)), xText, yText, paintRemain);// 남은 시간을 표시한다.
	    	}
	    	if(mBitmap != null){//폭탄이 있을때만, 위치에 boomSize를 빼줘서 Block위에 폭탄이 터지는 효과 
	    		canvas.drawBitmap(mBitmap, pBoom1[0]-boomXPosition, pBoom1[1]-boomYPosition, null);
	    		canvas.drawBitmap(mBitmap, pBoom2[0]-boomXPosition, pBoom2[1]-boomYPosition, null);
	    	}
		}
    }
}
