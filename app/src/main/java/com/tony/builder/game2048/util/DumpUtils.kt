package com.tony.builder.game2048.util

object DumpUtils {
    fun isEqual(expect: Array<IntArray>, value: Array<IntArray>): Boolean {
        if (expect.size != value.size) {
            return false
        }
        for (i in expect.indices) {
            if (value[i].size != expect[i].size) {
                return false
            }
            for (j in expect[i].indices) {
                if (value[i][j] != expect[i][j]) {
                    return false
                }
            }
        }
        return true
    }

    fun dump(array: Array<IntArray>): String {
        val sb = StringBuilder()
        sb.append("{\n")
        for (i in array.indices) {
            sb.append("\t")
            sb.append("{")
            for (j in array[i].indices) {
                sb.append(array[i][j])
                if (j + 1 !in array[i].indices) {
                    sb.append(",")
                }
            }
            sb.append("}\n")
        }
        sb.append("}\n")
        return sb.toString()
    }
}