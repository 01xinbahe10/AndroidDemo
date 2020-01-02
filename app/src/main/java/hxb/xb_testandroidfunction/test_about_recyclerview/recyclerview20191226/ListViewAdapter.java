package hxb.xb_testandroidfunction.test_about_recyclerview.recyclerview20191226;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import hxb.xb_testandroidfunction.R;

/**
 * Created by hxb on  2020/1/2
 */
public class ListViewAdapter extends BaseAdapter {

    @Override
    public int getCount() {
        return 50;
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
    public View getView(int position,View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView){
            holder = new ViewHolder();
            RelativeLayout.LayoutParams rl_lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,80);
            holder.relativeLayout = new RelativeLayout(parent.getContext());
            holder.relativeLayout.setLayoutParams(rl_lp);
//            holder.relativeLayout.setGravity(RelativeLayout.CENTER_HORIZONTAL);
            holder.relativeLayout.setGravity(Gravity.CENTER);
            holder.textView = new TextView(parent.getContext());
            holder.textView.setTextColor(Color.WHITE);
            holder.relativeLayout.addView(holder.textView);


            convertView = holder.relativeLayout;
            convertView.setBackground(ContextCompat.getDrawable(parent.getContext(), R.drawable.d_focused_status));
            convertView.setFocusable(true);
            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(("当前脚标："+position));
        return convertView;
    }

    final class ViewHolder{
        RelativeLayout relativeLayout;
        TextView textView;
    }
}
