package hxb.xb_testandroidfunction.test_about_recyclerview.tvlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.recyclerview.widget.RecyclerView.NO_ID;
import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

/**
 * Created by hxb on  2019/12/26
 *
 * 弃用  2020-10-21
 */
@Deprecated
public final class CustomGridLayoutManager extends GridLayoutManager {
    private static final String TAG = "CustomGridLayoutManager";

    private Context mContext;

    private boolean isInLayout;
    private boolean isScrolling;
    private int mFocusPosition = NO_POSITION;//记录当前有焦点的view的position

    private RecyclerView mRecyclerView;
    private CustomizeGridRecyclerView mCustomizeGridRecyclerView;

    private OnChildSelectedListener mChildSelectedListener;

    public CustomGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public CustomGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
        init(context);
    }

    public CustomGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        mRecyclerView = view;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        isInLayout = true;
        try {
            super.onLayoutChildren(recycler, state);
            if (getFocusedChild() != null && !isScrolling) {
                int position = getPosition(getFocusedChild());
                if (getChildCount() - position >= getSpanCount()) {
                    smoothScrollToPosition(position, false);
                }
            }
        } catch (IndexOutOfBoundsException ignored) {
        }
        isInLayout = false;
    }

    @Override
    public View onFocusSearchFailed(View focused, int focusDirection, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return super.onFocusSearchFailed(focused, focusDirection, recycler, state);
    }

    @Override
    public boolean onRequestChildFocus(@NonNull RecyclerView parent, @NonNull RecyclerView.State state, @NonNull View child, @Nullable View focused) {
        int position = getPositionByView(child);
        Log.d(TAG, "onRequestChildFocus:  当前view 的position = " + position + "     被选中的position = " + recyclerView().getCurrentFocusPosition() + "      focused = " + getPositionByView(focused));
        if (position == NO_POSITION) {
            return true;
        }
        if (!isInLayout) {
            smoothScrollToPosition(position, false);
        }
        return true;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        smoothScrollToCenter(position,this.isRequestFocus);
    }

    /**
     * 该方法排除RecyclerView对象的一些错误
     * */
    private CustomizeGridRecyclerView recyclerView(){
        if (null == mRecyclerView){
            throw new NullPointerException("recyclerView cannot be empty! Please check if the object is initialized!");
        }
        if (!(mRecyclerView instanceof CustomizeGridRecyclerView)){
            throw new ClassCastException("recyclerView is not CuCustomizeGridRecyclerView, Please check the type!");
        }
        if (null == mCustomizeGridRecyclerView) {
            return mCustomizeGridRecyclerView = (CustomizeGridRecyclerView) mRecyclerView;
        }
        return mCustomizeGridRecyclerView;
    }

    /**
     * 获取当前view 的位置
     * */
    private int getPositionByView(View view) {
        if (view == null) {
            return NO_POSITION;
        }
        GridLayoutManager.LayoutParams params = (LayoutParams) view.getLayoutParams();
        if (params == null || params.isItemRemoved()) {
            // when item is removed, the position value can be any value.
            return NO_POSITION;
        }
        return params.getViewLayoutPosition();
    }

    /**
     * 回调选中的view
     * */
    private void dispatchChildSelected(){
        if (null == mChildSelectedListener){
            return;
        }
        View view = mFocusPosition == NO_POSITION ? null : findViewByPosition(mFocusPosition);
        if (null != view) {
            RecyclerView.ViewHolder vh = recyclerView().getChildViewHolder(view);
            mChildSelectedListener.onChildSelected(recyclerView(), view, mFocusPosition, vh == null ? NO_ID : vh.getItemId());
        }else {
            mChildSelectedListener.onChildSelected(recyclerView(), null, NO_POSITION, NO_ID);
        }
    }

    /**
     * 滚动到指定位置
     * @param position 指定位置
     * @param isRequestFocus 滚动到指定位置是否请求焦点
     * */
    private boolean isRequestFocus = false;//滚动完是否请求焦点
    public void smoothScrollToPosition(int position, boolean isRequestFocus){
        if (isScrolling || isSmoothScrolling()){
            return;
        }
        isScrolling = true;
        this.isRequestFocus = isRequestFocus;
        recyclerView().smoothScrollToPosition(position);
    }

    private void smoothScrollToCenter(int position, boolean isRequestFocus) {
        recyclerView().stopScroll();
        RecyclerView.SmoothScroller smoothScroller = new CenterScroller(mContext, isRequestFocus);
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);

    }

    /**
     * 设置选中监听事件
     * */
    public void setOnChildSelectedListener(OnChildSelectedListener listener){
        mChildSelectedListener = listener;
    }

    /**自定义滚动效果的Scroller*/
    private class CenterScroller extends LinearSmoothScroller {

        private static final float MILLISECONDS_PER_INCH = 25f; //default is 25f (bigger = slower)
        private boolean isRequestFocus;

        public CenterScroller(Context context, boolean isRequestFocus) {
            super(context);
            this.isRequestFocus = isRequestFocus;
        }

        // 这里计算滚动到中部的偏移量
        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            float rollingDistance = (boxStart + (boxEnd - boxStart) / 2f) - (viewStart + (viewEnd - viewStart) / 2f);
            return (int) rollingDistance;

        }

        // 滚动速度控制
        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
           /* float ll = MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
            Log.e(TAG, "calculateSpeedPerPixel: >>>>>>>>>>>>>>>>>>>>>>    "+ll +"      "+displayMetrics.densityDpi);*/
            /*return super.calculateSpeedPerPixel(displayMetrics);*/
            return 0.20f;
        }

        @Override
        protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
            super.onTargetFound(targetView, state, action);

        }

        @Override
        protected void onStop() {
            if (isRequestFocus) {
                for (View view : recyclerView().getTempRecordFocusViews()) {
                    if (null == view){
                        continue;
                    }
                    view.clearFocus();
                }

                View targetView = findViewByPosition(getTargetPosition());
                if (targetView != null) {
                    targetView.requestFocus();
                }

            }

            super.onStop();
            isScrolling = false;
            int currentFocusPosition = getTargetPosition();
            if (mFocusPosition != currentFocusPosition){
                mFocusPosition =  currentFocusPosition;
                dispatchChildSelected();
            }
        }
    }

    protected int getFocusPosition(){
        return mFocusPosition;
    }

    private int getPositionByIndex(int index) {
        return getPositionByView(getChildAt(index));
    }

    private int findImmediateChildIndex(View view) {
        while (view != null && view != recyclerView()) {
            int index = recyclerView().indexOfChild(view);
            if (index >= 0) {
                return index;
            }
            view = (View) view.getParent();
        }
        return NO_POSITION;
    }

}
