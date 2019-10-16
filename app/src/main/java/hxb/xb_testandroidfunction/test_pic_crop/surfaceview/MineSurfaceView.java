package hxb.xb_testandroidfunction.test_pic_crop.surfaceview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import hxb.xb_testandroidfunction.R;
import hxb.xb_testandroidfunction.test_pic_crop.surfaceview.edge.Edge;
import hxb.xb_testandroidfunction.test_pic_crop.surfaceview.handle.CropWindowEdgeSelector;
import hxb.xb_testandroidfunction.test_pic_crop.surfaceview.util.CatchEdgeUtil;
import hxb.xb_testandroidfunction.test_pic_crop.surfaceview.util.UIUtil;

/**
 * Created by hxb on 2019/5/15.
 * 改图片裁剪基于SurfaceView实现，目的是避免在主UI线程造成卡顿
 *
 */

public class MineSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private final String TAG = "MineSurfaceView";
    //记录MineSurfaceView创建和消亡的状态
    private boolean mSurfaceAvailable;
    private SurfaceHolder mHolder;
    private Thread mDrawThread;
    //记录MineSurfaceView的控件大小
    private RectF mVRect = new RectF();
    //记录图片的大小
    private RectF mDRect = new RectF();
    //设置图片显示比例状态
    private Matrix mMatrix = new Matrix();
    private Drawable mDrawable;
    private Bitmap mBitmap;
    //保证首次初始化
    private boolean mInit = true;


    //裁剪框边框画笔
    private Paint mBorderPaint;
    //裁剪框九宫格画笔
    private Paint mGuidelinePaint;
    //绘制裁剪边框四个角的画笔
    private Paint mCornerPaint;
    //绘制裁剪框以外背景景色
    private Paint mBgColorPaint;
    //判断手指位置是否处于缩放裁剪框位置的范围：如果是当手指移动的时候裁剪框会相应的变化大小
    //否则手指移动的时候就是拖动裁剪框使之随着手指移动
    private float mScaleRadius;
    private float mCornerThickness;
    private float mBorderThickness;
    //四个角小短边的长度
    private float mCornerLength;
    //用来表示图片边界的矩形
    private RectF mBitmapRect = new RectF();
    //手指位置距离裁剪框的偏移量
    private PointF mTouchOffset = new PointF();
    private CropWindowEdgeSelector mPressedCropWindowEdgeSelector;
    public static final int NO_VALUE = 0;
    //获取宽高比例
    private float mWideRatio = NO_VALUE;
    private float mHighRatio = NO_VALUE;


    public MineSurfaceView(Context context) {
        super(context);
        init(context, null);
    }

    public MineSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MineSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MineSurfaceView);
            if (array.hasValue(R.styleable.MineSurfaceView_src)) {
                mDrawable = array.getDrawable(R.styleable.MineSurfaceView_src);
            }
            mWideRatio = array.getInt(R.styleable.MineSurfaceView_wide_ratio,NO_VALUE);
            mHighRatio = array.getInt(R.styleable.MineSurfaceView_high_ratio,NO_VALUE);
            array.recycle();
        }
        if (null == mDrawable) {
            mDrawable = new BitmapDrawable(context.getResources(), Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888));
        }

        /*
         * drawable.setCallback():
         * 如果你想创建一个动画可扩展实现此接口。使用setCallback()
         * 申请将此扩展绑定到Drawable视图，该视图会安排执行动画。
         *
         * drawable.setLevel(0):
         * 如果有Drawable有层次 默认为第一层
         *
         * 可以注释掉这两句代码，实测无影响
         * */
        mDrawable.setCallback(this);
        mDrawable.setLevel(0);


        /*
         * 当获取到了Drawable等信息，就转为Bitmap
         * */
        mBitmap = getBitmap(mDrawable);


        toolInit(context);//初始化画笔等工具(放在这里是防止多次初始化，浪费性能)

        mHolder = getHolder();
        mHolder.addCallback(this);


    }

    /**
     * 初始化画笔等工具
     */
    private void toolInit(Context context) {
        mBorderPaint = new Paint();
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(UIUtil.dip2px(context, 2));
        mBorderPaint.setColor(Color.parseColor("#AAFFFFFF"));

        mGuidelinePaint = new Paint();
        mGuidelinePaint.setStyle(Paint.Style.STROKE);
        mGuidelinePaint.setStrokeWidth(UIUtil.dip2px(context, 1));
        mGuidelinePaint.setColor(Color.parseColor("#AAFFFFFF"));


        mCornerPaint = new Paint();
        mCornerPaint.setStyle(Paint.Style.STROKE);
        mCornerPaint.setStrokeWidth(UIUtil.dip2px(context, 3.5f));
        mCornerPaint.setColor(Color.WHITE);


        mBgColorPaint = new Paint();
        mBgColorPaint.setStyle(Paint.Style.FILL);
        mBgColorPaint.setColor(Color.parseColor("#7D000000"));


        mScaleRadius = UIUtil.dip2px(context, 24);
        mBorderThickness = UIUtil.dip2px(context, 2);
        mCornerThickness = UIUtil.dip2px(context, 3);
        mCornerLength = UIUtil.dip2px(context, 18);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceAvailable = true;
        mInit = true;

        if (null == mDrawThread) {
            mDrawThread = new Thread(this);
            mDrawThread.start();
        } else {//当Activity onRestart();时,需要刷新
            mDrawThread.run();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceAvailable = false;

    }

    @Override
    public void run() {//属于SurfaceView线程操作
        if (!mSurfaceAvailable) {
            return;
        }
        Canvas canvas = null;
        try {
            canvas = mHolder.lockCanvas();
            if (mInit) {
                /*
                 * 初始化图片显示的Scale
                 * */
                mVRect.set(0, 0, getWidth(), getHeight());
                mDRect.set(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
//                Log.e(TAG, "run: ---------------  width: " + mBitmap.getWidth() + "            height: " + mBitmap.getHeight());
                //设置图片 居中铺满 展示在Canvas
                mMatrix.setRectToRect(mDRect, mVRect, Matrix.ScaleToFit.CENTER);

                mBitmapRect = getBitmapRect(mBitmap);//获取图片等信息
                //如果图片尺寸有一边为0，后续绘制则无意义
                if (mBitmapRect.right - mBitmapRect.left == 0 ||
                        mBitmapRect.bottom - mBitmapRect.top == 0){
                    return;
                }
                initCropWindow(mBitmapRect);

                mInit = false;
            }
            canvas.concat(mMatrix);
            canvas.drawColor(Color.BLACK);
            canvas.drawBitmap(mBitmap, 0, 0, null);
//                canvas.drawBitmap(mBitmap, mLeft,mTop , null);
            canvas.save();//保存上一次状态和比例

            // TODO: 2019-05-17 这里还需要优化
            Matrix matrix = new Matrix();//恢复正常比例并重新作画
            canvas.setMatrix(matrix);
            onStartDraw(canvas);//开始正式绘制裁剪框等

        } finally {
            if (canvas != null) {
                mHolder.unlockCanvasAndPost(canvas);
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {//属于主线线程的事件
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                onActionDown(event.getX(), event.getY());
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                onActionUp();
                return true;

            case MotionEvent.ACTION_MOVE:
                if (mWideRatio > NO_VALUE && mHighRatio > NO_VALUE) {
                    onActionMove2(event.getX(), event.getY());
                } else {
                    onActionMove(event.getX(), event.getY());
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                return true;

            default:
                return false;
        }

    }


    /**
     * 当手机系统为 8.0以上时，有些图片为 AdaptiveIconDrawable（自适应图片），
     * 所以需要区分转换Bitmap
     */
    private Bitmap getBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && drawable instanceof AdaptiveIconDrawable) {
            AdaptiveIconDrawable adaptiveIconDr = (AdaptiveIconDrawable) drawable;
            Drawable backgroundDr = adaptiveIconDr.getBackground();//背景图
            Drawable currentDr = adaptiveIconDr.getCurrent();//当前显示图
            Drawable foregroundDr = adaptiveIconDr.getForeground();//前景图
            //大概这三张图的顺序如以下
//            Drawable[] drr = new Drawable[]{backgroundDr,currentDr,foregroundDr};
            Drawable[] drr = new Drawable[]{currentDr, foregroundDr};
            //可以将多个图层合成一个
            LayerDrawable layerDrawable = new LayerDrawable(drr);

            int width = layerDrawable.getIntrinsicWidth();
            int height = layerDrawable.getIntrinsicHeight();

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);

            layerDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            layerDrawable.draw(canvas);

            return bitmap;
        }
        if (drawable instanceof StateListDrawable) {
            //........以后做@drawable/xxx_xxx类型处理
        }
        return null;
    }

    public void refresh() {
        mDrawThread.run();
    }

    /**
     * 绘制裁剪窗等样式
     * */
    private void onStartDraw(Canvas canvas) {

        //绘制裁剪宽外的蒙版
        drawBgColor(canvas);

        //绘制九宫格引导线
        drawGuidelines(canvas);
        //绘制裁剪边框
        drawBorder(canvas);
        //绘制裁剪边框的四个角
        drawCorners(canvas);
    }


    /**
     * 获取图片ImageView周围的边界组成的RectF对象
     */
    private RectF getBitmapRect(Bitmap bitmap) {

        final float[] matrixValues = new float[9];
        mMatrix.getValues(matrixValues);

        final float scaleX = matrixValues[Matrix.MSCALE_X];
        final float scaleY = matrixValues[Matrix.MSCALE_Y];
        final float transX = matrixValues[Matrix.MTRANS_X];
        final float transY = matrixValues[Matrix.MTRANS_Y];

        final int bitmapWidth = bitmap.getWidth();
        final int bitmapHeight = bitmap.getHeight();

        float drawableDisplayWidth = Math.round(bitmapWidth * scaleX);
        float drawableDisplayHeight = Math.round(bitmapHeight * scaleY);

        final float left = Math.max(transX, 0);
        final float top = Math.max(transY, 0);
        final float right = Math.min(left + drawableDisplayWidth, getWidth());
        final float bottom = Math.min(top + drawableDisplayHeight, getHeight());

        return new RectF(left, top, right, bottom);
    }


    /**
     * 初始化裁剪框
     *
     * @param bitmapRect
     */
    private void initCropWindow(@NonNull RectF bitmapRect) {

        float left, top, right, bottom;

        if (mWideRatio > NO_VALUE && mHighRatio > NO_VALUE) {
            float width = bitmapRect.right - bitmapRect.left;
            float height = bitmapRect.bottom - bitmapRect.top;


            //裁剪框的默认宽度是该view宽度的 2/3
//            final float cropWidth = (width * 2f) / 3f;
             float cropWidth;
            //裁剪框的默认高度是根据 宽高比求出
             float cropHeight;
            //以最近小边为基准
            if (width > height) {
                Edge.MIN_CROP_LENGTH_PX = (int) (height / 3f);
                cropHeight = height;
                cropWidth = (cropHeight * mWideRatio) / mHighRatio;
                //计算出来的裁剪框宽度大于了图片显示的宽度，需重新确定基准边并计算
                if (cropWidth >= width) {
                    cropWidth = width;
                    cropHeight = (cropWidth * mHighRatio) / mWideRatio;
                }
            } else {
                Edge.MIN_CROP_LENGTH_PX = (int) (width / 3f);
                cropWidth = width;
                cropHeight = (cropWidth * mHighRatio) / mWideRatio;
                if (cropHeight > height) {
                    cropHeight = height;
                    cropWidth = (cropHeight * mWideRatio) / mHighRatio;
                }
            }


            final float[] matrixValues = new float[9];
            mMatrix.getValues(matrixValues);
            final float scaleX = matrixValues[Matrix.MSCALE_X];
            final float scaleY = matrixValues[Matrix.MSCALE_Y];
            final float transX = matrixValues[Matrix.MTRANS_X];
            final float transY = matrixValues[Matrix.MTRANS_Y];

            left = ((width - cropWidth) / 2f) + transX;
            top = ((height - cropHeight) / 2f) + transY;
            right = left + cropWidth;
            bottom = top + cropHeight;

        } else {
            //裁剪框距离图片左右的padding值
            final float horizontalPadding = 0.15f * bitmapRect.width();
            final float verticalPadding = 0.15f * bitmapRect.height();

            left = bitmapRect.left + horizontalPadding;
            top = bitmapRect.top + verticalPadding;
            right = bitmapRect.right - horizontalPadding;
            bottom = bitmapRect.bottom - verticalPadding;
        }

        //初始化裁剪框上下左右四条边
        Edge.LEFT.initCoordinate(left);
        Edge.TOP.initCoordinate(top);
        Edge.RIGHT.initCoordinate(right);
        Edge.BOTTOM.initCoordinate(bottom);

        Log.e(TAG, "initCropWindow: left: " + Edge.LEFT.getCoordinate() + "    top:" + Edge.TOP.getCoordinate() + "   left:" + Edge.RIGHT.getCoordinate() + "   bottom:" + Edge.BOTTOM.getCoordinate());
    }

    private void drawBgColor(@NonNull Canvas canvas) {
        final int left = (int) Edge.LEFT.getCoordinate();
        final int top = (int) Edge.TOP.getCoordinate();
        final int right = (int) Edge.RIGHT.getCoordinate();
        final int bottom = (int) Edge.BOTTOM.getCoordinate();

        Rect rect1 = new Rect(left, 0, this.getWidth(), top);
        canvas.drawRect(rect1, mBgColorPaint);
        Rect rect2 = new Rect(right, top, this.getWidth(), this.getHeight());
        canvas.drawRect(rect2, mBgColorPaint);
        Rect rect3 = new Rect(0, bottom, right, this.getHeight());
        canvas.drawRect(rect3, mBgColorPaint);
        Rect rect4 = new Rect(0, 0, left, bottom);
        canvas.drawRect(rect4, mBgColorPaint);

    }

    private void drawGuidelines(@NonNull Canvas canvas) {

        final float left = Edge.LEFT.getCoordinate();
        final float top = Edge.TOP.getCoordinate();
        final float right = Edge.RIGHT.getCoordinate();
        final float bottom = Edge.BOTTOM.getCoordinate();

        final float oneThirdCropWidth = Edge.getWidth() / 3;

        final float x1 = left + oneThirdCropWidth;
        //引导线竖直方向第一条线
        canvas.drawLine(x1, top, x1, bottom, mGuidelinePaint);
        final float x2 = right - oneThirdCropWidth;
        //引导线竖直方向第二条线
        canvas.drawLine(x2, top, x2, bottom, mGuidelinePaint);

        final float oneThirdCropHeight = Edge.getHeight() / 3;

        final float y1 = top + oneThirdCropHeight;
        //引导线水平方向第一条线
        canvas.drawLine(left, y1, right, y1, mGuidelinePaint);
        final float y2 = bottom - oneThirdCropHeight;
        //引导线水平方向第二条线
        canvas.drawLine(left, y2, right, y2, mGuidelinePaint);
    }

    private void drawBorder(@NonNull Canvas canvas) {

        canvas.drawRect(Edge.LEFT.getCoordinate(),
                Edge.TOP.getCoordinate(),
                Edge.RIGHT.getCoordinate(),
                Edge.BOTTOM.getCoordinate(),
                mBorderPaint);
    }

    private void drawCorners(@NonNull Canvas canvas) {

        final float left = Edge.LEFT.getCoordinate();
        final float top = Edge.TOP.getCoordinate();
        final float right = Edge.RIGHT.getCoordinate();
        final float bottom = Edge.BOTTOM.getCoordinate();

        //简单的数学计算

        final float lateralOffset = (mCornerThickness - mBorderThickness) / 2f;
        final float startOffset = mCornerThickness - (mBorderThickness / 2f);

        //左上角左面的短线
        canvas.drawLine(left - lateralOffset, top - startOffset, left - lateralOffset, top + mCornerLength, mCornerPaint);
        //左上角上面的短线
        canvas.drawLine(left - startOffset, top - lateralOffset, left + mCornerLength, top - lateralOffset, mCornerPaint);

        //右上角右面的短线
        canvas.drawLine(right + lateralOffset, top - startOffset, right + lateralOffset, top + mCornerLength, mCornerPaint);
        //右上角上面的短线
        canvas.drawLine(right + startOffset, top - lateralOffset, right - mCornerLength, top - lateralOffset, mCornerPaint);

        //左下角左面的短线
        canvas.drawLine(left - lateralOffset, bottom + startOffset, left - lateralOffset, bottom - mCornerLength, mCornerPaint);
        //左下角底部的短线
        canvas.drawLine(left - startOffset, bottom + lateralOffset, left + mCornerLength, bottom + lateralOffset, mCornerPaint);

        //右下角左面的短线
        canvas.drawLine(right + lateralOffset, bottom + startOffset, right + lateralOffset, bottom - mCornerLength, mCornerPaint);
        //右下角底部的短线
        canvas.drawLine(right + startOffset, bottom + lateralOffset, right - mCornerLength, bottom + lateralOffset, mCornerPaint);
    }


    /**
     * 处理手指按下事件
     *
     * @param x 手指按下时水平方向的坐标
     * @param y 手指按下时竖直方向的坐标
     */
    private void onActionDown(float x, float y) {

        //获取边框的上下左右四个坐标点的坐标
        final float left = Edge.LEFT.getCoordinate();
        final float top = Edge.TOP.getCoordinate();
        final float right = Edge.RIGHT.getCoordinate();
        final float bottom = Edge.BOTTOM.getCoordinate();


        //获取手指所在位置位于图二种的A，B，C，D位置种哪一种
        mPressedCropWindowEdgeSelector = CatchEdgeUtil.getPressedHandle(x, y, left, top, right, bottom, mScaleRadius);

        if (mPressedCropWindowEdgeSelector != null) {
            //计算手指按下的位置与裁剪框的偏移量
            CatchEdgeUtil.getOffset(mPressedCropWindowEdgeSelector, x, y, left, top, right, bottom, mTouchOffset);
            Log.e(TAG, "onActionMove: x :" + x + "    y: " + y + "  left:" + left + "  top: " + top + "  right: " + right + "  bottom: " + bottom + "    点击范围x: " + mTouchOffset.x + "   点击范围y: " + mTouchOffset.y + "  范围大小：" + mScaleRadius);
            refresh();
        }
    }


    /**
     * 按宽高比例调整裁剪框(如：16:10,4:3等)
     */
    private void onActionMove2(float x, float y) {
        if (mPressedCropWindowEdgeSelector == null) {
            return;
        }

        x += mTouchOffset.x;
        y += mTouchOffset.y;

        //表示手指触摸点移动到该view外面了
        if (x < 0f || y < 0f || x > mBitmapRect.right || y > mBitmapRect.bottom) {
            return;
        }

//        Log.e(TAG, "onActionMove2: mmmmmmmmmmmm   " + x + "       " + y);

        /*
         * difference 表示提前检测裁剪框宽度值，是否大于最小限定值
         * difference2 表示提前检测裁剪框高度值，是否大于最小限定值
         * */
        float difference = -1f, difference2 = -1f;
        switch (mPressedCropWindowEdgeSelector) {

            case TOP_LEFT://左上角 对 右下角
                float right_x1 = Edge.RIGHT.getCoordinate();//获取不动点（右下角 x）
                difference = right_x1 - x;
                y = Edge.BOTTOM.getCoordinate() - (mHighRatio * difference) / mWideRatio;
                difference2 = Edge.BOTTOM.getCoordinate() - y;
                break;
            case TOP_RIGHT://右上角 对 左下角
                float left_x1 = Edge.LEFT.getCoordinate();//获取不动点（左下角 x）
                difference = x - left_x1;
                y = Edge.BOTTOM.getCoordinate() - (mHighRatio * difference) / mWideRatio;
                difference2 = Edge.BOTTOM.getCoordinate() - y;
                break;
            case BOTTOM_LEFT://左下角 对 右上角
                float right_x2 = Edge.RIGHT.getCoordinate();//获取不动点（右上角 x）
                difference = right_x2 - x;
                y = Edge.TOP.getCoordinate() + (mHighRatio * difference) / mWideRatio;
                difference2 = y - Edge.TOP.getCoordinate();
                break;
            case BOTTOM_RIGHT://右下角 对 左上角
                float left_x2 = Edge.LEFT.getCoordinate();//获取不动点（左上角 x）
                difference = x - left_x2;
                y = Edge.TOP.getCoordinate() + (mHighRatio * difference) / mWideRatio;
                difference2 = y - Edge.TOP.getCoordinate();
                break;
            case LEFT://左边
            case TOP://上边
            case RIGHT://右边
            case BOTTOM://下边
                //不执行
                return;
            case CENTER:
                difference = Edge.MIN_CROP_LENGTH_PX + 1;
                difference2 = Edge.MIN_CROP_LENGTH_PX + 1;
                break;
        }

        if (difference <= Edge.MIN_CROP_LENGTH_PX || difference2 <= Edge.MIN_CROP_LENGTH_PX) {
            return;
        }

        mPressedCropWindowEdgeSelector.updateCropWindow(x, y, mBitmapRect);
        refresh();
    }


    private void onActionUp() {
        if (mPressedCropWindowEdgeSelector != null) {
            mPressedCropWindowEdgeSelector = null;
            refresh();
        }
    }


    private void onActionMove(float x, float y) {
        if (mPressedCropWindowEdgeSelector == null) {
            return;
        }


        x += mTouchOffset.x;
        y += mTouchOffset.y;


        mPressedCropWindowEdgeSelector.updateCropWindow(x, y, mBitmapRect);
        refresh();
    }


    /**
     * 设置图片
     */
    public void setBitmap(Bitmap bitmap) {
        if (null == bitmap){
            return;
        }
        mBitmap = Bitmap.createBitmap(bitmap);
        mInit = true;
        refresh();
    }

    public void setRatio(int wideRatio,int heighRatio){
        mWideRatio = wideRatio;
        mHighRatio = heighRatio;
        mInit = true;
        refresh();
    }

    /**
     * 获取裁剪好的BitMap
     */
    public Bitmap getCroppedImage() {

        Bitmap bitmap = mBitmap;
        if (null == bitmap || bitmap.getWidth() <= 1f || bitmap.getHeight() <= 1f) {
            return null;
        }
        final float[] matrixValues = new float[9];
        mMatrix.getValues(matrixValues);

        final float scaleX = matrixValues[Matrix.MSCALE_X];
        final float scaleY = matrixValues[Matrix.MSCALE_Y];
        final float transX = matrixValues[Matrix.MTRANS_X];
        final float transY = matrixValues[Matrix.MTRANS_Y];

        Log.e(TAG, "getCroppedImage:  bitmapWidth:" + bitmap.getWidth() + "   bitmapHeight:" + bitmap.getHeight());
        //计算出需要裁剪图片的真实尺寸和检验尺寸是否在图片尺寸范围内
        float trueLeft = (Edge.LEFT.getCoordinate() - transX) / scaleX;
        if (trueLeft <= 0f) {
            trueLeft = 0f;
        }
        float trueTop = (Edge.TOP.getCoordinate() - transY) / scaleY;
        if (trueTop <= 0f) {
            trueTop = 0f;
        }
        float trueWidth = Edge.getWidth() / scaleX;
        if (trueWidth > bitmap.getWidth()) {
            trueWidth = bitmap.getWidth();
        }
        float trueHeight = Edge.getHeight() / scaleY;
        if (trueHeight > bitmap.getHeight()) {
            trueHeight = bitmap.getHeight();
        }

//            Log.e(TAG, "getCroppedImage:  trueLeft:" + trueLeft + "   trueTop:" + trueTop + "  trueWidth:" + trueWidth + "  trueHeight:" + trueHeight);
//            Log.e(TAG, "getCroppedImage:  trueLeft:" + (int) trueLeft + "   trueTop:" + (int) trueTop + "  trueWidth:" + (int) trueWidth + "  trueHeight:" + (int) trueHeight);


        Bitmap originalBitmap = Bitmap.createBitmap(bitmap
                , (int) trueLeft
                , (int) trueTop
                , (int) trueWidth
                , (int) trueHeight);
//
//
//
//            Log.e(TAG, "getCroppedImage:    "+originalBitmap.getWidth() +"    "+originalBitmap.getHeight()+"    "+Edge.getWidth()+"   "+Edge.getHeight() );

        return originalBitmap;

    }


}
