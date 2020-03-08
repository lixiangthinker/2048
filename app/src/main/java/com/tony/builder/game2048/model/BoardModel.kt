package com.tony.builder.game2048.model

import com.tony.builder.game2048.util.DumpUtils
import com.tony.builder.game2048.view.IMotionHandler
import com.tony.kotlin.libboardview.Point
import java.util.*

class BoardModel : IMotionHandler {
    companion object {
        const val BOARD_DIMENSION = 4
        private const val PROBABILITY_OF_FOUR = 0.4
    }

    private var cardMap: Array<IntArray>
    private var emptyPointList: MutableList<Point>
    private var boardEventListener: BoardEventListener? = null

    interface BoardEventListener {
        fun onBoardReset(boardDimension: Int)
        fun onCardGenerated(pt: Point, value: Int)
        fun onCardMerged(source: Point, sink: Point, sourceValue: Int, sinkValue: Int)
        fun onCardMoved(source: Point, sink: Point, sourceValue: Int, sinkValue: Int)
        fun onGameFinished()
    }

    constructor() {
        cardMap = Array(BOARD_DIMENSION) { IntArray(BOARD_DIMENSION) }
        emptyPointList = LinkedList()
        resetCardBoard()
        genRandomCards()
        genRandomCards()
    }

    // for inject cardmap and emptyPointList;
    constructor(cardMap: Array<IntArray>, emptyPointList: MutableList<Point>) {
        this.cardMap = cardMap
        this.emptyPointList = emptyPointList
        resetCardBoard()
        genRandomCards()
        genRandomCards()
    }

    fun startGame() {
        cardMap = Array(BOARD_DIMENSION) { IntArray(BOARD_DIMENSION) }
        emptyPointList = LinkedList()
        resetCardBoard()
        genRandomCards()
        genRandomCards()
    }

    private fun resetCardBoard() {
        emptyPointList.clear()
        for (y in 0 until BOARD_DIMENSION) {
            for (x in 0 until BOARD_DIMENSION) {
                cardMap[x][y] = 0
                emptyPointList.add(Point(x, y))
            }
        }
        boardEventListener?.onBoardReset(BOARD_DIMENSION)
    }

    private fun genRandomCards(): Boolean {
        emptyPointList.clear()
        for (y in 0 until BOARD_DIMENSION) {
            for (x in 0 until BOARD_DIMENSION) {
                if (cardMap[y][x] <= 0) {
                    emptyPointList.add(Point(x, y))
                }
            }
        }
        // no space left, check if game finished.
        if (emptyPointList.size == 0) {
            return isGameFinished
        }

        // gen 1 card at empty point.
        val pt = emptyPointList.removeAt((Math.random() * emptyPointList.size).toInt())
        cardMap[pt.y][pt.x] = if (Math.random() > PROBABILITY_OF_FOUR) 2 else 4
        boardEventListener?.onCardGenerated(pt, cardMap[pt.y][pt.x])

        // check after 1 card is generated.
        if (emptyPointList.size == 0) {
            return isGameFinished
        }
        return true
    }

    /**
     * check if this game is finished.
     *
     * @return if game is finished.
     */
    private val isGameFinished: Boolean
        get() {
            for (y in 0 until BOARD_DIMENSION) {
                for (x in 0 until BOARD_DIMENSION - 1) {
                    if (cardMap[x][y] == cardMap[x + 1][y]) {
                        // cards can be merged;
                        return false
                    }
                    if (cardMap[x][y] <= 0) {
                        // empty position
                        return false
                    }
                }
            }
            for (x in 0 until BOARD_DIMENSION) {
                for (y in 0 until BOARD_DIMENSION - 1) {
                    if (cardMap[x][y] == cardMap[x][y + 1]) {
                        // cards can be merged;
                        return false
                    }
                    if (cardMap[x][y] <= 0) {
                        // empty position
                        return false
                    }
                }
            }
            boardEventListener?.onGameFinished()
            return true
        }

    fun dump(): String {
        return DumpUtils.dump(cardMap)
    }

    enum class GameMotion {
        SWIPE_LEFT, SWIPE_RIGHT, SWIPE_UP, SWIPE_DOWN
    }

    fun handleMotion(motion: GameMotion?) {
        var changeResult = false
        when (motion) {
            GameMotion.SWIPE_UP -> changeResult = onSwipeUp()
            GameMotion.SWIPE_DOWN -> changeResult = onSwipeDown()
            GameMotion.SWIPE_LEFT -> changeResult = onSwipeLeft()
            GameMotion.SWIPE_RIGHT -> changeResult = onSwipeRight()
            else -> {
            }
        }
        if (changeResult) {
            genRandomCards()
        }
    }

    override fun onSwipeDown(): Boolean {
        var changeResult = false
        for (x in 0 until BOARD_DIMENSION) {
            var y = BOARD_DIMENSION - 1
            while (y >= 0) {

                // if current point is empty, find next none empty point, move to current point;
                if (cardMap[y][x] <= 0) {
                    var pt: Point? = null
                    for (k in y - 1 downTo 0) {
                        if (cardMap[k][x] > 0) {
                            pt = Point(x, k)
                            break
                        }
                    }
                    if (pt != null) {
                        cardMap[y][x] = cardMap[pt.y][pt.x]
                        cardMap[pt.y][pt.x] = 0
                        changeResult = true
                        notifyMoveEvent(pt, Point(x, y))
                    } else {
                        // current line is empty, scan next line;
                        y = -1
                        break
                    }
                }
                // now current point is not empty, check if next none-empty point should merge.
                for (k in y - 1 downTo 0) {
                    if (cardMap[k][x] <= 0) {
                        continue
                    }
                    if (cardMap[k][x] == cardMap[y][x]) {
                        cardMap[y][x] = cardMap[y][x] * 2
                        cardMap[k][x] = 0
                        changeResult = true
                        notifyMergeEvent(Point(x, k), Point(x, y))
                    }
                    break
                }
                y--
            }
        }
        return changeResult
    }

