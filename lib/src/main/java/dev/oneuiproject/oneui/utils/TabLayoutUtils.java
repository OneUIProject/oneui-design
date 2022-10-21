package dev.oneuiproject.oneui.utils;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;

import dev.oneuiproject.oneui.design.R;

public class TabLayoutUtils {
    private static final String TAG = "TabLayoutUtils";

    public interface TabButtonClickListener {
        void onClick(View v);
    }

    public static void addCustomButton(@NonNull TabLayout tabLayout, @DrawableRes int resId,
                                       @Nullable TabButtonClickListener listener) {
        if (tabLayout != null) {
            addCustomButton(tabLayout, tabLayout.getContext().getDrawable(resId), listener);
        } else {
            Log.e(TAG, "addCustomButton: tabLayout is null");
        }
    }

    public static void addCustomButton(@NonNull TabLayout tabLayout, @Nullable Drawable icon,
                                @Nullable TabButtonClickListener listener) {
        if (tabLayout != null) {
            TabLayout.Tab tab = tabLayout.newTab().setIcon(icon);
            tabLayout.addTab(tab);

            View tabView = getTabView(tabLayout, tab.getPosition());
            tabView.setBackground(tabLayout.getContext()
                    .getDrawable(R.drawable.oui_tab_layout_custom_button_background));
            tabView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            v.setPressed(true);
                            break;
                        case MotionEvent.ACTION_UP:
                            v.setPressed(false);
                            if (listener != null) {
                                listener.onClick(v);
                            }
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            v.setPressed(false);
                            break;
                    }

                    return true;
                }
            });
        } else {
            Log.e(TAG, "addCustomButton: tabLayout is null");
        }
    }

    @Nullable
    private static ViewGroup getTabViewGroup(@NonNull TabLayout tabLayout) {
        if (tabLayout.getChildCount() <= 0) {
            return null;
        }

        View view = tabLayout.getChildAt(0);
        if (view != null && view instanceof ViewGroup) {
            return (ViewGroup) view;
        }
        return null;
    }

    @Nullable
    private static View getTabView(@NonNull TabLayout tabLayout, int position) {
        ViewGroup viewGroup = getTabViewGroup(tabLayout);
        if (viewGroup != null && viewGroup.getChildCount() > position) {
            return viewGroup.getChildAt(position);
        }
        return null;
    }

}
