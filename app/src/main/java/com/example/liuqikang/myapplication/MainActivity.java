package com.example.liuqikang.myapplication;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = new GLSurfaceView(this);
        // request opengles 2.0 compatible context
        view.setEGLContextClientVersion(2);

        // 第一章Assign renderer
//        view.setRenderer(new FirstOpenglProjectRender());
        // 第二章
        // 第三章
//        view.setRenderer(new AirHockeyRenderer(this));
        view.setRenderer(new TestRender(this));
        // 第四章
//        view.setRenderer(new TestColorRender(this));
        // 第五章
//        view.setRenderer(new TestMatrixRender(this));
        // 第六章
//        view.setRenderer(new Test3DRender_w(this));   // w分量测试
//        view.setRenderer(new Test3DRender_Matrix(this));    // 投影矩阵测试
        // 第七章
//        view.setRenderer(new TestTextureRender(this));
        // 第十章
//        view.setRenderer(new TestParticleRender(this));
        setContentView(view);

    }

    @Override
    protected void onPause() {
        super.onPause();
        view.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        view.onResume();
    }

    // GLSurfaceView 在一个单独的线程中调用渲染器方法（后台线程，非UI线程）
    // 所以只能在渲染线程中调用opengl绘制
    // 线程通信
    // 从UI线程到GLSurfaceView渲染线程使用 GLSurfaceView.queueEvent(Runable)
    // 从渲染线程到UI线程使用 Activity.runOnUiThread
}
