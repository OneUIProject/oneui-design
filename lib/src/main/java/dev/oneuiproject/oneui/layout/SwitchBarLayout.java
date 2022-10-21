package dev.oneuiproject.oneui.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SeslSwitchBar;

import dev.oneuiproject.oneui.design.R;

/**
 * {@link ToolbarLayout} with a {@link SeslSwitchBar}.
 */
public class SwitchBarLayout extends ToolbarLayout {
    private static final String TAG = "SwitchBarLayout";

    private static final int MAIN_CONTENT = 0;

    private SeslSwitchBar mSwitchBar;
    private FrameLayout mSBLContainer;

    public SwitchBarLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(mContext)
                .inflate(R.layout.oui_layout_switchbarlayout, mMainContainer, true);
        mSwitchBar = findViewById(R.id.switchbarlayout_switchbar);
        mSBLContainer = findViewById(R.id.switchbarlayout_container);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (mSBLContainer == null) {
            super.addView(child, index, params);
        } else {
            if (((ToolbarLayoutParams) params).layout_location == MAIN_CONTENT) {
                mSBLContainer.addView(child, params);
            } else {
                super.addView(child, index, params);
            }
        }
    }

    /**
     * Returns the {@link SeslSwitchBar} in this layout.
     */
    @NonNull
    public SeslSwitchBar getSwitchBar() {
        return mSwitchBar;
    }
}
