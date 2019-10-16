package hxb.xb_testandroidfunction.test_log_monitoring;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

/**
 * Created by hxb on 2019/3/6.
 * 测试日志监控
 */
public class TestLogMonitoringActivity extends FragmentActivity {
    private final String TAG = "TestLogMonitorActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: 2019/4/9 测试异常捕获测试
        Boolean bool = null;

//        if (bool) {
//            Log.e(TAG, "onCreate: ------------>");
//        }

    }

}
