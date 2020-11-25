package com.example.aac.test.surfaceview.view.draw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.example.aac.R;
import com.example.aac.base_frame.LiveDataBus;
import com.example.aac.test.G;
import com.example.aac.test.surfaceview.view.graphics.GLLine2;
import com.example.aac.test.surfaceview.view.graphics.GLPoint;
import com.example.aac.test.surfaceview.view.graphics.GLStyle;
import com.example.aac.test.surfaceview.view.graphics.GLTexture;
import com.example.aac.test.surfaceview.view.manager.GLESManager;
import com.example.aac.test.surfaceview.view.manager.GLPaint;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicInteger;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.example.aac.test.surfaceview.view.manager.GLPaint.getPaintStyle;

/**
 * Created by hxb on  2020/11/11
 */
public class DrawingView3 extends GLSurfaceView implements GLSurfaceView.Renderer {
    private final String TAG = DrawingView3.class.getName();

    private float[] mProjectMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private float x, y;
    private int width, height;

    private final int defLength = 1024;
    private GLTexture glTexture;
    private GLStyle[] glStyleArray = null;
    private GLStyle currentGlStyle = null;
    private final AtomicInteger glStylePosition = new AtomicInteger();

    public DrawingView3(Context context) {
        this(context, null);
    }

    public DrawingView3(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8,8,8,8,16,8);
        setRenderer(this);
        /*渲染方式，RENDERMODE_WHEN_DIRTY表示被动渲染，只有在调用requestRender或者onResume等方法时才会进行渲染。RENDERMODE_CONTINUOUSLY表示持续渲染*/
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        //初始化样式管理类
        GLESManager.init();
        GLPaint.init();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.e(TAG, "onSurfaceCreated: >>>>>>>>>>>>>>>>>>>>> ");
        //Shader创建和链接
        GLESManager.createAndLinkProgram20(getContext());
        GLESManager.getShaderParamHandler();
        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(GLESManager.program());
        glTexture = GLTexture.init(getContext());
        glTexture.onCreate();
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_test);
        glTexture.setTextureBitmap(bitmap);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = getWidth();
        this.height = getHeight();
        Log.e(TAG, "onSurfaceChanged: >>>>>>>>>>>>>>>>>>>>>>>>>  width:" + width + "   height:" + height);
        GLESManager.saveWidthAndHeight(this.width, this.height);
        GLES20.glViewport(0, 0, width, height);
