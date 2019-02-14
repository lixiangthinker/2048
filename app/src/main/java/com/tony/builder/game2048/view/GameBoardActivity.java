package com.tony.builder.game2048.view;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.DaggerAppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tony.builder.game2048.R;
import com.tony.builder.game2048.model.Point;
import com.tony.builder.game2048.viewmodel.GameBoardViewModel;
import com.tony.builder.game2048.viewmodel.event.CardGenEvent;
import com.tony.builder.game2048.viewmodel.event.MergeEvent;
import com.tony.builder.game2048.viewmodel.event.MoveEvent;

import javax.inject.Inject;

public class GameBoardActivity extends DaggerAppCompatActivity {
    private static final String TAG = "GameBoardActivity";
    private static final int BOARD_DIMENSION = 4;
    TextView[][] tvCards = new TextView[BOARD_DIMENSION][BOARD_DIMENSION];
    TextView tvScore;
    TextView tvBest;
    Button btnNewGame;

    private GestureDetectorCompat mDetector;
    ConstraintLayout boardContainer;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    GameBoardViewModel viewmodel;

    @Inject
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
        initCardsView();
        tvScore = findViewById(R.id.tvScore);
        tvBest = findViewById(R.id.tvBest);
        btnNewGame = findViewById(R.id.btnNewgame);
        boardContainer = findViewById(R.id.boardContainer);

