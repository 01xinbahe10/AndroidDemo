package com.example.aac.test.surfaceview.view.draw;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;

import com.example.aac.test.surfaceview.view.draw_style.GLLine2;
import com.example.aac.test.surfaceview.view.draw_style.GLQuadrilateral;
import com.example.aac.test.surfaceview.view.draw_style.GLStyleManager;
import com.example.aac.test.surfaceview.view.draw_style.GLTriangle;
import com.example.aac.test.surfaceview.view.utils.ArrayUtil;

import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicInteger;

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
    //线条
    private GLLine2 line2;

    private GLLine2[] line2Array = new GLLine2[1024];
    private GLLine2 line2Current = null;
    private AtomicInteger line2Position = new AtomicInteger();

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
        triangle = GLTriangle.init(getContext());
        quadrilateral = GLQuadrilateral.init(getContext());
        line2 = GLLine2.init(getContext());
        triangle.onCreated(gl);
        quadrilateral.onCreated(gl);
        line2.onCreate().createAndLinkProgram();
        Log.e(TAG, "onSurfaceCreated: >>>>>>>>>>>>>>>>>>>>> " );
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.e(TAG, "onSurfaceChanged: >>>>>>>>>>>>>>>>>>>>>>>>>  " );
        this.width = getWidth();
        this.height = getHeight();
        DrawManager.setWidthAndHeight(this.width,this.height);
        //计算宽高比
        float ratio = (float) this.width / this.height;
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
//        quadrilateral.onDraw();
//        line2.onDrawTest();

        for (int i = 0;i<line2Array.length;i++){
            GLLine2 glLine2 = line2Array[i];
            if (null == glLine2){
                break;
            }
            glLine2.onDraw();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        GLStyleManager.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.x = event.getX();
        this.y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                createGLLine2(DrawManager.convertToScaledCoords(x,y,0));
                return true;
            case MotionEvent.ACTION_MOVE:
                float[] coords = DrawManager.convertToScaledCoords(x,y,0);
                Log.e(TAG, "onTouchEvent: x:"+coords[0]+"  y:"+coords[1]+"  z:0" );
                line2Current.addLinePath(coords);
                requestRender();
                return true;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }


    private void createGLLine2(float[] coords){
        if (line2Position.get() >= line2Array.length - 1){
            line2Array = (GLLine2[]) ArrayUtil.arrayMerge(line2Array,new GLLine2[1024]);
        }
        line2Current = GLLine2.init(getContext());
        line2Current.onCreate();
        line2Array[line2Position.get()] = line2Current;
//        line2Current.addLinePath(coords);
        line2Position.incrementAndGet();
    }

}
