package hxb.xb_testandroidfunction.test_about_recyclerview.recyclerview20191226;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hxb.xb_testandroidfunction.R;

//import android.support.v7.widget.RecyclerView;

/**
 * Created by hxb on 2019/4/18.
 */
public class Recycler2Adapter extends RecyclerView.Adapter<Recycler2Adapter.MyViewHolder> {

    private Context mContext;

    public Recycler2Adapter(Context context){
        this.mContext = context;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        /*ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setLayoutParams(layoutParams);
        return new MyViewHolder(linearLayout);*/

        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.item_test_about_recyclerview2,viewGroup,false);
        return new MyViewHolder(view,i);

    }

    private  final View[] currentView = new View[1];//默认
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

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


        holder.itemView.setOnKeyListener(null);
        holder.textView.setText(("第："+i+"个"));


    }

    @Override
    public int getItemCount() {
        return 100;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }

   /* public class MyViewHolder extends RecyclerView.ViewHolder{
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
    }*/

    public final class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public MyViewHolder(@NonNull View itemView,int type) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv1);
            textView.setTextSize(18);

        }
    }


}
