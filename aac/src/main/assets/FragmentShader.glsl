//version 120
precision mediump float;//精度 为float
uniform vec4 vColor;
varying vec2 v_texPo;//纹理位置  接收于vertex_shader
uniform sampler2D sTexture;//纹理
uniform int fragColorType;//输出颜色管道的颜色类别
void main() {
    if(fragColorType == 0){
        gl_FragColor = vColor;
    }else{
        gl_FragColor = texture2D(sTexture, v_texPo);
    }
}
