package com.duke.skinlib.skin.core;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.duke.skinlib.skin.entities.ResourceInfo;

/**
 * Author: duke
 * DateTime: 2017-12-01 19:09
 * Description: 支持的换肤属性
 */
public enum AttrType {
    BG("background") {
        @Override
        public void apply(View view, ResourceInfo resourceInfo) {
            if (view == null) {
                return;
            }
            Drawable drawable = ResourceManager.getInstance().getDrawable(resourceInfo);
            if (drawable == null) {
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(drawable);
            } else {
                view.setBackgroundDrawable(drawable);
            }
        }
    },
    SRC("src") {
        @Override
        public void apply(View view, ResourceInfo resourceInfo) {
            if (view == null || !(view instanceof ImageView)) {
                return;
            }
            Drawable drawable = ResourceManager.getInstance().getDrawable(resourceInfo);
            if (drawable == null) {
                return;
            }
            ((ImageView) view).setImageDrawable(drawable);
        }
    },
    COLOR("textColor") {
        @Override
        public void apply(View view, ResourceInfo resourceInfo) {
            if (view == null || !(view instanceof TextView)) {
                return;
            }
            ColorStateList colorStateList = ResourceManager.getInstance().getColorStateList(resourceInfo);
            if (colorStateList == null) {
                return;
            }
            ((TextView) view).setTextColor(colorStateList);
        }
    },
    TEXT("text") {
        @Override
        public void apply(View view, ResourceInfo resourceInfo) {
            if (view == null || !(view instanceof TextView)) {
                return;
            }
            String string = ResourceManager.getInstance().getString(resourceInfo);
            if (TextUtils.isEmpty(string)) {
                return;
            }
            ((TextView) view).setText(string);
        }
    };

    private String attrType;

    AttrType(String attrType) {
        this.attrType = attrType;
    }

    public String getAttrType() {
        return this.attrType;
    }

    public abstract void apply(View view, ResourceInfo resourceInfo);
}
