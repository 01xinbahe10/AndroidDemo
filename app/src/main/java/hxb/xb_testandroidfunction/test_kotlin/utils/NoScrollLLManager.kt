package hxb.xb_testandroidfunction.test_kotlin.utils

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

/**
 * 没有滚动事件的自定义LinerLayoutManager
 * */
class NoScrollLLManager(context: Context?) : androidx.recyclerview.widget.LinearLayoutManager(context) {
    private var isVerticalScroll = true

    override fun canScrollVertically(): Boolean {

        return isVerticalScroll and super.canScrollVertically()
    }

    override fun canScrollHorizontally(): Boolean {
        return super.canScrollHorizontally()
    }


    fun setVerticalScroll(isScroll:Boolean){
        isVerticalScroll = isScroll
    }



}