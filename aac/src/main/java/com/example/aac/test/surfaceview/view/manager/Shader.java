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
        String vColor = "vColor";
        String vMatrix = "vMatrix";
    }

    interface VertexShaderCode2{
        int key = 1;
        int shaderType = GLES20.GL_VERTEX_SHADER;
        String shaderCode = "VertexShaderCode2.glsl";//资源文件
    }

    interface FragmentShaderCode{
        int  key = 2;
        int shaderType = GLES20.GL_FRAGMENT_SHADER;
        String shaderCode = "FragmentShaderCode.glsl";//资源文件
    }


}
