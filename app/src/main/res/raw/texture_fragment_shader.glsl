precision mediump float;

uniform sampler2D u_TextureUnit;    // 二维纹理数据数组
varying vec2 v_TextureCoordinates;  // texture position

void main(){
    gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);
}