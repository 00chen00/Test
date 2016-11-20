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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

@SuppressLint({ "SetJavaScriptEnabled", "HandlerLeak" })
public class RegisterFrame extends Fragment implements OnClickListener {
	private SharedPreferences sharedPreferences;
	LinearLayout lin;
	// register view
	private EditText edit_re_name;
	private EditText edit_re_pwd;
	private Button regist_btn;
	private Button regist_fast_btn;
	private TextView to_service_txt;
	private Button read_btn;
	private boolean has_read = true;
	private String rUrl;
	private RegisterTask rTask;
	private Button name_clear;
	private Button pass_clear;
	private Button goback;
	private String user_name;
	private String user_pwd;
	// fast register view
	private FastReTask feTask;
	private String feUrl;
	private String fe_name;
	private String fe_pass;
	private Dialog dialog;
	private List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(
				Helper.getLayoutId(getActivity(), "wan3456_view_regist"), null,
				false);
		sharedPreferences = getActivity().getSharedPreferences("yssdk_info",
				Context.MODE_PRIVATE);
		lin = (LinearLayout) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_register_line"));
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
		// 注册view
		edit_re_name = (EditText) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_regist_account"));
		edit_re_pwd = (EditText) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_regist_pass"));
		regist_btn = (Button) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_regist_confirm"));
		read_btn = (Button) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_regist_agreement_check"));
		regist_fast_btn = (Button) v.findViewById(Helper.getResId(
				getActivity(), "wan3456_regist_fast"));
		to_service_txt = (TextView) v.findViewById(Helper.getResId(
				getActivity(), "wan3456_regist_agreement_btn"));
		name_clear = (Button) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_regist_account_clear"));
		pass_clear = (Button) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_regist_pass_clear"));
		goback = (Button) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_goback_login"));
		read_btn.setOnClickListener(this);
		name_clear.setOnClickListener(this);
		pass_clear.setOnClickListener(this);
		regist_btn.setOnClickListener(this);
		to_service_txt.setClickable(true);
		to_service_txt.setOnClickListener(this);
		regist_fast_btn.setOnClickListener(this);
		goback.setOnClickListener(this);
		edit_re_name.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s != null && !s.toString().trim().equals("")) {
					name_clear.setVisibility(View.VISIBLE);
				} else {
					name_clear.setVisibility(View.INVISIBLE);
				}

			}
		});

		edit_re_pwd.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s != null && !s.toString().trim().equals("")) {
					pass_clear.setVisibility(View.VISIBLE);
				} else {
					pass_clear.setVisibility(View.INVISIBLE);
				}

			}
		});

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

	}

	@Override
	public void onClick(View v) {

		if (v.getId() == Helper.getResId(getActivity(),
				"wan3456_regist_agreement_check")) {
			if (has_read == false) {
				has_read = true;
				v.setBackgroundResource(Helper.getResDraw(getActivity(),
						"wan3456_re_checked"));
			} else {
				has_read = false;
				v.setBackgroundResource(Helper.getResDraw(getActivity(),
						"wan3456_re_check"));
			}
			return;
		}
		if (v.getId() == Helper.getResId(getActivity(),
				"wan3456_regist_account_clear")) {
			edit_re_name.setText("");
			return;
		}
		if (v.getId() == Helper.getResId(getActivity(),
				"wan3456_regist_pass_clear")) {
			edit_re_pwd.setText("");
			return;
		}
		if (v.getId() == Helper.getResId(getActivity(),
				"wan3456_regist_confirm")) {
			user_name = edit_re_name.getText().toString();
			user_pwd = edit_re_pwd.getText().toString();
			regist();
			return;
		}
		if (v.getId() == Helper.getResId(getActivity(),
				"wan3456_regist_agreement_btn")) {
			serviceDialog();
			return;
		}
		if (v.getId() == Helper.getResId(getActivity(), "wan3456_regist_fast")) {
			feTask = new FastReTask();
			feTask.execute("");
			return;
		}
		if (v.getId() == Helper.getResId(getActivity(), "wan3456_goback_login")) {
			edit_re_name.setText("");
			edit_re_pwd.setText("");
			// 跳转到登录界面
			LoginActivity ac = (LoginActivity) getActivity();
			ac.setCurFragment(LoginActivity.INDEX_MANUAL_LOGIN);
			return;
		}

	}

	protected void regist() {
		if (LoginCheckVild.checkValid(user_name, StatusCode.CHECK_NAME) == false) {
			ToastTool.showToast(getActivity(), "用户名格式错误,正确格式:3-18位字母数字或下划线！",
					1000);
			return;
		}
		if (LoginCheckVild.checkValid(user_pwd, StatusCode.CHECK_PASSWORD) == false) {
			ToastTool.showToast(getActivity(), "密码格式错误,正确格式：6-20位字母数字！", 1000);

			return;
		}
		if (has_read == false) {
			ToastTool.showToast(getActivity(), "是否同意游戏协议？", 1000);
			return;
		} else {
			rTask = new RegisterTask();
			rTask.execute("");
		}

	}

	/**
	 * 协议dialog
	 */
	protected void serviceDialog() {
		// TODO Auto-generated method stub
		final Dialog dialog = new Dialog(getActivity(), Helper.getResStyle(
				getActivity(), "wan3456_Detail_dialog"));
		dialog.show();
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(Helper.getLayoutId(getActivity(),
				"wan3456_agreement_view"));
		WebView webview = (WebView) dialog.findViewById(Helper.getResId(
				getActivity(), "wan3456_agreement_webview"));
		final ProgressBar bar = (ProgressBar) dialog.findViewById(Helper
				.getResId(getActivity(), "wan3456_agreement_bar"));
		Button back = (Button) dialog.findViewById(Helper.getResId(
				getActivity(), "wan3456_agreement_back"));

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 0) {
					Bundle data = msg.getData();

					int p = data.getInt("pro", 1);

					if (p > 99) {
						bar.setVisibility(View.GONE);
					} else {
						bar.setVisibility(View.VISIBLE);
					}

					bar.setProgress(p);

				}
				super.handleMessage(msg);
			}

		};
		webview.getSettings().setDefaultTextEncodingName("UTF-8");
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

		});
		webview.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				Message mes = new Message();
				Bundle data = new Bundle();
				mes.what = 0;
				data.putInt("pro", newProgress);
				mes.setData(data);
				handler.sendMessage(mes);
				super.onProgressChanged(view, newProgress);
			}
		});

		webview.loadUrl(sharedPreferences.getString("agreement_url", ""));
	}

	/**
	 * 一键task
	 */
	private class FastReTask extends AsyncTask<String, Integer, String> {
		String body;

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			if (body != null) {

				JSONObject jobj;
				JSONObject jo;
				try {
					jobj = new JSONObject(body);
					int login_state = jobj.getInt("status");
					String msg = jobj.getString("msg");
					if (login_state == 1) {
						jo = jobj.getJSONObject("data");
						fe_name = jo.getString("username");
						fe_pass = jo.getString("password");

						dialog.cancel();
						edit_re_name.setText(fe_name);
						edit_re_pwd.setText(fe_pass);
					} else {
						dialog.cancel();
						ToastTool.showToast(getActivity(), msg, 1000);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					dialog.cancel();
					ToastTool.showToast(getActivity(), "服务器数据错误！", 1000);
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
			dialog = Helper.loadingDialog(getActivity(), "正在获取数据...");
		}

		@Override
		protected String doInBackground(String... params) {

			String a = "brand=" + sharedPreferences.getString("Brand", null)
					+ "&channelid="
					+ String.valueOf(sharedPreferences.getInt("channelID", 0))
					+ "&gameid="
					+ String.valueOf(sharedPreferences.getInt("gameID", 0))
					+ "&imei=" + sharedPreferences.getString("IMEI", null)
					+ "&modle=" + sharedPreferences.getString("Modle", null)
					+ "&osversion="
					+ sharedPreferences.getString("Version", null) + "&sdk_vs="
					+ StatusCode.SDK_VERSION + "&shadow=1" + "&time="
					+ LoginCheckVild.getCurrTime().toString();
			String sign = DesTool.replaceTool(StatusCode.DES_BEFOR, a);
			feUrl = StaticVariable.FASTRE_URL + sign;
			body = NetTool.getUrlContent(feUrl);
			if (body != null) {
				body = DesTool.replaceTool(StatusCode.DES_AFTER, body);
			}
			return body;
		}

	}

	/**
	 * 注册task
	 */
	private class RegisterTask extends AsyncTask<String, Integer, String> {
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
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("username", user_name);
						map.put("userpass", user_pwd);
						list.add(0, map);
						jo = jobj.getJSONObject("data");
						Editor editor = sharedPreferences.edit();// 获取编辑器
						editor.putString("name", user_name);
						editor.putString("password", user_pwd);
						editor.putInt("uid", jo.getInt("userid"));
						editor.putBoolean("hasphone", false);
						editor.putBoolean("hasCount", true);
						editor.putBoolean("remember", true);
						editor.putInt("isF", 1);
						editor.putBoolean("ISLogin", true);
						editor.putString("bind_qq", "");
						editor.putBoolean("vip", false);
						editor.putInt("isred_mes", 0);
						editor.putInt("message_update_time", 0);
						editor.putInt("isred_kf", 0);
						editor.putInt("service_update_time", 0);
						if (Integer.parseInt((sharedPreferences.getString(
								"ac_update_time", "0"))) > 0) {
							editor.putInt("isred_ac", 1);
						}

						if (Integer.parseInt((sharedPreferences.getString(
								"recommend_update_time", "0"))) > 0) {
							editor.putInt("isred", 1);
						}
						if (Integer.parseInt((sharedPreferences.getString(
								"peck_update_time", "0"))) > 0) {
							editor.putInt("isred_peck", 1);
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
						if (Wan3456.userListener != null) {
							Wan3456.userListener.onLoginSuccess(
									jo.getInt("userid"), jo.getInt("tstamp"),
									sign);
							getActivity().finish();
							Wan3456.getInstance(getActivity()).show();
						} else {
							ToastTool.showToast(getActivity(),
									"程序发生未知错误，请退出游戏重新登录！", 3000);
						}
					} else {
						dialog.cancel();
						ToastTool.showToast(getActivity(), msg, 1000);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					dialog.cancel();
					ToastTool.showToast(getActivity(), "服务器数据错误！", 1000);
					e.printStackTrace();
				}

			} else {
				dialog.cancel();
				ToastTool.showToast(getActivity(), "网络异常,请稍后再试！", 1000);
			}
		}

		@Override
		protected void onPreExecute() {
			dialog = Helper.loadingDialog(getActivity(), "正在注册...");
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

			rUrl = StaticVariable.REGISTER_URL + sign;
			body = NetTool.getUrlContent(rUrl);
			if (body != null) {
				body = DesTool.replaceTool(StatusCode.DES_AFTER, body);
			}
			return body;
		}

	}
}
