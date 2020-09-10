package com.pcloud.glessample_kotlin.gles

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.pcloud.glessample_kotlin.gles.shape.QuadShape
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class MyRenderer(): GLSurfaceView.Renderer {

    private var _myShape:MyShape? = null



    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        if(_myShape != null) {
            _myShape?.destroy()
        }
        _myShape = QuadShape()
        GLES20.glClearColor(0f, 0f, 0f, 0f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        _myShape?.draw()
    }
}