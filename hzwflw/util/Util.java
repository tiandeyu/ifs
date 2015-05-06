package ifs.hzwflw.util;

import java.util.List;

public class Util {
	public static boolean isEmpty(List list){
		return list == null || list.isEmpty();
	}
	public static boolean isEmpty(String str){
		return str == null || str.isEmpty();
	}
}
