package hxb.xb_testandroidfunction.test_custom_view.item_decoration;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by hxb on 2017/3/12.
 * recyclerView画线
 */

public class RecycleViewDivider2 extends RecyclerView.ItemDecoration {
    private final String TAG = "RecycleViewDivider2";

    public static DividerConfig init(RecyclerView recyclerView) {
        return new DividerConfig(recyclerView);
    }

    private DividerConfig mConfig;

    private float leftRightF;
    private float topBottomF;

    private RecycleViewDivider2(DividerConfig config) {
        mConfig = config;
        leftRightF = mConfig.mHorizontalSpacing / 2f;
        topBottomF = mConfig.mVerticalSpacing / 2f;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        float leftRight = mConfig.mHorizontalSpacing;
        float topBottom = mConfig.mVerticalSpacing;
        final GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        final GridLayoutManager.SpanSizeLookup lookup = layoutManager.getSpanSizeLookup();

        if (mConfig.mDivider == null || layoutManager.getChildCount() == 0) {
            return;
        }
        //判断总的数量是否可以整除
        int spanCount = layoutManager.getSpanCount();
        float left, right, top, bottom;
        final int childCount = parent.getChildCount();
        if (layoutManager.getOrientation() == GridLayoutManager.VERTICAL) {
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                //将带有颜色的分割线处于中间位置
                final float centerLeft = ((layoutManager.getLeftDecorationWidth(child) + layoutManager.getRightDecorationWidth(child))
                        * spanCount / (spanCount + 1f) + 1f - leftRight) / 2f;
                final float centerTop = (layoutManager.getBottomDecorationHeight(child) + 1f - topBottom) / 2f;
                //得到它在总数里面的位置
                final int position = parent.getChildAdapterPosition(child);
                //获取它所占有的比重
                final int spanSize = lookup.getSpanSize(position);
                //获取每排的位置
                final int spanIndex = lookup.getSpanIndex(position, layoutManager.getSpanCount());
                //判断是否为第一排
                boolean isFirst = layoutManager.getSpanSizeLookup().getSpanGroupIndex(position, spanCount) == 0;

                //画上边的，第一排不需要上边的,只需要在最左边的那项的时候画一次就好
                if (!isFirst && spanIndex == 0) {
                    left = layoutManager.getLeftDecorationWidth(child);
                    right = parent.getWidth() - layoutManager.getLeftDecorationWidth(child);
                    top = (child.getTop() - centerTop) - topBottom;
                    bottom = top + topBottom;
                    mConfig.mDivider.setBounds(Math.round(left), Math.round(top), Math.round(right), Math.round(bottom));
                    mConfig.mDivider.draw(c);
                }
                //最右边的一排不需要右边的
                boolean isRight = spanIndex + spanSize == spanCount;
                if (!isRight) {
                    //计算右边的
                    left = child.getRight() + centerLeft;
                    right = left + leftRight;
                    top = child.getTop();
                    if (!isFirst) {
                        top -= centerTop;
                    }
                    bottom = child.getBottom() + centerTop;
                    mConfig.mDivider.setBounds(Math.round(left), Math.round(top), Math.round(right), Math.round(bottom));
                    mConfig.mDivider.draw(c);
                }
            }
        } else {
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                //将带有颜色的分割线处于中间位置
                final float centerLeft = (layoutManager.getRightDecorationWidth(child) + 1f - leftRight) / 2f;
                final float centerTop = ((layoutManager.getTopDecorationHeight(child) + layoutManager.getBottomDecorationHeight(child))
                        * spanCount / (spanCount + 1f) - topBottom) / 2f;
                //得到它在总数里面的位置
                final int position = parent.getChildAdapterPosition(child);
                //获取它所占有的比重
                final int spanSize = lookup.getSpanSize(position);
                //获取每排的位置
                final int spanIndex = lookup.getSpanIndex(position, layoutManager.getSpanCount());
                //判断是否为第一列
                boolean isFirst = layoutManager.getSpanSizeLookup().getSpanGroupIndex(position, spanCount) == 0;
                //画左边的，第一排不需要左边的,只需要在最上边的那项的时候画一次就好
                if (!isFirst && spanIndex == 0) {
                    left = (child.getLeft() - centerLeft) - leftRight;
                    right = left + leftRight;
                    top = layoutManager.getRightDecorationWidth(child);
                    bottom = parent.getHeight() - layoutManager.getTopDecorationHeight(child);
                    mConfig.mDivider.setBounds(Math.round(left), Math.round(top), Math.round(right), Math.round(bottom));
                    mConfig.mDivider.draw(c);
                }
                //最下的一排不需要下边的
                boolean isRight = spanIndex + spanSize == spanCount;
                if (!isRight) {
                    //计算右边的
                    left = child.getLeft();
                    if (!isFirst) {
                        left -= centerLeft;
                    }
                    right = child.getRight() + centerTop;
                    top = child.getBottom() + centerLeft;
                    bottom = top + leftRight;
                    mConfig.mDivider.setBounds(Math.round(left), Math.round(top), Math.round(right), Math.round(bottom));
                    mConfig.mDivider.draw(c);
                }
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect,view,parent,state);

        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        final GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        final int orientation = layoutManager.getOrientation();//滚动方向
        final int spanCount = layoutManager.getSpanCount();//纵向(总列数) 横向(总行数)
//        final int childPosition = parent.getChildAdapterPosition(view);//当前view在总数中的位置
//        final int spanIndex = lp.getSpanIndex();//纵向(当前view在第几列)  横向(当前view在第几行)
//        final int spanSize = lp.getSpanSize();//纵向(当前view在一行中占几份) 横向(当前view在一列中占几份)
//        final int rowNumber = layoutManager.getSpanSizeLookup().getSpanGroupIndex(childPosition, spanCount);//纵向(表示第几排) 横向(表示第几列)
//        final int lastRowNumber = layoutManager.getSpanSizeLookup().getSpanGroupIndex(layoutManager.getItemCount() - 1, spanCount);


//        outRect.top = Math.round (topBottomF);
//        outRect.bottom = Math.round (topBottomF);
//        outRect.left = Math.round(leftRightF);
//        outRect.right = Math.round(leftRightF);

