package com.pcloud.glessample_kotlin.gles.shape

import com.pcloud.glessample_kotlin.gles.MyShape

class TriangleShape: MyShape() {
    override val vertexCoords:FloatArray? = floatArrayOf(
    0.0f, 1.0f, 0.0f, //상단 정점
    -1.0f, -1.0f, 0.0f, // 왼쪽 아래 정점
    1.0f, -1.0f, 0.0f// 오른쪽 아래 정점
    )

    init {
        initialize()
    }
}