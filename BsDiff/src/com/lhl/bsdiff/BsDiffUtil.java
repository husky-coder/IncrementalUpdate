package com.lhl.bsdiff;

/**
 * �������칤����
 * 
 * @author 
 * 
 */
public class BsDiffUtil {

	public static native int genDiff(String oldApk, String newApk, String patch);

}
