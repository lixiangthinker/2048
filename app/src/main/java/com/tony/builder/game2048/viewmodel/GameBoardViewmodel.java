package com.tony.builder.game2048.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GameBoardViewmodel extends ViewModel {
    private MutableLiveData<Integer> mScore;
    private MutableLiveData<Integer> mBest;
    private MutableLiveData<Integer[][]> mBoard;

    public LiveData<Integer> getScore() {
        if (mScore == null) {
            mScore = new MutableLiveData<>();
            loadScore();
        }
        return mScore;
    }

    private void loadScore() {
        // TODO: get score from shared preference
    }

    public LiveData<Integer> getBest() {
        if (mBest == null) {
            mBest = new MutableLiveData<>();
            loadBest();
        }
        return mScore;
    }

    private void loadBest() {
        // TODO: get score from shared preference
    }

    public LiveData<Integer[][]> getBoard() {
        if (mBoard == null) {
            mBoard = new MutableLiveData<>();
            loadBoard();
        }
        return mBoard;
    }

    private void loadBoard() {
        // TODO: get score from shared preference
    }
}
