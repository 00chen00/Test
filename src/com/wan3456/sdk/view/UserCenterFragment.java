package com.wan3456.sdk.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.wan3456.sdk.Wan3456;
import com.wan3456.sdk.activity.InfoActivity;
import com.wan3456.sdk.bean.GridViewAdapter;
import com.wan3456.sdk.tools.Helper;
import com.wan3456.sdk.tools.StatusCode;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

public class UserCenterFragment extends Fragment implements OnClickListener,
		OnItemClickListener {

	public SharedPreferences sharedPreferences;
	private Button switch_btn;
	private MyGridView gridview;
	private GridViewAdapter adapter;

	private List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(
				Helper.getLayoutId(getActivity(), "wan3456_fragment_account"),
				null, false);
		sharedPreferences = getActivity().getSharedPreferences("yssdk_info",
				Context.MODE_PRIVATE);
		Button back = (Button) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_account_backtogame"));
		gridview = (MyGridView) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_fra_account_list"));
		switch_btn = (Button) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_account_switch"));
		TextView account = (TextView) v.findViewById(Helper.getResId(
				getActivity(), "wan3456_account_account"));

		switch_btn.setOnClickListener(this);
		back.setOnClickListener(this);
		account.setText("帐号:" + sharedPreferences.getString("name", ""));

		getList();
		adapter = new GridViewAdapter(getActivity(), list);
		if (sharedPreferences.getInt("sreen", StatusCode.LANDSCAPE) == StatusCode.LANDSCAPE) {
			gridview.setNumColumns(StatusCode.ACCOUNT_GRIDVIEW_NUM_LAND);
		} else {
			gridview.setNumColumns(StatusCode.ACCOUNT_GRIDVIEW_NUM_PORT);
		}

		gridview.setFocusable(false);
		gridview.setAdapter(adapter);
		gridview.setOnItemClickListener(this);

		return v;
	}

	private void getList() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("name", "我的消息");
		map.put("index", "0");

		map.put("red_show", getMesStatus());
		list.add(map);
		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("name", "修改密码");
		map1.put("index", "1");
		map1.put("red_show", "0");
		list.add(map1);
		HashMap<String, String> map2 = new HashMap<String, String>();
		map2.put("name", "绑定手机");
		map2.put("index", "2");
		map2.put("red_show", "0");
		list.add(map2);
		HashMap<String, String> map3 = new HashMap<String, String>();
		map3.put("name", "绑定QQ");
		map3.put("index", "3");
		map3.put("red_show", "0");
		list.add(map3);
	}

	private String getMesStatus() {
		if (sharedPreferences.getInt("isred_mes", 0) == 1) {
			return "1";
		}

		return "0";
	}

	private void onBackToGameClick() {
		getActivity().finish();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == Helper.getResId(getActivity(),
				"wan3456_account_switch")) {
			Editor editor = sharedPreferences.edit();// 获取编辑器
			editor.putBoolean("ISLogin", false);
			editor.putBoolean("shake", false);
			editor.commit();
			Wan3456.userListener.onLogout();
			getActivity().finish();
			Wan3456.getInstance(getActivity()).close();
			return;
		}
		if (v.getId() == Helper.getResId(getActivity(),
				"wan3456_account_backtogame")) {
			onBackToGameClick();
			return;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (arg0.getId() == Helper.getResId(getActivity(),
				"wan3456_fra_account_list")) {
			HashMap<String, String> value = (HashMap<String, String>) gridview
					.getItemAtPosition(arg2);
			jumpToPage(Integer.parseInt(value.get("index").toString()));
		}

	}

	private void jumpToPage(int tag) {
		InfoActivity ac;
		switch (tag) {
		case 0:
			ac = (InfoActivity) getActivity();
			Editor edit = sharedPreferences.edit();
			edit.putInt("isred_mes", 0);
			edit.commit();
			ac.setCurFragment(InfoActivity.INDEX_MESSAGE_CENTER);
			break;
		case 1:
			ac = (InfoActivity) getActivity();
			ac.setCurFragment(InfoActivity.INDEX_CHANGE_PASS);
			break;
		case 2:
			ac = (InfoActivity) getActivity();
			ac.setCurFragment(InfoActivity.INDEX_BIND_PHONE);
			break;
		case 3:
			ac = (InfoActivity) getActivity();
			ac.setCurFragment(InfoActivity.INDEX_BIND_QQ);
			break;

		default:
			break;
		}

	}

}
