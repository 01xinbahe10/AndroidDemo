package com.example.aac.test.surfaceview.view.draw_style;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

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
    //设置线条顶点数组
    private final float lineCoords[] = {
            0.0f, 0.1f, 0.0f, // 第一点
            0.0f, 0.0f, 0.0f, // 第二点
            0.1f, 0.0f, 0.0f
    };

    private AtomicInteger arrayPosition = new AtomicInteger();//记录数组的脚标

    //颜色r g b a
    private final float[] color = {1f, 0f, 1f, 255f};

    public static GLLine2 init(Context context) {
        return new GLLine2(context);
    }

    private GLLine2(Context context) {
        this.context = context;
    }

    public GLStyleManager.Fun1 onCreate() {
      /*  ByteBuffer lineByteBuffer = ByteBuffer.allocateDirect(lineCoords.length * Float.BYTES);
        lineByteBuffer.order(ByteOrder.nativeOrder());
        pointBuffer = lineByteBuffer.asFloatBuffer();
        pointBuffer.put(lineCoords);
        pointBuffer.position(0);*/


        pointByteBuffer = ByteBuffer.allocateDirect(defArrayLength * Float.BYTES);
        pointByteBuffer.order(ByteOrder.nativeOrder());
        pointBuffer = pointByteBuffer.asFloatBuffer();
        pointBuffer.position(0);


        String vertexShaderCode = GLStyleManager.readGLSLFile(context, Shader.VertexShaderCode2.shaderCode);
        String fragmentShaderCode = GLStyleManager.readGLSLFile(context, Shader.FragmentShaderCode.shaderCode);
        GLStyleManager.putShader(Shader.VertexShaderCode2.key, Shader.VertexShaderCode2.shaderType, vertexShaderCode);
        GLStyleManager.putShader(Shader.FragmentShaderCode.key, Shader.FragmentShaderCode.shaderType, fragmentShaderCode);
        return GLStyleManager::createAndLinkProgram;
    }

    public void addLinePath(float[] lineCoords) {
        if (arrayPosition.get() + lineCoords.length >= defArrayLength - 1) {
            defArrayLength += 1024;
            ByteBuffer lineByteBuffer = ByteBuffer.allocateDirect(defArrayLength * Float.BYTES);
            System.arraycopy(pointByteBuffer.array(), 0, lineByteBuffer.array(), 0, (arrayPosition.get() + 1));
            pointByteBuffer = lineByteBuffer;
            pointBuffer = pointByteBuffer.asFloatBuffer();
            pointBuffer.position(0);
        }
        for (int i = 0; i < lineCoords.length; i++) {
            pointBuffer.put(arrayPosition.get(), lineCoords[i]);
            arrayPosition.incrementAndGet();
        }
        Log.e("TAG", "addLinePath: 当前脚标  " + arrayPosition.get() + "  ");
    }


    public void onDraw() {
        int program = GLStyleManager.program();
        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(program);
        //获取变换矩阵vMatrix成员句柄
//        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        //获取顶点着色器的vPosition成员句柄
        int positionHandler = GLES20.glGetAttribLocation(program, Shader.KeyWorld.vPosition);
        //获取片元着色器的vColor成员的句柄
        int colorHandler = GLES20.glGetUniformLocation(program, Shader.KeyWorld.vColor);

        if (null != pointBuffer) {
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


    public void onDrawTest() {
        int program = GLStyleManager.program();
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
