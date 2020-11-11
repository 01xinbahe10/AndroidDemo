package com.example.aac.test.surfaceview.view;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by hxb on  2020/11/10
 * 继承GlSurfaceView绘制图形
 */
public class DrawingView extends GLSurfaceView implements GLSurfaceView.Renderer {
    private static final String TAG = DrawingView.class.getName();


    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;
    private int mProgram;

    static final int COORDS_PER_VERTEX = 3;
    static short index[] = {0, 1, 2, 0, 2, 3};
    static float triangleCoords[] = {
            -0.5f, 0.5f, 0.0f, // top left
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f, // bottom right
            0.5f, 0.5f, 0.0f  // top right
    };

    private int mPositionHandle;
    private int mColorHandle;

    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    //顶点个数
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    //顶点之间的偏移量
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 每个顶点四个字节

    private int mMatrixHandler;

    //设置颜色，依次为红绿蓝和透明通道
    float color[] = {1.0f, 0f, 0f, 1.0f};

    public DrawingView(Context context) {
        this(context, null);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void init(Context context) {
        setEGLContextClientVersion(2);////GLContext设置OpenGLES2.0
        setRenderer(this);
        /*渲染方式，RENDERMODE_WHEN_DIRTY表示被动渲染，只有在调用requestRender或者onResume等方法时才会进行渲染。RENDERMODE_CONTINUOUSLY表示持续渲染*/
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        ByteBuffer bb = ByteBuffer.allocateDirect(
                triangleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(triangleCoords);
        vertexBuffer.position(0);

        ByteBuffer cc = ByteBuffer.allocateDirect(index.length * 2);
        cc.order(ByteOrder.nativeOrder());
        indexBuffer = cc.asShortBuffer();
        indexBuffer.put(index);
        indexBuffer.position(0);

        String vertexShaderCode = readGLSL(getContext(), "VertexShaderCode.glsl");
        String fragmentShaderCode = readGLSL(getContext(), "FragmentShaderCode.glsl");
        SparseArray<String> sparseArray = new SparseArray<>();
        sparseArray.put(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        sparseArray.put(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        mProgram = createAndLinkProgram(sparseArray);
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
        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(mProgram);
        //获取变换矩阵vMatrix成员句柄
        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0);
        //获取顶点着色器的vPosition成员句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        //获取片元着色器的vColor成员的句柄
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        //设置绘制三角形的颜色
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        //绘制三角形
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount);
        //索引法绘制正方形
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, index.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }


    /**
     * 从资源文件中读取 .glsl文件 并转为String
     */
    public String readGLSL(Context context, String fileName) {
        InputStream is = null;
        String result = null;
        try {
            is = context.getAssets().open(fileName);
            int lenght = is.available();
            byte[] buffer = new byte[lenght];
            is.read(buffer);
            result = new String(buffer, "utf8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 编译glsl
     */
    private int compileShader(int shaderType, String shaderSource) {
        //创建一个空shader
        int shaderHandle = GLES20.glCreateShader(shaderType);
        if (shaderHandle != 0) {
            //加载shader源码
            GLES20.glShaderSource(shaderHandle, shaderSource);
            //编译shader
            GLES20.glCompileShader(shaderHandle);
            final int[] compileStatus = new int[1];
            //检查shader状态
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
            if (compileStatus[0] == 0) {
                //输入shader异常日志
                Log.e(TAG, "Error compile shader:${GLES20.glGetShaderInfoLog(shaderHandle)}");
                //删除shader
                GLES20.glDeleteShader(shaderHandle);
                shaderHandle = 0;
            }
        }
        if (shaderHandle == 0) {
            Log.e(TAG, "Error create shader");
        }
        return shaderHandle;
    }

    /**
     * 将Shader链接到program
     */
    private int createAndLinkProgram(SparseArray<String> shaderTypeCode) {
        //创建一个空的program
        int programHandle = GLES20.glCreateProgram();
        if (programHandle != 0) {
//            //编译shader
//            final int vertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER, vertexCode);
//            final int fragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentCode);
//            //绑定shader和program
//            GLES20.glAttachShader(programHandle, vertexShaderHandle);
//            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            int size = shaderTypeCode.size();
            for (int i = 0; i < size; i++) {
                int key = shaderTypeCode.keyAt(i);
                String value = shaderTypeCode.valueAt(i);
                //编译shader
                final int shaderHandle = compileShader(key, value);
                //绑定shader和program
                GLES20.glAttachShader(programHandle, shaderHandle);
            }

            //链接program
            GLES20.glLinkProgram(programHandle);

            final int[] linkStatus = new int[1];
            //检测program状态
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] == 0) {
                Log.e(TAG, "Error link program:${GLES20.glGetProgramInfoLog(programHandle)}");
                //删除program
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }
        if (programHandle == 0) {
            Log.e(TAG, "Error create program");
        }
        return programHandle;
    }
}