    override fun onSwipeUp(): Boolean {
        var changeResult = false
        for (x in 0 until BOARD_DIMENSION) {
            var y = 0
            while (y < BOARD_DIMENSION) {

                // if current point is empty, find next none empty point, move to current point;
                if (cardMap[y][x] <= 0) {
                    var pt: Point? = null
                    for (k in y + 1 until BOARD_DIMENSION) {
                        if (cardMap[k][x] > 0) {
                            pt = Point(x, k)
                            break
                        }
                    }
                    if (pt != null) {
                        cardMap[y][x] = cardMap[pt.y][pt.x]
                        cardMap[pt.y][pt.x] = 0
                        changeResult = true
                        notifyMoveEvent(pt, Point(x, y))
                    } else {
                        // current line is empty, scan next line;
                        y = BOARD_DIMENSION
                        break
                    }
                }
                // now current point is not empty, check if next none-empty point should merge.
                for (k in y + 1 until BOARD_DIMENSION) {
                    if (cardMap[k][x] <= 0) {
                        continue
                    }
                    if (cardMap[k][x] == cardMap[y][x]) {
                        cardMap[y][x] = cardMap[y][x] * 2
                        cardMap[k][x] = 0
                        changeResult = true
                        notifyMergeEvent(Point(x, k), Point(x, y))
                    }
                    break
                }
                y++
            }
        }
        return changeResult
    }

    override fun onSwipeLeft(): Boolean {
        var changeResult = false
        for (y in 0 until BOARD_DIMENSION) {
            var x = 0
            while (x < BOARD_DIMENSION) {
                // if current point is empty, find next none empty point, move to current point;
                if (cardMap[y][x] <= 0) {
                    var pt: Point? = null
                    for (k in x + 1 until BOARD_DIMENSION) {
                        if (cardMap[y][k] > 0) {
                            pt = Point(k, y)
                            break
                        }
                    }
                    if (pt != null) {
                        cardMap[y][x] = cardMap[pt.y][pt.x]
                        cardMap[pt.y][pt.x] = 0
                        changeResult = true
                        notifyMoveEvent(pt, Point(x, y))
                    } else {
                        // current line is empty, scan next line;
                        x = BOARD_DIMENSION
                        break
                    }
                }
                // now current point is not empty, check if next none-empty point should merge.
                for (k in x + 1 until BOARD_DIMENSION) {
                    if (cardMap[y][k] <= 0) {
                        continue
                    }
                    if (cardMap[y][k] == cardMap[y][x]) {
                        cardMap[y][x] = cardMap[y][x] * 2
                        cardMap[y][k] = 0
                        changeResult = true
                        notifyMergeEvent(Point(k, y), Point(x, y))
                    }
                    break
                }
                x++
            }
        }
        return changeResult
    }

    override fun onSwipeRight(): Boolean {
        var changeResult = false
        for (y in 0 until BOARD_DIMENSION) {
            var x = BOARD_DIMENSION - 1
            while (x >= 0) {
                // if current point is empty, find next none empty point, move to current point;
                if (cardMap[y][x] <= 0) {
                    var pt: Point? = null
                    for (k in x - 1 downTo 0) {
                        if (cardMap[y][k] > 0) {
                            pt = Point(k, y)
                            break
                        }
                    }
                    if (pt != null) {
                        cardMap[y][x] = cardMap[pt.y][pt.x]
                        cardMap[pt.y][pt.x] = 0
                        changeResult = true
                        notifyMoveEvent(pt, Point(x, y))
                    } else {
                        // current line is empty, scan next line;
                        x = -1
                        break
                    }
                }
                // now current point is not empty, check if next none-empty point should merge.
                for (k in x - 1 downTo 0) {
                    if (cardMap[y][k] <= 0) {
                        continue
                    }
                    if (cardMap[y][k] == cardMap[y][x]) {
                        cardMap[y][x] = cardMap[y][x] * 2
                        cardMap[y][k] = 0
                        changeResult = true
                        notifyMergeEvent(Point(k, y), Point(x, y))
                    }
                    break
                }
                x--
            }
        }
        return changeResult
    }

    fun setBoardEventListener(boardEventListener: BoardEventListener?) {
        this.boardEventListener = boardEventListener
    }

    fun setCardMap(cardMap: Array<IntArray>) {
        this.cardMap = cardMap
    }

    private fun notifyMergeEvent(source: Point, sink: Point) {
        boardEventListener?.onCardMerged(source, sink,
                cardMap[source.y][source.x], cardMap[sink.y][sink.x])
    }

    private fun notifyMoveEvent(source: Point, sink: Point) {
        boardEventListener?.onCardMoved(source, sink,
                cardMap[source.y][source.x], cardMap[sink.y][sink.x])
    }
}