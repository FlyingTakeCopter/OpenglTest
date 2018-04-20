package com.example.liuqikang.myapplication.util;

import android.util.Log;

/**
 * Created by liuqikang on 2018/4/20.
 */

public class MyLog {
    final static boolean DEBUB = true;

    static void Log(String tag, String content){
        if (DEBUB){
            Log.i(tag, content);
        }
    }
}