        outRect.right = mConfig.mHorizontalSpacing;
//        outRect.right =(int) (((float) mConfig.mHorizontalSpacing * (spanCount + 1) / spanCount) - outRect.left);
        outRect.bottom = mConfig.mVerticalSpacing;
//        outRect.bottom = (int) (((float) mConfig.mVerticalSpacing * (spanCount + 1) / spanCount) - outRect.top);

    }



    public static final class DividerConfig {
        private RecyclerView mRecyclerView;
        private int mHorizontalSpacing = 0;
        private int mVerticalSpacing = 0;
        private Drawable mDivider;

        private DividerConfig(RecyclerView recyclerView) {
            mRecyclerView = recyclerView;
        }

        public DividerConfig setSpacing(int horizontalSpacing, int verticalSpacing) {
            mHorizontalSpacing = horizontalSpacing;
            mVerticalSpacing = verticalSpacing;
            return this;
        }

        public DividerConfig setDecorationColor(int color) {
            if (color != 0) {
                mDivider = new ColorDrawable(color);
            }else {
                mDivider = new ColorDrawable(Color.parseColor("#00000000"));
            }
            return this;
        }

        public RecycleViewDivider2 create()  {
            mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    /*
                    * 这里减掉padding 原因有二：
                    * 一 ：在 getItemOffsets() 方法中 采用平分法设置margin 会导致绘制线条有很大的误差
                    * 二 ：去掉四周画分割线
                    *
                    * */

                    GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();

                    mRecyclerView.setPadding(layoutManager.getPaddingLeft(),layoutManager.getPaddingTop(),layoutManager.getPaddingRight() - mHorizontalSpacing,layoutManager.getPaddingBottom()-mVerticalSpacing);
                    mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
            return new RecycleViewDivider2(this);
        }
    }
}
