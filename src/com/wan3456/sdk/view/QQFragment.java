package com.wan3456.sdk.view;

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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class QQFragment extends Fragment {
	public SharedPreferences sharedPreferences;
	private MyTask task;
	private EditText edit_qq;
	private Dialog dialog;
	private Button submit;
	private TextView hasbang;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(
				Helper.getLayoutId(getActivity(), "wan3456_fragment_bindqq"),
				null, false);
		sharedPreferences = getActivity().getSharedPreferences("yssdk_info",
				Context.MODE_PRIVATE);
		hasbang = (TextView) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_hasbang_qq_view"));
		Button back = (Button) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_bindpqq_back"));
		submit = (Button) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_bindqq_submit"));
		edit_qq = (EditText) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_bindqq_edit"));
		if (!sharedPreferences.getString("bind_qq", "").equals("")) {
			submit.setText("更换QQ");
			edit_qq.setEnabled(false);
			hasbang.setVisibility(View.VISIBLE);

			String mes = "<font color=#333333>" + " 您已绑定QQ号:" + "</font>"
					+ "<font color=#ff0000>"
					+ sharedPreferences.getString("bind_qq", "") + "</font>";

			hasbang.setText(Html.fromHtml(mes));
			edit_qq.setText(sharedPreferences.getString("bind_qq", ""));
		} else {
			hasbang.setVisibility(View.GONE);
			submit.setText("立即绑定");
			edit_qq.setEnabled(true);
			edit_qq.setText("");
		}

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				onBackClick();
			}
		});
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (submit.getText().toString().equals("立即绑定")) {
					onSubmitClick();
				} else {
					submit.setText("立即绑定");
					edit_qq.setEnabled(true);
					edit_qq.setText("");
				}

			}
		});
		return v;
	}

	protected void onSubmitClick() {

		if (LoginCheckVild.isQQ(edit_qq.getText().toString())) {
			task = new MyTask();
			task.execute("");
		} else {
			ToastTool.showToast(getActivity(), "qq号码格式错误", 1000);
		}
	}

	protected void onBackClick() {
		if (!sharedPreferences.getString("bind_qq", "").equals("")) {
			submit.setText("更换QQ");
			edit_qq.setEnabled(false);
			hasbang.setVisibility(View.VISIBLE);
			String mes = "<font color=#333333>" + " 您已绑定QQ号:" + "</font>"
					+ "<font color=#ff0000>"
					+ sharedPreferences.getString("bind_qq", "") + "</font>";

			hasbang.setText(Html.fromHtml(mes));
			edit_qq.setText(sharedPreferences.getString("bind_qq", ""));
		} else {
			hasbang.setVisibility(View.GONE);
			submit.setText("立即绑定");
			edit_qq.setEnabled(true);
			edit_qq.setText("");
		}
		InfoActivity act = (InfoActivity) getActivity();

		act.setCurFragment(InfoActivity.INDEX_ACCOUNT);
	}

	private class MyTask extends AsyncTask<String, Integer, String> {
		String body;

		@Override
		protected String doInBackground(String... params) {
			String a = "channelid="
					+ String.valueOf(sharedPreferences.getInt("channelID", 0))
					+ "&gameid="
					+ String.valueOf(sharedPreferences.getInt("gameID", 0))
					+ "&qq=" + edit_qq.getText().toString() + "&sdk_vs="
					+ StatusCode.SDK_VERSION + "&time="
					+ LoginCheckVild.getCurrTime().toString() + "&user_id="
					+ String.valueOf(sharedPreferences.getInt("uid", 0));

			String sign = DesTool.replaceTool(StatusCode.DES_BEFOR, a);
			String url = StaticVariable.BANG_QQ + sign;

			body = NetTool.getUrlContent(url);
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
						Editor edit = sharedPreferences.edit();
						edit.putString("bind_qq", edit_qq.getText().toString());
						edit.commit();
						edit_qq.setEnabled(false);
						hasbang.setVisibility(View.VISIBLE);
						String mes = "<font color=#333333>" + " 您已绑定QQ号:"
								+ "</font>" + "<font color=#ff0000>"
								+ sharedPreferences.getString("bind_qq", "")
								+ "</font>";
						hasbang.setText(Html.fromHtml(mes));
						submit.setText("更换QQ");
						ToastTool.showToast(getActivity(), "QQ绑定成功！", 1000);
					} else {
						dialog.cancel();
						ToastTool.showToast(getActivity(), jo.getString("msg"),
								1500);
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
			loadingDialog();
		}

	}

	public void loadingDialog() {
		dialog = new AlertDialog.Builder(getActivity()).create();
		dialog.show();
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(Helper.getLayoutId(getActivity(),
				"wan3456_progress_loading_view"));
		TextView text = (TextView) dialog.findViewById(Helper.getResId(
				getActivity(), "wan3456_loading_mes"));

		text.setText("正在提交数据...");

	}

}
