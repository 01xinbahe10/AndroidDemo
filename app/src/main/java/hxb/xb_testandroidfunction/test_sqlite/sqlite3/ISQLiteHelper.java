package hxb.xb_testandroidfunction.test_sqlite.sqlite3;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hxb on 2019/1/29
 */
public interface ISQLiteHelper {

    int NORMAL_TABLE_FIELD = 10;//表字段不变
    int ADD_TABLE_FIELD = 20;//添加表字段
    int DELETE_TABLE_FIELD = 30;//删除表字段
    int UPDATE_DATA_TYPE_TABLE_FIELD = 40;//修改表字段的数据类型

    String DB_NAME_LOG = "logDB";//数库名字

    String TABLE_NAME_LOG = "logMonitoring";
    String T_F_ID = "id";
    String T_F_ACTIVITYNAME = "activityName";
    String T_F_ACTIVITYNAME_NEW = "activityN";//表示字段更改名字

    SQLiteOpenHelper getHelper();
    void carriedOutWritable(SQLiteDataAction action);
    void carriedOutReadable(SQLiteDataAction action);

    /**
     * SQLite初始化回调
     * */
    interface SQLiteInit{
        String databaseName();
        int databaseVersion();
        void databaseCreate(SQLiteDatabase db);
        void databaseUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
    }
    /**
     * SQLite操作动作回调
     * */
    interface SQLiteDataAction{
        void action(SQLiteDatabase db);
        void actionFinally();
        void error(Exception e);
    }

}
