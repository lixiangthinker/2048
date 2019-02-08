package com.tony.builder.game2048.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.tony.builder.game2048.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int BOARD_DIMENTION = 4;
    TextView[][] cards = new TextView[BOARD_DIMENTION][BOARD_DIMENTION];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE); //not working for AppCompatActivity
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
        initCardsView();
    }

    private void initCardsView() {
        int[][] cardIds = getCardId();
        for (int i = 0; i < BOARD_DIMENTION; i++) {
            for (int j = 0; j < BOARD_DIMENTION; j++) {
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
        for (int i = 0; i < BOARD_DIMENTION; i++) {
            for (int j = 0; j < BOARD_DIMENTION; j++) {
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
}
