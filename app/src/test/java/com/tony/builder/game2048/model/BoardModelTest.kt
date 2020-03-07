package com.tony.builder.game2048.model

import com.tony.builder.game2048.util.DumpUtils
import com.tony.kotlin.libboardview.Point
import org.junit.Assert
import org.junit.Test
import java.lang.reflect.InvocationTargetException
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class BoardModelTest {
    companion object {
        private const val BOARD_DIMENSION = 4;
    }

    @Test
    fun addition_isCorrect() {
        Assert.assertEquals(4, 2 + 2.toLong())
    }

    @Test
    @Throws(NoSuchMethodException::class, IllegalAccessException::class, InvocationTargetException::class)
    fun testResetCardBoard() {
        val cardMap = Array(BOARD_DIMENSION) { IntArray(BOARD_DIMENSION) }
        val emptyPointList: MutableList<Point> = LinkedList()
        val bm = BoardModel(cardMap, emptyPointList)
        Assert.assertEquals(14, emptyPointList.size.toLong())
        val resetCardBoard = bm.javaClass.getDeclaredMethod("resetCardBoard")
        resetCardBoard.isAccessible = true
        resetCardBoard.invoke(bm)
        Assert.assertEquals(16, emptyPointList.size.toLong())
    }

    @Test
    fun testOnSwipeLeft() {
        val bm = BoardModel()
        val currentMap = arrayOf(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 2, 0, 0),
                intArrayOf(0, 0, 2, 0),
                intArrayOf(0, 0, 0, 2))
        val expectMap = arrayOf(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(2, 0, 0, 0),
                intArrayOf(2, 0, 0, 0),
                intArrayOf(2, 0, 0, 0))
        bm.setCardMap(currentMap)
        bm.onSwipeLeft()
        Assert.assertTrue(DumpUtils.isEqual(expectMap, currentMap))
        val currentMap2 = arrayOf(
                intArrayOf(4, 0, 0, 0),
                intArrayOf(4, 2, 0, 0),
                intArrayOf(4, 0, 2, 0),
                intArrayOf(4, 0, 0, 2))
        val expectMap2 = arrayOf(
                intArrayOf(4, 0, 0, 0),
                intArrayOf(4, 2, 0, 0),
                intArrayOf(4, 2, 0, 0),
                intArrayOf(4, 2, 0, 0))
        bm.setCardMap(currentMap2)
        bm.onSwipeLeft()
        Assert.assertTrue(DumpUtils.isEqual(expectMap2, currentMap2))
        val currentMap3 = arrayOf(
                intArrayOf(2, 0, 0, 0),
                intArrayOf(2, 2, 0, 0),
                intArrayOf(2, 0, 2, 0),
                intArrayOf(2, 0, 0, 2))
        val expectMap3 = arrayOf(
                intArrayOf(2, 0, 0, 0),
                intArrayOf(4, 0, 0, 0),
                intArrayOf(4, 0, 0, 0),
                intArrayOf(4, 0, 0, 0))
        bm.setCardMap(currentMap3)
        bm.onSwipeLeft()
        Assert.assertTrue(DumpUtils.isEqual(expectMap3, currentMap3))
        val currentMap4 = arrayOf(
                intArrayOf(0, 2, 0, 0),
                intArrayOf(0, 2, 0, 0),
                intArrayOf(0, 2, 2, 0),
                intArrayOf(0, 2, 0, 2))
        val expectMap4 = arrayOf(
                intArrayOf(2, 0, 0, 0),
                intArrayOf(2, 0, 0, 0),
                intArrayOf(4, 0, 0, 0),
                intArrayOf(4, 0, 0, 0))
        bm.setCardMap(currentMap4)
        bm.onSwipeLeft()
        Assert.assertTrue(DumpUtils.isEqual(expectMap4, currentMap4))
    }

    @Test
    fun testOnSwipeRight() {
        val bm = BoardModel()
        val currentMap = arrayOf(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 2, 0, 0),
                intArrayOf(0, 0, 2, 0),
                intArrayOf(0, 0, 0, 2))
        val expectMap = arrayOf(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 0, 0, 2),
                intArrayOf(0, 0, 0, 2),
                intArrayOf(0, 0, 0, 2))
        bm.setCardMap(currentMap)
        bm.onSwipeRight()
        Assert.assertTrue(DumpUtils.isEqual(expectMap, currentMap))
        val currentMap2 = arrayOf(
                intArrayOf(4, 0, 0, 0),
                intArrayOf(4, 2, 0, 0),
                intArrayOf(4, 0, 2, 0),
                intArrayOf(4, 0, 0, 2))
        val expectMap2 = arrayOf(
                intArrayOf(0, 0, 0, 4),
                intArrayOf(0, 0, 4, 2),
                intArrayOf(0, 0, 4, 2),
                intArrayOf(0, 0, 4, 2))
        bm.setCardMap(currentMap2)
        bm.onSwipeRight()
        Assert.assertTrue(DumpUtils.isEqual(expectMap2, currentMap2))
        val currentMap3 = arrayOf(
                intArrayOf(2, 0, 0, 0),
                intArrayOf(2, 2, 0, 0),
                intArrayOf(2, 0, 2, 0),
                intArrayOf(2, 0, 0, 2))
        val expectMap3 = arrayOf(
                intArrayOf(0, 0, 0, 2),
                intArrayOf(0, 0, 0, 4),
                intArrayOf(0, 0, 0, 4),
                intArrayOf(0, 0, 0, 4))
        bm.setCardMap(currentMap3)
        bm.onSwipeRight()
        Assert.assertTrue(DumpUtils.isEqual(expectMap3, currentMap3))
        val currentMap4 = arrayOf(
                intArrayOf(0, 2, 0, 0),
                intArrayOf(0, 2, 0, 0),
                intArrayOf(0, 2, 2, 0),
                intArrayOf(0, 2, 0, 2))
        val expectMap4 = arrayOf(
                intArrayOf(0, 0, 0, 2),
                intArrayOf(0, 0, 0, 2),
                intArrayOf(0, 0, 0, 4),
                intArrayOf(0, 0, 0, 4))
        bm.setCardMap(currentMap4)
        bm.onSwipeRight()
        Assert.assertTrue(DumpUtils.isEqual(expectMap4, currentMap4))
    }

    @Test
    fun testOnSwipeUp() {
        val bm = BoardModel()
        val currentMap = arrayOf(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 2, 0, 0),
                intArrayOf(0, 0, 2, 0),
                intArrayOf(0, 0, 0, 2))
        val expectMap = arrayOf(
                intArrayOf(0, 2, 2, 2),
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 0, 0, 0))
        bm.setCardMap(currentMap)
        bm.onSwipeUp()
        println(bm.dump())
        Assert.assertTrue(DumpUtils.isEqual(expectMap, currentMap))
        val currentMap2 = arrayOf(
                intArrayOf(4, 4, 4, 4),
                intArrayOf(0, 0, 0, 2),
                intArrayOf(0, 0, 2, 0),
                intArrayOf(0, 2, 0, 0))
        val expectMap2 = arrayOf(
                intArrayOf(4, 4, 4, 4),
                intArrayOf(0, 2, 2, 2),
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 0, 0, 0))
        bm.setCardMap(currentMap2)
        bm.onSwipeUp()
        println(bm.dump())
        Assert.assertTrue(DumpUtils.isEqual(expectMap2, currentMap2))
        val currentMap3 = arrayOf(
                intArrayOf(2, 2, 2, 2),
                intArrayOf(0, 0, 2, 0),
                intArrayOf(0, 2, 0, 0),
                intArrayOf(2, 0, 0, 0))
        val expectMap3 = arrayOf(
                intArrayOf(4, 4, 4, 2),
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 0, 0, 0))
        bm.setCardMap(currentMap3)
        bm.onSwipeUp()
        Assert.assertTrue(DumpUtils.isEqual(expectMap3, currentMap3))
        val currentMap4 = arrayOf(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(2, 2, 2, 2),
                intArrayOf(0, 2, 0, 0),
                intArrayOf(2, 0, 0, 0))
        val expectMap4 = arrayOf(
                intArrayOf(4, 4, 2, 2),
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 0, 0, 0))
        bm.setCardMap(currentMap4)
        bm.onSwipeUp()
        Assert.assertTrue(DumpUtils.isEqual(expectMap4, currentMap4))
    }

    @Test
    fun testOnSwipeDown() {
        val bm = BoardModel()
        val currentMap = arrayOf(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 2, 0, 0),
                intArrayOf(0, 0, 2, 0),
                intArrayOf(0, 0, 0, 2))
        val expectMap = arrayOf(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 2, 2, 2))
        bm.setCardMap(currentMap)
        bm.onSwipeDown()
        println(bm.dump())
        Assert.assertTrue(DumpUtils.isEqual(expectMap, currentMap))
        val currentMap2 = arrayOf(
                intArrayOf(4, 4, 4, 4),
                intArrayOf(0, 0, 0, 2),
                intArrayOf(0, 0, 2, 0),
                intArrayOf(0, 2, 0, 0))
        val expectMap2 = arrayOf(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 4, 4, 4),
                intArrayOf(4, 2, 2, 2))
        bm.setCardMap(currentMap2)
        bm.onSwipeDown()
        println(bm.dump())
        Assert.assertTrue(DumpUtils.isEqual(expectMap2, currentMap2))
        val currentMap3 = arrayOf(
                intArrayOf(2, 2, 2, 2),
                intArrayOf(0, 0, 2, 0),
                intArrayOf(0, 2, 0, 0),
                intArrayOf(2, 0, 0, 0))
        val expectMap3 = arrayOf(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 0, 0, 0),
                intArrayOf(4, 4, 4, 2))
        bm.setCardMap(currentMap3)
        bm.onSwipeDown()
        Assert.assertTrue(DumpUtils.isEqual(expectMap3, currentMap3))
        val currentMap4 = arrayOf(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(2, 2, 2, 2),
                intArrayOf(0, 2, 0, 0),
                intArrayOf(2, 0, 0, 0))
        val expectMap4 = arrayOf(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 0, 0, 0),
                intArrayOf(4, 4, 2, 2))
        bm.setCardMap(currentMap4)
        bm.onSwipeDown()
        Assert.assertTrue(DumpUtils.isEqual(expectMap4, currentMap4))
    }

    @Test
    fun testDumpUtil() {
        val expect = arrayOf(
                intArrayOf(0, 2, 0, 0),
                intArrayOf(0, 2, 0, 0),
                intArrayOf(0, 2, 2, 0),
                intArrayOf(0, 2, 0, 2))
        val currentMap = arrayOf(
                intArrayOf(0, 2, 0, 0),
                intArrayOf(0, 2, 0, 0),
                intArrayOf(0, 2, 2, 0),
                intArrayOf(0, 2, 0, 2))
        val errorMap = arrayOf(
                intArrayOf(0, 2, 0, 0),
                intArrayOf(0, 2, 0, 0),
                intArrayOf(0, 2, 2, 0),
                intArrayOf(4, 2, 0, 2))
        Assert.assertTrue(DumpUtils.isEqual(expect, currentMap))
        Assert.assertFalse(DumpUtils.isEqual(expect, errorMap))
    }
}