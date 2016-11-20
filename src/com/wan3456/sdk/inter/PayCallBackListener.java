package com.wan3456.sdk.inter;

import com.wan3456.sdk.bean.OrderInfo;



public interface PayCallBackListener {
	/**
	 * 支付返回
	 * @param code
	 * @param info
	 */
	public void callback(int code, OrderInfo info);
}
