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

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mFullScreen = new FullFrameRect(
                new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_EXT));
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {
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

    public void onPause(){
        if (mFullScreen != null){
            mFullScreen.release(false);
            mFullScreen = null;
        }
    }
}
