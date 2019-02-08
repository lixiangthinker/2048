package com.tony.builder.game2048.model;

import com.tony.builder.game2048.util.DumpUtils;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class BoardModelTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testDump() {
        BoardModel bm = new BoardModel();
        String result = bm.dump();
        System.out.println(result);
    }

    boolean resetFlag = false;

    @Test
    public void testResetCardBoard() throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        int cardMap[][] = new int[4][4];
        List<Point> emptyPointList = new LinkedList<>();
        BoardModel bm = new BoardModel(cardMap, emptyPointList);
        assertEquals(14, emptyPointList.size());

        final int resetDimention = 4;
        bm.setBoardEventListener(new BoardModel.BoardEventListener() {
            @Override
            public void onBoardReset(int boardDimension) {
                resetFlag = true;
                assertEquals(resetDimention, boardDimension);
            }

            @Override
            public void onCardGenerated(Point pt) {

            }

            @Override
            public void onCardMerged(Point source, Point sink, int sourceValue, int sinkValue) {

            }

            @Override
            public void onGameFinished() {

            }
        });

        Method resetCardBoard = bm.getClass().getDeclaredMethod("resetCardBoard", null);
        resetCardBoard.setAccessible(true);
        resetCardBoard.invoke(bm);
        assertTrue(resetFlag);

        assertEquals(16, emptyPointList.size());
    }

    @Test
    public void testSwipeLeft() {
        BoardModel bm = new BoardModel();
        bm.setBoardEventListener(new BoardModel.BoardEventListener() {
            @Override
            public void onBoardReset(int boardDimension) {
            }

            @Override
            public void onCardGenerated(Point pt) {

            }

            @Override
            public void onCardMerged(Point source, Point sink, int sourceValue, int sinkValue) {
                System.out.println("source=" + source + " sink=" + sink +
                        " sourceValue=" + sourceValue + " sinkValue=" + sinkValue);
            }

            @Override
            public void onGameFinished() {

            }
        });
        int[][] currentMap = {
                {0,0,0,0},
                {0,2,0,0},
                {0,0,2,0},
                {0,0,0,2}
        };
        System.out.println("original = " + DumpUtils.dump(currentMap));
        bm.setCardMap(currentMap);
        bm.onSwipeLeft();
        System.out.println(bm.dump());

        int[][] currentMap2 = {
                {4,0,0,0},
                {4,2,0,0},
                {4,0,2,0},
                {4,0,0,2}
        };
        System.out.println("original = " + DumpUtils.dump(currentMap2));
        bm.setCardMap(currentMap2);
        bm.onSwipeLeft();
        System.out.println(bm.dump());

        int[][] currentMap3 = {
                {2,0,0,0},
                {2,2,0,0},
                {2,0,2,0},
                {2,0,0,2}
        };
        System.out.println("original = " + DumpUtils.dump(currentMap3));
        bm.setCardMap(currentMap3);
        bm.onSwipeLeft();
        System.out.println(bm.dump());

        int[][] currentMap4 = {
                {0,2,0,0},
                {0,2,0,0},
                {0,2,2,0},
                {0,2,0,2}
        };
        System.out.println("original = " + DumpUtils.dump(currentMap4));
        bm.setCardMap(currentMap4);
        bm.onSwipeLeft();
        System.out.println(bm.dump());
    }
}
