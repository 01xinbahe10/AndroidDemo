package hxb.xb_testandroidfunction.test_about_recyclerview.recyclerview20191226;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by hxb on  2019/12/26
 */
public class CustomizeRecyclerView extends RecyclerView {
    private static final String TAG = "CustomizeRecyclerView";

    private Context mContext;
    //是否可以纵向移出
    private boolean mCanFocusOutVertical = true;
    //是否可以横向移出
    private boolean mCanFocusOutHorizontal = true;
    //焦点移出recyclerview的事件监听
    private FocusLostListener mFocusLostListener;
    //焦点移入recyclerview的事件监听
    private FocusGainListener mFocusGainListener;
    //默认第一次选中第一个位置
    private int mCurrentFocusPosition = 0;
    //是否滚动
    private boolean isScrolling = false;
    //临时记录当前焦点的view
    private View[] mTempRecordFocusViews = new View[2];

    public CustomizeRecyclerView(@NonNull Context context) {
        this(context, null, 0);
    }

    public CustomizeRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomizeRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;

        setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        setChildrenDrawingOrderEnabled(true);
        setItemAnimator(null);
        this.setFocusable(true);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        Log.e(TAG, "onFocusChanged: 是否有焦点 "+gainFocus );
    }

    @Override
    public View focusSearch(int direction) {
        return super.focusSearch(direction);
    }

    //覆写focusSearch寻焦策略
    @Override
    public View focusSearch(View focused, int direction) {
        Log.i(TAG, "focusSearch " + focused + ",direction= " + direction);
        View view = super.focusSearch(focused, direction);
        if (focused == null) {
            return view;
        }
        if (view != null) {
            //该方法返回焦点view所在的父view,如果是在recyclerview之外，就会是null.所以根据是否是null,来判断是否是移出了recyclerview
            View nextFocusItemView = findContainingItemView(view);
            if (nextFocusItemView == null) {
                if (!mCanFocusOutVertical && (direction == View.FOCUS_DOWN || direction == View.FOCUS_UP)) {
                    //屏蔽焦点纵向移出recyclerview
                    return focused;
                }
                if (!mCanFocusOutHorizontal && (direction == View.FOCUS_LEFT || direction == View.FOCUS_RIGHT)) {
                    //屏蔽焦点横向移出recyclerview
                    return focused;
                }
                //调用移出的监听
                if (mFocusLostListener != null) {
                    mFocusLostListener.onFocusLost(focused, direction);
                }
                return view;
            }
        }
        return view;
    }


    @Override
    public void requestChildFocus(View child, View focused) {
        Log.i(TAG, "nextchild= " + child + ",focused = " + focused);
        if (!hasFocus()) {
            //recyclerview 子view 重新获取焦点，调用移入焦点的事件监听
            if (mFocusGainListener != null) {
                mFocusGainListener.onFocusGain(child, focused);
            }
        }
        Log.i(TAG, "focusPos 2= " + mCurrentFocusPosition);
        int focusPosition =  getChildViewHolder(child).getLayoutPosition();
        Log.i(TAG, "focusPos = " + focusPosition);
        if (Math.abs(mCurrentFocusPosition - focusPosition) > 2){
            mTempRecordFocusViews[0] = child;
            mTempRecordFocusViews[1] = focused;
            smoothScrollToCenter(mCurrentFocusPosition);
        }else {
            super.requestChildFocus(child, focused);//执行过super.requestChildFocus之后hasFocus会变成true
            mCurrentFocusPosition = focusPosition;
        }

    }

    //实现焦点记忆的关键代码
    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        View view = null;
        if (this.hasFocus() || mCurrentFocusPosition < 0 || (view = getLayoutManager().findViewByPosition(mCurrentFocusPosition)) == null) {
            super.addFocusables(views, direction, focusableMode);
        } else if (view.isFocusable()) {
            //将当前的view放到Focusable views列表中，再次移入焦点时会取到该view,实现焦点记忆功能
            views.add(view);
        } else {
            super.addFocusables(views, direction, focusableMode);
        }
    }

    /**
     *    * 控制当前焦点最后绘制，防止焦点放大后被遮挡
     *    * 原顺序123456789，当4是focus时，绘制顺序变为123567894
     *    * @param childCount
     *    * @param i
     *    * @return
     *    
     */
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        View focusedChild = getFocusedChild();
        /*Log.i(TAG, "focusedChild =" + (focusedChild == null));*/
        if (focusedChild == null) {
            return super.getChildDrawingOrder(childCount, i);
        } else {
            int index = indexOfChild(focusedChild);
            /*Log.i(TAG, " index = " + index + ",i=" + i + ",count=" + childCount);*/
            if (i == childCount - 1) {
                return index;
            }
            if (i < index) {
                return i;
            }
            return i + 1;
        }
    }


    public void smoothScrollToCenter(int position) {
        isScrolling = true;
        RecyclerView.SmoothScroller smoothScroller = new CenterScroller(mContext);
        smoothScroller.setTargetPosition(position);
        this.getLayoutManager().startSmoothScroll(smoothScroller);

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
            /*if (targetView != null){
                Log.e(TAG, "onStop: LLLLLLLLLLLLLLL  111 "+this.getChildPosition(targetView) );
                targetView.requestFocus();
            }*/
            super.onTargetFound(targetView, state, action);

        }

        @Override
        protected void onStop() {
//            clearAllChildFocus();
            for (View view:mTempRecordFocusViews){
                view.clearFocus();
            }
            View targetView = findViewByPosition(getTargetPosition());
            if (targetView != null) {
                targetView.requestFocus();
            }
            super.onStop();
            isScrolling = false;

        }
    }


    public interface FocusLostListener {
        void onFocusLost(View lastFocusChild, int direction);
    }

    public interface FocusGainListener {
        void onFocusGain(View child, View focued);
    }


    public View findViewByPosition(int position){
        int childCount = this.getChildCount();
        int midCount = childCount >>> 1;//相当于childCount / 2;
        for (int i = 0; i <= midCount ; i++) {
            View view = this.getChildAt(i);
            if (this.getChildLayoutPosition(view) == position){
                return view;
            }
            View view2 = this.getChildAt((childCount - 1 - i));
            if (this.getChildLayoutPosition(view2) == position){
                return view2;
            }
        }

        return null;
    }

    private void clearAllChildFocus(){
        int childCount = this.getChildCount();
        for (int i = 0; i <childCount ; i++) {
            View view = this.getChildAt(i);
            if (null == view){
                continue;
            }
            view.clearFocus();
        }
    }

    public boolean isCanFocusOutVertical() {
        return mCanFocusOutVertical;
    }

    public void setCanFocusOutVertical(boolean canFocusOutVertical) {
        mCanFocusOutVertical = canFocusOutVertical;
    }

    public boolean isCanFocusOutHorizontal() {
        return mCanFocusOutHorizontal;
    }

    public void setCanFocusOutHorizontal(boolean canFocusOutHorizontal) {
        mCanFocusOutHorizontal = canFocusOutHorizontal;
    }

    public void setFocusLostListener(FocusLostListener focusLostListener) {
        this.mFocusLostListener = focusLostListener;
    }

    public void setGainFocusListener(FocusGainListener focusListener) {
        this.mFocusGainListener = focusListener;
    }

    public int getCurrentFocusPosition(){
        return mCurrentFocusPosition;
    }
}
