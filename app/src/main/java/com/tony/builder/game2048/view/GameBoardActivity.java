package com.tony.builder.game2048.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tony.builder.game2048.R;
import com.tony.builder.game2048.viewmodel.GameBoardViewmodel;

public class GameBoardActivity extends AppCompatActivity {
    private static final String TAG = "GameBoardActivity";
    private static final int BOARD_DIMENSION = 4;
    TextView[][] cards = new TextView[BOARD_DIMENSION][BOARD_DIMENSION];
    TextView tvScore;
    TextView tvBest;
    Button btnNewGame;

    private GestureDetectorCompat mDetector;
    ConstraintLayout boardContainer;

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

        GameBoardViewmodel viewmodel = ViewModelProviders.of(this).get(GameBoardViewmodel.class);
        subscribe(viewmodel);
        registerMotionMonitor();
    }

    private void initCardsView() {
        int[][] cardIds = getCardId();
        for (int i = 0; i < BOARD_DIMENSION; i++) {
            for (int j = 0; j < BOARD_DIMENSION; j++) {
                cards[i][j] = findViewById(cardIds[i][j]);
                //Log.d(TAG, "cards["+i+"]["+j+"] = " + cards[i][j].getText());
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
                //Log.d(TAG, "result["+i+"]["+j+"] = " + result[i][j]);
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
        model.getBoard().observe(this, new Observer<Integer[][]>() {
            @Override
            public void onChanged(Integer[][] integers) {
                // TODO: change board text.
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void registerMotionMonitor() {
        mDetector = new GestureDetectorCompat(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDown(MotionEvent event) {
                        Log.d(TAG,"onDown: " + event.toString());
                        return true;
                    }

                    @Override
                    public boolean onFling(MotionEvent event1, MotionEvent event2,
                                           float velocityX, float velocityY) {
                        Log.d(TAG, "onFling: " + event1.toString() + event2.toString() + " Vx = " + velocityX + " Vy = " + velocityY);
                        handleFling(velocityX, velocityY);
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

    private void handleFling(float velocityX, float velocityY) {

    }
}
