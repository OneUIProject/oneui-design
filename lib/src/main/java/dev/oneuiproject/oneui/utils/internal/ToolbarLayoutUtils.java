package dev.oneuiproject.oneui.utils.internal;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.reflect.content.res.SeslConfigurationReflector;

import java.lang.reflect.InvocationTargetException;

/**
 * @hide
 */
@RestrictTo(LIBRARY)
public class ToolbarLayoutUtils {

    /**
     * @hide
     */
    @RestrictTo(LIBRARY)
    static class DeviceFeature {
        private static double getDensity(@NonNull Context context) {
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager wm = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);

            Display display = null;
            if (wm != null) {
                display = wm.getDefaultDisplay();
            }

            if (display == null) {
                return 1.d;
            }
            display.getRealMetrics(metrics);

            Configuration config = context.getResources().getConfiguration();
            return config.densityDpi / metrics.densityDpi;
        }

        private static String getSystemProp(@Nullable String key) {
            String value = null;

            try {
                value = (String) Class.forName("android.os.SystemProperties")
                        .getMethod("get", String.class).invoke(null, key);
            } catch (IllegalAccessException | InvocationTargetException
                    | NoSuchMethodException | ClassNotFoundException e) {
                value = "Unknown";
            }

            return value;
        }

        static boolean isScreenDp(@NonNull Context context,
                                  int screenDp) {
            final int smallestScreenWidthDp = context.getResources()
                    .getConfiguration().smallestScreenWidthDp;
            final int screenSizeDp
                    = (int) (smallestScreenWidthDp * getDensity(context));
            return screenSizeDp > screenDp;
        }

        static boolean isTablet(@NonNull Context context) {
            PackageManager pm = context.getPackageManager();
            if (pm != null
                    && pm.hasSystemFeature("com.samsung.feature.device_category_tablet"))
                return true;

            return getSystemProp("ro.build.characteristics").equals("tablet");
        }
    }

    /**
     * @hide
     */
    private static boolean isInMultiWindowMode(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= 24) {
            return activity.isInMultiWindowMode();
        } else
            return false;
    }

    /**
     * @hide
     */
    @RestrictTo(LIBRARY)
    public static void hideStatusBarForLandscape(@NonNull Activity activity,
                                                 int orientation) {
        if (!DeviceFeature.isTablet(activity) && !SeslConfigurationReflector
                .isDexEnabled(activity.getResources().getConfiguration())) {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (isInMultiWindowMode(activity) || DeviceFeature.isScreenDp(activity, 420)) {
                    lp.flags &= -(WindowManager.LayoutParams.FLAG_FULLSCREEN
                            | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
                } else {
                    lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                }

                ReflectUtils.genericInvokeMethod(
                        WindowManager.LayoutParams.class,
                        lp,
                        "semAddExtensionFlags",
                        1 /* WindowManager.LayoutParams.SEM_EXTENSION_FLAG_RESIZE_FULLSCREEN_WINDOW_ON_SOFT_INPUT */);
            } else {
                lp.flags &= -(WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

                ReflectUtils.genericInvokeMethod(
                        WindowManager.LayoutParams.class,
                        lp,
                        "semClearExtensionFlags",
                        1 /* WindowManager.LayoutParams.SEM_EXTENSION_FLAG_RESIZE_FULLSCREEN_WINDOW_ON_SOFT_INPUT */);
            }
            activity.getWindow().setAttributes(lp);
        }
    }

    /**
     * @hide
     */
    @RestrictTo(LIBRARY)
    public static void updateListBothSideMargin(@NonNull Activity activity,
                                                @NonNull ViewGroup layout) {
        if (layout != null && activity != null
                && !activity.isDestroyed() && !activity.isFinishing()) {
            activity.findViewById(android.R.id.content).post(new Runnable() {
                @Override
                public void run() {
                    if (activity.getResources().getConfiguration()
                            .screenWidthDp >= 589) {
                        layout.getLayoutParams().width = MATCH_PARENT;
                    }
                    setHorizontalMargin(layout, getSideMargin(activity));
                }
            });
        }
    }

    /**
     * @hide
     */
    private static void setHorizontalMargin(@NonNull ViewGroup layout, int margin) {
        ViewGroup.MarginLayoutParams lp
                = (ViewGroup.MarginLayoutParams) layout.getLayoutParams();
        if (lp != null) {
            lp.leftMargin = margin;
            lp.rightMargin = margin;
        } else {
            layout.setPadding(margin, 0, margin, 0);
        }
    }

    /**
     * @hide
     */
    private static int getSideMargin(@NonNull Activity activity) {
        final int width = activity.findViewById(android.R.id.content).getWidth();
        Configuration config = activity.getResources().getConfiguration();
        return (int) (width * getMarginRatio(config.screenWidthDp, config.screenHeightDp));
    }

    /**
     * @hide
     */
    private static float getMarginRatio(int screenWidthDp, int screenHeightDp) {
        if (screenWidthDp < 589) {
            return 0.0f;
        }
        if (screenHeightDp > 411 && screenWidthDp <= 959) {
            return 0.05f;
        }
        if (screenWidthDp >= 960 && screenHeightDp <= 1919) {
            return 0.125f;
        }
        if (screenWidthDp >= 1920) {
            return 0.25f;
        }

        return 0.0f;
    }

}
