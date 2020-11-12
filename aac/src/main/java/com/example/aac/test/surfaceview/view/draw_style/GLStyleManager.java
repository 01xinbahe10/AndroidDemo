package com.example.aac.test.surfaceview.view.draw_style;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;
import android.util.SparseArray;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by hxb on  2020/11/12
 * 图形管理类
 *
 * 针对GLES2.0
 */
public final class GLStyleManager {
    private static final String TAG = GLStyleManager.class.getName();
    private static volatile GLStyleManager manager = null;
    // 创建空的OpenGL ES Program id
    private int program = 0;
    //存储GLSL shader字符串代码，以供编译时使用
    private SparseArray<CompileShader> shaderTypeCode;
    //记录最后一次shaderTypeCode的大小
    private int lastSize = 0;

    public static GLStyleManager init() {
        if (null == manager) {
            manager = new GLStyleManager();
        }
        return manager;
    }

    private GLStyleManager() {
    }


    /**
     * 从资源文件中读取 .glsl文件 并转为String
     */
    private String _readGLSL(Context context, String fileName) {
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
    private int _compileShader(int shaderType, String shaderSource) {
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
                Log.d(TAG, "Error compile shader:${GLES20.glGetShaderInfoLog(shaderHandle)}");
                //删除shader
                GLES20.glDeleteShader(shaderHandle);
                shaderHandle = 0;
            }
        }
        if (shaderHandle == 0) {
            Log.d(TAG, "Error create shader");
        }
        return shaderHandle;
    }

    /**
     * 将Shader链接到program
     */
    private int _createAndLinkProgram(SparseArray<CompileShader> shaderTypeCode) {
        //创建一个空的program
        int programHandle = GLES20.glCreateProgram();
        if (programHandle != 0) {
            int size = shaderTypeCode.size();
            for (int i = 0; i < size; i++) {
                int key = shaderTypeCode.keyAt(i);
                CompileShader value = shaderTypeCode.valueAt(i);
                //编译shader
                final int shaderHandle = _compileShader(value.shaderType, value.shaderCode);
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

    private SparseArray<CompileShader> _getArray(){
        if (null == shaderTypeCode){
            shaderTypeCode = new SparseArray<>();
        }
        return shaderTypeCode;
    }


    /**
     * 从assets中读取.glsl文件，并处理成字符串
     */
    public static String readGLSLFile(Context context, String path) {
        return null == manager ? null : manager._readGLSL(context, path);
    }


    /**
     * 存入Shader 的类型及代码，以供编译时使用
     */
    public static void putShader(int key, int shaderType, String shaderCode) {
        if (null == manager) {
            return;
        }

        CompileShader shader = new CompileShader();
        shader.shaderType = shaderType;
        shader.shaderCode = shaderCode;
        manager._getArray().put(key, shader);
    }


    /**
     * 该方法在创建OpenGL ES Program id不为0 和 shaderTypeCode容量没有发什么变化时，
     * 只执行一次。
     * 原因：
     * 绑定shader和program (GLES20.glAttachShader(param1,param2))和链接program
     * (GLES20.glLinkProgram(param3)) 这两句代码频繁使用会极大的消耗cpu性能。
     * <p>
     * 所以：
     * 在GLStyle的子类在链接GLES2.0时，请在最后调用，提前调用如shaderTypeCode容量没有发生变化，
     * 后续再调用是没有用的
     */
    @FunctionalInterface
    public interface Fun1 {
        void createAndLinkProgram();
    }

    public static void createAndLinkProgram() {
        if (null == manager) {
            return;
        }
        int size = manager._getArray().size();
        if (manager.program == 0 || size != manager.lastSize) {
            Log.e(TAG, "createAndLinkProgram: 链接Program "+size );
            manager.program = manager._createAndLinkProgram(manager.shaderTypeCode);
            manager.lastSize = size;
        }
    }

    /**
     * 提供program id 的接口
     */
    public static int program() {
        return null == manager ? 0 : manager.program;
    }


    /**
     * 销毁
     */
    public static void onDestroy() {
        if (null == manager) {
            return;
        }
        manager.program = 0;
        manager.shaderTypeCode.clear();
        manager.lastSize = 0;
        manager = null;
    }

    public final static class CompileShader {
        public int shaderType;
        public String shaderCode;
    }
}
