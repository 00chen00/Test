package com.wan3456.sdk.tools;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import org.aapache.commons.codec.binary.Base64;
import org.json.JSONObject;


import com.wan3456.sdk.Wan3456;
import com.wan3456.sdk.bean.MetaConfig;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class Helper {

	private static final int NETWORK_TYPE_UNAVAILABLE = -1;
	private static final int NETWORK_TYPE_WIFI = -101;

	private static final int NETWORK_CLASS_WIFI = -101;
	private static final int NETWORK_CLASS_UNAVAILABLE = -1;
	/** Unknown network class. */
	private static final int NETWORK_CLASS_UNKNOWN = 0;
	/** Class of broadly defined "2G" networks. */
	private static final int NETWORK_CLASS_2_G = 1;
	/** Class of broadly defined "3G" networks. */
	private static final int NETWORK_CLASS_3_G = 2;
	/** Class of broadly defined "4G" networks. */
	private static final int NETWORK_CLASS_4_G = 3;

	// 适配低版本手机
	/** Network type is unknown */
	public static final int NETWORK_TYPE_UNKNOWN = 0;
	/** Current network is GPRS */
	public static final int NETWORK_TYPE_GPRS = 1;
	/** Current network is EDGE */
	public static final int NETWORK_TYPE_EDGE = 2;
	/** Current network is UMTS */
	public static final int NETWORK_TYPE_UMTS = 3;
	/** Current network is CDMA: Either IS95A or IS95B */
	public static final int NETWORK_TYPE_CDMA = 4;
	/** Current network is EVDO revision 0 */
	public static final int NETWORK_TYPE_EVDO_0 = 5;
	/** Current network is EVDO revision A */
	public static final int NETWORK_TYPE_EVDO_A = 6;
	/** Current network is 1xRTT */
	public static final int NETWORK_TYPE_1xRTT = 7;
	/** Current network is HSDPA */
	public static final int NETWORK_TYPE_HSDPA = 8;
	/** Current network is HSUPA */
	public static final int NETWORK_TYPE_HSUPA = 9;
	/** Current network is HSPA */
	public static final int NETWORK_TYPE_HSPA = 10;
	/** Current network is iDen */
	public static final int NETWORK_TYPE_IDEN = 11;
	/** Current network is EVDO revision B */
	public static final int NETWORK_TYPE_EVDO_B = 12;
	/** Current network is LTE */
	public static final int NETWORK_TYPE_LTE = 13;
	/** Current network is eHRPD */
	public static final int NETWORK_TYPE_EHRPD = 14;
	/** Current network is HSPA+ */
	public static final int NETWORK_TYPE_HSPAP = 15;

	public static int getLayoutId(Context context, String name) {
		return context.getResources().getIdentifier(name, "layout",
				context.getPackageName());

	}

	public static int getResId(Context context, String name) {
		return context.getResources().getIdentifier(name, "id",
				context.getPackageName());

	}

	public static int getResDraw(Context context, String name) {
		return context.getResources().getIdentifier(name, "drawable",
				context.getPackageName());

	}

	public static int getResStyle(Context context, String name) {
		return context.getResources().getIdentifier(name, "style",
				context.getPackageName());

	}

	public static int getResStr(Context context, String name) {
		return context.getResources().getIdentifier(name, "string",
				context.getPackageName());

	}

	public static int getResCol(Context context, String name) {
		return context.getResources().getIdentifier(name, "color",
				context.getPackageName());

	}
	public static int getResAnm(Context context, String name) {
		return context.getResources().getIdentifier(name, "anim",
				context.getPackageName());

	}

	/**
	 * 
	 * @param tag
	 * @param isTest
	 * @param mes
	 */
	public static void out(String tag, int isTest, String mes) {
		if (isTest == 0) {
			Log.i(tag, mes);
		}
	}

	/**
	 * *号替换数字
	 * 
	 * @param org
	 * @param n
	 * @return
	 */
	public static String replaceString(String org, int n) {

		char[] cs = org.toCharArray();

		int len = org.length();

		for (int i = 0; i < n; i++)

		{
			cs[len - i - 5] = '*';

		}
		return String.valueOf(cs);

	}

	/**
	 * 判断是手机设备（手机/平板）
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isTabletDevice(Context context) {
		TelephonyManager telephony = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		int type = telephony.getPhoneType();
		if (type == TelephonyManager.PHONE_TYPE_NONE) {
			// Log.i("is Tablet!");
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 
	 * 判断miui5以上系统
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isMiui6(Context context) {
		String m = getSystemProperty("ro.miui.ui.version.name");
		if (m != null && !m.equals("V1") && !m.equals("V2") && !m.equals("V3")
				&& !m.equals("V4") && !m.equals("V5")) {
			return true;

		} else {
			return false;
		}

	}

	private static String getSystemProperty(String propName) {
		String line;
		BufferedReader input = null;
		try {
			Process p = Runtime.getRuntime().exec("getprop " + propName);
			input = new BufferedReader(
					new InputStreamReader(p.getInputStream()), 1024);
			line = input.readLine();
			input.close();
		} catch (IOException ex) {
			Log.e("wan3456",
					"Helper:Unable to read sysprop >>>>>>>" + propName, ex);
			return null;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					Log.e("wan3456",
							"Helper:Exception while closing InputStream>>>>>>",
							e);
				}
			}
		}
		return line;
	}

	/**
	 * 获取外网ip
	 * 
	 * @param context
	 * @return
	 */
	public static String GetNetIp(Context context) {

		{
			String IP = "";
			try {
				String address = "http://ip.taobao.com/service/getIpInfo2.php?ip=myip";
				URL url = new URL(address);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setUseCaches(false);

				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					InputStream in = connection.getInputStream();

					// 将流转化为字符串
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));

					String tmpString = "";
					StringBuilder retJSON = new StringBuilder();
					while ((tmpString = reader.readLine()) != null) {
						retJSON.append(tmpString + "\n");
					}

					JSONObject jsonObject = new JSONObject(retJSON.toString());
					String code = jsonObject.getString("code");
					if (code.equals("0")) {
						JSONObject data = jsonObject.getJSONObject("data");
						IP = data.getString("ip");

						Log.e("提示", "您的IP地址是：" + IP);
					} else {
						IP = getIP(context);
						Log.e("提示", "IP接口异常，无法获取IP地址！");
					}
				} else {
					IP = getIP(context);
					Log.e("提示", "网络连接异常，无法获取IP地址！");
				}
			} catch (Exception e) {
				IP = getIP(context);
				Log.e("提示", "获取IP地址时出现异常，异常信息是：" + e.toString());
			}
			return IP;
		}
	}

	/**
	 * 获取内网ip(wifi/gprs)
	 * 
	 * @param context
	 * @return
	 */
	public static String getIP(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		State gprs = connectivity.getNetworkInfo(
				ConnectivityManager.TYPE_MOBILE).getState();
		State wifi = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		if (gprs == State.CONNECTED || gprs == State.CONNECTING) {
			String ip = getGprsIP(context);
			return ip;
		}

		if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
			String ip = getWifiIP(context);
			return ip;
		}
		return null;

	}

	/**
	 * 获取wifi ip
	 * 
	 * @param context
	 * @return
	 */
	public static String getGprsIP(Context context) {

		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("WifiPreference IpAddress", ex.toString());
		}
		return null;

	}

	/**
	 * 获取gprs IP
	 * 
	 * @param context
	 * @return
	 */
	private static String getWifiIP(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);

		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = intToIp(ipAddress);
		return ip;
	}

	private static String intToIp(int i) {

		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + (i >> 24 & 0xFF);
	}

	/**
	 * 获取运营商
	 * 
	 * @return
	 */
	public static String getProvider(Context context) {
		String provider = "未知";
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String IMSI = telephonyManager.getSubscriberId();
			Log.v("tag", "getProvider.IMSI:" + IMSI);
			if (IMSI == null) {
				if (TelephonyManager.SIM_STATE_READY == telephonyManager
						.getSimState()) {
					String operator = telephonyManager.getSimOperator();
					Log.v("tag", "getProvider.operator:" + operator);
					if (operator != null) {
						if (operator.equals("46000")
								|| operator.equals("46002")
								|| operator.equals("46007")) {
							provider = "中国移动";
						} else if (operator.equals("46001")) {
							provider = "中国联通";
						} else if (operator.equals("46003")) {
							provider = "中国电信";
						}
					}
				}
			} else {
				if (IMSI.startsWith("46000") || IMSI.startsWith("46002")
						|| IMSI.startsWith("46007")) {
					provider = "中国移动";
				} else if (IMSI.startsWith("46001")) {
					provider = "中国联通";
				} else if (IMSI.startsWith("46003")) {
					provider = "中国电信";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return provider;
	}

	/**
	 * 获取网络类型
	 * 
	 * @return
	 */
	public static String getCurrentNetworkType(Context context) {
		int networkClass = getNetworkClass(context);
		String type = "未知";
		switch (networkClass) {
		case NETWORK_CLASS_UNAVAILABLE:
			type = "无";
			break;
		case NETWORK_CLASS_WIFI:
			type = "Wi-Fi";
			break;
		case NETWORK_CLASS_2_G:
			type = "2G";
			break;
		case NETWORK_CLASS_3_G:
			type = "3G";
			break;
		case NETWORK_CLASS_4_G:
			type = "4G";
			break;
		case NETWORK_CLASS_UNKNOWN:
			type = "未知";
			break;
		}
		return type;
	}

	private static int getNetworkClass(Context context) {
		int networkType = NETWORK_TYPE_UNKNOWN;
		try {
			final NetworkInfo network = ((ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE))
					.getActiveNetworkInfo();
			if (network != null && network.isAvailable()
					&& network.isConnected()) {
				int type = network.getType();
				if (type == ConnectivityManager.TYPE_WIFI) {
					networkType = NETWORK_TYPE_WIFI;
				} else if (type == ConnectivityManager.TYPE_MOBILE) {
					TelephonyManager telephonyManager = (TelephonyManager) context
							.getSystemService(Context.TELEPHONY_SERVICE);
					networkType = telephonyManager.getNetworkType();
				}
			} else {
				networkType = NETWORK_TYPE_UNAVAILABLE;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return getNetworkClassByType(networkType);

	}

	private static int getNetworkClassByType(int networkType) {
		switch (networkType) {
		case NETWORK_TYPE_UNAVAILABLE:
			return NETWORK_CLASS_UNAVAILABLE;
		case NETWORK_TYPE_WIFI:
			return NETWORK_CLASS_WIFI;
		case NETWORK_TYPE_GPRS:
		case NETWORK_TYPE_EDGE:
		case NETWORK_TYPE_CDMA:
		case NETWORK_TYPE_1xRTT:
		case NETWORK_TYPE_IDEN:
			return NETWORK_CLASS_2_G;
		case NETWORK_TYPE_UMTS:
		case NETWORK_TYPE_EVDO_0:
		case NETWORK_TYPE_EVDO_A:
		case NETWORK_TYPE_HSDPA:
		case NETWORK_TYPE_HSUPA:
		case NETWORK_TYPE_HSPA:
		case NETWORK_TYPE_EVDO_B:
		case NETWORK_TYPE_EHRPD:
		case NETWORK_TYPE_HSPAP:
			return NETWORK_CLASS_3_G;
		case NETWORK_TYPE_LTE:
			return NETWORK_CLASS_4_G;
		default:
			return NETWORK_CLASS_UNKNOWN;
		}
	}

	public static String WeatherList2String(
			List<HashMap<String, String>> WeatherList) throws IOException {
		// 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件。
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		// 然后将得到的字符数据装载到ObjectOutputStream
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				byteArrayOutputStream);
		// writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
		objectOutputStream.writeObject(WeatherList);
		// 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
		String WeatherListString = new String(
				Base64.encodeBase64(byteArrayOutputStream.toByteArray()));
		// 关闭objectOutputStream
		objectOutputStream.close();
		return WeatherListString;
	}

	@SuppressWarnings("unchecked")
	public static List<HashMap<String, String>> String2WeatherList(
			String WeatherListString) throws StreamCorruptedException,
			IOException, ClassNotFoundException {
		byte[] mobileBytes = Base64.decodeBase64(WeatherListString.getBytes());
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				mobileBytes);
		ObjectInputStream objectInputStream = new ObjectInputStream(
				byteArrayInputStream);
		List<HashMap<String, String>> WeatherList = (List<HashMap<String, String>>) objectInputStream
				.readObject();
		objectInputStream.close();
		return WeatherList;
	}

	/**
	 * 获取和保存当前屏幕的截图
	 */
	public static void GetandSaveCurrentImage(Activity context) {
		// 构建Bitmap
		WindowManager windowManager = context.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int w = display.getWidth();
		int h = display.getHeight();
		Bitmap Bmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		// 获取屏幕
		View decorview = context.getWindow().getDecorView();
		decorview.setDrawingCacheEnabled(true);
		Bmp = decorview.getDrawingCache();
		// 图片存储路径
		String SavePath = getSDCardPath() + "/Demo/ScreenImages";
		// 保存Bitmap
		try {
			File path = new File(SavePath);
			// 文件
			String filepath = SavePath + "/Screen_1.png";
			File file = new File(filepath);
			if (!path.exists()) {
				path.mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = null;
			fos = new FileOutputStream(file);
			if (null != fos) {
				Bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.flush();
				fos.close();
				ToastTool.showToast(context, "截屏文件已保存至SDCard/ScreenImages/目录下",
						1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取SDCard的目录路径功能
	 * 
	 * @return
	 */
	public static String getSDCardPath() {
		File sdcardDir = null;
		// 判断SDCard是否存在
		boolean sdcardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdcardExist) {
			sdcardDir = Environment.getExternalStorageDirectory();
		}
		return sdcardDir.toString();
	}

	public static void download(Context context, String Url) {

		try {
			DownloadManager dManager = (DownloadManager) context
					.getSystemService(Context.DOWNLOAD_SERVICE);
			Uri uri = Uri.parse(Url);
			Request request = new Request(uri);
			// 设置下载路径和文件名
			request.setDestinationInExternalPublicDir("download",
					Url.substring(Url.lastIndexOf("/") + 1));
			request.setDescription("游戏下载");
			request.setMimeType("application/vnd.android.package-archive");
			// 设置为可见和可管理
			request.setVisibleInDownloadsUi(true);
			long refernece = dManager.enqueue(request);
			// 把当前下载的ID保存起来
			SharedPreferences sharedPreferences = context.getSharedPreferences(
					"yssdk_info", Context.MODE_PRIVATE);
			sharedPreferences.edit().putLong("plato", refernece).commit();
		} catch (Exception e) {
			ToastTool.showToast(context, "下载失败，未知错误！", Toast.LENGTH_SHORT);
		}

	}

	/**
	 * 获取配置信息
	 * 
	 * @param context
	 * @param sharedPreferences
	 * @param screenOrientation
	 */
	public static void getPhoneDev(Context context,
			SharedPreferences sharedPreferences, int screenOrientation) {
		Editor editor = sharedPreferences.edit();// 获取编辑器
		editor.putBoolean("ISLogin", false);
		editor.putInt("isOn", 0);
		editor.putBoolean("islock", false);
		if (!NetTool.isNetworkAvailable(context)) {
			Wan3456.inListener.callback(StatusCode.INIT_FAIL, "网络不可用");
			Wan3456.hasInit = false;
		} else {

			if (screenOrientation != StatusCode.LANDSCAPE
					&& screenOrientation != StatusCode.PORTRAIT) {
				Wan3456.inListener.callback(StatusCode.INIT_FAIL, "屏幕方向参数错误！");
				Wan3456.hasInit = false;
				return;
			} else {
				editor.putInt("sreen", screenOrientation);

			}
			PackageInfo pinfo;
			try {
				pinfo = context.getPackageManager().getPackageInfo(
						context.getPackageName(), 0);
				editor.putString("packName", pinfo.packageName);
			} catch (NameNotFoundException e) {
				Wan3456.inListener.callback(StatusCode.INIT_FAIL, "未知错误！");
				Wan3456.hasInit = false;
				return;
			}

			int id = sharedPreferences.getInt("channelID", -1);
			int gid = sharedPreferences.getInt("gameID", -1);
			if (id == -1 && gid == -1) {
				Log.i("wan3456", "Helper:第一次安装>>>>>>>");
				ApplicationInfo appInfo;
				try {
					appInfo = context.getPackageManager().getApplicationInfo(
							context.getPackageName(),
							PackageManager.GET_META_DATA);
					// id = appInfo.metaData.getInt("YS_CHANNELID", -2);

					id = getConfigId(context, StatusCode.CONFIG_NAME_CHANNELID);

					gid = appInfo.metaData.getInt("YS_APPID", -2);

				} catch (NameNotFoundException e1) {
					Wan3456.inListener.callback(StatusCode.INIT_FAIL, "未知错误！");
					Wan3456.hasInit = false;
					return;
				}

				if (gid <= 0 || id <= 0) {
					Wan3456.inListener
							.callback(StatusCode.INIT_FAIL, "参数配置错误!");
					Wan3456.hasInit = false;
					return;
				}
				editor.putInt("gameID", gid);
				editor.putInt("channelID", id);

				Log.i("wan3456",
						"Helper:初始化信息>>>>>" + "主渠道ID:" + String.valueOf(id)
								+ "游戏ID:" + String.valueOf(gid));
			} else {

				Log.i("wan3456", "Helper:非第一次进入游戏>>>>>>>>>>");

				Log.i("wan3456",
						"Helper:初始化信息>>>>>" + "主渠道ID:" + String.valueOf(id)
								+ "游戏ID:" + String.valueOf(gid));

			}
			editor.putBoolean("IsV6", Helper.isMiui6(context));

			TelephonyManager TelephonyMgr = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			WifiManager wifi = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo infos = wifi.getConnectionInfo();

			editor.putString("Brand", Build.MANUFACTURER);
			editor.putString("Modle", Build.MODEL);
			editor.putString("Version", Build.VERSION.RELEASE);
			editor.putInt("SDK_INIT", Build.VERSION.SDK_INT);

			if (Helper.isTabletDevice(context) == true) {
				editor.putString("IMEI", infos.getMacAddress());
				editor.putString("MAC", infos.getMacAddress());
			} else {
				editor.putString("IMEI", TelephonyMgr.getDeviceId());
				editor.putString("MAC", infos.getMacAddress());
				Log.i("wan3456", "MAC:" + infos.getMacAddress());
			}
			if (TelephonyMgr.getDeviceId() == null
					|| TelephonyMgr.getDeviceId().equals("0")
					|| TelephonyMgr.getDeviceId().equals("")) {
				editor.putString("IMEI", infos.getMacAddress());
			}

			String netType = Helper.getCurrentNetworkType(context);

			if (netType.equals("2G") || netType.equals("3G")
					|| netType.equals("4G")) {
				editor.putString("NETTYPE", Helper.getProvider(context)
						+ netType);
				Log.i("wan3456", Helper.getProvider(context) + netType);
			} else {
				editor.putString("NETTYPE", netType);
				Log.i("wan3456", netType);
			}
			InterTool.getInstance(context).doInit(context);

		}
		editor.putBoolean("shake", false);
		editor.commit();
	}

	/**
	 * 获取META-INF中的配置文件信息
	 * 
	 * @param context
	 * @param tag
	 * @return
	 */
	private static int getConfigId(Context context, String tag) {
		if (MetaConfig.instance(context).getJson() == null) {
			return 1;
		}
		if (!DesTool.checkMd5(MetaConfig.instance(context).getJson())) {
			return 0;
		}

		return Integer.parseInt(MetaConfig.instance(context).getString(tag));
	}

	public static void openShakeFloat(Context context,
			SharedPreferences sharedPreferences, Vibrator vibrator) {
		Editor edit = sharedPreferences.edit();
		edit.putBoolean("shake", false);
		edit.commit();
		vibrator.vibrate(200);
		Wan3456.getInstance(context).show();
		showToast(context, "3456玩悬浮窗已打开！");
	}

	public static void showToast(Context context, String mes) {
		View toastRoot = LayoutInflater.from(context).inflate(
				Helper.getLayoutId(context, "wan3456_toast_view"), null);
		Toast toast = new Toast(context);
		toast.setView(toastRoot);
		toast.setGravity(Gravity.CENTER, 0, 0);

		TextView text = (TextView) toastRoot.findViewById(Helper.getResId(
				context, "wan3456_toast_mes"));
		text.setText(mes);
		toast.setDuration(3000);
		toast.show();
	}

	/**
	 * 是否是客服qq在线时间
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isQQTime(Activity context) {
		ContentResolver cv = context.getBaseContext().getContentResolver();
		String strTimeFormat = android.provider.Settings.System.getString(cv,
				android.provider.Settings.System.TIME_12_24);
		Calendar c = Calendar.getInstance();
		if (strTimeFormat != null && strTimeFormat.equals("24")) {

			Log.i("wan3456",
					"24小时制:" + String.valueOf(c.get(Calendar.HOUR_OF_DAY))
							+ "点");
			return checkQQSerTime(1, 0, c.get(Calendar.HOUR_OF_DAY));

		} else {

			if (c.get(Calendar.AM_PM) == 0) {

				Log.i("wan3456",
						"12小时制上午:" + String.valueOf(c.get(Calendar.HOUR)) + "点");
				return checkQQSerTime(0, 1, c.get(Calendar.HOUR));
			} else {

				Log.i("wan3456",
						"12小时制下午:" + String.valueOf(c.get(Calendar.HOUR)) + "点");
				return checkQQSerTime(0, 2, c.get(Calendar.HOUR));
			}

		}

	}

	/**
	 * 对比客服qq时间
	 * 
	 * @param tORf
	 *            0:12小时制 1:24小时制
	 * @param amOrpm
	 *            0: 其它 1:12小时制上午 2:12小时制下午
	 * @param hour
	 *            当前时间（小时）
	 */
	private static boolean checkQQSerTime(int tORf, int amOrpm, int hour) {
		if (tORf == 1) {
			if (hour >= StatusCode.QQ_SERVICE_TIME_BEGIN_24
					&& hour <= StatusCode.QQ_SERVICE_TIME_END_24) {
				return true;
			} else {
				return false;
			}
		} else {
			if (amOrpm == 1) {
				if (hour >= StatusCode.QQ_SERVICE_TIME_BEGIN_12) {
					return true;
				} else {
					return false;
				}
			}
			if (amOrpm == 2) {

				if (hour <= StatusCode.QQ_SERVICE_TIME_END_12) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}

	}

	/**
	 * 打开qq
	 * 
	 * @param context
	 */
	public static void startQQ(Activity context, String qq,
			SharedPreferences sharedPreferences) {
		try {
			String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + qq;
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
		} catch (Exception e) {

			showNoticeDialog(context, sharedPreferences);
		}
	}

	private static void showNoticeDialog(Activity context,
			SharedPreferences sharedPreferences) {
		final Dialog dialog = new AlertDialog.Builder(context).create();
		dialog.show();
		WindowManager m = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		int w = d.getWidth();
		if (sharedPreferences.getInt("sreen", StatusCode.LANDSCAPE) == StatusCode.PORTRAIT) {
			dialog.getWindow().setLayout(3 * w / 4, LayoutParams.WRAP_CONTENT);
		} else {
			dialog.getWindow().setLayout(2 * w / 5, LayoutParams.WRAP_CONTENT);
		}
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(Helper
				.getLayoutId(context, "wan3456_noqq_notice"));

		TextView mes = (TextView) dialog.findViewById(Helper.getResId(context,
				"wan3456_noqq_mes"));
		TextView close = (TextView) dialog.findViewById(Helper.getResId(
				context, "wan3456_noqq_close"));
		mes.setText("打开qq失败,请确定手机已安装qq软件!客服QQ："
				+ sharedPreferences.getString("qq", "0"));
		close.setClickable(true);

		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dialog.dismiss();

			}
		});
	}
	
	public static Dialog loadingDialog(Context context,String mes) {
		Dialog dialog = new AlertDialog.Builder(context).create();
		dialog.show();
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(Helper.getLayoutId(context,
				"wan3456_progress_loading_view"));
		TextView text = (TextView) dialog.findViewById(Helper.getResId(
				context, "wan3456_loading_mes"));
		text.setText(mes);
		return dialog;

	}

}
