package com.tony.builder.game2048.model;

import com.tony.builder.game2048.view.IMotionHandler;

import java.util.LinkedList;
import java.util.List;

public class BoardModel implements IMotionHandler {
    private static final int BOARD_DIMENSION = 4;
    private static final double PROBABILITY_OF_FOUR = 0.4;
    private int cardMap[][];
    private List<Point> emptyPointList;
    private BoardEventListener boardEventListener = null;

    public interface BoardEventListener{
        void onBoardReset(int boardDimension);
        void onCardGenerated(Point pt);
        void onCardMerged(Point source, Point sink);
        void onGameFinished();
    }

    public BoardModel() {
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
        // no space left, check if game finished.
        if (emptyPointList.size() == 0) {
            isGameFinished();
            return false;
        }

        // gen 1 card at empty point.
        Point pt = emptyPointList.remove((int) (Math.random() * emptyPointList.size()));
        cardMap[pt.x][pt.y] = Math.random() > PROBABILITY_OF_FOUR ? 2 : 4;
        if (boardEventListener != null) {
            boardEventListener.onCardGenerated(pt);
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
     * @return if game is finished.
     */
    private boolean isGameFinished() {
        for (int y = 0; y < BOARD_DIMENSION; y++) {
            for (int x = 0; x < BOARD_DIMENSION - 1; x++) {
                if (cardMap[x][y]==cardMap[x + 1][y]) {
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
                if (cardMap[x][y]==cardMap[x][y + 1]) {
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

    public void setBoardEventListener(BoardEventListener boardEventListener) {
        this.boardEventListener = boardEventListener;
    }

    public String dump() {
        StringBuilder sb = new StringBuilder();
        sb.append("cardMap = ");
        for (int y = 0; y < BOARD_DIMENSION; y++) {
            sb.append("{ ");
            for (int x = 0; x < BOARD_DIMENSION; x++) {
                sb.append("{"+cardMap[x][y]+" }");
            }
            sb.append(" }\n");
        }
        return sb.toString();
    }

    @Override
    public void onSwipeUp() {

    }

    @Override
    public void onSwipeDown() {

    }

    @Override
    public void onSwipeLeft() {

    }

    @Override
    public void onSwipeRight() {

    }
}
