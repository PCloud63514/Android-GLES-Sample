package com.pcloud.glessample_kotlin.gles

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView

class MyGLView(context: Context): GLSurfaceView(context) {
    private val _myRenderer:MyRenderer?
    init {
        setEGLContextClientVersion(2)
        preserveEGLContextOnPause = true
        holder.setFormat(PixelFormat.TRANSLUCENT)
        _myRenderer = MyRenderer()
        setRenderer(_myRenderer)

        renderMode = RENDERMODE_WHEN_DIRTY
//        isFocusableInTouchMode = true
        requestRender()
    }
}