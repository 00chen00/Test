package com.wan3456.sdk.bean;

import java.util.HashMap;
import java.util.List;


import com.wan3456.sdk.tools.Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridViewAdapter extends BaseAdapter {
	private List<HashMap<String, String>> list;
	private Context context;

	public GridViewAdapter(Context context, List<HashMap<String, String>> list) {
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
	
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MyView tag;
		
		if (convertView == null) {
			View v = LayoutInflater.from(context).inflate(
					Helper.getLayoutId(context, "wan3456_gridview_item"), null);
			tag = new MyView();

			tag.name = (TextView) v.findViewById(Helper.getResId(context,
					"wan3456_gridview_name"));
			tag.img = (ImageView) v.findViewById(Helper.getResId(context,
					"wan3456_gridview_img"));
			tag.point = (ImageView) v.findViewById(Helper.getResId(context,
					"wan3456_gridview_point"));
			v.setTag(tag);
			convertView = v;
		} else {
			tag = (MyView) convertView.getTag();
		}
		String name = list.get(position).get("name");
		tag.name.setText(name);
		switch (Integer.parseInt(list.get(position).get("index"))) {
		case 0:
			tag.img.setImageResource(Helper.getResDraw(context,
					"wan3456_fra_mes_icon"));
			break;
		case 1:
			tag.img.setImageResource(Helper.getResDraw(context,
					"wan3456_fra_modify_icon"));
			break;
		case 2:
			tag.img.setImageResource(Helper.getResDraw(context,
					"wan3456_fra_bind_icon"));
			break;
		case 3:
			tag.img.setImageResource(Helper.getResDraw(context,
					"wan3456_fra_qq_icon"));
			break;
		default:
			break;
		}
		if(list.get(position).get("red_show").equals("1")){
			tag.point.setVisibility(View.VISIBLE);
		}else{
		tag.point.setVisibility(View.GONE);}

		return convertView;
	}

}

class MyView {

	TextView name;
	ImageView img;
	ImageView point;

}
