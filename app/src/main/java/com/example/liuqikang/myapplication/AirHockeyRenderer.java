package com.example.liuqikang.myapplication;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.example.liuqikang.myapplication.util.ShaderHelper;
import com.example.liuqikang.myapplication.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static android.opengl.GLUtils.*;
import static android.opengl.Matrix.*;
/**
 * Created by liuqikang on 2019/7/9.
 */

public class AirHockeyRenderer implements GLSurfaceView.Renderer {
    private static final int POSITION_COMPONENT_COUNT = 2;

    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer vertexData;

    private Context mContext;

    private int program;

    private static final String A_POSITION = "a_Position";
    private int a_position;

    private static final String U_COLOR = "u_Color";
    private int u_color;

    public AirHockeyRenderer(Context context){
        mContext = context;
        float[] tableVertices = {
                // 桌面
                0f, 0f,
                9f, 14f,
                0f, 14f,

                0f, 0f,
                9f, 0f,
                9f, 14f,
                // 横线
                0f, 7f,
                9f, 7f,
                // 木锥
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
        // 设置清空屏幕用的颜色
        glClearColor(0.0f, 0.0f, 0f, 0f);

        program = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(mContext, R.raw.simple_vertex_sharder),
                TextResourceReader.readTextFileFromResource(mContext, R.raw.simple_fragment_sharder));

        glUseProgram(program);

        a_position = glGetAttribLocation(program, A_POSITION);
        u_color = glGetUniformLocation(program, U_COLOR);

        vertexData.position(0);
        glVertexAttribPointer(a_position, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, 0, vertexData);
        glEnableVertexAttribArray(a_position);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0,0,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GLES20.GL_COLOR_BUFFER_BIT);

        glUniform4f(u_color, 1.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 0, 6);

        // 绘制线
        GLES20.glUniform4f(u_color, 1.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2);
        // 绘制点
        GLES20.glUniform4f(u_color, 0.0f, 0.0f, 1.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1);

        GLES20.glUniform4f(u_color, 1.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1);
    }
}
