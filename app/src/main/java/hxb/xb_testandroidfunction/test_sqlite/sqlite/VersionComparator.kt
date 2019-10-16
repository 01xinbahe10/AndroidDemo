package hxb.xb_testandroidfunction.test_sqlite.sqlite

import android.util.Log
import java.util.Comparator
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by hxb on 2018/8/24
 * */
class VersionComparator : Comparator<String>{
    private val TAG = SQLiteAssetHelper::class.java.simpleName
    private val pattern: Pattern = Pattern.compile(".*_upgrade_([0-9]+)-([0-9]+).*")
    /**
     *比较指定的两个升级脚本字符串以确定它们的字符串考虑两个版本号的相对顺序。
     *
     *假定所有使用的数据库名称是相同的，因为这个函数只比较两个版本号。
     *
     * @param file0 升级脚本文件名
     * @param file1 第二个要与file0进行比较的升级脚本文件名
     * */
    override fun compare(file0: String?, file1: String?): Int {
        //To change body of created functions use File | Settings | File Templates.

        val m0: Matcher = pattern.matcher(file0)
        val m1: Matcher = pattern.matcher(file1)

        if (!m0.matches() ){
            Log.w(TAG, "could not parse upgrade script file: $file0")
            throw SQLiteAssetHelper.SQLiteAssetException("Invalid upgrade script file")
        }
        if (!m1.matches()) {
            Log.w(TAG, "could not parse upgrade script file: $file1")
            throw SQLiteAssetHelper.SQLiteAssetException("Invalid upgrade script file")
        }

        val v0_from = m0.group(1).toInt()
        val v1_from = m1.group(1).toInt()
        val v0_to = m0.group(2).toInt()
        val v1_to = m1.group(2).toInt()

        if (v0_from == v1_from) {
            // “from”两个版本都匹配;检查”到“下一个版本

            if (v0_to == v1_to) {
                return 0
            }

            return if (v0_to < v1_to) -1 else 1
        }

        return if (v0_from < v1_from) -1 else 1

    }

}