        viewmodel = ViewModelProviders.of(this, viewModelFactory).get(GameBoardViewModel.class);
        subscribe(viewmodel);
        registerMotionMonitor();
        viewmodel.onStartGame();

        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewmodel.onStartGame();
            }
        });
    }

    private void initCardsView() {
        int[][] cardIds = getCardId();
        for (int i = 0; i < BOARD_DIMENSION; i++) {
            for (int j = 0; j < BOARD_DIMENSION; j++) {
                tvCards[i][j] = findViewById(cardIds[i][j]);
                tvCards[i][j].setText("");
            }
        }
    }

    private int[][] getCardId() {
        int[][] result = new int[][]{
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        };
        for (int i = 0; i < BOARD_DIMENSION; i++) {
            for (int j = 0; j < BOARD_DIMENSION; j++) {
                String strCardID = "card" + i + j;
                result[i][j] = getId(strCardID);
            }
        }
        return result;
    }

    private int getId(String idName) {
        Resources resources = getResources();
        int resId = resources.getIdentifier(idName, "id", getPackageName());
        return resId;
    }

    private void subscribe(GameBoardViewModel model) {
        model.getScore().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer score) {
                tvScore.setText(String.valueOf(score));
            }
        });

        model.getBest().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer best) {
                tvBest.setText(String.valueOf(best));
            }
        });
        model.getGameFinishedFlag().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean flag) {
                if (flag) {
                    showSimpleDialog();
                }
            }
        });
        LiveData<Integer>[][] cards = model.getCards();
        for (int y = 0; y < cards.length; y++) {
            for (int x = 0; x < cards[y].length; x++) {
                LiveData<Integer> card = cards[y][x];
                card.observe(this, new CardObserver(x, y));
            }
        }
        model.getCardGenEvent().observe(this, new Observer<CardGenEvent>() {
            @Override
            public void onChanged(CardGenEvent cardGenEvent) {
                generateCards(cardGenEvent.position.x, cardGenEvent.position.y, cardGenEvent.value);
            }
        });
        model.getMergeEvent().observe(this, new Observer<MergeEvent>() {
            @Override
            public void onChanged(MergeEvent mergeEvent) {
                Log.d(TAG, "onMergeEvent " + mergeEvent);
                generateCards(mergeEvent.source.x, mergeEvent.source.y, mergeEvent.sourceValue);
                //source move to sink
                moveCards(mergeEvent.source, mergeEvent.sink);
                generateCards(mergeEvent.sink.x, mergeEvent.sink.y, mergeEvent.sinkValue);
            }
        });

        model.getMoveEvent().observe(this, new Observer<MoveEvent>() {
            @Override
            public void onChanged(MoveEvent moveEvent) {
                Log.d(TAG, "onMergeEvent " + moveEvent);
                generateCards(moveEvent.source.x, moveEvent.source.y, moveEvent.sourceValue);
                //source move to sink
                moveCards(moveEvent.source, moveEvent.sink);
                generateCards(moveEvent.sink.x, moveEvent.sink.y, moveEvent.sinkValue);
            }
        });
    }

    private void moveCards(Point source, Point sink) {
        TextView tvSource = tvCards[source.y][source.x];
        TextView tvSink = tvCards[sink.y][sink.x];

        Log.d(TAG, "source.position = ["+tvSource.getX()+","+tvSource.getY()+"]");
        Log.d(TAG, "sink.position = ["+tvSink.getX()+","+tvSink.getY()+"]");

        TranslateAnimation anim = getCardMoveAnim(tvSource.getX()-tvSink.getX(), 0,
                tvSource.getY()-tvSink.getY(), 0);
        tvSink.bringToFront();
        tvSink.startAnimation(anim);
    }

    private class CardObserver implements Observer<Integer> {
        private int x;
        private int y;

        CardObserver(int positionX, int positionY) {
            x = positionX;
            y = positionY;
        }

        @Override
        public void onChanged(Integer value) {
            generateCards(x, y, value);
        }
    }

    private void generateCards(int x, int y, int value) {
        TextView card = tvCards[y][x];
        GradientDrawable bg = (GradientDrawable) card.getBackground();
        bg.setColor(getResources().getColor(getColorId(value), null));
        card.setBackground(bg);
        String currentCard = String.valueOf(card.getText());
        if (value == 0) {
            card.setText("");
        } else {
            card.setText(String.valueOf(value));
            if (!"".equals(currentCard)) {
                card.startAnimation(getCardGenerateAnim());
            }
        }
    }

    private int getColorId(Integer value) {
        int result = R.color.card_0;
        if (value <= 0) {
            result = R.color.card_0;
        } else if (value == 2) {
            result = R.color.card_2;
        } else if (value == 4) {
            result = R.color.card_4;
        } else if (value == 8) {
            result = R.color.card_8;
        } else if (value == 16) {
            result = R.color.card_16;
        } else if (value == 32) {
            result = R.color.card_32;
        } else if (value == 64) {
            result = R.color.card_64;
        } else if (value == 128) {
            result = R.color.card_128;
        } else if (value == 256) {
            result = R.color.card_256;
        } else if (value == 512) {
            result = R.color.card_512;
        } else if (value == 1024) {
            result = R.color.card_1024;
        } else if (value >= 2048) {
            result = R.color.card_2048;
        }
        return result;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void registerMotionMonitor() {
        mDetector = new GestureDetectorCompat(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDown(MotionEvent event) {
                        return true;
                    }

                    @Override
                    public boolean onFling(MotionEvent event1, MotionEvent event2,
                                           float velocityX, float velocityY) {
                        viewmodel.onFling(velocityX, velocityY);
                        return true;
                    }
                });
        boardContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mDetector.onTouchEvent(event);
            }
        });
    }

    private void showSimpleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("Sorry");
        builder.setMessage("Game finished!");

        builder.setPositiveButton("Restart", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                viewmodel.onStartGame();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(GameBoardActivity.this, "Press New Game button to continue.", Toast.LENGTH_LONG).show();
            }
        });

        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private ScaleAnimation getCardGenerateAnim() {
        ScaleAnimation scaleAnim = new ScaleAnimation(0, 1, 0, 1,        //从无缩放大到正常大小
                Animation.RELATIVE_TO_SELF, 0.5F,                                       //以view自身中点为轴中心
                Animation.RELATIVE_TO_SELF, 0.5F);
        //scaleAnim.setInterpolator(new BounceInterpolator());
        scaleAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleAnim.setDuration(200);                                                         //持续1s
        scaleAnim.setFillAfter(true);                                       //保持缩放之后的效果
        return scaleAnim;
    }

    private TranslateAnimation getCardMoveAnim(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta) {
        Log.d(TAG, "x "+fromXDelta+"->"+toXDelta+" , y "+fromYDelta+"->"+toYDelta+" ");
        TranslateAnimation translateAnimation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimation.setDuration(100);
        translateAnimation.setFillAfter(true);
        return translateAnimation;
    }
}
