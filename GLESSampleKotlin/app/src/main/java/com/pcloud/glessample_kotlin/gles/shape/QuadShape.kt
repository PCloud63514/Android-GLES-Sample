package com.pcloud.glessample_kotlin.gles.shape

import com.pcloud.glessample_kotlin.gles.MyShape

class QuadShape: MyShape() {
    override val vertexCoords: FloatArray? = floatArrayOf(
        -0.5f, 0.5f, 0.0f,  //0번 정점
        -0.5f, -0.5f, 0.0f,  //1번 정점
        0.5f, -0.5f, 0.0f,  //2번 정점

        -0.5f, 0.5f, 0.0f,  //0번 정점
        0.5f, -0.5f, 0.0f,  //2번 정점
        0.5f, 0.5f, 0.0f  ///3번 정점
    )

    init {
        initialize()
    }
}