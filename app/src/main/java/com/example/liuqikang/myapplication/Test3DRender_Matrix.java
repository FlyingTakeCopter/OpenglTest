package com.example.liuqikang.myapplication;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.liuqikang.myapplication.util.MatrixHelper;
import com.example.liuqikang.myapplication.util.ShaderHelper;
import com.example.liuqikang.myapplication.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by liuqikang on 2018/4/20.
 */

public class Test3DRender_Matrix implements GLSurfaceView.Renderer {
    private Context mContext;
    // 每个顶点的坐标数量
    private static final int POISTION_COMPONET_COUNT = 2;
    // 每个颜色值的数量
    private static final int COLOR_COMPONET_COUNT = 3;
    // 坐标值字节数
    private static final int BYTES_PRE_FLOAT = 4;
    // 单项总字节数
    private static final int STRIDE =
            (POISTION_COMPONET_COUNT + COLOR_COMPONET_COUNT) * BYTES_PRE_FLOAT;
    // 位置+颜色
    private final FloatBuffer vertexData;

    // opengl对象
    private int program;
    // uniform
    private final static String A_COLOR = "a_Color";
    private int aColorLocation;
    // 顶点
    private final static String A_POSITION = "a_Position";
    private int aPositionLocation;
    // 矩阵
    private final static String U_MATRIX = "u_Matrix";
    private int uMatrixLocation;
    // 4*4单位矩阵空间
    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    Test3DRender_Matrix(Context cxt){
        this.mContext = cxt;
        float[] tableVerticesWithTriangles = {
                //triangle
                // X    Y     R     G     B
                0.0f,  0.0f,  1f,   1f,   1f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f,  -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f,  0.8f,  0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f,  0.7f, 0.7f, 0.7f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                //line
                -0.5f, 0f,    1f,   0f,   0f,
                0.5f,  0f,    1f,   0f,   0f,
                //point
                0f,    -0.4f, 0f,   0f,   1f,
                0f,    0.4f,  1f,   0f,   0f
        };

        // 碎片化内存管理技术?
        vertexData = ByteBuffer
                .allocateDirect(tableVerticesWithTriangles.length * BYTES_PRE_FLOAT)    // 分配一块本地内存
                .order(ByteOrder.nativeOrder()) // 设置字节缓冲区为本地字节序
                .asFloatBuffer();   // ByteBuffer转化为FloatBuffer,不直接操作单独字节，转化为浮点数
        vertexData.put(tableVerticesWithTriangles);// put(float)需要先将ByteBuffer转化为FloatBuffer
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f,0.0f,0.0f);
        String vertexShaderSource = TextResourceReader.readTextFileFromResource(mContext, R.raw.simple_vertex_sharder_5);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(mContext, R.raw.simple_fragment_shader_4);

        int vertextShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        program = ShaderHelper.linkProgram(vertextShader, fragmentShader);
        ShaderHelper.validateProgram(program);

        GLES20.glUseProgram(program);

        // 查询片段着色器的uniform
        aColorLocation = GLES20.glGetAttribLocation(program, A_COLOR);
        // 查询顶点着色器
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        // 查询矩阵uniform
        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);

        // 保证从头读取
        vertexData.position(0);
        // 关联属性与顶点数据数组
        GLES20.glVertexAttribPointer(aPositionLocation, POISTION_COMPONET_COUNT, GLES20.GL_FLOAT,
                false, STRIDE, vertexData);
        // 启用属性
        GLES20.glEnableVertexAttribArray(aPositionLocation);
        // 关联并启用颜色
        vertexData.position(POISTION_COMPONET_COUNT);
        GLES20.glVertexAttribPointer(aColorLocation, COLOR_COMPONET_COUNT, GLES20.GL_FLOAT,
                false, STRIDE, vertexData);
        GLES20.glEnableVertexAttribArray(aColorLocation);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0,0,width,height);
        // 创建45度透视投影，这个视椎体从Z值为-1位置开始到z值为-10位置结束
        MatrixHelper.perspectiveM(projectionMatrix, 45,
                (float)width / (float)height, 1f, 10f);
        // 由于没有设置Z值所以Z默认为0，但是当前视椎体从-1开始，
        // 由于物体不在视椎体中，所以无法显示

        Matrix.setIdentityM(modelMatrix, 0);    // 初始化单位矩阵
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2.5f); // 沿Z轴平移-2.5
        Matrix.rotateM(modelMatrix, 0, -60, 1f, 0f, 0f);// 以X轴旋转-60

        // 矩阵相乘，结果更新到投影矩阵
        final float[] temp = new float[16];
        Matrix.multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        // 给着色器传递正交投影矩阵
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);

        // 绘制类型，开始顶点，读取个数
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0 , 6);    // 前六个顶点绘制三角形

        // 绘制线
        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2);
        // 绘制点
        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1);
    }
}
