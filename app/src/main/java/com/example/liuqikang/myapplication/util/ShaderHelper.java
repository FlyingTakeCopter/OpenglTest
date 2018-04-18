package com.example.liuqikang.myapplication.util;

import android.opengl.GLES20;

/**
 * Created by liuqikang on 2018/4/18.
 */

public class ShaderHelper {
    private static final String TAG = "ShaderHelper";

    public static int compileVertexShader(String shaderCode){
        return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode);

    }

    public static int compileFragmentShader(String shaderCode){
        return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode);

    }

    private static int compileShader(int type, String shaderCode){
        final int sharderObjectID = GLES20.glCreateShader(type);
        if (sharderObjectID == 0){
            return 0;
        }
        return 0;
    }
}
