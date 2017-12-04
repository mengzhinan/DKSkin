package com.duke.skinlib.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Author: duke
 * DateTime: 2017-11-30 12:05
 * Description: SharedPreferences公共基类
 */
public class BasePreferences {
    private String mPreferencesFileName;
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    public String getPreferencesFileName() {
        return mPreferencesFileName;
    }

    public BasePreferences(Context context, String preferencesFileName) throws IllegalAccessException {
        if (context == null || TextUtils.isEmpty(preferencesFileName)) {
            throw new IllegalAccessException("context or preferencesFileName is null");
        }
        this.mContext = context;
        this.mPreferencesFileName = preferencesFileName;
        mSharedPreferences = getSharedPreferences(preferencesFileName);
    }

    private SharedPreferences getSharedPreferences() {
        return getSharedPreferences(null);
    }

    private SharedPreferences getSharedPreferences(String newFileName) {
        if (mContext == null) {
            return null;
        }
        if (!TextUtils.isEmpty(newFileName)) {
            mSharedPreferences = mContext.getSharedPreferences(mPreferencesFileName, Context.MODE_PRIVATE);
        } else {
            if (mSharedPreferences == null) {
                mSharedPreferences = mContext.getSharedPreferences(mPreferencesFileName, Context.MODE_PRIVATE);
            }
        }
        return mSharedPreferences;
    }

    public boolean set(String key, Object value) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value.toString())) {
            return false;
        }
        SharedPreferences sharedPreferences = getSharedPreferences();
        if (sharedPreferences == null) {
            return false;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean isChanged = false;
        if (value instanceof Boolean) {
            Boolean bl = (Boolean) value;
            editor.putBoolean(key, bl);
            isChanged = true;
        } else if (value instanceof Float) {
            Float ft = (Float) value;
            editor.putFloat(key, ft);
            isChanged = true;
        } else if (value instanceof Integer) {
            Integer it = (Integer) value;
            editor.putInt(key, it);
            isChanged = true;
        } else if (value instanceof Long) {
            Long lg = (Long) value;
            editor.putLong(key, lg);
            isChanged = true;
        } else if (value instanceof String) {
            String str = (String) value;
            editor.putString(key, str);
            isChanged = true;
        }
        if (isChanged) {
            editor.apply();
        }
        return isChanged;
    }

    public boolean getBoolean(String key) {
        if (TextUtils.isEmpty(key)) {
            return false;
        }
        SharedPreferences sharedPreferences = getSharedPreferences();
        if (sharedPreferences == null) {
            return false;
        }
        return sharedPreferences.getBoolean(key, false);
    }

    public float getFloat(String key) {
        if (TextUtils.isEmpty(key)) {
            return 0.0F;
        }
        SharedPreferences sharedPreferences = getSharedPreferences();
        if (sharedPreferences == null) {
            return 0.0F;
        }
        return sharedPreferences.getFloat(key, 0.0F);
    }

    public int getInt(String key) {
        if (TextUtils.isEmpty(key)) {
            return 0;
        }
        SharedPreferences sharedPreferences = getSharedPreferences();
        if (sharedPreferences == null) {
            return 0;
        }
        return sharedPreferences.getInt(key, 0);
    }

    public long getLong(String key) {
        if (TextUtils.isEmpty(key)) {
            return 0L;
        }
        SharedPreferences sharedPreferences = getSharedPreferences();
        if (sharedPreferences == null) {
            return 0L;
        }
        return sharedPreferences.getLong(key, 0L);
    }

    public String getString(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        SharedPreferences sharedPreferences = getSharedPreferences();
        if (sharedPreferences == null) {
            return null;
        }
        return sharedPreferences.getString(key, null);
    }
}
