package com.mkpro.keyboard.core.layers

import com.mkpro.keyboard.core.keyboard.KeyModel

data class Layer(
    val id: String,
    val name: String,
    val rows: List<List<KeyModel>>
)
