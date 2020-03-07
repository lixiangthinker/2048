package com.tony.builder.game2048.viewmodel;

import android.content.SharedPreferences;
import android.util.Log;

import com.tony.builder.game2048.model.BoardModel;
import com.tony.builder.game2048.util.AppExecutors;
import com.tony.builder.game2048.viewmodel.event.CardGenEvent;
import com.tony.builder.game2048.viewmodel.event.MergeEvent;
import com.tony.builder.game2048.viewmodel.event.MoveEvent;
import com.tony.kotlin.libboardview.Point;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GameBoardViewModel extends ViewModel {
    private static final String TAG = "GameBoardViewModel";
    private MutableLiveData<Integer> mScore;
    private static final String KEY_BEST_SCORE = "best_score";
    private MutableLiveData<Integer> mBest;

    private MutableLiveData<Integer>[][] mCards;
    private BoardModel boardModel;

    private MutableLiveData<Boolean> isGameFinished;

    private MutableLiveData<MergeEvent> mMergeEvent;
    private MutableLiveData<MoveEvent> mMoveEvent;
    private MutableLiveData<CardGenEvent> mCardGenEvent;

    private AppExecutors executors;
    private SharedPreferences sharedPreferences;

    @Inject
    public GameBoardViewModel(AppExecutors executors, BoardModel boardModel, SharedPreferences sharedPreferences) {
        this.executors = executors;
        this.sharedPreferences = sharedPreferences;
        setBoardModel(boardModel);
    }

    private void setBoardModel(BoardModel boardModel) {
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
                Log.d(TAG, "onCardGenerated ["+pt.getX()+","+pt.getY()+"] = "+value);
                if (mCardGenEvent != null) {
                    mCardGenEvent.postValue(new CardGenEvent(pt, value));
                    mCards[pt.getY()][pt.getY()].postValue(value);
                }
            }

            @Override
            public void onCardMerged(final Point source, final Point sink, final int sourceValue, final int sinkValue) {
                executors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "onCardMerged ["+source.getX()+","+source.getY()+"] = "+sourceValue+
                                " -> ["+sink.getX()+","+sink.getY()+"] = "+sinkValue);

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
                });
            }

            @Override
            public void onCardMoved(final Point source, final Point sink, final int sourceValue, final int sinkValue) {
                executors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "onCardMoved ["+source.getX()+","+source.getY()+"] = "+sourceValue+
                                " -> ["+sink.getX()+","+sink.getY()+"] = "+sinkValue);

                        if (mMoveEvent != null) {
                            mMoveEvent.setValue(new MoveEvent(source, sink, sourceValue, sinkValue));
                        }
                    }
                });
            }

            @Override
            public void onGameFinished() {
                Log.d(TAG, "onGameFinished");
                if (isGameFinished != null) {
                    isGameFinished.postValue(true);
                }
                updateBestScore();
            }
        });
    }

    private void updateBestScore() {
        if (mBest == null || mScore == null) {
            Log.e(TAG, "could not update best score");
            return;
        }
        int best = mBest.getValue();
        int score = mScore.getValue();
        if (best >= score) {
            Log.i(TAG, "do not need to update score");
            return;
        }
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(KEY_BEST_SCORE, score);
            editor.apply();
        }
        mBest.postValue(score);
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

    public LiveData<CardGenEvent> getCardGenEvent() {
        if (mCardGenEvent == null) {
            mCardGenEvent = new MutableLiveData<>();
        }
        return mCardGenEvent;
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
        if (sharedPreferences != null) {
            int best = sharedPreferences.getInt(KEY_BEST_SCORE, 0);
            mBest.postValue(best);
        }
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

    public void onFling(final float velocityX, final float velocityY) {
        executors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
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
        });
    }

    public void onStartGame() {
        Log.d(TAG, "onStartGame");
        if (boardModel == null) {
            return;
        }
        executors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                boardModel.startGame();
            }
        });
    }
}
