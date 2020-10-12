package hxb.xb_testandroidfunction.test_sqlite.sqlite3;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * Created by hxb on 2019/1/29
 * <p>
 * getWritableDatabase() 方法以读写方式打开数据库，一旦数据库的磁盘空间满了，
 * 数据库就只能读而不能写，倘若使用的是getWritableDatabase() 方法就会出错。
 * <p>
 * <p>
 * getReadableDatabase()方法则是先以读写方式打开数据库，
 * 如果数据库的磁盘空间满了，就会打开失败，当打开失败后会继续
 * 尝试以只读方式打开数据库。如果该问题成功解决，则只读数据库
 * 对象就会关闭，然后返回一个可读写的数据库对象。
 */
public class SQLiteAssetHelper extends SQLiteOpenHelper implements ISQLiteHelper {
    //特殊字符
    public static final String SPECIAL_CHAR = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？ 0123456789]";

    //通过记录操作次数避免频繁开启数据库
    private volatile AtomicInteger mOpenCounter = new AtomicInteger();

    private SQLiteInit mSqliteInit = null;

    private volatile SQLiteDatabase mSqLiteData = null;

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


    private SQLiteDatabase getSQLiteDatabase() {
        try {
            //当磁盘满了，该方法会报错
            mSqLiteData = getWritableDatabase();
        } catch (Exception e) {
            mSqLiteData = getReadableDatabase();
        }

        return mSqLiteData;
    }

    @Override
    public synchronized void carriedOut(SQLiteDataAction action) {
        if (null == action) {
            return;
        }
        try {

            int increment = mOpenCounter.incrementAndGet();
            if (increment == 1 || null == mSqLiteData) {
                mSqLiteData = getSQLiteDatabase();
            }
            //如果increment表示当前线程开启时，另外一条线程还未结束时，此时需要结束事务
            //如果当前线程有待处理的事务，则返回true。
            boolean isTransaction = mSqLiteData.inTransaction();
//            Log.e(TAG, "carriedOut: >>>>>>>>>  " + isTransaction+"    "+increment);
            if (isTransaction || increment > 1) {
                mSqLiteData.endTransaction();
            }

            mSqLiteData.beginTransaction();
            action.action(mSqLiteData);
            if (null != mSqLiteData) {
                mSqLiteData.setTransactionSuccessful();
            }

            action.actionFinally();

        } catch (Exception e) {
            action.error(e);
            e.printStackTrace();
        } finally {

            if (mOpenCounter.decrementAndGet() == 0) {
                if (mSqLiteData != null) {
                    mSqLiteData.endTransaction();
                    mSqLiteData.close();
//                    mSqLiteData = null;
                }
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
