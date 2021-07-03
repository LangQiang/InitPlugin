package com.lq.plugin.init;

public class Log {
    public static void e(String msg) {
        System.out.println("\033[0;34m" + msg + "\033[0m");
    }
}
