package com.wan3456.sdk.view;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONException;
import org.json.JSONObject;
import com.wan3456.sdk.activity.InfoActivity;
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
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class FindAccountFrame extends Fragment implements OnClickListener {
	private SharedPreferences sharedPreferences;
	LinearLayout lin;
	// find password view
	private Button getCode_btn;
	private Button find_confirm_btn;
	private Button find_back_btn;
	private TextView ser_info;
	private EditText find_name;
	private EditText find_code;
	private EditText find_pwd;
	private String fcUrl;
	private FindCheckTask fcTask;
	private int num = 0;
	private String findUrl;
	private FindTask findTask;
	private Dialog dialog;
	private Handler handler;
	private Timer timer;
	private TimerTask task;
	private int count = 60;
	private List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(
				Helper.getLayoutId(getActivity(), "wan3456_view_findpass"),
				null, false);
		sharedPreferences = getActivity().getSharedPreferences("yssdk_info",
				Context.MODE_PRIVATE);
		lin = (LinearLayout) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_findaccount_line"));
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
		// 找回密码view
		find_name = (EditText) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_findpass_name"));
		find_code = (EditText) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_findpass_wcode"));
		find_pwd = (EditText) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_findpass_newpass"));
		getCode_btn = (Button) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_findpass_getcode"));
		find_back_btn = (Button) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_findpass_back"));
		find_confirm_btn = (Button) v.findViewById(Helper.getResId(
				getActivity(), "wan3456_findpass_confirm"));
		ser_info = (TextView) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_to_ser"));
		getCode_btn.setOnClickListener(this);
		find_confirm_btn.setOnClickListener(this);
		ser_info.setClickable(true);
		ser_info.setOnClickListener(this);
		find_back_btn.setOnClickListener(this);
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
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {

				super.handleMessage(msg);
				switch (msg.what) {
				case 1:			
					getCode_btn.setEnabled(false);
					find_name.setEnabled(false);
					String mes = "可重取" + "(" + String.valueOf(count) + ")";
					getCode_btn.setText(mes);
					if (count <= 0) {
						timer.cancel();
						task.cancel();
						count = 60;
						getCode_btn.setEnabled(true);
						getCode_btn.setText("获取验证码");
						find_name.setEnabled(true);
					}

					break;
				}
			}

		};	
		
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==Helper.getResId(getActivity(),
				"wan3456_findpass_getcode")){
			if (LoginCheckVild.isMobileNO(find_name.getText().toString()) == false) {
				ToastTool.showToast(getActivity(), "手机号格式错误,正确格式:11位手机号！",
						1000);
			} else {

				fcTask = new FindCheckTask();
				fcTask.execute("");
			}
			return;
		}if(v.getId()==Helper.getResId(getActivity(),
				"wan3456_findpass_confirm")){

			if (LoginCheckVild.isMobileNO(find_name.getText().toString()) == true) {
				if (LoginCheckVild
						.isCheckNO(find_code.getText().toString()) == false) {
					ToastTool.showToast(getActivity(), "验证码格式错误！", 1000);

				} else {
					if (LoginCheckVild.checkValid(find_pwd.getText()
							.toString(), StatusCode.CHECK_PASSWORD) == false) {
						ToastTool.showToast(getActivity(), "密码格式错误！", 1000);

					} else {

						findTask = new FindTask();
						findTask.execute("");
					}
				}

			} else {
				ToastTool.showToast(getActivity(), "手机号格式错误,正确格式:11位数字",
						1000);
			}

			return;
		}
		
		if(v.getId()==Helper.getResId(getActivity(),
				"wan3456_to_ser")){
			InfoActivity.show(getActivity(),
					InfoActivity.INDEX_SERVICE_CENTER);
			return;
		}
		if(v.getId()==Helper.getResId(getActivity(),
				"wan3456_findpass_back")){
			//调整会登陆界面
			find_name.setEnabled(true);
			getCode_btn.setEnabled(true);
			getCode_btn.setText("获取验证码");
		
			find_pwd.setText("");
			find_name.setText("");
			find_code.setText("");
			if (timer != null) {
				timer.cancel();
			}
			if (task != null) {
				task.cancel();
			}
			count = 60;
		LoginActivity ac=(LoginActivity) getActivity();
		ac.setCurFragment(LoginActivity.INDEX_MANUAL_LOGIN);
			return;
		}
		
	}
	
	/**
	 * 获取找回密码验证码task
	 */

	private class FindCheckTask extends AsyncTask<String, Integer, String> {
		String body;

		@Override
		protected void onPostExecute(String result) {
			if (body != null) {
				JSONObject jo;
				try {
					jo = new JSONObject(body);
					if (jo.getInt("status") == 1) {
						dialog.cancel();						
						timer = new Timer();
						startTask();
						timer.schedule(task, 1000, 1000);
						ToastTool.showToast(getActivity(), "验证码已经发送至手机",
								1000);
					} else {
						dialog.cancel();
						ToastTool.showToast(getActivity(),
								jo.getString("msg"), 1000);
					}

				} catch (JSONException e) {
					dialog.cancel();
					ToastTool.showToast(getActivity(), "服务器数据错误!", 1000);
				}

			} else {
				ToastTool.showToast(getActivity(), "网络连接出错!", 1000);
			}
		}

		@Override
		protected void onPreExecute() {
			dialog=Helper.loadingDialog(getActivity(),"正在获取验证码...");
		}

		@Override
		protected String doInBackground(String... params) {
			String a = "channelid="
					+ String.valueOf(sharedPreferences.getInt("channelID", 0))
					+ "&gameid="
					+ String.valueOf(sharedPreferences.getInt("gameID", 0))
					+ "&imei=" + sharedPreferences.getString("IMEI", null)
					+ "&mobile=" + find_name.getText().toString() + "&sdk_vs="
					+ StatusCode.SDK_VERSION + "&time="
					+ LoginCheckVild.getCurrTime().toString() + "&type=find";
			String sign = DesTool.replaceTool(StatusCode.DES_BEFOR, a);
			fcUrl = StaticVariable.GET_CHECKCODE + sign;
			body = NetTool.getUrlContent(fcUrl);
			if (body != null) {
				body = DesTool.replaceTool(StatusCode.DES_AFTER, body);
			}
			return body;
		}

	}

	/**
	 * 确认找回密码task
	 */

	private class FindTask extends AsyncTask<String, Integer, String> {
		String body;

		@Override
		protected String doInBackground(String... params) {
			String a = "channelid="
					+ String.valueOf(sharedPreferences.getInt("channelID", 0))
					+ "&code=" + find_code.getText().toString() + "&gameid="
					+ String.valueOf(sharedPreferences.getInt("gameID", 0))
					+ "&imei=" + sharedPreferences.getString("IMEI", null)
					+ "&newpassword=" + find_pwd.getText().toString()
					+ "&mobile=" + find_name.getText().toString() + "&sdk_vs="
					+ StatusCode.SDK_VERSION + "&time="
					+ LoginCheckVild.getCurrTime().toString();
			String sign = DesTool.replaceTool(StatusCode.DES_BEFOR, a);
			findUrl = StaticVariable.FIND + sign;
			body = NetTool.getUrlContent(findUrl);
			if (body != null) {
				body = DesTool.replaceTool(StatusCode.DES_AFTER, body);
			}
			return body;
		}

		@Override
		protected void onPostExecute(String result) {
			if (body != null) {
				JSONObject jo;
				JSONObject js;
				try {
					jo = new JSONObject(body);
					if (jo.getInt("status") == 1) {
						js = jo.getJSONObject("data");
						num = num + 1;
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("username", js.getString("username"));
						map.put("userpass", find_pwd.getText().toString());
						list.add(0, map);
						Editor editor = sharedPreferences.edit();// 获取编辑器
						editor.putString("name", js.getString("username"));
						editor.putString("password", find_pwd.getText()
								.toString());
						editor.putInt("uid", js.getInt("userid"));
						editor.putBoolean("hasCount", true);
						editor.putBoolean("remember", true);
						String a;
						try {
							a = Helper.WeatherList2String(list);
							editor.putString("userlist", a);

						} catch (IOException e) {
							e.printStackTrace();
						}
						editor.commit();
						dialog.cancel();
						ToastTool.showToast(getActivity(), "重置密码成功，请重新登录",
								1000);
						find_name.setEnabled(true);
						getCode_btn.setEnabled(true);
						getCode_btn.setText("获取验证码");
					
						find_pwd.setText("");
						find_name.setText("");
						find_code.setText("");
						if (timer != null) {
							timer.cancel();
						}
						if (task != null) {
							task.cancel();
						}
						count = 60;
						LoginActivity ac=(LoginActivity) getActivity();
						ac.setCurFragment(LoginActivity.INDEX_MANUAL_LOGIN);
						LoginFrame.findAccount();
					} else {
						dialog.cancel();
						ToastTool.showToast(getActivity(),
								jo.getString("msg"), 1000);
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
			dialog=Helper.loadingDialog(getActivity(),"正在验证...");

		}
	}
	private void startTask() {
		task = new TimerTask() {
			@Override
			public void run() {
				count--;
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
			}
		};
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		find_name.setEnabled(true);
		getCode_btn.setEnabled(true);
		getCode_btn.setText("获取验证码");
	
		find_pwd.setText("");
		find_name.setText("");
		find_code.setText("");
		if (timer != null) {
			timer.cancel();
		}
		if (task != null) {
			task.cancel();
		}
		count = 60;
	}
	
	
}