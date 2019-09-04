package com.tiangou.javacvtest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;


public class PermissionUtil {

    public static final int CHECK_CAMERA_PERMISSION = 1;
    public static final int CHECK_STORAGE_PERMISSION = 2;
    public static final int CHECK_READ_SMS_PERMISSION = 4;
    public static final int CHECK_BAIDU_MAP_LOCATION_PERMISSION = 5;//检查百度地图定位所需的权限
    public static final int CHECK_READ_SMS_PERMISSION_WHEN_REGISTER_OBSERVER = 6;
    public static final int CHECK_READ_PHTNE_STATE_PERMISSION = 7;


    /**
     * 检查读取短信的权限
     * @param activity
     * @return
     */
    public static boolean checkReadSmsPermission(Activity activity, boolean isRequest) {
        return checkReadSmsPermission(activity, isRequest, CHECK_READ_SMS_PERMISSION);
    }

    public static boolean checkReadSmsPermission(Activity activity, boolean isRequest, int requestCode) {
        return checkAndRequestPermission(
                activity,
                new String[]{
                        Manifest.permission.READ_SMS,
                },
                isRequest,
                requestCode);
    }
    /**
     * 检查定位所需要的权限
     * @param activity
     * @param isRequest
     * @return
     */
    public static boolean checkLocationPermission(Activity activity, boolean isRequest) {
        return checkAndRequestPermission(
                activity,
                new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                },
                isRequest,
                CHECK_BAIDU_MAP_LOCATION_PERMISSION);
    }

    /**
     * 检查相机权限
     * @param activity
     * @return
     */
    public static boolean checkCameraPermission(Activity activity, boolean isRequest) {

        return checkAndRequestPermission(
                activity,
                new String[]{
                        Manifest.permission.CAMERA,
                },
                isRequest,
                CHECK_CAMERA_PERMISSION);
    }

    public static boolean checkPhoneStatePermission(Context context) {
        return isGranted(context, Manifest.permission.READ_PHONE_STATE);
    }

    /**
     * 检查读取外部存储权限
     * @param activity
     * @return
     */
    public static boolean checkStoragePermission(Activity activity, boolean isRequest) {
        return checkStoragePermission(activity, isRequest, CHECK_STORAGE_PERMISSION);
    }

    public static boolean checkStoragePermission(Activity activity, boolean isRequest, int requestCode) {
        return checkAndRequestPermission(
                activity,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                },
                isRequest,
                requestCode);
    }
    /**
     * 判断权限是否被授权
     * @param activity
     * @param permissionName
     * @return
     */
    public static boolean isGranted(@NonNull Context activity, @NonNull String permissionName) {
        return ContextCompat.checkSelfPermission(activity, permissionName) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     *  判断系统版本是否为M及以上
     * @return
     */
    public static boolean isSysAboveM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }


    public static boolean checkAndRequestPermission(@NonNull Activity activity, @NonNull String[] permissions, boolean isRequest, int requestCode) {
        List<String> permissionList = new ArrayList<>();

        if (permissions != null) {
            for (int i = 0; i < permissions.length; i++) {
                if (!TextUtils.isEmpty(permissions[i]) && !isGranted(activity, permissions[i])) {
                    permissionList.add(permissions[i]);
                }
            }
        }

        if (permissionList.size() > 0) {
            if (isSysAboveM() && isRequest) {
                ActivityCompat.requestPermissions(activity, permissionList.toArray(new String[permissionList.size()]), requestCode);
            }
            return false;
        } else {
            return true;
        }
    }
}
