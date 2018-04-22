package com.example.liuqikang.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.liuqikang.myapplication.objects.ParticleShooter;
import com.example.liuqikang.myapplication.objects.ParticleSystem;
import com.example.liuqikang.myapplication.programs.ParticleShaderProgram;
import com.example.liuqikang.myapplication.util.Geometry;
import com.example.liuqikang.myapplication.util.MatrixHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by liuqikang on 2018/4/23.
 */

public class TestParticleRender implements GLSurfaceView.Renderer {
    private Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];

    private ParticleShaderProgram particleProgramm;
    private ParticleSystem particleSystem;
    private ParticleShooter redParticleShooter;
    private ParticleShooter greenParticleShooter;
    private ParticleShooter blueParticleShooter;
    private long globalStartTime;

    public TestParticleRender(Context cxt){
        context = cxt;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        particleProgramm = new ParticleShaderProgram(context);
        particleSystem = new ParticleSystem(10000);
        globalStartTime = System.nanoTime();

        final Geometry.Vector particleDirection = new Geometry.Vector(0f, 0.5f, 0f);

        redParticleShooter = new ParticleShooter(new Geometry.Point(-1f, 0f, 0f),
                particleDirection, Color.rgb(255, 50, 5));
        greenParticleShooter = new ParticleShooter(new Geometry.Point(0f, 0f, 0f),
                particleDirection, Color.rgb(25,255,25));
        blueParticleShooter = new ParticleShooter(new Geometry.Point(1f, 0f, 0f),
                particleDirection, Color.rgb(5, 50,255));
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0,0,width,height);

        MatrixHelper.perspectiveM(projectionMatrix, 45,
                (float)width / (float)height, 1f, 10f);

        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.translateM(viewMatrix, 0, 0f, -1.5f, -5f);
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;

        redParticleShooter.addParticles(particleSystem, currentTime, 5);
        greenParticleShooter.addParticles(particleSystem, currentTime, 5);
        blueParticleShooter.addParticles(particleSystem, currentTime, 5);

        particleProgramm.useProgram();
        particleProgramm.setUniform(viewProjectionMatrix, currentTime);
        particleSystem.bindData(particleProgramm);
        particleSystem.draw();
    }
}
