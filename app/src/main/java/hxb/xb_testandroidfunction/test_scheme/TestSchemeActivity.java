package hxb.xb_testandroidfunction.test_scheme;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import java.util.List;

/**
 * Created by hxb on 2018/10/22
 * 测试 自定义URL使用Scheme方式唤起其它app中的Activity或App
 */
public class TestSchemeActivity extends FragmentActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        ConstraintLayout constraintLayout = new ConstraintLayout(this);
        constraintLayout.setLayoutParams(layoutParams);

        ConstraintLayout.LayoutParams buttonLayoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.topToTop = 0;
        buttonLayoutParams.leftToLeft = 0;
        buttonLayoutParams.rightToRight = 0;
        Button button = new Button(this);
        button.setText("点击跳转TestScheme1Activity");
        button.setTextColor(Color.parseColor("#000000"));
        button.setLayoutParams(buttonLayoutParams);

        constraintLayout.addView(button);

        setContentView(constraintLayout);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //APP内部/外部跳转（如果有两个以上的app是相同的Uri,系统会跳出弹窗让用户选择）
                Uri uri0 = Uri.parse("xb://test_function:1/test_scheme");
                Intent intent = new Intent(Intent.ACTION_VIEW,uri0 );
                startActivity(intent);

                //唤起其它APP(TestProject)中的TestSchemaActivity
//                Uri uri1 = Uri.parse("xb://test_project:1/test");
//                Intent intent1 = new Intent(Intent.ACTION_VIEW,uri1);
//                startActivity(intent1);

                //唤起其它APP并指定界面
               /* Intent intent2 = new Intent();
                intent2.setClassName("com.example.testjpeg","com.example.testjpeg.SchemeAct");
                startActivity(intent2);*/
            }
        });
    }


    /**
     * 通过scheme uri判断某个app是否安装
     *
     * @return
     */
    public static boolean checkUrlScheme(Activity activity,Uri schemeUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, schemeUri);

        PackageManager packageManager = activity.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        return !activities.isEmpty();
    }

}
