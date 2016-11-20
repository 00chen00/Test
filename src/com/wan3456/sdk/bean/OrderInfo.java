package com.wan3456.sdk.bean;

public class OrderInfo {
	public String  orderId;//订单号
	public String orderAmount;//支付金额
	public String payWayName;//支付渠道名称

	@Override
	public String toString() {
		return "OrderInfo [orderId=" + orderId + ", orderAmount=" + orderAmount
				+ ", payWayName=" + payWayName + "]";
	}
	public String getPayWayName() {
		return payWayName;
	}
	public void setPayWayName(String payWayName) {
		this.payWayName = payWayName;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getOrderAmount() {
		return orderAmount;
	}
	public void setOrderAmount(String orderAmount) {
		this.orderAmount = orderAmount;
	}
		
	
	
}
