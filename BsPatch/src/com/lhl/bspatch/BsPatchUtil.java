package com.lhl.bspatch;

/**
 * �ϲ�������
 * @author 
 *
 */
public class BsPatchUtil {

	public static native int patch(String oldApk, String newApk, String patch);

}
