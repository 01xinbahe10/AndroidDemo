package com.example.aac.test.surfaceview.view.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import androidx.annotation.Size;

import com.example.aac.test.surfaceview.view.manager.GLESManager;
import com.example.aac.test.surfaceview.view.manager.Shader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by hxb on  2020/11/16
 * 纹理
 */
public class GLTexture extends GLStyle{
    private final String TAG = GLTexture.class.getName();

    private Context context;

    //顶点位置数据的缓冲
    private FloatBuffer vertexBuffer;
    //纹理位置数据的缓冲
    private FloatBuffer textureBuffer;
    //纹理id
    private int textureId;

    //顶点坐标
    static float[] vertexData = {   // in counterclockwise order:
            -1f, -1f, 0.0f, // bottom left
            1f, -1f, 0.0f, // bottom right
            -1f, 1f, 0.0f, // top left
            1f, 1f, 0.0f,  // top right
    };

    //纹理坐标  对应顶点坐标  与之映射
    static float[] textureData = {   // in counterclockwise order:
            0f, 1f, 0.0f, // bottom left
            1f, 1f, 0.0f, // bottom right
            0f, 0f, 0.0f, // top left
            1f, 0f, 0.0f,  // top right
    };

    //每一次取点的时候取几个点
    static final int COORDS_PER_VERTEX = 3;
    //每一次取的总的点 大小
    private final int vertexStride = COORDS_PER_VERTEX * Float.BYTES; // 4 bytes per vertex
    private final int vertexCount = vertexData.length / COORDS_PER_VERTEX;

    public static GLTexture init(Context context){
        return new GLTexture(context);
    }

    private GLTexture(Context context){
        this.context = context;
    }

    @Override
    public void onCreate() {
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * Float.BYTES)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        textureBuffer = ByteBuffer.allocateDirect(textureData.length * Float.BYTES)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureData);
        textureBuffer.position(0);

        int[] textureIds = new int[1];
        //创建纹理
        GLES20.glGenTextures(1, textureIds, 0);
        if (textureIds[0] == 0) {
            return;
        }
        textureId = textureIds[0];
        //绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        //环绕（超出纹理坐标范围）  （s==x t==y GL_REPEAT 重复）
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        //过滤（纹理像素映射到坐标点）  （缩小、放大：GL_LINEAR线性）
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
    }


    public GLTexture setVertexPosition(@Size(12) float[] vertex){
        for (int i = 0; i <vertexData.length ; i++) {
            vertexData[i] = vertex[i];
        }
        return this;
    }

    public GLTexture setTexturePosition(@Size(12) float[] texture){
        for (int i = 0; i <textureData.length ; i++) {
            textureData[i] = texture[i];
        }
        return this;
    }

    //设置纹理图片
    public GLTexture setTextureBitmap(Bitmap bitmap){
        if (bitmap != null) {
            //设置纹理为2d图片
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        }
        return this;
    }

    @Override
    public void onDraw() {
        int program = GLESManager.program();
        GLES20.glUseProgram(program);
        //激活数据句柄
        GLES20.glEnableVertexAttribArray(GLESManager.vertexPositionHandler());
        GLES20.glEnableVertexAttribArray(GLESManager.texturePositionHandler());

        //设置顶点位置值
        GLES20.glVertexAttribPointer(GLESManager.vertexPositionHandler(), COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        //设置纹理位置值
        GLES20.glVertexAttribPointer(GLESManager.texturePositionHandler(), COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, textureBuffer);
        //选择着色片源的输出颜色的类型
        GLES20.glUniform1i(GLESManager.fragColorTypeHandler(),Shader.KeyWorld.outTexture);
        //绘制 GLES20.GL_TRIANGLE_STRIP:复用坐标
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount);

        //禁止数组的句柄
        GLES20.glDisableVertexAttribArray(GLESManager.vertexPositionHandler());
        GLES20.glDisableVertexAttribArray(GLESManager.texturePositionHandler());
    }

    @Override
    public void onClear() {

    }
}
