package com.lq.plugin.init.utils;

import java.util.List;

public class Log {
    public static void e(String msg) {
        System.out.println("\033[0;34m" + msg + "\033[0m");
    }

    public static <T> String listToString(List<T> list) {
        StringBuilder sb = new StringBuilder();

        sb.append("[");

        for (T initClass : list) {
            sb.append("\"").append(initClass).append("\"").append(",");
        }

        sb.append("]");
        return sb.toString();
    }
}
