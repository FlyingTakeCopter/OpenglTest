package com.example.liuqikang.myapplication;

import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by liuqikang on 2019/7/9.
 */

public class AirHockeyRenderer implements GLSurfaceView.Renderer {
    private static final int POSITION_COMPONENT_COUNT = 2;

    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer vertexData;

    public AirHockeyRenderer(){
        float[] tableVertices = {
                0f, 0f,
                9f, 14f,
                0f, 14f,

                0f, 0f,
                9f, 0f,
                9f, 14f,

                0f, 7f,
                9f, 7f,

                4.5f, 2f,
                4.5f, 12f
        };

        // FloatBuffer用来在本地内存中存储数据，而不是在虚拟机汇总
        // 本地存储是为了方便本地系统库中的opengl进行访问
        // allocateDirect分配一块本地内存，这块内存不受虚拟机管控，不会被垃圾回收
        // order 告诉字节缓冲区(byte buffer)按照本地字节序(nativeOrder)组织它的内容
        //       本地字节序是指，当一个值占用多个字节时，比如32位整数，字节按照最重要位置到最不重要位或者相反
        //       这个排序并不重要，但是重要的是作为一个平台要使用同样的排序
        //       order(ByteOrder.nativeOrder())能保证这一点
        // asFloatBuffer() 我们不愿直接操作单独的字节，而是希望使用浮点数，
        //                 因此，调用asFloatBuffer()可以得到一个可以反映底层字节的FloatBuffer实例
        // 然后就可以使用put方法把数据从虚拟机内存复制到本地内存了。当进程结束的时候，这块内存会被释放
        // 但是随着程序运行产生了很多的ByteBuffer，需要堆碎片化以及内存管理的技术
        vertexData = ByteBuffer
                .allocateDirect(tableVertices.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(tableVertices);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }
}
