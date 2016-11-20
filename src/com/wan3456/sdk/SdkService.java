package com.wan3456.sdk;


import com.wan3456.sdk.view.FloatView;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;

import android.os.IBinder;
import android.util.Log;

public class SdkService extends Service {

	public SharedPreferences sharedPreferences;
	public static FloatView mFloatView;

	@Override
	public void onCreate() {

		super.onCreate();
		Log.i("wan3456", "sdk service is oncreate>>>>>");
		mFloatView = new FloatView(this);
		showFloat();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new FloatViewServiceBinder();
	}

	public void showFloat() {

		if (mFloatView != null) {
			Log.i("wan3456", "sdkservice show>>>>>>.");
			mFloatView.show();
		}
	}

	public void hideFloat() {
		if (mFloatView != null) {
			mFloatView.hide();
		}
	}

	public void destroyFloat() {
		if (mFloatView != null) {
			mFloatView.destroy();
		}
		mFloatView = null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		destroyFloat();
	}

	public class FloatViewServiceBinder extends Binder {
		public SdkService getService() {
			return SdkService.this;
		}
	}
}
