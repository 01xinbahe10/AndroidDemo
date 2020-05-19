package hxb.xb_testandroidfunction.test_sqlite.db_controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Size;

import java.util.ArrayList;
import java.util.List;

import hxb.xb_testandroidfunction.test_sqlite.sqlite3.ISQLiteHelper;
import hxb.xb_testandroidfunction.test_sqlite.sqlite3.SQLiteAssetHelper;

/**
 * Created by hxb on  2020/5/19
 */
public class Sqlite1DBControl {
    private static final String TAG = "Sqlite1DBControl";
    private static Sqlite1DBControl mControl = null;
    private ISQLiteHelper mHelper = null;
    private String TABLE_NAME = "";
    private Cursor mCursor = null;

    interface DBVersion {
        String dbName = "TestDB";
        int version = 3;
        String tableName = "Test";
    }

    @Deprecated
    interface FieldVersion1 {
        ///////////数据库版本1的字段///////////
        String f_numbering = "numbering";//编号(int)
        String f_name = "name";//(char(255))
        String f_age = "age";//(unsigned smallint)
    }

    @Deprecated
    interface FieldVersion2 {
        ///////////数据库版本2的字段///////////
        //说明：比上一个版本多一个字段
        String f_numbering = "numbering";//编号(int)
        String f_name = "name";//(char(255))
        String f_age = "age";//(unsigned smallint)
        String f_height = "height";//(unsigned smallint)
    }

    interface FieldVersion3{
        ///////////数据库版本3的字段///////////
        //说明：改字段名和字段数据类型
        String f_numbering = "number";//(int)
        String f_name = "userName";//(char(255))
        String f_age = "age";//(unsigned int)
        String f_height = "height";//(unsigned int)
    }


    private Sqlite1DBControl() {
    }

