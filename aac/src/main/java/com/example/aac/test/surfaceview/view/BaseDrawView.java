package com.example.aac.test.surfaceview.view;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by hxb on  2020/11/11
 */
public class BaseDrawView extends GLSurfaceView {
    private final String TAG = BaseDrawView.class.getName();

    public BaseDrawView(Context context) {
        super(context);
    }

    public BaseDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 从资源文件中读取 .glsl文件 并转为String
     */
    protected String readGLSL(Context context, String fileName) {
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
    protected int compileShader(int shaderType, String shaderSource) {
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
    protected int createAndLinkProgram(SparseArray<String> shaderTypeCode) {
        //创建一个空的program
        int programHandle = GLES20.glCreateProgram();
        if (programHandle != 0) {
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
