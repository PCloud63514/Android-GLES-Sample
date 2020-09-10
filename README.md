# Android GLES2.0 Sample

Android GLES2.0 기본예제가 작성된 학습 프로젝트

언어는 Kotlin을 기반으로 작성하였습니다. 



몇년 전 학습한 gles1.0 코딩 기억과 여러 블로그를 참고하여 작성한 학습용 예제입니다.

순서대로 구현만 하더라도 기본적인 도형이나 텍스쳐 까지는 쉽게 가능합니다.



작성된 예제 문서는 Kotlin을 기반으로 하였습니다. 



## Index

1. [기본 개념](#기본-개념)
2. [OpenGLES 환경 구성](#opengles-환경-구성)
   1. [Manifest](#manifest)
   2. [GLSurfaceView](#glsurfaceview)
   3. [GLSurfaceView.Renderer](#glsurfaceview.renderer)
   4. [Shape](#shape)

### 기본 개념

---

Open GL(Graphic Library)는 고성능 2D 3D 그래픽스 표준 API를 지원하는 Library입니다.

여기서 OpenGL ES는 삽입 기기용 OpenGL 사양의 특성이며, Android에서는 1.0~3... 의 다양한 버전의 OpenGL ES API를 적용할 수 있습니다.



- OpenGL ES1.0 ~ 1.1: Android 1.0 이상에서 지원
- OpenGL ES2.0: Android 2.2(API Level 8) 이상에서 지원
- OpenGL ES3.0: Android 4.3(API Level 18) 이상에서 지원
- OpenGL ES3.1: Android 5.0(API Level 21) 이상에서 지원



[*] 본 글에서는 OpenGLES 2.0을 기준으로 작성합니다.

참고문헌

[OpenGL ES | Android Developer](https://developer.android.com/guide/topics/graphics/opengl?hl=ko)



### OpenGLES 환경 구성

---

Android에서 OpenGLES를 이용해 그래픽을 구성하기 위한 준비단계 입니다.

기초적으로 알아야할 클래스는 GLSurfaceView와 GLSurfaceView.Renderer 이며 기본 사용법에 대해 설명할 것입니다.



#### Manifest

---

Application에서 OpenGLES 2.0 API를 사용한 다는 것을 명시하기 위해선 Manifest에 다음 내용을 추가해야합니다. 

```
<uses-feature android:glEsVersion="0x00020000" android:required="true"/>
```

이는 OpenGLES의 특정 버전을 사용한다는 내용으로 써 추가하지 않는다 해서 GLES가 작동하지 않는 것은 아닙니다. 

단, 이 내용을 추가하면 Google Play에서 OpenGL ES2.0을 지원하지 않는 기기에 Applicationd를 설치하지 못하도록 제한합니다.

마찬가지로 3.0이나 3.1을 사용할 것을 명시하고 싶은 경우 glEsVersion을 0x00030000 또는 0x00030001 을 작성하면 됩니다.



[*] OpenGLES 3.x API는 2.0 API와 역호환이 가능하여 더욱 폭이 넓고 유연하게 그래픽을 구현할 수 있습니다. 단 2.0에서 1.1은 지원하지 않습니다.(문제가 발생하므로 사용하지 않는 편이 가장 좋습니다.)



만약 Application에서 Texture 압축을 사용할 경우 호환 가능 기기만 설치할 수 있도록  압축형식과 함께 명시할 수 있습니다.

```
 <supports-gl-texture android:name="GL_OES_compressed_ETC1_RGB8_texture" />
 <supports-gl-texture android:name="GL_OES_compressed_paletted_texture" />
```



#### GLSurfaceView

---

GLSurfaceView Class는 OpenGL API호출을 사용하여 그래픽을 보여줄 View입니다.

기본적으로 [Renderer](#glsurfaceview.renderer)를 추가하여야 하며, Touch Event관련 기능을 여기서 추가할 수 있습니다.

##### **EGL Config**

그래픽을 표현할 채널 수와 각 채널에 할당된 bit 정보가 작성되어있습니다. 
별도의 설정이 없다면 GLSurfaceView는 최소 16bit depth buffer와 PixelFormat.RGB888 정보가 작성된 EGL Config 를 사용합니다.

##### **구현**

```kotlin
import android.opengl.GLSurfaceView;
class MyGLView(context: Context): GLSurfaceView(context) {
    private var myGLRenderer:MyGLRenderer?
    init {
        setEGLContextClientVersion(2)
        myGLRenderer = MyGLRenderer()
        setRenderer(myGLRenderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }
}
```



##### **[필수]**

| In Code                                        | 설명                                                         |
| :--------------------------------------------- | ------------------------------------------------------------ |
| setEGLContextClientVersion(Int)                | EGLContext Client Version을 지정합니다.<br />내부 동작에서 EGLContextFactory 및 EGLConfigChooser에 버전을 알리고 각 인스턴스를 생성하기 때문에 단순히 Version을 명시하는 메소드는 아닙니다. |
| setRenderer(glRenderer:GLSurfaceView.Renderer) | 현재 GLSurfaceView의 Renderer를 설정합니다.                  |



##### **[선택사항]**

| In Code                                                      | 설명                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| preserveEGLContextOnPause = Boolean                          | Rendering Thread를 일시 중지하고 EGLContext 값을 축소합니다. |
| setEGLConfigChooser(needDepth:Boolean or configChooser:GLSurfaceView.EGLConfigChooser) | EGLConfig 을 선택하는 메서드 입니다. 보통 직접 호출할 일이 없고, 위에 EGLConfig 설명에 작성된 정보 외의 설정이 필요할 경우 지정하면 됩니다. |
| holder.setFormat(PixelFormat.TRANSLUCENT or PixelFormat.TRANSPARENT) | GLSurfaceView의 구성 표면을 가져오는 대상을 설정할 수 있습니다.<br />**PixelFormat.TRANSLUCENT**<br />- GLSurfaceView의 배경이 투명해야할 경우 설정할 수 있습니다.<br />**PixelFormat.TRANSPARENT**<br />- 별도의 설정할 필요가 없습니다. |
| rederMode = RENDERMODE_CONTINUOUSLY or RENDERMODE_WHEN_DIRTY | Rendering Mode를 설정합니다. <br />**RENDERMODE_CONTINUOUSLY**<br />- 반복적으로 랜더링을 진행합니다. 즉 draw() 메서드가 계속 호출되어 비동기적으로 변화해야하는 대상에 적합합니다.<br />**RENDERMODE_WHEN_DIRTY**<br />- View의 업데이트가 필요 없을 때 적합한 Mode입니다. GPU와 CPU의 지속적인 연산이 없으므로 배터리 및 시스템 성능 향상을 확인할 수 있습니다. <br />만약 RENDERMODE_WHEN_DIRTY 상태에서 View를 업데이트 하려는 경우 requestRender() 메서드를 호출하면 됩니다. |
| isFocusableInTouchMode = Boolean                             | GLSurfaceView가 Touch Event를 발생시킬 수 있습니다.          |
| requestRender()                                              | GLSurfaceVIew를 Update합니다.                                |

[참고문헌] [GLSurfaceView | Android Developer](https://developer.android.com/reference/android/opengl/GLSurfaceView)



#### GLSurfaceView.Renderer

---

GLSurfaceView.Renderer Interface는 GLSurfaceView에 그래픽을 그릴 때 필요한 메서드를 정의합니다.

Rendering 성능이 UI Thread에서 분리되도록 Rendering이 별도의 Thread에서 동작합니다. 

이로인해 입력 이벤트의 수신 위치는 UI Thread이므로 구현한 Renderer와 Cross-Thread 통신이 이루어져야합니다.



구현할 메소드는 아래와 같습니다.

- onSurfaceCreated(gl: GL10?, confing: EGLConfig?): GLSurfaceView를 만들 때 호출됩니다. 시스템 동작에서 단 한번만 호출되어야하는(그래픽 객체 초기화, 매개변수 설정 등) 작업을 작성하면 됩니다. 

- onSurfaceChanged(gl:GL10?, width:Int, height:Int):  GLSurfaceView의 크기 변경, Device의 화면 방향 변경, 도형 변경이 발생할 때 호출되는 메서드 입니다.

- onDrawFrame(gl:GL10?): GLSurfaceView를 그릴 때마다 호출되는 메서드입니다. 그래픽 객체 그리기의 기본 실행지점으로 사용합니다.

  

##### **구현**

```kotlin
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyRenderer(): GLSurfaceView.Renderer {
    private var _myShape:MyShape?
    init {
        _myShape = MyShape()
    }
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        _myShape?.draw()
    }
}
```



##### Method

| GLES20 InCode                                    | 설명                                                         |
| ------------------------------------------------ | ------------------------------------------------------------ |
| glClearColor(r:Float, g:Float, b:Float, a:Float) | Color Buffer를 Clear할 때 사용할 색상을 설정합니다. rgba는 0.0 ~ 1.0 사이의 값을 지정할 수 있습니다. |
| glClear(mask:Int)                                | glClearColor로 설정한 값으로 ColorBuffer를 클리어 합니다.<br />Clear하고 싶은 Buffer를 mask로 지정해 Clear할 수 있으며 or 연산이 가능합니다.<br />GL_COLOR_BUFFER_BIT: Color를 사용하기위해 현재 활성화 된 Buffer<br />GL_DEPTH_BUFFER_BIT: Depth Buffer<br />GL_ACCUM_BUFFER_BIT: 누적 Buffer<br />GL_STENCIL_BUFFER_BIT: 스텐실 Buffer<br /><br />[*]glClearColor 만이 아닌 glClearIndex, glClearDepth, glClearStencil, glClearAccum 함수로 선택된 값을 이용해 ColorBuffer를 설정하게 됩니다. |
| glViewport(x:Int, y:Int, width:Int, height:Int)  | x, y, width, height를 설정합니다.                            |
| glEnable(cap:Int)                                | 지정된 [옵션](#glEnable & glDisable Option)을 활성화 합니다. |
| glDisable(cap:Int)                               | 지정된 [옵션](#glEnable & glDisable Option)을 비활성화 합니다. |
| glEnableClientState                              |                                                              |
| glLoadIdentity                                   |                                                              |
| glTranslatef                                     |                                                              |



##### glEnableClientState & glDisableClientState

```
glEnable & DisableClientState 함수는 gl10에 존재하는 함수입니다. gles20은 호출방식만 다를 뿐 공통적으로 필요할 수 있는 기능이므로, 여기서 설명합니다.
함수명의 Client는 OpenGL 설계에 따라 나온 명칭입니다. OpenGL은 Server와 Client Model로 설계 및 구현이 되어 Server(이미지 생성) Client(이미지 출력)를 구분 짓기 위한 함수명입니다.
OpenGL 및 ES는 기본적으로 Client의 기능이 모두 비활성화 상태이며, 이를 활성화 하기 위한 함수가 glEnableClientState입니다.(gl10 기준)

```

| Client Option           | GLES20 InCode                        | 설명                                                         |
| ----------------------- | ------------------------------------ | ------------------------------------------------------------ |
| GL_COLOR_ARRAY          |                                      | glDrawArrays 및 glDrawElements로 랜더링 할 때 glColorPointer로 설정한 색상 배열을 참고하여 랜더링 합니다. |
| GL_NORMAL_ARRAY         |                                      | glDrawArrays 및 glDrawElements로 랜더링 할 때 glNormalPointer로 설정한 법선 배열을 참고하여 랜더링 합니다. |
| GL_POINT_SIZE_ARRAY_OES |                                      | 점과 점 스프라이트를 랜더링 할 때 점 크기 배열을 참고하여 랜더링 합니다. |
| GL_TEXTURE_COORD_ARRAY  |                                      | glDrawArrays 및 glDrawElements로 랜더링 할 때 glTexCoordPointer로 설정한 Texture 자표 배열을 참고하여 랜더링 합니다. |
| GL_VERTEX_ARRAY         | glEnableVertexAttribArray(index:Int) | glDrawArrays 및 glDrawElements로 랜더링 할 때 glVertexPointer로 설정한 정점 배열을 참고하여 랜더링 합니다. |



##### **glEnable & glDisable Option**

| Option              | 설명                            |
| :------------------ | ------------------------------- |
| GL_BLEND            | 색상 블랜딩                     |
| GL_CULL_FACE        | 폴리곤 추려내기                 |
| GL_DEPTH_TEST       | 깊이 테스트                     |
| GL_DITHER           | 디더링                          |
| GL_FOG              | OpenGL 안개모드                 |
| GL_LIGHTING         | OpenGL 조명                     |
| GL_HIGHTx           | x 번째 OpenGL 조명(최소값 8)    |
| GL_POINT_SMOOTH     | 점 탄티알리아싱                 |
| GL_LINE_SMOOTH      | 선 안티알리아싱                 |
| GL_POLYGON_SMOOTH   | 폴리곤 안티알리아싱             |
| GL_LINE_STIPPLE     | 선 스티플링                     |
| GL_SCISSOR_TEST     | 시서링                          |
| GL_STENCIL_TEST     | 스텐실 테스트                   |
| GL_TEXTURE_xD       | x 차원의 텍스쳐링(x: 1 ~ 3)     |
| GL_TEXTURE_CUBE_MAP | 큐브 맵 텍스쳐링                |
| GL_TEXTURE_GEN_x    | x에 대한 texgen(x는 S, T, R, Q) |

[참고문헌] 
[공유 세상 ShareGlobe Blog의 glClear](https://m.blog.naver.com/PostView.nhn?blogId=happylamb&logNo=30005421525&proxyReferer=https:%2F%2Fwww.google.com%2F)

[GLSurfaceView.Renderer | Android Developer](https://developer.android.com/reference/kotlin/android/opengl/GLSurfaceView.Renderer?hl=ko)

[멈춤보단 천천히라도 Blog의 OpenGL ES 2.0 예제](https://webnautes.tistory.com/1009)

[사자 Blog의 glEnable, glDisable 설정 기능](https://blog.naver.com/isaja/140058428574)

[OpenGL-ES-튜토리얼-5편](https://skyfe.tistory.com/entry/iOS-OpenGL-ES-%ED%8A%9C%ED%86%A0%EB%A6%AC%EC%96%BC-5%ED%8E%B8)

http://blog.daum.net/aero2k/84



#### Shape

---

Shape는 위에서 설명한 GLSurfaceView 클래스나 GLSurfaceView.Renderer 인터페이스와 다르게 따로 GL로 부터 상속받는 것 없이 구현하면 됩니다.

화면에 그려질 그래픽 객체로 써 대부분의 작업을 이 클래스에서 하게 될 것입니다.



**시작하기 전 앞서...**

계속적으로 나올 중요한 부분을 짚고 넘어가겠습니다.

일단 Shape는 Vertex( 정점) , Coords(좌표)을 이용해 그래픽을 그려내는 객체 입니다.

- Shape는 Renderer의 onSurfaceCreated 메소드에서 초기화되어야 합니다.

- OpenGL은 좌표를 읽는 순서가 기본적으로 반 시계 방향으로 적용되어 있으며, 이는 시계방향으로 변경할 수 있습니다.

- OpenGL의 좌표계의 원점(0, 0, 0)은 GLSurfaceView의 중앙입니다. 예로 삼각형을 그리기 위해선 (0, 1, 0), (-1, -1, 0), (1, -1, 0) 의 좌표를 이용할 수 있습니다.



##### 구현

```kotlin
import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class MyShape {
    //region Field
	val vertexCoords:FloatArray? = floatArrayOf(
        0.0f, 1.0f, 0.0f, //상단 정점
        -1.0f, -1.0f, 0.0f, // 왼쪽 아래 정점
        1.0f, -1.0f, 0.0f// 오른쪽 아래 정점
    )
	val COORDS_PER_VERTEX:Int? = 3
	var vertexCount:Int = vertexCorrds.size / COORDS_PER_VERTEX
	var vertexStride:Int = COORDS_PER_VERTEX * 4
	var vertexBuffer:FloatBuffer
	val color:FloatArray = floatArrayOf(0.6f, 0.7f, 0.2f, 1.0f)
	val vertexShaderCode:String = 
			"attribute vec4 vPosition;" +
            "void main() {" +
                "gl_Position = vPosition;" +
            "}"
    val fragmentShaderCode:String = 
    		"precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "    gl_FragColor = vColor;" +
            "}"
	var program:Int?
	//endregion Field
	init {       
        vertexBuffer = arrayToBuffer(vertexCorrds)
        var vertexShader:Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        var fragmentShader:Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
	}
    
    fun arrayToBuffer(f:FloatArray): FloatBuffer {
        var buf: ByteBuffer = ByteBuffer.allocateDirect(f.size * 4)
        buf.order(ByteOrder.nativeOrder())
        var fbuf:FloatBuffer = buf.asFloatBuffer()
        fbuf.put(f)
        fbuf.position(0)

        return fbuf
    }
    
    fun loadShader(type:Int, shaderCode:String): Int {
        var shader:Int = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
    
   		return shader
	}

    fun draw() {
        GLES20.glUseProgram(program!!)
        var positionHandle = GLES20.glGetAttribLocation(program!!, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX!!,
                GLES20.GL_FLOAT, false, vertexStride!!, vertexBuffer)
        
        var colorHandle = GLES20.glGetUniformLocation(program!!, "vColor")
        GLES20.glUniform4fv(colorHandle, 1, color, 0)
        
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount!!)
        GLES20.glDisableVertexAttribArray(positionHandle)
    }
}
```



이번 파트에선 구현 코드 내용이 많아 분할해서 하나하나 설명하겠습니다.

###### **Field**

- **vertexCorrds:FloatArray**  -  변수명 그대로 정점좌표(x,y,z) 배열 입니다. 위에서 언급한 대로 읽는 순서는 기본적으로 반시계 방향입니다.
- **COORDS_PER_VERTEX:Int**  -   vertex(정점) 를 지정하기 위해 필요한 좌표의 갯수입니다. 예제 기준(x, y, z)이므로 3입니다.
- **vertexCount:Int** -  vertex(정점)의 갯수입니다. 예제 기준 vertexCoords는 9이며 이를 COORDS_PER_VERTEX로 나눌 시 삼각형의 정점 갯수(3)를 확인 가능합니다.
- **vertexStride:Int** - 향후 program에 할당할 byte크기를 좌표의 갯수만큼 계산하기 위한 변수입니다. 좌표 수 *  4입니다.
- **vertexBuffer:FloatBuffer** - vertex(정점) 좌표들을 저장할 변수입니다. 자세한 설명은 [arraytToBuffer(f:FloatArray)](#arrayToBuffer(f:FloatArray)) 를 참고해주시길 바랍니다.
- **color:FloatArray** - rgba 순의 색상 정보입니다.



별도로 이해해야할 변수 입니다. OpenGL에서 랜더링 시 화면에 Shape를 그리기 위해선 아래의 3 변수를 이해하고 사용해야합니다.

- **vertexShaderCode:String** - shape의 vertex를 랜더링 하기 위한 OpenGLES Graphics Code
- **fragmentShaderCode:String**  - shape의 색 및 Textures를 랜더링 하기 위한 OpenGLES Graphics Code
- **program:Int** - shape를 그리기 위해 사용되는 위 Shader Code를 갖고 있는 OpenGLES 객체



OpenGLES1.0으로 할 때는 Shader Code 작성을 피할 수 있었지만 이번엔 확실히 확인하고 넘어가겠습니다.

OpenGLES 에서 랜더링을 진행 할 때 Shape를 그리기 위해선 최소 하나의 vertexShader가 필요합니다. 

그 외 FragmentShader는 Shape의 표면을 채우는(Color or Texture) 역할을 하게 됩니다.

위 vertexShaderCode와 fragmentShaderCode는 **OpenGL Shading Language(GLSL)**  **코드**이며, OpenGLES 환경에서 사용하기 위해선 

랜더링이 진행되기 전에 우선적으로 컴파일 되어야합니다. 아래 설명할 [loadShader 함수](#loadShader(type:Int, shaderCode:String): Int)는 이를 위해 만들어진 함수입니다. 



###### init

위의 내용 및 연결된 링크 (loadShader 함수) 등을 이해하면 아래의 코드를 이해할 수 있습니다.

```kotlin
init {       
        //정점 좌표를 저장한 버퍼 생성
        vertexBuffer = arrayToBuffer(vertexCorrds)
        // vertexShader 및 fragmentShader 객체 생성 및 컴파일
        var vertexShader:Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        var fragmentShader:Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        // OpenGLES program 객체 생성
        program = GLES20.glCreateProgram()
        // program 객체에 vertex Shader 및 fragmentShader 를 추가
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        // program 객체를 OpenGL에 연결한다. 이 작업 이전에 shader를 추가.
        GLES20.glLinkProgram(program)
	}
```



###### arrayToBuffer(f:FloatArray)

Method의 내용을 살펴보면 ByteBuffer를 생성한 후에 FloatBuffer로 변환하여 정점 좌표를 저장하는 것을 확인할 수 있습니다.

이처럼 구현한 이유는 ByteBuffer를 사용할 경우 랜더링 성능 개선되기 때문입니다.

```kotlin
fun arrayToBuffer(f:FloatArray): FloatBuffer {
        // 4byte * f.size 라 이해하시면 됩니다.
        var buf: ByteBuffer = ByteBuffer.allocateDirect(f.size * 4)
        // byteBuffer에서 사용할 엔디안을 지정합니다. 예제에선 하드웨어의 native byte order를 사용합니다.
        // 만약 사각형 같은 Shape를 구현할 경우 정점 재활용의 유무에 따라 코드가 변경될 부분임을 기억해주세요.
        buf.order(ByteOrder.nativeOrder())
        // ByteBuffer를 FloatBuffer로 변환합니다.
        var fbuf:FloatBuffer = buf.asFloatBuffer()
        // FloatBuffer에 정점좌표의 정보를 저장하고, 읽어올 버퍼의 위치(position)을 설정합니다.
        fbuf.put(f)
        fbuf.position(0)

        return fbuf
    }
```



###### loadShader(type:Int, shaderCode:String): Int

매개변수의 type은 GLES20.GL_VERTEX_SHADER와 GLES20.GL_FRAGMENT_SHADER 2 가지가 있습니다.

shaderCode는 위에 작성된 vertexShaderCode 및 fragmentShaderCode 입니다.

```kotlin
fun loadShader(type:Int, shaderCode:String): Int {
        // type 정보를 바탕으로 shader 객체를 생성합니다.
        var shader:Int = GLES20.glCreateShader(type)
        // 생성한 shader 객체에 shader code를 저장합니다.
        GLES20.glShaderSource(shader, shaderCode)
    	// shader 객체를 컴파일 합니다.
        GLES20.glCompileShader(shader)
    
   		return shader
	}
```



###### draw()

shape를 그려줄 함수 입니다. [MyRenderer](#GLSurfaceView.Renderer) 클래스의 onDrawFrame 함수에서 Clear 뒤에 호출하면 됩니다.

```kotlin
fun draw() {
        // 현재 랜더링 상태 부분에 program객체를 설치합니다.
        GLES20.glUseProgram(program)
        // program 객체에서 vertexShader의 vPosition 멤버 핸들을 가져옵니다.
        var positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
    	// 랜더링 시 vertex가 그려질 수 있도록 활성화 시킵니다. (이렇게 활성화 시킨 Shader는 비활성화를 해주어야 합니다.)
        GLES20.glEnableVertexAttribArray(positionHandle)
    	// vertex 속성을 vertexBuffer에 저장한 vertex 좌표로 정의합니다.
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, vertexStride, vertexBuffer)
        // program 객체에서 fragmentShader 의 vColor 멤버 핸들을 가져옵니다.
        var colorHandle = GLES20.glGetUniformLocation(program, "vColor")
    	// 랜더링 시 그려질 색상을 color변수 값으로 정의합니다.
        GLES20.glUniform4fv(colorHandle, 1, color, 0)
        
    	// vertex 갯수만큼 랜더링을 진행합니다.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
    	// 활성화한 vertex 속성을 비활성화로 변경합니다.
        GLES20.glDisableVertexAttribArray(positionHandle)
    }
```



[참고문헌]

[glUseProgram](https://www.khronos.org/registry/OpenGL-Refpages/gl2.1/xhtml/glUseProgram.xml)

[멈춤보단 천천히라도 Blog의 OpenGL ES 2.0 예제](https://webnautes.tistory.com/1009)