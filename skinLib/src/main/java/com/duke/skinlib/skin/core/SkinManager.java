package com.duke.skinlib.skin.core;

import android.view.LayoutInflater;
import android.view.View;

import com.duke.skinlib.skin.entities.ResourceInfo;
import com.duke.skinlib.skin.interfaces.ISkinChanged;
import com.duke.skinlib.skin.interfaces.ISkinLoadingListener;
import com.duke.skinlib.skin.interfaces.ISkinViewAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: duke
 * DateTime: 2017-11-30 15:17
 * Description: 皮肤对外管理类(对外开放)
 */
public class SkinManager {
    //页面所有需要换肤的View
    private Map<ISkinChanged, List<ISkinViewAgent>> mSkinViewsMap = new HashMap<>();
    //需要换肤的页面
    private List<ISkinChanged> mSkinChangedListeners = new ArrayList<>();

    private SkinManager() {
    }

    private static class Instance {
        static SkinManager sInstance = new SkinManager();
    }

    public static SkinManager getInstance() {
        return Instance.sInstance;
    }

    /**
     * 获取资源包路径
     *
     * @return
     */
    public String getSkinApkFullPath() {
        return ResourceManager.getInstance().mSkinApkFullPath;
    }

    /**
     * 设置factory
     *
     * @param layoutInflater
     * @param skinChangedListener
     */
    public void setFactory2(LayoutInflater layoutInflater, ISkinChanged skinChangedListener) {
        if (layoutInflater == null || skinChangedListener == null) {
            return;
        }
        layoutInflater.setFactory2(new SkinInflaterFactory(skinChangedListener));
        /*try {
            Field field = LayoutInflater.class.getDeclaredField("mFactorySet");
            field.setAccessible(true);
            field.setBoolean(layoutInflater, false);
            layoutInflater.setFactory2(new SkinInflaterFactory(skinChangedListener));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * 加载皮肤插件包，主要目的是得到对应插件包里面的Resources对象
     *
     * @param skinApkFillPath
     * @param listener
     */
    public void loadSkinPackage(String skinApkFillPath, ISkinLoadingListener listener) {
        ResourceManager.getInstance().loadSkinPackage(skinApkFillPath, listener);
        notifySkinChanged();
    }

    /**
     * 添加换肤观察者对象
     *
     * @param skinChangedListener
     */
    public void addListener(ISkinChanged skinChangedListener) {
        mSkinChangedListeners.add(skinChangedListener);
    }

    /**
     * 移除换肤观察者对象和该页面的换肤views
     *
     * @param skinChangedListener
     */
    public void removeListener(ISkinChanged skinChangedListener) {
        mSkinViewsMap.remove(skinChangedListener);
        mSkinChangedListeners.remove(skinChangedListener);
    }

    /**
     * 恢复默认皮肤
     */
    public void resetDefault() {
        ResourceManager.getInstance().resetHostDefault();
        notifySkinChanged();
    }

    /**
     * 找到View自己的代理
     *
     * @param skinChanged
     * @param view
     * @return
     */
    public ISkinViewAgent getSkinView(ISkinChanged skinChanged, View view) {
        List<ISkinViewAgent> skinViews = mSkinViewsMap.get(skinChanged);
        for (ISkinViewAgent skinView : skinViews) {
            if (skinView.isOneself(view)) {
                return skinView;
            }
        }
        ISkinViewAgent skinView = new SkinViewAgentImpl(view, new HashMap<AttrType, ResourceInfo>());
        skinViews.add(skinView);
        return skinView;
    }

    /**
     * 添加皮肤View
     *
     * @param skinChanged
     * @param skinView
     */
    public void addSkinView(ISkinChanged skinChanged, ISkinViewAgent skinView) {
        List<ISkinViewAgent> skinViews = mSkinViewsMap.get(skinChanged);
        if (skinViews == null) {
            skinViews = new ArrayList<>();
        }
        skinViews.add(skinView);
        mSkinViewsMap.put(skinChanged, skinViews);
    }

    /**
     * 通知所有需要通知的UI
     */
    public void notifySkinChanged() {
        for (ISkinChanged skinChanged : mSkinChangedListeners) {
            skinChanged.notifySkinChanged();
        }
    }

    /**
     * 更新所有皮肤View
     *
     * @param skinChangedListener
     */
    public void apply(ISkinChanged skinChangedListener) {
        if (skinChangedListener == null) {
            return;
        }
        List<ISkinViewAgent> skinViews = mSkinViewsMap.get(skinChangedListener);
        if (skinViews == null) {
            return;
        }
        for (ISkinViewAgent skinView : skinViews) {
            if (skinView == null) continue;
            skinView.applySkin();
        }
    }
}
