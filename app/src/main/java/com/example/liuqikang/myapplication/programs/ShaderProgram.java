package com.example.liuqikang.myapplication.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.example.liuqikang.myapplication.util.ShaderHelper;
import com.example.liuqikang.myapplication.util.TextResourceReader;

/**
 * Created by Administrator on 2018/4/22.
 */

public class ShaderProgram {
    // uniform
    protected static final String U_MARTIX = "u_Matrix";
    protected static final String U_TEXURE_UNIT = "u_TextureUnit";

    // attribute
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    // shader program
    protected final int program;
    protected ShaderProgram(Context cxt, int vertexShaderResourceID,
                            int fragmentShaderResourceID){
        program = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(cxt, vertexShaderResourceID),
                TextResourceReader.readTextFileFromResource(cxt, fragmentShaderResourceID));
    }

    public void useProgram(){
        GLES20.glUseProgram(program);
    }
}
