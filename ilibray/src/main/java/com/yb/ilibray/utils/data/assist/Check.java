package com.yb.ilibray.utils.data.assist;

import java.util.Collection;
import java.util.Map;

/**
 * 辅助判断
 * 
 * @author mty
 * @date 2013-6-10下午5:50:57
 */
public class Check {

	public static boolean isEmpty(CharSequence str) {
		return isNull(str) || str.length() == 0;
	}

	public static boolean isEmpty(Object[] os) {
		return isNull(os) || os.length == 0;
	}

	public static boolean isEmpty(Collection<?> l) {
		return isNull(l) || l.isEmpty();
	}

	public static boolean isEmpty(Map<?, ?> m) {
		return isNull(m) || m.isEmpty();
	}

	public static boolean isNull(Object o) {
		return o == null;
	}

	public static <T> T checkNotNull(T reference) {
		if (reference == null) {
			throw new NullPointerException();
		}
		return reference;
	}
	public static <T> T checkNotNull(T reference,String describe) {
		if (reference == null) {
			throw new NullPointerException(describe);
		}
		return reference;
	}

	public static String checkReplace(String str) {
		if (str==null){
			return "";
		}
		return str;
	}
	public static String checkReplace(String str, String rep) {
		if (str==null){
			return rep;
		}
		return str;
	}
}
