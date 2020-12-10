//version 120
uniform int u_vertexStyle;//样式
attribute vec4 a_position;//顶点坐标
attribute vec2 a_texturePosition;//纹理2D坐标
uniform float u_pointSize;//点的大小
varying vec2 v_texturePosition;//纹理2D坐标 与fragment_shader交互（shader 内部使用）

void main() {
    if (u_vertexStyle == 0){ //表示点
        gl_Position = a_position;
        gl_PointSize = u_pointSize;
        return;
    }

    if (u_vertexStyle == 1//表是Line
    || u_vertexStyle == 3//三角形
    || u_vertexStyle == 4//四边形
    ){
        gl_Position = a_position;
        return;
    }

    if (u_vertexStyle == 2){ //纹理
        gl_Position = a_position;
        v_texturePosition = a_texturePosition;
    }
}
