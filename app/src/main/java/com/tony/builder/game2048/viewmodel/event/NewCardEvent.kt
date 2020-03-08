package com.tony.builder.game2048.viewmodel.event

import com.tony.kotlin.libboardview.Point

class NewCardEvent(val position: Point, val value: Int) {
    override fun toString(): String {
        return "[$position] -> $value"
    }
}