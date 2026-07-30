package com.mkpro.keyboard.domain.model

import com.mkpro.keyboard.core.keyboard.KeyModel

/**
 * Output of the future Custom Keyboard Designer (drag/drop/resize/rotate).
 * Positions are free-form (x/y/rotation) rather than row-based, unlike
 * core.layers.Layer, so the designer's canvas isn't constrained to rows.
 */
data class CustomLayoutKeyPlacement(
    val key: KeyModel,
    val xDp: Float,
    val yDp: Float,
    val widthDp: Float,
    val heightDp: Float,
    val rotationDegrees: Float = 0f
)

data class CustomLayout(
    val id: String,
    val name: String,
    val placements: List<CustomLayoutKeyPlacement>,
    val backgroundImagePath: String? = null
)
