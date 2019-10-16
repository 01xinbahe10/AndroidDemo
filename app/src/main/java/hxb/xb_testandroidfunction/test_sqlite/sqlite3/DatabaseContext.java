package hxb.xb_testandroidfunction.test_sqlite.sqlite3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * 主要自定义 SQLite 文件的路径
 */
@Deprecated
public class DatabaseContext extends ContextWrapper {

    @SuppressLint("StaticFieldLeak")
    private static config mConfig = null;

    public static config init(Context context) {
        if (null == mConfig) {
            mConfig = new config();
        }
        mConfig.context = context;
        return mConfig;
    }


    private DatabaseContext(Context base) {
        super(base);
    }

    /**
     * 获得数据库路径，如果不存在，则创建对象对象
     */
    @Override
    public File getDatabasePath(String name) {
//        return super.getDatabasePath(name);

//        //判断是否存在sd卡
//        boolean sdExist = android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState());
//        if (!sdExist) {//如果不存在,
//            Log.e("SD卡管理：", "SD卡不存在，请加载SD卡");
//            return null;
//        } else {//如果存在,获取sd卡路径
//            String dbDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
//            dbDir += "/hxbDB";//数据库所在目录
//            String dbPath = dbDir + "/" + name;//数据库路径
//            File dirFile = new File(dbDir);
//            if (!dirFile.exists())//判断目录是否存在，不存在则创建该目录
//                dirFile.mkdirs();
//
//            //数据库文件是否创建成功
//            boolean isFileCreateSuccess = false;
//            File dbFile = new File(dbPath);
//            if (!dbFile.exists()) {//判断文件是否存在，不存在则创建该文件
//                try {
//                    isFileCreateSuccess = dbFile.createNewFile();//创建文件
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            } else
//                isFileCreateSuccess = true;
//
//            //返回数据库文件对象
//            if (isFileCreateSuccess)
//                return dbFile;
//            else
//                return null;
//        }
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

    public static final class config {
        private Context context;
        private String filePath = "";//自定配置数据库路径
        private boolean isFileCreateSuccess = false;//检测数据库文件是否创建成功

        public config setFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public DatabaseContext create() {
            return new DatabaseContext(context);
        }
    }
}
