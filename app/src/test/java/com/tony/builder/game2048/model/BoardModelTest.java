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
    public void testResetCardBoard() throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        int cardMap[][] = new int[4][4];
        List<Point> emptyPointList = new LinkedList<>();
        BoardModel bm = new BoardModel(cardMap, emptyPointList);
        assertEquals(14, emptyPointList.size());

        Method resetCardBoard = bm.getClass().getDeclaredMethod("resetCardBoard", null);
        resetCardBoard.setAccessible(true);
        resetCardBoard.invoke(bm);

        assertEquals(16, emptyPointList.size());
    }

    @Test
    public void testOnSwipeLeft() {
        BoardModel bm = new BoardModel();
        int[][] currentMap = {
                {0,0,0,0},
                {0,2,0,0},
                {0,0,2,0},
                {0,0,0,2}
        };
        int[][] expectMap = {
                {0,0,0,0},
                {2,0,0,0},
                {2,0,0,0},
                {2,0,0,0}
        };
        bm.setCardMap(currentMap);
        bm.onSwipeLeft();
        assertTrue(DumpUtils.isEqual(expectMap, currentMap));

        int[][] currentMap2 = {
                {4,0,0,0},
                {4,2,0,0},
                {4,0,2,0},
                {4,0,0,2}
        };
        int[][] expectMap2 = {
                {4,0,0,0},
                {4,2,0,0},
                {4,2,0,0},
                {4,2,0,0}
        };
        bm.setCardMap(currentMap2);
        bm.onSwipeLeft();
        assertTrue(DumpUtils.isEqual(expectMap2, currentMap2));

        int[][] currentMap3 = {
                {2,0,0,0},
                {2,2,0,0},
                {2,0,2,0},
                {2,0,0,2}
        };
        int[][] expectMap3 = {
                {2,0,0,0},
                {4,0,0,0},
                {4,0,0,0},
                {4,0,0,0}
        };
        bm.setCardMap(currentMap3);
        bm.onSwipeLeft();
        assertTrue(DumpUtils.isEqual(expectMap3, currentMap3));

        int[][] currentMap4 = {
                {0,2,0,0},
                {0,2,0,0},
                {0,2,2,0},
                {0,2,0,2}
        };
        int[][] expectMap4 = {
                {2,0,0,0},
                {2,0,0,0},
                {4,0,0,0},
                {4,0,0,0}
        };
        bm.setCardMap(currentMap4);
        bm.onSwipeLeft();
        assertTrue(DumpUtils.isEqual(expectMap4, currentMap4));
    }

    @Test
    public void testOnSwipeRight() {
        BoardModel bm = new BoardModel();
        int[][] currentMap = {
                {0,0,0,0},
                {0,2,0,0},
                {0,0,2,0},
                {0,0,0,2}
        };
        int[][] expectMap = {
                {0,0,0,0},
                {0,0,0,2},
                {0,0,0,2},
                {0,0,0,2}
        };
        bm.setCardMap(currentMap);
        bm.onSwipeRight();
        assertTrue(DumpUtils.isEqual(expectMap, currentMap));

        int[][] currentMap2 = {
                {4,0,0,0},
                {4,2,0,0},
                {4,0,2,0},
                {4,0,0,2}
        };
        int[][] expectMap2 = {
                {0,0,0,4},
                {0,0,4,2},
                {0,0,4,2},
                {0,0,4,2}
        };
        bm.setCardMap(currentMap2);
        bm.onSwipeRight();
        assertTrue(DumpUtils.isEqual(expectMap2, currentMap2));

        int[][] currentMap3 = {
                {2,0,0,0},
                {2,2,0,0},
                {2,0,2,0},
                {2,0,0,2}
        };
        int[][] expectMap3 = {
                {0,0,0,2},
                {0,0,0,4},
                {0,0,0,4},
                {0,0,0,4}
        };
        bm.setCardMap(currentMap3);
        bm.onSwipeRight();
        assertTrue(DumpUtils.isEqual(expectMap3, currentMap3));

        int[][] currentMap4 = {
                {0,2,0,0},
                {0,2,0,0},
                {0,2,2,0},
                {0,2,0,2}
        };
        int[][] expectMap4 = {
                {0,0,0,2},
                {0,0,0,2},
                {0,0,0,4},
                {0,0,0,4}
        };
        bm.setCardMap(currentMap4);
        bm.onSwipeRight();
        assertTrue(DumpUtils.isEqual(expectMap4, currentMap4));
    }

    @Test
    public void testDumpUtil() {
        int[][] expect = {
                {0,2,0,0},
                {0,2,0,0},
                {0,2,2,0},
                {0,2,0,2}
        };

        int[][] currentMap = {
                {0,2,0,0},
                {0,2,0,0},
                {0,2,2,0},
                {0,2,0,2}
        };

        int[][] errorMap = {
                {0,2,0,0},
                {0,2,0,0},
                {0,2,2,0},
                {4,2,0,2}
        };

        assertTrue(DumpUtils.isEqual(expect, currentMap));
        assertFalse(DumpUtils.isEqual(expect,errorMap));
    }
}
