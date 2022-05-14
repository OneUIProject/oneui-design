package dev.oneuiproject.oneui.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import androidx.reflect.content.res.SeslConfigurationReflector;

import dev.oneuiproject.oneui.utils.internal.ReflectUtils;

public class WindowManagerSupport {
    private static final String TAG = "WindowManagerSupport";

    public static int getSmallestDeviceWidthDp(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(displayMetrics);
        Log.d(TAG, "metrics = " + displayMetrics);
        return Math.round(Math.min(((float) displayMetrics.heightPixels) / displayMetrics.density, ((float) displayMetrics.widthPixels) / displayMetrics.density));
    }

    @SuppressLint({"WrongConstant", "RestrictedApi"})
    public static void hideStatusBarForLandscape(Activity activity, int orientation) {
        if (!DeviceInfo.isTabletDevice(activity.getApplicationContext()) && !SeslConfigurationReflector.isDexEnabled(activity.getResources().getConfiguration())) {
            WindowManager.LayoutParams params = activity.getWindow().getAttributes();
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (/*!isMultiWindowMode(activity)*/true) {
                    params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                } else {
                    params.flags &= -WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON;
                }

                ReflectUtils.genericInvokeMethod(params, "semAddExtensionFlags", 1 /* WindowManager.LayoutParams.SEM_EXTENSION_FLAG_RESIZE_FULLSCREEN_WINDOW_ON_SOFT_INPUT */);
            } else {
                params.flags &= -1025;

                ReflectUtils.genericInvokeMethod(params, "semClearExtensionFlags", 1 /* WindowManager.LayoutParams.SEM_EXTENSION_FLAG_RESIZE_FULLSCREEN_WINDOW_ON_SOFT_INPUT */);
            }

            activity.getWindow().setAttributes(params);
        }
    }

    /*public static boolean isMultiWindowMode(Activity activity) {
        if (Build.VERSION.SDK_INT >= 24) {
            return activity.isInMultiWindowMode();
        } else if (DeviceInfo.isSdlDevice()) {
            return new SMultiWindowActivity(activity).isMultiWindow();
        } else
            return false;
    }*/
}
