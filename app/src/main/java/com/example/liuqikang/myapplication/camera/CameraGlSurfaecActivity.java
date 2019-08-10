package com.example.liuqikang.myapplication.camera;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.liuqikang.myapplication.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class CameraGlSurfaecActivity extends Activity implements SurfaceTexture.OnFrameAvailableListener, View.OnClickListener {
    private final static String TAG = CameraGlSurfaceView.class.getName();

    public CameraGlSurfaceView getGlSurfaceView() {
        return glSurfaceView;
    }

    CameraGlSurfaceView glSurfaceView;

    public SurfaceTexture getCameraTexture() {
        return cameraTexture;
    }

    SurfaceTexture cameraTexture;

    public int getmTextureID() {
        return mTextureID;
    }

    private TextView fpsText;
    private Spinner fpsSpinner;

    private FrameLayout frameLayout;

    private Button frameMove;
    private Button surfaceMove;
    private Button frameRotation;
    private Button frameScale;
    private Button frameTranslation;
    private Button frameAlpha;

    private Button switchCamera;

    boolean canSurfaceMove = false;
    boolean canFrameMove = false;

    int mTextureID;

    private static final int VIDEO_WIDTH = 1920;
    private static final int VIDEO_HEIGHT = 1080;
    private static int DESIRED_PREVIEW_FPS = 15;

    CameraHandler handler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glsurface_camera);
        handler = new CameraHandler(this);

        fpsText = (TextView) findViewById(R.id.camera_text);
        fpsSpinner = (Spinner) findViewById(R.id.spinner_simple);
        fpsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DESIRED_PREVIEW_FPS = Integer.parseInt(parent.getItemAtPosition(position).toString());

                CameraController.setCameraFPS(DESIRED_PREVIEW_FPS);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        frameLayout = (FrameLayout) findViewById(R.id.camera_frame_layout);
        frameMove = (Button) findViewById(R.id.camera_framelayout_move);
        surfaceMove = (Button) findViewById(R.id.camera_surfaceView_move);
        frameRotation = (Button)findViewById(R.id.camera_framelayout_rotation);
        frameScale = (Button) findViewById(R.id.camera_framelayout_scale);
        frameTranslation = (Button) findViewById(R.id.camera_framelayout_translation);
        frameAlpha = (Button) findViewById(R.id.camera_framelayout_alpha);
        switchCamera = (Button) findViewById(R.id.camera_switch);
        frameMove.setOnClickListener(this);
        surfaceMove.setOnClickListener(this);
        frameRotation.setOnClickListener(this);
        frameScale.setOnClickListener(this);
        frameTranslation.setOnClickListener(this);
        frameAlpha.setOnClickListener(this);
        switchCamera.setOnClickListener(this);

        glSurfaceView = (CameraGlSurfaceView) findViewById(R.id.camera_glsurfaceview);
        CameraGlSurfaceView.GLSurfaceListener listener = new CameraGlSurfaceView.GLSurfaceListener() {
            @Override
            public void surfaceCreate(int textureID) {
                mTextureID = textureID;
                cameraTexture = new SurfaceTexture(mTextureID);
                cameraTexture.setOnFrameAvailableListener(getActivity());

                CameraController.startPreview(cameraTexture);
            }

            @Override
            public void onCurrentFps(final int fps) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fpsText.setText("FPS: " + fps);
                    }
                });
            }
        };
        glSurfaceView.init(listener);

        findViewById(R.id.camera_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PropertyValuesHolder valuesHolder = PropertyValuesHolder.ofFloat("Rotation", 0.0f, 360.0f, 0.0F);
//                PropertyValuesHolder valuesHolder1 = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.5f,1.0f);
//                PropertyValuesHolder valuesHolder3 = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.5f,1.0f);
//                PropertyValuesHolder valuesHolder = PropertyValuesHolder.ofFloat("translationX",
//                        0.0f, 1000f, 0.0F);
                PropertyValuesHolder valuesHolder1 = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0f, 1.0F);

                ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(frameLayout,  valuesHolder, valuesHolder1);
                objectAnimator.setDuration(5000).start();
            }
        });
    }

    private CameraGlSurfaecActivity getActivity(){
        return this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        CameraController.releaseCamera();
//        glSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (CameraController.canOpenCamera()) {
            // Ideally, the frames from the camera are at the same resolution as the input to
            // the video encoder so we don't have to scale.
            CameraController.openCamera(this, VIDEO_WIDTH, VIDEO_HEIGHT, DESIRED_PREVIEW_FPS);

            fpsSpinner.post(new Runnable() {
                @Override
                public void run() {
                    ArrayList<Integer> fpsList = CameraController.getCameraFps();
                    ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getActivity(),
                            android.R.layout.simple_list_item_1, fpsList);
                    fpsSpinner.setAdapter(adapter);
                }
            });
        }
        if (!glSurfaceView.isScreenNull()){
            CameraController.startPreview(cameraTexture);
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        handler.sendEmptyMessage(CameraHandler.MSG_FRAME_AVAILABLE);
    }

    int rotation = 90;
    float scale = 0.5f;
    float translation = 100;
    float alpha = 0.5f;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.camera_surfaceView_move:
                canSurfaceMove = !canSurfaceMove;
                surfaceMove.setText("surface移动:" + (canSurfaceMove ? "开" : "复位"));
                if (!canSurfaceMove){
                    glSurfaceView.setX(0);
                    glSurfaceView.setY(0);
                }
                break;
            case R.id.camera_framelayout_move:
                canFrameMove = !canFrameMove;
                frameMove.setText("frameLayout移动:" + (canFrameMove ? "开" : "复位"));
                if (!canFrameMove){
                    frameLayout.setX(0);
                    frameLayout.setY(0);
                }
                break;
            case R.id.camera_framelayout_rotation:
                frameLayout.setRotation(rotation);
                if (rotation == 90){
                    rotation = 0;
                    frameRotation.setText("旋转复位");
                }else {
                    rotation = 90;
                    frameRotation.setText("旋转90");
                }
                break;
            case R.id.camera_framelayout_scale:
                frameLayout.setScaleX(scale);
                frameLayout.setScaleY(scale);
                if (scale == 0.5){
                    scale = 1;
                    frameScale.setText("缩放复位");
                }else {
                    scale = 0.5f;
                    frameScale.setText("缩放0.5");
                }
                break;
            case R.id.camera_framelayout_translation:
                frameLayout.setTranslationY(translation);
                if (translation == 100f){
                    translation = 0f;
                    frameTranslation.setText("偏移复位");
                }else {
                    translation = 100f;
                    frameTranslation.setText("Y偏移100");
                }
                break;
            case R.id.camera_framelayout_alpha:
                frameLayout.setAlpha(alpha);
                if (alpha == 0.5f){
                    alpha = 1.0f;
                    frameAlpha.setText("复位");
                }else {
                    alpha = 0.5f;
                    frameAlpha.setText("透明50");
                }
                break;
            case R.id.camera_switch:
                CameraController.switchCamera();
                CameraController.openCamera(this, VIDEO_WIDTH, VIDEO_HEIGHT, DESIRED_PREVIEW_FPS);
                CameraController.startPreview(cameraTexture);
                break;
        }
    }

    float surfaceTouchX;
    float surfaceTouchY;

    float framelayoutTouchX;
    float framelayoutTouchY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (canSurfaceMove){
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                surfaceTouchX = event.getX() - glSurfaceView.getX();
                surfaceTouchY = event.getY() - glSurfaceView.getY();
            }

            glSurfaceView.setX(event.getX() - surfaceTouchX);
            glSurfaceView.setY(event.getY() - surfaceTouchY);
        }

        if (canFrameMove){
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                framelayoutTouchX = event.getX() - frameLayout.getX();
                framelayoutTouchY = event.getY() - frameLayout.getY();
            }

            frameLayout.setX(event.getX() - framelayoutTouchX);
            frameLayout.setY(event.getY() - framelayoutTouchY);
        }

        return super.onTouchEvent(event);
    }

    private static class CameraHandler extends Handler {
        // 刷新视频帧
        public static final int MSG_FRAME_AVAILABLE = 0;

        private WeakReference<CameraGlSurfaecActivity> mWeakActivity;

        public CameraHandler(CameraGlSurfaecActivity activity){
            mWeakActivity = new WeakReference<CameraGlSurfaecActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            CameraGlSurfaecActivity activit = mWeakActivity.get();
            if (activit == null){
                Log.e(TAG, "handler activity null");
                return;
            }

            switch (msg.what){
                case MSG_FRAME_AVAILABLE:
                    if (activit.getGlSurfaceView() != null){
                        activit.getGlSurfaceView().onDrawFrame(
                                activit.getCameraTexture(), activit.getmTextureID());
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
