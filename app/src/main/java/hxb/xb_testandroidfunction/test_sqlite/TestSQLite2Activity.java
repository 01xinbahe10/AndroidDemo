package hxb.xb_testandroidfunction.test_sqlite;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;

import hxb.xb_testandroidfunction.test_sqlite.db_controller.Sqlite3UseDBController;
import hxb.xb_testandroidfunction.test_sqlite.sqlite3.ISQLiteHelper;
import hxb.xb_testandroidfunction.test_sqlite.sqlite3.SQLiteDataContext;

/**
 * Created by hxb on 2019/1/29
 * 测试SQLite
 */
public class TestSQLite2Activity extends FragmentActivity {

    private final String TAG = "TestSQLite2Activity";
    private ISQLiteHelper mISqLiteHelper;
    private Sqlite3UseDBController mController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
}
