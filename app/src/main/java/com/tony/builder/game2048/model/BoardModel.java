package com.tony.builder.game2048.model;

import com.tony.builder.game2048.util.DumpUtils;
import com.tony.builder.game2048.view.IMotionHandler;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BoardModel implements IMotionHandler {
    public static final int BOARD_DIMENSION = 4;
    private static final double PROBABILITY_OF_FOUR = 0.4;
    private int cardMap[][];
    private List<Point> emptyPointList;
    private BoardEventListener boardEventListener = null;

    public interface BoardEventListener {
        void onBoardReset(int boardDimension);

        void onCardGenerated(Point pt, int value);

        void onCardMerged(Point source, Point sink, int sourceValue, int sinkValue);

        void onCardMoved(Point source, Point sink, int sourceValue, int sinkValue);

        void onGameFinished();
    }

    @Inject
    public BoardModel() {
        startGame();
    }

    public void startGame() {
        cardMap = new int[BOARD_DIMENSION][BOARD_DIMENSION];
        emptyPointList = new LinkedList<>();
        resetCardBoard();
        genRandomCards();
        genRandomCards();
    }

    // for inject cardmap and emptyPointList;
    public BoardModel(int cardMap[][], List<Point> emptyPointList) {
        this.cardMap = cardMap;
        this.emptyPointList = emptyPointList;
        resetCardBoard();
        genRandomCards();
        genRandomCards();
    }

    private void resetCardBoard() {
        emptyPointList.clear();
        for (int y = 0; y < BOARD_DIMENSION; y++) {
            for (int x = 0; x < BOARD_DIMENSION; x++) {
                cardMap[x][y] = 0;
                emptyPointList.add(new Point(x, y));
            }
        }
        if (boardEventListener != null) {
            boardEventListener.onBoardReset(BOARD_DIMENSION);
        }
    }

    private boolean genRandomCards() {
        emptyPointList.clear();

        for (int y = 0; y < BOARD_DIMENSION; y++) {
            for (int x = 0; x < BOARD_DIMENSION; x++) {
                if (cardMap[y][x] <= 0) {
                    emptyPointList.add(new Point(x, y));
                }
            }
        }
        // no space left, check if game finished.
        if (emptyPointList.size() == 0) {
            isGameFinished();
            return false;
        }

        // gen 1 card at empty point.
        Point pt = emptyPointList.remove((int) (Math.random() * emptyPointList.size()));
        cardMap[pt.y][pt.x] = Math.random() > PROBABILITY_OF_FOUR ? 2 : 4;
        if (boardEventListener != null) {
            boardEventListener.onCardGenerated(pt, cardMap[pt.y][pt.x]);
        }

        // check after 1 card is generated.
        if (emptyPointList.size() == 0) {
            isGameFinished();
            return true;
        }
        return true;
    }

    /**
     * check if this game is finished.
     *
     * @return if game is finished.
     */
    private boolean isGameFinished() {
        for (int y = 0; y < BOARD_DIMENSION; y++) {
            for (int x = 0; x < BOARD_DIMENSION - 1; x++) {
                if (cardMap[x][y] == cardMap[x + 1][y]) {
                    // cards can be merged;
                    return false;
            }

                if (cardMap[x][y] <= 0) {
                    // empty position
                    return false;
                }
            }
        }

        for (int x = 0; x < BOARD_DIMENSION; x++) {
            for (int y = 0; y < BOARD_DIMENSION - 1; y++) {
                if (cardMap[x][y] == cardMap[x][y + 1]) {
                    // cards can be merged;
                    return false;
                }

                if (cardMap[x][y] <= 0) {
                    // empty position
                    return false;
                }
            }
        }
        if (boardEventListener != null) {
            boardEventListener.onGameFinished();
        }
        return true;
    }

    public String dump() {
        return DumpUtils.dump(cardMap);
    }

    public enum GameMotion{
        SWIPE_LEFT,
        SWIPE_RIGHT,
        SWIPE_UP,
        SWIPE_DOWN
    }

    public void handleMotion(GameMotion motion) {
        boolean changeResult = false;
        switch (motion) {
            case SWIPE_UP:
                changeResult = onSwipeUp();
                break;
            case SWIPE_DOWN:
                changeResult = onSwipeDown();
                break;
            case SWIPE_LEFT:
                changeResult = onSwipeLeft();
                break;
            case SWIPE_RIGHT:
                changeResult = onSwipeRight();
                break;
            default:
                break;
        }
        if (changeResult) {
            genRandomCards();
        }
    }

    @Override
    public boolean onSwipeDown() {
        boolean changeResult = false;
        for (int x = 0; x < BOARD_DIMENSION; x++) {
            for (int y = BOARD_DIMENSION - 1; y >= 0; y--) {
                // if current point is empty, find next none empty point, move to current point;
                if (cardMap[y][x] <= 0) {
                    Point pt = null;
                    for (int k = y - 1; k >= 0; k--) {
                        if (cardMap[k][x] > 0) {
                            pt = new Point(x, k);
                            break;
                        }
                    }

                    if (pt != null) {
                        cardMap[y][x] = cardMap[pt.y][pt.x];
                        cardMap[pt.y][pt.x] = 0;
                        changeResult = true;
                        notifyMoveEvent(pt, new Point(x, y));
                    } else {
                        // current line is empty, scan next line;
                        y = -1;
                        break;
                    }
                }
                // now current point is not empty, check if next none-empty point should merge.
                for (int k = y - 1; k >= 0; k--) {
                    if (cardMap[k][x] <= 0) {
                        continue;
                    }
                    if (cardMap[k][x] == cardMap[y][x]) {
                        cardMap[y][x] = cardMap[y][x] * 2;
                        cardMap[k][x] = 0;
                        changeResult = true;
                        notifyMergeEvent(new Point(x,k), new Point(x,y));
                    }
                    break;
                }
            }
        }
        return changeResult;
    }

    @Override
    public boolean onSwipeUp() {
        boolean changeResult = false;
        for (int x = 0; x < BOARD_DIMENSION; x++) {
            for (int y = 0; y < BOARD_DIMENSION; y++) {
                // if current point is empty, find next none empty point, move to current point;
                if (cardMap[y][x] <= 0) {
                    Point pt = null;
                    for (int k = y + 1; k < BOARD_DIMENSION; k++) {
                        if (cardMap[k][x] > 0) {
                            pt = new Point(x, k);
                            break;
                        }
                    }

                    if (pt != null) {
                        cardMap[y][x] = cardMap[pt.y][pt.x];
                        cardMap[pt.y][pt.x] = 0;
                        changeResult = true;
                        notifyMoveEvent(pt, new Point(x, y));
                    } else {
                        // current line is empty, scan next line;
                        y = BOARD_DIMENSION;
                        break;
                    }
                }
                // now current point is not empty, check if next none-empty point should merge.
                for (int k = y + 1; k < BOARD_DIMENSION; k++) {
                    if (cardMap[k][x] <= 0) {
                        continue;
                    }
                    if (cardMap[k][x] == cardMap[y][x]) {
                        cardMap[y][x] = cardMap[y][x] * 2;
                        cardMap[k][x] = 0;
                        changeResult = true;
                        notifyMergeEvent(new Point(x,k), new Point(x,y));
                    }
                    break;
                }
            }
        }
        return changeResult;
    }

    private void notifyMergeEvent(Point source, Point sink) {
        if (boardEventListener != null) {
            boardEventListener.onCardMerged(source, sink,
                    cardMap[source.y][source.x], cardMap[sink.y][sink.x]);
        }
    }

    private void notifyMoveEvent(Point source, Point sink) {
        if (boardEventListener != null) {
            boardEventListener.onCardMoved(source, sink,
                    cardMap[source.y][source.x], cardMap[sink.y][sink.x]);
        }
    }

    @Override
    public boolean onSwipeLeft() {
        boolean changeResult = false;
        for (int y = 0; y < BOARD_DIMENSION; y++) {
            for (int x = 0; x < BOARD_DIMENSION; x++) {
                // if current point is empty, find next none empty point, move to current point;
                if (cardMap[y][x] <= 0) {
                    Point pt = null;
                    for (int k = x + 1; k < BOARD_DIMENSION; k++) {
                        if (cardMap[y][k] > 0) {
                            pt = new Point(k, y);
                            break;
                        }
                    }

                    if (pt != null) {
                        cardMap[y][x] = cardMap[pt.y][pt.x];
                        cardMap[pt.y][pt.x] = 0;
                        changeResult = true;
                        notifyMoveEvent(pt, new Point(x, y));
                    } else {
                        // current line is empty, scan next line;
                        x = BOARD_DIMENSION;
                        break;
                    }
                }
                // now current point is not empty, check if next none-empty point should merge.
                for (int k = x + 1; k < BOARD_DIMENSION; k++) {
                    if (cardMap[y][k] <= 0) {
                        continue;
                    }
                    if (cardMap[y][k] == cardMap[y][x]) {
                        cardMap[y][x] = cardMap[y][x] * 2;
                        cardMap[y][k] = 0;
                        changeResult = true;
                        notifyMergeEvent(new Point(k,y), new Point(x,y));
                    }
                    break;
                }
            }
        }
        return changeResult;
    }

    @Override
    public boolean onSwipeRight() {
        boolean changeResult = false;
        for (int y = 0; y < BOARD_DIMENSION; y++) {
            for (int x = BOARD_DIMENSION - 1; x >= 0; x--) {
                // if current point is empty, find next none empty point, move to current point;
                if (cardMap[y][x] <= 0) {
                    Point pt = null;
                    for (int k = x - 1; k >= 0; k--) {
                        if (cardMap[y][k] > 0) {
                            pt = new Point(k, y);
                            break;
                        }
                    }

                    if (pt != null) {
                        cardMap[y][x] = cardMap[pt.y][pt.x];
                        cardMap[pt.y][pt.x] = 0;
                        changeResult = true;
                        notifyMoveEvent(pt, new Point(x, y));
                    } else {
                        // current line is empty, scan next line;
                        x = -1;
                        break;
                    }
                }
                // now current point is not empty, check if next none-empty point should merge.
                for (int k = x - 1; k >= 0; k--) {
                    if (cardMap[y][k] <= 0) {
                        continue;
                    }
                    if (cardMap[y][k] == cardMap[y][x]) {
                        cardMap[y][x] = cardMap[y][x] * 2;
                        cardMap[y][k] = 0;
                        changeResult = true;
                        notifyMergeEvent(new Point(k,y), new Point(x,y));
                    }
                    break;
                }
            }
        }
        return changeResult;
    }

    public void setBoardEventListener(BoardEventListener boardEventListener) {
        this.boardEventListener = boardEventListener;
    }

    public void setCardMap(int[][] cardMap) {
        this.cardMap = cardMap;
    }

    public void setEmptyPointList(List<Point> emptyPointList) {
        this.emptyPointList = emptyPointList;
    }
}
