package com.wan3456.sdk.tools;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;




import com.wan3456.sdk.Wan3456;
import com.wan3456.sdk.activity.InfoActivity;
import com.wan3456.sdk.bean.SerInfo;

public class InterTool {
	public String eUrl;
	public String pUrl;
	public String cUrl;
	private InitTask inTask;
	public PassTask pTask;
	public ExsitTask eTask;
	public Context context;
	public SharedPreferences sharedPreferences;
	public static int flag = 0;
	public static InterTool instance = null;
	public static List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	public static List<HashMap<String, String>> slist = new ArrayList<HashMap<String, String>>();
	public static String stringlist;
	InterTool(Context context) {
		this.context = context;
		sharedPreferences = context.getSharedPreferences("yssdk_info",
				Context.MODE_PRIVATE);
	}

	public static InterTool getInstance(Context context) {

		if (instance == null) {
			instance = new InterTool(context);
		}
		return instance;
	}

	public void doInit(Context context) {
		this.context = context;
		inTask = new InitTask();
		inTask.execute("");
	}

	/**
	 * 初始化task
	 * 
	 * @author Administrator
	 *
	 */
	private class InitTask extends AsyncTask<String, Integer, String> {
		String xbody;

		@Override
		protected void onPostExecute(String result) {

			getIPThread();
			Editor editor = sharedPreferences.edit();// 获取编辑器
			JSONObject js;
			JSONObject jo;
			if (xbody != null) {
				try {
					js = new JSONObject(xbody);
					if (js.getInt("status") == 1) {

						jo = js.getJSONObject("data");
						editor.putInt("update", jo.getInt("update"));
						// editor.putInt("update", 1);
						editor.putInt("versionCode", jo.getInt("versionCode"));
						editor.putString("updateUrl", jo.getString("updateUrl"));
						editor.putString("explain",
								jo.getString("updateContent"));
						editor.putString("tjUrl",
								jo.getString("recommendGameUrl"));
						if (Integer.parseInt((sharedPreferences.getString(
								"recommend_update_time", "0"))) < Integer
								.parseInt(jo.getString("recommend_update_time"))) {
							editor.putInt("isred", 1);
						}

						if (Integer.parseInt((sharedPreferences.getString(
								"ac_update_time", "0"))) < Integer.parseInt(jo
								.getString("last_news"))) {
							editor.putInt("isred_ac", 1);
						}

						if (Integer.parseInt((sharedPreferences.getString(
								"peck_update_time", "0"))) < Integer
								.parseInt(jo.getString("last_gift"))) {
							editor.putInt("isred_peck", 1);
						}

						editor.putString("recommend_update_time",
								jo.getString("recommend_update_time"));
						editor.putString("ac_update_time",
								jo.getString("last_news"));
						editor.putString("peck_update_time",
								jo.getString("last_gift"));
						editor.putString("tel", jo.getString("tel"));
						editor.putString("qq", jo.getString("qq"));
						editor.putString("giftUrl", jo.getString("giftUrl"));
						editor.putString("activityUrl",
								jo.getString("activityUrl"));
						editor.putString("bbsUrl", jo.getString("bbsUrl"));
						editor.putString("service_center_url",
								jo.getString("service_center_url"));// 客服中心网页地址
						editor.putString("agreement_url",
								jo.getString("agreement_url"));
						editor.putString("message_center_url",
								jo.getString("message_center_url"));
						editor.putString("last_message_url",
								jo.getString("last_message_url"));
						getAccount(jo.getJSONArray("accounts"), editor);

						editor.commit();
						if (checkUpdate(context)) {

							update(context,
									sharedPreferences.getInt("update", 0));

						} else {

							Wan3456.hasInit = true;
							Wan3456.inListener.callback(
									StatusCode.INIT_SUCCESS, "初始化成功");
						}

					} else {
						Wan3456.hasInit = false;
						Wan3456.inListener.callback(StatusCode.INIT_FAIL,
								js.getString("msg"));
					}
				} catch (JSONException e) {

					Wan3456.hasInit = false;
					editor.commit();
					Wan3456.inListener.callback(StatusCode.INIT_FAIL, "未知错误");

				} catch (Exception e) {

					Wan3456.hasInit = false;
					editor.commit();
					Wan3456.inListener.callback(StatusCode.INIT_FAIL, "未知错误");

				}
			} else {
				Wan3456.hasInit = false;
				editor.commit();
				Wan3456.inListener.callback(StatusCode.INIT_FAIL, "未知错误");
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			String a = "brand=" + sharedPreferences.getString("Brand", null)
					+ "&channelid="
					+ String.valueOf(sharedPreferences.getInt("channelID", 0))
					+ "&gameid="
					+ String.valueOf(sharedPreferences.getInt("gameID", 0))
					+ "&imei=" + sharedPreferences.getString("IMEI", null)
					+ "&mac=" + sharedPreferences.getString("MAC", "")

					+ "&modle=" + sharedPreferences.getString("Modle", null)
					+ "&osversion="
					+ sharedPreferences.getString("Version", null) + "&sdk_vs="
					+ StatusCode.SDK_VERSION + "&time="
					+ LoginCheckVild.getCurrTime().toString() + "&uid="
					+ String.valueOf(sharedPreferences.getInt("uid", 0))
					+ "&web_type="
					+ sharedPreferences.getString("NETTYPE", "未知");
			String sign = DesTool.replaceTool(StatusCode.DES_BEFOR, a);

			String url = StaticVariable.INIT_SDK + sign;

			xbody = NetTool.getUrlContent(url);

			if (xbody != null) {

				xbody = DesTool.replaceTool(StatusCode.DES_AFTER, xbody);
			}

			return xbody;
		}

	}

