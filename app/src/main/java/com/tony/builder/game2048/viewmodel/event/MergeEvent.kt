package com.tony.builder.game2048.viewmodel.event

import com.tony.kotlin.libboardview.Point

open class MergeEvent(val source: Point, val sink: Point, val sourceValue: Int, val sinkValue: Int) {
    override fun toString(): String {
        return "[$source] -> [$sink], $sourceValue->$sinkValue"
    }
}