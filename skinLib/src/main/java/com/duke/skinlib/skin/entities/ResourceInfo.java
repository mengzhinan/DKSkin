package com.duke.skinlib.skin.entities;

import android.content.res.Resources;
import android.text.TextUtils;

/**
 * Author: duke
 * DateTime: 2017-11-30 10:07
 * Description:
 */
public class ResourceInfo {
    private String resName;//资源名，比喻app_name、buy_btn_text等
    private String resType;//资源类型，drawable、color、mipmap、string等

    private ResourceInfo(String resName, String resType) {
        this.resName = resName;
        this.resType = resType;
    }

    public String getResName() {
        return resName;
    }

    public String getResType() {
        return resType;
    }

    /**
     * 根据资源id，获取资源名和类型，构造一个ResourceInfo对象
     */
    public static ResourceInfo getResourceInfo(Resources resources, int resId) {
        if (resources == null) {
            return null;
        }
        //程序中属性引用的键(比喻：app_name)
        String resName = resources.getResourceEntryName(resId);
        //引用的资源类型(比喻：string)
        String typeName = resources.getResourceTypeName(resId);
        if (TextUtils.isEmpty(resName) || TextUtils.isEmpty(typeName)) {
            return null;
        }
        return new ResourceInfo(resName, typeName);
    }
}
