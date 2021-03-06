package com.example.liuqikang.myapplication.objects;

import android.opengl.GLES20;

import com.example.liuqikang.myapplication.Constants;
import com.example.liuqikang.myapplication.data.VertexArray;
import com.example.liuqikang.myapplication.programs.ColorShaderProgram;

/**
 * Created by Administrator on 2018/4/22.
 */

public class Mallet {
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
        + COLOR_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;

    private static final float[] VERTEX_DATA = {
            // x,   y,      R,  G,  B
            0f,    -0.4f,   0f,0f,1f,
            0f,     0.4f,   0f,1f,0f
    };

    private final VertexArray vertexArray;

    public Mallet(){
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(ColorShaderProgram colorShaderProgram){
        // 关联顶点数组
        vertexArray.setVertexAttributePointer(
                0,
                colorShaderProgram.getPositionLoaction(),
                POSITION_COMPONENT_COUNT, STRIDE
        );
        // 关联颜色数组
        vertexArray.setVertexAttributePointer(
                POSITION_COMPONENT_COUNT,
                colorShaderProgram.getColorLocation(),
                COLOR_COMPONENT_COUNT, STRIDE
        );
    }

    public void draw(){
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 2);
    }
}
