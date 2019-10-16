package hxb.xb_testandroidfunction.test_jni;

import android.content.Context;

/**
 * Created by hxb on 2018/10/31
 */
public class NativeUtils {
    public static native String reverseWords(String s);
    public static native String parseP2PID(Context context, byte[] raw, String rid);
}
