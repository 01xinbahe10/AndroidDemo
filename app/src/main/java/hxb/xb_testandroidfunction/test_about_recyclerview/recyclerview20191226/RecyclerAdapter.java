package hxb.xb_testandroidfunction.test_about_recyclerview.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
//import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by hxb on 2019/4/18.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private Context mContext;

    public RecyclerAdapter(Context context){
        this.mContext = context;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300);
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setLayoutParams(layoutParams);
        return new MyViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        holder.textView.setText("第："+i+"个");
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            LinearLayout viewGroup  = (LinearLayout) itemView;
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textView = new TextView(mContext);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(layoutParams);
            textView.setBackgroundColor(Color.GREEN);
            textView.setPadding(50,50,50,50);
            viewGroup.addView(textView);

            ViewGroup.LayoutParams layoutParams2 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,2);
            View viewLine = new View(mContext);
            viewLine.setBackgroundColor(Color.BLUE);
            viewLine.setLayoutParams(layoutParams2);
            viewGroup.addView(viewLine);
        }
    }
}
