package com.wan3456.sdk.tools;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Iterator;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.aapache.commons.codec.binary.Base64;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * 3des 加/解密处理类
 * @author Administrator
 *
 */
public class DesTool {
	public static final String Tag = "DesTool";
	public static final String PASSWORD_KEY = "5l388s0*pvzm5cfuf81evnh$";

	/**
	 * 加密
	 * 
	 * @param keybyte
	 * @param src
	 * @return
	 */
	@SuppressLint("TrulyRandom")
	public static byte[] encryptMode(byte[] keybyte, byte[] src) {
		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, "DESede");
			// 加密
			Cipher c1 = Cipher.getInstance("DESede");
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			return c1.doFinal(src);// 在单一方面的加密或解密
		} catch (java.security.NoSuchAlgorithmException e1) {
			// TODO: handle exception
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	/**
	 * 解密
	 * 
	 * @param keybyte
	 * @param src
	 * @return
	 */
	public static byte[] decryptMode(byte[] keybyte, byte[] src) {
		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, "DESede");
			// 解密
			Cipher c1 = Cipher.getInstance("DESede");
			c1.init(Cipher.DECRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			// TODO: handle exception
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	/**
	 * 对字符串加密
	 * 
	 * @param pwd
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getDesBefor(String pwd) {
		String result = "";
		byte[] utfByte;
		try {
			utfByte = pwd.getBytes(HTTP.UTF_8);
			byte[] keyByte = PASSWORD_KEY.getBytes(HTTP.UTF_8);
			result = new String(Base64.encodeBase64(encryptMode(keyByte,
					utfByte)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 对字符串解密
	 * 
	 * @param encodePwd
	 * @return
	 */
	public static String getDesAfter(String encodePwd) {
		String result = "";
		byte[] base64Pwd = Base64.decodeBase64(encodePwd);
		// 生成密钥
		byte[] keyByte;
		try {
			keyByte = PASSWORD_KEY.getBytes(HTTP.UTF_8);
			String algorithm = "DESede"; // 定义 加密算法,可用 DES,DESede,Blowfish
			SecretKey deskey = new SecretKeySpec(keyByte, algorithm);
			// 解密
			Cipher c1 = Cipher.getInstance(algorithm);
			c1.init(Cipher.DECRYPT_MODE, deskey);
			byte[] pwdByte = c1.doFinal(base64Pwd);
			result = new String(pwdByte);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 加解密及特殊字符替换
	 * 
	 * @param tag
	 * @param mes
	 * @return
	 */
	public static String replaceTool(int tag, String mes) {

		if (tag == StatusCode.DES_BEFOR) {
			mes = getDesBefor(mes);
			mes = mes.replace("=", "-");
			mes = mes.replace("+", "_");
			mes = mes.replace("/", ".");
			return mes;
		}
		if (tag == StatusCode.DES_AFTER) {
			mes = mes.replace("-", "=");
			mes = mes.replace("_", "+");
			mes = mes.replace(".", "/");
			mes = getDesAfter(mes);
			return mes;
		}
		return "";
	}

	/**
	 * 校验对比md5值
	 * @param json
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean checkMd5(JSONObject json) {
		String beforMes = "";
		Iterator it = json.keys();
		while (it.hasNext()) {
			String key = it.next().toString();
			if (!key.equals(StatusCode.CONFIG_NAME_SIGN)) {
				Log.i(Tag, ">>>>>>>>1");
				try {
					Log.i(Tag, ">>>>>>>>2");
					String a = "&" + key + "=" + json.getString(key);
					beforMes = beforMes + a;
					Log.i(Tag, beforMes);
				} catch (JSONException e) {
					Log.i(Tag, ">>>>>>>>3");
					e.printStackTrace();
					return false;
				}
			}

		}
		Log.i(Tag, ">>>>>>>>4");

		try {
			beforMes = beforMes.substring(1, beforMes.length()) + "&"
					+ PASSWORD_KEY;
			if (Md5(beforMes).equals(
					json.getString(StatusCode.CONFIG_NAME_SIGN))) {
				return true;
			} else {

				return false;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * Md5 32位 or 16位 加密
	 * 
	 * @param plainText
	 * @return 32位加密
	 */
	public static String Md5(String mes) {
		StringBuffer buf = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(mes.getBytes());
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

			e.printStackTrace();
		}

		return buf.toString();
	}
}
