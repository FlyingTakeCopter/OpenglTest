package com.example.liuqikang.myapplication.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

/**
 * Created by Administrator on 2018/4/22.
 */

public class TextureHelper {
    private static final String TAG = "TextureHelper";

    public static int loadTexture(Context cxt, int resourceID){
        // 创建纹理对象
        final int[] textureObjectID = new int[1];
        GLES20.glGenTextures(1, textureObjectID, 0);

        if (textureObjectID[0] != GLES20.GL_TRUE){
            MyLog.Log(TAG, "create texture failed");
            return 0;
        }

        // 读取图像并压缩为Android位图
        final BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inScaled = false;   // 非缩放版本

        final Bitmap bmp = BitmapFactory.decodeResource(cxt.getResources(), resourceID, opt);
        if (bmp == null){
            MyLog.Log(TAG, "Resource ID" + resourceID + "could not be decoded");
            GLES20.glDeleteTextures(1, textureObjectID, 0);
            return 0;
        }

        // 告诉opengl后面的纹理应该应用于这个纹理对象
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectID[0]);

        // 设置纹理过滤 GL_LINEAR_MIPMAP_LINEAR 三线性过滤
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);// 缩小采用最邻近过滤
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);//放大采用双线性过滤

        // 加载位图到opengl
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

        // 释放读入的位图
        bmp.recycle();

        // 生成所有的MIP贴图
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        // 接触当前纹理绑定，防止调用其他纹理方法修改当前绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        return textureObjectID[0];
    }
}
