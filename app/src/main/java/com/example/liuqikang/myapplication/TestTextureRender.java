package com.example.liuqikang.myapplication;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.liuqikang.myapplication.objects.Mallet;
import com.example.liuqikang.myapplication.objects.Table;
import com.example.liuqikang.myapplication.programs.ColorShaderProgram;
import com.example.liuqikang.myapplication.programs.TextureShaderProgram;
import com.example.liuqikang.myapplication.util.MatrixHelper;
import com.example.liuqikang.myapplication.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by liuqikang on 2018/4/22.
 */

public class TestTextureRender implements GLSurfaceView.Renderer {
    private final Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    private Table table;
    private Mallet mallet;

    private TextureShaderProgram textureShaderProgram;
    private ColorShaderProgram colorShaderProgram;

    private int texture;

    public TestTextureRender(Context cxt){
        context = cxt;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        table = new Table();
        mallet = new Mallet();

        textureShaderProgram = new TextureShaderProgram(context);
        colorShaderProgram = new ColorShaderProgram(context);

        texture = TextureHelper.loadTexture(context, R.drawable.table);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        // 创建45度透视投影矩阵
        MatrixHelper.perspectiveM(projectionMatrix,
                45, (float) width / (float) height, 1f, 10f);
        // 创建平移矩阵
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2.5f);
        Matrix.rotateM(modelMatrix, 0, -60, 1f, 0f, 0f);

        // 矩阵相乘
        float[] temp = new float[16];
        Matrix.multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        // 赋值
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // draw Table
        textureShaderProgram.useProgram();
        textureShaderProgram.setUniforms(projectionMatrix, texture);
        table.bindData(textureShaderProgram);
        table.draw();

        // draw Mallet
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniform(projectionMatrix);
        mallet.bindData(colorShaderProgram);
        mallet.draw();
    }
}
