package com.example.liuqikang.myapplication;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.example.liuqikang.myapplication.util.ShaderHelper;
import com.example.liuqikang.myapplication.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

public class AirHockeyRenderer_4 implements GLSurfaceView.Renderer {
    private Context mContext;

    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    private static final String A_COLOR = "a_Color";
    private int aColorLocation;

    private static final int BYTES_FLOAT_SIZE = 4;
    FloatBuffer vertexBuffer;

    private static final int POSITION_COUNT = 2;
    private static final int COLOR_COUNT = 3;


    public AirHockeyRenderer_4(Context context) {
        mContext = context;

        float[] data = {
                // 扇形绘制
                0f, 0f,         0.0f, 0.0f, 0.0f,
                -0.5f, -0.5f,   1.0f, 1.0f, 1.0f,
                0.5f,-0.5f,     1.0f, 1.0f, 1.0f,
                0.5f,0.5f,      1.0f, 1.0f, 1.0f,
                -0.5f,0.5f,     1.0f, 1.0f, 1.0f,
                -0.5f,-0.5f,    1.0f, 1.0f, 1.0f,

                -0.5f, 0f,      1.0f, 0.0f, 0.0f,
                0.5f, 0f,       0.0f, 0.0f, 1.0f,

                0f, -0.25f,     0.0f, 1.0f, 0.0f,
                0f, 0.25f,      0.0f, 0.0f, 1.0f
        };
        vertexBuffer = ByteBuffer
                .allocateDirect(data.length * BYTES_FLOAT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(data);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0f, 0f, 0f, 1f);

        int program = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(mContext, R.raw.simple_vertex_sharder_4),
                TextResourceReader.readTextFileFromResource(mContext, R.raw.simple_fragment_shader_4)
        );
        // 一定要设置当前的program
        glUseProgram(program);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aColorLocation = glGetAttribLocation(program, A_COLOR);
        // 设置坐标分量
        vertexBuffer.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COUNT,
                GL_FLOAT, false, (POSITION_COUNT + COLOR_COUNT)*BYTES_FLOAT_SIZE, vertexBuffer);
        // 一定要启用attribute
        glEnableVertexAttribArray(aPositionLocation);
        // 设置颜色分量
        vertexBuffer.position(POSITION_COUNT);
        glVertexAttribPointer(aColorLocation, COLOR_COUNT,
                GL_FLOAT, false, (POSITION_COUNT + COLOR_COUNT)*BYTES_FLOAT_SIZE, vertexBuffer);
        glEnableVertexAttribArray(aColorLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0,0,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

        glDrawArrays(GL_LINES, 6, 2);

        glDrawArrays(GL_POINTS, 8, 1);
        glDrawArrays(GL_POINTS, 9, 1);
    }
}
