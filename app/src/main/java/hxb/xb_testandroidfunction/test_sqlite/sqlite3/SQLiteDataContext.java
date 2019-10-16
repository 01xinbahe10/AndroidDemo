package hxb.xb_testandroidfunction.test_sqlite.sqlite3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.IOException;

/**
 * Created by hxb on 2019/3/1
 * 主要自定义 SQLite 文件的路径
 */
public class SQLiteDataContext extends ContextWrapper {

    @SuppressLint("StaticFieldLeak")
    private static Config mConfig = null;

    public static Config init(Context context) {
        if (null == mConfig) {
            mConfig = new Config();
        }
        mConfig.context = context;
        return mConfig;
    }

    private SQLiteDataContext(Context base) {
        super(base);
    }

    @Override
    public File getDatabasePath(String name) {
        if (mConfig.filePath.equals("")) {//如用户未给定路径，默认系统的
            return super.getDatabasePath(name);
        }

        File dbPath = new File(mConfig.filePath);
        if (!dbPath.exists()) {
            dbPath.mkdirs();
        }

        //数据库文件是否创建成功
        mConfig.isFileCreateSuccess = false;
        File dbFile = new File(dbPath, name);
        if (!dbFile.exists()) {
            try {
                mConfig.isFileCreateSuccess = dbFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mConfig.isFileCreateSuccess = true;
        }
        //返回数据库文件对象
        if (mConfig.isFileCreateSuccess) {
            return dbFile;
        }
        //如创建失败，则默认系统的路径
        return super.getDatabasePath(name);
    }

    /**
     * 重载这个方法，是用来打开SD卡上的数据库的，android 2.3及以下会调用这个方法。
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        File file = getDatabasePath(name);//这里需要提前调用，因为mConfig.isFileCreateSuccess 才生效
        if (mConfig.filePath.equals("") || !mConfig.isFileCreateSuccess) {
            return super.openOrCreateDatabase(name, mode, factory);
        }

        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(file, null);
        return result;
    }

    /**
     * Android 4.0会调用此方法获取数据库。
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        File file = getDatabasePath(name);
        if (mConfig.filePath.equals("") || !mConfig.isFileCreateSuccess) {
            return super.openOrCreateDatabase(name, mode, factory, errorHandler);
        }

        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(file, null);
        return result;
    }

    public static final class Config {
        private Context context;
        private String filePath = "";//自定配置数据库路径
        private boolean isFileCreateSuccess = false;//检测数据库文件是否创建成功

        public Config setFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public SQLiteDataContext create() {
            return new SQLiteDataContext(context);
        }
    }
}
