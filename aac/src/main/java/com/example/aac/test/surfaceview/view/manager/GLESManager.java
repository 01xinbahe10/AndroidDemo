package com.example.aac.test.surfaceview.view.manager;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.IntDef;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by hxb on  2020/11/12
 * 图形管理类
 */
public final class GLESManager {
    private static final String TAG = GLESManager.class.getName();
    private static volatile GLESManager manager = null;
    // 创建空的OpenGL ES Program id
    private int program = 0;
    //存储GLSL shader字符串代码，以供编译时使用
    private SparseArray<CompileShader> shaderTypeCode;

    ///////////////Shader 参数句柄位置//////////////////
    private int vPosition;//顶点坐标参数变量的句柄
    private int tPosition;//纹理参数变量的句柄
    private int vColor;//顶点渲染颜色变量的句柄
    private int fragColorType;//输出颜色管道的颜色类别的变量句柄


    private int width, height;
    private float width1_2, height1_2;//表示二分之一的宽高
    private float[] scaledCoords = {0.0f, 0.0f, 0.0f};//比例坐标x y z 容器（正中）

    public static GLESManager init() {
        if (null == manager) {
            manager = new GLESManager();
        }
        return manager;
    }

    private GLESManager() {
    }


    /**
     * 从资源文件中读取文件 并转为String
     */
    private String _readString(Context context, String fileName) {
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
     * 针对GLES2.0
     */
    private int _compileShader20(int shaderType, String shaderSource) {
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
     * 针对GLES2.0
     */
    private int _createAndLinkProgram20(SparseArray<CompileShader> shaderTypeCode) {
        //创建一个空的program
        int programHandle = GLES20.glCreateProgram();
        if (programHandle != 0) {
            int size = shaderTypeCode.size();
            for (int i = 0; i < size; i++) {
                int key = shaderTypeCode.keyAt(i);
                CompileShader value = shaderTypeCode.valueAt(i);
                //编译shader
                final int shaderHandle = _compileShader20(value.shaderType, value.shaderCode);
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

    private SparseArray<CompileShader> _getArray() {
        if (null == shaderTypeCode) {
            shaderTypeCode = new SparseArray<>();
        }
        return shaderTypeCode;
    }


    /**
     * 从assets中读取.glsl文件，并处理成字符串
     * 针对GLES2.0
     */
    public static String readGLSLFile20(Context context, String path) {
        return null == manager ? null : manager._readString(context, path);
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

    public static void createAndLinkProgram20(Context context) {
        if (null == manager) {
            return;
        }

        //注意：这里初始化Shader，需要一次性在onSurfaceCreated初始化，不得其它函数中初始化会报错
        String vertexShaderCode = GLESManager.readGLSLFile20(context, Shader.VertexShader.shaderCode);
        String fragmentShaderCode = GLESManager.readGLSLFile20(context, Shader.FragmentShader.shaderCode);

        CompileShader vShader = new CompileShader();
        vShader.shaderType = Shader.VertexShader.shaderType;
        vShader.shaderCode = vertexShaderCode;
        CompileShader fShader = new CompileShader();
        fShader.shaderType = Shader.FragmentShader.shaderType;
        fShader.shaderCode = fragmentShaderCode;

        manager._getArray().put(Shader.VertexShader.key, vShader);
        manager._getArray().put(Shader.FragmentShader.key, fShader);
        if (manager.program == 0) {
            manager.program = manager._createAndLinkProgram20(manager.shaderTypeCode);
            Log.d(TAG, "createAndLinkProgram: 链接Program :" + manager.program);
        }
    }

    /**
     * 获取shader中各个参数的句柄
     */
    public static void getShaderParamHandler() {
        if (null == manager) {
            return;
        }
        if (manager.program > 0) {
            //获取顶点着色器的vPosition成员句柄
            manager.vPosition = GLES20.glGetAttribLocation(manager.program, Shader.KeyWorld.vPosition);
            //获取纹理着色器的tPosition成员句柄
            manager.tPosition = GLES20.glGetAttribLocation(manager.program, Shader.KeyWorld.tPosition);
            //获取片元着色器的vColor成员句柄
            manager.vColor = GLES20.glGetUniformLocation(manager.program, Shader.KeyWorld.vColor);
            //获取片元着色器的fragColorType成员句柄
            manager.fragColorType = GLES20.glGetUniformLocation(manager.program, Shader.KeyWorld.fragColorType);

            Log.d(TAG, "getShaderParamHandler: 获取Shader各个参数成员变量的句柄  vPosition:" + manager.vPosition + "  tPosition:" + manager.tPosition
                    + "  vColor:" + manager.vColor + "  fragColorType:" + manager.fragColorType);
        }
    }

    /**
     * 提供program id 的接口
     * 针对GLES2.0
     */
    public static int program() {
        return null == manager ? 0 : manager.program;
    }

    /**
     * 提供外部Shader各个param句柄
     */
    public static int vertexPositionHandler() {
        return null == manager ? -1 : manager.vPosition;
    }

    public static int texturePositionHandler() {

        return null == manager ? -1 : manager.tPosition;
    }

    public static int vertexColorHandler() {
        return null == manager ? -1 : manager.vColor;
    }

    public static int fragColorTypeHandler() {
        return null == manager ? -1 : manager.fragColorType;
    }


    /**
     * 清除Shader容器
     * 针对GLES2.0
     */
    public static void clearShader20() {
        if (null == manager) {
            return;
        }
        manager.program = 0;
        manager.shaderTypeCode.clear();
    }

    public final static class CompileShader {
        public int shaderType;
        public String shaderCode;
    }


    /**
     * 保存宽高
     */
    public static void saveWidthAndHeight(int width, int height) {
        if (null == manager) {
            return;
        }
        manager.width = width;
        manager.height = height;
        manager.width1_2 = manager.width / 2f;
        manager.height1_2 = manager.height / 2f;
    }

    /**
     * 将触摸的坐标转换为比例坐标X Y Z
     */
    public static float[] convertToScaledCoords(float x, float y, float z) {
        if (null == manager) {
            return null;
        }
        /*
         * x/width :表示用百分比表示点在屏幕x轴上的位置
         * 2f :由于GLES20的坐标采用比例，正中是{0,0,0}到屏幕四角的位置分别是1，所以边长为2
         * x>manager.width1_2?1f:-1f :表示取正负，以正中为基点左边为负右边为正(X)、上边为正下边为负(Y)、
         * 前边为正后边为负(Y)
         * */

        boolean maxX = x > manager.width1_2;
        boolean maxY = y > manager.height1_2;
        boolean minX = x < manager.width1_2;
        boolean minY = y < manager.height1_2;
        float percentX = (x / manager.width) * 2f;
        float percentY = (y / manager.height) * 2f;
        float scaleX, scaleY, scaleZ = 0;
        if (maxX && minY) {//第一象限
            scaleX = percentX - 1f;
            scaleY = 1f - percentY;
        } else if (minX && minY) {//第二象限
            scaleX = -(1f - percentX);
            scaleY = 1f - percentY;
        } else if (minX && maxY) {//第三象限
            scaleX = -(1f - percentX);
            scaleY = -(percentY - 1f);
        } else if (maxX && maxY) {//第四象限
            scaleX = percentX - 1f;
            scaleY = -(percentY - 1f);
        } else {
            scaleX = 0;
            scaleY = 0;
            scaleZ = 0;
        }

        manager.scaledCoords[0] = scaleX;
        manager.scaledCoords[1] = scaleY;
        manager.scaledCoords[2] = scaleZ;//目前只是2D图形，如以后需要3D再计算
        return manager.scaledCoords;
    }

    /**
     * 图形样式设置
     */
    public static final int NO_STYLE = -1;
    public static final int POINT = 0;//点
    public static final int LINE = 10;//线
    public static final int TRIANGLE = 20;//三角形
    public static final int QUADRILATERAL = 30;//四边形
    public static final int TEXTURE = 40;//纹理

    @IntDef({NO_STYLE, POINT, LINE, TRIANGLE, QUADRILATERAL, TEXTURE})
    @Retention(RetentionPolicy.CLASS)
    public @interface GraphicStyle {
    }

    private int graphicStyle = NO_STYLE;

    public static void setGraphicStyle(@GraphicStyle int style) {
        if (null == manager) {
            return;
        }
        manager.graphicStyle = style;
    }

    public static int getGraphicStyle() {
        if (null == manager) {
            return NO_STYLE;
        }
        return manager.graphicStyle;
    }
}
