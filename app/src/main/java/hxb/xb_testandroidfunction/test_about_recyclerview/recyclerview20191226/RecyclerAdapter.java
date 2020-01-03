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
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private Context mContext;

    public RecyclerAdapter(Context context){
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
        if (i == 0){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_test_about_recyclerview1,viewGroup,false);
            return new MyViewHolder(view,i);
        }
        view = LayoutInflater.from(mContext).inflate(R.layout.item_test_about_recyclerview2,viewGroup,false);
        return new MyViewHolder(view,i);

    }

    private  final View[] currentView = new View[1];//默认
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
       /* holder.textView.setText("第："+i+"个");
        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    holder.itemView.setBackgroundColor(Color.RED);
                }else {
                    holder.itemView.setBackgroundColor(Color.WHITE);
                }
            }
        });*/



       int type = getItemViewType(i);

        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    if (type == 0){
                        if (null == currentView[0]) {
                            currentView[0] = holder.viewVideo;
                        }
                        revertViewStatus(currentView[0],false);
                    }else {
                        revertViewStatus(currentView[0],true);
                    }
                    holder.itemView.setBackgroundColor(Color.RED);
                }else {
                    revertViewStatus(currentView[0],true);
                    holder.itemView.setBackgroundColor(Color.WHITE);
                }
            }
        });
       if (type == 0){
           holder.itemView.setOnKeyListener(new KeyListener(holder.itemView));
       }else {
           holder.itemView.setOnKeyListener(null);
           holder.textView.setText(("第："+i+"个"));
       }

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
        View viewVideo,viewAd1,viewAd2;
        TextView textView;
        public MyViewHolder(@NonNull View itemView,int type) {
            super(itemView);
            if (type == 0){
                viewVideo = itemView.findViewById(R.id.viewVideo);
                viewAd1 = itemView.findViewById(R.id.viewAd1);
                viewAd2 = itemView.findViewById(R.id.viewAd2);
            }else {
                textView = itemView.findViewById(R.id.tv1);
            }
        }
    }

    private View findNextFocusView(View root,View view,int direction){
        if (null == view){
            return null;
        }
        switch (direction){
            case View.FOCUS_LEFT:
                return root.findViewById(view.getNextFocusLeftId());
            case View.FOCUS_RIGHT:
                return root.findViewById(view.getNextFocusRightId());
            case View.FOCUS_UP:
                return root.findViewById(view.getNextFocusUpId());
            case View.FOCUS_DOWN:
                return root.findViewById(view.getNextFocusDownId());
        }

        return null;
    }

    private void revertViewStatus(View viewStatus ,boolean isRevert){
        if (null == viewStatus){
            return;
        }
        switch (viewStatus.getId()){
            case R.id.viewVideo:
                viewStatus.setBackgroundColor(isRevert?Color.parseColor("#FF9800"):Color.RED);
                break;
            case R.id.viewAd1:
                viewStatus.setBackgroundColor(isRevert?Color.parseColor("#4CAF50"):Color.RED);
                break;
            case R.id.viewAd2:
                viewStatus.setBackgroundColor(isRevert?Color.parseColor("#673AB7"):Color.RED);
                break;
        }
    }

    private class KeyListener implements View.OnKeyListener{
        View root;
        public KeyListener(View root){
            this.root = root;
        }

        public void setViewRoot(View root){
            this.root = root;
        }
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            int action = event.getAction();
            if (action == KeyEvent.ACTION_DOWN){
                Log.e("TAG", "onKey: >>>>>>>>>>>>>>>>>>>>>>拉拉   " + keyCode);
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        View viewLeft = findNextFocusView(root,currentView[0],View.FOCUS_LEFT);
                        Log.e("TAG", "onKey: >>>>>>>>>>>>>>>>>>>>>>拉拉   LEFT1" );
                        if (null != viewLeft){
                           /* if (viewLeft.getId() == R.id.viewBoundary){
                                return true;
                            }*/
                            revertViewStatus(currentView[0],true);
                            revertViewStatus(viewLeft,false);
                            currentView[0] = viewLeft;
                            Log.e("TAG", "onKey: >>>>>>>>>>>>>>>>>>>>>>拉拉   LEFT2" );
                            return true;
                        }
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        View viewRight = findNextFocusView(root,currentView[0],View.FOCUS_RIGHT);
                        Log.e("TAG", "onKey: >>>>>>>>>>>>>>>>>>>>>>拉拉   RIGHT1" );
                        if (null != viewRight){
                            revertViewStatus(currentView[0],true);
                            revertViewStatus(viewRight,false);
                            currentView[0] = viewRight;
                            Log.e("TAG", "onKey: >>>>>>>>>>>>>>>>>>>>>>拉拉   RIGHT2" );
                            return true;
                        }
                        break;
                    case KeyEvent.KEYCODE_DPAD_UP:
                        View viewUp = findNextFocusView(root,currentView[0],View.FOCUS_UP);
                        Log.e("TAG", "onKey: >>>>>>>>>>>>>>>>>>>>>>拉拉   UP1" );
                        if (null != viewUp){
                            revertViewStatus(currentView[0],true);
                            revertViewStatus(viewUp,false);
                            currentView[0] = viewUp;
                            Log.e("TAG", "onKey: >>>>>>>>>>>>>>>>>>>>>>拉拉   UP2" );
                            return true;
                        }
                        break;
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        View viewDown = findNextFocusView(root,currentView[0],View.FOCUS_DOWN);
                        Log.e("TAG", "onKey: >>>>>>>>>>>>>>>>>>>>>>拉拉   DOWN1" );
                        if (null != viewDown){
                            revertViewStatus(currentView[0],true);
                            revertViewStatus(viewDown,false);
                            currentView[0] = viewDown;
                            Log.e("TAG", "onKey: >>>>>>>>>>>>>>>>>>>>>>拉拉   DOWN2" );
                            return true;
                        }
                        revertViewStatus(currentView[0],true);
//                        currentView[0] = null;
                        break;

                }

            }
            return false;
        }
    }

}
