package com.example.liuqikang.myapplication.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.WINDOW_SERVICE;

public class CameraController {
    private final static String TAG = CameraController.class.getName();

    private volatile static Camera camera;

    private static int cameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;

    public static synchronized void openCamera(Context context,
                                               int desiredWidth, int desiredHeight, int desiredFps) {
        if (camera != null) {
            throw new RuntimeException("camera already initialized");
        }

        Camera.CameraInfo info = new Camera.CameraInfo();

        // Try to find a front-facing camera (e.g. for videoconferencing).
        int nucameras = Camera.getNumberOfCameras();
        for (int i = 0; i < nucameras; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == cameraFacing) {
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
        int cameraPreviewThousandFps = CameraUtils.chooseFixedPreviewFps(parms, desiredFps * 1000);

        // Give the camera a hint that we're recording video.  This can have a big
        // impact on frame rate.
        parms.setRecordingHint(true);

        camera.setParameters(parms);

        Camera.Size cameraPreviewSize = parms.getPreviewSize();
        String previewFacts = cameraPreviewSize.width + "x" + cameraPreviewSize.height +
                " @" + (cameraPreviewThousandFps / 1000.0f) + "fps";
        Log.i(TAG, "Camera config: " + previewFacts);

//        AspectFrameLayout layout = (AspectFrameLayout) findViewById(R.id.continuousCapture_afl);

        Display display = ((WindowManager)context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

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

    public static synchronized void startPreview(SurfaceTexture cameraTexture){
        if (camera != null){
            try{
                camera.setPreviewTexture(cameraTexture);
            }catch (IOException e){
                throw new RuntimeException(e);
            }
            camera.startPreview();
        }

    }

    public static ArrayList<Integer> getCameraFps(){
        if (camera != null){
            Camera.Parameters parms = camera.getParameters();

            // FPS 提取
            List<int[]> supported = parms.getSupportedPreviewFpsRange();

            ArrayList<Integer> fpsList = new ArrayList<>();
            for (int[] entry : supported) {
                Log.e(TAG, "fps: " + entry[0] + " " + entry[1]);
                if(!fpsList.contains(entry[1] / 1000)) {
                    fpsList.add(entry[1] / 1000);
                }
            }

            return fpsList;
        }
        return null;
    }

    public static synchronized void setCameraFPS(int DESIRED_PREVIEW_FPS){
        if (camera != null){
            Camera.Parameters parms = camera.getParameters();

            CameraUtils.chooseFixedPreviewFps(parms, DESIRED_PREVIEW_FPS * 1000);

            // Give the camera a hint that we're recording video.  This can have a big
            // impact on frame rate.
            parms.setRecordingHint(true);

            camera.setParameters(parms);
        }
    }

    public static synchronized void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
            Log.d(TAG, "releaseCamera -- done");
        }
    }

    public static synchronized boolean canOpenCamera(){
        return camera == null;
    }

    public static synchronized void switchCamera(){
        releaseCamera();
        if (cameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK){
            cameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }else {
            cameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
    }
}
