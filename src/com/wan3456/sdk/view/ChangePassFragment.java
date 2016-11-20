package com.wan3456.sdk.view;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.wan3456.sdk.activity.InfoActivity;
import com.wan3456.sdk.tools.DesTool;
import com.wan3456.sdk.tools.Helper;
import com.wan3456.sdk.tools.NetTool;
import com.wan3456.sdk.tools.LoginCheckVild;
import com.wan3456.sdk.tools.StaticVariable;
import com.wan3456.sdk.tools.StatusCode;
import com.wan3456.sdk.tools.ToastTool;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChangePassFragment extends Fragment {
	public SharedPreferences sharedPreferences;
	private EditText m_oldpwd;
	private EditText m_newpwd;
	private String oldpwd;
	private String newpwd;
	private String mUrl;
	private ModifyTask mTask;
	private Dialog dialog;
	private List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	private TextView name_txt;
	private TextView notice_txt;

	@Override
	public void onHiddenChanged(boolean hidden) {

		super.onHiddenChanged(hidden);
		if (!hidden) {
			if (sharedPreferences.getBoolean("hasphone", false) == false) {
				notice_txt.setVisibility(View.VISIBLE);
			} else {
				notice_txt.setVisibility(View.GONE);
			}

		}
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(Helper.getLayoutId(getActivity(),
				"wan3456_fragment_change_pass"), null, false);
		sharedPreferences = getActivity().getSharedPreferences("yssdk_info",
				Context.MODE_PRIVATE);
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
		Button back = (Button) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_changepass_back"));
		Button submit = (Button) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_changepass_submit"));
		m_oldpwd = (EditText) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_changepass_curpass"));
		m_newpwd = (EditText) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_changepass_newpass"));
		name_txt = (TextView) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_changepass_name"));
		notice_txt = (TextView) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_changepass_notice"));
		name_txt.setText("当前帐号:" + sharedPreferences.getString("name", ""));
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				onBackClick();
			}
		});
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				onSubmitClick();
			}
		});
		if (sharedPreferences.getBoolean("hasphone", false) == false) {
			notice_txt.setVisibility(View.VISIBLE);
		} else {
			notice_txt.setVisibility(View.GONE);
		}
		return v;
	}

	private void onBackClick() {
		InfoActivity act = (InfoActivity) getActivity();
		m_oldpwd.setText("");
		m_newpwd.setText("");
		act.setCurFragment(InfoActivity.INDEX_ACCOUNT);
	}

	private void onSubmitClick() {
		oldpwd = m_oldpwd.getText().toString();
		newpwd = m_newpwd.getText().toString();
		if (!oldpwd.equals(sharedPreferences.getString("password", ""))) {
			ToastTool.showToast(getActivity(), "当前密码错误！", 1000);
			return;
		}
		if (LoginCheckVild.checkValid(newpwd, StatusCode.CHECK_PASSWORD) == false) {
			ToastTool.showToast(getActivity(), "新密码格式错误,正确格式：6-20位字母数字！", 1000);
			return;
		}
		if (oldpwd.equals(newpwd)) {
			ToastTool.showToast(getActivity(), "新旧密码不能相同！", 1000);
			return;
		} else {
			InputMethodManager imm = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(m_newpwd.getWindowToken(), 0);
			mTask = new ModifyTask();
			mTask.execute("");
		}
	}

	/**
	 * 修改密码task
	 * 
	 * @author Administrator
	 * 
	 */
	private class ModifyTask extends AsyncTask<String, Integer, String> {
		String body;
		String msg;

		@Override
		protected String doInBackground(String... params) {
			String a = "channelid="
					+ String.valueOf(sharedPreferences.getInt("channelID", 0))
					+ "&gameid="
					+ String.valueOf(sharedPreferences.getInt("gameID", 0))
					+ "&imei=" + sharedPreferences.getString("IMEI", null)
					+ "&newpassword=" + newpwd + "&oldpassword=" + oldpwd
					+ "&sdk_vs=" + StatusCode.SDK_VERSION + "&time="
					+ LoginCheckVild.getCurrTime().toString() + "&username="
					+ sharedPreferences.getString("name", "");
			String sign = DesTool.replaceTool(StatusCode.DES_BEFOR, a);
			mUrl = StaticVariable.MODIFY + sign;

			body = NetTool.getUrlContent(mUrl);
			if (body != null) {
				body = DesTool.replaceTool(StatusCode.DES_AFTER, body);
			}

			return body;
		}

		@Override
		protected void onPostExecute(String result) {
			if (body != null) {

				JSONObject jobj;
				try {
					jobj = new JSONObject(body);

					int login_state = jobj.getInt("status");

					msg = jobj.getString("msg");

					if (login_state == 1) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("username",
								sharedPreferences.getString("name", ""));
						map.put("userpass", newpwd);
						list.remove(0);
						list.add(0, map);
						Editor editor = sharedPreferences.edit();// 获取编辑器

						editor.putString("password", newpwd);
						String a;
						try {
							a = Helper.WeatherList2String(list);
							editor.putString("userlist", a);

						} catch (IOException e) {

							e.printStackTrace();
						}
						editor.commit();
						dialog.cancel();
						View toastRoot = LayoutInflater.from(getActivity())
								.inflate(
										Helper.getLayoutId(getActivity(),
												"wan3456_toast_modify"), null);
						Toast toast = new Toast(getActivity());
						toast.setView(toastRoot);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						onBackClick();

					} else {
						dialog.cancel();
						ToastTool.showToast(getActivity(), msg, 1000);

					}
				} catch (JSONException e) {

					dialog.cancel();
					ToastTool.showToast(getActivity(), "服务器数据错误！", 1000);
					e.printStackTrace();
				}

			} else {
				dialog.cancel();
				ToastTool.showToast(getActivity(), "网络异常，请稍后再试！", 1000);

			}

		}

		@Override
		protected void onPreExecute() {
			dialog = Helper.loadingDialog(getActivity(), "正在提交请求...");
		}

	}

}
