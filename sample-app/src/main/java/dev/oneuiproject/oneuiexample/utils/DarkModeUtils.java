package dev.oneuiproject.oneuiexample.utils;

import android.app.UiModeManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class DarkModeUtils {
    public static final int DARK_MODE_AUTO = 2;
    public static final int DARK_MODE_DISABLED = 0;
    public static final int DARK_MODE_ENABLED = 1;

    private static final String NAME = "DarkModeUtils",
            KEY_DARK_MODE = "dark_mode";

    public static Configuration createDarkModeConfig(Context context, Configuration config) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(
                NAME, Context.MODE_PRIVATE);
        final int darkMode = sharedPrefs.getInt(KEY_DARK_MODE, DARK_MODE_AUTO);

        final int uiModeNight;
        switch (darkMode) {
            case DARK_MODE_DISABLED:
                uiModeNight = Configuration.UI_MODE_NIGHT_NO;
                break;
            case DARK_MODE_ENABLED:
                uiModeNight = Configuration.UI_MODE_NIGHT_YES;
                break;
            default:
                UiModeManager uiModeManager = (UiModeManager) context
                        .getSystemService(Context.UI_MODE_SERVICE);
                uiModeNight = uiModeManager.getCurrentModeType() & Configuration.UI_MODE_NIGHT_MASK;
                break;
        }

        Configuration newConfig = new Configuration(config);
        final int newUiMode = newConfig.uiMode & -Configuration.UI_MODE_NIGHT_MASK
                | Configuration.UI_MODE_TYPE_NORMAL;
        newConfig.uiMode = newUiMode | uiModeNight;
        return newConfig;
    }

    public static ContextWrapper createDarkModeContextWrapper(Context context) {
        Configuration newConfig = createDarkModeConfig(
                context, context.getResources().getConfiguration());
        if (newConfig != null) {
            return new ContextWrapper(context.createConfigurationContext(newConfig));
        } else {
            return new ContextWrapper(context);
        }
    }

    public static int getDarkMode(Context context) {
        SharedPreferences sharedPreferences = context
                .getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_DARK_MODE, DARK_MODE_AUTO);
    }

    public static void setDarkMode(AppCompatActivity activity, int mode) {
        if (getDarkMode(activity) != mode) {
            SharedPreferences sharedPreferences = activity.getSharedPreferences(
                    NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(KEY_DARK_MODE, mode).apply();
        }

        if (mode != DARK_MODE_AUTO) {
            AppCompatDelegate.setDefaultNightMode(mode == DARK_MODE_ENABLED
                    ? AppCompatDelegate.MODE_NIGHT_YES
                    : AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

        activity.getDelegate().applyDayNight();
    }
}