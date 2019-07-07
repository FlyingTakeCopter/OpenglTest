package com.example.liuqikang.myapplication;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static android.opengl.GLUtils.*;
import static android.opengl.Matrix.*;

/**
 * Created by liuqikang on 2019/7/7.
 */

public class FirstOpenglProjectRender implements GLSurfaceView.Renderer {
    // onSurfaceCreated
    // 会被多次调用
    // 调用时机：
    //      第一次运行时 surface被创建的时候
    //      设备被唤醒
    //      从其他activity切换回来时
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 设置清空屏幕用的颜色
        glClearColor(1.0f, 0.0f, 0f, 0f);
    }

    // onSurfaceChanged
    // 每次surface尺寸发生变化时，都会被调用
    // 横竖屏切换时调用
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 设置视口大小 告诉opengl可以用来渲染的surface大小
        glViewport(0,0,width,height);
    }

    //onDrawFrame
    // 当绘制一帧的时候调用
    // 在这个方法中一定要绘制一些东西，即使即使清空屏幕
    // 因为在这个方法返回后，渲染缓冲区会被交换显示在屏幕上
    // 如果什么都没画，很可能会看到糟糕的闪烁效果
    @Override
    public void onDrawFrame(GL10 gl) {
        // 清空屏幕 擦除所有颜色 并且用之前GLES20.glClearColor定义的颜色填充整个屏幕
        glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }
}
