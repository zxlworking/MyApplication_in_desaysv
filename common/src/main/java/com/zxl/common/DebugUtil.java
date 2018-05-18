package com.zxl.common;

import android.util.Log;

/**
 * Created by uidq0955 on 2018/5/18.
 */

public class DebugUtil {
    public static final boolean IS_DEBUG = false;

    public static void d(String tag,String message){
        if(IS_DEBUG){
            Log.d(tag,message);
        }
    }
}
