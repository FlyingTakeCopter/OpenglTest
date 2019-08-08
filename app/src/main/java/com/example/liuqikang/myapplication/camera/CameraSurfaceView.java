package com.example.liuqikang.myapplication.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.liuqikang.myapplication.gles.EglCore;
import com.example.liuqikang.myapplication.gles.FullFrameRect;
import com.example.liuqikang.myapplication.gles.Texture2dProgram;
import com.example.liuqikang.myapplication.gles.WindowSurface;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private EglCore eglCore;
    private FullFrameRect fullFrameBlit;
    private WindowSurface displaySurface;

    public void setListener(surfaceCreateListener listener) {
        this.listener = listener;
    }

    private surfaceCreateListener listener;

    public CameraSurfaceView(Context context) {
        super(context);
        init();
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        getHolder().addCallback(this);
    }

    public void init(surfaceCreateListener listener){
        this.listener = listener;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 创建EGL
        eglCore = new EglCore(null, EglCore.FLAG_RECORDABLE);
        // 创建 EGL window 绑定要绘制的surface
        displaySurface = new WindowSurface(eglCore, holder.getSurface(), false);
        displaySurface.makeCurrent();

        // 初始化OpenglEs环境
        fullFrameBlit = new FullFrameRect(new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_EXT));
        // 获取GLES的TextureID
        this.listener.surCreated(fullFrameBlit.createTextureObject());
    }

    interface surfaceCreateListener{
        public void surCreated(int textureID);

        public void onCurrentFps(int fps);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void onPause(){
        if (displaySurface != null) {
            displaySurface.release();
            displaySurface = null;
        }
        if (fullFrameBlit != null) {
            fullFrameBlit.release(false);
            fullFrameBlit = null;
        }
        if (eglCore != null) {
            eglCore.release();
            eglCore = null;
        }
    }

    private final float[] mTmpMatrix = new float[16];

    long baseTime = -1;

    public int getFPS() {
        return drawNum;
    }

    int drawNum = 0;

    public void drawFrame(SurfaceTexture cameraTexture, int textureID){
        if (eglCore == null){
            return;
        }

        drawNum++;
        if (baseTime == -1){
            baseTime = System.nanoTime();
        }

        if (System.nanoTime() - baseTime >= 1000000000){
            baseTime = System.nanoTime();
            listener.onCurrentFps(drawNum);
            drawNum = 0;
        }

        displaySurface.makeCurrent();
        cameraTexture.updateTexImage();
        cameraTexture.getTransformMatrix(mTmpMatrix);

        GLES20.glViewport(0, 0, getWidth(), getHeight());
        fullFrameBlit.drawFrame(textureID, mTmpMatrix);
        displaySurface.swapBuffers();
    }

    public boolean isEglNull(){
        return eglCore == null;
    }
}
