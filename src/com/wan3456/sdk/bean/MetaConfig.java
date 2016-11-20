package com.wan3456.sdk.bean;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.json.JSONException;
import org.json.JSONObject;


import com.wan3456.sdk.tools.StatusCode;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Base64;

public class MetaConfig {

	private static MetaConfig ysConfig;
	private JSONObject json = null;

	public static MetaConfig instance(Context context) {
		if (ysConfig == null) {
			synchronized (MetaConfig.class) {
				if (ysConfig == null) {
					ysConfig = new MetaConfig(context);
				}
			}
		}
		return ysConfig;
	}

	private MetaConfig(Context context) {
		// 从apk包中获取
		ApplicationInfo appinfo = context.getApplicationInfo();
		String sourceDir = appinfo.sourceDir;
		// 默认放在meta-inf/里
		String key = "META-INF/" + StatusCode.CONFIG_NAME;

		ZipFile zipfile = null;
		try {
			zipfile = new ZipFile(sourceDir);
			Enumeration<?> entries = zipfile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = ((ZipEntry) entries.nextElement());
				String entryName = entry.getName();
				if (entryName.startsWith(key)) {

					InputStream is = zipfile.getInputStream(entry);
					int len = 0;
					byte[] buffer = new byte[1024];
					StringBuffer sb = new StringBuffer();

					while ((len = is.read(buffer)) != -1) {
						sb.append(new String(buffer, 0, len));
					}
					is.close();
					String config = sb.toString();
					String temp = null;
					try {
						byte[] data = Base64.decode(config.getBytes(), 0);
						temp = new String(data, "UTF8");

					} catch (Exception e) {

					}

					if ((temp != null) && (temp.startsWith("{"))
							&& (temp.endsWith("}"))) {

						config = temp;
					}
					JSONObject json_m = new JSONObject(config);
					json = json_m;
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {

			e.printStackTrace();
		} finally {
			if (zipfile != null) {
				try {
					zipfile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		}

	}

	public JSONObject getJson() {

		return json;

	}

	public String getString(String key) {
		try {
			String value = json.getString(key);
			return value;
		} catch (JSONException e) {

			e.printStackTrace();
		}
		return null;
	}

	public boolean getBoolean(String key) {
		try {
			boolean value = json.getBoolean(key);
			return value;
		} catch (JSONException e) {

			e.printStackTrace();
		}
		return false;
	}

	public int getInt(String key) {
		try {
			int value = json.getInt(key);
			return value;
		} catch (JSONException e) {

			e.printStackTrace();
		}
		return 0;
	}

	public double getDouble(String key) {
		try {
			double value = json.getDouble(key);
			return value;
		} catch (JSONException e) {

			e.printStackTrace();
		}
		return 0;
	}

	public long getLong(String key) {
		try {
			long value = json.getLong(key);
			return value;
		} catch (JSONException e) {

			e.printStackTrace();
		}
		return 0;
	}
}
