package com.oopsbaby.sunday;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class GroupAdapter extends BaseAdapter {
	
	private Context mContext;
	int layoutResourceId;
    Shisensho block;

	public GroupAdapter(Context context, int layoutResourceId, Shisensho block){
		this.mContext = context;
		this.layoutResourceId = layoutResourceId;
        this.block = block;
	}
	
	@Override
	public int getCount() {
		return block.size();
	}

	@Override
	public Object getItem(int position) {
		return block.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	/*
	 * GridView Adapter를 통해 block image들을 보여준다.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = ((Activity) mContext).getLayoutInflater().inflate(layoutResourceId, parent, false);
		int value = block.get(position);
		ImageView iv = (ImageView) convertView.findViewById(R.id.blockimage);
		if(value==1)
			iv.setImageResource(R.drawable.img_1);
		else if(value==2)
			iv.setImageResource(R.drawable.img_2);
		else if(value==3)
			iv.setImageResource(R.drawable.img_3);
		else if(value==4)
			iv.setImageResource(R.drawable.img_4);
		else if(value==5)
			iv.setImageResource(R.drawable.img_5);
		else if(value==6)
			iv.setImageResource(R.drawable.img_6);
		else if(value==7)
			iv.setImageResource(R.drawable.img_7);
		else if(value==8)
			iv.setImageResource(R.drawable.img_8);
		else if(value==9)
			iv.setImageResource(R.drawable.img_9);
		else if(value==10)
			iv.setImageResource(R.drawable.img_10);
		else if(value==0){
			((FrameLayout)convertView).removeAllViews();
		}
		return convertView;
	}

}
