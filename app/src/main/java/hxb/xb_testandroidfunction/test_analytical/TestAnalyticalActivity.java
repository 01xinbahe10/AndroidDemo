package hxb.xb_testandroidfunction.test_analytical;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import java.io.IOException;
import java.io.InputStream;

import hxb.xb_testandroidfunction.test_analytical.xml_analytical.XmlTest1Analytical;

/**
 * Created by hxb on 2019/1/18
 * 数据解析测试界面 如xml,json等
 */
public class TestAnalyticalActivity extends FragmentActivity{
    private final String xmlHeadPath = "test_analytical/xml/";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(new Runnable() {
            @Override
            public void run() {
                XmlTest1Analytical xmlTest1Analytical = new XmlTest1Analytical();
                try {
                    InputStream in  = getResources().getAssets().open(xmlHeadPath+"test3_blockly.xml");
                    xmlTest1Analytical.draggingVerifyXmlFormat(in);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
