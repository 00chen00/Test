package com.wan3456.sdk.tools;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetTool {
	static Context context;

	/**
	 * HttpGet
	 * 
	 * @param url
	 * @return
	 */
	public static String getUrlContent(String url) {

		HttpClient hc = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);

		HttpResponse rp;
		hc.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
				10000);
		Log.i("wan3456", "NetTool getUrlContent: connection>>>>>>>>>>>>>>");
		try {
			rp = hc.execute(get);

			Log.i("wan3456", "NetTool getUrlContent: statusCode>>>>>" + rp.getStatusLine().getStatusCode());
			if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

				return EntityUtils.toString(rp.getEntity()).trim();

			} else {

				return null;

			}
		} catch (ClientProtocolException e) {

			Log.i("wan3456", "NetTool getUrlContent :error ClientProtocolException >>>>>>>>>>>>");
			e.printStackTrace();
			return null;
		} catch (IOException e) {

			Log.i("wan3456", "NetTool getUrlContent :error IOException >>>>>>>>>>>>>");
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * HttpPost
	 * 
	 * @param url
	 * @param list
	 * @return
	 */

	public static String postUrlContent(String url,
			List<HashMap<String, String>> list) {
		{
			String uriAPI = url;// Post方式没有参数在这里
			String result = null;
			HttpPost httpRequst = new HttpPost(uriAPI);// 创建HttpPost对象
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (int i = 0; i < list.size(); i++) {
				HashMap<String, String> map = list.get(i);
				params.add(new BasicNameValuePair(map.get("key"), map
						.get("value")));
				Log.i("wan3456", "NetTool postUrlContent: key" + map.get("key").trim() + "," + "value="
						+ map.get("value").trim());
			}
			try {
				httpRequst.setEntity(new UrlEncodedFormEntity(params,
						HTTP.UTF_8));
				httpRequst.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
				HttpResponse httpResponse = new DefaultHttpClient()
						.execute(httpRequst);
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					HttpEntity httpEntity = httpResponse.getEntity();
					result = EntityUtils.toString(httpEntity);// 取出应答字符串
				}
			} catch (UnsupportedEncodingException e) {
			
				e.printStackTrace();
			
			} catch (ClientProtocolException e) {
			
				e.printStackTrace();
			
			} catch (IOException e) {

				e.printStackTrace();
			
			}
			return result;
		}
	}

	/**
	 * 检测网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {

		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivity != null)

		{

			NetworkInfo[] info = connectivity.getAllNetworkInfo();

			if (info != null)

				for (int i = 0; i < info.length; i++)

					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;

	}

}
