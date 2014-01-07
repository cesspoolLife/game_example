package com.cesspoollife.sunday;

import java.util.List;

import android.graphics.Path;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;

public class ItemClickListener implements AdapterView.OnItemClickListener {
	private int first_position;
	public ItemClickListener(){

		this.first_position = -1;
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		Shisensho card = Shisensho.getInstace();
		if(card.get(position)==0||this.first_position==position)
			return;
		else if(this.first_position!=-1){
			List<Integer> path = card.isMatch(this.first_position, position);
			if(!path.isEmpty()){
				//폭발 애니메이션을 위한 두 포지션의 위치를 구해서 폭발 애니메이션 함수 호출 
				int[] p1 = new int[2];
				int[] p2 = new int[2];
				view.getLocationInWindow(p1);
				((FrameLayout)parent.getChildAt(this.first_position)).getLocationInWindow(p2);
				((GameActivity)view.getContext()).drawBoom(p1, p2);
				
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
				((GameActivity)view.getContext()).setPath(p);
				
				((FrameLayout)view).removeAllViews(); //두 위치의 이미지 제거
				((FrameLayout)parent.getChildAt(this.first_position)).removeAllViews();
				
				card.removeCard(this.first_position);//두 위치를 0으로 변경
				card.removeCard(position);
				
				card.isAvailable();
				if(card.isFinish())//패가 없을 시 게임 종료 함수 호출
					((GameActivity)view.getContext()).gameFinish(true);
				this.first_position = -1;
			}else
				this.first_position = position;
		}else{
			this.first_position = position;
		}
	}
}
