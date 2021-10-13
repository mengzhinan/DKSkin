package com.duke.skinlib.skin.base;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.duke.skinlib.skin.core.SkinManager;
import com.duke.skinlib.skin.interfaces.ISkinChanged;

/**
 * Author: duke
 * DateTime: 2017-11-30 15:56
 * Description:
 * 需要换肤的Activity请继承此类，或者在你的Activity基类加入如下代码。<br/>
 * Activity内部的fragment不需要做任何处理。<br/>
 * 在需要换肤的layout页面跟布局添加属性 xmlns:app="http://schemas.android.com/apk/res-auto"。<br/>
 * 在需要换肤的layout页面view上添加属性 app:skinEnable="true"，报红没事。<br/>
 * 需要换肤的资源，比喻text和color等值，请使用引用，硬编码无法换肤的。<br/>
 */
@SuppressLint("Registered")
public class BaseSkinActivity extends AppCompatActivity
        implements ISkinChanged //实现此接口
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //设置LayoutInflater.Factory
        SkinManager.getInstance().setFactory2AndFirstInit(getLayoutInflater(), this, this);
        super.onCreate(savedInstanceState);
        //将此Activity加入到观察者集合中
        SkinManager.getInstance().addListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //将此Activity从观察者集合中移除
        SkinManager.getInstance().removeListener(this);
    }

    @Override
    public void notifySkinChanged() {
        //更新当前Activity内部所有需要换肤的view，包括Fragment和RecyclerView的item
        SkinManager.getInstance().apply(this);
    }

    /**
     * demo示例，请移到换肤设置页面对应位置：重置皮肤，即使用默认的
     */
    /*public void resetSkin() {
        SkinManager.getInstance().resetDefault();
    }*/

    /**
     * demo示例，请移到换肤设置页面对应位置：加载某一套皮肤
     *
     * @param skinApkFullPath xxx/xxx/xxx/xxx.apk
     */
    /*public void loadSkin(String skinApkFullPath) {
        SkinManager.getInstance().loadSkinPackage(skinApkFullPath, new ISkinLoadingListener() {

            @Override
            public void onStart() {
                //开始加载皮肤
            }

            @Override
            public void onSuccess() {
                //皮肤加载成功
            }

            @Override
            public void onFailure(String error) {
                //皮肤加载失败，error为失败原因
            }
        });
    }*/
}