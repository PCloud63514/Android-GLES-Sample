package com.pcloud.glessample_kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pcloud.glessample_kotlin.gles.MyGLView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var myGLView:MyGLView? = MyGLView(this)

        setContentView(myGLView)
    }
}