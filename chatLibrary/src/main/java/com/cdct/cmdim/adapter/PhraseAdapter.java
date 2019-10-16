package com.cdct.cmdim.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cdct.cmdim.beans.CommonLanguageBean;

/**
 * Created by hxb on 2018/3/30.
 * 常用语适配器
 */

public class PhraseAdapter extends BaseAdapter {
    private Context mContext;
    private float mScale;//手机密度
    private OnItemClickListener mOnItemClickListener;
    private CommonLanguageBean response;

    public PhraseAdapter(Context context, CommonLanguageBean response){
        this.mContext = context;
        this.response = response;
        mScale = mContext.getResources().getDisplayMetrics().density;
    }
    @Override
    public int getCount() {
        return response.getData().getList().size();
    }

    @Override
    public Object getItem(int position) {
        return response.getData().getList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyViewHolder holder = null;
        if (convertView == null){
            holder = new MyViewHolder();
            AbsListView.LayoutParams LlLayoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout linearLayout = new LinearLayout(parent.getContext());
            linearLayout.setLayoutParams(LlLayoutParams);
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            AbsListView.LayoutParams TvLayoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            holder.textView = new TextView(parent.getContext());
            holder.textView.setLayoutParams(TvLayoutParams);
            holder.textView.setTextColor(Color.parseColor("#000000"));
            holder.textView.setTextSize(17f);
            holder.textView.setPadding(dp2px(13),dp2px(13),dp2px(13),dp2px(13));

            linearLayout.addView(holder.textView);
            convertView = linearLayout;
            convertView.setTag(holder);
        }else {
            holder = (MyViewHolder) convertView.getTag();
        }
        holder.textView.setText(response.getData().getList().get(position).getCCYYNR());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == mOnItemClickListener){
                    return;
                }
                mOnItemClickListener.onItemClick(v,position,response.getData().getList().get(position).getCCYYNR());
            }
        });
        return convertView;
    }

    class MyViewHolder{
        TextView textView;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position, String content);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }

    private int dp2px(float dp) {
        return (int) (mScale * dp + 0.5f);
    }
}
