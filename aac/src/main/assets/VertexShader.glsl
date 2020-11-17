//version 120
attribute vec4 vPosition;
attribute vec2 tPosition;//纹理位置
varying vec2 v_texPo;//纹理位置  与fragment_shader交互
void main() {
    v_texPo = tPosition;
    gl_Position = vPosition;
}
