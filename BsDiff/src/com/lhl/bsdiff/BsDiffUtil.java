package com.lhl.bsdiff;

/**
 * 产生差异工具类
 * 
 * @author 
 * 
 */
public class BsDiffUtil {

	public static native int genDiff(String oldApk, String newApk, String patch);

}
