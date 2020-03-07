package com.tony.builder.game2048.view;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import dagger.android.support.DaggerAppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tony.builder.game2048.R;
import com.tony.builder.game2048.viewmodel.GameBoardViewModel;
import com.tony.kotlin.libboardview.BoardView;

import javax.inject.Inject;

public class GameBoardActivity extends DaggerAppCompatActivity {
    private static final String TAG = "GameBoardActivity";
    TextView tvScore;
    TextView tvBest;
    Button btnNewGame;
    BoardView boardView;

    private GestureDetectorCompat mDetector;
    ConstraintLayout boardContainer;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    GameBoardViewModel viewModel;

    @Inject
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
        boardView = findViewById(R.id.boardView);
        tvScore = findViewById(R.id.tvScore);
        tvBest = findViewById(R.id.tvBest);
        btnNewGame = findViewById(R.id.btnNewgame);
        boardContainer = findViewById(R.id.boardContainer);

        viewModel = new ViewModelProvider(this, viewModelFactory).get(GameBoardViewModel.class);
        subscribe(viewModel);
        registerMotionMonitor();
        viewModel.onStartGame();

        btnNewGame.setOnClickListener(v -> viewModel.onStartGame());
    }

    private void subscribe(GameBoardViewModel model) {
        model.getScore().observe(this, score -> tvScore.setText(String.valueOf(score)));
        model.getBest().observe(this, best -> tvBest.setText(String.valueOf(best)));
        model.getGameFinishedFlag().observe(this, flag -> {
            if (flag) {
                showSimpleDialog();
            }
        });
        LiveData<Integer>[][] cards = model.getCards();
        for (int y = 0; y < cards.length; y++) {
            for (int x = 0; x < cards[y].length; x++) {
                LiveData<Integer> card = cards[y][x];
                card.observe(this, new CardObserver(x, y));
            }
        }
        model.getCardGenEvent().observe(this, cardGenEvent ->
                boardView.onNewCard(cardGenEvent.position.getX(), cardGenEvent.position.getY(), cardGenEvent.value));
        model.getMergeEvent().observe(this, mergeEvent -> {
            Log.d(TAG, "onMergeEvent " + mergeEvent);
            boardView.onNewCard(mergeEvent.source.getX(), mergeEvent.source.getY(), mergeEvent.sourceValue);
            //source move to sink
            boardView.onMoveCards(mergeEvent.source, mergeEvent.sink);
            boardView.onNewCard(mergeEvent.sink.getX(), mergeEvent.sink.getY(), mergeEvent.sinkValue);
        });

        model.getMoveEvent().observe(this, moveEvent -> {
            Log.d(TAG, "onMergeEvent " + moveEvent);
            boardView.onNewCard(moveEvent.source.getX(), moveEvent.source.getY(), moveEvent.sourceValue);
            //source move to sink
            boardView.onMoveCards(moveEvent.source, moveEvent.sink);
            boardView.onNewCard(moveEvent.sink.getX(), moveEvent.sink.getY(), moveEvent.sinkValue);
        });
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
            boardView.onNewCard(x, y, value);
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
                        viewModel.onFling(velocityX, velocityY);
                        return true;
                    }
                });
        boardContainer.setOnTouchListener((v, event) -> mDetector.onTouchEvent(event));
    }

    private void showSimpleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("Sorry");
        builder.setMessage("Game finished!");

        builder.setPositiveButton("Restart", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                viewModel.onStartGame();
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
