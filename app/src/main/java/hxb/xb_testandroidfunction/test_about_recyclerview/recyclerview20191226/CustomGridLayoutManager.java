package hxb.xb_testandroidfunction.test_about_recyclerview.recyclerview20191226;

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

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

/**
 * Created by hxb on  2019/12/26
 */
public class CustomGridLayoutManager extends GridLayoutManager {
    private static final String TAG = "CustomGridLayoutManager";

    private Context mContext;

    private boolean isInLayout;
    private boolean isScrolling;

    private CustomizeRecyclerView mCustomizeRecyclerView;

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

    private void init(Context context){
        mContext = context;
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        if (view instanceof CustomizeRecyclerView){
            mCustomizeRecyclerView = (CustomizeRecyclerView) view;
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        isInLayout = true;
        try {
            super.onLayoutChildren(recycler, state);
            if (getFocusedChild() != null &&  !isScrolling) {
                int position = getPosition(getFocusedChild());
                if (getChildCount() - position >= getSpanCount()) {
                    smoothScrollToCenter(position);
                }
            }
        } catch (IndexOutOfBoundsException ignored) {}
        isInLayout = false;
    }

    @Override
    public boolean onRequestChildFocus(@NonNull RecyclerView parent, @NonNull RecyclerView.State state, @NonNull View child, @Nullable View focused) {
        int position = getPositionByView(child);
        Log.e(TAG, "onRequestChildFocus:  当前view 的position = "+position +"     被选中的position = "+mCustomizeRecyclerView.getCurrentFocusPosition()+"      focused = "+getPositionByView(focused));
        if (position == NO_POSITION) {
            return true;
        }
//        if (Math.abs(mCustomizeRecyclerView.getCurrentFocusPosition() - position) > getSpanCount()){
//            smoothScrollToCenter(mCustomizeRecyclerView.getCurrentFocusPosition());
//        }else {
            if (!isInLayout && !isScrolling) {
                smoothScrollToCenter(position);
            }
//        }

         return true;
    }

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


    public void smoothScrollToCenter(int position) {
        isScrolling = true;
        RecyclerView.SmoothScroller smoothScroller = new CenterScroller(mContext);
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);

    }

    // 自定义滚动效果的Scroller
    private class CenterScroller extends LinearSmoothScroller {

        private static final float MILLISECONDS_PER_INCH = 50f; //default is 25f (bigger = slower)

        public CenterScroller(Context context) {
            super(context);
        }
        // 这里计算滚动到中部的偏移量
        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2);
        }
        // 滚动速度控制
        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
        }

        @Override
        protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
            super.onTargetFound(targetView, state, action);
        }

        @Override
        protected void onStop() {

          /*  View targetView = findViewByPosition(getTargetPosition());
            if (hasFocus() && targetView != null) {
                targetView.requestFocus();
            }*/

            super.onStop();
            isScrolling = false;


        }
    }
}
