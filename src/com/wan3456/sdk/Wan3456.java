package com.wan3456.sdk;

import com.wan3456.sdk.activity.LoginActivity;
import com.wan3456.sdk.activity.PayActivity;
import com.wan3456.sdk.bean.CheckPayCallBackListener;
import com.wan3456.sdk.bean.PaymentInfo;
import com.wan3456.sdk.bean.SerInfo;
import com.wan3456.sdk.inter.ChooseSerCallBackListener;
import com.wan3456.sdk.inter.ExitCallBackListener;
import com.wan3456.sdk.inter.FloatViewCallBackListener;
import com.wan3456.sdk.inter.InitCallBackListener;

import com.wan3456.sdk.inter.PayCallBackListener;
import com.wan3456.sdk.inter.UserCallBackListener;
import com.wan3456.sdk.tools.Helper;
import com.wan3456.sdk.tools.InterTool;
import com.wan3456.sdk.tools.StatusCode;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

@SuppressLint("CommitPrefEdits")
public class Wan3456 {
	public static Wan3456 instance;
	public Context context;
	public static InitCallBackListener inListener;
	public static UserCallBackListener userListener;
	public static PayCallBackListener payListener;
	public static FloatViewCallBackListener floatListener;
	public static ChooseSerCallBackListener serListener;
	public static ExitCallBackListener exitListener;
	public static CheckPayCallBackListener checkListener;
	public static boolean hasInit = false;
	public SharedPreferences sharedPreferences;
	private SensorManager sensorManager;
	private Vibrator vibrator;
	private SdkService mFloatViewService;
	/**
	 * 连接到Service
	 */
	private final ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder iBinder) {
			mFloatViewService = ((SdkService.FloatViewServiceBinder) iBinder)
					.getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mFloatViewService = null;
		}
	};

	Wan3456(Context context) {
		super();
		this.context = context;
		sharedPreferences = context.getSharedPreferences("yssdk_info",
				Context.MODE_PRIVATE);
	}

	public static Wan3456 getInstance(Context context) {

		if (instance == null) {
			instance = new Wan3456(context);
		}
		return instance;
	}

	/**
	 * 初始化接口
	 * 
	 * @param text
	 * @param listener
	 * @param screenOrientation
	 */
	public void initSdk(Context context, int screenOrientation,
			InitCallBackListener listener) {
		Log.i("wan3456", "初始化init>>>>>>version=" + StatusCode.SDK_VERSION);
		this.context = context;
		inListener = listener;
		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		Helper.getPhoneDev(context, sharedPreferences, screenOrientation);

	}

	/**
	 * 设置用户监听器
	 * 
	 * @param context
	 * @param listener
	 */
	public void setUserListener(Context context, UserCallBackListener listener) {
		this.context = context;
		userListener = listener;
	}

	/**
	 * 登录接口
	 * 
	 * @param context
	 * @param listener
	 */
	public void login(Context context) {
		Log.i("wan3456", "调起登录：login>>>>>>");
		this.context = context;
		if (hasInit == false || userListener == null) {
			userListener.onLoginFailed("请先初始化及设置用户监听");
			return;
		}
		if (sharedPreferences.getBoolean("ISLogin", false)) {
			userListener.onLoginFailed("请先退出已登录的帐号");
			return;
		}
		if (sharedPreferences.getBoolean("islock", false) == false) {
			Intent intent = new Intent();
			intent.setClass(context, LoginActivity.class);
			context.startActivity(intent);
			Editor editor = sharedPreferences.edit();// 获取编辑器
			editor.putBoolean("islock", true);
			editor.commit();
		}

	}

	/**
	 * 登出接口
	 * 
	 * @param context
	 * @param listener
	 */
	public void logout(Context context) {
		Log.i("wan3456", "调起登出：logout>>>>>>");
		this.context = context;
		if (hasInit == true && userListener != null
				&& sharedPreferences.getBoolean("ISLogin", false)) {
			Editor editor = sharedPreferences.edit();// 获取编辑器
			editor.putBoolean("ISLogin", false);
			editor.putBoolean("shake", false);
			editor.commit();
			userListener.onLogout();
			close();
		} else {
            Log.i("wan3456", "请先登录帐号！");
	
		}
	}

	/**
	 * 支付接口
	 * 
	 * @param context
	 * @param listener
	 */
	public void pay(Context context, PaymentInfo info,
			PayCallBackListener listener) {
		Log.i("wan3456", "调起支付pay：PaymentInfo>>>>>>" + info.toString());
		payListener = listener;
		this.context = context;
		if (hasInit == true && sharedPreferences.getBoolean("ISLogin", false)) {
			Editor editor = sharedPreferences.edit();// 获取编辑器
			editor.putInt("serId", info.getServerId());
			editor.putString("serName", info.getServerName());
			editor.putString("eInfo", info.getExtraInfo());
			editor.putString("gRole", info.getGameRole());
			editor.putInt("roleLevel", info.getRoleLevel());
			editor.putString("itemName", info.getItemName());
			if (info.getAmount() != 0) {
				editor.putInt("AMOUNT", info.getAmount());
				editor.putInt("count", info.getCount());
				editor.putInt("IsLimit", 1);
			} else {
				editor.putInt("IsLimit", 0);
				editor.putInt("ratio", info.getRatio());
			}
			editor.commit();
			Intent intent = new Intent();
			intent.setClass(context, PayActivity.class);
			context.startActivity(intent);

		} else {
            payListener.callback(StatusCode.PAY_STATIC_EXIT, null);
		}
	}

	/**
	 * 创建悬浮窗
	 */
	public void show() {
		Log.i("wan3456", "show >>>>>>");
		Intent intent = new Intent(context, SdkService.class);
		context.startService(intent);
		context.bindService(intent, mServiceConnection,
				Context.BIND_AUTO_CREATE);
	}

	/**
	 * 关闭悬浮窗
	 */
	public void close() {
		Log.i("wan3456", "float close>>>>>>>>>>1");
		if (mFloatViewService != null) {
			context.unbindService(mServiceConnection);
			context.stopService(new Intent(context, SdkService.class));
			//context.unbindService(mServiceConnection);
			mFloatViewService = null;
		}
	}

	/**
	 * 生命周期：onResume
	 * 
	 * @param context
	 */
	public void onResume() {
		Log.i("wan3456", "onResume.>>>>>>>>>>>>>");
		if (mFloatViewService != null) {
			mFloatViewService.showFloat();

		}
		if (sensorManager != null) {// 注册监听器
			sensorManager.registerListener(sensorEventListener,
					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_NORMAL);
			// 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
		}
	}

	/**
	 * 生命周期：onPause
	 * 
	 * @param context
	 */

	public void onPause() {
		Log.i("wan3456", "onPause.>>>>>>>>>>>>>");
		if (mFloatViewService != null) {
			mFloatViewService.hideFloat();

		}
		if (sensorManager != null) {// 取消监听器
			sensorManager.unregisterListener(sensorEventListener);
		}
	}

	/**
	 * 生命周期：onDestory
	 * 
	 * @param context
	 */
	public void onDestory() {
		Editor editor = sharedPreferences.edit();// 获取编辑器
		editor.putBoolean("ISLogin", false);
		editor.putBoolean("shake", false);
		editor.commit();
//		if (mFloatViewService != null) {
//			context.unbindService(mServiceConnection);
//			context.stopService(new Intent(context, SdkService.class));
//			//context.unbindService(mServiceConnection);
//			mFloatViewService = null;
//		}

	}

	/**
	 * 上传游戏信息接口
	 * 
	 * @param context
	 * @param info
	 * @param listener
	 */
	public void passSerInfo(Context context, SerInfo info,
			ChooseSerCallBackListener listener) {
		Log.i("wan3456", "上传游戏信息passSerInfo：serInfo>>>>>>>" + info.toString());
		serListener = listener;
		SerInfo sinfo = new SerInfo();
		sinfo = info;
		if (hasInit == true) {
			// 获取编辑器
			String id = String.valueOf(sharedPreferences.getString("name", ""));
			sinfo.setUid(id);
			InterTool.getInstance(context).passSer(sinfo);
		}

	}

	/**
	 * 显示退出sdk对话框接口
	 * 
	 * @param context
	 * @param listener
	 */
	public void exitSDK(Context context, ExitCallBackListener listener) {
		Log.i("wan3456", "调用退出对话框exitSDK >>>>>>");
		this.context = context;
		exitListener = listener;
		// 初始化及登录成功才能显示退出框
		if (hasInit == true
				&& sharedPreferences.getBoolean("ISLogin", false) == true) {
			InterTool.getInstance(context).showExitDialog(context);
		} else {
			close();
			Wan3456.exitListener.callback(StatusCode.EXIT_GAME, "exit game");
		}

	}

	/**
	 * 重力感应监听
	 */
	private SensorEventListener sensorEventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			// 传感器信息改变时执行该方法
			float[] values = event.values;
			float x = values[0]; // x轴方向的重力加速度，向右为正
			// float y = values[1]; // y轴方向的重力加速度，向前为正
			// float z = values[2]; // z轴方向的重力加速度，向上为正
			// Log.i("wan3456", "左右：x="+x+"，前后:y="+y+",上下：z="+z);
			int medumValue = 22;
			if (Math.abs(x) > medumValue) {
				if (sharedPreferences.getBoolean("shake", false)) {

					Helper.openShakeFloat(context, sharedPreferences, vibrator);
				}
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}
	};

}