    public static Sqlite1DBControl getDB(Context context) {
        if (null != mControl) {
            return mControl;
        }

        String account = "";//不同的用户有不同的表名
        mControl = new Sqlite1DBControl();
        mControl.mHelper = SQLiteAssetHelper.init(context, new ISQLiteHelper.SQLiteInit() {
            @Override
            public String databaseName() {
                return DBVersion.dbName;
            }

            @Override
            public int databaseVersion() {
                return DBVersion.version;
            }

            @Override
            public void databaseCreate(SQLiteDatabase db) {
                /*在第一次创建数据库时调用。 这就是表的创建和表的初始填充应该发生。*/
                Log.d(TAG, "databaseCreate: >>>>>> 初始化数据库 ");
                mControl.TABLE_NAME = DBVersion.tableName + DBVersion.version;
                mControl.createTable(db,mControl.TABLE_NAME);
            }

            @Override
            public void databaseUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                Log.e(TAG, "databaseUpgrade: oldVersion: " + oldVersion + "  newVersion: " + newVersion);
                String oldTableName = "";
                switch (newVersion) {
                    case 1://初始版本不会走这里
                        //do nothing
                        break;
                    case 2://高版本
                    case 3:
                        //测试1升至3, 2升至3等
                        oldTableName = DBVersion.tableName + oldVersion;
                        mControl.exchangeTableData(db, oldTableName, oldVersion);
                        break;
                }
            }
        });
        return mControl;
    }

    private void createTable(SQLiteDatabase db,String tableName) {
        String sql = "";
        switch (DBVersion.version) {
            case 1:
                sql = FieldVersion1.f_numbering + " int primary key not null,";
                sql = sql + FieldVersion1.f_name + " char(255),";
                sql = sql + FieldVersion1.f_age + " unsigned smallint check( " + FieldVersion1.f_age + " <=150)";
                sql = "create table if not exists " + tableName + " (" + sql + ")";
                db.execSQL(sql);
                break;
            case 2:
                sql = FieldVersion2.f_numbering + " int primary key not null,";
                sql = sql + FieldVersion2.f_name + " char(255),";
                sql = sql + FieldVersion2.f_age + " unsigned smallint check( " + FieldVersion2.f_age + " <=150 ),";
                sql = sql + FieldVersion2.f_height + " unsigned smallint check( " + FieldVersion2.f_height + " <= 500)";
                sql = "create table if not exists " + tableName + " (" + sql + ")";
                db.execSQL(sql);
                break;
            case 3:
                sql = FieldVersion3.f_numbering + " int primary key not null,";
                sql = sql + FieldVersion3.f_name + " char(255),";
                sql = sql + FieldVersion3.f_age + " unsigned int check( " + FieldVersion3.f_age + " <=150 ),";
                sql = sql + FieldVersion3.f_height + " unsigned int check( " + FieldVersion3.f_height + " <= 500)";
                sql = "create table if not exists " + tableName + " (" + sql + ")";
                db.execSQL(sql);
                break;
        }
    }

    /**
     * 交换表数据
     * 实现原理：1,创建新表(表名+版本号+其它)
     * 2,查询旧表数据(封装数据到旧版数据模型中)
     * 3,将旧版数据按字段存入新版数据表中
     * 4,删除旧表
     *
     * 注意：1,交换表数据方法中，每个旧版表数据写入新版表数据中，一定要重新梳理(注意是”每个不同版本的旧版字段“)
     * 每个不同版本的旧版字段与最新版字段的对应关系及数据类型；防止升至最新版出错。
     * 2,增删改查 方法接口中一定要使用最新的表字段
     */
    private void exchangeTableData(SQLiteDatabase db, String oldTableName, int oldVersion) {
        switch (oldVersion) {
            case 1:
                TABLE_NAME = DBVersion.tableName + DBVersion.version;
                createTable(db,TABLE_NAME);//创建新表

                Log.d(TAG, "action: 开始查看旧表字段");
                String fSql = "select * from " + oldTableName + " a";
                mCursor = db.rawQuery(fSql, null);
                for (String f : mCursor.getColumnNames()) {
                    Log.d(TAG, "action: 旧表字段...  " + f);
                }
                mCursor.close();
                Log.d(TAG, "action: 查看旧表字段 end");


                Log.d(TAG, "action: 开始查看新表字段");
                fSql = "select * from " + TABLE_NAME + " a";
                mCursor = db.rawQuery(fSql, null);
                for (String f : mCursor.getColumnNames()) {
                    Log.d(TAG, "action: 新表字段...  " + f);
                }
                mCursor.close();
                Log.d(TAG, "action: 查看新表字段 end");

                Log.d(TAG, "action: 开始查询旧表数据 ");
                //查询旧表
                mCursor = db.query(oldTableName, null, null, null, null, null, null);
                List<UserInfoVO> list = new ArrayList<>();
                if (mCursor.moveToFirst()) {
                    do {
                        int numbering = mCursor.getInt(mCursor.getColumnIndex(FieldVersion1.f_numbering));
                        String name = mCursor.getString(mCursor.getColumnIndex(FieldVersion1.f_name));
                        int age = mCursor.getInt(mCursor.getColumnIndex(FieldVersion1.f_age));

                        Log.d(TAG, "action: 查询旧表数据中...   numbering:" + numbering + "  name:" + name + "  age:" + age);
                        list.add(UserInfoVO.init().setNumbering(numbering).setName(name).setAge(age));

                    } while (mCursor.moveToNext());
                    mCursor.close();
                }

                Log.d(TAG, "action: 查询旧表结束");

                //将旧表数据存入到新表
                Log.d(TAG, "action: 开始存入新表 " + TABLE_NAME);
                for (UserInfoVO info : list) {
                    ContentValues contentValues2 = new ContentValues();
                    contentValues2.put(FieldVersion3.f_numbering, info.getNumbering());
                    contentValues2.put(FieldVersion3.f_name, info.getName());
                    contentValues2.put(FieldVersion3.f_age, info.getAge());
                    long i = db.replace(TABLE_NAME, null, contentValues2);
                    Log.d(TAG, "action: 存入新表中...  " + i);
                }
                Log.d(TAG, "action: 结束存入新表  end");

                Log.d(TAG, "action: 开始删除旧表  " + oldTableName);
                //删除旧表
                String sql = "drop table " + oldTableName;
                db.execSQL(sql);
                Log.d(TAG, "action: 删除旧表结束  end");
                break;
            case 2:
                TABLE_NAME = DBVersion.tableName + DBVersion.version;
                createTable(db,TABLE_NAME);//创建新表

                Log.d(TAG, "action: 2开始查看旧表字段");
                String fSql2 = "select * from " + oldTableName + " a";
                mCursor = db.rawQuery(fSql2, null);
                for (String f : mCursor.getColumnNames()) {
                    Log.d(TAG, "action: 2旧表字段...  " + f);
                }
                mCursor.close();
                Log.d(TAG, "action: 2查看旧表字段 end");


                Log.d(TAG, "action: 2开始查看新表字段");
                fSql2 = "select * from " + TABLE_NAME + " a";
                mCursor = db.rawQuery(fSql2, null);
                for (String f : mCursor.getColumnNames()) {
                    Log.d(TAG, "action: 2新表字段...  " + f);
                }
                mCursor.close();
                Log.d(TAG, "action: 2查看新表字段 end");

                //查询旧表
                mCursor = db.query(oldTableName, null, null, null, null, null, null);
                List<UserInfoVO> list2 = new ArrayList<>();
                Log.d(TAG, "action: 2开始查询旧表数据 ");
                if (mCursor.moveToFirst()) {
                    do {
                        int numbering = mCursor.getInt(mCursor.getColumnIndex(FieldVersion2.f_numbering));
                        String name = mCursor.getString(mCursor.getColumnIndex(FieldVersion2.f_name));
                        int age = mCursor.getInt(mCursor.getColumnIndex(FieldVersion2.f_age));
                        int height = mCursor.getInt(mCursor.getColumnIndex(FieldVersion2.f_height));
                        Log.d(TAG, "action: 2查询旧表数据中...   numbering:" + numbering + "  name:" + name + "  age:" + age);
                        list2.add(UserInfoVO.init().setNumbering(numbering).setName(name).setAge(age).setHeight(height));

                    } while (mCursor.moveToNext());
                    mCursor.close();
                }
                Log.d(TAG, "action: 2查询旧表结束");

                //将旧表数据存入到新表
                Log.d(TAG, "action: 2开始存入新表 " + TABLE_NAME);
                for (UserInfoVO info : list2) {
                    ContentValues contentValues2 = new ContentValues();
                    contentValues2.put(FieldVersion3.f_numbering, info.getNumbering());
                    contentValues2.put(FieldVersion3.f_name, info.getName());
                    contentValues2.put(FieldVersion3.f_age, info.getAge());
                    contentValues2.put(FieldVersion3.f_height,info.getHeight());
                    long i = db.replace(TABLE_NAME, null, contentValues2);
                    Log.d(TAG, "action: 2存入新表中...  " + i);
                }
                Log.d(TAG, "action: 2结束存入新表  end");

                Log.d(TAG, "action: 2开始删除旧表  " + oldTableName);
                //删除旧表
                String sql2 = "drop table " + oldTableName;
                db.execSQL(sql2);
                Log.d(TAG, "action: 2删除旧表结束  end");
                break;
        }
    }


    public boolean delTable(String tableName) {
        final boolean[] is = {false};
        mHelper.carriedOutWritable(new ISQLiteHelper.SQLiteDataAction() {
            @Override
            public void action(SQLiteDatabase db) {
                String sql = "drop table " + tableName;
                db.execSQL(sql);
                is[0] = true;
            }

            @Override
            public void actionFinally() {

            }

            @Override
            public void error(Exception e) {
                is[0] = false;
            }
        });
        return is[0];
    }

    public boolean saveData(int numbering, String name, @Size(150) int age) {
        final boolean[] is = {false};
        mHelper.carriedOutWritable(new ISQLiteHelper.SQLiteDataAction() {
            @Override
            public void action(SQLiteDatabase db) {
                TABLE_NAME = DBVersion.tableName + DBVersion.version;
                createTable(db,TABLE_NAME);//如不存在就建表
                ContentValues contentValues2 = null;
                try {
                    contentValues2 = new ContentValues();
                    contentValues2.put(FieldVersion3.f_numbering, numbering);
                    contentValues2.put(FieldVersion3.f_name, name);
                    contentValues2.put(FieldVersion3.f_age, age);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (null == contentValues2) {
                    is[0] = false;
                } else {
                    long i = db.replace(TABLE_NAME, null, contentValues2);
                    Log.d(TAG, "action: >> 存入状态  "+i);
                    is[0] = true;
                }
            }

            @Override
            public void actionFinally() {

            }

            @Override
            public void error(Exception e) {
                is[0] = false;
//                Log.e(TAG, "error: ppppppppppppppppppppp    " + e.getMessage());
            }
        });

        return is[0];
    }

    public static class UserInfoVO {
        private int numbering;
        private String name;
        private int age;
        private int height;

        private UserInfoVO() {
        }

        public static UserInfoVO init() {
            return new UserInfoVO();
        }

        public int getNumbering() {
            return numbering;
        }

        public UserInfoVO setNumbering(int numbering) {
            this.numbering = numbering;
            return this;
        }

        public String getName() {
            return name;
        }

        public UserInfoVO setName(String name) {
            this.name = name;
            return this;
        }

        public int getAge() {
            return age;
        }

        public UserInfoVO setAge(int age) {
            this.age = age;
            return this;
        }

        public int getHeight() {
            return height;
        }

        public UserInfoVO setHeight(int height) {
            this.height = height;
            return this;
        }
    }


}
