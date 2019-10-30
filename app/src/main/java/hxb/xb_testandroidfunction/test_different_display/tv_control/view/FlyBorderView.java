package hxb.xb_testandroidfunction.test_different_display.tv_control.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

public class FlyBorderView extends View {
    private int borderWidth;//焦点移动飞框的边框
    private int duration = 200;//动画持续时间
    private int statusBarHeight = 0;
    private ValueAnimator valueAnimator;

    public FlyBorderView(Context context) {
        this(context, null);

    }

    public FlyBorderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlyBorderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
//            statusBarHeight = getStatusBarHeight();
            View rootView = this.getRootView();
            int[] location = new int[2];
            rootView.getLocationOnScreen(location);
            statusBarHeight = location[1];
            Log.e("TAG", "onWindowVisibilityChanged: ------------------------->>>>> " + statusBarHeight);
        }
    }

    /**
     * @param newFocus 下一个选中项视图
     * @param scale    选中项视图的伸缩大小
     */
    public void attachToView(View newFocus, float scale) {
        //当前选中项与下一个选中项的宽度偏移量
        final int widthInc = (int) ((newFocus.getWidth() * scale + 2 * borderWidth - getWidth()));
        //当前选中项与下一个选中项的高度偏移量
        final int heightInc = (int) ((newFocus.getHeight() * scale + 2 * borderWidth - getHeight()));
        int[] location = new int[2];
        //获取在当前窗口内的绝对坐标
        newFocus.getLocationInWindow(location);
        //获取在整个屏幕内的绝对坐标
//        newFocus.getLocationOnScreen(location);
        //飞框到达下一个选中项的X轴偏移量
        float translateX = location[0] - borderWidth
                - (newFocus.getWidth() * scale - newFocus.getWidth()) / 2f;
        //飞框到达下一个选中项的Y轴偏移量
        float translateY = location[1] - borderWidth
                - ((newFocus.getHeight() * scale - newFocus.getHeight()) / 2f) - statusBarHeight;
        //调用飞框 自适应和移动 动画效果
        startTotalAnim(widthInc, heightInc, translateX, translateY);
    }

    /**
     * 飞框 自适应和移动 动画效果
     *
     * @param widthInc   宽度偏移量
     * @param heightInc  高度偏移量
     * @param translateX X轴偏移量
     * @param translateY Y轴偏移量
     */
    private void startTotalAnim(final int widthInc, final int heightInc, float translateX, float translateY) {
        if (null != valueAnimator && valueAnimator.isRunning()){
            valueAnimator.cancel();//取消
        }

        //当前飞框的宽度
        final int width = getWidth();
        //当前飞框的高度
        final int height = getHeight();

        //数值变化动画器，能获取平均变化的值
        valueAnimator = ValueAnimator.ofFloat(0, 1).setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //设置当前飞框的宽度和高度的自适应变化
                setFlyBorderLayoutParams((int) (width + widthInc * Float.parseFloat(valueAnimator.getAnimatedValue().toString())),
                        (int) (height + heightInc * Float.parseFloat(valueAnimator.getAnimatedValue().toString())));
            }
        });


        //X轴移动的属性动画
        ObjectAnimator translationX = ObjectAnimator.ofFloat(this, "translationX", translateX);
        //y轴移动的属性动画
        ObjectAnimator translationY = ObjectAnimator.ofFloat(this, "translationY", translateY);

        //动画集合
        AnimatorSet set = new AnimatorSet();
        //动画一起实现
        set.play(valueAnimator).with(translationX).with(translationY);
        set.setDuration(duration);
        //设置动画插值器
        set.setInterpolator(new LinearInterpolator());
        //开始动画
        set.start();
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    private void setFlyBorderLayoutParams(int width, int height) {//设置焦点移动飞框的宽度和高度
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = width;
        params.height = height;
        setLayoutParams(params);
    }

    //获取顶部（Status Bar） 高度 (无论显示不显示都有高度值)
    public int getStatusBarHeight() {

        Resources resources = getResources();

        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");

        int height = resources.getDimensionPixelSize(resourceId);

//        Log.v("status bar>>>", "height:" + height);

        return height;
    }

    //获取底部 (Navigation Bar) 高度 (无论显示不显示都有高度值)
    public int getNavigationBarHeight() {

        Resources resources = getResources();

        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");

        int height = resources.getDimensionPixelSize(resourceId);

//        Log.v("navigation bar>>>", "height:" + height);

        return height;

    }

    private int getStatusBarHeight2() {
        int[] windowParams = new int[2];
        int[] screenParams = new int[2];
        this.getLocationInWindow(windowParams);
        this.getLocationOnScreen(screenParams);
        // 如果状态栏隐藏，返回0，如果状态栏显示则返回高度
        return screenParams[1] - windowParams[1];
    }

}
