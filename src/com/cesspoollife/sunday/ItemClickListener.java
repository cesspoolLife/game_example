package com.cesspoollife.sunday;

import java.util.List;

import android.graphics.Path;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;

public class ItemClickListener implements AdapterView.OnItemClickListener {
	private int first_position;
	private PathView pv=null;
	public ItemClickListener(){

		this.first_position = -1;
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		if(pv==null)
			pv = (PathView)view.getRootView().findViewById(R.id.cavas_path);
		Shisensho card = Shisensho.getInstace();
		if(card.get(position)==0||this.first_position==position)
			return;
		else if(this.first_position!=-1){
			List<Integer> path = card.isMatch(this.first_position, position);
			if(!path.isEmpty()){
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
					pv.setCoordinates(p);
				}
				pv.setCoordinates(p);
				((FrameLayout)view).removeAllViews();
				((FrameLayout)parent.getChildAt(this.first_position)).removeAllViews();
				card.removeCard(this.first_position);
				card.removeCard(position);
				card.isAvailable();
				if(card.isFinish())
					((GameActivity)view.getContext()).gameFinish(true);
				Handler handler = new Handler();
				Runnable runnable = new Runnable() {
			        public void run() {
			        	ItemClickListener.this.removePath();
			        }
			    };
			    handler.postDelayed(runnable, 300);
				this.first_position = -1;
			}else
				this.first_position = position;
		}else{
			this.first_position = position;
		}
	}
	
	void removePath(){
		pv.setCoordinates(null);
	}

}
