package com.example.liuqikang.myapplication.data;

import android.opengl.GLES20;

import com.example.liuqikang.myapplication.Constants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Administrator on 2018/4/22.
 */

public class VertexArray {
    private final FloatBuffer floatBuffer;

    public VertexArray(float[] vertexData){
        floatBuffer = ByteBuffer.allocateDirect(vertexData.length * Constants.BYTES_PER_FLOAT)// 申请空间
                .order(ByteOrder.nativeOrder()) // 设置排序方式
                .asFloatBuffer()    // ByteBuffer转化为FloatBuffer,不直接操作单独字节，转化为浮点数
                .put(vertexData);   // 输入数据
    }

    public void setVertexAttributePointer(int dataOffset, int attributeLocation,
                                          int componentCount, int stride){
        floatBuffer.position(dataOffset);
        GLES20.glVertexAttribPointer(attributeLocation, componentCount, GLES20.GL_FLOAT,
                false, stride, floatBuffer);// 关联顶点数组
        GLES20.glEnableVertexAttribArray(attributeLocation);// 启用
        floatBuffer.position(0);// 归位
    }

}
