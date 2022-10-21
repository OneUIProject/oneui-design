package dev.oneuiproject.oneui.utils;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.indexscroll.widget.SeslIndexScrollView;

import dev.oneuiproject.oneui.design.R;

public class IndexScrollUtils {
    private static final String TAG = "IndexScrollUtils";

    public static void animateVisibility(@NonNull SeslIndexScrollView indexScroll,
                                          boolean visible) {
        if (indexScroll != null) {
            boolean isViewVisible = indexScroll.getVisibility() == View.VISIBLE;

            if (isViewVisible != visible) {
                Animation anim = AnimationUtils.loadAnimation(indexScroll.getContext(), visible
                        ? R.anim.oui_index_scroll_show
                        : R.anim.oui_index_scroll_hide);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        if (visible) {
                            indexScroll.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (!visible) {
                            indexScroll.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) { }
                });

                indexScroll.startAnimation(anim);
            }
        } else {
            Log.e(TAG, "animateVisibility: indexScroll is null");
        }
    }
}
