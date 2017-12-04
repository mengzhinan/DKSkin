package com.duke.skinlib.log;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * Author: duke
 * DateTime: 2017-11-30 10:07
 * Description: log，toast辅助类
 */
public class LogHelper {

    public static final String TAG = LogHelper.class.getSimpleName();
    private static boolean isDebug = true;
    private static boolean isOpenToast = true;

    public static void setDebug(boolean isDebug) {
        LogHelper.isDebug = isDebug;
    }

    public static void setToast(boolean isOpenToast) {
        LogHelper.isOpenToast = isOpenToast;
    }

    public static boolean isDebug() {
        return LogHelper.isDebug;
    }

    public static boolean isOpenToast() {
        return LogHelper.isOpenToast;
    }

    //***toast************************************************
    public static void toast(Context context, String text) {
        toast(context, text, Toast.LENGTH_SHORT);
    }

    public static void toast(Context context, String text, int duration) {
        if (context == null || TextUtils.isEmpty(text)) {
            return;
        }
        if (isOpenToast) {
            Toast.makeText(context, text, duration).show();
        }
    }

    //***d************************************************
    public static void d(String msg) {
        d(TAG, msg);
    }

    public static void d(String tag, String msg) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
            return;
        }
        if (isDebug) {
            Log.d(tag, msg);
        }
    }

    //***v************************************************
    public static void v(String msg) {
        v(TAG, msg);
    }

    public static void v(String tag, String msg) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
            return;
        }
        if (isDebug) {
            Log.v(tag, msg);
        }
    }

    //***e************************************************
    public static void e(String msg) {
        e(TAG, msg);
    }

    public static void e(String tag, String msg) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
            return;
        }
        if (isDebug) {
            Log.e(tag, msg);
        }
    }

    //***i************************************************
    public static void i(String msg) {
        i(TAG, msg);
    }

    public static void i(String tag, String msg) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
            return;
        }
        if (isDebug) {
            Log.i(tag, msg);
        }
    }

    //***w************************************************
    public static void w(String msg) {
        w(TAG, msg);
    }

    public static void w(String tag, String msg) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
            return;
        }
        if (isDebug) {
            Log.w(tag, msg);
        }
    }

    //***wtf************************************************
    public static void wtf(String msg) {
        wtf(TAG, msg);
    }

    public static void wtf(String tag, String msg) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
            return;
        }
        if (isDebug) {
            Log.wtf(tag, msg);
        }
    }
}
