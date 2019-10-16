package hxb.xb_testandroidfunction.test_arithmetic;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;



/**
 * 算法题
 * */
public class TestArithmeticActivity extends FragmentActivity {
    int[] analogData = {1,2,0};
    int[] afterTheRainData = {0,2,0,0,2,1,0,1,3,2,1,2,1};
    int[] afterTheRainData2= {0,1,0,2,1,0,1,3,2,1,1,4,10,55,4,1,44,4,54,2,1};
    int[] afterTheRainData3 = {0,1,0,2,1,0,1,3,2,1,2,1};
    int[] afterTheRainData4 = {1,-1,2,-1,2,1,7,-45,45,4,4,47,-45,454,454,454,454,453,454,454,454,-45,24,73,3,89,16,146,166,16,16,5,11,32,22,235,-8,4};
    public  String TAG = "hxb";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread(new Runnable() {
            @Override
            public void run() {
//                Log.e("TAG", " 求最小正整数："+findTheSmallestPositiveInteger(analogData) );
                Log.e(TAG, " 求接雨水算法："+Arithmetic.afterTheRain2(afterTheRainData4) );
            }
        }).start();

    }



}
