package com.example.liuqikang.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.example.liuqikang.myapplication.camera.CameraGlSurfaecActivity;
import com.example.liuqikang.myapplication.camera.CameraSurfaceViewActivity;
import com.example.liuqikang.myapplication.camera.CameraTextureViewActivity;

public class AllActivity extends Activity implements View.OnClickListener {

    Button surfaceViewBtn;
    Button glSurfaceViewBtn;
    Button textureViewBtn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all);

        surfaceViewBtn = (Button) findViewById(R.id.cameraSurfaceBtn);
        glSurfaceViewBtn = (Button)findViewById(R.id.cameraGlSurfaceBtn);
        textureViewBtn = (Button) findViewById(R.id.cameraTextureBtn);
        surfaceViewBtn.setOnClickListener(this);
        glSurfaceViewBtn.setOnClickListener(this);
        textureViewBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cameraSurfaceBtn:
                startActivity(new Intent(this, CameraSurfaceViewActivity.class));
                break;
            case R.id.cameraGlSurfaceBtn:
                startActivity(new Intent(this, CameraGlSurfaecActivity.class));
                break;
            case R.id.cameraTextureBtn:
                startActivity(new Intent(this, CameraTextureViewActivity.class));
                break;
        }
    }
}
