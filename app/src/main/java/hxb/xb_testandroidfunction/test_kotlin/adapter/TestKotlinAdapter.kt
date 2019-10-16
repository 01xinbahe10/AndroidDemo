package hxb.xb_testandroidfunction.test_kotlin.adapter

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import hxb.xb_testandroidfunction.R
import hxb.xb_testandroidfunction.test_kotlin.event.AdapterEvent
import java.util.*
import kotlin.collections.ArrayList

class TestKotlinAdapter(context:Context) : androidx.recyclerview.widget.RecyclerView.Adapter<TestKotlinAdapter.MyViewHolder>() {

    private var mContext = context
    private var mArrayList:ArrayList<Int> = ArrayList()
    private var mAdapterEvent:AdapterEvent? = null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       //To change body of created functions use File | Settings | File Templates.
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_test_kotlin,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        //To change body of created functions use File | Settings | File Templates.
        return mArrayList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //To change body of created functions use File | Settings | File Templates.
        val bean = mArrayList[position]
        holder.tvList.text = "$bean"
        holder.clParent.setOnClickListener {
            mAdapterEvent!!.onClick(it,position)
        }

    }

    fun setEvent(event:AdapterEvent){
        mAdapterEvent = event
    }
    fun addList(list:List<Int>){
        mArrayList.addAll(list)
        notifyDataSetChanged()
    }
    class MyViewHolder(itemView: View?) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView!!) {
        var tvList:TextView = itemView!!.findViewById(R.id.tvList)
        var clParent:View = itemView!!.findViewById(R.id.clParent)
    }
}