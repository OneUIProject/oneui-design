package dev.oneuiproject.oneui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SeslSeekBar;
import androidx.reflect.view.SeslHapticFeedbackConstantsReflector;

public class HapticSeekBar extends SeslSeekBar {
    private boolean mHasTickMark = false;
    private SeslSeekBar.OnSeekBarChangeListener onSeekBarChangeListener;

    public HapticSeekBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setOnSeekBarChangeListener(
            SeslSeekBar.OnSeekBarChangeListener onSeekBarChangeListener) {
        this.onSeekBarChangeListener = onSeekBarChangeListener;
        if (!mHasTickMark) {
            super.setOnSeekBarChangeListener(onSeekBarChangeListener);
        }
    }

    @Override
    public void setTickMark(Drawable drawable) {
        super.setTickMark(drawable);

        final boolean validDrawable = drawable != null;
        if (mHasTickMark != validDrawable) {
            mHasTickMark = validDrawable;
            if (validDrawable) {
                super.setOnSeekBarChangeListener(
                        new SeslSeekBar.OnSeekBarChangeListener() {
                            final int HAPTIC_CONSTANT_CURSOR_MOVE
                                    = SeslHapticFeedbackConstantsReflector.semGetVibrationIndex(41);

                            @Override
                            public void onProgressChanged(SeslSeekBar seekBar,
                                                          int progress, boolean fromUser) {
                                if (mHasTickMark && fromUser) {
                                    seekBar.performHapticFeedback(HAPTIC_CONSTANT_CURSOR_MOVE);
                                }

                                if (onSeekBarChangeListener != null) {
                                    onSeekBarChangeListener.onProgressChanged(
                                            seekBar, progress, fromUser);
                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeslSeekBar seekBar) {
                                if (onSeekBarChangeListener != null) {
                                    onSeekBarChangeListener.onStartTrackingTouch(seekBar);
                                }
                            }

                            @Override
                            public void onStopTrackingTouch(SeslSeekBar seekBar) {
                                if (onSeekBarChangeListener != null) {
                                    onSeekBarChangeListener.onStopTrackingTouch(seekBar);
                                }
                            }
                        });
            } else {
                super.setOnSeekBarChangeListener(onSeekBarChangeListener);
            }
        }
    }
}