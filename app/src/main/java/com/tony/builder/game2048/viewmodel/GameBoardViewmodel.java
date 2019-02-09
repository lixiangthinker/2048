package com.tony.builder.game2048.viewmodel;

import android.util.Log;

import com.tony.builder.game2048.model.BoardModel;
import com.tony.builder.game2048.model.Point;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GameBoardViewmodel extends ViewModel {
    private static final String TAG = "GameBoardViewmodel";
    private MutableLiveData<Integer> mScore;
    private MutableLiveData<Integer> mBest;

    private MutableLiveData<Integer>[][] mCards;
    private BoardModel boardModel;

    public void setBoardModel(BoardModel boardModel) {
        this.boardModel = boardModel;
        boardModel.setBoardEventListener(new BoardModel.BoardEventListener() {
            @Override
            public void onBoardReset(int boardDimension) {
                Log.d(TAG, "onBoardReset");
                if (mCards != null) {
                    for (int i = 0; i < mCards.length; i++) {
                        for (int j = 0; j < mCards[i].length; j++) {
                            mCards[i][j].postValue(0);
                        }
                    }
                }
            }

            @Override
            public void onCardGenerated(Point pt, int value) {
                Log.d(TAG, "onCardGenerated ["+pt.x+","+pt.y+"] = "+value);
                if (mCards != null) {
                    mCards[pt.y][pt.x].postValue(value);
                }
            }

            @Override
            public void onCardMerged(Point source, Point sink, int sourceValue, int sinkValue) {
                Log.d(TAG, "onCardMerged ["+source.x+","+source.y+"] = "+sourceValue+
                        " -> ["+sink.x+","+sink.y+"] = "+sinkValue);
                if (mCards != null) {
                    mCards[source.y][source.x].postValue(sourceValue);
                    mCards[sink.y][sink.x].postValue(sinkValue);
                }
            }

            @Override
            public void onGameFinished() {
                Log.d(TAG, "onGameFinished");
            }
        });
    }

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

    public LiveData<Integer>[][] getCards() {
        if (mCards == null) {
            mCards = new MutableLiveData[boardModel.BOARD_DIMENSION][boardModel.BOARD_DIMENSION];
            for (int i = 0; i < mCards.length; i++) {
                for (int j = 0; j < mCards[i].length; j++) {
                    mCards[i][j] = new MutableLiveData<>();
                }
            }
            loadCards();
        }
        return mCards;
    }

    private void loadCards() {
        // TODO: get score from shared preference
    }

    public void onFling(float velocityX, float velocityY) {
        Log.d(TAG, "onFling velocityX = "+velocityX+", velocityY = "+velocityY);
        if (boardModel == null) {
            return;
        }
        if (Math.abs(velocityY) > Math.abs(velocityX)) {
            if (velocityY > 0) {
                Log.d(TAG, "boardModel.onSwipeDown();");
                boardModel.onSwipeDown();
            } else {
                Log.d(TAG, "boardModel.onSwipeUp();");
                boardModel.onSwipeUp();
            }
        } else {
            if (velocityX > 0) {
                Log.d(TAG, "boardModel.onSwipeRight();");
                boardModel.onSwipeRight();
            } else {
                Log.d(TAG, "boardModel.onSwipeLeft();");
                boardModel.onSwipeLeft();
            }
        }
    }

    public void onStartGame() {
        Log.d(TAG, "onStartGame");
        if (boardModel == null) {
            return;
        }
        boardModel.startGame();
    }
}
