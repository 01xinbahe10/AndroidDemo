package com.example.aac.test.surfaceview.view.graphics;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.example.aac.test.surfaceview.view.manager.GLESManager;
import com.example.aac.test.surfaceview.view.manager.Shader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by hxb on  2020/11/12
 */
public class GLLine2 extends GLStyle {
    private Context context;
    //顶点坐标数组**/
    private ByteBuffer pointByteBuffer = null;
    private FloatBuffer pointBuffer = null;
    //默认数组长度
    private int defArrayLength = 1024;

    private AtomicInteger arrayPosition = new AtomicInteger();//记录数组的脚标

    //颜色r g b a
    private final float[] color = {1f, 0f, 1f, 255f};

    public static GLLine2 init(Context context) {
        return new GLLine2(context);
    }

    private GLLine2(Context context) {
        this.context = context;
    }

    public synchronized void onCreate() {
        pointByteBuffer = ByteBuffer.allocateDirect(defArrayLength * Float.BYTES);
        pointByteBuffer.order(ByteOrder.nativeOrder());
        pointBuffer = pointByteBuffer.asFloatBuffer();
        pointBuffer.position(0);
    }

    public synchronized void addLinePath(float[] lineCoords) {
        if (arrayPosition.get() >= defArrayLength - lineCoords.length) {
            defArrayLength += 1024;
            ByteBuffer lineByteBuffer = ByteBuffer.allocateDirect(defArrayLength * Float.BYTES);
            lineByteBuffer.order(ByteOrder.nativeOrder());
            byte[] arrays = pointByteBuffer.array();
            System.arraycopy(arrays, 0, lineByteBuffer.array(), 0,arrays.length);
            pointByteBuffer = lineByteBuffer;
            pointBuffer = pointByteBuffer.asFloatBuffer();
        }
        for (int i = 0; i < lineCoords.length; i++) {
            pointBuffer.put(arrayPosition.get(), lineCoords[i]);
            arrayPosition.incrementAndGet();
        }
//        Log.e("TAG", "addLinePath: 当前脚标  " + arrayPosition.get() + "  ");
    }


    public synchronized void onDraw() {
        int program = GLESManager.program();
        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(program);
        //获取变换矩阵vMatrix成员句柄
//        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        //获取顶点着色器的vPosition成员句柄
        int positionHandler = GLES20.glGetAttribLocation(program, Shader.KeyWorld.vPosition);
        //获取片元着色器的vColor成员的句柄
        int colorHandler = GLES20.glGetUniformLocation(program, Shader.KeyWorld.vColor);

        Log.e("TAG", "addLinePath: 当前脚标2222222222  " + arrayPosition.get() + "  ");
        if (null != pointBuffer) {
            pointBuffer.position(0);
            //启用顶点属性数组
            GLES20.glEnableVertexAttribArray(positionHandler);
            //准备单个顶点坐标数据(一个顶点(x,y,z)三个坐标点，这三个坐标点是float（4个字节）类型，所以字节数为3*4)
            GLES20.glVertexAttribPointer(positionHandler, 3, GLES20.GL_FLOAT, false, 3 * Float.BYTES, pointBuffer);
            GLES20.glLineWidth(10f);
            GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, arrayPosition.get()/3);
            //设置绘制图元颜色
            GLES20.glUniform4fv(colorHandler, 1, color, 0);
        }

        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(positionHandler);
    }

    @Override
    public void onClear() {
        if (null != pointBuffer){
            pointBuffer.clear();
        }
        if (null != pointByteBuffer){
            pointByteBuffer.clear();
        }
        pointBuffer = null;
        pointByteBuffer = null;
        arrayPosition.set(0);
    }


    public void onDrawTest() {
        int program = GLESManager.program();
        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(program);
        //获取变换矩阵vMatrix成员句柄
//        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        //获取顶点着色器的vPosition成员句柄
        int positionHandler = GLES20.glGetAttribLocation(program, Shader.KeyWorld.vPosition);
        //获取片元着色器的vColor成员的句柄
        int colorHandler = GLES20.glGetUniformLocation(program, Shader.KeyWorld.vColor);

        final float[] lineCoodrs = {
                0.25f, 0.25f, 0f,//v0
                0.25f, -0.25f, 0f,//v1
                -0.25f, -0.25f, 0f,//v2
                -0.25f, 0.25f, 0f,//v3
        };

        FloatBuffer pointBuffer = null;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(lineCoodrs.length * Float.BYTES);
        byteBuffer.order(ByteOrder.nativeOrder());
        pointBuffer = byteBuffer.asFloatBuffer();
        pointBuffer.put(lineCoodrs);
        pointBuffer.position(0);

        Log.e("TAG", "onDrawTest: >>>>>>>>>>>>>>>>>111111111");

        //启用顶点属性数组
        GLES20.glEnableVertexAttribArray(positionHandler);
        //准备单个顶点坐标数据(一个顶点(x,y,z)三个坐标点，这三个坐标点是float（4个字节）类型，所以字节数为3*4)
        GLES20.glVertexAttribPointer(positionHandler, 3, GLES20.GL_FLOAT, false, 12, pointBuffer);
        GLES20.glLineWidth(10f);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, lineCoodrs.length/3);
        //设置绘制图元颜色
        GLES20.glUniform4fv(colorHandler, 1, color, 0);


        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(positionHandler);
    }
}
