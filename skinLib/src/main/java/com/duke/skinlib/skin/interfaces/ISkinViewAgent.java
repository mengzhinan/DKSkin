package com.duke.skinlib.skin.interfaces;

import android.view.View;

/**
 * Author: duke
 * DateTime: 2017-11-30 10:07
 * Description: 换肤代理view的接口
 */
public interface ISkinViewAgent {
    void setBackground(int resId);

    void setImageResource(int resId);

    void setTextColor(int resId);

    void setText(int resId);

    /**
     * 是否是自己
     *
     * @param view
     * @return
     */
    boolean isOneself(View view);

    /**
     * 申请批复生效
     */
    void applySkin();
}