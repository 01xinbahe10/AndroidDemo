package com.example.aac.test.surfaceview.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.example.aac.test.surfaceview.view.draw_style.GLLine;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by hxb on  2020/11/10
 */
public class DrawingView2 extends GLSurfaceView implements GLSurfaceView.Renderer {

    private final String TAG = DrawingView2.class.getName();

    private float x;
    private float y;

    private GLLine currentLines = null;  //当前绘制的线
    private List<GLLine> linesList = new ArrayList<>(); //当前绘制线的表

    public long frameCount = 0;  //共绘制了多少帧
    private float ratio;
    private int width;
    private int height;


    public DrawingView2(Context context) {
        this(context,null);
    }

    public DrawingView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
//        setEGLContextClientVersion(2);//GLContext设置OpenGLES2.0;（要使用2.0 见DrawingView）
        setRenderer(this);
        /*渲染方式，RENDERMODE_WHEN_DIRTY表示被动渲染，只有在调用requestRender或者onResume等方法时才会进行渲染。RENDERMODE_CONTINUOUSLY表示持续渲染*/
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        this.width = getWidth();
        this.height = getHeight();
        ratio = (float) width / height;
        // 设置OpenGL场景的大小,(0,0)表示窗口内部视口的左下角，(w,h)指定了视口的大小
        gl.glViewport(0, 0, width, height);
        // 设置投影矩阵
        gl.glMatrixMode(GL10.GL_PROJECTION);
        // 重置投影矩阵
        gl.glLoadIdentity();
        // 设置视口的大小
        gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
        //以下两句声明，以后所有的变换都是针对模型(即我们绘制的图形)
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 设置透明色为清屏
        gl.glClearColor(0, 0, 0, 0);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 清除屏幕和深度缓存
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);   //不加这个可以产生残影(模拟器可以)
        // 重置当前的模型观察矩阵
        gl.glLoadIdentity();

        // 允许设置顶点
        //GL10.GL_VERTEX_ARRAY顶点数组
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        // 允许设置颜色
        //GL10.GL_COLOR_ARRAY颜色数组
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        //反走样
//        gl.glEnable(GL10.GL_BLEND);
        //线条抗锯齿
//        gl.glEnable(GL10.GL_LINE_SMOOTH);

        //绘制模型
        drawModel(gl);


        // 取消颜色设置
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        // 取消顶点设置
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

        //绘制结束
        gl.glFinish();

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.x = event.getX();
        this.y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentLines = new GLLine();
                synchronized (linesList) {
                    linesList.add(currentLines);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, String.format("x: %f, y: %f", x, y));
                float realtiveX = x / height * 4f;  //4个象限
                float realtiveY = -y / height * 4f;
                currentLines.drawLine(realtiveX, realtiveY);

                requestRender();
                return true;
            case MotionEvent.ACTION_UP:
                break;
        }

        return super.onTouchEvent(event);
    }

    /**帧绘制**/
    public void drawModel(GL10 gl) {
        gl.glTranslatef(-2f * ratio, 2f, -2f); //必须有,z轴可以用于做缩放，按16比9来做，只要右下角象限
        Log.e(TAG, "drawModel: >>>>>>>>>>>>>>>>>>>  "+linesList.size() );
        synchronized (linesList) {
            for(GLLine line : linesList) {
                line.drawTo(gl);
            }
        }
        frameCount++;
    }

    public void clearAll() {
        synchronized (linesList) {
            linesList.clear();
            requestRender();
        }
    }


}
