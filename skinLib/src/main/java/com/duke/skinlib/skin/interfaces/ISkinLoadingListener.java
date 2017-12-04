package com.duke.skinlib.skin.interfaces;

/**
 * Author: duke
 * DateTime: 2017-11-30 10:07
 * Description: 皮肤加载过程的回调接口
 */
public interface ISkinLoadingListener {

    void onStart();

    void onSuccess();

    void onFailure(String error);
}
