uniform mat4 u_Matrix;
uniform float u_Time;

attribute vec3 a_Position;
attribute vec3 a_Color;
attribute vec3 a_DirectionVector;
attribute float a_ParticleStartTime;

varying vec3 v_Color;
varying float v_ElapsedTime;

void main(){
    v_Color = a_Color;
    v_ElapsedTime = u_Time - a_ParticleStartTime;// 计算粒子从被创建后运行了多少时间
    // 当前位置 = 根据起点 + （方向 * 运行时间）
    vec3 currentPosition = a_Position + (a_DirectionVector * v_ElapsedTime);
    gl_Position = u_Matrix * vec4(currentPosition, 1.0);
    gl_PointSize = 10.0;
}