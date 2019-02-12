package com.tony.builder.game2048.viewmodel;

import android.util.Log;

import com.tony.builder.game2048.model.BoardModel;
import com.tony.builder.game2048.model.Point;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GameBoardViewmodel extends ViewModel {
    private static final String TAG = "GameBoardViewmodel";
    private MutableLiveData<Integer> mScore;
    private MutableLiveData<Integer> mBest;

    private MutableLiveData<Integer>[][] mCards;
    private BoardModel boardModel;

    private MutableLiveData<Boolean> isGameFinished;

    private MutableLiveData<MergeEvent> mMergeEvent;
    private MutableLiveData<MoveEvent> mMoveEvent;

    public void setBoardModel(BoardModel boardModel) {
        this.boardModel = boardModel;
        boardModel.setBoardEventListener(new BoardModel.BoardEventListener() {
            @Override
            public void onBoardReset(int boardDimension) {
                Log.d(TAG, "onBoardReset");
                if (mScore != null) {
                    mScore.postValue(0);
                }
                if (isGameFinished != null) {
                    isGameFinished.postValue(false);
                }
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

                if (mMergeEvent != null) {
                    mMergeEvent.setValue(new MergeEvent(source, sink, sourceValue, sinkValue));
                }

                if (mScore != null) {
                    // merge will add to score, but move will not.
                    Log.d(TAG, "mScore.getValue() = " + mScore.getValue() + " sinkvalue = " + sinkValue);
                    // need to ensure currently is on main thread.
                    mScore.setValue(mScore.getValue() + sinkValue);
                }
            }

            @Override
            public void onCardMoved(Point source, Point sink, int sourceValue, int sinkValue) {
                Log.d(TAG, "onCardMoved ["+source.x+","+source.y+"] = "+sourceValue+
                        " -> ["+sink.x+","+sink.y+"] = "+sinkValue);
//                if (mCards != null) {
//                    mCards[source.y][source.x].postValue(sourceValue);
//                    mCards[sink.y][sink.x].postValue(sinkValue);
//                }

                if (mMoveEvent != null) {
                    mMoveEvent.setValue(new MoveEvent(source, sink, sourceValue, sinkValue));
                }
            }


            @Override
            public void onGameFinished() {
                Log.d(TAG, "onGameFinished");
                if (isGameFinished != null) {
                    isGameFinished.postValue(true);
                }
                // TODO: save best score;
            }
        });
    }

    public LiveData<Boolean> getGameFinishedFlag() {
        if (isGameFinished == null) {
            isGameFinished = new MutableLiveData<>();
        }
        return isGameFinished;
    }

    public LiveData<MergeEvent> getMergeEvent() {
        if (mMergeEvent == null) {
            mMergeEvent = new MutableLiveData<>();
        }
        return mMergeEvent;
    }

    public LiveData<MoveEvent> getMoveEvent() {
        if (mMoveEvent == null) {
            mMoveEvent = new MutableLiveData<>();
        }
        return mMoveEvent;
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
        return mBest;
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
                Log.d(TAG, "boardModel.handleMotion(BoardModel.GameMotion.SWIPE_DOWN);");
                boardModel.handleMotion(BoardModel.GameMotion.SWIPE_DOWN);
            } else {
                Log.d(TAG, "boardModel.handleMotion(BoardModel.GameMotion.SWIPE_UP);");
                boardModel.handleMotion(BoardModel.GameMotion.SWIPE_UP);
            }
        } else {
            if (velocityX > 0) {
                Log.d(TAG, "boardModel.handleMotion(BoardModel.GameMotion.SWIPE_RIGHT);");
                boardModel.handleMotion(BoardModel.GameMotion.SWIPE_RIGHT);
            } else {
                Log.d(TAG, "boardModel.handleMotion(BoardModel.GameMotion.SWIPE_LEFT);");
                boardModel.handleMotion(BoardModel.GameMotion.SWIPE_LEFT);
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

    public class MergeEvent {
        public Point source;
        public Point sink;
        public int sourceValue;
        public int sinkValue;
        public MergeEvent(Point source, Point sink, int sourceValue, int sinkValue) {
            this.source = source;
            this.sink = sink;
            this.sourceValue = sourceValue;
            this.sinkValue = sinkValue;
        }

        @NonNull
        @Override
        public String toString() {
            String result = "["+source.x+","+source.y+"] -> ["+sink.x+","+sink.y+"], "+sourceValue+"->"+sinkValue;
            return result;
        }
    }

    public class MoveEvent extends MergeEvent{
        public MoveEvent(Point source, Point sink, int sourceValue, int sinkValue) {
            super(source, sink, sourceValue, sinkValue);
        }
    }
}
