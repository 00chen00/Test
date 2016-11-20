package com.wan3456.sdk.activity;

import com.wan3456.sdk.Wan3456;
import com.wan3456.sdk.tools.Helper;
import com.wan3456.sdk.tools.StatusCode;

import com.wan3456.sdk.view.FindAccountFrame;
import com.wan3456.sdk.view.LoginFrame;
import com.wan3456.sdk.view.RegisterFrame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

public class LoginActivity extends FragmentActivity {
	private static final String KEY_INDEX = "key_index";
	public SharedPreferences sharedPreferences;
	public static final int INDEX_MANUAL_LOGIN= 0;//登录
//	public static final int INDEX_AUTO_LOGIN = 1;//自动登录	
	public static final int INDEX_REGISTER = 1;//注册
	public static final int INDEX_FIND_ACCOUNT = 2;//找回帐号
	private Fragment[] mFragments = { null, null, null};
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(Helper.getLayoutId(LoginActivity.this,
				"wan3456_activity_login"));
		sharedPreferences = getSharedPreferences("yssdk_info",
				Context.MODE_PRIVATE);
		if (sharedPreferences.getInt("sreen", StatusCode.LANDSCAPE) == StatusCode.PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		
		Intent intent = getIntent();
		int index = intent.getIntExtra(KEY_INDEX, INDEX_MANUAL_LOGIN);
		setCurFragment(index);
	}
	public static void show(Context ctx, int index) {
		Intent intent = new Intent();
		intent.putExtra(KEY_INDEX, index);
		intent.setClass(ctx, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(intent);
	}

	public  void setCurFragment(int index) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		for (Fragment frag : mFragments) {
			if (frag != null) {
				ft.hide(frag);
			}
		}

		if (mFragments[index] == null) {
			switch (index) {
			case INDEX_MANUAL_LOGIN:
				mFragments[index] = new LoginFrame();
				break;
			case INDEX_REGISTER:
				mFragments[index] = new RegisterFrame();
				break;
			case INDEX_FIND_ACCOUNT:
				mFragments[index] = new  FindAccountFrame();
				break;

			}
			ft.add(Helper.getResId(LoginActivity.this, "wan3456_login_content"),
					mFragments[index]);
		}
		Log.i("wan3456", "loginactiivty: index>>>>>>>>" + String.valueOf(index));
		ft.show(mFragments[index]);
		ft.commit();
		
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Editor editor = sharedPreferences.edit();// 获取编辑器
		editor.putBoolean("islock", false);
		editor.commit();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Wan3456.userListener.onLoginFailed("用户取消登录");
		}
		return super.onKeyDown(keyCode, event);
	}
}
