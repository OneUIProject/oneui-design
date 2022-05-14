package dev.oneuiproject.oneui.utils;

import android.app.Activity;
import android.content.res.Configuration;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class ViewSupport {

    public static void setHorizontalMargin(ViewGroup viewGroup, int margin) {
        ViewGroup.LayoutParams layoutParams = viewGroup.getLayoutParams();
        if (layoutParams instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) layoutParams;
            lp.setMargins(margin, 0, margin, 0);
            viewGroup.setLayoutParams(lp);
        } else if (layoutParams instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) layoutParams;
            lp.setMargins(margin, 0, margin, 0);
            viewGroup.setLayoutParams(lp);
        }
    }

    public static void updateListBothSideMargin(final Activity activity, final ViewGroup viewGroup) {
        if (viewGroup != null && activity != null && !activity.isDestroyed() && !activity.isFinishing()) {
            activity.findViewById(android.R.id.content).post(new Runnable() {
                public void run() {
                    int width = activity.findViewById(android.R.id.content).getWidth();
                    Configuration configuration = activity.getResources().getConfiguration();
                    if (configuration.screenHeightDp <= 411 || configuration.screenWidthDp < 512) {
                        setHorizontalMargin(viewGroup, 0);
                        return;
                    }
                    viewGroup.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                    int screenWidthDp = configuration.screenWidthDp;
                    if (screenWidthDp < 685 || screenWidthDp > 959) {
                        if (screenWidthDp >= 960 && screenWidthDp <= 1919) {
                            int i = (int) (((float) width) * 0.125f);
                            setHorizontalMargin(viewGroup, i);
                        } else if (configuration.screenWidthDp >= 1920) {
                            int i = (int) (((float) width) * 0.25f);
                            setHorizontalMargin(viewGroup, i);
                        } else {
                            setHorizontalMargin(viewGroup, 0);
                        }
                    } else {
                        int i = (int) (((float) width) * 0.05f);
                        setHorizontalMargin(viewGroup, i);
                    }
                }
            });
        }
    }
}
