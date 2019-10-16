package hxb.xb_testandroidfunction.test_kotlin.event


import android.view.View

interface AdapterEvent{
    fun onClick(view: View, position:Int)

}