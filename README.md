# Android GLES2.0 Sample

Android GLES2.0 기본예제가 작성된 학습 프로젝트



언어는 Kotlin을 기반으로 작성하였습니다. 

Java와 Kotlin 모두 작성해본 결과 코드 작성에 큰 차이는 없습니다. (getter setter의 차이로 setRenderMode같은 내용은 착각할 수 있습니다.)

## Index

1. [기본 개념](#기본-개념)
2. [OpenGLES 환경 구성](#opengles-환경-구성)
   1. [Manifest](#manifest)
   2. [GLSurfaceView](#glsurfaceview)
   3. [GLSurfaceView.Renderer](#glsurfaceview.renderer)

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

**EGL Config**

```
그래픽을 표현할 채널 수와 각 채널에 할당된 bit 정보가 작성되어있습니다. 
별도의 설정이 없다면 GLSurfaceView는 최소 16bit depth buffer와 PixelFormat.RGB888 정보가 작성된 EGL Config 를 사용합니다.
```



**구현**

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



**[필수]**

| In Code                                        | 설명                                                         |
| :--------------------------------------------- | ------------------------------------------------------------ |
| setEGLContextClientVersion(Int)                | EGLContext Client Version을 지정합니다.<br />내부 동작에서 EGLContextFactory 및 EGLConfigChooser에 버전을 알리고 각 인스턴스를 생성하기 때문에 단순히 Version을 명시하는 메소드는 아닙니다. |
| setRenderer(glRenderer:GLSurfaceView.Renderer) | 현재 GLSurfaceView의 Renderer를 설정합니다.                  |



**[선택사항]**

| In Code                                                      | 설명                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| preserveEGLContextOnPause = Boolean                          | Rendering Thread를 일시 중지하고 EGLContext 값을 축소합니다. |
| setEGLConfigChooser(needDepth:Boolean or configChooser:GLSurfaceView.EGLConfigChooser) | EGLConfig 을 선택하는 메서드 입니다. 보통 직접 호출할 일이 없고, 위에 EGLConfig 설명에 작성된 정보 외의 설정이 필요할 경우 지정하면 됩니다. |
| holder.setFormat(PixelFormat.TRANSLUCENT or PixelFormat.TRANSPARENT) | GLSurfaceView의 구성 표면을 가져오는 대상을 설정할 수 있습니다.<br />**PixelFormat.TRANSLUCENT**<br />- GLSurfaceView의 배경이 투명해야할 경우 설정할 수 있습니다.<br />**PixelFormat.TRANSPARENT**<br />- 별도의 설정할 필요가 없습니다. |
| rederMode = RENDERMODE_CONTINUOUSLY or RENDERMODE_WHEN_DIRTY | Rendering Mode를 설정합니다. <br />**RENDERMODE_CONTINUOUSLY**<br />- 반복적으로 랜더링을 진행합니다. 즉 draw() 메서드가 계속 호출되어 비동기적으로 변화해야하는 대상에 적합합니다.<br />**RENDERMODE_WHEN_DIRTY**<br />- View의 업데이트가 필요 없을 때 적합한 Mode입니다. GPU와 CPU의 지속적인 연산이 없으므로 배터리 및 시스템 성능 향상을 확인할 수 있습니다. <br />만약 RENDERMODE_WHEN_DIRTY 상태에서 View를 업데이트 하려는 경우 requestRender() 메서드를 호출하면 됩니다. |
| isFocusableInTouchMode = Boolean                             | GLSurfaceView가 Touch Event를 발생시킬 수 있습니다.          |
| requestRender()                                              | GLSurfaceVIew를 Update합니다.                                |
|                                                              |                                                              |





[참고문헌] [GLSurfaceView | Android Developer](https://developer.android.com/reference/android/opengl/GLSurfaceView)

#### GLSurfaceView.Renderer

---

GLSurfaceView.Renderer Interface는 GLSurfaceView에 그래픽을 그릴 때 필요한 메서드를 정의합니다.

구현할 메소드는 아래와 같습니다.

- onSurfaceCreate(gl: GL10?, confing: EGLConfig?): GLSurfaceView를 만들 때 호출됩니다. 시스템 동작에서 단 한번만 호출되어야하는(그래픽 객체 초기화, 매개변수 설정 등) 작업을 작성하면 됩니다. 

- onSurfaceChanged(gl:GL10?, width:Int, height:Int):  GLSurfaceView의 크기 변경, Device의 화면 방향 변경, 도형 변경이 발생할 때 호출되는 메서드 입니다.

- onDrawFrame(gl:GL10?): GLSurfaceView를 그릴 때마다 호출되는 메서드입니다. 그래픽 객체 그리기의 기본 실행지점으로 사용합니다.

  