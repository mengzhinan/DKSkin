package com.duke.dkskin.common;

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @Author: duke
 * @DateTime: 2019-03-12 13:33
 * @Description: 请求权限代理类
 */
public class DPermission {

    private static final String TAG_FRAGMENT = String.valueOf(DPermission.class.getName().hashCode());

    private final WeakReference<DFragment> mFragmentWeakReference;

    private DCallback mDCallback;

    public DPermission setCallback(DCallback dCallback) {
        this.mDCallback = dCallback;
        DFragment fragment = getCurrentFragment();
        if (fragment != null) {
            fragment.mDCallback = dCallback;
        }
        return this;
    }

    private DFragment getCurrentFragment() {
        if (mFragmentWeakReference != null &&
                mFragmentWeakReference.get() != null &&
                mFragmentWeakReference.get().isAdded()) {
            return mFragmentWeakReference.get();
        }
        return null;
    }


    public static DPermission newInstance(@NonNull final FragmentActivity activity) {
        return new DPermission(activity.getSupportFragmentManager());
    }

    public static DPermission newInstance(@NonNull final Fragment fragment) {
        return new DPermission(fragment.getChildFragmentManager());
    }

    private DPermission(@NonNull final FragmentManager fragmentManager) {
        DFragment dFragment = (DFragment) fragmentManager.findFragmentByTag(TAG_FRAGMENT);
        if (dFragment == null) {
            dFragment = new DFragment();
            fragmentManager
                    .beginTransaction()
                    .add(dFragment, TAG_FRAGMENT)
                    .commitNow();
        }
        mFragmentWeakReference = new WeakReference<>(dFragment);
    }

