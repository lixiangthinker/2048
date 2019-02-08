package com.tony.builder.game2048.model;

import org.junit.Test;

import java.lang.reflect.Field;
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
            IllegalAccessException, InvocationTargetException{
        int cardMap[][] = new int[4][4];
        List<Point> emptyPointList = new LinkedList<>();
        BoardModel bm = new BoardModel(cardMap, emptyPointList);
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
            public void onCardMerged(Point source, Point sink) {

            }

            @Override
            public void onGameFinished() {

            }
        });

        Method resetCardBoard = bm.getClass().getDeclaredMethod("resetCardBoard",null);
        resetCardBoard.setAccessible(true);
        resetCardBoard.invoke(bm);
        assertTrue(resetFlag);

        assertEquals(16, emptyPointList.size());
    }
}
