package com.tony.builder.game2048.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tony.builder.game2048.R;
import com.tony.builder.game2048.model.BoardModel;
import com.tony.builder.game2048.viewmodel.GameBoardViewmodel;

public class GameBoardActivity extends AppCompatActivity {
    private static final String TAG = "GameBoardActivity";
    private static final int BOARD_DIMENSION = 4;
    TextView[][] tvCards = new TextView[BOARD_DIMENSION][BOARD_DIMENSION];
    TextView tvScore;
    TextView tvBest;
    Button btnNewGame;

    private GestureDetectorCompat mDetector;
    ConstraintLayout boardContainer;
    GameBoardViewmodel viewmodel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE); //not working for AppCompatActivity
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
        initCardsView();
        tvScore = findViewById(R.id.tvScore);
        tvBest = findViewById(R.id.tvBest);
        btnNewGame = findViewById(R.id.btnNewgame);
        boardContainer = findViewById(R.id.boardContainer);

        viewmodel = ViewModelProviders.of(this).get(GameBoardViewmodel.class);
        viewmodel.setBoardModel(new BoardModel());
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

    private void subscribe(GameBoardViewmodel model) {
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
        for (int i = 0; i < cards.length; i++) {
            for (int j = 0; j < cards[i].length; j++) {
                LiveData<Integer> card = cards[i][j];
                card.observe(this, new CardObserver(i,j));
            }
        }
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
            TextView card = tvCards[x][y];
            card.setBackgroundColor(getResources().getColor(getColorId(value), null));
            if (value == 0) {
                card.setText("");
            } else {
                card.setText(String.valueOf(value));
            }
        }

        private int getColorId(Integer value) {
            int result = R.color.card_0;
            if (value <=0) {
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

        //监听下方button点击事件
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
}
