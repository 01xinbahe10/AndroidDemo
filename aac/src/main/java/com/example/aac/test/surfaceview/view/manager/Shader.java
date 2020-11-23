package com.example.aac.test.surfaceview.view.manager;

import android.opengl.GLES20;

/**
 * Created by hxb on  2020/11/12
 * 包装Shader语言它所对应的编译类型是什么及标识符key等
 *
 * Vertex:表示包装名。注意：取名时要通过名字就知道是干什么的
 * key:表示标识符且唯一
 * shaderType:表示创建什么类型的着色器，以编译代码
 * shaderCode:代码；可以是字符串类型的代码和后缀为.glsl的文件这两种
 * KeyWorld:表示shaderCode中那些关键字需要提供给外部使用
 *
 * 说明：
 * 如以后需要增减Shader须在该接口中自行修改，统一维护
 */
public interface Shader {
    interface KeyWorld{
        String vPosition = "vPosition";
        String tPosition = "tPosition";
        String pointSize = "pointSize";
        String vColor = "vColor";
        String vMatrix = "vMatrix";
        String fragColorType = "fragColorType";
        int outColor = 0;
        int outTexture = 1;


        String u_vertexStyle = "u_vertexStyle";
        String u_fragmentStyle = "u_fragmentStyle";
        String a_position = "a_position";
        String a_texturePosition = "a_texturePosition";
        String u_pointSize = "u_pointSize";
        String u_color = "u_color";

        int stylePoint = 0;//点
        int styleLine = 1;//线
        int styleTexture = 2;//纹理
        int styleTriangle = 3;//三角形
        int styleQuadrilateral = 4;//四边形
    }

    interface VertexShader{
        int key = 1;
        int shaderType = GLES20.GL_VERTEX_SHADER;
        String shaderCode = "VertexShader.glsl";//资源文件
    }

    interface FragmentShader{
        int  key = 2;
        int shaderType = GLES20.GL_FRAGMENT_SHADER;
        String shaderCode = "FragmentShader.glsl";//资源文件
    }

    interface VertexShader1{
        int key = 3;
        int shaderType = GLES20.GL_VERTEX_SHADER;
        String shaderCode = "VertexShader1.glsl";
    }

    interface FragmentShader1{
        int key = 4;
        int shaderType = GLES20.GL_FRAGMENT_SHADER;
        String shaderCode = "FragmentShader1.glsl";
    }
}
