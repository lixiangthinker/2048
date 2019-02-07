package com.tony.builder.game2048.controller;

import java.util.ArrayList;
import java.util.List;

import com.tony.builder.game2048.GameMainActivity;
import com.tony.builder.game2048.GlobleSettings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;

public class GameLayout extends GridLayout {

    private static final String TAG = "GameLayout";
    private CardLayout cardMap[][] = new CardLayout[GlobleSettings.GAME_DIMENTION][GlobleSettings.GAME_DIMENTION];
    private List<Point> emptyPointList = new ArrayList<Point>();

    public GameLayout(Context context) {
        super(context);
        initGameLayout();
    }

    public GameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGameLayout();
    }

    public GameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initGameLayout();
    }

    private void initGameLayout() {
        this.setColumnCount(GlobleSettings.GAME_DIMENTION);
        this.setOnTouchListener(mOnTouchListener);
        setBackgroundColor(0xFFBBADA0);

        Display d = ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay();
        int cardWidth = (Math.min(d.getWidth(), d.getHeight()) - 10)
                / GlobleSettings.GAME_DIMENTION;
        initCards(cardWidth, cardWidth);
    }

    private void initCards(int width, int height) {
        for (int j = 0; j < GlobleSettings.GAME_DIMENTION; j++) {
            for (int i = 0; i < GlobleSettings.GAME_DIMENTION; i++) {
                CardLayout c = new CardLayout(getContext());
                c.setNumber(0);
                this.addView(c, width, height);
                this.cardMap[i][j] = c;
            }
        }

        genRandomCards();
        genRandomCards();
    }

    private void resetCards() {
        for (int j = 0; j < GlobleSettings.GAME_DIMENTION; j++) {
            for (int i = 0; i < GlobleSettings.GAME_DIMENTION; i++) {
                cardMap[i][j].setNumber(0);
            }
        }
        genRandomCards();
        genRandomCards();
    }

    private boolean genRandomCards() {
        emptyPointList.clear();

        for (int y = 0; y < GlobleSettings.GAME_DIMENTION; y++) {
            for (int x = 0; x < GlobleSettings.GAME_DIMENTION; x++) {
                if (cardMap[x][y].getNumber() <= 0) {
                    emptyPointList.add(new Point(x, y));
                }
            }
        }

        if (emptyPointList.size() == 0) {
            Log.i(TAG, "No space left!");
            checkGameResult();
            return false;
        }

        Point pt = emptyPointList.remove((int) (Math.random() * emptyPointList
                .size()));

        cardMap[pt.x][pt.y].setNumber(Math.random() > 0.9 ? 2 : 4);

        if (emptyPointList.size() == 0) {
            Log.i(TAG, "only 1 card generated!");
            checkGameResult();
            return true;
        }
        return true;
    }

    private void checkGameResult() {
        if (isGameFinished()) {
            new AlertDialog.Builder(getContext()).setTitle("Failed").setMessage("You have fialed this GAME!")
                    .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Log.i(TAG, "Restart a new game");
                            resetCards();
                            Handler h = GameMainActivity.getInstance().h;
                            h.sendEmptyMessage(GameMainActivity.EVENT_RESET_GAME);
                        }
                    }).setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    Log.i(TAG, "Quit this game");
                    Handler h = GameMainActivity.getInstance().h;
                    h.sendEmptyMessage(GameMainActivity.EVENT_FINISH_ACTIVITY);
                }
            }).show();
        }
    }

    /**
     * if each neighbor pair cards is different, the game should be finished.
     *
     * @return result if the game should finished.
     */
    private boolean isGameFinished() {
        for (int y = 0; y < GlobleSettings.GAME_DIMENTION; y++) {
            for (int x = 0; x < GlobleSettings.GAME_DIMENTION - 1; x++) {
                if (cardMap[x][y].equals(cardMap[x + 1][y])) {
                    return false;
                }

                if (cardMap[x][y].getNumber() <= 0) {
                    return false;
                }
            }
        }

        for (int x = 0; x < GlobleSettings.GAME_DIMENTION; x++) {
            for (int y = 0; y < GlobleSettings.GAME_DIMENTION - 1; y++) {
                if (cardMap[x][y].equals(cardMap[x][y + 1])) {
                    return false;
                }

                if (cardMap[x][y].getNumber() <= 0) {
                    return false;
                }
            }
        }

        return true;
    }

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        float x = 0;
        float y = 0;
        float deltaX = 0;
        float deltaY = 0;

        @Override
        public boolean onTouch(View view, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = event.getX();
                    y = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    deltaX = event.getX() - x;
                    deltaY = event.getY() - y;
                    int action = getUserActionFromPos(deltaX, deltaY);
                    if (action != EVENT_UNKOWN) {
                        h.sendEmptyMessage(action);
                    }
                    view.performClick();
                    break;
                default:
                    break;
            }
            return true;
        }

        /**
         * get users real action by calculation position changes
         *
         * @param deltaX
         * @param deltaY
         * @return
         */
        private int getUserActionFromPos(float deltaX, float deltaY) {
            int result = EVENT_UNKOWN;
            if (Math.abs(deltaX) < 5 || Math.abs(deltaY) < 5) {
                return result;
            }
            if (Math.abs(deltaX) >= Math.abs(deltaY)) {
                if (deltaX > 0) {
                    result = EVENT_SWIPE_RIGHT;
                } else {
                    result = EVENT_SWIPE_LEFT;
                }
            } else {
                if (deltaY > 0) {
                    result = EVENT_SWIPE_DOWN;
                } else {
                    result = EVENT_SWIPE_UP;
                }
            }
            return result;
        }
    };

    private static final int EVENT_UNKOWN = -1;
    private static final int EVENT_SWIPE_LEFT = 0;
    private static final int EVENT_SWIPE_RIGHT = 1;
    private static final int EVENT_SWIPE_UP = 2;
    private static final int EVENT_SWIPE_DOWN = 3;
    private static final int EVENT_ADD_SCORE = 4;

    private String getStringNameByEvent(int event) {
        String result = null;
        switch (event) {
            case EVENT_SWIPE_LEFT:
                result = "EVENT_SWIPE_LEFT";
                break;
            case EVENT_SWIPE_RIGHT:
                result = "EVENT_SWIPE_RIGHT";
                break;
            case EVENT_SWIPE_UP:
                result = "EVENT_SWIPE_UP";
                break;
            case EVENT_SWIPE_DOWN:
                result = "EVENT_SWIPE_DOWN";
                break;
            case EVENT_ADD_SCORE:
                result = "EVENT_ADD_SCORE";
                break;
            default:
                result = "EVENT_UNKOWN";
                break;
        }
        return result;
    }

    private Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "msg = " + getStringNameByEvent(msg.what) + " received.");
            boolean changeResult = false;
            switch (msg.what) {
                case EVENT_SWIPE_LEFT:
                    changeResult = handleLeftMotion();
                    break;
                case EVENT_SWIPE_RIGHT:
                    changeResult = hanleRightMotion();
                    break;
                case EVENT_SWIPE_UP:
                    changeResult = hanleUpMotion();
                    break;
                case EVENT_SWIPE_DOWN:
                    changeResult = hanleDownMotion();
                    break;
                case EVENT_ADD_SCORE:
                    Handler callBack = GameMainActivity.getInstance().h;
                    Message m = callBack.obtainMessage(GameMainActivity.EVENT_ADD_SCORE, msg.arg1, 0);
                    callBack.sendMessage(m);
                    break;
                default:
                    Log.e(TAG, "unkown event: " + msg.what);
                    break;
            }

            if (changeResult) {
                genRandomCards();
            }
        }

        ;
    };

    private void notifyScoreChangeEvent(int score) {
        h.obtainMessage(EVENT_ADD_SCORE, score, 0).sendToTarget();
    }

    protected boolean handleLeftMotion() {
        boolean changeResult = false;
        for (int y = 0; y < GlobleSettings.GAME_DIMENTION; y++) {
            for (int x = 0; x < GlobleSettings.GAME_DIMENTION; x++) {
                // if current point is empty, find next none empty point, move to current point;
                Log.d(TAG, "cardMap[" + x + "][" + y + "]= " + cardMap[x][y].getNumber());
                if (cardMap[x][y].getNumber() <= 0) {
                    CardLayout cl = null;
                    for (int k = x + 1; k < GlobleSettings.GAME_DIMENTION; k++) {
                        if (cardMap[k][y].getNumber() > 0) {
                            Log.d(TAG, "found none empyt card[" + k + "][" + y + "]");
                            cl = cardMap[k][y];
                            break;
                        }
                    }

                    if (cl != null) {
                        Log.d(TAG, "switch card[" + x + "][" + y + "] with cl");
                        cardMap[x][y].setNumber(cl.getNumber());
                        cl.setNumber(0);
                        changeResult = true;
                    } else {
                        // current line is empty, scan next line;
                        Log.d(TAG, "line " + y + " is empty");
                        x = GlobleSettings.GAME_DIMENTION;
                        break;
                    }
                }

                // now current point is not empty, check if next none-empty
                // point should merge.
                for (int k = x + 1; k < GlobleSettings.GAME_DIMENTION; k++) {
                    Log.d(TAG, "cardMap[" + k + "][" + y + "] = " + cardMap[k][y].getNumber());
                    if (cardMap[k][y].getNumber() <= 0) {
                        continue;
                    }
                    if (cardMap[k][y].equals(cardMap[x][y])) {
                        Log.d(TAG, "cardMap[" + k + "][" + y + "] merge to cardMap[" + x + "][" + y + "]");
                        cardMap[x][y].setNumber(cardMap[x][y].getNumber() * 2);
                        cardMap[k][y].setNumber(0);
                        changeResult = true;
                        notifyScoreChangeEvent(cardMap[x][y].getNumber());
                    }
                    break;
                }
            }
        }
        return changeResult;
    }

    protected boolean hanleRightMotion() {
        boolean changeResult = false;
        for (int y = 0; y < GlobleSettings.GAME_DIMENTION; y++) {
            for (int x = GlobleSettings.GAME_DIMENTION - 1; x >= 0; x--) {
                // if current point is empty, find next none empty point, move to current point;
                Log.d(TAG, "cardMap[" + x + "][" + y + "]= " + cardMap[x][y].getNumber());
                if (cardMap[x][y].getNumber() <= 0) {
                    CardLayout cl = null;
                    for (int k = x - 1; k >= 0; k--) {
                        if (cardMap[k][y].getNumber() > 0) {
                            Log.d(TAG, "found none empyt card[" + k + "][" + y + "]");
                            cl = cardMap[k][y];
                            break;
                        }
                    }

                    if (cl != null) {
                        Log.d(TAG, "switch card[" + x + "][" + y + "] with cl");
                        cardMap[x][y].setNumber(cl.getNumber());
                        cl.setNumber(0);
                        changeResult = true;
                    } else {
                        // current line is empty, scan next line;
                        Log.d(TAG, "line " + y + " is empty");
                        x = -1;
                        break;
                    }
                }

                // now current point is not empty, check if next none-empty point should merge.
                for (int k = x - 1; k >= 0; k--) {
                    Log.d(TAG, "cardMap[" + k + "][" + y + "] = " + cardMap[k][y].getNumber());
                    if (cardMap[k][y].getNumber() <= 0) {
                        continue;
                    }
                    if (cardMap[k][y].equals(cardMap[x][y])) {
                        Log.d(TAG, "cardMap[" + k + "][" + y + "] merge to cardMap[" + x + "][" + y + "]");
                        cardMap[x][y].setNumber(cardMap[x][y].getNumber() * 2);
                        cardMap[k][y].setNumber(0);
                        changeResult = true;
                        notifyScoreChangeEvent(cardMap[x][y].getNumber());
                    }
                    break;
                }
            }
        }
        return changeResult;
    }

    protected boolean hanleUpMotion() {
        boolean changeResult = false;
        for (int x = 0; x < GlobleSettings.GAME_DIMENTION; x++) {
            for (int y = 0; y < GlobleSettings.GAME_DIMENTION; y++) {
                // if current point is empty, find next none empty point, move to current point;
                Log.d(TAG, "cardMap[" + x + "][" + y + "] = " + cardMap[x][y].getNumber());
                if (cardMap[x][y].getNumber() <= 0) {
                    CardLayout cl = null;
                    for (int k = y + 1; k < GlobleSettings.GAME_DIMENTION; k++) {
                        if (cardMap[x][k].getNumber() > 0) {
                            Log.d(TAG, "found none empyt card[" + x + "][" + k + "]");
                            cl = cardMap[x][k];
                            break;
                        }
                    }

                    if (cl != null) {
                        Log.d(TAG, "switch card[" + x + "][" + y + "] with cl");
                        cardMap[x][y].setNumber(cl.getNumber());
                        cl.setNumber(0);
                        changeResult = true;
                    } else {
                        // current line is empty, scan next line;
                        Log.d(TAG, "line " + x + " is empty");
                        y = GlobleSettings.GAME_DIMENTION;
                        break;
                    }
                }

                // now current point is not empty, check if next none-empty
                // point should merge.
                for (int k = y + 1; k < GlobleSettings.GAME_DIMENTION; k++) {
                    Log.d(TAG, "cardMap[" + x + "][" + k + "] = " + cardMap[x][k].getNumber());
                    if (cardMap[x][k].getNumber() <= 0) {
                        continue;
                    }
                    if (cardMap[x][k].equals(cardMap[x][y])) {
                        Log.d(TAG, "cardMap[" + x + "][" + k + "] merge to cardMap[" + x + "][" + y + "]");
                        cardMap[x][y].setNumber(cardMap[x][y].getNumber() * 2);
                        cardMap[x][k].setNumber(0);
                        changeResult = true;
                        notifyScoreChangeEvent(cardMap[x][y].getNumber());
                    }
                    break;
                }
            }
        }
        return changeResult;
    }

    protected boolean hanleDownMotion() {
        boolean changeResult = false;
        for (int x = 0; x < GlobleSettings.GAME_DIMENTION; x++) {
            for (int y = GlobleSettings.GAME_DIMENTION - 1; y >= 0; y--) {
                // if current point is empty, find next none empty point, move to current point;
                Log.d(TAG, "cardMap[" + x + "][" + y + "]= " + cardMap[x][y].getNumber());
                if (cardMap[x][y].getNumber() <= 0) {
                    CardLayout cl = null;
                    for (int k = y - 1; k >= 0; k--) {
                        if (cardMap[x][k].getNumber() > 0) {
                            Log.d(TAG, "found none empyt card[" + k + "][" + y + "]");
                            cl = cardMap[x][k];
                            break;
                        }
                    }

                    if (cl != null) {
                        Log.d(TAG, "switch card[" + x + "][" + y + "] with cl");
                        cardMap[x][y].setNumber(cl.getNumber());
                        cl.setNumber(0);
                        changeResult = true;
                    } else {
                        // current row is empty, scan next line;
                        Log.d(TAG, "line " + x + " is empty");
                        y = -1;
                        break;
                    }
                }

                // now current point is not empty, check if next none-empty point should merge.
                for (int k = y - 1; k >= 0; k--) {
                    Log.d(TAG, "cardMap[" + x + "][" + k + "] = " + cardMap[x][k].getNumber());
                    if (cardMap[x][k].getNumber() <= 0) {
                        continue;
                    }
                    if (cardMap[x][k].equals(cardMap[x][y])) {
                        Log.d(TAG, "cardMap[" + x + "][" + k + "] merge to cardMap[" + x + "][" + y + "]");
                        cardMap[x][y].setNumber(cardMap[x][y].getNumber() * 2);
                        cardMap[x][k].setNumber(0);
                        changeResult = true;
                        notifyScoreChangeEvent(cardMap[x][y].getNumber());
                    }
                    break;
                }
            }
        }

        return changeResult;
    }
}
