package com.wan3456.sdk.activity;

import com.wan3456.sdk.tools.Helper;
import com.wan3456.sdk.tools.StatusCode;
import com.wan3456.sdk.view.UserCenterFragment;
import com.wan3456.sdk.view.BindPhoneFragment;
import com.wan3456.sdk.view.ChangePassFragment;
import com.wan3456.sdk.view.LastMessageFragment;
import com.wan3456.sdk.view.MessageCenterFragment;
import com.wan3456.sdk.view.QQFragment;
import com.wan3456.sdk.view.ServiceCenterFragment;

import com.wan3456.sdk.view.UnbindFragment;
import com.wan3456.sdk.view.WebFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Window;

public class InfoActivity extends FragmentActivity {
	private static final String KEY_INDEX = "key_index";
	public SharedPreferences sharedPreferences;
	public static final int INDEX_ACCOUNT = 0;
	public static final int INDEX_CHANGE_PASS = 1;
	public static final int INDEX_BIND_PHONE = 2;
	public static final int INDEX_GIFT = 3;
	public static final int INDEX_ACTIVITY = 4;
	public static final int INDEX_SITE = 5;
	public static final int INDEX_TJ = 6;

	public static final int INDEX_BIND_QQ = 7;
	public static final int INDEX_MESSAGE_CENTER = 8;
	public static final int INDEX_UNBIND = 9;
	public static final int INDEX_LAST_MESSAGE = 10;
	public static final int INDEX_SERVICE_CENTER = 11;
	private Fragment[] mFragments = { null, null, null, null, null, null, null,
			null, null, null, null, null };
	private static int tag;
	@Override
	protected void onDestroy() {
		super.onDestroy();
	
	}

	public static void show(Context ctx, int index) {
		tag = index;
		Intent intent = new Intent();
		intent.putExtra(KEY_INDEX, index);
		intent.setClass(ctx, InfoActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(Helper.getLayoutId(InfoActivity.this,
				"wan3456_activity_info"));
		sharedPreferences = getSharedPreferences("yssdk_info",
				Context.MODE_PRIVATE);
		if (sharedPreferences.getInt("sreen", StatusCode.LANDSCAPE) == StatusCode.PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}

		Intent intent = getIntent();
		int index = intent.getIntExtra(KEY_INDEX, INDEX_ACCOUNT);
		setCurFragment(index);
	}

	public void setCurFragment(int index) {

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		for (Fragment frag : mFragments) {
			if (frag != null) {
				ft.hide(frag);
			}
		}

		if (mFragments[index] == null) {
			switch (index) {
			case INDEX_ACCOUNT:
				mFragments[index] = new UserCenterFragment();
				break;
			case INDEX_CHANGE_PASS:
				mFragments[index] = new ChangePassFragment();
				break;
			case INDEX_BIND_PHONE:
				mFragments[index] = new BindPhoneFragment();
				break;
			case INDEX_GIFT:
				mFragments[index] = new WebFragment("礼包中心",
						sharedPreferences.getString("giftUrl", null));
				break;
			case INDEX_ACTIVITY:
				mFragments[index] = new WebFragment("游戏专区",
						sharedPreferences.getString("activityUrl", null));
				break;
			case INDEX_SITE:
				mFragments[index] = new WebFragment("游戏论坛",
						sharedPreferences.getString("bbsUrl", null));
				break;
			case INDEX_TJ:
				mFragments[index] = new WebFragment("游戏中心",
						sharedPreferences.getString("tjUrl", null));
				break;
			case INDEX_BIND_QQ:
				mFragments[index] = new QQFragment();
				break;

			case INDEX_MESSAGE_CENTER:
				mFragments[index] = new MessageCenterFragment(tag);
				break;
			case INDEX_UNBIND:
				mFragments[index] = new UnbindFragment();
				break;
			case INDEX_LAST_MESSAGE:
				mFragments[index] = new LastMessageFragment();
				break;
			case INDEX_SERVICE_CENTER:
				mFragments[index] = new ServiceCenterFragment();
				break;		

			}
			ft.add(Helper.getResId(InfoActivity.this, "wan3456_info_content"),
					mFragments[index]);
		}
		Log.i("wan3456", "infoactiivty: index>>>>>>>>" + String.valueOf(index));
		ft.show(mFragments[index]);
		ft.commit();
	}

}
