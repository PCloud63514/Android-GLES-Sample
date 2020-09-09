# Android GLES2.0 Sample

Android GLES2.0 기본예제가 작성된 학습 프로젝트



언어는 Kotlin을 기반으로 작성하였습니다. 

Java와 Kotlin 모두 작성해본 결과 코드 작성에 큰 차이는 없습니다. (getter setter의 차이로 setRenderMode같은 내용은 착각할 수 있습니다.)

## Index

1. [기본 개념](#기본-개념)
2. [OpenGLES 환경 구성](#opengles-환경-구성)
   1. [Manifest](#manifest)
   2. [GLSurfaceView](#glsurfaceview)

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