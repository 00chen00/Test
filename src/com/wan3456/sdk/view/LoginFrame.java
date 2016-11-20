package com.wan3456.sdk.view;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.wan3456.sdk.Wan3456;
import com.wan3456.sdk.activity.LoginActivity;
import com.wan3456.sdk.bean.AccountListAdapter;
import com.wan3456.sdk.tools.DesTool;
import com.wan3456.sdk.tools.Helper;
import com.wan3456.sdk.tools.LoginCheckVild;
import com.wan3456.sdk.tools.NetTool;
import com.wan3456.sdk.tools.StaticVariable;
import com.wan3456.sdk.tools.StatusCode;
import com.wan3456.sdk.tools.ToastTool;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;

@SuppressLint("HandlerLeak")
public class LoginFrame extends Fragment implements OnClickListener {
	private static SharedPreferences sharedPreferences;
	private LinearLayout lin;
	private Dialog dialog;
	// login view
	private Button login_btn;
	private Button toregist_btn;
	private Button remember_btn;
	private static EditText edit_name;
	private static EditText edit_pwd;
	private TextView to_find_txt;
	private LoginTask lTask;
	private String lUrl;
	private boolean see = false;
	private boolean more = true;
	private ImageButton see_btn;
	private ImageButton more_btn;
	private PopupWindow popupWindow;
	private String user_name;
	private String user_pwd;
	private Dialog bindDialog;
	// private Handler handler;
	private Handler bHandler;
	private List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	private String bUrl;
	private BangTask bTask;
	private String bcUrl;
	private BindCheckTask bcTask;
	private EditText phone_num;
	private EditText phone_check_num;
	private RelativeLayout name_line;
	private RelativeLayout pwd_line;

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (!hidden) {
			Log.i("LoginFrame", "========onHiddenChanged()========");
			name_line
					.setBackgroundDrawable(getActivity().getResources()
							.getDrawable(
									Helper.getResDraw(getActivity(),
											"wan3456_edit_bg")));
			pwd_line.setBackgroundDrawable(getActivity()
					.getResources()
					.getDrawable(
							Helper.getResDraw(getActivity(), "wan3456_edit_bg")));

		}
	}

	public static void findAccount() {
		edit_name.setText(sharedPreferences.getString("name", ""));
		edit_pwd.setText(sharedPreferences.getString("password", ""));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(
				Helper.getLayoutId(getActivity(), "wan3456_view_login"),

				null, false);
		sharedPreferences = getActivity().getSharedPreferences("yssdk_info",
				Context.MODE_PRIVATE);
		lin = (LinearLayout) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_login_manual_line"));
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
		initView(v);
		return v;
	}

	private void initView(View v) {
		name_line = (RelativeLayout) v.findViewById(Helper.getResId(
				getActivity(), "wan3456_login_manual_name_line"));
		pwd_line = (RelativeLayout) v.findViewById(Helper.getResId(
				getActivity(), "wan3456_login_manual_pwd_line"));
		edit_name = (EditText) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_login_account"));
		edit_pwd = (EditText) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_login_pass"));
		remember_btn = (Button) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_login_remember"));
		login_btn = (Button) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_login_login"));
		toregist_btn = (Button) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_login_regist"));
		to_find_txt = (TextView) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_login_findpass"));
		see_btn = (ImageButton) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_login_pass_see"));
		more_btn = (ImageButton) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_login_account_more"));

		to_find_txt.setClickable(true);
		to_find_txt.setOnClickListener(this);
		remember_btn.setOnClickListener(this);
		toregist_btn.setOnClickListener(this);
		see_btn.setOnClickListener(this);
		more_btn.setOnClickListener(this);
		login_btn.setOnClickListener(this);

		String stringlist = sharedPreferences.getString("userlist", null);
		if (stringlist != null) {
			try {
				list = Helper.String2WeatherList(stringlist);
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		if (list.size() == 0) {
			more_btn.setVisibility(View.INVISIBLE);
		}

		if (sharedPreferences.getString("name", "").equals("")
				&& list.size() > 0) {
			Editor editor = sharedPreferences.edit();
			editor.putString("name", list.get(0).get("username"));
			editor.putString("password", list.get(0).get("userpass"));
			editor.commit();
		}

		edit_name.setText(sharedPreferences.getString("name", ""));
		edit_name
				.setSelection(sharedPreferences.getString("name", "").length());// 光标定位至末尾
		if (sharedPreferences.getBoolean("remember", true) == false) {
			edit_pwd.setText("");
			remember_btn.setBackgroundResource(Helper.getResDraw(getActivity(),
					"wan3456_re_check"));
		} else {
			edit_pwd.setText(sharedPreferences.getString("password", ""));

			remember_btn.setBackgroundResource(Helper.getResDraw(getActivity(),
					"wan3456_re_checked"));
		}

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == Helper.getResId(getActivity(),
				"wan3456_login_remember")) {
			boolean rem = sharedPreferences.getBoolean("remember", false);
			if (rem == false) {
				remember_btn.setBackgroundResource(Helper.getResDraw(
						getActivity(), "wan3456_re_checked"));
				Editor editor = sharedPreferences.edit();
				editor.putBoolean("remember", true);
				editor.commit();

			} else {
				remember_btn.setBackgroundResource(Helper.getResDraw(
						getActivity(), "wan3456_re_check"));
				Editor editor = sharedPreferences.edit();
				editor.putBoolean("remember", false);
				editor.commit();

			}
			return;
		}
		if (v.getId() == Helper.getResId(getActivity(), "wan3456_login_login")) {
			user_name = edit_name.getText().toString();
			user_pwd = edit_pwd.getText().toString();
			if (LoginCheckVild.checkValid(user_name, StatusCode.CHECK_NAME) == false) {
				ToastTool.showToast(getActivity(),
						"用户名格式错误,正确格式:3-18位字母数字或下划线！", 1000);
				return;
			}
			if (LoginCheckVild.checkValid(user_pwd, StatusCode.CHECK_PASSWORD) == false) {
				ToastTool.showToast(getActivity(), "密码格式错误,正确格式：6-20位字母数字！",
						1000);
				return;
			} else {
				lTask = new LoginTask();
				lTask.execute("");
			}
			return;
		}
		if (v.getId() == Helper.getResId(getActivity(), "wan3456_login_regist")) {
			// 跳转注册界面
			LoginActivity ac = (LoginActivity) getActivity();
			ac.setCurFragment(LoginActivity.INDEX_REGISTER);
			return;
		}
		if (v.getId() == Helper.getResId(getActivity(),
				"wan3456_login_pass_see")) {
			if (see == false) {
				see_btn.setImageResource(Helper.getResDraw(getActivity(),
						"wan3456_pwd_see"));
				edit_pwd.setTransformationMethod(HideReturnsTransformationMethod
						.getInstance());
				see = true;
			} else {
				see_btn.setImageResource(Helper.getResDraw(getActivity(),
						"wan3456_pwd_nosee"));
				edit_pwd.setTransformationMethod(PasswordTransformationMethod
						.getInstance());
				see = false;
			}
			return;
		}
		if (v.getId() == Helper.getResId(getActivity(),
				"wan3456_login_account_more")) {
			if (more == true) {
				more_btn.setImageResource(Helper.getResDraw(getActivity(),
						"wan3456_more_pressed"));
				more = false;
			} else {
				more_btn.setImageResource(Helper.getResDraw(getActivity(),
						"wan3456_more_press"));
				more = true;
			}
			getPopupWindow();
			popupWindow.showAsDropDown(edit_name, -20, 1);
			return;
		}
		if (v.getId() == Helper.getResId(getActivity(),
				"wan3456_login_findpass")) {
			// 跳转找回帐号界面
			LoginActivity ac = (LoginActivity) getActivity();
			ac.setCurFragment(LoginActivity.INDEX_FIND_ACCOUNT);
			return;
		}

	}

	protected void getPopupWindow() {
		// TODO Auto-generated method stub
		if (null != popupWindow) {
			popupWindow.dismiss();
			initPopupWindow();
			return;
		} else {
			initPopupWindow();
		}
	}

	private void initPopupWindow() {
		View popupWindow_view = getActivity().getLayoutInflater().inflate(
				Helper.getLayoutId(getActivity(), "wan3456_pop_login_more"),
				null, false);

		popupWindow_view.setBackgroundResource(Helper.getResDraw(getActivity(),
				"wan3456_pop_bg1"));
		int screenHeight = getActivity().getWindowManager().getDefaultDisplay()
				.getHeight();
		if (sharedPreferences.getInt("sreen", StatusCode.LANDSCAPE) == StatusCode.PORTRAIT) {
			popupWindow = new PopupWindow(popupWindow_view,
					edit_name.getWidth() + 20, screenHeight * 3 / 9, true);
		} else {
			popupWindow = new PopupWindow(popupWindow_view,
					edit_name.getWidth() + 20, screenHeight * 4 / 9, true);
		}
		ColorDrawable dw = new ColorDrawable(0x00);
		popupWindow.setBackgroundDrawable(dw);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);

		final ListView listView = (ListView) popupWindow_view
				.findViewById(Helper.getResId(getActivity(),
						"wan3456_account_listview"));
		AccountListAdapter uadapter = new AccountListAdapter(getActivity(),
				list);
		listView.setAdapter(uadapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				HashMap<String, String> value = (HashMap<String, String>) listView
						.getItemAtPosition(arg2);
				edit_name.setText(value.get("username"));
				edit_pwd.setText(value.get("userpass"));

				popupWindow.dismiss();
				more_btn.setImageResource(Helper.getResDraw(getActivity(),
						"wan3456_more_press"));
				more = true;

			}
		});
		popupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				more_btn.setImageResource(Helper.getResDraw(getActivity(),
						"wan3456_more_press"));
				more = true;
			}
		});

	}

	/**
	 * 登陆task
	 */
	private class LoginTask extends AsyncTask<String, Integer, String> {
		String body;

		@Override
		protected void onPostExecute(String result) {
			if (body != null) {

				JSONObject jobj;
				JSONObject jo;
				try {
					jobj = new JSONObject(body);
					int login_state = jobj.getInt("status");
					String msg = jobj.getString("msg");
					if (login_state == 1) {
						jo = jobj.getJSONObject("data");
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("username", user_name);
						map.put("userpass", user_pwd);
						if (list.size() > 0) {
							for (int i = 0; i < list.size(); i++) {
								HashMap<String, String> map1 = list.get(i);
								if (user_name.equals(map1.get("username"))) {
									list.remove(i);
								}
							}

						}
						list.add(0, map);

						Editor editor = sharedPreferences.edit();// 获取编辑器
						editor.putString("name", user_name);
						editor.putString("password", user_pwd);
						editor.putInt("uid", jo.getInt("userid"));
						editor.putBoolean("hasphone", jo.getBoolean("hasBind"));
						editor.putString("phone", jo.getString("phone"));
						editor.putBoolean("hasCount", true);
						editor.putBoolean("ISLogin", true);
						editor.putString("bind_qq", jo.getString("qq"));
						if (sharedPreferences.getInt("message_update_time", 0) != jo
								.getInt("last_message")) {
							editor.putInt("isred_mes", 1);
						}
						editor.putInt("message_update_time",
								jo.getInt("last_message"));

						if ((sharedPreferences.getInt("service_update_time", 0)) < jo
								.getInt("last_service_message")) {
							editor.putInt("isred_kf", 1);
						}
						editor.putInt("service_update_time",
								jo.getInt("last_service_message"));
						if (jo.getInt("is_pay_user") == 0) {
							editor.putBoolean("vip", false);
						} else {
							editor.putBoolean("vip", true);
						}
						String a;
						try {
							a = Helper.WeatherList2String(list);
							editor.putString("userlist", a);

						} catch (IOException e) {

							e.printStackTrace();
						}

						editor.commit();
						dialog.cancel();
						String sign = jo.getString("sign");
						if (sharedPreferences.getInt("isF", 0) == 1
								&& jo.getBoolean("hasBind") == false) {
							// 跳到绑定手机页面
							bindDialog(jo.getInt("userid"),
									jo.getInt("tstamp"), sign, user_name);
							Editor editor1 = sharedPreferences.edit();// 获取编辑器
							editor1.putInt("isF", 0);
							editor1.commit();
						} else {
							if (sharedPreferences.getBoolean("vip", false)
									&& !jo.getBoolean("hasBind")) {
								// 跳到绑定手机页面
								bindDialog(jo.getInt("userid"),
										jo.getInt("tstamp"), sign, user_name);

							} else {
								if (Wan3456.userListener != null) {
									Wan3456.userListener.onLoginSuccess(
											jo.getInt("userid"),
											jo.getInt("tstamp"), sign);
									getActivity().finish();
									Wan3456.getInstance(getActivity()).show();
								} else {
									ToastTool.showToast(getActivity(),
											"程序发生未知错误，请退出游戏重新登录！", 3000);
								}

							}

						}

					} else {
						dialog.cancel();
						ToastTool.showToast(getActivity(), msg, 1000);

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block

					dialog.cancel();

					e.printStackTrace();
				}

			} else {
				dialog.cancel();
				ToastTool.showToast(getActivity(), "网络异常,请稍后再试！", 1000);
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			dialog = Helper.loadingDialog(getActivity(), "正在登录...");
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
					+ sharedPreferences.getString("Version", null)
					+ "&password=" + user_pwd + "&sdk_vs="
					+ StatusCode.SDK_VERSION + "&time="
					+ LoginCheckVild.getCurrTime().toString() + "&username="
					+ user_name + "&web_type="
					+ sharedPreferences.getString("NETTYPE", "未知");
			String sign = DesTool.replaceTool(StatusCode.DES_BEFOR, a);
			lUrl = StaticVariable.LOGIN_URL + sign;
			body = NetTool.getUrlContent(lUrl);
			if (body != null) {
				body = DesTool.replaceTool(StatusCode.DES_AFTER, body);
			}
			return body;
		}

	}

	/**
	 * 绑定手机dialog
	 * 
	 * @param int1
	 * @param int2
	 * @param string
	 * @param name
	 */
	public void bindDialog(final int uid, final int tstmap, final String sign,
			String name) {
		bindDialog = new Dialog(getActivity(), Helper.getResStyle(
				getActivity(), "wan3456_Detail_dialog"));
		bindDialog.show();
		bindDialog.setCancelable(true);
		bindDialog.setCanceledOnTouchOutside(false);
		bindDialog.setContentView(Helper.getLayoutId(getActivity(),
				"wan3456_login_bind_view"));
		Button bang_btn = (Button) bindDialog.findViewById(Helper.getResId(
				getActivity(), "wan3456_login_bindphone_submit"));
		Button cancle_btn = (Button) bindDialog.findViewById(Helper.getResId(
				getActivity(), "wan3456_login_bindphone_back"));
		TextView name_text = (TextView) bindDialog.findViewById(Helper
				.getResId(getActivity(), "wan3456_login_bindphone_account"));
		final Button btn_get_check = (Button) bindDialog.findViewById(Helper
				.getResId(getActivity(), "wan3456_login_bindphone_getcode"));
		phone_num = (EditText) bindDialog.findViewById(Helper.getResId(
				getActivity(), "wan3456_login_bindphone_phone"));
		phone_check_num = (EditText) bindDialog.findViewById(Helper.getResId(
				getActivity(), "wan3456_login_bindphone_code"));
		name_text.setText("您的帐号是:" + name);
		bHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:
					int time = (Integer) msg.obj;
					String a = String.valueOf(time);
					String mes = "重新获取" + "(" + a + ")";
					btn_get_check.setText(mes);
					if (a.equals("0")) {
						btn_get_check.setEnabled(true);
						phone_num.setEnabled(true);
						btn_get_check.setText("获取验证码");
					}
					break;
				case 2:
					phone_num.setEnabled(false);
					btn_get_check.setEnabled(false);

					new Thread(new Runnable() {

						@Override
						public void run() {
							for (int i = 0; i <= 60; i++) {
								Message mes = new Message();
								mes.what = 1;
								mes.obj = 60 - i;
								bHandler.sendMessage(mes);
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					}).start();
					break;

				}
			}

		};

		btn_get_check.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (LoginCheckVild.isMobileNO(phone_num.getText().toString())) {
					bcTask = new BindCheckTask();
					bcTask.execute("");
				} else {
					ToastTool.showToast(getActivity(), "电话号码格式错误!", 1000);

				}

			}
		});

		bang_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (LoginCheckVild.isMobileNO(phone_num.getText().toString()) == false) {
					ToastTool.showToast(getActivity(), "电话号码格式错误!", 1000);
					return;
				}
				if (LoginCheckVild.isCheckNO(phone_check_num.getText()
						.toString()) == false) {
					ToastTool.showToast(getActivity(), "验证码格式错误!", 1000);
					return;
				} else {
					InputMethodManager imm = (InputMethodManager) getActivity()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(
							phone_check_num.getWindowToken(), 0);
					bTask = new BangTask(uid, tstmap, sign);
					bTask.execute("");

				}

			}
		});

		cancle_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				bindDialog.dismiss();
				if (Wan3456.userListener != null) {
					Wan3456.userListener.onLoginSuccess(uid, tstmap, sign);
					getActivity().finish();
					Wan3456.getInstance(getActivity()).show();
				} else {
					ToastTool.showToast(getActivity(), "程序发生未知错误，请退出游戏重新登录！",
							3000);
				}

			}
		});
		bindDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				bindDialog.dismiss();
				if (Wan3456.userListener != null) {
					Wan3456.userListener.onLoginSuccess(uid, tstmap, sign);
					getActivity().finish();
					Wan3456.getInstance(getActivity()).show();
				} else {
					ToastTool.showToast(getActivity(), "程序发生未知错误，请退出游戏重新登录！",
							3000);
				}
			}
		});

	}

	/**
	 * 绑定手机task
	 * 
	 */
	private class BangTask extends AsyncTask<String, Integer, String> {
		String body;
		String sign;
		int uid;
		int tstmap;

		public BangTask(int uid, int tstmap, String sign) {
			this.uid = uid;
			this.tstmap = tstmap;
			this.sign = sign;
		}

		@Override
		protected String doInBackground(String... params) {
			String a = "channelid="
					+ String.valueOf(sharedPreferences.getInt("channelID", 0))
					+ "&code=" + phone_check_num.getText().toString()
					+ "&gameid="
					+ String.valueOf(sharedPreferences.getInt("gameID", 0))
					+ "&imei=" + sharedPreferences.getString("IMEI", null)
					+ "&mobile=" + phone_num.getText().toString() + "&sdk_vs="
					+ StatusCode.SDK_VERSION + "&time="
					+ LoginCheckVild.getCurrTime().toString() + "&username="
					+ sharedPreferences.getString("name", "");
			String sign = DesTool.replaceTool(StatusCode.DES_BEFOR, a);
			bUrl = StaticVariable.BANG_PHONE + sign;
			body = NetTool.getUrlContent(bUrl);
			if (body != null) {
				body = DesTool.replaceTool(StatusCode.DES_AFTER, body);
			}
			return body;
		}

		@Override
		protected void onPostExecute(String result) {
			if (body != null) {
				JSONObject jo;
				try {
					jo = new JSONObject(body);
					if (jo.getInt("status") == 1) {
						Editor editor = sharedPreferences.edit();// 获取编辑器
						editor.putBoolean("hasphone", true);
						editor.putString("phone", phone_num.getText()
								.toString());
						editor.commit();
						dialog.cancel();
						ToastTool
								.showToast(getActivity(), "绑定手机成功,进入游戏!", 1000);
						bindDialog.dismiss();
						if (Wan3456.userListener != null) {
							Wan3456.userListener.onLoginSuccess(uid, tstmap,
									sign);
							getActivity().finish();
							Wan3456.getInstance(getActivity()).show();
						} else {
							ToastTool.showToast(getActivity(),
									"程序发生未知错误，请退出游戏重新登录！", 3000);
						}

					} else {
						dialog.cancel();
						ToastTool.showToast(getActivity(), jo.getString("msg"),
								1000);

					}
				} catch (JSONException e) {
					dialog.cancel();
					ToastTool.showToast(getActivity(), "服务器数据错误!", 1000);
				}

			} else {
				dialog.cancel();
				ToastTool.showToast(getActivity(), "网络连接出错！", 1000);
			}
		}

		@Override
		protected void onPreExecute() {
			dialog = Helper.loadingDialog(getActivity(), "正在验证...");
		}

	}

	/**
	 * /* 获取绑定手机验证码task
	 */
	private class BindCheckTask extends AsyncTask<String, Integer, String> {
		String body;

		@Override
		protected String doInBackground(String... params) {
			String a = "channelid="
					+ String.valueOf(sharedPreferences.getInt("channelID", 0))
					+ "&gameid="
					+ String.valueOf(sharedPreferences.getInt("gameID", 0))
					+ "&imei=" + sharedPreferences.getString("IMEI", null)
					+ "&mobile=" + phone_num.getText().toString() + "&sdk_vs="
					+ StatusCode.SDK_VERSION + "&time="
					+ LoginCheckVild.getCurrTime().toString() + "&type=bind"
					+ "&username=" + sharedPreferences.getString("name", "");
			String sign = DesTool.replaceTool(StatusCode.DES_BEFOR, a);
			bcUrl = StaticVariable.GET_CHECKCODE + sign;
			body = NetTool.getUrlContent(bcUrl);
			if (body != null) {
				body = DesTool.replaceTool(StatusCode.DES_AFTER, body);
			}
			return body;
		}

		@Override
		protected void onPostExecute(String result) {
			if (body != null) {
				JSONObject jo;
				try {
					jo = new JSONObject(body);
					if (jo.getInt("status") == 1) {
						dialog.cancel();
						ToastTool.showToast(getActivity(), "验证码已发送，请注意查收！",
								1000);
						bHandler.sendEmptyMessage(2);
					} else {

						dialog.cancel();
						ToastTool.showToast(getActivity(), jo.getString("msg"),
								1000);
					}

				} catch (JSONException e) {
					dialog.cancel();
					ToastTool.showToast(getActivity(), "服务器数据错误!", 1000);
				}

			} else {
				dialog.cancel();
				ToastTool.showToast(getActivity(), "网络连接出错,请稍后再试!", 1000);
			}
		}

		@Override
		protected void onPreExecute() {
			dialog = Helper.loadingDialog(getActivity(), "正在获取验证码....");
		}
	}

}
