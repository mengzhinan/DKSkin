package com.duke.dkskin.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duke.dkskin.R;
import com.duke.dkskin.common.DPermission;
import com.duke.dkskin.common.FileUtils;
import com.duke.dkskin.common.MyAdapter;
import com.duke.skinlib.skin.base.BaseSkinActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseSkinActivity implements View.OnClickListener, MyAdapter.OnItemClickCallback {
    private Button btn;
    private List<String> list;
    private RecyclerView recyclerview;
    private MyAdapter adapter;

    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        copyApk();
        initData();
        btn = findViewById(R.id.btn);
        btn.setOnClickListener(this);
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(adapter = new MyAdapter(this, list));
        adapter.setClickCallback(this);

    }

    private void copyApk() {
        String name = "skinResApp-debug.skin";
        File file = new File(getExternalFilesDir(null) + File.separator + name);
        FileUtils.copyAssetsFileTo(this, name, file);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn) {
            DPermission.newInstance(this)
                    .setCallback(new DPermission.DCallback() {

                        @Override
                        public void onResult(ArrayList<DPermission.PermissionInfo> permissionInfoList) {
                            startActivity(new Intent(MainActivity.this, SkinSettingsActivity.class));
                        }
                    }).startRequest(permissions);
        }
    }

    private void initData() {
        list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add("测试换肤" + (++i));
        }
    }

    @Override
    public void onItemClick(View view, int position, Object value) {
        Toast.makeText(this, value == null ? "null" : String.valueOf(value), Toast.LENGTH_SHORT).show();
    }

}
