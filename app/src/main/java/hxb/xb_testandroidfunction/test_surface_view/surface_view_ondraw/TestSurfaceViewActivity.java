package hxb.xb_testandroidfunction.test_surface_view.surface_view_ondraw;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

import hxb.xb_testandroidfunction.R;

/**
 * Created by hxb on 2018/6/27.
 *
 */

public class TestSurfaceViewActivity extends FragmentActivity implements View.OnClickListener {

    /**
     * Called when the activity is first created.
     */
    Button btnSimpleDraw, btnTimerDraw;
    SurfaceView sfv;
    SurfaceHolder sfh;

    private Timer mTimer;
    private MyTimerTask mTimerTask;
    int Y_axis[],//保存正弦波的Y轴上的点
            centerY,//中心线
            oldX, oldY,//上一个XY点
            currentX;//当前绘制到的X轴上的点

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_surface_view);
        btnSimpleDraw = findViewById(R.id.Button01);
        btnTimerDraw = findViewById(R.id.Button02);
        btnSimpleDraw.setOnClickListener(this);
        btnTimerDraw.setOnClickListener(this);
        sfv = findViewById(R.id.SurfaceView01);
        sfh = sfv.getHolder();
        sfh.setFormat(PixelFormat.TRANSLUCENT);

        //动态绘制正弦波的定时器
        mTimer = new Timer();
        mTimerTask = new MyTimerTask();

        // 初始化y轴数据
        centerY = (getWindowManager().getDefaultDisplay().getHeight() - sfv
                .getTop()) / 2;
        Y_axis = new int[getWindowManager().getDefaultDisplay().getWidth()];
        for (int i = 1; i < Y_axis.length; i++) {// 计算正弦波
            Y_axis[i - 1] = centerY
                    - (int) (100 * Math.sin(i * 2 * Math.PI / 180));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //由于当前界面关闭了，mTimerTask会继续执行，所以mTimerTask需要将任务从队列中移除
        if (null != mTimerTask) {
            mTimerTask.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnSimpleDraw) {
            SimpleDraw(Y_axis.length - 1);//直接绘制正弦波

        } else if (v == btnTimerDraw) {
            oldY = centerY;
            //每次放定时任务前，确保之前任务已从定时器队列中移除
            if (null != mTimerTask) {
                mTimerTask.cancel();//将原任务从队列中移除
            }
            //每次放任务都要新建一个对象，否则出现一下错误：
            // java.lang.IllegalStateException: TimerTask is scheduled already
            mTimerTask = new MyTimerTask();

            //同一个定时器任务只能被放置一次
            mTimer.schedule(mTimerTask, 0, 5);//动态绘制正弦波

        }
    }


    class MyTimerTask extends TimerTask {
        @Override
        public void run() {

            SimpleDraw(currentX);
            currentX++;//往前进
            Log.e("TAG", "run:       " + currentX);
            if (currentX == Y_axis.length - 1) {//如果到了终点，则清屏重来
                ClearDraw();
                currentX = 0;
                oldY = centerY;
            }
        }

    }

    /**
     * 绘制指定区域
     */
    void SimpleDraw(int length) {
        if (length == 0) {
            oldX = 0;
        }
        Canvas canvas = sfh.lockCanvas(new Rect(oldX, 0, oldX + length,
                getWindowManager().getDefaultDisplay().getHeight()));// 关键:获取画布
        Log.i("Canvas:",
                String.valueOf(oldX) + "," + String.valueOf(oldX + length));
        if (null == canvas) {
            return;
        }
        Paint mPaint = new Paint();
        mPaint.setColor(Color.GREEN);// 画笔为绿色
        mPaint.setStrokeWidth(2);// 设置画笔粗细

        int y;
        for (int i = oldX + 1; i < length; i++) {// 绘画正弦波
            y = Y_axis[i - 1];
            canvas.drawLine(oldX, oldY, i, y, mPaint);
            oldX = i;
            oldY = y;
        }
        sfh.unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
    }

    void ClearDraw() {
        Canvas canvas = sfh.lockCanvas(null);
        canvas.drawColor(Color.BLACK);// 清除画布
        sfh.unlockCanvasAndPost(canvas);

    }

}
