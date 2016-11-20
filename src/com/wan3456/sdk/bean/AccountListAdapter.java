package com.wan3456.sdk.bean;

import java.util.HashMap;
import java.util.List;
import com.wan3456.sdk.tools.Helper;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

@SuppressLint("ViewHolder")
public class AccountListAdapter extends BaseAdapter {

	private List<HashMap<String, String>> list;
	private Context context;
	public AccountListAdapter(Context context, List<HashMap<String, String>> list) {
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
		View v = LayoutInflater.from(context).inflate(
				Helper.getLayoutId(context, "wan3456_login_pop_item"), null);
		TextView name=(TextView) v.findViewById(Helper.getResId(context, "wan3456_login_pop_name"));
		name.setText(list.get(position).get("username"));
		return v;
	}

}


