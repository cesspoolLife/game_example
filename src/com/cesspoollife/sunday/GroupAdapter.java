package com.cesspoollife.sunday;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GroupAdapter extends BaseAdapter {
	
	private Context mContext;
	int layoutResourceId;
    Shisensho card;

	public GroupAdapter(Context context, int layoutResourceId, Shisensho card){
		this.mContext = context;
		this.layoutResourceId = layoutResourceId;
        this.card = card;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return card.size();
	}

	@Override
	public Object getItem(int position) {
		return card.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = ((Activity) mContext).getLayoutInflater().inflate(layoutResourceId, parent, false);
		ImageView iv = (ImageView) convertView.findViewById(R.id.cardimage);
		int value = card.get(position);
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
		return convertView;
	}

}
