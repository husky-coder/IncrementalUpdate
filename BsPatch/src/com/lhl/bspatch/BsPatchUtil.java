package com.lhl.bspatch;

/**
 * 合并工具类
 * @author 
 *
 */
public class BsPatchUtil {

	public static native int patch(String oldApk, String newApk, String patch);

}
