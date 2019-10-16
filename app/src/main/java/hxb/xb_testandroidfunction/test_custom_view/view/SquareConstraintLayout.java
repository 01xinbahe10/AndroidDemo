package hxb.xb_testandroidfunction.test_custom_view.view;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;

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
        getTypeArray(context,attrs);
    }

    public SquareConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getTypeArray(context,attrs);

    }

    private void getTypeArray(Context context,AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SquareConstraintLayout);
        try {
            axis.setChildLengthBasedOn(a.getInt(CHILD_LENGTH_BASED_ON, -1));
            axis.setAddIncr(a.getDimensionPixelOffset(CHILD_LENGTH_BASED_INCR, 0));
            axis.setMultipleIncr(a.getFloat(CHILD_LENGTH_BASED_MULTIPLE, 1.0f));
            Log.e(TAG, "SquareConstraintLayout: ----------------->:  "+axis.getMultipleIncr() );
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //不通过计算，只需宽度的最大值 和 高度最大值相等 就是正方形
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        int childWidthSize = getMeasuredWidth();
        // 高度和宽度一样
        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);//默认
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        //通过计算拿到该控件的准确值
        int hPadding = getPaddingLeft() + getPaddingRight();
        int vPadding = getPaddingTop() + getPaddingBottom();

        int widthSpecSansPadding = adjust(widthMeasureSpec, -hPadding);
        int heightSpecSansPadding = adjust(heightMeasureSpec, -vPadding);

//        int modeWidth = MeasureSpec.getMode(widthSpecSansPadding);
//        int modeHeight = MeasureSpec.getMode(widthSpecSansPadding);
        int widthSize = MeasureSpec.getSize(widthSpecSansPadding);
        int heightSize = MeasureSpec.getSize(heightSpecSansPadding);
        switch (axis.getChildLengthBasedOn()) {
            case FOLLOW_THE_WIDTH:
                if (axis.getAddIncr() != 0) {
                    widthSize = widthSize+axis.getAddIncr();
                    break;
                } else if ((0.0f < Math.abs(axis.getMultipleIncr()) && Math.abs(axis.getMultipleIncr()) < 1.0f) || Math.abs(axis.getMultipleIncr()) > 1.0f) {
                    widthSize = (int) ((float)widthSize*axis.getMultipleIncr());
                    break;
                }else {
                    widthSize = 0;
                }
                break;
            case FOLLOW_THE_HEIGHT:
                break;
             default:
                 widthSize = 0;
//                 heightSize = 0;
                 break;
        }

        Log.e(TAG, "onMeasure:     sizeWidth:"+widthSize +"  sizeHeight:"+heightSize);
//        Log.e(TAG, "onMeasure:     sizeWidth:"+widthMeasureSpec +"  sizeHeight:"+heightMeasureSpec);
//        setMeasuredDimension(widthSize, heightSize);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec + widthSize);
    }


    static int adjust(int measureSpec, int delta) {
        return makeMeasureSpec(
                MeasureSpec.getSize(measureSpec + delta), MeasureSpec.getMode(measureSpec));
    }


    final class Axis {
        private int childLengthBasedOn = -1;
        private int addIncr = 0;//加法数增量
        private float multipleIncr = 1.0f;//倍数增量

        public void setChildLengthBasedOn(int childLengthBasedOn) {
            this.childLengthBasedOn = childLengthBasedOn;
        }

        public void setAddIncr(int addIncr) {
            this.addIncr = addIncr;
        }

        public void setMultipleIncr(float multipleIncr) {
            this.multipleIncr = multipleIncr;
        }

        public int getChildLengthBasedOn() {
            return childLengthBasedOn;
        }

        public int getAddIncr() {
            return addIncr;
        }

        public float getMultipleIncr() {
            return multipleIncr;
        }
    }
}
