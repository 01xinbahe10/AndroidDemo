package hxb.xb_testandroidfunction.test_sqlite;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import hxb.xb_testandroidfunction.test_gif_anim.MyApplication;
import hxb.xb_testandroidfunction.test_sqlite.db_controller.Sqlite1DBControl;
import hxb.xb_testandroidfunction.test_sqlite.db_controller.Sqlite3UseDBController;
import hxb.xb_testandroidfunction.test_sqlite.sqlite3.ISQLiteHelper;
import hxb.xb_testandroidfunction.test_sqlite.sqlite3.SQLiteDataContext;

/**
 * Created by hxb on 2019/1/29
 * 测试SQLite
 */
public class TestSQLite2Activity extends FragmentActivity {

    private final String TAG = "TestSQLite2Activity";
    private Handler handler = new Handler();
    private ISQLiteHelper mISqLiteHelper;
    private Sqlite3UseDBController mController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(initView());

        testFun2();
    }

    @Override
    protected void onDestroy() {
        if (null != handler){
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    private View initView(){
        LinearLayout.LayoutParams ll_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayout.setLayoutParams(ll_lp);

        LinearLayout.LayoutParams tv_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView tv = new TextView(this);
        tv.setText("生成数据");
        tv.setTextSize(24);
        tv.setTextColor(Color.parseColor("#000000"));
        tv.setLayoutParams(tv_lp);

        linearLayout.removeAllViews();
        linearLayout.addView(tv);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fun2GenerateData1();
            }
        });


        return linearLayout;
    }

    /**
     * 该方法主要测试DB的自定义路径
     * */
    private void testFun1(){
        boolean sdExist = android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState());
        if (!sdExist) {//如果不存在,
            Log.e("SD卡管理：", "SD卡不存在，请加载SD卡");
        } else {//如果存在,获取sd卡路径
            String dbDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
            dbDir += "/hxbDB";//数据库所在目录

//            DatabaseContext databaseContext = DatabaseContext.init(this).setFilePath(dbDir).create();
//            DatabaseContext databaseContext = DatabaseContext.init(this).create();//不设置路径默认 /data/data/..
//            mISqLiteHelper = SQLiteAssetHelper.init(databaseContext);

            SQLiteDataContext sqLiteDataContext = SQLiteDataContext.init(this).setFilePath(dbDir).create();
            mController = Sqlite3UseDBController.getDB(sqLiteDataContext);
        }


//        //插入数据
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                mController.insert0();
//
//            }
//        }).start();
//        //检测数据是否正确
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                mController.query0_1();
//            }
//        }).start();
        //跟上面的线程同时测试，验证是否有多线程保护
        new Thread(new Runnable() {
            @Override
            public void run() {
                mController.query0_2();

            }
        }).start();
    }


    /**
     * 测试数据库版本升级
     * */
    private void testFun2(){
        Sqlite1DBControl.getDB(MyApplication.getAppContext());
    }


    private void fun2GenerateData1(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Sqlite1DBControl sqlite1DBControl = Sqlite1DBControl.getDB(MyApplication.getAppContext());
                for (int i = 0; i < 60; i++) {
                    boolean is = sqlite1DBControl.saveData((i + 1), ((i + 1) + " name"), (int) (1 + Math.random() * (150 - 1 + 1)));
                    Log.e(TAG, "run: 是否存储成功 "+is );
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TestSQLite2Activity.this,"数据生成完成",Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).start();
    }


}
