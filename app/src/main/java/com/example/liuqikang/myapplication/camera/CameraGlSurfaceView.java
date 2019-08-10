package com.example.liuqikang.myapplication.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.example.liuqikang.myapplication.gles.FullFrameRect;
import com.example.liuqikang.myapplication.gles.Texture2dProgram;
import com.example.liuqikang.myapplication.gles.WindowSurface;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraGlSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private FullFrameRect mFullScreen;
    private WindowSurface displaySurface;
    private GLSurfaceListener listener;

    public CameraGlSurfaceView(Context context) {
        super(context);
        init();
    }

    public CameraGlSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void init(GLSurfaceListener listener){
        this.listener = listener;
    }

    interface GLSurfaceListener{
        public void surfaceCreate(int textureID);
        public void onCurrentFps(int fps);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mFullScreen = new FullFrameRect(
                new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_EXT));
        if (listener != null){
            listener.surfaceCreate(mFullScreen.getProgram().createTextureObject());
        }
    }

    public boolean isScreenNull(){
        return mFullScreen == null;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    long baseTime = -1;

    public int getFPS() {
        return drawNum;
    }

    int drawNum = 0;

    @Override
    public void onDrawFrame(GL10 gl) {
        drawNum++;
        if (baseTime == -1){
            baseTime = System.nanoTime();
        }

        if (System.nanoTime() - baseTime >= 1000000000){
            baseTime = System.nanoTime();
            listener.onCurrentFps(drawNum);
            drawNum = 0;
        }
        if (surfaceTexture != null && mFullScreen != null){
            surfaceTexture.updateTexImage();
            surfaceTexture.getTransformMatrix(mTmpMatrix);

            GLES20.glViewport(0, 0, getWidth(), getHeight());
            mFullScreen.drawFrame(texutureID, mTmpMatrix);
        }
    }
    private final float[] mTmpMatrix = new float[16];

    private SurfaceTexture surfaceTexture;
    private int texutureID;
    public void onDrawFrame(SurfaceTexture st, int textureID){
        this.surfaceTexture = st;
        this.texutureID = textureID;
        requestRender();
    }

    @Override
    public void onPause(){
        super.onPause();

        if (mFullScreen != null){
            mFullScreen.release(false);
            mFullScreen = null;
        }
    }
}
