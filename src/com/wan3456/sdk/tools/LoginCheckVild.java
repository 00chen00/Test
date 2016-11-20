package com.wan3456.sdk.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;

@SuppressLint({ "SimpleDateFormat", "DefaultLocale" })
public class LoginCheckVild {
	/**
	 * 检测用户名，密码是否符合规则
	 * 
	 * @param input
	 * @param type
	 * @return
	 */
	public static boolean checkValid(String input, int type) {
		Pattern pattern = null;
		switch (type) {
		case StatusCode.CHECK_NAME:
			pattern = Pattern.compile("^[a-zA-Z0-9_]{3,18}$");
			if (!pattern.matcher(input).matches()) {
				return false;
			}
			break;

		case StatusCode.CHECK_PASSWORD:
			String a = "^[a-zA-Z0-9]" + "{6,20}$";
			pattern = Pattern.compile(a);
			if (!pattern.matcher(input).matches()) {
				return false;
			}

			return true;
			// break;

		}
		return true;

	}

	/**
	 * 检测qq是否符合规则(第一位非0，最长15位)
	 * 
	 * @param qq
	 * @return
	 */
	public static boolean isQQ(String qq) {
		boolean is = true;
		Pattern p = Pattern

		.compile("^[1-9]{1}[0-9]{4,14}$");

		Matcher m = p.matcher(qq);
		is = m.matches();

		return is;
	}

	/**
	 * 检测手机号码是否符合规则
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles) {
		boolean is = true;
		Pattern p = Pattern

		.compile("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$");

		Matcher m = p.matcher(mobiles);
		is = m.matches();

		return is;
	}

	/**
	 * 检测验证码是否符合规则
	 * 
	 * @param code
	 * @return
	 */

	public static boolean isCheckNO(String code) {
		if (code.length() == 6) {
			return true;
		}

		return false;
	}

	/**
	 * 返回1970至今秒数
	 */

	public static String getCurrTime() {

		// SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		// Date date = new Date(System.currentTimeMillis());
		// String a = df.format(date);
		long a = System.currentTimeMillis();
		a = a / 1000;
		return String.valueOf(a);

	}

	/**
	 * 时间格式转换
	 * 
	 * @param time
	 * @return
	 */

	@SuppressLint("SimpleDateFormat")
	public static String getTurnTime(String time) {
		String a = null;
		SimpleDateFormat s = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date;
		try {
			date = s.parse(time);
			a = s1.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return a;

	}

	/**
	 * 返回相差小时数
	 * 
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public static long compare(String beginTime, String endTime) {

		long hours = 0;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date d1 = df.parse(beginTime);
			Date d2 = df.parse(endTime);
			long diff = d2.getTime() - d1.getTime();
			hours = diff / (1000 * 60 * 60);// 小时
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hours;

	}

	/**
	 * 返回相差秒数
	 * 
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public static long compare2(String beginTime, String endTime) {

		long hours = 0;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date d1 = df.parse(beginTime);
			Date d2 = df.parse(endTime);
			long diff = d2.getTime() - d1.getTime();
			hours = diff / (1000);// 秒数
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hours;

	}

	/**
	 * Md5 32位 or 16位 加密
	 * 
	 * @param plainText
	 * @return 32位加密
	 */
	public static String Md5(String plainText) {
		StringBuffer buf = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			// Log.e("555","result: " + buf.toString());//32位的加密
			// Log.e("555","result: " + buf.toString().substring(8,24));//16位的加密

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buf.toString();
	}

	/**
	 * 转换成十六进制字符串
	 * 
	 * @param b
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public static String byte2Hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
			if (n < b.length - 1)
				hs = hs + ":";
		}
		return hs.toUpperCase();
	}

}
