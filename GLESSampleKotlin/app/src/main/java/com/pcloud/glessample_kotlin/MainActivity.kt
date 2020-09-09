package com.pcloud.glessample_kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import com.pcloud.glessample_kotlin.gles.BaseGLView

class MainActivity : AppCompatActivity() {
    private var baseGLView: BaseGLView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var root_layout:ConstraintLayout? = findViewById(R.id.root_layout)
        baseGLView = BaseGLView(this)
        root_layout?.addView(baseGLView)
    }
}