package com.duke.skinlib.skin.core;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.duke.skinlib.skin.entities.ResourceInfo;
import com.duke.skinlib.skin.interfaces.ISkinLoadingListener;

import java.lang.reflect.Method;

/**
 * Author: duke
 * DateTime: 2017-11-30 10:07
 * Description: 皮肤资源管理类
 */
public class ResourceManager {

    //host主项目的上下文
    private Context mContext;

    //当前host加载的插件皮肤包apk的资源对象
    private Resources mCurrentResources;
    //host主项目的资源对象
    private Resources mHostResources;

    //当前host加载的插件皮肤包apk的包名
    private String mCurrentPackageName;
    //host主项目的包名
    private String mHostPackageName;

    //标识是否已经初始化
    private boolean isInited;

    //当前加载的皮肤包路径
    String mSkinApkFullPath;


    private static class Instance {
        private static ResourceManager sInstance = new ResourceManager();
    }

    private ResourceManager() {
    }

    static ResourceManager getInstance() {
        return Instance.sInstance;
    }

    /**
     * 使用(等效): SkinManager.getInstance().init(getApplicationContext())
     *
     * @param context
     */
    void init(Context context) {
        if (context == null || isInited) {
            return;
        }
        //host的application对象
        mContext = context.getApplicationContext();
        mHostResources = mContext.getResources();
        mHostPackageName = mContext.getPackageName();
        //暂时使用host的Resources资源对象
        mCurrentResources = mHostResources;
        isInited = true;
    }

    void loadSkinPackage(String skinApkFullPath) {
        loadSkinPackage(skinApkFullPath, null);
    }

    /**
     * 使用(等效): SkinManager.getInstance().loadSkinPackage(getApplicationContext())
     *
     * @param skinApkFullPath
     * @param listener
     */
    void loadSkinPackage(String skinApkFullPath, ISkinLoadingListener listener) {
        if (TextUtils.isEmpty(skinApkFullPath)) {
            return;
        }
        mSkinApkFullPath = skinApkFullPath;
        try {
            if (listener != null) {
                listener.onStart();
            }
            PackageInfo packageInfo = mContext.getPackageManager().getPackageArchiveInfo(mSkinApkFullPath, 0);
            if (packageInfo == null || TextUtils.isEmpty(packageInfo.packageName)) {
                if (listener != null) {
                    listener.onFailure("packageName is null");
                }
                return;
            }
            mCurrentPackageName = packageInfo.packageName;
            mCurrentResources = createSkinResources(mHostResources, createAssetManager(mSkinApkFullPath));
            if (mCurrentResources == null) {
                throw new IllegalArgumentException("skin Resources is null, load skin failure");
            } else {
                if (listener != null) {
                    listener.onSuccess();
                }
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.onFailure(e.getMessage());
            }
        }
    }

    /**
     * 创建AssetManager对象, 使用java的反射方式
     *
     * @param skinApkFullPath
     * @return
     */
    private static AssetManager createAssetManager(String skinApkFullPath) {
        if (TextUtils.isEmpty(skinApkFullPath)) {
            return null;
        }
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            //Method addAssetPath = assetManager.getClass().getDeclaredMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, skinApkFullPath);
            return assetManager;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建目标皮肤包里面的 Resources 对象
     *
     * @param resources
     * @param assetManager
     * @return
     */
    private Resources createSkinResources(Resources resources, AssetManager assetManager) {
        if (resources == null || assetManager == null) {
            return null;
        }
        return new Resources(assetManager, resources.getDisplayMetrics(), resources.getConfiguration());
    }

    /**
     * 判断是否需要通知换肤
     *
     * @return
     */
    boolean isNeedNotifyChangeSkin() {
        return mHostResources != mCurrentResources;
    }

    /**
     * 重置当前资源，即切换到host状态
     */
    void resetHostDefault() {
        mCurrentResources = mHostResources;
        mCurrentPackageName = mHostPackageName;
    }

    private int getIdentifier(Resources resources, ResourceInfo resourceInfo, String packageName) {
        if (resources == null || resourceInfo == null || TextUtils.isEmpty(packageName)) {
            return 0;
        }
        return resources.getIdentifier(resourceInfo.resName, resourceInfo.resType, packageName);
    }


    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    /**
     * 根据资源id，获取资源名和类型，构造一个ResourceInfo对象
     */
    ResourceInfo getResourceInfo(int resId) {
        ResourceInfo resourceInfo = ResourceInfo.getResourceInfo(mCurrentResources, resId);
        if (resourceInfo == null) {
            resourceInfo = ResourceInfo.getResourceInfo(mHostResources, resId);
        }
        return resourceInfo;
    }

    /**
     * 获取当前资源id
     */
    int getResId(ResourceInfo resourceInfo) {
        if (resourceInfo == null) {
            return 0;
        }
        return getIdentifier(mCurrentResources, resourceInfo, mCurrentPackageName);
    }

    /**
     * 获取host的资源id
     */
    int getHostResId(ResourceInfo resourceInfo) {
        if (resourceInfo == null) {
            return 0;
        }
        return getIdentifier(mHostResources, resourceInfo, mHostPackageName);
    }

    XmlResourceParser getLayout(ResourceInfo resourceInfo) {
        if (resourceInfo == null) {
            return null;
        }
        try {
            return mCurrentResources.getLayout(getResId(resourceInfo));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mHostResources.getLayout(getHostResId(resourceInfo));
    }

    XmlResourceParser getAnimation(ResourceInfo resourceInfo) {
        if (resourceInfo == null) {
            return null;
        }
        try {
            return mCurrentResources.getAnimation(getResId(resourceInfo));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return mHostResources.getAnimation(getHostResId(resourceInfo));
    }

    //获取对应资源drawable
    Drawable getDrawable(ResourceInfo resourceInfo) {
        if (resourceInfo == null) {
            return null;
        }
        try {
            return mCurrentResources.getDrawable(getResId(resourceInfo));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return mHostResources.getDrawable(getHostResId(resourceInfo));
    }

    //获取颜色
    ColorStateList getColorStateList(ResourceInfo resourceInfo) {
        if (resourceInfo == null) {
            return null;
        }
        try {
            return mCurrentResources.getColorStateList(getResId(resourceInfo));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return mHostResources.getColorStateList(getHostResId(resourceInfo));
    }

    float getDimen(ResourceInfo resourceInfo) {
        if (resourceInfo == null) {
            return 0.0F;
        }
        try {
            return mCurrentResources.getDimension(getResId(resourceInfo));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mHostResources.getDimension(getHostResId(resourceInfo));
    }

    String getString(ResourceInfo resourceInfo) {
        if (resourceInfo == null) {
            return null;
        }
        try {
            return mCurrentResources.getString(getResId(resourceInfo));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mHostResources.getString(getHostResId(resourceInfo));
    }
}