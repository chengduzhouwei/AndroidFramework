package org.zw.android.framework.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public final class NumberUtils {

	public static final String NUMBER_PATTERN 				= "\\d+";
	public static final String DEFAULT_MONEY_FORMAT 		= "###,##0.00";
	public static final DecimalFormat FORMAT_DIS			= new DecimalFormat("#.00") ;

	private NumberUtils() {
		
	}

	public static boolean isNumber(String source) {
		return StringUtils.matches(source, NUMBER_PATTERN);
	}
	
	public static String formatDistance(double distance){
		return FORMAT_DIS.format(distance) ;
	}
	
	public static String moneyFormater(Double money){
		return moneyFormater(money,NumberUtils.DEFAULT_MONEY_FORMAT);
	}
	
	public static String moneyFormater(Double money, String format) {

		if (StringUtils.isEmpty(format)) {
			return "0.00";
		}
		
		if (money == null) {
			money = 0.00;
		}
		
		return new DecimalFormat(format).format(money);
	}
	
	public static double retain(double decimal, int scale) {
		BigDecimal bg = new BigDecimal(decimal);
		return bg.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public static long toLong(String source) {

		if (StringUtils.isEmpty(source)) {
			return 0;
		}
		
		return Long.parseLong(source);
	}

	public static int toInt(String source) {

		if (StringUtils.isEmpty(source)) {
			return 0;
		}
		
		return Integer.parseInt(source);
	}

	public static double toDouble(String source) {

		if (StringUtils.isEmpty(source)) {
			return 0;
		}
		return Double.parseDouble(source);
	}

	public static boolean toBoolean(String source) {

		if (StringUtils.isEmpty(source)) {
			return false;
		}
		
		return Boolean.parseBoolean(source);
	}

	public static double valueOf(Double value) {
		return (value == null ? 0.0 : value.doubleValue());
	}

	public static boolean larger(Number source, double num) {
		return (source != null && source.doubleValue() > num);
	}

	public static boolean smaller(Number source, double num) {
		return (source != null && source.doubleValue() < num);
	}
}
