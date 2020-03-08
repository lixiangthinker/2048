package com.tony.builder.game2048.viewmodel.event

import com.tony.kotlin.libboardview.Point

class MoveEvent(source: Point, sink: Point, sourceValue: Int, sinkValue: Int)
    : MergeEvent(source, sink, sourceValue, sinkValue)