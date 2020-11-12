package com.example.aac.test.surfaceview.view.draw_style;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;
import android.util.SparseArray;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by hxb on  2020/11/11
 */
public class GLQuadrilateral extends GLStyle{
    private final String TAG = GLQuadrilateral.class.getName();
    private Context context;

    // 创建空的OpenGL ES Program id
    private int mProgram;
    //顶点坐标数组缓冲
    private FloatBuffer pointBuffer = null;
    //索引数组缓冲
    private ShortBuffer indexBuffer = null;
    //四边形四点坐标
    private final float[] quadrilateralCoords = {
            -0.5f,0.5f,0f,//v0(x,y,z)
            0.5f,0.5f,0f,//v1
            0.5f,-0.5f,0,//v2
            -0.5f,-0.5f,0//v3
    };
    //根据四边形推算出的索引坐标
    private final short[] indexCoords = {0,1,3,1,2,3};
    //颜色r g b a
    private final float[] color = {1f,0f,1f,255f};

    private GLQuadrilateral(Context context){
        this.context = context;
    }


    public static GLQuadrilateral init(Context context){
        return new GLQuadrilateral(context);
    }

    public GLStyleManager.Fun1 onCreated(GL10 gl) {
        //按初始化顶点 本地字节缓存（注意：这里只能是系统级的内存分配，不然GL会报错）
        ByteBuffer pointByteBuffer = ByteBuffer.allocateDirect(quadrilateralCoords.length * Float.BYTES);    //顶点数 * sizeof(float)
        pointByteBuffer.order(ByteOrder.nativeOrder());
        //转为Float类型缓冲
        pointBuffer = pointByteBuffer.asFloatBuffer();
        // 把坐标都添加到FloatBuffer中
        pointBuffer.put(quadrilateralCoords);
        //设置buffer从第一个坐标开始读
        pointBuffer.position(0);

        ByteBuffer indexByteBuffer = ByteBuffer.allocateDirect(indexCoords.length*Short.BYTES);
        indexByteBuffer.order(ByteOrder.nativeOrder());
        indexBuffer = indexByteBuffer.asShortBuffer();
        indexBuffer.put(indexCoords);
        indexBuffer.position(0);

        String vertexShaderCode = GLStyleManager.readGLSLFile(context, Shader.VertexShaderCode2.shaderCode);
        String fragmentShaderCode = GLStyleManager.readGLSLFile(context, Shader.FragmentShaderCode.shaderCode);
        GLStyleManager.putShader(Shader.VertexShaderCode2.key,Shader.VertexShaderCode2.shaderType,vertexShaderCode);
        GLStyleManager.putShader(Shader.FragmentShaderCode.key,Shader.FragmentShaderCode.shaderType,fragmentShaderCode);
        //提供编译shader函数
        return GLStyleManager::createAndLinkProgram;
    }

    public void onDraw(){
        mProgram = GLStyleManager.program();
        Log.e(TAG, "onCreated: 四边形》》Program: "+mProgram );
        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(mProgram);
        //获取变换矩阵vMatrix成员句柄
//        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        //获取顶点着色器的vPosition成员句柄
        int positionHandler = GLES20.glGetAttribLocation(mProgram, Shader.KeyWorld.vPosition);
        //获取片元着色器的vColor成员的句柄
        int colorHandler = GLES20.glGetUniformLocation(mProgram, Shader.KeyWorld.vColor);

        ///////////////////////////////////////////////////////////////////////////
        if (null != pointBuffer) {
            //启用顶点属性数组
            GLES20.glEnableVertexAttribArray(positionHandler);
            //准备单个顶点坐标数据(一个顶点(x,y,z)三个坐标点，这三个坐标点是float（4个字节）类型，所以字节数为3*4)
            GLES20.glVertexAttribPointer(positionHandler, 3, GLES20.GL_FLOAT, false, 3 * Float.BYTES, pointBuffer);
            /*
             * 按索引绘制图形
             * GLES20.glDrawElements(param1,param2,param3,param4);
             * param1:表示你绘制的基本图元是什么（基本图元 ：点，线，三角形）
             * param2:表示你绘制图形的索引长度。所谓的索引就是：
             * v == vec3 向量 (x,y,z)
             * 例如绘制一个正四边形刚好在正中，那四个点为v0,v1,v2,v3(左上为起点，顺时针)，
             * 由于四边形可有最大的两个三角形组合的，从右上到左下该方向上切割四边形，
             * 那么三角形1的三个点v0,v1,v2;三角形2的三个点v1,v2,v3;
             * 所以索引[] = {v0,v1,v3,v1,v2,v3};
             * param3:无签名类型
             * param4:Buffer,创建一个Buffer将索引数组put()进去,再传入
             * */
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCoords.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

            //设置绘制图元颜色
            GLES20.glUniform4fv(colorHandler, 1, color, 0);
        }

        ////////////////////////////////////////////////////////////


        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(positionHandler);
    }
}
