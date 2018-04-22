package com.example.liuqikang.myapplication.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.example.liuqikang.myapplication.R;

/**
 * Created by liuqikang on 2018/4/22.
 */

public class ParticleShaderProgram extends ShaderProgram {
    // uniform
    private final int uMatrixLocation;
    private final int uTimeLocation;

    // attribute
    private final int aPositionLocation;
    private final int aColorLocation;
    private final int aDirectionVectorLocation;
    private final int aParticleStartTimeLocation;

    public ParticleShaderProgram(Context cxt){
        super(cxt, R.raw.particle_vertex_shader, R.raw.particle_fragment_shader);

        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MARTIX);
        uTimeLocation = GLES20.glGetUniformLocation(program, U_TIME);

        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        aColorLocation = GLES20.glGetAttribLocation(program, A_COLOR);
        aDirectionVectorLocation = GLES20.glGetAttribLocation(program, A_DIRECTION_VECTOR);
        aParticleStartTimeLocation = GLES20.glGetAttribLocation(program, A_PARTICLE_START_TIME);
    }

    public void setUniform(float[] matrix, float elapsedTime){
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        GLES20.glUniform1f(uTimeLocation, elapsedTime);
    }

    public int getPositionLocation(){
        return aPositionLocation;
    }

    public int getColorLocation(){
        return aColorLocation;
    }

    public int getDirectionVectorLocation(){
        return aDirectionVectorLocation;
    }

    public int getParticleStartTimeLocation(){
        return aParticleStartTimeLocation;
    }
}
