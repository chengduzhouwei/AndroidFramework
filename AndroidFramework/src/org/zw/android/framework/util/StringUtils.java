package org.zw.android.framework.util;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public final class StringUtils {

	// Email正则表达式
	public static final String EMAIL_PATTERN 				= "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
	// 手机号正则表达式
	public static final String PHONE_PATTERN 				= "^(\\+?\\d+)?1[3456789]\\d{9}$";
	// 密码格式验证
	public static final String PASSWORD_PATTERN				= "^[a-zA-Z0-9_]{6,20}$" ;
	// 特殊字符串
	public static final String SPECIAL_STRING_PATTERN		= "[`~!@#$%^&*()+=|{}':;'\",\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";

	private StringUtils() {
		
	}

	public static boolean isEmpty(String source) {

		if (source == null || "".equals(source.trim()))
			return true;

		for (int i = 0 ,length = source.length(); i < length; i++) {
			
			char c = source.charAt(i);
			
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		
		return true ;
	}

	public static boolean matches(String source, String pattern) {

		if (source == null || !source.matches(pattern)) {
			return false;
		}
		
		return true;
	}
	
	public static boolean isSpecialString(String source){
		return matches(source, SPECIAL_STRING_PATTERN);
	}

	public static boolean isEmail(String source) {
		return matches(source, EMAIL_PATTERN);
	}

	public static boolean isPhone(String source) {
		return matches(source, PHONE_PATTERN);
	}
	
	public static boolean isPassword(String source){
		return matches(source, PASSWORD_PATTERN);
	}
	
	public static boolean isHasEmptyChar(String source){
		
		if(source == null){
			return true ;
		}
		
		return source.trim().indexOf(' ') >= 0 ;
	}

	public static String toNotNullString(String source) {
		return source == null ? "" : source;
	}
	
	public static String arrayToString(String[] array, String separator) {

		if (array == null || array.length == 0) {
			return "";
		}
		StringBuilder result = new StringBuilder();
		for (String temp : array) {
			result.append(temp).append(separator);
		}
		return result.substring(0, result.length() - 2);
	}

	public static String[] toArray(String source) {

		if (isEmpty(source)) {
			return null;
		}
		return source.split(",");
	}

	public static String capitalize(String str) {

		if (str == null || str.length() == 0) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str.length());
		sb.append(Character.toUpperCase(str.charAt(0)));
		sb.append(str.substring(1));
		return sb.toString();
	}

	public static String getTimeName() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		return dateFormat.format(date);
	}

	public static boolean larger(String source, int length) {
		return (source != null && source.length() > length);
	}

	public static String toString(Object source) {
		return (source == null ? null : source.toString());
	}

}
