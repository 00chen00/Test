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
import android.widget.LinearLayout;
import android.widget.TextView;

public class PayWayAdapter extends BaseAdapter {
	private List<HashMap<String, String>> list;
	private Context context;

	public PayWayAdapter(Context context, List<HashMap<String, String>> list) {
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

		PayWayView tag;
		if (convertView == null) {
			View v = LayoutInflater.from(context).inflate(
					Helper.getLayoutId(context, "wan3456_paylist_view"), null);
			tag = new PayWayView();
			tag.name = (TextView) v.findViewById(Helper.getResId(context,
					"wan3456_pay_name"));
			tag.payIcon = (ImageView) v.findViewById(Helper.getResId(context,
					"wan3456_pay_item_corner"));
			tag.line = (LinearLayout) v.findViewById(Helper.getResId(context,
					"wan3456_paylist_item_reline"));
			v.setTag(tag);
			convertView = v;
		} else {
			tag = (PayWayView) convertView.getTag();
		}

		if (list.get(position).get("pay_check").equals("yes")) {
			tag.line.setBackgroundResource(Helper.getResCol(context,
					"wan3456_detail_c1"));
			tag.name.setTextColor(context.getResources().getColor(
					Helper.getResCol(context, "wan3456_white")));

		} else {
			tag.line.setBackgroundResource(Helper.getResCol(context,
					"wan3456_detail_c5"));
			tag.name.setTextColor(context.getResources().getColor(
					Helper.getResCol(context, "wan3456_detail_c1")));
		}

		if (list.get(position).get("pay_type").toString().equals("alipay")) {
			tag.payIcon.setImageResource(Helper.getResDraw(context,
					"wan3456_alipay_icon"));
		}
		if (list.get(position).get("pay_type").toString().equals("yeepay")) {
			tag.payIcon.setImageResource(Helper.getResDraw(context,
					"wan3456_yhpay_icon"));
		}

		if (list.get(position).get("pay_type").toString().equals("wftpay")) {
			tag.payIcon.setImageResource(Helper.getResDraw(context,
					"wan3456_wxpay_icon"));
		}
		if (list.get(position).get("pay_type").toString().equals("unionpay")) {
			tag.payIcon.setImageResource(Helper.getResDraw(context,
					"wan3456_ylpay_icon"));
		}

		String text = (String) list.get(position).get("pay_name");
		tag.name.setText(text);
		return convertView;
	}

}

class PayWayView {
	TextView name;
	ImageView payIcon;
	LinearLayout line;
}
