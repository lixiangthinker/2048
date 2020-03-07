package com.tony.builder.game2048.view

interface IMotionHandler {
    fun onSwipeUp(): Boolean
    fun onSwipeDown(): Boolean
    fun onSwipeLeft(): Boolean
    fun onSwipeRight(): Boolean
}