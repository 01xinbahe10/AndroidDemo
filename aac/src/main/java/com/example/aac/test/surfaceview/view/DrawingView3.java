package com.example.aac.test.surfaceview.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.aac.test.surfaceview.view.draw_style.GLQuadrilateral;
import com.example.aac.test.surfaceview.view.draw_style.GLTriangle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by hxb on  2020/11/11
 */
public class DrawingView3 extends BaseDrawView implements GLSurfaceView.Renderer {
    private final String TAG = DrawingView3.class.getName();

    private float[] mProjectMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private float x,y;
    private int width,height;

    //三角形图形
    private GLTriangle triangle;
    //四边形
    private GLQuadrilateral quadrilateral;

    public DrawingView3(Context context) {
        this(context,null);
    }

    public DrawingView3(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        setEGLContextClientVersion(2);
        setRenderer(this);
        /*渲染方式，RENDERMODE_WHEN_DIRTY表示被动渲染，只有在调用requestRender或者onResume等方法时才会进行渲染。RENDERMODE_CONTINUOUSLY表示持续渲染*/
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        this.width = getWidth();
        this.height = getHeight();

        triangle = GLTriangle.init(getContext());
        triangle.onCreated(gl);
        quadrilateral = GLQuadrilateral.init(getContext());
        quadrilateral.onCreated(gl);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //计算宽高比
        float ratio = (float) width / height;
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

//        triangle.onDraw();
        quadrilateral.onDraw();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.x = event.getX();
        this.y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                return true;
            case MotionEvent.ACTION_MOVE:

                requestRender();
                return true;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }

}
