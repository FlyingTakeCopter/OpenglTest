package com.example.liuqikang.myapplication.objects;


import android.graphics.Color;
import android.opengl.GLES20;

import com.example.liuqikang.myapplication.Constants;
import com.example.liuqikang.myapplication.data.VertexArray;
import com.example.liuqikang.myapplication.programs.ParticleShaderProgram;
import com.example.liuqikang.myapplication.util.Geometry;


/**
 * Created by liuqikang on 2018/4/22.
 */

public class ParticleSystem {
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int VECTOR_COMPONENT_COUNT = 3;
    private static final int PARTICLE_START_TIME_COMPONENT_COUNT = 1;

    private static final int TOTAL_COMPONENT_COUNT =
            POSITION_COMPONENT_COUNT
            + COLOR_COMPONENT_COUNT
            + VECTOR_COMPONENT_COUNT
            + PARTICLE_START_TIME_COMPONENT_COUNT;

    private static final int STRIDE = TOTAL_COMPONENT_COUNT * Constants.BYTES_PER_FLOAT;

    private final float[] particles;
    private final VertexArray vertexArray;
    private final int maxParticleCount;

    private int currentParticleCount;
    private int nextParticle;

    public ParticleSystem(int maxParticleCount){
        particles = new float[maxParticleCount * TOTAL_COMPONENT_COUNT];
        vertexArray = new VertexArray(particles);
        this.maxParticleCount = maxParticleCount;
    }

    // 创建新粒子
    public void addParticle(Geometry.Point position, int color, Geometry.Vector direction,
                            float particleStartTime){
        // 计算当前粒子存储位置
        // 计算偏移量
        final int particleOffset = nextParticle * TOTAL_COMPONENT_COUNT;

        int currentOffset = particleOffset;
        nextParticle++;

        if (currentParticleCount < maxParticleCount){
            currentParticleCount++;
        }
        // 结尾处从0回收最旧的粒子
        if (nextParticle == maxParticleCount){
            nextParticle = 0;
        }

        // 开始赋值
        particles[currentOffset++] = position.x;
        particles[currentOffset++] = position.y;
        particles[currentOffset++] = position.z;

        particles[currentOffset++] = Color.red(color) / 255f;
        particles[currentOffset++] = Color.green(color) / 255f;
        particles[currentOffset++] = Color.blue(color) / 255f;

        particles[currentOffset++] = direction.x;
        particles[currentOffset++] = direction.y;
        particles[currentOffset++] = direction.z;

        particles[currentOffset++] = particleStartTime;

        vertexArray.updateBuffer(particles, particleOffset, TOTAL_COMPONENT_COUNT);

    }

    public void bindData(ParticleShaderProgram particleShaderProgram){
        int dataOffset = 0;
        vertexArray.setVertexAttributePointer(dataOffset,
                particleShaderProgram.getPositionLocation(),
                POSITION_COMPONENT_COUNT, STRIDE);
        dataOffset += POSITION_COMPONENT_COUNT;
        vertexArray.setVertexAttributePointer(dataOffset,
                particleShaderProgram.getColorLocation(),
                COLOR_COMPONENT_COUNT, STRIDE);
        dataOffset += COLOR_COMPONENT_COUNT;
        vertexArray.setVertexAttributePointer(dataOffset,
                particleShaderProgram.getDirectionVectorLocation(),
                VECTOR_COMPONENT_COUNT, STRIDE);
        dataOffset += VECTOR_COMPONENT_COUNT;
        vertexArray.setVertexAttributePointer(dataOffset,
                particleShaderProgram.getParticleStartTimeLocation(),
                PARTICLE_START_TIME_COMPONENT_COUNT, STRIDE);
    }

    public void draw(){
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, currentParticleCount);
    }
}
