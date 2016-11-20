package com.wan3456.sdk.activity;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.switfpass.pay.MainApplication;
import com.switfpass.pay.activity.PayPlugin;
import com.switfpass.pay.bean.RequestMsg;
import com.wan3456.sdk.Wan3456;
import com.wan3456.sdk.alipay.AlixPay;
import com.wan3456.sdk.bean.OrderInfo;
import com.wan3456.sdk.bean.PayWayAdapter;
import com.wan3456.sdk.tools.DesTool;
import com.wan3456.sdk.tools.Helper;
import com.wan3456.sdk.tools.NetTool;
import com.wan3456.sdk.tools.LoginCheckVild;
import com.wan3456.sdk.tools.StaticVariable;
import com.wan3456.sdk.tools.StatusCode;
import com.wan3456.sdk.tools.ToastTool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint({ "ResourceAsColor", "SetJavaScriptEnabled", "HandlerLeak" })
public class PayActivity extends Activity {

	private SharedPreferences sharedPreferences;
	public static PayActivity PayAct = null;
	private Dialog dialog;
	private FrameLayout pay_lin;
	private int screenWidth = 0;
	private RelativeLayout title_line;
	private ListView listview;
	private PayWayAdapter adapter;
	private Button back;
	private Button pay;
	private Button more;
	// payshow view
	private LinearLayout payshow_lin;
	private LinearLayout payshow_editmoney_lin;
	private TextView paynum_show;
	private TextView payxn_num_show;
	private TextView payuser_show;
	private EditText money_edit;
	// union view
	private LinearLayout pay_union_line;
	private int ptag;// 0.支付宝 1.银行卡 2:微富通
	private int webPay = 0;// 判断银行卡是否支付成功
	private Dialog ybDialog;// 易宝充值界面dialog
	private Dialog exDialog;// 退出支付dialog
	private String unUrl;// 跳转银行卡支付页面的url
	// alipay view
	private LinearLayout pay_alipay_line;
	private HashMap<String, String> cmap = new HashMap<String, String>();// 选中的支付方式信息map
	private List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();// 支付方式集合
	// wx
	private LinearLayout pay_wx_line;
	// yl
	private LinearLayout pay_yl_line;
	// private int per;
	private String money;// 支付金额
	private String pUrl;// 请求订单url
	private PayTask pTask;
	private String gUrl;// 请求支付列表url
	private ListTask lTask;
	private String ord;// 订单号
	private PopupWindow popupWindow = null;
	protected void onDestroy() {
		super.onDestroy();
		Editor editor = sharedPreferences.edit();// 获取编辑器
		editor.putInt("isOn", 1);
		editor.commit();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(Helper.getLayoutId(PayActivity.this, "wan3456_pay"));
		PayAct = this;
		sharedPreferences = getSharedPreferences("yssdk_info",
				Context.MODE_PRIVATE);
		if (sharedPreferences.getInt("sreen", StatusCode.LANDSCAPE) == StatusCode.PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		}
		if (sharedPreferences.getInt("IsLimit", 0) == 1) {
			money = String.valueOf(sharedPreferences.getInt("AMOUNT", 0));
		}

		pay_lin = (FrameLayout) findViewById(Helper.getResId(PayActivity.this,
				"wan3456_pay_line"));
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		screenWidth = d.getWidth();
		int a;
		if (sharedPreferences.getInt("sreen", StatusCode.LANDSCAPE) == StatusCode.PORTRAIT) {
			a = (int) (0.34 * screenWidth);
		} else {
			a = (int) (0.21 * screenWidth);
		}
		LayoutParams layoutParams = (LayoutParams) pay_lin.getLayoutParams();
		layoutParams.width = a;
		pay_lin.setLayoutParams(layoutParams);
		back = (Button) findViewById(Helper.getResId(PayActivity.this,
				"wan3456_pay_exsit"));
		more = (Button) findViewById(Helper.getResId(PayActivity.this,
				"wan3456_pay_more"));
		pay = (Button) findViewById(Helper.getResId(PayActivity.this,
				"wan3456_pay_btn"));
		title_line = (RelativeLayout) findViewById(Helper.getResId(
				PayActivity.this, "wan3456_pay_re_line"));
		payshow_lin = (LinearLayout) findViewById(Helper.getResId(
				PayActivity.this, "wan3456_paymes_show_line"));
		payshow_editmoney_lin = (LinearLayout) payshow_lin.findViewById(Helper
				.getResId(PayActivity.this, "wan3456_money_line"));
		money_edit = (EditText) payshow_lin.findViewById(Helper.getResId(
				PayActivity.this, "wan3456_money"));
		paynum_show = (TextView) payshow_lin.findViewById(Helper.getResId(
				PayActivity.this, "wan3456_pay_amount"));
		payxn_num_show = (TextView) payshow_lin.findViewById(Helper.getResId(
				PayActivity.this, "wan3456_pay_sp"));
		payuser_show = (TextView) payshow_lin.findViewById(Helper.getResId(
				PayActivity.this, "wan3456_pay_username"));
		// alipay
		pay_alipay_line = (LinearLayout) findViewById(Helper.getResId(
				PayActivity.this, "wan3456_pay_alipay_lin"));
		// union
		pay_union_line = (LinearLayout) findViewById(Helper.getResId(
				PayActivity.this, "wan3456_pay_union_lin"));
		pay_wx_line = (LinearLayout) findViewById(Helper.getResId(
				PayActivity.this, "wan3456_pay_wx_lin"));
		pay_yl_line = (LinearLayout) findViewById(Helper.getResId(
				PayActivity.this, "wan3456_pay_yl_lin"));
		listview = (ListView) findViewById(Helper.getResId(PayActivity.this,
				"wan3456_pay_list"));
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pHandler.sendEmptyMessage(StatusCode.PAY_STATIC_EXIT);
			}
		});
		more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getPopupWindow();
				popupWindow.showAsDropDown(title_line, title_line.getWidth(), 0);

			}
		});
		pay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sharedPreferences.getInt("IsLimit", 0) == 1) {
					money = String.valueOf(sharedPreferences
							.getInt("AMOUNT", 0));
					pUrl = StaticVariable.USER_PAYLIST;
					pTask = new PayTask();
					pTask.execute("");
				} else {
					if (money_edit.getText().toString().equals("")
							|| money_edit.getText().toString()
									.subSequence(0, 1).equals("0")) {
						ToastTool.showToast(PayActivity.this, "输入金额格式错误", 1000);
					} else {
						money = money_edit.getText().toString();
						pUrl = StaticVariable.USER_PAYLIST;
						pTask = new PayTask();
						pTask.execute("");
					}
				}

			}
		});

		money_edit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s != null && !s.toString().trim().equals("")) {
					if (s.toString().trim().subSequence(0, 1).equals("0")) {
						payxn_num_show.setText("商品:"
								+ "0"
								+ " "
								+ sharedPreferences
										.getString("itemName", "游戏币"));
					} else {
						int a = Integer.parseInt(s.toString().trim());
						int all = a * sharedPreferences.getInt("ratio", 10);
						payxn_num_show.setText("商品:"
								+ String.valueOf(all)
								+ " "
								+ sharedPreferences
										.getString("itemName", "游戏币"));
					}
				} else {
					payxn_num_show.setText(String.valueOf("商品:" + "0 " + " "
							+ sharedPreferences.getString("itemName", "游戏币")));
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		if (sharedPreferences.getInt("IsLimit", 0) == 0) {
			payshow_editmoney_lin.setVisibility(View.VISIBLE);
			paynum_show.setVisibility(View.GONE);
		} else {
			payshow_editmoney_lin.setVisibility(View.GONE);
			paynum_show.setVisibility(View.VISIBLE);
		}

		listview.setCacheColorHint(0);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				HashMap<String, String> map = (HashMap<String, String>) listview
						.getItemAtPosition(arg2);
				pay.setEnabled(true);
				if (map.get("pay_check").equals("no")) {
					for (int i = 0; i < list.size(); i++) {
						HashMap<String, String> map1 = list.get(i);
						if (map1.get("pay_check").equals("yes")) {
							map1.put("pay_check", "no");
							list.remove(i);
							list.add(i, map1);
						}
					}
					map.put("pay_check", "yes");
					list.remove(arg2);
					list.add(arg2, map);
					cmap = map;
					adapter.notifyDataSetChanged();
					check(cmap);
				}

			}
		});
		lTask = new ListTask();
		lTask.execute("");
	}

	public List<HashMap<String, String>> getPayList(String string, JSONObject jo) {
		try {
			JSONArray array = jo.getJSONArray(string);
			for (int i = 0; i < array.length(); i++) {
				JSONObject j = array.getJSONObject(i);
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("pay_name", j.getString("payName"));
				map.put("pay_type", j.getString("payType"));
				if (i == 0) {
					map.put("pay_check", "yes");
				} else {
					map.put("pay_check", "no");
				}
				if (j.getString("payType").equals("alipay")
						|| j.getString("payType").equals("yeepay")
						|| j.getString("payType").equals("wftpay")
						|| j.getString("payType").equals("unionpay")) {
					list.add(map);
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 支付列表task
	 */
	private class ListTask extends AsyncTask<String, Integer, String> {
		String body;

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			if (body != null) {
				Log.i("wan3456", "back>>>>" + body);
				JSONObject jobj;
				JSONObject jo;
				try {
					jobj = new JSONObject(body);
					int login_state = jobj.getInt("status");
					String msg = jobj.getString("msg");
					jo = jobj.getJSONObject("data");
					if (login_state == 1) {
						getPayList("paylist", jo);

						if (list.size() > 0) {
							adapter = new PayWayAdapter(PayActivity.this, list);
							listview.setAdapter(adapter);
							cmap = list.get(0);
							check(cmap);
							dialog.cancel();
						} else {
							dialog.cancel();
							ToastTool.showToast(PayActivity.this,
									"进入充值中心失败，获取列表失败！", 1000);
							pHandler.sendEmptyMessage(StatusCode.PAY_STATIC_EXIT);
						}

					} else {
						dialog.cancel();
						ToastTool.showToast(PayActivity.this, msg, 1000);
						pHandler.sendEmptyMessage(StatusCode.PAY_STATIC_EXIT);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					dialog.cancel();
					ToastTool.showToast(PayActivity.this, "进入充值中心失败，服务器数据错误！",
							1000);
					pHandler.sendEmptyMessage(StatusCode.PAY_STATIC_EXIT);
				}

			} else {

				dialog.cancel();
				ToastTool.showToast(PayActivity.this, "进入充值中心失败，检测网络是否已连接！",
						1000);
				pHandler.sendEmptyMessage(StatusCode.PAY_STATIC_EXIT);
			}
		}

		@Override
		protected void onPreExecute() {
			loadingDialog("正在进入3456玩支付中心");

		}

		@Override
		protected String doInBackground(String... params) {
			String a = "channelid="
					+ String.valueOf(sharedPreferences.getInt("channelID", 0))
					+ "&gameid="
					+ String.valueOf(sharedPreferences.getInt("gameID", 0))
					+ "&sdk_vs=" + StatusCode.SDK_VERSION + "&time="
					+ LoginCheckVild.getCurrTime().toString();
			String sign = DesTool.replaceTool(StatusCode.DES_BEFOR, a);
			gUrl = StaticVariable.USER_PAYLIST + sign;
			body = NetTool.getUrlContent(gUrl);
			if (body != null) {
				body = DesTool.replaceTool(StatusCode.DES_AFTER, body);
			}

			return body;
		}

	}

	/**
	 * 支付task
	 */
	private class PayTask extends AsyncTask<String, Integer, String> {
		String body;

		@Override
		protected void onPostExecute(String result) {
			if (body != null) {
				Log.i("wan3456", "back>>>>" + body);
				JSONObject jobj;
				JSONObject jo;
				try {
					jobj = new JSONObject(body);
					int login_state = jobj.getInt("status");
					String msg = jobj.getString("msg");
					jo = jobj.getJSONObject("data");
					if (login_state == 1) {
						ord = jo.getString("orderid");
						dialog.cancel();
						doPay(ptag, jo);
					} else {
						dialog.cancel();
						pay.setEnabled(true);
						ToastTool.showToast(PayActivity.this, msg, 1000);

					}

				} catch (JSONException e) {
					dialog.cancel();
					pay.setEnabled(true);
					e.printStackTrace();
				}

			} else {
				dialog.cancel();
				pay.setEnabled(true);
				ToastTool.showToast(PayActivity.this, "网络异常,请稍后再试！", 1000);
			}
		}

		@Override
		protected void onPreExecute() {
			pay.setEnabled(false);
			loadingDialog("正在通过3456玩安全支付");
		}

		@Override
		protected String doInBackground(String... params) {
			String IP = sharedPreferences.getString("IP", null);
			if (IP == null || IP.equals("")) {
				IP = Helper.getIP(PayActivity.this);
				Editor editor = sharedPreferences.edit();// 获取编辑器
				editor.putString("IP", IP);
				editor.commit();
			}

			String a = "channelid="
					+ String.valueOf(sharedPreferences.getInt("channelID", 0))
					+ "&extrainfo="
					+ String.valueOf(sharedPreferences.getString("eInfo", null))
					+ "&gameid="
					+ String.valueOf(sharedPreferences.getInt("gameID", 0))
					+ "&gamerole="
					+ String.valueOf(sharedPreferences.getString("gRole", null))
					+ "&imei=" + sharedPreferences.getString("IMEI", null)
					+ "&money=" + money + "&paytype=" + cmap.get("pay_type")
					+ "&rolelevel="
					+ String.valueOf(sharedPreferences.getInt("roleLevel", 0))
					+ "&sdk_vs=" + StatusCode.SDK_VERSION + "&serverid="
					+ String.valueOf(sharedPreferences.getInt("serId", 0))
					+ "&terminalid="
					+ sharedPreferences.getString("IMEI", null)
					+ "&terminaltype="
					+ sharedPreferences.getString("PHONE_TYPE", null)
					+ "&time=" + LoginCheckVild.getCurrTime().toString()
					+ "&userip=" + IP + "&username="
					+ sharedPreferences.getString("name", "");
			Log.i("wan3456", "sign=" + a);
			String sign = DesTool.replaceTool(StatusCode.DES_BEFOR, a);
			pUrl = StaticVariable.USER_PAYPOST + sign;
			body = NetTool.getUrlContent(pUrl);
			if (body != null) {
				body = DesTool.replaceTool(StatusCode.DES_AFTER, body);
			}
			return body;
		}

	}

	/**
	 * 易宝支付dialog
	 * 
	 * @param tag
	 * 
	 * @param name
	 * @param url
	 */
	private void creatYBWebDialog(String name, String loadUrl) {
		webPay = 0;
		dialog.cancel();
		ybDialog = new Dialog(PayActivity.this, Helper.getResStyle(
				PayActivity.this, "wan3456_Detail_dialog"));
		ybDialog.show();
		ybDialog.setCancelable(true);
		ybDialog.setCanceledOnTouchOutside(false);
		ybDialog.setContentView(Helper.getLayoutId(PayActivity.this,
				"wan3456_union_web_view"));
		final Button back = (Button) ybDialog.findViewById(Helper.getResId(
				PayActivity.this, "wan3456_pay_web_back"));
		final ProgressBar bar = (ProgressBar) ybDialog.findViewById(Helper
				.getResId(PayActivity.this, "wan3456_union_web_bar"));
		final WebView web = (WebView) ybDialog.findViewById(Helper.getResId(
				PayActivity.this, "wan3456_union_webview"));
		web.getSettings().setDefaultTextEncodingName("UTF-8");
		web.getSettings().setJavaScriptEnabled(true);
		web.requestFocus();
		web.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE); // 禁止缓存
		ybDialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {

				if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
						&& event.getAction() != KeyEvent.ACTION_UP) {
					if (webPay == 0) {
						createExsitDialog();
					}
				}
				return false;
			}
		});
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (webPay == 1) {
					ybDialog.dismiss();
					pHandler.sendEmptyMessage(StatusCode.PAY_STATIC_SUCCESS);
				} else {
					createExsitDialog();
				}

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
		web.setWebViewClient(new WebViewClient() {

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				handler.proceed(); // 接受所有网站的证书
			}

			@Override
			public void onPageFinished(WebView view, String url) {

				super.onPageFinished(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				if (url.contains("http://api.3456wan.com/pay/front_callback?paytype=yeepay")) {
					webPay = 1;
					pHandler.sendEmptyMessage(StatusCode.PAY_STATIC_SUCCESS);
				} else {
					webPay = 0;
					
				}
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

		});
		web.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onCloseWindow(WebView window) {
				super.onCloseWindow(window);
			}

			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					JsResult result) {
				
                if(ptag==3){
                	if(message.equals("success")){
                		webPay=1;
                	
                		pHandler.sendEmptyMessage(StatusCode.PAY_STATIC_SUCCESS);
                		
                	}if(message.equals("failure")){
                		webPay=0;
                		ybDialog.dismiss();
        				pHandler.sendEmptyMessage(StatusCode.PAY_STATIC_EXIT);
                	}
                	result.confirm();
        			return true;
                }else{
				return super.onJsAlert(view, url, message, result);}
			}

			@Override
			public boolean onJsBeforeUnload(WebView view, String url,
					String message, JsResult result) {

				return super.onJsBeforeUnload(view, url, message, result);
			}

			@Override
			public boolean onJsConfirm(WebView view, String url,
					String message, JsResult result) {

				return super.onJsConfirm(view, url, message, result);
			}

			@Override
			public boolean onJsPrompt(WebView view, String url, String message,
					String defaultValue, JsPromptResult result) {

				return super.onJsPrompt(view, url, message, defaultValue,
						result);
			}

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

			@Override
			public void onReceivedTitle(WebView view, String title) {
				
				super.onReceivedTitle(view, title);
			}

		});

		class SdkUnionForJs {
			public SdkUnionForJs() {

			}

			@SuppressWarnings("unused")
			public void paySuccess() {
				webPay = 1;
				ybDialog.dismiss();
				pHandler.sendEmptyMessage(StatusCode.PAY_STATIC_SUCCESS);

			}

		}

		web.addJavascriptInterface(new SdkUnionForJs(), "sdkUnoin");
		web.loadUrl(loadUrl);

	}

	public void doPay(int ptag, JSONObject jo) throws JSONException {
		switch (ptag) {
		case 0:
			String info = jo.getString("alipaycontent");
			String sign = jo.getString("alipysign");
			loadingDialog("正在调用支付宝支付");
			AlixPay al = new AlixPay(PayActivity.this, info, sign);
			al.pay();
			break;
		case 1:
			unUrl = jo.getString("url");
			creatYBWebDialog("银行卡支付", unUrl);
			break;

		case 2:
			// 微信wap支付
			RequestMsg wx_msg = new RequestMsg();
			wx_msg.setMoney(Double.parseDouble(money));// 支付金额（转成double型）
			wx_msg.setTokenId(jo.getString("wfttokenid"));// 服务器端获取的token_id
			wx_msg.setOutTradeNo(ord);// 订单号
			wx_msg.setTradeType(MainApplication.PAY_WX_WAP);
			PayPlugin.unifiedH5Pay(PayActivity.this, wx_msg);
			break;
		case 3:
			loadingDialog("正在调用银联支付");
			String ylUrl = jo.getString("postUrl");
			creatYBWebDialog("银联支付", ylUrl);
			break;
		}

	}

	/**
	 * webiew退出dialog
	 */
	protected void createExsitDialog() {
		exDialog = new AlertDialog.Builder(PayActivity.this).create();
		exDialog.show();
		exDialog.setCancelable(false);
		exDialog.setCanceledOnTouchOutside(false);
		exDialog.setContentView(Helper.getLayoutId(PayActivity.this,
				"wan3456_pay_exsit_view"));
		Button con = (Button) exDialog.findViewById(Helper.getResId(
				PayActivity.this, "wan3456_payexsit_osure"));
		Button exsit = (Button) exDialog.findViewById(Helper.getResId(
				PayActivity.this, "wan3456_payexsit_odimiss"));
		exsit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				exDialog.dismiss();
				ybDialog.dismiss();
				pHandler.sendEmptyMessage(StatusCode.PAY_STATIC_EXIT);

			}
		});
		con.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				exDialog.dismiss();
			}
		});

	}

	private void check(HashMap<String, String> map) {
		payuser_show.setText("帐号:" + sharedPreferences.getString("name", ""));
		// per = Integer.valueOf(cmap.get("pay_per"));
		if (sharedPreferences.getInt("IsLimit", 0) == 1) {
			payxn_num_show.setText("商品:"
					+ String.valueOf(sharedPreferences.getInt("count", 10))
					+ " " + sharedPreferences.getString("itemName", "游戏币"));
			paynum_show.setText(String.valueOf(sharedPreferences.getInt(
					"AMOUNT", 0)) + "元");
		} else {
			money_edit.setText("");
			payxn_num_show.setText("商品:" + "0" + " "
					+ sharedPreferences.getString("itemName", "游戏币"));
		}
		if (cmap.get("pay_type").equals("alipay")) {
			// cardType=0;
			ptag = 0;
			pay_union_line.setVisibility(View.GONE);
			pay_alipay_line.setVisibility(View.VISIBLE);
			pay_wx_line.setVisibility(View.GONE);
			pay_yl_line.setVisibility(View.GONE);
			pay.setText("立即支付");
			return;
		}
		if (cmap.get("pay_type").equals("yeepay")) {
			ptag = 1;
			// scrollPullTop();

			pay_union_line.setVisibility(View.VISIBLE);
			pay_alipay_line.setVisibility(View.GONE);
			pay_wx_line.setVisibility(View.GONE);
			pay_yl_line.setVisibility(View.GONE);
			pay.setText("下一步");
			return;
		}
		if (cmap.get("pay_type").equals("wftpay")) {
			// 微信支付
			ptag = 2;
			pay_union_line.setVisibility(View.GONE);
			pay_alipay_line.setVisibility(View.GONE);
			pay_yl_line.setVisibility(View.GONE);
			pay_wx_line.setVisibility(View.VISIBLE);
			pay.setText("立即支付");
			return;
		}
		if (cmap.get("pay_type").equals("unionpay")) {
			// 银联支付
			ptag = 3;
			pay_yl_line.setVisibility(View.VISIBLE);
			pay_union_line.setVisibility(View.GONE);
			pay_alipay_line.setVisibility(View.GONE);
			pay_wx_line.setVisibility(View.GONE);
			pay.setText("立即支付");
			return;
		}
	}

	protected void getPopupWindow() {
		if (null != popupWindow) {
			popupWindow.dismiss();
			initPopupWindow();
			return;
		} else {
			initPopupWindow();
		}

	}

	private void initPopupWindow() {
		// TODO Auto-generated method stub
		View popupWindow_view = getLayoutInflater().inflate(
				Helper.getLayoutId(PayActivity.this, "wan3456_pop_more"), null,
				false);
		popupWindow_view.setBackgroundResource(Helper.getResDraw(
				PayActivity.this, "wan3456_pop_bg"));
		popupWindow = new PopupWindow(popupWindow_view,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		ColorDrawable dw = new ColorDrawable(0x00);
		popupWindow.setBackgroundDrawable(dw);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		TextView ser = (TextView) popupWindow_view.findViewById(Helper
				.getResId(PayActivity.this, "wan3456_pay_toser"));
		ser.setClickable(true);
		ser.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// serDialog();
				InfoActivity.show(PayActivity.this,
						InfoActivity.INDEX_SERVICE_CENTER);
				popupWindow.dismiss();
			}
		});

	}

	private void loadingDialog(String title) {
		dialog = new AlertDialog.Builder(PayActivity.this).create();
		dialog.show();
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(Helper.getLayoutId(PayActivity.this,
				"wan3456_pay_loading_view"));
		TextView text = (TextView) dialog.findViewById(Helper.getResId(
				PayActivity.this, "wan3456_pay_loading_text"));
		text.setText(title);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Wan3456.payListener.callback(StatusCode.PAY_STATIC_EXIT, null);
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 支付结果dialog
	 * 
	 * @param info_i
	 */
	protected void createPayStatic(final OrderInfo info, final int type) {
		final Dialog sDialog = new AlertDialog.Builder(PayActivity.this)
				.create();
		sDialog.show();
		WindowManager m = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		int w = d.getWidth();

		if (sharedPreferences.getInt("sreen", StatusCode.LANDSCAPE) == StatusCode.PORTRAIT) {
			sDialog.getWindow().setLayout(3 * w / 4, LayoutParams.WRAP_CONTENT);
		}
		sDialog.setCancelable(false);
		sDialog.setCanceledOnTouchOutside(false);
		sDialog.setContentView(Helper.getLayoutId(PayActivity.this,
				"wan3456_pay_static_view"));
		Button ok = (Button) sDialog.findViewById(Helper.getResId(
				PayActivity.this, "wan3456_paystatic_ok"));
		TextView title = (TextView) sDialog.findViewById(Helper.getResId(
				PayActivity.this, "wan3456_paystatic_mes"));
		if (type == 0) {
			title.setText("本次订单已成功提交！！");

		} else {
			title.setText("本次订单已支付成功,金额: " + info.getOrderAmount() + " 元");

		}
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Wan3456.payListener.callback(StatusCode.PAY_STATIC_SUCCESS,
						info);
				sDialog.dismiss();
				if (ybDialog != null && ybDialog.isShowing()) {
					ybDialog.dismiss();
				}
				PayActivity.this.finish();
			}
		});

	}

	public Handler pHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
		
			switch (msg.what) {
			case StatusCode.PAY_STATIC_SUCCESS:
				pay.setEnabled(true);
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				OrderInfo infos = new OrderInfo();
				infos.setOrderAmount(money);
				infos.setOrderId(ord);
				infos.setPayWayName(cmap.get("pay_name").toString());
				createPayStatic(infos, 1);
				break;
			case StatusCode.PAY_STATIC_EXIT:
				pay.setEnabled(true);
				Wan3456.payListener.callback(StatusCode.PAY_STATIC_EXIT, null);
				PayActivity.this.finish();
				break;
			case StatusCode.PAY_STATIC_ING:
				pay.setEnabled(true);
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				OrderInfo info_i = new OrderInfo();
				info_i.setOrderAmount(money);
				info_i.setOrderId(ord);
				info_i.setPayWayName(cmap.get("pay_name").toString());
				createPayStatic(info_i, 0);

				break;
			case StatusCode.OTHER_FAIL:
				pay.setEnabled(true);
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				ToastTool.showToast(PayActivity.this, "支付失败!", 1000);
				break;
			}
		}

	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			pay.setEnabled(true);
			return;
		}
		checkBackData(ptag, requestCode, data, resultCode);

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void checkBackData(int tag, int requestCode, Intent data,
			int resultCode) {
		switch (tag) {
		case 2:// 微信
			String respCode = data.getExtras().getString("resultCode");
			if (!TextUtils.isEmpty(respCode)
					&& respCode.equalsIgnoreCase("success")) {
				// 标示支付成功

				pHandler.sendEmptyMessage(StatusCode.PAY_STATIC_SUCCESS);
			} else { // 其他状态NOPAY状态：取消支付，未支付等状态

				pHandler.sendEmptyMessage(StatusCode.OTHER_FAIL);
			}
			break;

		default:
			break;
		}

	}

}
