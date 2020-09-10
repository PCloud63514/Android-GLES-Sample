package com.pcloud.glessample_kotlin.gles

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

open class MyShape {
    //변수 명 그대로 정점의 좌표(x, y, z)입니다. 읽는 순서는 기본 반시계방향이며 시계방향으로 변경할 수 있습니다.
    // OpenGLES의 좌표계 기본 값은 원점(0,0,0)이 GLSurfaceView의 중앙입니다.
    open val vertexCoords:FloatArray? = null
    open val COORDS_PER_VERTEX:Int? = 3

    private var vertexCount:Int? = null
    private var vertexStride:Int? = null

    private var vertexBuffer:FloatBuffer? = null
    //rgba 순의 Color 정보 입니다.
    private val color:FloatArray? = floatArrayOf(
        0.6f, 0.7f, 0.2f, 1.0f
    )
    //Shape의 정점을 랜더링 하기 위한 OpenGLES Graphics Code
    private val vertexShaderCode:String? =
        "attribute vec4 vPosition;" +
            "void main() {" +
                "gl_Position = vPosition;" +
            "}"
    //Shape의 Color 또는 Texture를 랜더링 하기 위한 OpenGLES Graphics Code
    private val fragmentShaderCode:String =
        "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "    gl_FragColor = vColor;" +
                "}"

    private var program:Int? = null

    fun loadShader(type:Int, shaderCode:String): Int {
        // 다음 2가지 타입 중 하나로 shader객체를 생성한다.
        // vertex shader type (GLES20.GL_VERTEX_SHADER)
        // 또는 fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        var shader:Int = GLES20.glCreateShader(type)
        // shader객체에 shader source code를 로드합니다.
        GLES20.glShaderSource(shader, shaderCode)
        //shader객체를 컴파일 합니다.
        GLES20.glCompileShader(shader)
        return shader
    }

    fun initialize() {
        vertexCount = vertexCoords!!.size / COORDS_PER_VERTEX!!
        vertexStride = COORDS_PER_VERTEX!! * 4

        vertexBuffer = arrayToBuffer(vertexCoords!!)
        //vertex Shader Type 의 객체를 생성하여 vertexShaderCode 를 로드 및 컴파일 합니다.
        var vertexShader:Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode!!)
        //fragment Shader Type 의 객체를 생성하여 fragmentShaderCode 를 로드 및 컴파일 합니다.
        var fragmentShader:Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode!!)

        //program 객체 생성
        program = GLES20.glCreateProgram()
        // vertex shader를 program 객체에 추가
        GLES20.glAttachShader(program!!, vertexShader)
        // fragment shader 를 program 객체에 추가
        GLES20.glAttachShader(program!!, fragmentShader)
        // program 객체를 OpenGL 에 연결한다. program 에 추가된 shader 또한 OpenGL 에 연결된다.
        GLES20.glLinkProgram(program!!)
    }



    //ByteBuffer를 생성 한 후에 FloatBuffer로 변환하여 vertex 좌표를 저장하는데, 이는 ByteBuffer를 이용하여 랜더링을 진행 할 시 성능개선이 이루어지기 때문입니다.
    fun arrayToBuffer(f:FloatArray): FloatBuffer {
        // ByteBuffer를 f의 크기 4배 만큼 할당 받습니다.
        var buf: ByteBuffer = ByteBuffer.allocateDirect(f.size * 4)
        //ByteBuffer에서 사용할 엔디안을 지정합니다. 하드웨어의 native byte order 사용.
        buf.order(ByteOrder.nativeOrder())
        //ByteBuffer를 FloatBuffer로 변환
        var fbuf:FloatBuffer = buf.asFloatBuffer()
        //f 에 저장된 좌표들을 fbuf에 저장합니다.
        fbuf.put(f)
        //읽어올 버퍼의 위치(커서)를 설정합니다.
        fbuf.position(0)

        return fbuf
    }

    fun draw() {
        GLES20.glUseProgram(program!!)
        // program 객체로부터 vertex shader의 vPosition 맴버 핸들을 가져옴
        var positionHandle = GLES20.glGetAttribLocation(program!!, "vPosition")
        // vertex 속성을 활성화 시킴 (랜더링 시 반영되어 그려질 것이다.)
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX!!,
                GLES20.GL_FLOAT, false, vertexStride!!, vertexBuffer)

        // program 객체로부터 fragment shader의 vColor 맴버 핸들을 가져옴
        var colorHandle = GLES20.glGetUniformLocation(program!!, "vColor")
        // 랜더링 시 사용할 색상으로 color 변수 값을 사용
        GLES20.glUniform4fv(colorHandle, 1, color, 0)
        //정점 갯수만큼 랜더링을 진행
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount!!)
        //vertex 속성을 비활성화 (필수)
        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    public fun destroy() {

    }
}