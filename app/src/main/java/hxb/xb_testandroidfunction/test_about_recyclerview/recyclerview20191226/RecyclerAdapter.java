package hxb.xb_testandroidfunction.test_about_recyclerview.recyclerview20191226;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import android.support.v7.widget.RecyclerView;

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
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setLayoutParams(layoutParams);
        return new MyViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        holder.textView.setText("第："+i+"个");
        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    holder.itemView.setBackgroundColor(Color.RED);
                }else {
                    holder.itemView.setBackgroundColor(Color.WHITE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 60;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (!(manager instanceof androidx.recyclerview.widget.GridLayoutManager)) {
            return;
        }
        androidx.recyclerview.widget.GridLayoutManager gridManager = ((androidx.recyclerview.widget.GridLayoutManager) manager);
        final int spanSize = gridManager.getSpanCount();
        gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0){
                    return spanSize;
                }else {
                    return spanSize/2;
                }

            }
        });

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


            itemView.setBackgroundColor(Color.WHITE);
            itemView.setFocusable(true);
        }
    }
}