	/**
	 * 检测是否有更新提示
	 * 
	 * @param context
	 * @return
	 */
	private boolean checkUpdate(Context context) {
		PackageInfo info;
		try {
			info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			int o_version = info.versionCode;
			if (sharedPreferences.getInt("update", 0) != 0
					&& o_version < sharedPreferences.getInt("versionCode", 1)) {
				return true;
			} else {
				return false;
			}
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	/**
	 * 更新提示dialog
	 * 
	 * @param context
	 * @param tag
	 */
	private void update(final Context context, final int tag) {
		final Dialog dialog = new AlertDialog.Builder(context).create();
		dialog.show();
		WindowManager m = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		int w = d.getWidth();
		if (sharedPreferences.getInt("sreen", StatusCode.LANDSCAPE) == StatusCode.PORTRAIT) {
			dialog.getWindow().setLayout(5 * w / 6, LayoutParams.WRAP_CONTENT);
		}
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(Helper
				.getLayoutId(context, "wan3456_view_update"));
		Button sure = (Button) dialog.findViewById(Helper.getResId(context,
				"wan3456_loading_osure"));
		Button cancle = (Button) dialog.findViewById(Helper.getResId(context,
				"wan3456_loading_odimiss"));
		TextView text = (TextView) dialog.findViewById(Helper.getResId(context,
				"wan3456_loading_omes"));
		text.setText(sharedPreferences.getString("explain", "发现游戏新版本，请立即下载!"));
		if (tag == 2) {
			cancle.setVisibility(View.GONE);
			sure.setText("立即更新");
		}
		sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (flag == 0) {
					if (tag == 1) {
						Helper.download(context, sharedPreferences.getString(
								"updateUrl", "http://"));
						ToastTool.showToast(context, "正在后台下载游戏更新包", 2000);
						Wan3456.hasInit = true;
						Wan3456.inListener.callback(StatusCode.INIT_SUCCESS,
								"初始化成功");

						dialog.dismiss();
					} else {
						flag = 1;
						((TextView) v).setTextSize(14);
						((TextView) v).setText("关闭游戏(请安装新包后进入游戏)");

						Helper.download(context, sharedPreferences.getString(
								"updateUrl", "http://"));
						ToastTool.showToast(context, "正在后台下载游戏更新包", 2000);
					}

				} else {
					Wan3456.hasInit = false;
					Wan3456.inListener.callback(StatusCode.INIT_FAIL,
							"正在下载强制更新包");
					// 关闭游戏
					dialog.dismiss();
					// android.os.Process.killProcess(android.os.Process.myPid());
					System.exit(0);
				}

			}
		});
		cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Wan3456.hasInit = true;
				Wan3456.inListener.callback(StatusCode.INIT_SUCCESS, "初始化成功");

