package hxb.xb_testandroidfunction.test_about_recyclerview.tvlist;

/**
 * Created by hxb on  2020/10/20
 */
public class DebugLog {
    private static final boolean DEBUG = false;
    public static void e(String TAG,String msg){
        if (DEBUG){
            android.util.Log.e(TAG, msg);
        }
    }

    public static void v(String TAG,String msg){
        if (DEBUG){
            android.util.Log.v(TAG, msg);
        }
    }

    public static void i(String TAG,String msg){
        if (DEBUG){
            android.util.Log.i(TAG, msg);
        }
    }

    public static void d(String TAG,String msg){
        if (DEBUG){
            android.util.Log.d(TAG, msg);
        }
    }
}
