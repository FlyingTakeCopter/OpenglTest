package com.example.liuqikang.myapplication.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.example.liuqikang.myapplication.R;

/**
 * Created by Administrator on 2018/4/22.
 */

public class TextureShaderProgram extends ShaderProgram {
    // uniform
    private final int uMatrixLocation;
    private final int uTextureUnitLocation;
    // attribute
    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;

    public TextureShaderProgram(Context cxt){
        super(cxt, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);

        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MARTIX);
        uTextureUnitLocation = GLES20.glGetUniformLocation(program, U_TEXURE_UNIT);

        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = GLES20.glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    // 传递矩阵和纹理，给uniform
    public void setUniforms(float[] matrix, int textureID){
        // 传递matrix到uMatrixLocation
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);

        // 传递纹理到uTextureUnitLocation
        // 激活一个纹理单元GL_TEXTURE0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // 绑定一个纹理到活动单元GL_TEXTURE0
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);
        // 把选定的纹理单元传递给片段着色器
        GLES20.glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPositionLocation(){
        return aPositionLocation;
    }

    public int getTextureCoordinatesLocation(){
        return aTextureCoordinatesLocation;
    }
}
