package com.wan3456.sdk.view;

import com.wan3456.sdk.tools.Helper;
import com.wan3456.sdk.tools.StatusCode;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class AutoLoginFrame extends Fragment {
	private SharedPreferences sharedPreferences;
	LinearLayout lin;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(
				Helper.getLayoutId(getActivity(), "wan3456_view_autologin"),
				null, false);
		sharedPreferences = getActivity().getSharedPreferences("yssdk_info",
				Context.MODE_PRIVATE);	
		lin = (LinearLayout) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_login_aotu_line"));
		WindowManager m = getActivity().getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		int a;
		if (sharedPreferences.getInt("sreen", StatusCode.LANDSCAPE) == StatusCode.PORTRAIT) {
			a = (int) (d.getWidth() * 0.85);
		} else {
			a = (int) (d.getWidth() * 0.48);
		}
		LayoutParams layoutParams = (LayoutParams) lin.getLayoutParams();
		layoutParams.width = a;
		lin.setLayoutParams(layoutParams);
		return v;
	}
}
