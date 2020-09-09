package com.pcloud.glessample_kotlin.gles

import android.content.Context
import android.opengl.GLSurfaceView

class BaseGLView(context: Context): GLSurfaceView(context) {
    private val _baseRenderer:BaseRenderer?
    init {
        setEGLContextClientVersion(2)
        preserveEGLContextOnPause = true
        _baseRenderer = BaseRenderer()
        setRenderer(_baseRenderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }
}