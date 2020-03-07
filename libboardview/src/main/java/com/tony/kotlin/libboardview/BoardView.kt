package com.tony.kotlin.libboardview

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class BoardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {
    private lateinit var tvCards: Array<Array<TextView>>

    companion object {
        private const val TAG = "BoardView"
        private const val BOARD_DIMENSION = 4
    }

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.layout_board_view, this)
        initCardsView()
    }

    private fun initCardsView() {
        val cardIds = getCardId()
        tvCards = Array(BOARD_DIMENSION) {i ->
          Array(BOARD_DIMENSION) {j ->
              findViewById<TextView>(cardIds[i][j])
          }
        }
    }

    /**
     * API: generate a new card
     */
    fun onNewCard(x: Int, y: Int, value: Int) {
        val card = tvCards[y][x]
        val bg = card.background as GradientDrawable
        bg.setColor(resources.getColor(getColorId(value), null))
        card.background = bg
        val currentCard = card.text.toString()
        if (value == 0) {
            card.text = ""
        } else {
            card.text = value.toString()
            if ("" != currentCard) {
                card.startAnimation(getCardGenerateAnim())
            }
        }
    }

    /**
     * API: move a card from source to sink.
     */
    fun onMoveCards(source: Point, sink: Point) {
        val tvSource = tvCards[source.y][source.x]
        val tvSink = tvCards[sink.y][sink.x]
        Log.d(TAG, "source.position = [${tvSource.x} ${tvSource.y} ]")
        Log.d(TAG, "sink.position = [${tvSink.x} ${tvSink.y} ]")
        val anim = getCardMoveAnim(tvSource.x - tvSink.x, 0f,
                tvSource.y - tvSink.y, 0f)
        tvSink.bringToFront()
        tvSink.startAnimation(anim)
    }

    private fun getCardId(): Array<Array<Int>> {
        return Array(BOARD_DIMENSION) {i ->
            Array(BOARD_DIMENSION) {j ->
                getId("card$i$j")
            }
        }
    }

    private fun getId(idName: String): Int {
        val resources = resources
        return resources.getIdentifier(idName, "id", context.packageName)
    }

    private fun getColorId(value: Int): Int {
        return when {
            value <= 0 -> R.color.card_0
            value == 2 -> R.color.card_2
            value == 4 -> R.color.card_4
            value == 8 -> R.color.card_8
            value == 16 -> R.color.card_16
            value == 32 -> R.color.card_32
            value == 64 -> R.color.card_64
            value == 128 -> R.color.card_128
            value == 256 -> R.color.card_256
            value == 512 -> R.color.card_512
            value == 1024 -> R.color.card_1024
            value == 2048 -> R.color.card_2048
            else -> R.color.card_0
        }
    }

    private fun getCardGenerateAnim(): ScaleAnimation {
        val scaleAnim = ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f)
        scaleAnim.interpolator = AccelerateDecelerateInterpolator()
        scaleAnim.duration = 200
        scaleAnim.fillAfter = true
        return scaleAnim
    }

    private fun getCardMoveAnim(fromXDelta: Float, toXDelta: Float, fromYDelta: Float, toYDelta: Float)
            : TranslateAnimation {
        Log.d(TAG, "x $fromXDelta->$toXDelta , y $fromYDelta->$toYDelta ")
        val translateAnimation = TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta)
        translateAnimation.interpolator = AccelerateDecelerateInterpolator()
        translateAnimation.duration = 100
        translateAnimation.fillAfter = true
        return translateAnimation
    }
}