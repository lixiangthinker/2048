package com.tony.builder.game2048.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tony.builder.game2048.model.BoardModel
import com.tony.builder.game2048.model.BoardModel.BoardEventListener
import com.tony.builder.game2048.viewmodel.event.NewCardEvent
import com.tony.builder.game2048.viewmodel.event.MergeEvent
import com.tony.builder.game2048.viewmodel.event.MoveEvent
import com.tony.kotlin.libboardview.Point
import kotlin.math.abs
import kotlin.math.max

class GameBoardViewModel(private val boardModel: BoardModel) : ViewModel() {
    companion object {
        private const val TAG = "GameBoardViewModel"
    }
    private var _currentScore = MutableLiveData<Int>()
    private var _bestScore = MutableLiveData<Int>()
    private var _cards: Array<Array<MutableLiveData<Int>>> = Array(BoardModel.BOARD_DIMENSION) {
        Array(BoardModel.BOARD_DIMENSION) {
            MutableLiveData<Int>()
        }
    }
    private var _gameFinished = MutableLiveData<Boolean>()
    private var _mergeEvent = MutableLiveData<MergeEvent>()
    private var _moveEvent = MutableLiveData<MoveEvent>()
    private var _newCardEvent = MutableLiveData<NewCardEvent>()

    val gameFinishedFlag: LiveData<Boolean>
        get() = _gameFinished

    val mergeEvent: LiveData<MergeEvent>
        get() = _mergeEvent

    val moveEvent: LiveData<MoveEvent>
        get() = _moveEvent

    val newCardEvent: LiveData<NewCardEvent>
        get() = _newCardEvent

    val currentScore: LiveData<Int>
        get() = _currentScore

    val bestScore: LiveData<Int>
        get() = _bestScore

    val cards: Array<Array<MutableLiveData<Int>>>
        get() = _cards

    init {
        boardModel.setBoardEventListener(object : BoardEventListener {
            override fun onBoardReset(boardDimension: Int) {
                Log.d(TAG, "onBoardReset")
                _currentScore.value = 0
                // TODO: update best score from shared preference.
                _bestScore.value = 0
                _gameFinished.value = false
                for (i in 0 until BoardModel.BOARD_DIMENSION) {
                    for (j in 0 until BoardModel.BOARD_DIMENSION) {
                        _cards[i][j].value = 0
                    }
                }
            }

            override fun onCardGenerated(pt: Point, value: Int) {
                Log.d(TAG, "onCardGenerated [" + pt.x + "," + pt.y + "] = " + value)
                _newCardEvent.value = NewCardEvent(pt, value)
                _cards[pt.y][pt.y].value = value
            }

            override fun onCardMerged(source: Point, sink: Point, sourceValue: Int, sinkValue: Int) {
                Log.d(TAG, "onCardMerged [" + source.x + "," + source.y + "] = " + sourceValue +
                        " -> [" + sink.x + "," + sink.y + "] = " + sinkValue)
                _mergeEvent.value = MergeEvent(source, sink, sourceValue, sinkValue)
                // merge will add to score, but move will not.
                Log.d(TAG, "mScore.getValue() = " + _currentScore.value + " sinkvalue = " + sinkValue)
                // need to ensure currently is on main thread.
                _currentScore.value = _currentScore.value!! + sinkValue
            }

            override fun onCardMoved(source: Point, sink: Point, sourceValue: Int, sinkValue: Int) {
                Log.d(TAG, "onCardMoved [" + source.x + "," + source.y + "] = " + sourceValue +
                        " -> [" + sink.x + "," + sink.y + "] = " + sinkValue)
                _moveEvent.value = MoveEvent(source, sink, sourceValue, sinkValue)
            }

            override fun onGameFinished() {
                Log.d(TAG, "onGameFinished")
                _gameFinished.postValue(true)
                updateBestScore()
            }
        })
    }

    private fun updateBestScore() {
        _bestScore.value = max(_bestScore.value!!, _currentScore.value!!)
    }

    private fun loadScore() {
        // TODO: get score from shared preference
    }

    private fun loadBest() {
//        if (sharedPreferences != null) {
//            int best = sharedPreferences.getInt(KEY_BEST_SCORE, 0);
//            mBest.postValue(best);
//        }
    }

    private fun loadCards() {
        // TODO: get score from shared preference
    }

    fun onFling(velocityX: Float, velocityY: Float) {
        Log.d(TAG, "onFling velocityX = $velocityX, velocityY = $velocityY")
        if (abs(velocityY) > abs(velocityX)) {
            if (velocityY > 0) {
                Log.d(TAG, "boardModel.handleMotion(BoardModel.GameMotion.SWIPE_DOWN);")
                boardModel.handleMotion(BoardModel.GameMotion.SWIPE_DOWN)
            } else {
                Log.d(TAG, "boardModel.handleMotion(BoardModel.GameMotion.SWIPE_UP);")
                boardModel.handleMotion(BoardModel.GameMotion.SWIPE_UP)
            }
        } else {
            if (velocityX > 0) {
                Log.d(TAG, "boardModel.handleMotion(BoardModel.GameMotion.SWIPE_RIGHT);")
                boardModel.handleMotion(BoardModel.GameMotion.SWIPE_RIGHT)
            } else {
                Log.d(TAG, "boardModel.handleMotion(BoardModel.GameMotion.SWIPE_LEFT);")
                boardModel.handleMotion(BoardModel.GameMotion.SWIPE_LEFT)
            }
        }
    }

    fun onStartGame() {
        Log.d(TAG, "onStartGame")
        boardModel.startGame()
    }
}