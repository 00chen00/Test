package com.wan3456.sdk.tools;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class ToastTool {
	private static Toast mToast;
    private static Handler mHandler = new Handler();
    private static Runnable r = new Runnable() {
        public void run() {
            mToast.cancel();
        }
    };

    private static void show(Context mContext, String text, int duration) {
       
        mHandler.removeCallbacks(r);
        if (mToast != null)
            mToast.setText(text);
        else
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        mHandler.postDelayed(r, duration);

        mToast.show();
    }

    public static void showToast(Context mContext, String mes, int duration) {
        show(mContext, mes, duration);
    }

}
