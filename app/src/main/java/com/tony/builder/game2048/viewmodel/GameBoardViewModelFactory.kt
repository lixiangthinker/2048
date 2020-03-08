package com.tony.builder.game2048.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tony.builder.game2048.model.BoardModel

@Suppress("UNCHECKED_CAST")
class GameBoardViewModelFactory(private val boardModel: BoardModel)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameBoardViewModel::class.java)) {
            return GameBoardViewModel(boardModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}