				dialog.dismiss();

			}
		});

	}

	/**
	 * 获取后台返回的该机已注册帐号列表
	 * 
	 * @param jsonArray
	 * @param editor
	 */
	private void getAccount(JSONArray jsonArray, Editor editor) {
		slist.clear();
		list.clear();
		stringlist = sharedPreferences.getString("userlist", null);
		if (stringlist != null) {
			try {
				list = Helper.String2WeatherList(stringlist);
			} catch (StreamCorruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (!sharedPreferences.getString("name", "").equals("")
				&& list.size() == 0) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("username", sharedPreferences.getString("name", ""));
			map.put("userpass", sharedPreferences.getString("password", ""));
			list.add(0, map);
		}

		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				if (checkUser(jsonArray.getString(i)) == false) {

					HashMap<String, String> map = new HashMap<String, String>();
					map.put("username", jsonArray.getString(i));
					map.put("userpass", "");
					slist.add(map);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		list.addAll(slist);

		String a;
		try {
			a = Helper.WeatherList2String(list);
			editor.putString("userlist", a);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 检测返回用户的列表与本地列表排重
	 * 
	 * @param name
	 * @return
	 */
	private boolean checkUser(String name) {
		for (HashMap<String, String> map : list) {
			if (map.get("username").equals(name)) {

				return true;
			}
		}

		return false;
	}

	/**
	 * 选服传参
	 * 
	 * @param info
	 */
	public void passSer(SerInfo info) {
		Editor editor = sharedPreferences.edit();
		editor.putInt("serId", info.getServerId());
		editor.putString("serName", info.getServerName());
		editor.putString("gRole", info.getGameRole());
		editor.putInt("roleLevel", info.getRoleLevel());
		editor.commit();
		String a = "channelid="
				+ String.valueOf(sharedPreferences.getInt("channelID", 0))
				+ "&gameid="
				+ String.valueOf(sharedPreferences.getInt("gameID", 0))
				+ "&gamerole=" + info.getGameRole() + "&imei="
				+ sharedPreferences.getString("IMEI", null) + "&rolelevel="
				+ String.valueOf(info.getRoleLevel()) + "&sdk_vs="
				+ StatusCode.SDK_VERSION + "&serverid="
				+ String.valueOf(info.getServerId()) + "&servername"
				+ info.getServerName() + "&time="
				+ LoginCheckVild.getCurrTime().toString() + "&username="
				+ info.getUid();

		String sign = DesTool.replaceTool(StatusCode.DES_BEFOR, a);
		pUrl = StaticVariable.PASS_SER + sign;
		pTask = new PassTask();
		pTask.execute("");

	}

	/**
	 * 退出dialog
	 * 
	 * @param context
	 */
	public void showExitDialog(final Context context) {
		this.context = context;
		final Dialog dialog = new AlertDialog.Builder(context).create();
		dialog.show();
		dialog.setCancelable(true);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.alpha = 0.8f;
		window.setAttributes(lp);
		WindowManager m = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		int w = d.getWidth();
		if (sharedPreferences.getInt("sreen", StatusCode.LANDSCAPE) == StatusCode.PORTRAIT) {
			dialog.getWindow().setLayout(5 * w / 6, LayoutParams.WRAP_CONTENT);
		} else {
			dialog.getWindow().setLayout(7 * w / 15, LayoutParams.WRAP_CONTENT);
		}

		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(Helper
				.getLayoutId(context, "wan3456_dialog_exit"));
		Button exit = (Button) dialog.findViewById(Helper.getResId(context,
				"wan3456_exit_exit"));
		Button ct = (Button) dialog.findViewById(Helper.getResId(context,
				"wan3456_exit_continue"));
		RelativeLayout e_ac = (RelativeLayout) dialog.findViewById(Helper
				.getResId(context, "wan3456_exit_activity"));
		RelativeLayout e_gift = (RelativeLayout) dialog.findViewById(Helper
				.getResId(context, "wan3456_exit_gift"));
		e_gift.setClickable(true);
		e_ac.setClickable(true);
		dialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {

				Wan3456.exitListener.callback(StatusCode.CONTINUE_GAME,
						"continue game");
			}
		});
		e_gift.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sharedPreferences.getString("giftUrl", "").equals("")) {
					ToastTool.showToast(context, "暂无礼包！", 1000);
				} else {
					InfoActivity.show(context, InfoActivity.INDEX_GIFT);
					Wan3456.exitListener.callback(StatusCode.CONTINUE_GAME,
							"continue game");
					dialog.dismiss();
				}
			}
		});
		e_ac.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InfoActivity.show(context, InfoActivity.INDEX_TJ);
				Wan3456.exitListener.callback(StatusCode.CONTINUE_GAME,
						"continue game");
				dialog.dismiss();

			}
		});

		exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				closeFloatView(context);
				Editor editor = sharedPreferences.edit();
				editor.putBoolean("ISLogin", false);
				editor.commit();
				dialog.dismiss();
			}
		});
		ct.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Wan3456.exitListener.callback(StatusCode.CONTINUE_GAME,
						"continue game");
				dialog.dismiss();
			}
		});

	}

	/**
	 * 关闭悬浮窗
	 * 
	 * @param context
	 * @param sId
	 */

	private void closeFloatView(Context context) {

			Editor editor = sharedPreferences.edit();
			editor.putBoolean("ISLogin", false);
			editor.commit();
		Wan3456.getInstance(context).close();
		Wan3456.exitListener.callback(StatusCode.EXIT_GAME, "exit game");

	}

	/**
	 * 查询订单 线程
	 * 
	 * @param orderId
	 */
	public void check(final String orderId) {

		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					Thread.sleep(5 * 1000);

				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				String a = "channelid="
						+ String.valueOf(sharedPreferences.getInt("channelID",
								0)) + "&gameid="
						+ String.valueOf(sharedPreferences.getInt("gameID", 0))
						+ "&orderid=" + orderId + "&sdk_vs="
						+ StatusCode.SDK_VERSION + "&time="
						+ LoginCheckVild.getCurrTime().toString();
				String sign = DesTool.replaceTool(StatusCode.DES_BEFOR, a);

				cUrl = StaticVariable.CHECK_PAY + sign;

				String xbody = NetTool.getUrlContent(cUrl);
				if (xbody != null) {
					xbody = DesTool.replaceTool(StatusCode.DES_AFTER, xbody);
				}

				JSONObject jo;
				JSONObject jos;
				if (xbody != null) {
					try {

						jo = new JSONObject(xbody);
						jos = jo.getJSONObject("data");
						if (jo.getInt("status") == 1) {
							if (jos.getBoolean("completed") == false) {
								Wan3456.checkListener
										.callback(StatusCode.PAY_STATIC_FAIL);
							} else {
								Wan3456.checkListener
										.callback(StatusCode.PAY_STATIC_SUCCESS);
							}

						} else {
							Wan3456.checkListener.callback(StatusCode.ERROR);
						}
					} catch (JSONException e) {
						Wan3456.checkListener.callback(StatusCode.ERROR);
					}
				} else {
					Wan3456.checkListener.callback(StatusCode.ERROR);
				}

			}
		}).start();

	}

	/**
	 * 选服传参 task
	 * 
	 * @author Administrator
	 *
	 */
	private class PassTask extends AsyncTask<String, Integer, String> {
		String body;

		@Override
		protected void onPostExecute(String result) {
			if (body != null) {
				JSONObject jobj;
				try {
					jobj = new JSONObject(body);

					if (jobj.getInt("status") == 1) {

						Wan3456.serListener.callback(StatusCode.PASS_SUCCESS);

					} else {
						Wan3456.serListener.callback(StatusCode.PASS_FAIL);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Wan3456.serListener.callback(StatusCode.PASS_FAIL);
				}
			} else {
				Wan3456.serListener.callback(StatusCode.PASS_FAIL);
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {

			body = NetTool.getUrlContent(pUrl);
			if (body != null) {
				body = DesTool.replaceTool(StatusCode.DES_AFTER, body);
			}

			return body;
		}

	}

	/**
	 * 退出task
	 * 
	 * @author Administrator
	 *
	 */
	private class ExsitTask extends AsyncTask<String, Integer, String> {
		String body;

		@Override
		protected void onPostExecute(String result) {
			if (body != null) {
				JSONObject jobj;
				try {
					jobj = new JSONObject(body);
					int pass_state = jobj.getInt("status");
					if (pass_state == 1) {
						Wan3456.exitListener.callback(StatusCode.EXIT_GAME,
								"exit game");
					} else {

						Wan3456.exitListener.callback(StatusCode.EXIT_GAME,
								"exit game");
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Wan3456.exitListener.callback(StatusCode.EXIT_GAME,
							"exit game");

				}

			} else {

				Wan3456.exitListener
						.callback(StatusCode.EXIT_GAME, "exit game");
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {

			String username = sharedPreferences.getString("name", "");
			String a = "channelid="
					+ String.valueOf(sharedPreferences.getInt("channelID", 0))
					+ "&gameid="
					+ String.valueOf(sharedPreferences.getInt("gameID", 0))
					+ "&sdk_vs=" + StatusCode.SDK_VERSION + "&time="
					+ LoginCheckVild.getCurrTime().toString() + "&username="
					+ username;
			String sign = DesTool.replaceTool(StatusCode.DES_BEFOR, a);

			eUrl = StaticVariable.SDK_EXSIT + sign;
			body = NetTool.getUrlContent(eUrl);
			if (body != null) {
				body = DesTool.replaceTool(StatusCode.DES_AFTER, body);
			}
			return body;
		}

	}

	/**
	 * 复制 方法
	 * 
	 * @param code
	 */
	public void copy(Context context, String code) {
		this.context = context;
		// TODO Auto-generated method stub
		ClipboardManager cmb = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(code.trim());
		ToastTool.showToast(context, "邀请码已复制至粘贴板", 2500);
	}

	/**
	 * 获取客户端网络IP
	 */
	private void getIPThread() {
		// TODO Auto-generated method stub
		Thread a = new Thread(new Runnable() {
			@Override
			public void run() {
				Editor editor = sharedPreferences.edit();// 获取编辑器
				String IP = Helper.GetNetIp(context);
				editor.putString("IP", IP);
				Log.i("wan3456", "IP>>>>>>>>" + IP);
				editor.commit();
			}
		});
		a.start();

	}
}
