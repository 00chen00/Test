package com.wan3456.sdk.view;

import com.wan3456.sdk.tools.DesTool;
import com.wan3456.sdk.tools.Helper;
import com.wan3456.sdk.tools.LoginCheckVild;
import com.wan3456.sdk.tools.StatusCode;
import com.wan3456.sdk.tools.ToastTool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

@SuppressLint({ "SetJavaScriptEnabled", "HandlerLeak" })
public class ServiceCenterFragment extends Fragment {
	private Handler handler;
	private ProgressBar bar;
	private WebView webview;
	private Button back;
	private Button close;
	private String url;
	public SharedPreferences sharedPreferences;
	private String apk_name;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(Helper.getLayoutId(getActivity(),
				"wan3456_fragment_service_center"), null, false);
		PackageInfo info;
		try {
			info = getActivity().getPackageManager().getPackageInfo(
					getActivity().getPackageName(), 0);
			PackageManager pManager = getActivity().getPackageManager();
			apk_name = pManager.getApplicationLabel(info.applicationInfo)
					.toString();

		} catch (NameNotFoundException e) {

			e.printStackTrace();
		}
		sharedPreferences = getActivity().getSharedPreferences("yssdk_info",
				Context.MODE_PRIVATE);
		String sign = "channelid="
				+ String.valueOf(sharedPreferences.getInt("channelID", 0))
				+ "&gameid=" + sharedPreferences.getInt("gameID", -1)
				+ "&gamename=" + apk_name + "&sdk_ver="
				+ StatusCode.SDK_VERSION + "&time="
				+ LoginCheckVild.getCurrTime().toString() + "&username="
				+ sharedPreferences.getString("name", "");
		sign = DesTool.replaceTool(StatusCode.DES_BEFOR, sign);
		//url = "http://testapi.3456wan.com/user/my_service?sign=" + sign;
		// Log.i("wan3456", "url>>>"+url);
		url = sharedPreferences.getString("service_center_url", "") + sign;
		back = (Button) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_service_center_back"));
		close = (Button) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_service_center_colse"));

		webview = (WebView) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_service_center_webview"));
		bar = (ProgressBar) v.findViewById(Helper.getResId(getActivity(),
				"wan3456_service_center_bar"));
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (webview.canGoBack()) {
					webview.goBack();
				}
			}
		});
		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		webview.getSettings().setDefaultTextEncodingName("UTF-8");
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setWebViewClient(new MyWebClient());
		webview.setWebChromeClient(new MyChromeClient());
		webview.setDownloadListener(new MyDownLoadListener());
		// webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE); //
		// 禁止缓存
		webview.loadUrl(url);

		handler = new Handler() {
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
		return v;
	}

	class MyWebClient extends WebViewClient {

		@Override
		public void onPageFinished(WebView view, String url) {
			if (webview.canGoBack()) {
				back.setVisibility(View.VISIBLE);
				back.setText("上一页");
			} else {
				back.setVisibility(View.GONE);
			}

			super.onPageFinished(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {

			super.onPageStarted(view, url, favicon);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

	}

	
	class MyChromeClient extends WebChromeClient {

		@Override
		public void onConsoleMessage(String message, int lineNumber,
				String sourceID) {
			super.onConsoleMessage(message, lineNumber, sourceID);
		}

		@Override
		public void onCloseWindow(WebView window) {

			super.onCloseWindow(window);
		}

		@Override
		public boolean onCreateWindow(WebView view, boolean isDialog,
				boolean isUserGesture, Message resultMsg) {

			return super.onCreateWindow(view, isDialog, isUserGesture,
					resultMsg);
		}

		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result) {
			// 判断打开qq
			if (message.equals("qq")) {
				Helper.startQQ(getActivity(),
						sharedPreferences.getString("qq", null),
						sharedPreferences);
				result.confirm();
				return true;
			} else {
				return super.onJsAlert(view, url, message, result);
			}

		}

		@Override
		public boolean onJsBeforeUnload(WebView view, String url,
				String message, JsResult result) {

			return super.onJsBeforeUnload(view, url, message, result);
		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message,
				JsResult result) {

			return super.onJsConfirm(view, url, message, result);
		}

		@Override
		public boolean onJsPrompt(WebView view, String url, String message,
				String defaultValue, JsPromptResult result) {

			return super.onJsPrompt(view, url, message, defaultValue, result);
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

	}

	private class MyDownLoadListener implements DownloadListener {

		@Override
		public void onDownloadStart(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength) {
			ToastTool
					.showToast(getActivity(), "开始下载游戏，可到手机的“下载管理”中查看任务.", 2500);
			Helper.download(getActivity(), url);
		}
	}

}
