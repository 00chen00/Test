package com.wan3456.sdk.inter;

public interface UserCallBackListener {
	public void onLoginSuccess(int uid, int tstamp, String sign);
	public void onLoginFailed(String reason);
	public void onLogout();
}
