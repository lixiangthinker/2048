package com.tony.kotlin.libboardview

class Point(var x: Int, var y: Int) {
    constructor(point: Point) : this(point.x, point.y)

    override fun toString(): String {
        return "[$x,$y]"
    }
}