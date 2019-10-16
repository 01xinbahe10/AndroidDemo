package hxb.xb_testandroidfunction.test_sqlite

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import hxb.xb_testandroidfunction.R
import hxb.xb_testandroidfunction.test_sqlite.sqlite.SQLiteAssetHelper

class TestSqliteActivity : androidx.fragment.app.FragmentActivity(){
    private var mSQLiteHelper: SQLiteAssetHelper?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_sqlite)

    }
}