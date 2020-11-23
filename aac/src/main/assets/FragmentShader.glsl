//version 120
precision mediump float;//精度 为float （片原着色器需要这一句）
uniform int u_fragmentStyle;
uniform vec4 u_color;
uniform sampler2D sTexture;//纹理
varying vec2 v_texturePosition;//纹理2D坐标 接收于vertex_shader
void main() {

    if (u_fragmentStyle == 0){ //表示点
        float d = distance(gl_PointCoord, vec2(0.5, 0.5));
        if (d < 0.5){//倒圆角
            gl_FragColor = u_color;
        } else {
            discard;//丢弃
        }
        return;
    }

    if (u_fragmentStyle == 1//表示Line
    || u_fragmentStyle == 3//三角形
    || u_fragmentStyle == 4//四边形
    ){
        gl_FragColor = u_color;
        return;
    }

    if (u_fragmentStyle == 2){ //表示纹理
        gl_FragColor = texture2D(sTexture, v_texturePosition);
    }
}
