package com.wan3456.sdk;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class YSReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			long myDwonloadID = intent.getLongExtra(
					DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			SharedPreferences sharedPreferences = context.getSharedPreferences(
					"yssdk_info", Context.MODE_PRIVATE);
			long refernece = sharedPreferences.getLong("plato", 0);
			if (refernece == myDwonloadID) {
				Log.i("wan3456", "YSReceiver:receiver download end>>>>>>>>>");
				String serviceString = Context.DOWNLOAD_SERVICE;
				DownloadManager dManager = (DownloadManager) context
						.getSystemService(serviceString);
				Intent install = new Intent(Intent.ACTION_VIEW);
				Cursor c = dManager.query(new DownloadManager.Query()
						.setFilterById(refernece));
				c.moveToFirst();
				String path = c.getString(c
						.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
				install.setDataAndType(Uri.parse(path),
						"application/vnd.android.package-archive");
				install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(install);
			}
		} catch (Exception e) {
			Log.i("wan3456", "YSReceiver:open apk error>>>>>>>>>");
		}

	}
}
