precision mediump float;
varying vec3 v_Color;
varying float v_ElapsedTime;
void main(){
    // 颜色除以运行时间，年轻的粒子更亮
    // 防止分母为0，总是加上一个很小的数1.0
    gl_FragColor = vec4(v_Color / v_ElapsedTime, 1.0);
}