package com.wan3456.sdk.view;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.wan3456.sdk.activity.InfoActivity;
import com.wan3456.sdk.tools.DesTool;
import com.wan3456.sdk.tools.Helper;
import com.wan3456.sdk.tools.LoginCheckVild;
import com.wan3456.sdk.tools.NetTool;
import com.wan3456.sdk.tools.StaticVariable;
import com.wan3456.sdk.tools.StatusCode;
import com.wan3456.sdk.tools.ToastTool;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class UnbindFragment extends Fragment {
	public SharedPreferences sharedPreferences;
	private Button back;
	private Handler handler;
	private Timer timer;
	private TimerTask task;
	private EditText edit_code;
	private Button submit;
	private Button getCode;
	private int count = 60;
	private String bUrl;// 绑定url
	private UnbindTask bTask;
	private String bcUrl;// 绑定验证码url
	private CheckTask bcTask;
	private Dialog dialog;

	@Override
	public void onDestroy() {

		super.onDestroy();
		if (timer != null) {
			timer.cancel();
		}
		if (task != null) {
			task.cancel();
		}
		count = 60;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(Helper.getLayoutId(getActivity(),
				"wan3456_fragment_unbind_phone"), null, false);
		sharedPreferences = getActivity().getSharedPreferences("yssdk_info",
				Context.MODE_PRIVATE);
		back = (Button) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_unbindphone_back"));
		submit = (Button) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_unbindphone_submit"));
		edit_code = (EditText) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_unbindphone_code"));
		getCode = (Button) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_unbindphone_getcode"));
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackClick();

			}
		});
		getCode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				bcTask = new CheckTask();
				bcTask.execute("");

			}
		});
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onSubmitClick();

			}
		});

		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {

				super.handleMessage(msg);
				switch (msg.what) {

				case 1:
					getCode.setEnabled(false);
					String mes = "可重取" + "(" + String.valueOf(count) + ")";
					getCode.setText(mes);
					if (count <= 0) {
						timer.cancel();
						task.cancel();
						count = 60;
						getCode.setEnabled(true);
						getCode.setText("获取验证码");
					}
					break;
				}
			}

		};
		return v;
	}

	protected void onSubmitClick() {

		if (LoginCheckVild.isCheckNO(edit_code.getText().toString()) == false) {
			ToastTool.showToast(getActivity(), "验证码格式错误!", 1500);
			return;
		} else {
			InputMethodManager imm = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(edit_code.getWindowToken(), 0);
			bTask = new UnbindTask();
			bTask.execute("");
		}
	}

	/**
	 * /* 获取绑定手机验证码task
	 */
	private class CheckTask extends AsyncTask<String, Integer, String> {
		String body;

		@Override
		protected String doInBackground(String... params) {

			String a = "channelid="
					+ String.valueOf(sharedPreferences.getInt("channelID", 0))
					+ "&gameid="
					+ String.valueOf(sharedPreferences.getInt("gameID", 0))
					+ "&imei=" + sharedPreferences.getString("IMEI", null)
					+ "&sdk_vs=" + StatusCode.SDK_VERSION + "&time="
					+ LoginCheckVild.getCurrTime().toString() + "&type=unbind"
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
						timer = new Timer();
						startTask();
						timer.schedule(task, 1000, 1000);
						ToastTool.showToast(getActivity(), "验证码已发送，请注意查收！",
								1000);

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
			loadingDialog(1);
		}

	}

	private void loadingDialog(int type) {
		dialog = new AlertDialog.Builder(getActivity()).create();
		dialog.show();
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(Helper.getLayoutId(getActivity(),
				"wan3456_progress_loading_view"));
		TextView text = (TextView) dialog.findViewById(Helper.getResId(
				getActivity(), "wan3456_loading_mes"));
		if (type == 1) {
			text.setText("正在获取验证码...");
		} else {
			text.setText("正在验证...");
		}

	}

	/**
	 * 绑定手机task
	 * 
	 */
	private class UnbindTask extends AsyncTask<String, Integer, String> {
		String body;

		@Override
		protected String doInBackground(String... params) {
			String a = "channelid="
					+ String.valueOf(sharedPreferences.getInt("channelID", 0))
					+ "&code=" + edit_code.getText().toString() + "&gameid="
					+ String.valueOf(sharedPreferences.getInt("gameID", 0))
					+ "&imei=" + sharedPreferences.getString("IMEI", null)
					+ "&sdk_vs=" + StatusCode.SDK_VERSION + "&time="
					+ LoginCheckVild.getCurrTime().toString() + "&username="
					+ sharedPreferences.getString("name", "");
			String sign = DesTool.replaceTool(StatusCode.DES_BEFOR, a);
			bUrl = StaticVariable.UNBIND_PHONE + sign;
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
						editor.putBoolean("hasphone", false);
						editor.putString("phone", "");
						editor.commit();
						dialog.cancel();
						if (timer != null) {
							timer.cancel();
						}
						if (task != null) {
							task.cancel();
						}
						count = 60;
						edit_code.setText("");
						getCode.setEnabled(true);
						getCode.setText("获取验证码");
						ToastTool
								.showToast(getActivity(), "手机解绑成功，返回游戏！", 2000);
						getActivity().finish();

					} else {
						dialog.cancel();
						ToastTool.showToast(getActivity(), jo.getString("msg"),
								1500);

					}
				} catch (JSONException e) {
					dialog.cancel();
					ToastTool.showToast(getActivity(), "服务器数据错误!", 1500);
				}

			} else {

				dialog.cancel();
				ToastTool.showToast(getActivity(), "网络连接出错！", 1500);
			}
		}

		@Override
		protected void onPreExecute() {
			loadingDialog(2);
		}

	}

	protected void onBackClick() {

		edit_code.setText("");
		getCode.setEnabled(true);
		getCode.setText("获取验证码");
		if (timer != null) {
			timer.cancel();
		}
		if (task != null) {
			task.cancel();
		}
		count = 60;
		InfoActivity act = (InfoActivity) getActivity();
		act.setCurFragment(InfoActivity.INDEX_BIND_PHONE);

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
}