//        //设置支持横竖屏转换
//        final float aspectRatio = width > height ?
//                (float) width / (float) height :
//                (float) height / (float) width;
//
//        if (width > height) {
//            // Landscape
//            orthoM(mProjectMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
//        } else {
//            // Portrait or square
//            orthoM(mProjectMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
//        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_STENCIL_BUFFER_BIT);
        GLES20.glClearColor(255f, 255f, 255f, 255f);


        glTexture.onDraw();

        drawGLStyle();

        getBitmap();
    }

    @Override
    public void onPause() {
        super.onPause();
        GLESManager.clearShader20();
        Log.e(TAG, "onPause: >>>>>>>>>>>>>>>>>>>>>>> ");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.x = event.getX();
        this.y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                createGLStyle();
                return true;
            case MotionEvent.ACTION_MOVE:
                setDataGLStyle();
                return true;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }


    private void createGLStyle() {
        if (null == glStyleArray) {
            glStyleArray = new GLStyle[defLength];
        }
        if (glStylePosition.get() >= glStyleArray.length - 1) {
            glStyleArray = (GLStyle[]) GLESManager.arrayAppendLength(glStyleArray,defLength);
        }
        switch (getPaintStyle()) {
            case POINT:
                currentGlStyle = GLPoint.init(getContext());
                break;
            case LINE:
                currentGlStyle = GLLine2.init(getContext());
                break;
            case ERASER:
                GLLine2 eraser = GLLine2.init(getContext());
                eraser.setLineColor(255f, 255f, 255f, 255f);
                currentGlStyle = eraser;
                break;
            case TRIANGLE:
                break;
            case QUADRILATERAL:
                break;
            case TEXTURE:
                break;
            default:
                return;
        }

        if (null != currentGlStyle) {
            currentGlStyle.onCreate();
            glStyleArray[glStylePosition.get()] = currentGlStyle;
            glStylePosition.incrementAndGet();
        }
    }

    private void setDataGLStyle() {
        if (null == currentGlStyle) {
            return;
        }
        switch (getPaintStyle()) {
            case POINT:
                GLPoint glPoint = (GLPoint) currentGlStyle;
                glPoint.addLinePath(GLESManager.convertToScaledCoords(x, y, 0));
                break;
            case LINE:
            case ERASER:
                GLLine2 glLine2 = (GLLine2) currentGlStyle;
                float[] glLine2Coords = GLESManager.convertToScaledCoords(x, y, 0);
                Log.e(TAG, "onTouchEvent: x:" + glLine2Coords[0] + "  y:" + glLine2Coords[1] + "  z:0");
                glLine2.addLinePath(glLine2Coords);
                break;
            case TRIANGLE:
                break;
            case QUADRILATERAL:
                break;
            case TEXTURE:
                break;

            default:
                return;
        }
        requestRender();
    }

    private void drawGLStyle() {
        if (null == glStyleArray) {
            return;
        }
        for (int i = 0; i < glStyleArray.length; i++) {
            GLStyle glStyle = glStyleArray[i];
            if (null == glStyle) {
                break;
            }
            Log.e(TAG, "onDrawFrame: >>>>>>>>>>>>>>>>>>  ");
//            if (glStyle instanceof GLPoint){
//                ((GLPoint)glStyle).onDrawTest();
//                continue;
//            }
//
            glStyle.onDraw();

        }
    }


    public void clearAll() {
        if (null != glStyleArray && glStyleArray.length > 0) {
            for (int i = 0; i < (glStylePosition.get() + 1); i++) {
                GLStyle glStyle = glStyleArray[i];
                if (null != glStyle) {
                    glStyle.onClear();
                }
                glStyleArray[i] = null;
            }
            glStylePosition.set(0);
            glStyleArray = null;
        }
        requestRender();
    }


    private volatile boolean isSave = false;
    public void savePic() {
        isSave = true;
        requestRender();

    }

    //必须GLThread线程里调用
    private void getBitmap(){
        if (!isSave){
            return;
        }
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect((width * height * Integer.BYTES));
        byteBuffer.order(ByteOrder.nativeOrder());
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, byteBuffer);
        byteBuffer.position(0);
        int[] data = new int[width * height];
        byteBuffer.asIntBuffer().get(data);//这是将intbuffer中的数据赋值到pix数组中
        byteBuffer = null;


//        for (int i = 0;i< data.length;i++) {
//            //2.每个int类型的c是接收到的ABGR，但bitmap需要ARGB格式，所以需要交换B和R的位置
//            int c = data[i];
//            //交换B和R，得到ARGB
//            data[i] = c & -0xff0100 | (c & 0x00ff0000 >> 16) | (c & 0x000000ff << 16);
//        }

        //上下翻转
        for (int y =  0; y <height / 2;y++) {
            for (int x = 0; x< width;x++) {
                int temp = data[(height - y - 1)*width+x];
                data[(height - y - 1) * width + x] = data[y * width + x];
                data[y * width + x] = temp;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(data,width,height, Bitmap.Config.ARGB_8888);

        isSave = false;
        LiveDataBus.get().with(G.TestA.SUB_SURFACE_VIEW_FT).postValue(new Object[]{G.TestA.tGetBitmap,bitmap});

        Log.e(TAG, "savePic: bmp width:" + bitmap.getWidth() + "   height:" + bitmap.getHeight());
    }

}
