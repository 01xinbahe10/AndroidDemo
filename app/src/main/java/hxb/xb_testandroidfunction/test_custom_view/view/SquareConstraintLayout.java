package hxb.xb_testandroidfunction.test_custom_view.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;


import androidx.constraintlayout.widget.ConstraintLayout;

import hxb.xb_testandroidfunction.R;

import static android.view.View.MeasureSpec.makeMeasureSpec;


/**
 * Created by hxb on 2019/1/22
 * 正方形 ConstraintLayout布局
 */
public class SquareConstraintLayout extends ConstraintLayout {
    private final String TAG = "SquareConstraintLayout";

    // TypedArray indices
    private static final int CHILD_LENGTH_BASED_ON = R.styleable.SquareConstraintLayout_childLengthBasedOn;
    private static final int CHILD_LENGTH_BASED_INCR = R.styleable.SquareConstraintLayout_childLengthBasedIncr;
    private static final int CHILD_LENGTH_BASED_MULTIPLE = R.styleable.SquareConstraintLayout_childLengthBasedMultiple;

    //常量
    private final int FOLLOW_THE_HEIGHT = 0;//跟随高度
    private final int FOLLOW_THE_WIDTH = 1;//跟随宽度
    private final Axis axis = new Axis();//记录TypedArray数据

    public SquareConstraintLayout(Context context) {
        super(context);
    }

    public SquareConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        getTypeArray(context, attrs);
    }

    public SquareConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getTypeArray(context, attrs);

    }

    private void getTypeArray(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SquareConstraintLayout);
        try {
            axis.setLengthBasedOn(a.getInt(CHILD_LENGTH_BASED_ON, -1));
            axis.setAddIncr(a.getDimensionPixelOffset(CHILD_LENGTH_BASED_INCR, 0));
            axis.setMultipleIncr(a.getFloat(CHILD_LENGTH_BASED_MULTIPLE, 1.0f));
//            Log.e(TAG, "SquareConstraintLayout: ----------------->:  "+mul );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //不通过计算，只需宽度的最大值 和 高度最大值相等 就是正方形
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
//        int childWidthSize = getMeasuredWidth();
//        // 高度和宽度一样
//        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);//默认


//        通过计算拿到该控件的准确值
//        int hPadding = getPaddingLeft() + getPaddingRight();
//        int vPadding = getPaddingTop() + getPaddingBottom();
//
//        int widthSpecSansPadding = adjust(widthMeasureSpec, -hPadding);
//        int heightSpecSansPadding = adjust(heightMeasureSpec, -vPadding);
//
//        int modeWidth = MeasureSpec.getMode(widthSpecSansPadding);
//        int modeHeight = MeasureSpec.getMode(widthSpecSansPadding);

        int widthSize = 0;
        int heightSize = 0;
        switch (axis.getLengthBasedOn()){
            case FOLLOW_THE_WIDTH:
                int childWidthSize = getMeasuredWidth();
                // 高度和宽度一样
                heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);//默认
                widthSize = MeasureSpec.getSize(widthMeasureSpec);

                if (axis.getAddIncr() != 0) {
                    widthSize = widthSize + axis.getAddIncr();
                } else if ((0.0f < Math.abs(axis.getMultipleIncr()) && Math.abs(axis.getMultipleIncr()) < 1.0f) || Math.abs(axis.getMultipleIncr()) > 1.0f) {
                    widthSize = (int) (widthSize * axis.getMultipleIncr());
                } else {
                    widthSize = 0;
                }
                break;
            case FOLLOW_THE_HEIGHT:
                int childHeigthSize = getMeasuredHeight();
                // 高度和宽度一样
                heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childHeigthSize, MeasureSpec.EXACTLY);//默认
                heightSize = MeasureSpec.getSize(heightMeasureSpec);



                break;
            default:
                widthSize = 0;
                heightSize = 0;
                break;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec + widthSize);
    }

    static int adjust(int measureSpec, int delta) {
        return makeMeasureSpec(
                MeasureSpec.getSize(measureSpec + delta), MeasureSpec.getMode(measureSpec));
    }


    final class Axis {
        private int lengthBasedOn = -1;
        private int addIncr = 0;//加法数增量
        private float multipleIncr = 1.0f;//倍数增量

        public void setLengthBasedOn(int childLengthBasedOn) {
            this.lengthBasedOn = childLengthBasedOn;
        }

        public void setAddIncr(int addIncr) {
            this.addIncr = addIncr;
        }

        public void setMultipleIncr(float multipleIncr) {
            this.multipleIncr = multipleIncr;
        }

        public int getLengthBasedOn() {
            return lengthBasedOn;
        }

        public int getAddIncr() {
            return addIncr;
        }

        public float getMultipleIncr() {
            return multipleIncr;
        }
    }
}
