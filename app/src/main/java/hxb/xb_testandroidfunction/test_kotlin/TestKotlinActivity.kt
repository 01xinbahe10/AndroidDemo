package hxb.xb_testandroidfunction.test_kotlin

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import hxb.xb_testandroidfunction.R
import hxb.xb_testandroidfunction.test_kotlin.adapter.TestKotlinAdapter
import hxb.xb_testandroidfunction.test_kotlin.event.AdapterEvent
import hxb.xb_testandroidfunction.test_kotlin.utils.NoScrollLLManager
import java.util.*
import kotlin.collections.ArrayList

class TestKotlinActivity : androidx.fragment.app.FragmentActivity(), View.OnClickListener, AdapterEvent {


    private var mTv1: TextView? = null
    private var mRecyclerView: androidx.recyclerview.widget.RecyclerView? = null
    private var mAdapter: TestKotlinAdapter? = null
    private var mTvUp: TextView? = null
    private var mTvDown: TextView? = null

    private var mPosition = 0
    private var mListSize = 0

    private var myOnScrollListener: MyOnScrollListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_kotlin)
        mTv1 = findViewById(R.id.tv1)
        mTvUp = findViewById(R.id.tvUp)
        mTvDown = findViewById(R.id.tvDown)

        mTv1!!.setOnClickListener(this)
        mTvUp!!.setOnClickListener(this)
        mTvDown!!.setOnClickListener(this)


        mRecyclerView = findViewById(R.id.recyclerView)
//        val linearLayoutManager = LinearLayoutManager(this)
//        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        val linearLayoutManager = NoScrollLLManager(this)
        linearLayoutManager.orientation = androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
        linearLayoutManager.setVerticalScroll(true)
        mRecyclerView!!.layoutManager = linearLayoutManager

        //暂时无用
//        myOnScrollListener = MyOnScrollListener(mRecyclerView)
//        mRecyclerView!!.addOnScrollListener(myOnScrollListener)

        mAdapter = TestKotlinAdapter(this)
        mAdapter!!.setEvent(this)
        mRecyclerView!!.adapter = mAdapter

        val list = ArrayList<Int>()
        for (i in 0..30) {
            list.add(i)
        }
        mListSize = list.size
        mAdapter!!.addList(list)

    }

    override fun onClick(v: View?) {
        //To change body of created functions use File | Settings | File Templates.
        when(v!!.id) {
            R.id.tv1 -> mTv1!!.text = "红色"
            R.id.tvUp -> {
                val scope = mPosition >= 0
                val scope2  = mPosition < mListSize
                if (scope and scope2){
                    mPosition++

                    Toast.makeText(this, " 点击了向上:$mPosition", Toast.LENGTH_SHORT).show()
                    myOnScrollListener!!.smoothMoveToPosition(mPosition)
                }
            }
            R.id.tvDown -> {
                val scope = mPosition > 0
                val scope2  = mPosition < mListSize
                if (scope and scope2) {
                    mPosition--
                    Toast.makeText(this, " 点击了向下：$mPosition", Toast.LENGTH_SHORT).show()
                    myOnScrollListener!!.smoothMoveToPosition(mPosition)
                }
            }
            else -> Log.e("TAG", "哈哈哈")
        }


    }

    override fun onClick(view: View, position: Int) {
        //To change body of created functions use File | Settings | File Templates.
        Toast.makeText(this, "点击了$position", Toast.LENGTH_SHORT).show()
    }

//    LayoutManager用的是LinearLayoutManager，强烈推荐下面的方法获取滑动距离：
    fun getScollYDistance(): Int {
        val layoutManager: androidx.recyclerview.widget.LinearLayoutManager = mRecyclerView!!.layoutManager as androidx.recyclerview.widget.LinearLayoutManager
        val position: Int = layoutManager.findFirstVisibleItemPosition()
        val firstVisiableChildView: View = layoutManager.findViewByPosition(position)!!
        val itemHeight: Int = firstVisiableChildView.height
        return position * itemHeight - firstVisiableChildView.top

    }



    class MyOnScrollListener(mRecyclerView: androidx.recyclerview.widget.RecyclerView?) : OnScrollListener(){

        private var mShouldScroll = false
        private var mToPosition = 0

        private val mRecyclerView: androidx.recyclerview.widget.RecyclerView = mRecyclerView!!

        override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
        }

        override fun onScrollStateChanged(recyclerView: androidx.recyclerview.widget.RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE) {
//                // mAutoScrolling = false;
                if (mShouldScroll) {
                    mShouldScroll = false
                    smoothMoveToPosition(mToPosition)
                }
            }
        }

        fun smoothMoveToPosition(position: Int){
            val childCount = mRecyclerView.childCount
//            val firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0))
            val lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(childCount -1))
            if (position < lastItem){
                // 如果要跳转的位置在第一个可见项之前，则smoothScrollToPosition可以直接跳转
                mRecyclerView.smoothScrollToPosition(position)
            }else if (position <= lastItem){
                // 如果要跳转的位置在第一个可见项之后，且在最后一个可见项之前
                // smoothScrollToPosition根本不会动，
                // 通过计算要跳转的item的top，然后在使用smoothScrollBy进行跳转
                val movePosition = position - lastItem

                val is1 =  movePosition >= 0
                val is2 = movePosition < childCount
                if ( is1 and is2){
                    val top = mRecyclerView.getChildAt(movePosition).top
                    mRecyclerView.smoothScrollBy(0, top)
                }
            }else{
                // 如果要跳转的位置在最后可见项之后，则先调用smoothScrollToPosition让要跳转的item位于屏幕可见范围之内，然后再通过smoothScrollBy进行跳转
                // 再通过onScrollStateChanged控制再次调用smoothMoveToPosition，进入上一个控制语句
                mRecyclerView.smoothScrollToPosition(position)
                mShouldScroll = true
                mToPosition = position
            }
        }
    }

}



