package com.example.liuqikang.myapplication.util;

import android.opengl.GLES20;
import android.util.Log;

/**
 * Created by liuqikang on 2018/4/18.
 */

public class ShaderHelper {
    private static final String TAG = "ShaderHelper";
    // 编译顶点着色器
    public static int compileVertexShader(String shaderCode){
        return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode);

    }
    // 编译片着色器
    public static int compileFragmentShader(String shaderCode){
        return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode);
    }

    private static int compileShader(int type, String shaderCode){
        // 创建新的着色器对象
        final int sharderObjectID = GLES20.glCreateShader(type);
        if (sharderObjectID == 0){
            MyLog.Log(TAG, "compileShader failed");
            return 0;
        }
        // 上传着色器代码shaderCode到sharderObjectID关联的着色器对象中
        GLES20.glShaderSource(sharderObjectID, shaderCode);
        // 编译sharderObjectID关联的着色器代码
        GLES20.glCompileShader(sharderObjectID);
        // 取出与sharderObjectID关联的编译状态，并写入compileStatus第0个元素
        final int compileStatus[] = new int[1];
        GLES20.glGetShaderiv(sharderObjectID, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        if (compileStatus[0] == 0){
            MyLog.Log(TAG, shaderCode + "\n:" + GLES20.glGetShaderInfoLog(sharderObjectID));
            GLES20.glDeleteShader(sharderObjectID);
            return 0;
        }

        return sharderObjectID;
    }
    // 联结顶点着色器和片段着色器，返回opengl程序对象
    public static int linkProgram(int vertexShaderID, int fragmentShaderID){
        // 注册程序对象
        final int programObjectID = GLES20.glCreateProgram();
        if (programObjectID == 0){
            MyLog.Log(TAG, "glCreateProgram create failed");
            return 0;
        }
        // 绑定着色器
        GLES20.glAttachShader(programObjectID, vertexShaderID);
        GLES20.glAttachShader(programObjectID, fragmentShaderID);
        // 联合对象的所有着色器
        GLES20.glLinkProgram(programObjectID);
        // 取出programObjectID的着色器的联合状态状态，并写入programStatus第0个元素
        final int programStatus[] = new int[1];
        GLES20.glGetProgramiv(programObjectID, GLES20.GL_LINK_STATUS, programStatus, 0);

        if (programStatus[0] == 0){
            MyLog.Log(TAG, "linkProgram :\n" + GLES20.glGetProgramInfoLog(programObjectID));
            GLES20.glDeleteProgram(programObjectID);
            return 0;
        }

        return programObjectID;
    }
    // 判定opengl对象有效性
    public static int validateProgram(int program) {
        int[] status = new int[1];
        GLES20.glValidateProgram(program);
        // 获取VALIDATE信息
        GLES20.glGetProgramiv(program, GLES20.GL_VALIDATE_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            MyLog.Log(TAG, "Error validating program: " + GLES20.glGetProgramInfoLog(program));
            return 0;
        }
        return 1;
    }
}
