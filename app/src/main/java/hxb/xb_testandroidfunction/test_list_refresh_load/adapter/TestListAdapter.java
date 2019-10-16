package hxb.xb_testandroidfunction.test_list_refresh_load.adapter;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by hxb on 2018/5/29.
 */

public class TestListAdapter extends BaseAdapter{
    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder holder = null;
        if (convertView == null){
            holder = new MyViewHolder();
            RelativeLayout.LayoutParams rl_lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,200);
            holder.relativeLayout = new RelativeLayout(parent.getContext());
            holder.relativeLayout.setLayoutParams(rl_lp);
//            holder.relativeLayout.setGravity(RelativeLayout.CENTER_HORIZONTAL);
            holder.relativeLayout.setGravity(Gravity.CENTER);
            holder.textView = new TextView(parent.getContext());
            holder.relativeLayout.addView(holder.textView);
            convertView = holder.relativeLayout;
            convertView.setTag(holder);
        }else {
            holder = (MyViewHolder) convertView.getTag();
        }

        holder.textView.setText(position+"");
        return convertView;
    }
    class MyViewHolder{
        RelativeLayout relativeLayout;
        TextView textView;
    }
}
