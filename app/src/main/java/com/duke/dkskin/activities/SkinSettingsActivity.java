package com.duke.dkskin.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.duke.dkskin.R;
import com.duke.dkskin.fragments.SkinSettingsFragment;
import com.duke.skinlib.log.LogHelper;
import com.duke.skinlib.skin.base.BaseSkinActivity;
import com.duke.skinlib.skin.core.SkinManager;
import com.duke.skinlib.skin.interfaces.ISkinLoadingListener;
import com.duke.skinlib.skin.interfaces.ISkinViewAgent;

public class SkinSettingsActivity extends BaseSkinActivity implements View.OnClickListener {
    private Button btn_skin_default;
    private Button btn_skin1;
    private ImageView iv_pic;
    private ImageView iv_pic2;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slin_setting);
        btn_skin_default = findViewById(R.id.btn_skin_default);
        btn_skin1 = findViewById(R.id.btn_skin1);
        iv_pic = findViewById(R.id.iv_pic);
        iv_pic2 = findViewById(R.id.iv_pic2);
        linearLayout = findViewById(R.id.line2);
        btn_skin_default.setOnClickListener(this);
        btn_skin1.setOnClickListener(this);
        iv_pic.setOnClickListener(this);
        iv_pic2.setOnClickListener(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.line2, new SkinSettingsFragment());
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_skin_default) {
            SkinManager.getInstance().resetDefault();
        } else if (v.getId() == R.id.btn_skin1) {
            load();
        } else if (v.getId() == R.id.iv_pic2) {
            ISkinViewAgent skinView = SkinManager.getInstance().getSkinView(this, v);
            skinView.setImageResource(R.mipmap.common_img);
        }
    }

    public void load() {
        SkinManager.getInstance().loadSkinPackage("/sdcard/skinResApp-debug.apk", new ISkinLoadingListener() {
            @Override
            public void onLibInit() {
                LogHelper.d("测试ddd", "loadSkinPackage.onLibInit");
            }

            @Override
            public void onStart() {
                LogHelper.d("测试ddd", "loadSkinPackage.onStart");
            }

            @Override
            public void onSuccess() {
                LogHelper.d("测试ddd", "loadSkinPackage.onSuccess");
            }

            @Override
            public void onFailure(String error) {
                LogHelper.d("测试ddd", "loadSkinPackage.onFailure:" + error);
            }
        });
    }
}
