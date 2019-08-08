package com.example.liuqikang.myapplication.camera;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liuqikang.myapplication.R;
import com.example.liuqikang.myapplication.gles.EglCore;
import com.example.liuqikang.myapplication.gles.FullFrameRect;
import com.example.liuqikang.myapplication.gles.Texture2dProgram;
import com.example.liuqikang.myapplication.gles.WindowSurface;
import com.example.liuqikang.myapplication.util.PermissionHelper;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends Activity implements
        SurfaceTexture.OnFrameAvailableListener, View.OnClickListener {
    private static final String TAG = CameraActivity.class.getName();

    private static final int VIDEO_WIDTH = 1920;
    private static final int VIDEO_HEIGHT = 1080;
    private static int DESIRED_PREVIEW_FPS = 15;

    private FullFrameRect fullFrameBlit;
    private SurfaceTexture cameraTexture;  // receives the output from the camera preview
    private int mTextureId;
    private int mCameraPreviewThousandFps;

    private CameraHandler mHandler;
    CameraSurfaceView cameraSurfaceView;
    private Camera camera;

    private TextView fpsText;
    private Spinner fpsSpinner;

    private FrameLayout frameLayout;

    private Button frameMove;
    private Button surfaceMove;
    private Button frameRotation;
    private Button frameScale;
    private Button frameTranslation;
    private Button frameAlpha;

    boolean canSurfaceMove = false;
    boolean canFrameMove = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        fpsText = (TextView) findViewById(R.id.camera_text);
        fpsSpinner = (Spinner) findViewById(R.id.spinner_simple);
        fpsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (camera != null){
                    Camera.Parameters parms = camera.getParameters();

                    DESIRED_PREVIEW_FPS = Integer.parseInt(parent.getItemAtPosition(position).toString());

                    mCameraPreviewThousandFps = CameraUtils.chooseFixedPreviewFps(parms, DESIRED_PREVIEW_FPS * 1000);

                    // Give the camera a hint that we're recording video.  This can have a big
                    // impact on frame rate.
                    parms.setRecordingHint(true);

                    camera.setParameters(parms);
                }
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
        frameMove.setOnClickListener(this);
        surfaceMove.setOnClickListener(this);
        frameRotation.setOnClickListener(this);
        frameScale.setOnClickListener(this);
        frameTranslation.setOnClickListener(this);
        frameAlpha.setOnClickListener(this);

        cameraSurfaceView = (CameraSurfaceView) findViewById(R.id.camera_surfaceview);
        cameraSurfaceView.init(new CameraSurfaceView.surfaceCreateListener() {
            @Override
            public void surCreated(int textureID) {
                // 初始化OpenglEs环境
                fullFrameBlit = new FullFrameRect(new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_EXT));
                // 获取GLES的TextureID
                mTextureId = fullFrameBlit.createTextureObject();
                // 初始化 绑定SurfaceTexture
                cameraTexture = new SurfaceTexture(mTextureId);
                cameraTexture.setOnFrameAvailableListener(getActivity());
                startPreview();
            }

            @Override
            public void onCurrentFps(int fps) {
                fpsText.setText("FPS: " + fps);
            }
        });

        mHandler = new CameraHandler(this);

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

    private CameraActivity getActivity(){
        return this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!PermissionHelper.hasCameraPermission(this)) {
            PermissionHelper.requestCameraPermission(this, false);
        } else  {
            if (camera == null) {
                // Ideally, the frames from the camera are at the same resolution as the input to
                // the video encoder so we don't have to scale.
                openCamera(VIDEO_WIDTH, VIDEO_HEIGHT, DESIRED_PREVIEW_FPS);
            }
            if (!cameraSurfaceView.isEglNull()) {
                startPreview();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        releaseCamera();

        if (fullFrameBlit != null){
            fullFrameBlit.release(false);
            fullFrameBlit = null;
        }

        if (cameraTexture != null) {
            cameraTexture.release();
            cameraTexture = null;
        }
        cameraSurfaceView.onPause();
    }

    private void startPreview(){
        if (camera != null){
            try{
                camera.setPreviewTexture(cameraTexture);
            }catch (IOException e){
                throw new RuntimeException(e);
            }
            camera.startPreview();
        }

    }

    private void openCamera(int desiredWidth, int desiredHeight, int desiredFps) {
        if (camera != null) {
            throw new RuntimeException("camera already initialized");
        }

        Camera.CameraInfo info = new Camera.CameraInfo();

        // Try to find a front-facing camera (e.g. for videoconferencing).
        int nucameras = Camera.getNumberOfCameras();
        for (int i = 0; i < nucameras; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                camera = Camera.open(i);
                break;
            }
        }
        if (camera == null) {
            Log.d(TAG, "No front-facing camera found; opening default");
            camera = Camera.open();    // opens first back-facing camera
        }
        if (camera == null) {
            throw new RuntimeException("Unable to open camera");
        }

        Camera.Parameters parms = camera.getParameters();

        CameraUtils.choosePreviewSize(parms, desiredWidth, desiredHeight);

        // Try to set the frame rate to a constant value.
        mCameraPreviewThousandFps = CameraUtils.chooseFixedPreviewFps(parms, desiredFps * 1000);

        pickCameraParams(parms);

        // Give the camera a hint that we're recording video.  This can have a big
        // impact on frame rate.
        parms.setRecordingHint(true);

        camera.setParameters(parms);

        Camera.Size cameraPreviewSize = parms.getPreviewSize();
        String previewFacts = cameraPreviewSize.width + "x" + cameraPreviewSize.height +
                " @" + (mCameraPreviewThousandFps / 1000.0f) + "fps";
        Log.i(TAG, "Camera config: " + previewFacts);

//        AspectFrameLayout layout = (AspectFrameLayout) findViewById(R.id.continuousCapture_afl);

        Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        if(display.getRotation() == Surface.ROTATION_0) {
            camera.setDisplayOrientation(90);
//            layout.setAspectRatio((double) cameraPreviewSize.height / cameraPreviewSize.width);
        } else if(display.getRotation() == Surface.ROTATION_270) {
//            layout.setAspectRatio((double) cameraPreviewSize.height / cameraPreviewSize.width);
            camera.setDisplayOrientation(180);
        } else {
            // Set the preview aspect ratio.
//            layout.setAspectRatio((double) cameraPreviewSize.width / cameraPreviewSize.height);
        }

    }

    private void pickCameraParams(Camera.Parameters parms){
        // FPS 提取
        List<int[]> supported = parms.getSupportedPreviewFpsRange();

        List<Integer> fpsList = new ArrayList<>();
        for (int[] entry : supported) {
            Log.e(TAG, "fps: " + entry[0] + " " + entry[1]);
            if(!fpsList.contains(entry[1] / 1000)) {
                fpsList.add(entry[1] / 1000);
            }
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,
                android.R.layout.simple_list_item_1, fpsList);

        fpsSpinner.setAdapter(adapter);

        // 白平衡

    }

    /**
     * Stops camera preview, and releases the camera to the system.
     */
    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
            Log.d(TAG, "releaseCamera -- done");
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        // 激活绘制
        mHandler.sendEmptyMessage(CameraHandler.MSG_FRAME_AVAILABLE);
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
                    cameraSurfaceView.setX(0);
                    cameraSurfaceView.setY(0);
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
        }
    }

    private static class CameraHandler extends Handler{
        // 刷新视频帧
        public static final int MSG_FRAME_AVAILABLE = 0;

        private WeakReference<CameraActivity> mWeakActivity;

        public CameraHandler(CameraActivity activity){
            mWeakActivity = new WeakReference<CameraActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            CameraActivity activit = mWeakActivity.get();
            if (activit == null){
                Log.e(TAG, "handler activity null");
                return;
            }

            switch (msg.what){
                case MSG_FRAME_AVAILABLE:
                    if (activit.getCameraSurfaceView() != null){
                        activit.getCameraSurfaceView().drawFrame(
                                activit.getCameraTexture(), activit.getmTextureId());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!PermissionHelper.hasCameraPermission(this)) {
            Toast.makeText(this,
                    "申请相机权限失败", Toast.LENGTH_LONG).show();
            PermissionHelper.launchPermissionSettings(this);
            finish();
        } else {
            openCamera(VIDEO_WIDTH, VIDEO_HEIGHT, DESIRED_PREVIEW_FPS);
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
                surfaceTouchX = event.getX() - cameraSurfaceView.getX();
                surfaceTouchY = event.getY() - cameraSurfaceView.getY();
            }

            cameraSurfaceView.setX(event.getX() - surfaceTouchX);
            cameraSurfaceView.setY(event.getY() - surfaceTouchY);
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


    public CameraSurfaceView getCameraSurfaceView() {
        return cameraSurfaceView;
    }


    public SurfaceTexture getCameraTexture() {
        return cameraTexture;
    }

    public int getmTextureId() {
        return mTextureId;
    }
}
