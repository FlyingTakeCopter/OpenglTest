第一章 GLSurfaceView
GLSurfaceView会处理Opengl初始化过程中比较基本的操作，
如配置显示设备以及后台线程中渲染;渲染是指在显示设备中一个称谓surface的特定区域完成的，也成视口
GLSurfaceView绑定activity生命周期，包括OpenGL的创建、销毁、暂停、resume等辅助方法

在幕后，GLSurfaceView是加上为它自己创建了一个窗口window，并且在试图层次（View Hierarchy）上穿了个洞，
让底层的Opengl surface显示出来，对于大多数情况，这就足够了，
但是，GLSurfaceView与常规的View不同，他没有动画或者变形特效，因为GLSurfaceView是窗口的一部分

从4.0开始，android提供了一个纹理试图TextureView，它可以渲染Opengl而不用创建单独的窗口或打洞了，
这就意味着这个视图像一个常规窗口一样，可以被操作，且有动画和变形特效。但是TextureView没有内置Opengl初始化操作
想要使用TextureView，一种方法是执行自定义的Opengl初始化，并在TextureView上运行，
另外一种方法是把GLSurfaceView的源代码拿出来，把它适配到TextureView上

第二章 opengl 基础知识
Android屏幕原点在左上角

opengl只能绘制点、直线、三角形，三角形是最基本的图像
当我们定义三角形的时候总是以逆时针的顺序排列到顶点，这成为卷曲顺序，可以优化性能
使用卷曲顺序可以指出一个三角形属于任何指定物体的前面或者后面，opengl可以忽略哪些无论如何都无法被看到的后面的三角形

Android是运行在虚拟机上的程序，所以不必关心特定的CPU或者机器架构，也不必关心底层的内存管理
这通常都能做的很好，除非要与本地系统交互，比如opengl
opengl作为本地系统库直接运行在硬件上，没有虚拟机，也没有垃圾回收和内存管理

那么Java如何与本地的opengl进行通信呢？两种技术
第一种，JNI 使用JNI调用本地系统库
第二种，改变内存分配的方式，
Java有一个特殊的类集合(FloatBuffer)，他们可以分配本地的内存块，并且把JAVA的数据复制到本地内存
本地内存可以被本地环境读取，而不受垃圾回收器的管控

FloatBuffer vertexData = ByteBuffer
                .allocateDirect(tableVertices.length * BYTES_PER_FLOAT) 分配一块本地内存，非虚拟机内存，不会被虚拟机管理
                .order(ByteOrder.nativeOrder())                         设置字节序按照本地字节序，保证同一个平台下的字节序一样
                .asFloatBuffer();                                       作为浮点数使用
        vertexData.put(tableVertices);

FloatBuffer用来在本地内存中存储数据，而不是在虚拟机中
本地存储是为了方便本地系统库中的opengl进行访问
allocateDirect分配一块本地内存，这块内存不受虚拟机管控，不会被垃圾回收
order 告诉字节缓冲区(byte buffer)按照本地字节序(nativeOrder)组织它的内容
      本地字节序是指，当一个值占用多个字节时，比如32位整数，字节按照最重要位置到最不重要位或者相反
      这个排序并不重要，但是重要的是作为一个平台要使用同样的排序
      order(ByteOrder.nativeOrder())能保证这一点
asFloatBuffer() 我们不愿直接操作单独的字节，而是希望使用浮点数，
                因此，调用asFloatBuffer()可以得到一个可以反映底层字节的FloatBuffer实例
然后就可以使用put方法把数据从虚拟机内存复制到本地内存了。当进程结束的时候，这块内存会被释放
但是随着程序运行产生了很多的ByteBuffer，需要堆碎片化以及内存管理的技术

字节序：字节序是描述一个硬件架构是如何组织位(bit)和字节(byte)的方式，他们在底层组成一个数字
大头架构
小头架构

顶点着色器 vertex shader 生成每个顶点的最终位置，针对每个顶点，他都会执行一次，
一旦最终位置确定了，opengl就可以把这些可见的顶点的集合组装成点、直线以及三角形

片元着色器 fragment shader 为组成点、直线或者三角形的每个片段生成最终的颜色，针对每一个片段，它都会执行一次
一个片段是一个小的、单一颜色的长方形区域，类似于计算机屏幕上的一个像素

一旦最后颜色生成了，opengl就会把他们写到一块成为帧缓冲区(frame buffer)的内存块中
然后，android会把这个帧缓冲区显示到屏幕上

