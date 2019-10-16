package hxb.xb_testandroidfunction.test_sqlite.db

import android.content.Context
import androidx.annotation.StringDef
import hxb.xb_testandroidfunction.test_sqlite.sqlite.SQLiteAssetHelper
/**
 * Created by hxb on 2018/8/24
 * */

class MineSQLiteDatabase : SQLiteAssetHelper {
    companion object {
        var mDBName: String  = "test.db"
        var mDBVersion:Int = 1
        const val mTableName = "ddd"
    }


    constructor(context: Context) : super(context, mDBName,null,mDBVersion){


    }


    @StringDef(mTableName)
    @Retention(AnnotationRetention.SOURCE)
    annotation class tableName

//    //建表
//    fun creatTable(tableName:String?,fields: Array<String>?){
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }

    //
}