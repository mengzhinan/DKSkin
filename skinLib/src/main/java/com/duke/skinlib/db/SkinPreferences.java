package com.duke.skinlib.db;

import android.content.Context;
import android.text.TextUtils;

/**
 * Author: duke
 * DateTime: 2017-11-30 12:49
 * Description: skin存储
 */
public class SkinPreferences {
    private static final String SkinPreferencesName = "SkinPreferencesName";
    private static SkinPreferences mInstance;
    private static BasePreferences mBaseInstance;

    //记录上一次使用的skin包路径
    public static final String LAST_SKIN_PACKAGE_PATH = "last_skin_package_path";

    private SkinPreferences(Context context) {
        try {
            mBaseInstance = new BasePreferences(context, SkinPreferencesName);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static SkinPreferences getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SkinPreferences.class) {
                if (mInstance == null) {
                    mInstance = new SkinPreferences(context);
                }
            }
        }
        return mInstance;
    }

    public static void setLastSkinPackagePath(String apkFilePath) {
        if (TextUtils.isEmpty(apkFilePath)) {
            return;
        }
        mBaseInstance.set(LAST_SKIN_PACKAGE_PATH, apkFilePath);
    }

    public static String getLastSkinPackagePath() {
        return mBaseInstance.getString(LAST_SKIN_PACKAGE_PATH);
    }
}