opengl管道描述
读取顶点数据
-》执行顶点着色器   (vertex shader)
-》组装图元
-》光栅化图元
-》执行片段着色器   (fragment shader)
-》写入帧缓冲区
-》显示在屏幕上

最简单的顶点着色器
每一个定义过得单一顶点，顶点着色器都会被调用一次
attribute vec4 a_Position;
void main(){
    gl_Position = a_Position;
}
a_Position:接受当前顶点位置
vec4:包含4个分量 x,y,z,w 默认情况下为 0,0,0,1
attribute:关键字，用来传值

片元着色器
precision mediump float;
uniform vec4 u_Color;
void main()
{
    gl_FragColor = u_Color;
}
precision: 精度限定符，可以选择lowp,mediump和highp
为什么顶点着色器没有定义精度呢？
顶点着色器同样可以改变默认精度，但是，对于一个顶点的位置而言，精度是最重要的，
Opengl的设计者决定把顶点着色器的精度默认设置成最高级别---highp
高精度数据类型更加精确，但是这是以降低性能为代价的；
对于片元着色器，出于最大的兼容性考虑，选择了mediump，这也是基于速度与质量的权衡

uniform:传递值
gl_GragColor:当前片/像素最终显示的颜色

第三章 opengl绘图
加载着色器
// 读取文本
public static String readTextFileFromResource(context, id){
    StringBuilder body = new StringBuilder();
    try{
        InputStream is = context.getResource().openRawResource(id);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        while((line = br.readLine()) != null){
            body.append(line);
            body.append(''\n');
        }
    }catch(e){

    }
    return body.toString();
}
// 着色器编译
private static int compileShader(int type, String shaderCode){
    // 创建着色器对象，获取创建的着色器对象编号
    final int shaderObjectId = glCreateShader(type);
    if(shaderObjectId == 0){创建失败，通过glGetError()获取错误信息
        return;
    }
    // 将着色器代码上传到创建好的着色器对象中
    glShaderSource(shaderObjectId, shaderCode);
    // 编译创建好的着色器对象
    glCompileShader(shaderObjectId);
    // 取出编译状态
    final int[] compileStatus = new int[1];
    // 取出shaderObjectId着色器对象的编译状态，并将结果写入compileStatus的第0个元素
    glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);
    // 取出着色器编译信息日志
    glGetShaderInfoLog(shaderObjectId);
    // 验证着色器对象的编译状态并返回着色器对象ID
    if(compileStatus[0] == 0){
        // 编译失败
        glDeleteShader(shaderObjectId);
        return 0;
    }
    // 编译成功返回着色器对象编号ID
    return shaderObjectId;
}

GL_VERTEX_SHADER
GL_FRAGMENT_SHADER

将着色器链接进opengl程序program
opengl程序
一个opengl程序就是把一个顶点着色器和一个片元着色器链接在一起变成单个对象
顶点着色器与片着色器总是在一起工作的，不能单独存在，但也并不是必须要一对一配对
// shader链接到program
public static int linkProgram(int vertexShaderId, int fragemtnShaderId){
    // 创建opengl program
    final int programObjectId = glCreateProgram();
    // 判断是否创建成功
    if(programObjectId == 0){
        return 0;
    }
    // 绑定着色器Id到opengl program
    glAttachShader(programObjectId, vertexShaderId);
    glAttachShader(programObjectId, fragmentShaderId);
    // 链接程序
    glLinkProgram(programObjectId);
    // 获取链接结果
    final int[] linkStatus = new int[1];
    glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatu, 0);
    // 验证链接状态
    if(linkStatus[0] == 0){
        glDeleteProgram(programObjectId);
        return 0;
    }
    // 返回opengl程序ID
    return programObjectId;
}

// 在GlSurfaceView.onSurfaceCreated中
// 绑定opengl program 告诉Opengl在绘制任何东西到屏幕上都要使用这个opengl program
glUseProgram(programID)
// 绑定uniform
private static final String U_COLOR = "u_color";
private int uColorLocation;
uColorLocation = glGetUniformLocation(programID, U_COLOR);
// 绑定attribute
private static final String A_POSITION = "a_position";
private int aPositionLocation;
aPositionLocation = glGetAttribLocation(programID, A_POSITION);

// 关联attribute与顶点数据的数组
// FloatBuffer 本地内存块中存储的顶点信息
vertexData.position(0);// 保证从头读取
glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
// 启用attribute
glEnableVertexAttribArray(aPositionLocation)

glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, Buffer ptr)
int index:属性ID
int size:标识每个属性数据的计数，代表多少个分量标识一个顶点信息 如果一个坐标用(x,y)表示这个值设置为2，(x,y,z)设置为3
         注意，在vertex shader中我们定义顶点a_Position为vec4，在着色器中他有4个分量，
         但实际上我们只传进去了两个有效值(x,y)，如果分量没有被指定值，默认情况下，opengl会把前三个分量设置为0，最后一个设置为1
int type:标识分量的数据类型
boolean normalized:只有使用整形数据的时候，这个参数才有意义
int stride:只有当一个数组存储多于一个属性时，他才有意义
Buffer ptr:告诉opengl去哪里读取数据，
           如果我们没有调用vertexData.position(0)，它可能尝试读取缓冲区结尾后面的内容，并使我们的应用程序崩溃

在onDrawFrame中
// 绘制桌子
// 更新fragment shader中的u_color值
// 注意uniform分量没有默认值，如果定位为vec4，需要提供4个值，否则会崩溃
glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f)
// 第一个参数告诉opengl绘制三角形
// 第二个参数告诉opengl从顶点数组的开头处开始读顶点
// 第三个参数告诉opengl读入6个顶点
glDrawArrays(GL_TRIANGLES, 0, 6);

// 绘制分割线
glUniform4f(u_ColorLocation, 1.0f, 0f, 0f, 1.0f)
glDrawArrays(GL_LINES, 6, 2);

// 绘制木锥
glUniform4f(u_ColorLocation, 0f, 0f, 1.0f, 1.0f);
glDrawArrays(GL_POINTS, 8, 1);
glUniform4f(u_ColorLocation, 1.0f, 0f, 0f, 1.0f)
glDrawArrays(GL_POINTS, 9, 1);

第四章 颜色和着色 varying STRIDE跨距
GL_TRIANGLES    三角形
GL_TRIANGLE_FAN 三角扇形

使用android的Color类转换颜色
opengl中只能识别 0到1的颜色分量值
所以
float red = Color.red(Color.RED) / 255f;
float blue = Color.red(Color.BLUE) / 255f;
float green = Color.red(Color.GREEN) / 255f;

从fragment shader中去掉uniform定义的颜色，用另外一个属性替换它
为vertex shader增加
attribute vec4 a_Color

varying vec4 v_Color;

void main()
{
    v_Color = a_Color;
}
varying是一种特殊的变量类型，它把给他的那些值进行混合，并且把这些混合后的值发送给fragment shader
比如顶点0的a_Color是红色，顶点1的a_Color是绿色
那么混合后的结果就是，距离顶点0越近的点越红，距离顶点1越近的点越绿，中间颜色过度

fragment shader去掉uniform， 增加
varying vec4 v_Color;
void main()
{
    gl_FragColor = v_Color;
}

varying可以混合任何值，这种混合是使用线性插值实现的(每种颜色的变化随着所在位置距离两端的距离比)
blended_value = (vertex_0_value) * (100% - distance_ratio) + (vertex_1_value * distance_ratio)

三角形颜色过度线性插值
对于三角形内的任意给定点，从那个点向每个顶点画一条直线就可以生成3个内部三角形
这三个内部三角形的面积比例决定了那个点上眉中颜色的权重
blended_value = (vertex_0_value * vertex_0_weight) +
                (vertex_1_value * vertex_1_weight) +
                (vertex_2_value * (100% - vertex_0_weight - vertex_1_weight))

STRIDE分量的意义:如果在同一个数据数组中，既有位置又有颜色，opengl不能再假定每一个分量的下一个位置还是一个同样意义的分量
                 所以需要跳过颜色分量，opengl需要知道每个分量之间的"跨距"才能知道每个位置分量之间要跳过多少个字节

第五章 Martix
单位矩阵：
1   0   0   0
0   1   0   0
0   0   1   0
0   0   0   1
单位矩阵乘以任何向量总是得到与原来相同的向量
1   0   0   0       1       1*1 + 0*2 + 0*3 + 0*4       1
0   1   0   0   *   2   =   0*1 + 1*2 + 0*3 + 0*4   =   2
0   0   1   0       3       0*1 + 0*2 + 1*3 + 0*4       3
0   0   0   1       4       0*1 + 0*2 + 0*3 + 1*4       4

