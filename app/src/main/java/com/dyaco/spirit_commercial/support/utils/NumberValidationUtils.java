package com.dyaco.spirit_commercial.support.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberValidationUtils {
	
	private static boolean isMatch(String regex, String original){
		if (original == null || original.trim().equals("")) {
            return false;
        }
		Pattern pattern = Pattern.compile(regex);
		Matcher isNum = pattern.matcher(original);
		return isNum.matches();
	}
 
	public static boolean isPositiveInteger(String original) {
		return isMatch("^\\+{0,1}[1-9]\\d*", original);
	}
 
	public static boolean isNegativeInteger(String original) {
		return isMatch("^-[1-9]\\d*", original);
	}
 
	public static boolean isWholeNumber(String original) {
		return isMatch("[+-]{0,1}0", original) || isPositiveInteger(original) || isNegativeInteger(original);
	}
	
	public static boolean isPositiveDecimal(String original){
		return isMatch("\\+{0,1}\\.[1-9]*|\\+{0,1}[1-9]\\d*\\.\\d*", original);
	}
	
	public static boolean isNegativeDecimal(String original){
		return isMatch("^-[0]\\.[1-9]*|^-[1-9]\\d*\\.\\d*", original);
	}
	
	public static boolean isDecimal(String original){
		return isMatch("[-+]{0,1}\\d+\\.\\d*|[-+]{0,1}\\d*\\.\\d+", original);
	}
	
	public static boolean isRealNumber(String original){
		return isPositiveInteger(original) || isPositiveDecimal(original);
	}
 
}
