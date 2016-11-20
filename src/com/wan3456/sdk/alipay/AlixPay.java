package com.wan3456.sdk.alipay;

import com.alipay.sdk.app.PayTask;
import com.wan3456.sdk.activity.PayActivity;
import com.wan3456.sdk.tools.StatusCode;



import com.wan3456.sdk.tools.ToastTool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;


@SuppressLint("HandlerLeak")
public class AlixPay {

	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_CHECK_FLAG = 2;
	private Activity context;
	private String info;
	private String sign;
	public AlixPay(Activity context, String info, String sign) {
		this.context = context;
		this.sign = sign;
		this.info = info;

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {

				PayResult payResult = new PayResult((String) msg.obj);

				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				// String resultInfo = payResult.getResult();

				String resultStatus = payResult.getResultStatus();

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					PayActivity.PayAct.pHandler
							.sendEmptyMessage(StatusCode.PAY_STATIC_SUCCESS);
					
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
					
						PayActivity.PayAct.pHandler
								.sendEmptyMessage(StatusCode.PAY_STATIC_ING);
					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						PayActivity.PayAct.pHandler
								.sendEmptyMessage(StatusCode.OTHER_FAIL);
					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				ToastTool.showToast(context, "检查结果为：" + msg.obj, 1000);
				break;
			}
			default:
				break;
			}
		};
	};

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void pay() {
		// 订单
		// String orderInfo = getOrderInfo("测试的商品", "该测试商品的详细描述", "0.01");

		// 对订单做RSA 签名
		// String sign = sign(orderInfo);
		/*
		 * try { // 仅需对sign 做URL编码 sign = URLEncoder.encode(sign, "UTF-8"); }
		 * catch (UnsupportedEncodingException e) { e.printStackTrace(); }
		 */

		// 完整的符合支付宝参数规范的订单信息

		final String payInfo = info + "&sign=\"" + sign + "\"&" + getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(context);

				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * check whether the device has authentication alipay account.
	 * 查询终端设备是否存在支付宝认证账户
	 * 
	 */
	public void check() {
		Runnable checkRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask payTask = new PayTask(context);
				// 调用查询接口，获取查询结果
				boolean isExist = payTask.checkAccountIfExist();

				Message msg = new Message();
				msg.what = SDK_CHECK_FLAG;
				msg.obj = isExist;
				mHandler.sendMessage(msg);
			}
		};

		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();

	}

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(context);
		String version = payTask.getVersion();
		ToastTool.showToast(context, version, 1000);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}
}
