package com.tony.builder.game2048.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import com.tony.builder.game2048.R;
import com.tony.builder.game2048.databinding.ActivityMainBinding;
import com.tony.builder.game2048.model.BoardModel;
import com.tony.builder.game2048.util.AppExecutors;
import com.tony.builder.game2048.viewmodel.GameBoardViewModel;
import com.tony.builder.game2048.viewmodel.GameBoardViewModelFactory;

public class GameBoardActivity extends AppCompatActivity {
    private static final String TAG = "GameBoardActivity";
    ActivityMainBinding binding;

    private GestureDetectorCompat mDetector;
    GameBoardViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        GameBoardViewModelFactory viewModelFactory =
                new GameBoardViewModelFactory(new AppExecutors(), new BoardModel());
        viewModel = new ViewModelProvider(this, viewModelFactory).get(GameBoardViewModel.class);
        binding.setViewModel(viewModel);
        subscribe(viewModel);
        registerMotionMonitor();
        viewModel.onStartGame();
    }

    private void subscribe(GameBoardViewModel model) {
        model.getScore().observe(this, score -> binding.tvScore.setText(String.valueOf(score)));
        model.getBest().observe(this, best -> binding.tvBest.setText(String.valueOf(best)));
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
                binding.boardView.onNewCard(cardGenEvent.position.getX(), cardGenEvent.position.getY(), cardGenEvent.value));
        model.getMergeEvent().observe(this, mergeEvent -> {
            Log.d(TAG, "onMergeEvent " + mergeEvent);
            binding.boardView.onNewCard(mergeEvent.source.getX(), mergeEvent.source.getY(), mergeEvent.sourceValue);
            //source move to sink
            binding.boardView.onMoveCards(mergeEvent.source, mergeEvent.sink);
            binding.boardView.onNewCard(mergeEvent.sink.getX(), mergeEvent.sink.getY(), mergeEvent.sinkValue);
        });

        model.getMoveEvent().observe(this, moveEvent -> {
            Log.d(TAG, "onMergeEvent " + moveEvent);
            binding.boardView.onNewCard(moveEvent.source.getX(), moveEvent.source.getY(), moveEvent.sourceValue);
            //source move to sink
            binding.boardView.onMoveCards(moveEvent.source, moveEvent.sink);
            binding.boardView.onNewCard(moveEvent.sink.getX(), moveEvent.sink.getY(), moveEvent.sinkValue);
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
            binding.boardView.onNewCard(x, y, value);
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
        binding.boardView.setOnTouchListener((v, event) -> mDetector.onTouchEvent(event));
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