    /**
     * 过滤无效的权限
     *
     * @param permissions 用户设置的权限
     * @return 去除重复、无效后的权限
     */
    private String[] filterPermissions(String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return null;
        }
        HashSet<String> set = new HashSet<>();
        for (String permission : permissions) {
            if (isEmpty(permission)) {
                continue;
            }
            set.add(permission);
        }
        String[] permissionResult = new String[set.size()];
        Iterator<String> iterator = set.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            String p = iterator.next();
            if (isEmpty(p)) {
                continue;
            }
            permissionResult[index] = p;
            index++;
        }
        return permissionResult;
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().length() <= 0;
    }

    /**
     * 上层请求权限入口方法
     *
     * @param permissions 待申请的权限
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void startRequest(String... permissions) {
        // 过滤
        permissions = filterPermissions(permissions);
        if (permissions == null || permissions.length == 0) {
            return;
        }

        ArrayList<PermissionInfo> notRequestList = new ArrayList<>(permissions.length);
        ArrayList<String> needRequestList = new ArrayList<>(permissions.length);

        for (String permission : permissions) {
            if (isEmpty(permission)) {
                continue;
            }
            if (isGranted(permission)) {
                // isGranted = true
                notRequestList.add(new PermissionInfo(permission, true, false));
                continue;
            }
            if (isRevoked(permission)) {
                // isGranted = false
                notRequestList.add(new PermissionInfo(permission, false, false));
                continue;
            }

            needRequestList.add(permission);
        }

        //部分已经允许的权限，也可以在此提前返回

        if (!needRequestList.isEmpty()) {
            // 请求权限(有为允许且可能被用户允许的权限，需要向系统申请)
            // 由底层一层返回结果
            requestPermissionsFromFragment(notRequestList, needRequestList.toArray(new String[0]));
        } else {
            // 已经全部是不允许或者是被系统策略拒绝的权限
            // 就算调用 requestPermissions(string, int)，系统也不会回调 onRequestPermissionsResult() 函数
            // 此情况，只好自己向上层返回
            if (mDCallback != null && !notRequestList.isEmpty()) {
                mDCallback.onResult(notRequestList);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermissionsFromFragment(ArrayList<PermissionInfo> notRequestList, String[] permissions) {
        DFragment fragment = getCurrentFragment();
        if (fragment == null) {
            return;
        }
        // 保存已经允许或者系统拒绝的权限，底层一起返回
        fragment.notRequestList = notRequestList;
        // 底层请求权限
        fragment.requestPermissions(permissions);
    }


    /**
     * 如果已经授权，则返回true。<br/>
     * 如果 SDK < 23，则永远返回true。
     */
    private boolean isGranted(String permission) {
        DFragment fragment = getCurrentFragment();
        return !isMarshmallow() ||
                (fragment != null && fragment.isGranted(permission));
    }

    /**
     * 如果权限已被策略撤销，则返回true。<br/>
     * 如果 SDK < 23 ，则永远返回false。
     */
    private boolean isRevoked(String permission) {
        DFragment fragment = getCurrentFragment();
        return isMarshmallow() && fragment != null && fragment.isRevoked(permission);
    }

    /**
     * 是否是 >= 23
     *
     * @return 是否需要动态权限适配
     */
    private boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    //============================================================

    /**
     * @Author: duke
     * @DateTime: 2019-03-11 15:12
     * @Description: 请求权限fragment
     */
    public static class DFragment extends Fragment {

        private static final int PERMISSIONS_REQUEST_CODE = 1111;

        private ArrayList<PermissionInfo> notRequestList;

        private DCallback mDCallback;

        public DFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        @TargetApi(Build.VERSION_CODES.M)
        void requestPermissions(@NonNull String... permissions) {
            // 底层请求权限的方法
            requestPermissions(permissions, PERMISSIONS_REQUEST_CODE);
        }

        @TargetApi(Build.VERSION_CODES.M)
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            // 系统权限回调

            if (requestCode != PERMISSIONS_REQUEST_CODE) {
                return;
            }

            ArrayList<PermissionInfo> permissionResultList = new ArrayList<>(permissions.length);
            for (String permission : permissions) {
                permissionResultList.add(new PermissionInfo(permission,
                        isGranted(permission),
                        shouldShowRequestPermissionRationale(permission)));
                // 如果想了解跟多该方法的含义，请查看 PermissionInfo 类对应属性说明
                // shouldShowRequestPermissionRationale()
            }

            if (!notRequestList.isEmpty()) {
                // 累加上层已经确定的权限
                permissionResultList.addAll(notRequestList);
            }

            if (mDCallback != null && !permissionResultList.isEmpty()) {
                // 返回外层回调
                mDCallback.onResult(permissionResultList);
            }
        }

        @TargetApi(Build.VERSION_CODES.M)
        boolean isGranted(String permission) {
            final FragmentActivity fragmentActivity = getActivity();
            if (fragmentActivity == null) {
                throw new NullPointerException("Exception caused by fragment detached from activity.");
            }
            return fragmentActivity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        @TargetApi(Build.VERSION_CODES.M)
        boolean isRevoked(String permission) {
            final FragmentActivity fragmentActivity = getActivity();
            if (fragmentActivity == null) {
                throw new NullPointerException("Exception caused by fragment detached from activity.");
            }
            if (fragmentActivity.getPackageManager() == null) {
                throw new NullPointerException("Exception caused by activity.getPackageManager() == null.");
            }
            if (TextUtils.isEmpty(getActivity().getPackageName())) {
                throw new NullPointerException("Exception caused by activity.getPackageName() == null.");
            }
            return fragmentActivity.getPackageManager().isPermissionRevokedByPolicy(permission, getActivity().getPackageName());
        }

    }

    //============================================================

    /**
     * @Author: duke
     * @DateTime: 2019-03-11 15:10
     * @Description: 权限bean
     */
    public static class PermissionInfo {
        // 权限名称
        public String name;
        // 是否授权
        public boolean isGranted;

        /**
         * 是否需要向用户解释此权限功能。<br/>
         * 官网说明：https://developer.android.google.cn/training/permissions/requesting.html <br/>
         * <p>
         * 返回 true 情况： <br/>
         * 当用户 仅仅只是 拒绝 某项权限时，此方法返回 true。but，看 false 情况。 <br/>
         * <p>
         * 注意，返回 false 情况： <br/>
         * 如果用户在过去拒绝了权限请求，并在权限请求系统对话框中选择了 Don't ask again 选项，此方法将返回 false。 <br/>
         * 如果设备规范禁止应用具有该权限，此方法也会返回 false。 <br/>
         */
        public boolean isShouldShowRequestPermissionRationale;

        public PermissionInfo(String name,
                              boolean isGranted,
                              boolean isShouldShowRequestPermissionRationale) {
            this.name = name;
            this.isGranted = isGranted;
            this.isShouldShowRequestPermissionRationale = isShouldShowRequestPermissionRationale;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }

            if (!(o instanceof PermissionInfo)
                    || getClass() != o.getClass()) {
                return false;
            }

            final PermissionInfo that = (PermissionInfo) o;

            if (!name.equals(that.name)
                    || isGranted != that.isGranted
                    || isShouldShowRequestPermissionRationale != that.isShouldShowRequestPermissionRationale) {
                return false;
            }
            return true;
        }
    }

    //============================================================

    /**
     * @Author: duke
     * @DateTime: 2019-03-11 15:02
     * @Description: 权限请求回调
     */
    public interface DCallback {

        void onResult(ArrayList<PermissionInfo> permissionInfoList);

    }
}