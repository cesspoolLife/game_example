package com.cesspoollife.sunday;

import java.util.List;

import android.graphics.Path;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;

/*
 * Grid View Block Click Listener
 */
public class ItemClickListener implements AdapterView.OnItemClickListener {
	
	private int first_position;
	
	/* 
	 * 생성자
	 * 처음 위치를 실제 존재하지 않는 -1로 
	 */
	public ItemClickListener(){
		this.first_position = -1;
	}
	
	/*
	 * 아이템 선택시 호출 되는 함수
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Shisensho block = Shisensho.getInstace();

		//선택 위치가 비어있거나 동일 한 곳을 선택시 아무일도 일어나지 않는다.
		if(block.get(position)==0||this.first_position==position)
			return;
		else if(this.first_position!=-1){
			//두 개의 Block으로 연결 가능 여부 확인.
			List<Integer> path = block.isMatch(this.first_position, position);
			
			//path가 있다면 두개의 Block 연결이 가능하다는 것
			if(!path.isEmpty()){
				//폭발 애니메이션을 위한 두 포지션의 위치를 구해서 폭발 애니메이션 함수 호출 
				int[] p1 = new int[2];
				int[] p2 = new int[2];
				view.getLocationInWindow(p1);
				((FrameLayout)parent.getChildAt(this.first_position)).getLocationInWindow(p2);
				((GameActivity)view.getContext()).drawBoom(p1, p2);
				
				//두개의 연결 선을 보여주기 위해 isMatch 메소드를 통해 받은 위치들의 패스를 만들어줌.
				Path p = new Path();
				for(int i=0;i<path.size();i++){
					View child = parent.getChildAt(path.get(i));
					int[] location = new int[2];
					child.getLocationInWindow(location);
					if(p.isEmpty())
						p.moveTo(location[0], location[1]);
					else{
						p.lineTo(location[0], location[1]);
					}
				}
				((GameActivity)view.getContext()).setPath(p);//만들어진 Path를 넘겨줌	
				
				block.removeCard(this.first_position);//두 위치를 0으로 변경
				block.removeCard(position);
				
				//블럭이 연결가능하지 않다면 재배열
				while(!block.isAvailable()){
					if(block.isFinish()){//남은 블럭이 없을 시 게임 종료 함수 호출
						((GameActivity)view.getContext()).gameFinish(true);
						break;
					}
					block.setReArrage();
					((GameActivity)view.getContext()).setGridViewChange();//재배열할때만 GridView를 변경
				}
			//	((GameActivity)view.getContext()).setGridViewChange();
				((FrameLayout)view).removeAllViews(); //두 위치의 이미지만 제거 setGridViewChange()사용하는 것보다 빠름.
                ((FrameLayout)parent.getChildAt(this.first_position)).removeAllViews();
                
				this.first_position = -1;//첫위치 다시 초기화
			}else
				this.first_position = position;//두개의 Block이 연결이 안될경우로 마지막 선택한 위치를 저장해준다.
		}else{
			this.first_position = position;//첫위치가 -1인 경우로 아직 하나도 선택이 되어 있지 않은 상황이니 첫위치를 선택한 위치로.
		}
	}
}
