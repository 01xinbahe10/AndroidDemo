package hxb.xb_testandroidfunction.test_list_refresh_load.pull_away_pulltorefreshlistview.layout;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.Log;

import hxb.xb_testandroidfunction.R;
import hxb.xb_testandroidfunction.test_gif_anim.gif_utils.AnimationsContainer;
import hxb.xb_testandroidfunction.test_list_refresh_load.pull_away_pulltorefreshlistview.PullToRefreshBase;

/**
 * Created by hxb on 2018/5/29.
 * 帧动画布局
 */

public class FrameAnimLayout extends LoadingLayout{
    private AnimationsContainer.FramesSequenceAnimation animation;
    public FrameAnimLayout(Context context, PullToRefreshBase.Mode mode, PullToRefreshBase.Orientation scrollDirection, TypedArray attrs) {
        super(context, mode, scrollDirection, attrs);
        Log.e("TAG", "FrameAnimLayout: "+mode);
        switch (mode){
            case PULL_FROM_START:
                animation = AnimationsContainer.getInstance(R.array.loading_anim2, 21).createProgressDialogAnim(mHeaderImage);
                break;
            case PULL_FROM_END:
                animation = AnimationsContainer.getInstance(R.array.loading_footer, 21).createProgressDialogAnim(mHeaderImage);
                break;
        }
        animation.start();

    }

    @Override
    protected int getDefaultDrawableResId() {
        return 0;
    }

    @Override
    protected void onLoadingDrawableSet(Drawable imageDrawable) {

    }

    @Override
    protected void onPullImpl(float scaleOfLayout) {
//        Log.e("TAG", "onPullImpl: 11111111111111" );

    }

    @Override
    protected void pullToRefreshImpl() {
//        Log.e("TAG", "pullToRefreshImpl: 222222222222222" );

    }

    @Override
    protected void refreshingImpl() {
//        Log.e("TAG", "refreshingImpl: 333333333333333" );
    }

    @Override
    protected void releaseToRefreshImpl() {
//        Log.e("TAG", "releaseToRefreshImpl: 444444444444444" );
        animation.stop();

    }

    @Override
    protected void resetImpl() {
//        Log.e("TAG", "resetImpl: 55555555555555" );

    }
}
