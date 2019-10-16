package hxb.xb_testandroidfunction.test_scheme;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import android.widget.TextView;

import hxb.xb_testandroidfunction.R;

/**
 * Created by hxb on 2018/10/22
 * 测试界面1
 */
public class TestScheme1Activity extends FragmentActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("测试Scheme界面1");
        textView.setTextColor(ContextCompat.getColor(this,R.color.colorAccent));
        textView.setTextSize(36);
        setContentView(textView);
    }

}
