package com.tony.builder.game2048.util;

public class DumpUtils {
    int[][] currentMap = {
            {0,0,0,0},
            {2,2,0,0},
            {2,0,2,0},
            {0,2,2,0}
    };
    public static String dump(int[][] array) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        for(int i=0; i<array.length; i++) {
            sb.append("\t");
            sb.append("{");
            for(int j=0; j<array[i].length; j++) {
                sb.append(array[i][j]);
                if ((j+1) != array[i].length) {
                    sb.append(",");
                }
            }
            sb.append("}\n");
        }
        sb.append("}\n");
        return sb.toString();
    }
}
