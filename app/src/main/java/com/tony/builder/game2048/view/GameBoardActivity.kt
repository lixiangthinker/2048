package com.tony.builder.game2048.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.tony.builder.game2048.R
import com.tony.builder.game2048.databinding.ActivityMainBinding
import com.tony.builder.game2048.model.BoardModel
import com.tony.builder.game2048.viewmodel.GameBoardViewModel
import com.tony.builder.game2048.viewmodel.GameBoardViewModelFactory
import com.tony.builder.game2048.viewmodel.event.MergeEvent
import com.tony.builder.game2048.viewmodel.event.MoveEvent
import com.tony.builder.game2048.viewmodel.event.NewCardEvent

class GameBoardActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "GameBoardActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: GameBoardViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val viewModelFactory = GameBoardViewModelFactory(BoardModel())
        viewModel = ViewModelProvider(this, viewModelFactory).get(GameBoardViewModel::class.java)
        binding.viewModel = viewModel
        subscribe(viewModel)
        registerMotionMonitor()
        viewModel.onStartGame()
    }

    private fun subscribe(model: GameBoardViewModel) {
        model.currentScore.observe(this, Observer { score: Int ->
            binding.tvScore.text = score.toString()
        })
        model.bestScore.observe(this, Observer { best: Int ->
            binding.tvBest.text = best.toString()
        })
        model.gameFinishedFlag.observe(this, Observer { flag: Boolean ->
            if (flag) {
                showSimpleDialog()
            }
        })
        val cards: Array<Array<MutableLiveData<Int>>> = model.cards
        for (y in 0 until BoardModel.BOARD_DIMENSION) {
            for (x in 0 until BoardModel.BOARD_DIMENSION) {
                val card = cards[y][x]
                card.observe(this, CardObserver(x, y))
            }
        }
        model.newCardEvent.observe(this, Observer { cardGenEvent: NewCardEvent ->
            binding.boardView.onNewCard(cardGenEvent.position.x, cardGenEvent.position.y, cardGenEvent.value)
        })
        model.mergeEvent.observe(this, Observer { mergeEvent: MergeEvent ->
            Log.d(TAG, "onMergeEvent $mergeEvent")
            binding.boardView.onNewCard(mergeEvent.source.x, mergeEvent.source.y, mergeEvent.sourceValue)
            //source move to sink
            binding.boardView.onMoveCards(mergeEvent.source, mergeEvent.sink)
            binding.boardView.onNewCard(mergeEvent.sink.x, mergeEvent.sink.y, mergeEvent.sinkValue)
        })
        model.moveEvent.observe(this, Observer { moveEvent: MoveEvent ->
            Log.d(TAG, "onMergeEvent $moveEvent")
            binding.boardView.onNewCard(moveEvent.source.x, moveEvent.source.y, moveEvent.sourceValue)
            //source move to sink
            binding.boardView.onMoveCards(moveEvent.source, moveEvent.sink)
            binding.boardView.onNewCard(moveEvent.sink.x, moveEvent.sink.y, moveEvent.sinkValue)
        })
    }

    private inner class CardObserver internal constructor(private val x: Int, private val y: Int)
        : Observer<Int> {
        override fun onChanged(value: Int) {
            binding.boardView.onNewCard(x, y, value)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun registerMotionMonitor() {
        val detector = GestureDetectorCompat(this,
                object : SimpleOnGestureListener() {
                    override fun onDown(event: MotionEvent): Boolean {
                        return true
                    }

                    override fun onFling(event1: MotionEvent, event2: MotionEvent,
                                         velocityX: Float, velocityY: Float): Boolean {
                        viewModel.onFling(velocityX, velocityY)
                        return true
                    }
                })
        binding.boardView.setOnTouchListener { _, event -> detector.onTouchEvent(event) }
    }

    private fun showSimpleDialog() {
        AlertDialog.Builder(this).apply {
            setIcon(R.drawable.ic_launcher)
            setTitle("Sorry")
            setMessage("Game finished!")
            setCancelable(false)
            setPositiveButton("Restart") { _, _ -> viewModel.onStartGame() }
            setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(this@GameBoardActivity,
                                "Press New Game button to continue.",
                                Toast.LENGTH_LONG).show()
            }
        }.create().show()
    }
}