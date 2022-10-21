package dev.oneuiproject.oneui.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SeslSeekBar;

import dev.oneuiproject.oneui.design.R;

public class SeekBarUtils {
    private static final String TAG = "SeekBarUtils";

    public static void showOverlapPreview(@NonNull SeslSeekBar seekBar,
                                          boolean show) {
        if (seekBar != null) {
            Context context = seekBar.getContext();
            final int bgColor = context.getColor(show
                    ? R.color.oui_seekbar_legacy_overlap_color_default
                    : R.color.oui_seekbar_overlap_color_default);
            final int fgColor = context.getColor(
                    R.color.oui_seekbar_overlap_color_activated);
            seekBar.setDualModeOverlapColor(bgColor, fgColor);
        } else {
            Log.e(TAG, "showOverlapPreview: seekBar is null");
        }
    }

    public static void showTickMark(@NonNull SeslSeekBar seekBar,
                                    boolean show) {
        if (seekBar != null) {
            Drawable d = show
                    ? seekBar.getContext().getDrawable(R.drawable.oui_seekbar_tick_mark)
                    : null;
            seekBar.setTickMark(d);

            ColorStateList progressColor = ColorStateList
                    .valueOf(seekBar.getContext().getColor(show
                            ? android.R.color.transparent
                            : R.color.sesl_seekbar_control_color_activated));
            seekBar.setProgressTintList(progressColor);
        } else {
            Log.e(TAG, "showTickMark: seekBar is null");
        }
    }

}
