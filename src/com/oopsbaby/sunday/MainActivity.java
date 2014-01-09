package com.oopsbaby.sunday;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/*
 * 앱 실행시 처음 실행되는 Activity
 */
public class MainActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//5단계 버튼에 이벤트를 붙여줌
		for(int i=1;i<=5;i++){
			String name = "stage_"+String.valueOf(i);
			Button bv = (Button)findViewById( getResources().getIdentifier(name, "id", getPackageName()) );
			bv.setOnClickListener(this);
		}
	}
	
	/*
	 * button view에 tag 값을 불러와서 GameActivity를 실행
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		Intent intent = new Intent(v.getContext(),GameActivity.class);
		intent.putExtra("stage", Integer.parseInt(v.getTag().toString()));
		startActivity(intent);
	}
	
	/*
	 * 게임 종료 함수.
	 */
	public void exitGame(){
		finish();
	}
	
	/*
	 * 뒤로가기 버튼을 눌렀을때 다이럴로그가 떠서 종료여부를 확인
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle("Exit")
		.setMessage("종료 하시겠습니까?")
		.setCancelable(true);
		builder.setPositiveButton("확 인", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//exitGame 메소도를 호출해서 종료.
				MainActivity.this.exitGame();
			}
		});
		
		builder.setNegativeButton("취 소", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		AlertDialog dialog = builder.create();
		
		dialog.show();
	}
}
