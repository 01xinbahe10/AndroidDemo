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
 * Created by hxb on  2020/11/18
 * 点
 */
public class GLPoint extends GLStyle{
    private static final String TAG = GLPoint.class.getName();
    private Context context;
    //顶点坐标数组**/
    private ByteBuffer pointByteBuffer = null;
    private FloatBuffer pointBuffer = null;
    //默认数组长度
    private int defArrayLength = 1024;

    private AtomicInteger arrayPosition = new AtomicInteger();//记录数组的脚标

    //颜色r g b a
    private final float[] color = {1f, 0f, 1f, 255f};


    public static GLPoint init(Context context) {
        return new GLPoint(context);
    }

    private GLPoint(Context context) {
        this.context = context;
    }


    @Override
    public void onCreate() {
        pointByteBuffer = ByteBuffer.allocateDirect(defArrayLength * Float.BYTES);
        pointByteBuffer.order(ByteOrder.nativeOrder());
        pointBuffer = pointByteBuffer.asFloatBuffer();
        pointBuffer.position(0);
    }


    public void addLinePath(float[] lineCoords) {
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

    @Override
    public void onDraw() {

        int program = GLESManager.program();
        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(program);

        Log.e(TAG, "addLinePath: 当前脚标2222222222  " + arrayPosition.get() + "  "+GLESManager.pointSizeHandler());
        if (null != pointBuffer) {
            pointBuffer.position(0);
            GLES20.glUniform1i(GLESManager.vertexStyleHandler(),Shader.KeyWorld.stylePoint);
            GLES20.glUniform1i(GLESManager.fragmentStyleHandler(),Shader.KeyWorld.stylePoint);

            //激活属性数组
            GLES20.glEnableVertexAttribArray(GLESManager.vertexPositionHandler());

            //准备单个顶点坐标数据(一个顶点(x,y,z)三个坐标点，这三个坐标点是float（4个字节）类型，所以字节数为3*4)
            GLES20.glVertexAttribPointer(GLESManager.vertexPositionHandler(), 3, GLES20.GL_FLOAT, false, 3 * Float.BYTES, pointBuffer);
            //设置点大小
            GLES20.glUniform1f(GLESManager.pointSizeHandler(), 20f);
//            GLES20.gl
            //设置绘制图元颜色
            GLES20.glUniform4fv(GLESManager.vertexColorHandler(), 1, color, 0);
//            GLES20.glUniform1i(GLESManager.fragColorTypeHandler(), Shader.KeyWorld.outColor);

            GLES20.glDrawArrays(GLES20.GL_POINTS,0,arrayPosition.get()/3);
        }

        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(GLESManager.vertexPositionHandler());
    }
    private float[] mProjectMatrix = new float[16];
    public void onDrawTest(){

        if (null != pointBuffer) {
            Log.e(TAG, "onDrawTest: >>>>>>>>>>>>>>>>  " );

            pointBuffer.clear();
            float[] tableVerticesWithTriangles = {
                    0f, 0f,
                    0f, 0.25f
            };
            pointBuffer.put(tableVerticesWithTriangles);
            pointBuffer.position(0);


            GLES20.glUniform1i(GLESManager.vertexStyleHandler(),Shader.KeyWorld.stylePoint);
            GLES20.glUniform1i(GLESManager.fragmentStyleHandler(),Shader.KeyWorld.stylePoint);

            //激活属性数组
            GLES20.glEnableVertexAttribArray(GLESManager.vertexPositionHandler());
//            GLES20.glEnableVertexAttribArray(GLESManager.pointSizeHandler());
            //准备单个顶点坐标数据(一个顶点(x,y,z)三个坐标点，这三个坐标点是float（4个字节）类型，所以字节数为3*4)
            GLES20.glVertexAttribPointer(GLESManager.vertexPositionHandler(), 2, GLES20.GL_FLOAT, false, 2*Float.BYTES, pointBuffer);
            //设置点大小
            GLES20.glUniform1f(GLESManager.pointSizeHandler(), 20f);
            //设置绘制图元颜色
            GLES20.glUniform4fv(GLESManager.vertexColorHandler(), 1, color, 0);


            GLES20.glDrawArrays(GLES20.GL_POINTS,0,2);
        }

        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(GLESManager.vertexPositionHandler());
//        GLES20.glDisableVertexAttribArray(GLESManager.pointSizeHandler());

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
}
