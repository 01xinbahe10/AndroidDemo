package hxb.xb_testandroidfunction.test_sqlite.sqlite3;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.regex.Pattern;

/**
 * Created by hxb on 2019/1/29
 */
public class SQLiteAssetHelper extends SQLiteOpenHelper implements ISQLiteHelper {
    //特殊字符
    public static final String SPECIAL_CHAR = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？ 0123456789]";

    private SQLiteInit mSqliteInit = null;

    private SQLiteDatabase mSqLiteData = null;

    public static ISQLiteHelper init(Context context, SQLiteInit sqLiteInit) {
        return new SQLiteAssetHelper(context, sqLiteInit.databaseName(), null, sqLiteInit.databaseVersion(), sqLiteInit);
    }

    private SQLiteAssetHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    private SQLiteAssetHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    private SQLiteAssetHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, SQLiteInit sqLiteInit) {
        super(context, name, factory, version);
        mSqliteInit = sqLiteInit;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (null != mSqliteInit) {
            mSqliteInit.databaseCreate(db);
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (null != mSqliteInit) {
            mSqliteInit.databaseUpgrade(db, oldVersion, newVersion);
        }
    }


    @Override
    public SQLiteOpenHelper getHelper() {
        return this;
    }

    @Override
    public synchronized void carriedOutWritable(SQLiteDataAction action) {
        if (null == action) {
            return;
        }
        try {
            mSqLiteData = getWritableDatabase();
            if(mSqLiteData.inTransaction()){//防止别的线程开启事务而未结束事务
                mSqLiteData.endTransaction();
            }
            mSqLiteData.beginTransaction();
            action.action(mSqLiteData);
            if (null != mSqLiteData) {
                mSqLiteData.setTransactionSuccessful();
            }
        } catch (Exception e) {
            action.error(e);
            e.printStackTrace();
        } finally {
            action.actionFinally();
            if (null != mSqLiteData) {
                mSqLiteData.endTransaction();
                mSqLiteData.close();
                mSqLiteData = null;
            }
        }

    }

    @Override
    public synchronized void carriedOutReadable(SQLiteDataAction action) {
        if (null == action) {
            return;
        }
        try {
            mSqLiteData = getReadableDatabase();
            if(mSqLiteData.inTransaction()){//防止别的线程开启事务而未结束事务
                mSqLiteData.endTransaction();
            }
            mSqLiteData.beginTransaction();
            action.action(mSqLiteData);
            if (null != mSqLiteData) {
                mSqLiteData.setTransactionSuccessful();
            }
        } catch (Exception e) {
            action.error(e);
            e.printStackTrace();
        } finally {
            action.actionFinally();
            if (null != mSqLiteData) {
                mSqLiteData.endTransaction();
                mSqLiteData.close();
                mSqLiteData = null;
            }
        }

    }

    /**
     * 效验字符开头第一个是否是特殊字符
     */
    public static boolean verifySpecialStartChar(String str) {
        Pattern pattern = Pattern.compile(SPECIAL_CHAR);
        return pattern.matcher(str.substring(0, 1)).matches();
    }

}
