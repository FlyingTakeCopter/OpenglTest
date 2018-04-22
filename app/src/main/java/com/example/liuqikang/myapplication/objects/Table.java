package com.example.liuqikang.myapplication.objects;

import android.opengl.GLES20;

import com.example.liuqikang.myapplication.Constants;
import com.example.liuqikang.myapplication.data.VertexArray;

/**
 * Created by Administrator on 2018/4/22.
 */

public class Table {
    private static final int POSITION_COMPONENT_COUNT = 2;  // position
    private static final int TEXTURE_COORDINATES_COUNT = 2; // S, T
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
        + TEXTURE_COORDINATES_COUNT) * Constants.BYTES_PER_FLOAT;// 跨距

    // data
    private static final float[] VERTEX_DATA = {
            // X, Y, S, T
            0f,     0f,     0.5f,   0.5f,
            -0.5f,  -0.8f,  0f,     0.9f,
            0.5f,   -0.8f,  1f,     0.9f,
            0.5f,   0.8f,   1f,     0.1f,
            -0.5f,  0.8f,   0f,     0.1f,
            -0.5f,  -0.8f,  0f,     0.9f
    };

    private final VertexArray vertexArray;

    public Table(){
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(){

    }

    public void draw(){
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);
    }

}
