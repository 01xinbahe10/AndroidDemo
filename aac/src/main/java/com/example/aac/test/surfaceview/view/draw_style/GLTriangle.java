package com.example.aac.test.surfaceview.view.draw_style;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import android.util.SparseArray;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by hxb on  2020/11/10
 * 三角形
 */
public class GLTriangle extends GLStyle{
    private final String TAG = GLTriangle.class.getName();

    private Context context;

    // 创建空的OpenGL ES Program id
    private int mProgram;
    //顶点字节数组
    private ByteBuffer pointByteBuffer;
    //顶点坐标数组**/
    private FloatBuffer pointBuffer = null;

    //设置三角形顶点数组
    private final float triangleCoords[] = {   //默认按逆时针方向绘制
            0.0f,  1.0f, 0.0f, // 顶点
            -1.0f, -0.0f, 0.0f, // 左下角
            1.0f, -0.0f, 0.0f  // 右下角
    };
    //颜色r g b a
    private final float[] color = {1f,0f,1f,255f};



    private GLTriangle(Context context){
        this.context = context;
    }


    public static GLTriangle init(Context context){
        return new GLTriangle(context);
    }



    public void onCreated(GL10 gl) {
        //按初始化顶点 本地字节缓存（注意：这里只能是系统级的内存分配，不然GL会报错）
        pointByteBuffer = ByteBuffer.allocateDirect(triangleCoords.length * 4);    //顶点数 * sizeof(float)
        pointByteBuffer.order(ByteOrder.nativeOrder());
        //转为Float类型缓冲
        pointBuffer = pointByteBuffer.asFloatBuffer();
        // 把坐标都添加到FloatBuffer中
        pointBuffer.put(triangleCoords);
        //设置buffer从第一个坐标开始读
        pointBuffer.position(0);

        ///////////////////////////////////////////////////

        // 编译shader代码
        String vertexShaderCode = readGLSL(context, "VertexShaderCode2.glsl");
        String fragmentShaderCode = readGLSL(context, "FragmentShaderCode.glsl");
        SparseArray<String> sparseArray = new SparseArray<>();
        sparseArray.put(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        sparseArray.put(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        //链接到GLES2.0上
        mProgram = createAndLinkProgram(sparseArray);
    }


    public void onChanged(int width, int height) {

    }


    public void onDraw() {
        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(mProgram);
        //获取变换矩阵vMatrix成员句柄
//        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        //获取顶点着色器的vPosition成员句柄
        int positionHandler = GLES20.glGetAttribLocation(mProgram, "vPosition");
        //获取片元着色器的vColor成员的句柄
        int colorHandler = GLES20.glGetUniformLocation(mProgram, "vColor");


        ///////////////////////////////////////////////////////////////////////////
        if (null != pointBuffer) {
            //启用顶点属性数组
            GLES20.glEnableVertexAttribArray(positionHandler);
            //准备单个顶点坐标数据(一个顶点(x,y,z)三个坐标点，这三个坐标点是float（4个字节）类型，所以字节数为3*4)
            GLES20.glVertexAttribPointer(positionHandler, 3, GLES20.GL_FLOAT, false, 3 * Float.BYTES, pointBuffer);

            /*
            * 按点数绘制图形
            * GLES20.glDrawArrays(param1, param1, param3);
            * param1:表示你绘制的基本图元是什么（基本图元 ：点，线，三角形）
            * param2:从顶点数据读取数据的起点位置(以点作为单位，而非向量)
            * param3:绘制的顶点数(以点作为单位，而非向量)
            * */
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
            //设置绘制图元颜色
            GLES20.glUniform4fv(colorHandler, 1, color, 0);
        }

        ////////////////////////////////////////////////////////////


        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(positionHandler);
    }



}
