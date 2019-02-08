package com.tony.builder.game2048.model;

import org.junit.Test;

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
}
