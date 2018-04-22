package com.example.liuqikang.myapplication.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.example.liuqikang.myapplication.R;

/**
 * Created by liuqikang on 2018/4/22.
 */

public class ColorShaderProgram extends ShaderProgram {
    // uniform
    private final int uMatrixLocation;
    // attribute
    private final int aPositionLoaction;
    private final int aColorLocation;

    public ColorShaderProgram(Context cxt){
        super(cxt, R.raw.simple_vertex_sharder_5, R.raw.simple_fragment_shader_4);

        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MARTIX);

        aPositionLoaction = GLES20.glGetAttribLocation(program, A_POSITION);
        aColorLocation = GLES20.glGetAttribLocation(program, A_COLOR);
    }

    public void setUniform(float[] matrix){
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
    }

    public int getPositionLoaction(){
        return aPositionLoaction;
    }

    public int getColorLocation(){
        return aColorLocation;
    }
}
