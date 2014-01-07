package com.cesspoollife.sunday;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.GridView;

public class GameActivity extends Activity {
	private int stage;
    private SurfaceView sfvTrack;
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
		
		Shisensho card = Shisensho.getInstace();
		card.makeCard(stage);
		
		GroupAdapter adapter = new GroupAdapter(this, R.layout.image_layout, card);
		GridView gv = (GridView)findViewById(R.id.cardgroup);
		gv.setAdapter(adapter);
		gv.setOnItemClickListener(new ItemClickListener());
		
	}
	
	public void setPath(Path p){
		((ThreadView) sfvTrack).setPath(p);
		if(p==null)
			return;
		Handler handler = new Handler();
		Runnable runnable = new Runnable() {
	        public void run() {
	        	GameActivity.this.setPath(null);
	        }
	    };
	    handler.postDelayed(runnable, 300); 
	}
	
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
	
	public void timeOver(){
		handler.postDelayed(runnable, 10);
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
		//Chain together various setter methods to set the dialog characteristics
		builder.setTitle("Exit!")
		.setMessage("게임을 종료하시겠습니까?")//set message in content area.
		.setCancelable(true);
		
		//set action buttons, you can get button text from resources too.
		builder.setPositiveButton("확 인", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
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
			}});
		
		AlertDialog dialog = builder.create();
		
		//show dialog
		dialog.show();
	}

	/*
	 * 게임이 종료되었을떄 호출되는 함수
	 * success가 true이면 성공 false이면 실패
	 * dialog로 결과를 보여준다.
	 */
	public void gameFinish(boolean success){
		if(success){
			((ThreadView) sfvTrack).setPause();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		
    		//Chain together various setter methods to set the dialog characteristics
    		builder.setTitle("성 공!")
    		.setMessage("기록 : "+String.valueOf(((ThreadView) sfvTrack).getRemainTime())+"초")//set message in content area.
    		.setCancelable(true);
    		
    		//set action buttons, you can get button text from resources too.
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
					startActivity(intent);
				}
			});
    		
    		AlertDialog dialog = builder.create();
    		
    		//show dialog
    		dialog.show();
		}else{
			((ThreadView) sfvTrack).setPause();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		
    		//Chain together various setter methods to set the dialog characteristics
    		builder.setTitle("실 패!")
    		.setMessage("다시 도전해 주세요")//set message in content area.
    		.setCancelable(true);
    		
    		//set action buttons, you can get button text from resources too.
    		builder.setPositiveButton("확 인", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					GameActivity.this.exitGame();;
				}
			});
    		
    		AlertDialog dialog = builder.create();
    		
    		//show dialog
    		dialog.show();
		}
	}	
}
