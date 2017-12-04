package com.duke.skinlib.skin.core;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;

import com.duke.skinlib.skin.interfaces.ISkinViewAgent;
import com.duke.skinlib.skin.entities.ResourceInfo;
import com.duke.skinlib.skin.interfaces.ISkinChanged;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: duke
 * DateTime: 2017-11-30 15:17
 * Description: 页面view构建时回调的工厂类，借助此类抓取需要换肤的页面、view和支持的属性
 */
public class SkinInflaterFactory implements LayoutInflater.Factory2 {
    private static final Class<?>[] sConstructorSignature = new Class[]{Context.class, AttributeSet.class};
    private static final Map<String, Constructor<? extends View>> sConstructorMap = new HashMap<>();
    private final Object[] mConstructorArgs = new Object[2];
    private final String[] PREFIX_ARR = {"android.widget.", "android.view.", "android.webkit."};


    private ISkinChanged skinChangedListener;

    public SkinInflaterFactory(ISkinChanged skinChangedListener) {
        this.skinChangedListener = skinChangedListener;
    }


    private View createView(Context context, String name, String prefix)
            throws ClassNotFoundException, InflateException {
        Constructor<? extends View> constructor = sConstructorMap.get(name);
        try {
            if (constructor == null) {
                Class<? extends View> clazz = context.getClassLoader().loadClass(
                        prefix != null ? (prefix + name) : name).asSubclass(View.class);
                constructor = clazz.getConstructor(sConstructorSignature);
                sConstructorMap.put(name, constructor);
            }
            constructor.setAccessible(true);
            return constructor.newInstance(mConstructorArgs);
        } catch (Exception e) {
            return null;
        }
    }

    private View createViewFromTag(Context context, String name, AttributeSet attrs) {
        if (!TextUtils.isEmpty(name) && name.toLowerCase().equals("view")) {
            name = attrs.getAttributeValue(null, "class");
        }
        try {
            mConstructorArgs[0] = context;
            mConstructorArgs[1] = attrs;
            if (-1 == name.indexOf('.')) {
                View view = null;
                for (String prefix : PREFIX_ARR) {
                    view = createView(context, name, prefix);
                    if (view != null) {
                        break;
                    }
                }
                return view;
            } else {
                return createView(context, name, null);
            }
        } catch (Exception e) {
            return null;
        } finally {
            mConstructorArgs[0] = null;
            mConstructorArgs[1] = null;
        }
    }

    /**
     * 每一个view的创建，都会执行一次此方法
     *
     * @param parent
     * @param name
     * @param context
     * @param attrs
     * @return
     */
    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        Map<AttrType, ResourceInfo> skinViewAttrs = getAttrTypes(context, attrs);
        if (skinViewAttrs == null || skinViewAttrs.isEmpty()) {
            return null;
        }
        View view = null;
        try {
            view = createViewFromTag(context, name, attrs);
            if (view != null) {
                updateSkinViews(new SkinViewAgentImpl(view, skinViewAttrs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return null;
    }


    /**
     * 获取需要切换皮肤的控件的所有属性
     *
     * @param context
     * @param attrs
     * @return
     */
    public Map<AttrType, ResourceInfo> getAttrTypes(Context context, AttributeSet attrs) {
        Map<AttrType, ResourceInfo> attrTypes = new HashMap<>();
        if (attrs == null) {
            return attrTypes;
        }
        boolean isSkinEnable = false;
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            //获取属性名和属性值
            String attrName = attrs.getAttributeName(i);
            String attrValue = attrs.getAttributeValue(i);
            if (SkinConstants.SKIN_ENABLE_ATTR.equals(attrName)) {
                //目标项目view上是否设置了skinEnable=true值
                try {
                    isSkinEnable = Boolean.parseBoolean(attrValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            AttrType attrType = getSupportAttrTypes(attrName);
            if (attrType == null) continue;

            /**
             * @1065432107 ，即引用资源：@string/  @dimen/  @color/  等
             */
            if (!TextUtils.isEmpty(attrValue) && attrValue.startsWith("@")) {
                int resId = Integer.parseInt(attrValue.substring(1));
                ResourceInfo resourceInfo = ResourceInfo.getResourceInfo(context.getResources(), resId);
                if (resourceInfo != null) {
                    attrTypes.put(attrType, resourceInfo);
                }
            }
        }
        if (isSkinEnable) {
            return attrTypes;
        }
        return null;
    }

    /**
     * 获取支持切换皮肤的属性集合
     *
     * @param attrName 某控件的属性名
     * @return
     */
    private AttrType getSupportAttrTypes(String attrName) {
        for (AttrType attrType : AttrType.values()) {
            if (attrType.getAttrType().equals(attrName))
                return attrType;
        }
        return null;
    }

    private void updateSkinViews(ISkinViewAgent skinViewAgent) {
        SkinManager.getInstance().addSkinView(skinChangedListener, skinViewAgent);
        if (ResourceManager.getInstance().isNeedNotifyChangeSkin()) {
            skinViewAgent.applySkin();
        }
    }
}
