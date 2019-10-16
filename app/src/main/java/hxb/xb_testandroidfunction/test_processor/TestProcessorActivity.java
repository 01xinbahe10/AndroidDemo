package hxb.xb_testandroidfunction.test_processor;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.example.compiler_processor.PermissionCheck;

import hxb.xb_testandroidfunction.R;
import permission.PermissionUtil;

/**
 * Created by hxb on 2019-07-02.
 * 测试AbstractProcessor 使用
 */
public class TestProcessorActivity extends AppCompatActivity {

    @PermissionCheck
    private View m;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_processor);
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        int resultCode = PermissionUtil.checkPermission(this,permissions);
        if (resultCode != PermissionUtil.SUCCESS){
            this.requestPermissions(permissions,1);
        }
    }


    public void permissCheck(){}
}
