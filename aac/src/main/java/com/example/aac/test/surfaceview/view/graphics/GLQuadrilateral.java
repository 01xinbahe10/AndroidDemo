package com.example.aac.test.surfaceview.view.graphics;

import android.content.Context;
import android.opengl.GLES20;

import com.example.aac.test.surfaceview.view.manager.GLESManager;
import com.example.aac.test.surfaceview.view.manager.Shader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

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
            -1f,1f,0f,//v0(x,y,z)
            1f,1f,0f,//v1
            1f,-1f,0,//v2
            -1f,-1f,0//v3
    };
    //根据四边形推算出的索引坐标
    private final short[] indexCoords = {0,1,3,1,2,3};
    //颜色r g b a
    private final float[] color = {255f,255f,255f,255f};

    private GLQuadrilateral(Context context){
        this.context = context;
    }


    public static GLQuadrilateral init(Context context){
        return new GLQuadrilateral(context);
    }

    @Override
    public void onCreate() {
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
    }

    @Override
    public void onDraw() {
        mProgram = GLESManager.program();
//        Log.e(TAG, "onCreated: 四边形》》Program: "+mProgram );
        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(mProgram);

        if (null != pointBuffer) {
            //启用顶点属性数组
            GLES20.glEnableVertexAttribArray(GLESManager.vertexPositionHandler());
            //准备单个顶点坐标数据(一个顶点(x,y,z)三个坐标点，这三个坐标点是float（4个字节）类型，所以字节数为3*4)
            GLES20.glVertexAttribPointer(GLESManager.vertexPositionHandler(), 3, GLES20.GL_FLOAT, false, 3 * Float.BYTES, pointBuffer);
            //设置绘制图元颜色
            GLES20.glUniform4fv(GLESManager.vertexColorHandler(), 1, color, 0);
            GLES20.glUniform1i(GLESManager.fragColorTypeHandler(),Shader.KeyWorld.outColor);

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

        }

        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(GLESManager.vertexPositionHandler());
    }

    @Override
    public void onClear() {

    }
}
