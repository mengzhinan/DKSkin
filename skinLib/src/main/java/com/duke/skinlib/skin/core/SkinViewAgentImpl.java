package com.duke.skinlib.skin.core;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.duke.skinlib.skin.entities.ResourceInfo;
import com.duke.skinlib.skin.interfaces.ISkinViewAgent;

import java.util.Map;

/**
 * Author: duke
 * DateTime: 2017-11-30 10:07
 * Description:
 */
public class SkinViewAgentImpl implements ISkinViewAgent {
    private View mSkinView;
    private Map<AttrType, ResourceInfo> mSkinAttrs = null;

    public SkinViewAgentImpl(View view, Map<AttrType, ResourceInfo> skinAttrs) {
        mSkinView = view;
        mSkinAttrs = skinAttrs;
    }

    @Override
    public void setBackground(int resId) {
        ResourceInfo info = ResourceManager.getInstance().getHostResourceInfo(resId);
        mSkinAttrs.put(AttrType.BG, info);
        AttrType.BG.apply(mSkinView, info);
    }

    @Override
    public void setImageResource(int resId) {
        if (mSkinView instanceof ImageView) {
            ResourceInfo info = ResourceManager.getInstance().getHostResourceInfo(resId);
            mSkinAttrs.put(AttrType.SRC, info);
            AttrType.SRC.apply(mSkinView, info);
        }
    }

    @Override
    public void setTextColor(int resId) {
        if (mSkinView instanceof TextView) {
            ResourceInfo info = ResourceManager.getInstance().getHostResourceInfo(resId);
            mSkinAttrs.put(AttrType.COLOR, info);
            AttrType.COLOR.apply(mSkinView, info);
        }
    }

    @Override
    public void setText(int resId) {
        if (mSkinView instanceof TextView) {
            ResourceInfo info = ResourceManager.getInstance().getHostResourceInfo(resId);
            mSkinAttrs.put(AttrType.TEXT, info);
            AttrType.TEXT.apply(mSkinView, info);
        }
    }

    @Override
    public boolean isOneself(View view) {
        return mSkinView == view;
    }

    @Override
    public void applySkin() {
        if (mSkinAttrs == null || mSkinAttrs.isEmpty()) {
            return;
        }
        for (Map.Entry<AttrType, ResourceInfo> entry : mSkinAttrs.entrySet()) {
            entry.getKey().apply(mSkinView, entry.getValue());
        }
    }
}