平移矩阵
1   0   0   X_translation
0   1   0   Y_translation
0   0   1   Z_translation
0   0   0   1
(2,2) X平移3，Y平移3
1   0   0   3       2       1*2 + 0*2 + 0*0 + 3*1       5
0   1   0   3   *   2   =   0*2 + 1*2 + 0*0 + 3*1   =   5
0   0   1   0       0       0*2 + 0*2 + 1*0 + 0*1       0
0   0   0   1       1       0*2 + 0*2 + 0*0 + 1*1       1

正交投影    android.opengl.Matrix   orthoM()
orthoM(float[] m, int mOffset, float left, float right, float bottom, float top, float near, float far)
float[] m:目标数组，这个数组的长度至少有16个元素，这样才能存储正交投影矩阵
int mOffet: 结果矩阵起始的偏移值
float left: x min
float right: x max
float bottom: y min
float top: y max
float near: z min
float near: z max

2/(right - left)            0                   0               (right + left) / (right - left)
        0           2 / (top - bottom)          0               (top + bottom) / (top - bottom)
        0                   0               2 / (far - near)    (far + near) / (far - near)
        0                   0                   0                           1

左手坐标与右手坐标
opengl使用右手坐标    远处z为负，近处z为正
gl_Position = u_Matrix * a_Postion
顶点数组a_Position不必被翻译为归一化设备坐标，而是理解为存在这个矩阵所定义的虚拟空间坐标中
这个矩阵会把坐标从虚拟坐标空间变换回归一化设备坐标

创建正交投影
final float aspectRatio = width > height ? (float)width / (float)height : (float)height/ (float)width
if(width > height){
    Matrix.orthoM(projectionMatrix, 0, -aspectRation, aspecRation, -1f, 1f, -1f, 1f);
}else{
    Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -aspectRation, aspecRation,-1f, 1f);
}

第六章 3D
6.1 从着色器到屏幕坐标的变换：
gl_Position          归一化设备坐标           窗口坐标
————————————》透视除法——————————————》视口变换————————————》

6.2.1 剪裁空间：
x   坐标
y   坐标
z   坐标
w   剪裁空间:对于任何给定位置，它的x、y、z分量都需要在那个位置的-w ~ +w之间。
比如一个位置的w是1，那么其x、y、z分量都要在-1 ~ +1之间
任何在剪裁空间之外的事物在屏幕上都是不可见的

6.2.2 透视除法：
在顶点位置成为一个归一化坐标之前，opengl实际上执行了一个透视除法的步骤。
透视除法之后保证每一个可是坐标取值范围都在 [-1，+1] 之间
为了在屏幕上创建三维的幻象，OpenGL会把每个gl_Position的x、y、z分量都除以它的分量w
当w分量用来表示距离的时候，就使得较远的物体被移动到距离渲染区域中心更近的地方，这个中心的作用就像是一个消失点
想象一副公路在远处越来越窄的画面，远处看不到的公路尽头，在画面中心
假设一个物体，他有两个顶点，每个顶点都在三维空间中的同一个位置，他们有同样的x,y,z，但w分量不同
          （1,1,1,1）       （1,1,1,2）
透视除法  （1/1,1/1,1/1）   （1/2,1/2,1/2）
            (1,1,1)        (0.5,0.5,0.5)
              近                 远
w值越大的坐标被移动到距离消失点(0,0,0)更近的位置

同质化坐标：因为透视除法，剪裁空间中的坐标经常被称为同质化坐标。
被称为同质化的原因是因为剪裁空间中的几个坐标可以映射到同一个点，例如:
(1,1,1,1)   (2,2,2,2)   (3,3,3,3)   (4,4,4,4)   (5,5,5,5)
透视除法后，这些所有点都映射到归一化设备坐标中的(1,1,1)

6.2.3 视口变换
这些被映射的坐标称为窗口坐标
onSurfaceChanged()中的glViewport()用来给OpenGL设置视口

6.4 透视投影
视椎体:是一个立方体，远端比近端大，从而使其变成一个被截断的金字塔。两端的大小差别越大，观察的范围越宽，我们能看到的也越多
视椎体焦点:顺着从视椎体较大端向较小端扩展出来的那些直线，一直向前通过较小端直到他们汇聚到一起。
当使用透视投影观察一个场景的时候，那个场景看上去就像是你的头被放在了焦点处。
焦点和视椎体小端的距离被称为焦距，焦距影响视椎体小端和大端的比例，以及其对应的视野

