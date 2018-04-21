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
        // 启动GLES20
        view.setEGLContextClientVersion(2);
        // 第三章
//        view.setRenderer(new TestRender(this));
        // 第四章
//        view.setRenderer(new TestColorRender(this));
        // 第五章
//        view.setRenderer(new TestMatrixRender(this));
        // 第六章
//        view.setRenderer(new Test3DRender_w(this));   // w分量测试
        view.setRenderer(new Test3DRender_Matrix(this));    // 投影矩阵测试
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
}
