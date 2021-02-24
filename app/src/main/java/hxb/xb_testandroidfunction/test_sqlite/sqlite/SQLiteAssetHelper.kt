package hxb.xb_testandroidfunction.test_sqlite.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*
import java.util.zip.ZipInputStream

/**
 * Created by hxb on 2018/8/24
 * */
open class SQLiteAssetHelper : SQLiteOpenHelper {

    private val TAG: String = SQLiteAssetHelper::class.java.simpleName
    private val ASSET_DB_PATH: String = "databases"

    private var mContext: Context? = null
    private var mName: String? = null
    private var mFactory: SQLiteDatabase.CursorFactory? = null
    private var mNewVersion = 0

    private var mDatabase: SQLiteDatabase? = null
    private var mIsInitializing = false

    private var mDatabasePath: String? = null

    private var mAssetPath: String? = null

    private var mUpgradePathFormat: String? = null

    private var mForcedUpgradeVersion = 0

    constructor(context: Context, name: String?, storageDirectory: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(context, name, factory, version) {
        if (version < 1) {
            throw  IllegalArgumentException("Version must be >= 1, was $version")
        }
        if (name == null) {
            throw  IllegalArgumentException("Database name cannot be null")
        }

        mContext = context
        mName = name
        mFactory = factory
        mNewVersion = version

        mAssetPath = "$ASSET_DB_PATH/$name"
        mDatabasePath = storageDirectory ?: context.applicationInfo.dataDir + "/databases"
        mUpgradePathFormat = ASSET_DB_PATH + "/" + name + "_upgrade_%s-%s.sql"
    }

    constructor(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) : this(context, name, null, factory, version)

    override fun getWritableDatabase(): SQLiteDatabase {

        if (mDatabase!!.isOpen && !mDatabase!!.isReadOnly) {
            return mDatabase!! // 该数据库已经开放用于业务
        }
        if (mIsInitializing) {
            throw IllegalStateException("getWritableDatabase called recursively")
        }
        /*
        * 如果我们有一个只读数据库打开，有人可能正在使用它(尽管他们不应该这样做)，
        * 这会导致锁被锁住文件，而我们尝试打开数据库读写会等待文件锁失败。
        * 为了防止这种情况，我们获得锁定只读数据库，关闭其他用户。
        * */

        var success = false
        var db: SQLiteDatabase? = null

        return try {
            mIsInitializing = false
            db = createOrOpenDatabase(false)

            var version = db!!.version
            //检测版本升级
            if (version != 0 && version < mForcedUpgradeVersion) {
                db = createOrOpenDatabase(true)
                db!!.version = mNewVersion
                version = db.version
            }

            if (version != mNewVersion) {
                db.beginTransaction()
                try {
                    if (version == 0) {
                        onCreate(db)
                    } else {
                        if (version > mNewVersion) {
                            Log.w(TAG, "Can't downgrade read-only database from version $version to $mNewVersion: ${db.path}")
                        }
                        onUpgrade(db, version, mNewVersion)
                    }

                } finally {
                    db.endTransaction()
                }
            }
            onOpen(db)
            success = true
            db

        } finally {
            mIsInitializing = false
            if (success) {
                try {
                    mDatabase!!.close()
                } catch (e: Exception) {

                }
                mDatabase = db
            } else {
                db!!.close()
            }
        }

    }


    override fun getReadableDatabase(): SQLiteDatabase {
        if (mDatabase!!.isOpen) {
            return mDatabase!!
        }
        if (mIsInitializing) {
            throw IllegalStateException("getReadableDatabase called recursively")
        }

        try {
            return writableDatabase
        } catch (e: SQLiteException) {
            //不能打开临时数据库只读!
            if (null == mName) {
                throw e
            }
            Log.e(TAG, "Couldn't open $mName for writing (will try read-only):", e)

        }

        var db: SQLiteDatabase? = null
        return try {
            mIsInitializing = true
            val path = mContext!!.getDatabasePath(mName).path
            db = SQLiteDatabase.openDatabase(path, mFactory, SQLiteDatabase.OPEN_READONLY)
            if (db!!.version != mNewVersion) {
                throw  SQLiteException("Can't upgrade read-only database from version ${db.version} to $mNewVersion : $path")
            }
            onOpen(db)
            Log.w(TAG, "Opened $mName in read-only mode")
            mDatabase = db
            mDatabase!!
        } finally {
            mIsInitializing = false
            if (db!! != mDatabase!!) {
                db.close()
            }
        }
    }


    override fun onCreate(db: SQLiteDatabase?) {
        //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //To change body of created functions use File | Settings | File Templates.

        Log.w(TAG, "Upgrading database $mName  from version $oldVersion to $newVersion ...")
        val paths: ArrayList<String> = ArrayList()
        getUpgradeFilePaths(oldVersion, newVersion - 1, newVersion, paths)
        if (paths.isEmpty()) {
            Log.e(TAG, "no upgrade script path from $oldVersion to $newVersion")
            throw SQLiteAssetException("no upgrade script path from $oldVersion to $newVersion")
        }

        Collections.sort(paths, VersionComparator())

        for (path: String in paths) {
            try {
                Log.w(TAG, "processing upgrade: $path")
                val inputStream: InputStream = mContext!!.assets.open(path)
                val sql: String = Utils.fun0.convertStreamToString(inputStream) ?: continue
                val cmds: List<String> = Utils.fun0.splitSqlScript(sql, ';')
                for (cmd: String in cmds) {
                    if (cmd.trim().isNotEmpty()) {
                        db!!.execSQL(cmd)
                    }
                }


            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        Log.w(TAG, "Successfully upgraded database $mName from version $oldVersion to $newVersion")

    }

    /**
     * 关闭任何打开的数据库对象
     * */
    override fun close() {
        if (mIsInitializing) {
            throw IllegalStateException("Closed during initialization")
        }

        if (mDatabase!!.isOpen) {
            mDatabase!!.close()
            mDatabase = null
        }
    }

    /**
     * 设置更新版本
     * */
    fun setForcedUpgrade(version: Int) {
        mForcedUpgradeVersion = version
    }

    fun setForcedUpgrade() {
        setForcedUpgrade(mNewVersion)
    }


    @Throws(SQLiteAssetException::class)
    private fun createOrOpenDatabase(force: Boolean): SQLiteDatabase? {
        /*
        * 首先测试db文件是否存在，不要尝试打开防止在API 14+日志中出现错误跟踪
        * */

        var db: SQLiteDatabase? = null
        val file = File("$mDatabasePath/$mName")
        if (file.exists()) {
            db = returnDatabase()
        }

        return if (null != db) {
            if (force) {
                Log.w(TAG, "forcing database upgrade!")
                copyDatabaseFromAssets()
                db = returnDatabase()
            }
            db
        } else {
            copyDatabaseFromAssets()
            db = returnDatabase()
            db
        }

    }


    private fun returnDatabase(): SQLiteDatabase? {
        return try {
            val db: SQLiteDatabase = SQLiteDatabase.openDatabase("$mDatabasePath/$mName", mFactory, SQLiteDatabase.OPEN_READWRITE)
            Log.i(TAG, "successfully opened database $mName")
            db
        } catch (e: SQLiteException) {
            Log.w(TAG, "could not open database $mName - ${e.message}")
            null
        }
    }


    @Throws(SQLiteAssetException::class)
    private fun copyDatabaseFromAssets() {
        Log.w(TAG, "copying database from assets...")
        val path = mAssetPath
        val dest = "$mDatabasePath / $mName"
        var inputStream: InputStream
        var isZip = false
        try {
            inputStream = mContext!!.assets.open(path.toString())
        } catch (e: IOException) {
            // 尝试压缩
            try {
                inputStream = mContext!!.assets.open("$path.zip")
                isZip = true
            } catch (e2: IOException) {
                // 尝试压缩
                try {
                    inputStream = mContext!!.assets.open("$path.gz")
                } catch (e3: IOException) {
                    val se = SQLiteAssetException("Missing $mAssetPath file (or .zip, .gz archive) in assets, or target folder not writable")
                    se.stackTrace = e3.stackTrace
                    throw se
                }
            }
        }

        try {
            val f = File("$mDatabasePath/")
            if (!f.exists()) {
                f.mkdir(); }
            if (isZip) {
                val zis: ZipInputStream = Utils.fun0.getFileFromZip(inputStream)
                        ?: throw  SQLiteAssetException("Archive is missing a SQLite database file")
                Utils.fun0.writeExtractedFileToDisk(zis, FileOutputStream(dest))
            } else {
                Utils.fun0.writeExtractedFileToDisk(inputStream, FileOutputStream(dest))
            }

            Log.w(TAG, "database copy complete")
        } catch (e: IOException) {

        }
    }


    private fun getUpgradeFilePaths(baseVersion: Int, start: Int, end: Int, paths: ArrayList<String>) {
        val a: Int
        val b: Int
        val isInputStream: InputStream? = getUpgradeSQLStream(start, end)
        if (null != isInputStream) {
            val path = String.format(mUpgradePathFormat!!, start, end)
            paths.add(path)

            a = start - 1
            b = start
//            isInputStream = null
        } else {
            a = start - 1
            b = end
        }

        if (a < baseVersion) {
            return
        } else {
            getUpgradeFilePaths(baseVersion, a, b, paths)//递归调用
        }
    }


    private fun getUpgradeSQLStream(oldVersion: Int, newVersion: Int): InputStream? {
        val path: String = String.format(mUpgradePathFormat!!, oldVersion, newVersion)
        return try {
            mContext!!.assets.open(path)
        } catch (e: IOException) {
            null
        }

    }

    /**
     * 表明SQLite资产检索或解析出现错误的异常。
     * */
    @SuppressWarnings("serial")
    class SQLiteAssetException : SQLiteException {
        constructor()
        constructor(error: String) : super(error)
    }

}