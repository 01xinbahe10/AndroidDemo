package hxb.xb_testandroidfunction.test_sqlite.db_controller;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import hxb.xb_testandroidfunction.test_sqlite.sqlite3.ISQLiteHelper;
import hxb.xb_testandroidfunction.test_sqlite.sqlite3.SQLiteAssetHelper;

/**
 * Created by hxb on 2019-06-18.
 * sqlite3 文件包使用 模板
 */
public class Sqlite3UseDBController {
    private static final String TAG = "Sqlite3UseDBController";
    private static final String DBName = "logDB";
    private ISQLiteHelper mILog = null;
    private static final int mUpgradeSate = ISQLiteHelper.UPDATE_DATA_TYPE_TABLE_FIELD;//更新的类型
    private Cursor mCursor = null;

    interface DBVersion0 {
        int version = 0;

        String tableName = "logMonitoring";
        String activityName = "activityName";
    }

    interface DBVersion1{
        int version = 1;

        String tableName = "logMonitoring";
        String activityName = "activityN";//是activityName 改名之后的名字
        String date = "date";//新增字段
    }

    public static Sqlite3UseDBController getDB(Context context) {
        Sqlite3UseDBController controller = new Sqlite3UseDBController();
        controller.mILog = SQLiteAssetHelper.init(context, new ISQLiteHelper.SQLiteInit() {
            @Override
            public String databaseName() {
                return DBName;
            }

            @Override
            public int databaseVersion() {
                return DBVersion0.version;
            }

            @Override
            public void databaseCreate(SQLiteDatabase db) {
                StringBuilder tableField = new StringBuilder();
                tableField.append(DBVersion0.activityName + " varchar(255)");
                //db.execSQL("create table if not exists "+ISQLiteHelper.TABLE_NAME_LOG + "(id integer primary key autoincrement,activityName varchar(255))");
                String sql = "create table if not exists " + DBVersion0.tableName + " (id integer primary key autoincrement," + tableField.toString() + ")";
                db.execSQL(sql);
            }

            @Override
            public void databaseUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

                switch (newVersion){
                    case DBVersion1.version:
                        controller.UpgradeDBVersion1(ISQLiteHelper.ADD_TABLE_FIELD,db);//添加表字段
                        controller.UpgradeDBVersion1(ISQLiteHelper.UPDATE_DATA_TYPE_TABLE_FIELD,db);//更改表字段
                        break;
                }
            }
        });


        return controller;
    }

    private void UpgradeDBVersion1(int upgradeSate,SQLiteDatabase db){
        switch (upgradeSate) {
            /**
             * 增加表字段
             * */
            case ISQLiteHelper.ADD_TABLE_FIELD:
                //给该表添加表字段
                db.execSQL("alter table " + DBVersion1.tableName + " add column "+DBVersion1.date+" integer");
                break;
            /**
             * 删除表字段
             * */
            case ISQLiteHelper.DELETE_TABLE_FIELD:
                String temporaryTableNameD = DBVersion1.tableName + 2 + "";//创建临时表名
                //去除原表名中间的某些字段时，则将临时表复制原表时不进行 某些表字段 选择就行。
                db.execSQL("create table " + temporaryTableNameD + " as select activityName from " + DBVersion1.tableName + " where 1 = 1");
                //删除原表
                db.execSQL("drop table if exists " + DBVersion1.tableName);
                //将临时表名 改成 原表名
                db.execSQL("alter table " + temporaryTableNameD + " rename to " + DBVersion1.tableName);
                break;

            /**
             * 更改表字段或是表字段的数据类型
             * */
            case ISQLiteHelper.UPDATE_DATA_TYPE_TABLE_FIELD:
                String temporaryTableNameR = DBVersion1.tableName + 2 + "";
                //把原表改成另外一个名字作为暂存表
                db.execSQL("alter table " + DBVersion1.tableName + " rename to " + temporaryTableNameR);
                //如果需要，可以删除原表的索引 ix_name:关联表的表字段
                //db.execSQL("drop index "+"ix_name");
                //用原表的名字创建新表
                db.execSQL("create table " + DBVersion1.tableName + " (id integer primary key autoincrement,"+DBVersion1.activityName+" varchar(255))");
                //如果需要，可以创建新表的索引
                //db.execSQL("create index "+"ix_name"+" on "+mUpgradeTableName+" (id)");
                //将暂存表数据写入到新表，很方便的是不需要去理会自动增长的 ID
                db.execSQL("insert into " + DBVersion1.tableName + " select id,"+DBVersion0.activityName+" from " + temporaryTableNameR);
                //删除暂存表
                db.execSQL("drop table if exists " + temporaryTableNameR);
                break;
        }
    }










    public void insert0() {
        mILog.carriedOutWritable(new ISQLiteHelper.SQLiteDataAction() {
            @Override
            public void action(SQLiteDatabase db) {
                String sql = "insert into " + DBVersion0.tableName + "(activityName) values(?)";
                for (int i = 0; i < 50; i++) {
                    db.execSQL(sql, new Object[]{"系统默认 " + i});
                    Log.e(TAG, "run: 插入数据： " + i);
                }
            }

            @Override
            public void actionFinally() {

            }

            @Override
            public void error(Exception e) {

            }
        });
    }


    /**
     * 测试 表字段改名 增加字段名
     */
    public void query0_1() {
        mILog.carriedOutReadable(new ISQLiteHelper.SQLiteDataAction() {
            @Override
            public void action(SQLiteDatabase db) {
                String sql = "select * from " + DBVersion0.tableName;
                mCursor = db.rawQuery(sql, null);
                while (mCursor.moveToNext()) {
                    int i = 1;
                    try {
                        i = mCursor.getInt(mCursor.getColumnIndex("date"));
                    } catch (Exception e) {
                        i = -1;
                    }

                    String activityName = "没有";
                    try {
                        activityName = mCursor.getString(mCursor.getColumnIndex("activityName"));
                    } catch (Exception e) {
                        activityName = "没有2";
                    }

                    String activityN = "没没";
                    try {
                        activityN = mCursor.getString(mCursor.getColumnIndex("activityN"));
                    } catch (Exception e) {
                        activityN = "没没2";
                    }

                    Log.e(TAG, "run: -----2222222222222-------> activityName:" + activityName + "  activityN: " + activityN + "        " + i);
                }
            }

            @Override
            public void actionFinally() {
                if (null != mCursor) {
                    mCursor.close();
                }
            }

            @Override
            public void error(Exception e) {

            }
        });
    }


    public void query0_2() {
        mILog.carriedOutReadable(new ISQLiteHelper.SQLiteDataAction() {
            @Override
            public void action(SQLiteDatabase db) {
                String sql = "select * from " + DBVersion0.tableName;
                mCursor = db.rawQuery(sql, null);
                while (mCursor.moveToNext()) {
                    Log.e(TAG, "run: ------------>  " + mCursor.getString(mCursor.getColumnIndex(DBVersion0.activityName)));
                }
            }

            @Override
            public void actionFinally() {
                if (null != mCursor) {
                    mCursor.close();
                }
            }

            @Override
            public void error(Exception e) {

            }
        });
    }
}