6.5 定义透视投影
对宽高比和视野进行调整
一个通用的投影矩阵，它允许我们调整视野以及屏幕的宽高比:
a/aspect            0               0                   0
    0               a               0                   0
    0               0      -(f + n) / (f - n)      2fn / (f - n)
    0               0               -1                  0
a: 想象一个相机拍摄的场景，这个变量就代表那个相机的焦距。焦距是由 1 / tan( 视野角度 / 2 ) 计算得到的，视野角度<180
aspect: 屏幕宽高比，宽度 / 高度
f: 到远处平面的距离，必须是正数且大于到最近处平面的距离
n: 到近处平面的距离，必须是正数。如果此值被设为1，那么近处平面就位于一个z值为-1处

随着视野变小，焦距边长，可以映射到归一化坐标中[-1,1]范围内的x和y值得范围就越小。这会产生使视椎体变窄的效果

6.7 开始使用投影矩阵
// 创建45度透视投影，这个视椎体从Z值为-1位置开始到z值为-10位置结束
MatrixHelper.perspectiveM(projectionMatrix, 45,
        (float)width / (float)height, 1f, 10f);
// 由于没有设置Z值所以Z默认为0，但是当前视椎体从-1开始，
// 由于物体不在视椎体中，所以无法显示

// 创建平移矩阵
Matrix.setIdentityM(modelMatrix, 0);    // 初始化单位矩阵
Matrix.translateM(modelMatrix, 0, 0f, 0f, -2f); // 沿Z轴平移-2

相乘一次还是相乘两次
两次：为了让每个顶点都移动到45度投影矩阵中，我们要先将平移矩阵应用于每个顶点，在将45度投影矩阵应用于每个顶点
一次：更简单方法是，我们让 45度投影矩阵 与 平移矩阵 相乘，得到新的矩阵再应用到顶点着色器。通过这种方式我们就可以在着色器中仅保留一个矩阵

矩阵乘法顺序，顺序不同导致结果不同
选择适当的顺序，
只使用投影矩阵: vertex_clip = ProjectionMatrix * vertex_eye(场景中的顶点在与投影矩阵相乘之前的位置)
加入平移矩阵:  vertex_eye = ModelMatrix * vertex_model(顶点在被模型矩阵放进场景中之前的位置)
             vertex_clip = ProjectionMatrix * vertex_eye

最终:         vertex_clip = ProjectionMatrix * ModelMatrix * vertex_model

6.8 旋转矩阵
以X为轴的旋转矩阵:
1           0           0           0
0         cos(a)      -sin(a)       0
0         sin(a)      cos(a)        0
0           0           0           1
以Y为轴:
cos(a)      0         sin(a)        0
0           1           0           0
-sin(a)     0         cos(a)        0
0           0           0           1
以Z为轴:
cos(a)    -sin(a)       0           0
sin(a)    cos(a)        0           0
0           0           1           0
0           0           0           1

举例 (0,1,0)以X轴旋转90度:
1       0       0       0       0       0
0    cos(90)  -sin(90)  0   *   1   =   0
0    sin(90)  cos(90)   0       0       1
0       0       0       1       1       1

(0,1,0)以X轴旋转90度结果为(0,0,1)

7.1 理解纹理
每个二维的纹理都有其自己的坐标空间
(0,1)               (1,1)


  T      Texture



(0,0)       S       (1,0)

大多数计算机图像都有一个默认的方向
        |
        |
        |
      Y |
        |
        |
        V   ————————————>
                 x
在标准opengl es 20中，纹理不必是正方形，但是每个维度都应该是2的幂，如128,256,512

X,Y坐标是从-1到+1，原点在正中心
S,T坐标是从（0,0）到（1,1）并且原点在左下角
private static final float[] VERTEX_DATA = {
        // X, Y, S, T
        0f,     0f,     0.5f,   0.5f,
        -0.5f,  -0.8f,  0f,     0.9f,
        0.5f,   -0.8f,  1f,     0.9f,
        0.5f,   0.8f,   1f,     0.1f,
        -0.5f,  0.8f,   0f,     0.1f,
        -0.5f,  -0.8f,  0f,     0.9f
};

裁剪纹理：
使用0.1f和0.9f作为T坐标？
桌子是1单位宽、1.6个单位高，而纹理图像是512 * 1024，因此，如果它的宽度对应1个单位，那纹理实际高度为2
1:1.6和1:2不匹配，为了避免将纹理压扁，使用0.1和0.9裁剪原图的边缘，只画正中间部分
如何依旧使用0和1作为坐标?
纹理预拉伸

10 粒子(particles)
































































