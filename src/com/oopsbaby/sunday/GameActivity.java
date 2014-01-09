package com.oopsbaby.sunday;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.GridView;

/*
 * 게임 실행 Activity
 */
public class GameActivity extends Activity {
	
	private int stage;
	private GroupAdapter adapter;
	private GridView gv;
    private SurfaceView sfvTrack;
    //시간초과로 인한 게임 종료를 위한 handler
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
	    public void run() {
	        gameFinish(false);
	    }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		sfvTrack = (SurfaceView)findViewById(R.id.cavas_path);
		sfvTrack.setZOrderOnTop(true);
		SurfaceHolder sfhTrack = sfvTrack.getHolder();
		sfhTrack.setFormat(PixelFormat.TRANSPARENT);
		
		Intent intent= getIntent();
		stage = intent.getIntExtra("stage",0);
		
		Shisensho block = Shisensho.getInstace();
		block.makeBlock(stage);//단계별로 블럭을 만든다.
		
		adapter = new GroupAdapter(this, R.layout.image_layout, block);
		gv = (GridView)findViewById(R.id.cardgroup);
		gv.setAdapter(adapter);
		gv.setOnItemClickListener(new ItemClickListener());
	}
	
	/*
	 * adapter의 값이 변경되었을때 호출다는 함수.
	 */
	public void setGridViewChange(){
		adapter.notifyDataSetChanged();
	}
	
	/*
	 * 패스를 받아 넘겨주는 함수.
	 */
	public void setPathPosition(List<int[]> p){
		((ThreadView) sfvTrack).setPathPosition(p);
	}
	
	/*
	 * 폭탄 애니메이션 두 위치를 지정
	 */
	public void drawBoom(int[] p1, int[] p2){
		((ThreadView) sfvTrack).setBoomPosition(p1, p2);
	}
	
	/*
	 * 게임 종료 함수
	 * 초기 메뉴로 돌아간다.
	 * history가 남지 않는다.(AndroidManifest.xml에서 설정)
	 */
	public void exitGame(){
		Intent intent = new Intent(GameActivity.this,MainActivity.class);
		startActivity(intent);
		finish();
	}
	
	/*
	 * 시간이 다 되면 호출되는 함수.
	 * handler를 이용해서 gameFinish 함수를 호출한다.(ThreadView의 Thread에서 직접 gameFinish 함수를 호출할수 없기때문에)
	 */
	public void timeOver(){
		handler.postDelayed(runnable, 0);
	}
	
	/*
	 * 게임 중간에 뒤로 가기 버튼을 눌렀을시 호출
	 * 게임 시간은 중단됨
	 * 종료하면 초기메뉴로
	 * 계속 하기를 선택하면 이어서 게임 시작.
	 */
	@Override
	public void onBackPressed() {
		((ThreadView) sfvTrack).setPause();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Exit!")
		.setMessage("게임을 종료하시겠습니까?")
		.setCancelable(true);
		
		builder.setPositiveButton("확 인", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				((ThreadView) sfvTrack).setStop();
				GameActivity.this.exitGame();
			}
		});
		
		builder.setNegativeButton("계속하기", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				((ThreadView) sfvTrack).setRestart();
			}
		});
		
		//취소 할 경우도 게임이 이어서 진행
		builder.setOnCancelListener(new OnCancelListener(){

			@Override
			public void onCancel(DialogInterface dialog) {
				((ThreadView) sfvTrack).setRestart();
			}
		});
		
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);		
		dialog.show();
	}
	
	/*
	 * 게임이 종료되었을떄 호출되는 함수
	 * success가 true이면 성공 false이면 실패
	 * dialog로 결과를 보여준다.
	 */
	public void gameFinish(boolean success){
		if(success){//성공했으면 기록을 보여준다.
			((ThreadView) sfvTrack).setStop();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		
    		builder.setTitle("성 공!")
    		.setMessage("기록 : "+String.valueOf(((ThreadView) sfvTrack).getUseTime())+"초")
    		.setCancelable(true);
    		
    		builder.setPositiveButton("확 인", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					GameActivity.this.exitGame();
				}
			});
    		
    		builder.setNegativeButton("다 음", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(GameActivity.this,GameActivity.class);
					intent.putExtra("stage", stage+1);
					startActivity(intent);//history가 남지 않으니 바로 다음 단계로 진행
				}
			});
    		
    		builder.setOnCancelListener(new OnCancelListener(){

    			@Override
    			public void onCancel(DialogInterface dialog) {
    				GameActivity.this.exitGame();
    			}
    		});
    		
    		
    		AlertDialog dialog = builder.create();
    		dialog.setCanceledOnTouchOutside(false);
    		dialog.show();
		}else{//실패
			((ThreadView) sfvTrack).setStop();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		
    		builder.setTitle("실 패!")
    		.setMessage("다시 도전해 주세요")
    		.setCancelable(true);
    		
    		builder.setPositiveButton("확 인", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					GameActivity.this.exitGame();;
				}
			});
    		
    		builder.setOnCancelListener(new OnCancelListener(){

    			@Override
    			public void onCancel(DialogInterface dialog) {
    				GameActivity.this.exitGame();
    			}
    		});
    		
    		AlertDialog dialog = builder.create();
    		dialog.setCanceledOnTouchOutside(false);
    		dialog.show();
		}
	}	
}
