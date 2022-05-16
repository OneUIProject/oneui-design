package dev.oneuiproject.oneui.layout;

import static androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
import static androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.content.ContextCompat;

import java.text.NumberFormat;
import java.util.Locale;

import dev.oneuiproject.oneui.R;
import dev.oneuiproject.oneui.utils.ViewUtils;

public class DrawerLayout extends ToolbarLayout {
    private static final String TAG = "DrawerLayout";

    private static final float DEFAULT_DRAWER_RADIUS = 15.f;

    private static final int DRAWER_HEADER = 4;
    private static final int DRAWER_PANEL = 5;

    private NumberFormat mNumberFormat
            = NumberFormat.getInstance(Locale.getDefault());
    private OnBackPressedCallback mOnBackPressedCallback
            = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (mDrawer.isDrawerOpen(mDrawerContent)) {
                mDrawer.closeDrawer(mDrawerContent, true);
                return;
            }
            this.setEnabled(false);
            mActivity.onBackPressed();
            this.setEnabled(true);
        }
    };

    private boolean mIsRtl;

    private androidx.drawerlayout.widget.DrawerLayout mDrawer;
    private LinearLayout mToolbarContent;
    private LinearLayout mDrawerContent;
    private View mHeaderView;
    private AppCompatImageButton mHeaderButton;
    private TextView mHeaderBadge;
    private FrameLayout mDrawerContainer;

    public DrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initDrawer();

        if (!isInEditMode()) {
            mActivity.getOnBackPressedDispatcher().addCallback(mOnBackPressedCallback);
        }

        ViewUtils.semSetRoundedCorners(
                mActivity.getWindow().getDecorView(), ViewUtils.SEM_ROUNDED_CORNER_NONE);
    }

    @Override
    protected void initLayoutAttrs(@Nullable AttributeSet attrs) {
        super.initLayoutAttrs(attrs);

        TypedArray a = mContext.getTheme()
                .obtainStyledAttributes(
                        attrs, R.styleable.ToolbarLayout, 0, 0);
        try {
            mLayout = a.getResourceId(R.styleable.ToolbarLayout_android_layout,
                    R.layout.oui_layout_drawerlayout);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void inflateChildren() {
        if (mLayout != R.layout.oui_layout_drawerlayout) {
            Log.w(TAG, "Inflating custom " + TAG);
        }
        LayoutInflater.from(mContext)
                .inflate(mLayout, this, true);
    }

    private void initDrawer() {
        mIsRtl = getResources().getConfiguration()
                .getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;

        setNavigationButtonIcon(
                ContextCompat.getDrawable(mContext, R.drawable.ic_oui_drawer));
        setNavigationButtonTooltip(
                getResources().getText(R.string.oui_navigation_drawer));

        mDrawer = findViewById(R.id.drawerlayout_drawer);
        mToolbarContent = findViewById(R.id.drawerlayout_toolbar_content);
        mDrawerContent = findViewById(R.id.drawerlayout_drawer_content);

        mHeaderView = mDrawerContent.findViewById(R.id.drawerlayout_default_header);
        mHeaderButton = mHeaderView.findViewById(R.id.drawerlayout_header_button);
        mHeaderBadge = mHeaderView.findViewById(R.id.drawerlayout_header_badge);

        mDrawerContainer = mDrawerContent.findViewById(R.id.drawerlayout_drawer_container);

        mDrawer.setScrimColor(mContext.getColor(R.color.oui_drawerlayout_drawer_dim_color));
        mDrawer.setDrawerElevation(0);
        setDrawerWidth();
        setDrawerCornerRadius(DEFAULT_DRAWER_RADIUS);

        if (!isInEditMode()) {
            View translationView = findViewById(R.id.drawer_custom_translation);
            if (translationView == null) translationView = mToolbarContent;
            View content = translationView;

            Window window = mActivity.getWindow();
            ActionBarDrawerToggle listener
                    = new ActionBarDrawerToggle(mActivity, mDrawer,
                    0, 0) {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    super.onDrawerSlide(drawerView, slideOffset);

                    float slideX = drawerView.getWidth() * slideOffset;
                    if (mIsRtl) slideX *= -1;
                    content.setTranslationX(slideX);

                    float[] hsv = new float[3];
                    Color.colorToHSV(mContext.getColor(R.color.oui_round_and_bgcolor), hsv);
                    hsv[2] *= 1f - (slideOffset * 0.2f);
                    window.setStatusBarColor(Color.HSVToColor(hsv));
                    window.setNavigationBarColor(Color.HSVToColor(hsv));
                }
            };
            mDrawer.addDrawerListener(listener);

            setNavigationButtonOnClickListener(v -> mDrawer.openDrawer(mDrawerContent));
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (mToolbarContent == null || mDrawerContainer == null) {
            super.addView(child, index, params);
        } else {
            switch (((ToolbarLayoutParams) params).layout_location) {
                case DRAWER_HEADER:
                    mDrawerContent.removeView(mHeaderView);
                    mHeaderButton = null;
                    mHeaderBadge = null;
                    mDrawerContent.addView(child, 0, params);
                    mHeaderView = mDrawerContent.getChildAt(0);
                    break;
                case DRAWER_PANEL:
                    mDrawerContainer.addView(child, params);
                    break;
                default:
                    super.addView(child, index, params);
                    break;
            }
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mIsRtl = newConfig.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
        setDrawerWidth();
        setDrawerOpen(false, false);
    }

    private void lockDrawerIfAvailable(boolean lock) {
        if (mDrawer != null) {
            if (lock) {
                mDrawer.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED);
            } else {
                mDrawer.setDrawerLockMode(LOCK_MODE_UNLOCKED);
            }
        }
    }

    private void setDrawerWidth() {
        ViewGroup.LayoutParams lp = mDrawerContent.getLayoutParams();

        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        final int displayWidth = size.x;
        final float density = getResources().getDisplayMetrics().density;
        final float dpi = (float) displayWidth / density;

        double widthRate;
        if (dpi >= 1920.0F) {
            widthRate = 0.22D;
        } else if (dpi >= 960.0F && dpi < 1920.0F) {
            widthRate = 0.2734D;
        } else if (dpi >= 600.0F && dpi < 960.0F) {
            widthRate = 0.46D;
        } else if (dpi >= 480.0F && dpi < 600.0F) {
            widthRate = 0.5983D;
        } else {
            widthRate = 0.844D;
        }

        lp.width = (int) ((double) displayWidth * widthRate);
    }

    @Override
    public void showActionMode() {
        lockDrawerIfAvailable(true);
        super.showActionMode();
    }

    @Override
    public void dismissActionMode() {
        super.dismissActionMode();
        lockDrawerIfAvailable(false);
    }

    @Override
    public void showSearchMode() {
        lockDrawerIfAvailable(true);
        super.showSearchMode();
    }

    @Override
    public void dismissSearchMode() {
        super.dismissSearchMode();
        lockDrawerIfAvailable(false);
    }

    //
    // Drawer methods
    //
    public void showDrawerTopMargin(boolean show) {
        MarginLayoutParams lp = (MarginLayoutParams) mDrawerContent.getLayoutParams();
        lp.topMargin = show
                ? getResources().getDimensionPixelSize(R.dimen.oui_drawerlayout_drawer_top_margin)
                : 0;
        mDrawerContent.setLayoutParams(lp);
    }

    public void setDrawerCornerRadius(@Dimension float dp) {
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        setDrawerCornerRadius(px);
    }

    public void setDrawerCornerRadius(@Px int px) {
        mDrawerContent.setOutlineProvider(new DrawerOutlineProvider(px));
        mDrawerContent.setClipToOutline(true);
    }

    public void setDrawerButtonIcon(@Nullable Drawable icon) {
        if (mHeaderButton != null) {
            mHeaderButton.setImageDrawable(icon);
            mHeaderButton.setImageTintList(ColorStateList.valueOf(
                            mContext.getColor(R.color.oui_drawerlayout_header_icon_color)));
            mHeaderView.setVisibility(icon != null ? View.VISIBLE : View.GONE);
        } else {
            Log.e(TAG, "setDrawerButtonIcon: this method can be used " +
                    "only with the default header view");
        }
    }

    public void setDrawerButtonTooltip(@Nullable CharSequence tooltipText) {
        if (mHeaderButton != null) {
            TooltipCompat.setTooltipText(mHeaderButton, tooltipText);
        } else {
            Log.e(TAG, "setDrawerButtonTooltip: this method can be used " +
                    "only with the default header view");
        }
    }

    public void setDrawerButtonOnClickListener(@Nullable OnClickListener listener) {
        if (mHeaderButton != null) {
            mHeaderButton.setOnClickListener(listener);
        } else {
            Log.e(TAG, "setDrawerButtonOnClickListener: this method can be used " +
                    "only with the default header view");
        }
    }

    public void setButtonBadges(int navigationIcon, int drawerIcon) {
        setNavigationButtonBadge(navigationIcon);
        setDrawerButtonBadge(drawerIcon);
    }

    public void setDrawerButtonBadge(int count) {
        if (mHeaderBadge != null) {
            if (count > 0) {
                if (count > 99) {
                    count = 99;
                }

                String badgeText = mNumberFormat.format(count);
                mHeaderBadge.setText(badgeText);

                ViewGroup.LayoutParams lp = mHeaderBadge.getLayoutParams();
                lp.width = (int) (getResources().getDimension(R.dimen.oui_n_badge_default_width) +
                        ((float) badgeText.length() * getResources().getDimension(R.dimen.oui_n_badge_additional_width)));
                lp.height = getResources().getDimensionPixelSize(R.dimen.oui_n_badge_view_size);
                mHeaderBadge.setLayoutParams(lp);

                mHeaderBadge.setVisibility(View.VISIBLE);
            } else if (count == N_BADGE) {
                mHeaderBadge.setText(getResources().getString(R.string.oui_new_badge_text));
                mHeaderBadge.setVisibility(View.VISIBLE);
            } else {
                mHeaderBadge.setVisibility(View.GONE);
            }
        } else {
            Log.e(TAG, "setDrawerButtonBadge: this method can be used " +
                    "only with the default header view");
        }
    }

    public void setDrawerOpen(boolean open, boolean animate) {
        if (open) {
            mDrawer.openDrawer(mDrawerContent, animate);
        } else {
            mDrawer.closeDrawer(mDrawerContent, animate);
        }
    }

    private class DrawerOutlineProvider extends ViewOutlineProvider {
        private int mCornerRadius;

        public DrawerOutlineProvider(@Px int cornerRadius) {
            mCornerRadius = cornerRadius;
        }

        @Override
        public void getOutline(View view, Outline outline) {
            outline.setRoundRect(
                    mIsRtl
                            ? 0
                            : -mCornerRadius,
                    0,
                    mIsRtl
                            ? view.getWidth() + mCornerRadius
                            : view.getWidth(), view.getHeight(),
                    mCornerRadius);
        }
    }
}
