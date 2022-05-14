package dev.oneuiproject.oneui.layout;

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
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import java.text.NumberFormat;
import java.util.Locale;

import dev.oneuiproject.oneui.R;

public class DrawerLayout extends ToolbarLayout {
    private static final String TAG = "DrawerLayout";
    private NumberFormat mNumberFormat = NumberFormat.getInstance(Locale.getDefault());
    private OnBackPressedCallback mOnBackPressedCallback;
    private androidx.drawerlayout.widget.DrawerLayout drawerLayout;
    private LinearLayout toolbarLayoutContainer;
    private FrameLayout drawerButtonContainer;
    private AppCompatImageButton drawerButton;
    private ViewGroup drawerIconBadgeBackground;
    private TextView drawerIconBadgeText;
    private LinearLayout drawer_container;
    private View drawer;

    public DrawerLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initDrawer();

        if (!isInEditMode()) {
            mOnBackPressedCallback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (drawerLayout.isDrawerOpen(drawer)) {
                        drawerLayout.closeDrawer(drawer, true);
                        return;
                    }
                    this.setEnabled(false);
                    mActivity.onBackPressed();
                    this.setEnabled(true);
                }
            };
            mActivity.getOnBackPressedDispatcher().addCallback(mOnBackPressedCallback);
        }
    }

    @Override
    protected void initLayoutAttrs(@Nullable AttributeSet attrs) {
        super.initLayoutAttrs(attrs);
        TypedArray attr = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.ToolBarLayout, 0, 0);
        try {
            mLayout = attr.getResourceId(R.styleable.ToolBarLayout_android_layout, R.layout.oui_layout_drawerlayout);
        } finally {
            attr.recycle();
        }
    }

    @Override
    protected void inflateChildren() {
        if (mLayout != R.layout.oui_layout_drawerlayout) {
            Log.w(TAG, "Inflating custom DrawerLayout");
        }
        LayoutInflater.from(mContext).inflate(mLayout, this, true);
    }

    private void initDrawer() {
        drawer_container = findViewById(R.id.drawer_container);

        setNavigationButton(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.ic_oui_drawer, mContext.getTheme()), getResources().getText(R.string.oui_navigation_drawer));

        drawerButtonContainer = findViewById(R.id.drawer_layout_drawerButton_container);
        drawerButton = findViewById(R.id.drawer_layout_drawerButton);
        drawerButton.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.drawer_icon_color)));

        drawerLayout = findViewById(R.id.drawerLayout);
        toolbarLayoutContainer = findViewById(R.id.toolbarlayout_container);
        drawer = findViewById(R.id.drawer);

        drawerLayout.setScrimColor(ContextCompat.getColor(getContext(), R.color.drawer_dim_color));
        drawerLayout.setDrawerElevation(0);
        setDrawerWidth();

        drawer.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                float cornerRadius = 26;
                boolean isRtl = getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
                outline.setRoundRect(isRtl ? 0 : (int) (-cornerRadius), 0, isRtl ? (int) (view.getWidth() + cornerRadius) : view.getWidth(), view.getHeight(), cornerRadius);
            }
        });
        drawer.setClipToOutline(true);

        if (!isInEditMode()) {
            Boolean isRtl = getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;

            View translationView = findViewById(R.id.drawer_custom_translation);
            if (translationView == null) translationView = toolbarLayoutContainer;
            View content = translationView;

            Window window = mActivity.getWindow();
            ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(mActivity, drawerLayout, 0, 0) {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    super.onDrawerSlide(drawerView, slideOffset);
                    float slideX = drawerView.getWidth() * slideOffset;
                    if (isRtl) slideX *= -1;
                    content.setTranslationX(slideX);

                    float[] hsv = new float[3];
                    Color.colorToHSV(ContextCompat.getColor(getContext(), R.color.oui_round_and_bgcolor), hsv);
                    hsv[2] *= 1f - (slideOffset * 0.2f);
                    window.setStatusBarColor(Color.HSVToColor(hsv));
                    window.setNavigationBarColor(Color.HSVToColor(hsv));

                }
            };
            drawerLayout.addDrawerListener(actionBarDrawerToggle);
            setNavigationOnClickListener(v -> drawerLayout.openDrawer(drawer, true));
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (toolbarLayoutContainer == null || drawer_container == null) {
            super.addView(child, index, params);
        } else {
            if (((ToolbarLayoutParams) params).layout_location == 4) {
                drawer_container.addView(child, params);
            } else {
                super.addView(child, index, params);
            }
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setDrawerWidth();
    }

    private void lockDrawerIfAvailable(boolean lock) {
        if (drawerLayout != null) {
            if (lock) {
                drawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            } else {
                drawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        }
    }

    private void setDrawerWidth() {
        ViewGroup.LayoutParams layoutParams = drawer.getLayoutParams();
        Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        int displayWidth = p.x;
        float density = getResources().getDisplayMetrics().density;
        float dpi = (float) displayWidth / density;

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

        layoutParams.width = (int) ((double) displayWidth * widthRate);
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
    public void setDrawerButton(Drawable drawerIcon, CharSequence tooltipText) {
        drawerButton.setImageDrawable(drawerIcon);
        drawerButtonContainer.setVisibility(drawerIcon != null ? View.VISIBLE : View.GONE);
        TooltipCompat.setTooltipText(drawerButton, tooltipText);
    }

    public void setDrawerButtonOnClickListener(OnClickListener listener) {
        drawerButton.setOnClickListener(listener);
    }

    public void setButtonBadges(int navigationIcon, int drawerIcon) {
        //setNavigationButtonBadge(navigationIcon);
        setDrawerButtonBadge(drawerIcon);
    }

    public void setDrawerButtonBadge(int count) {
        if (drawerIconBadgeBackground == null) {
            //todo same for toolbar buttons (maybe instead layout: sesl_action_menu_item_badge)
            drawerIconBadgeBackground = (ViewGroup) ((LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.oui_navigation_button_badge_layout, drawerButtonContainer, false);
            drawerIconBadgeText = (TextView) drawerIconBadgeBackground.getChildAt(0);
            drawerIconBadgeText.setTextSize(0, (float) ((int) getResources().getDimension(R.dimen.sesl_menu_item_badge_text_size)));
            drawerButtonContainer.addView(drawerIconBadgeBackground);
        }
        if (drawerIconBadgeText != null) {
            if (count > 0) {
                if (count > 99) {
                    count = 99;
                }
                String countString = mNumberFormat.format((long) count);
                drawerIconBadgeText.setText(countString);
                int width = (int) (getResources().getDimension(R.dimen.sesl_badge_default_width) + (float) countString.length() * getResources().getDimension(R.dimen.sesl_badge_additional_width));
                MarginLayoutParams lp = (MarginLayoutParams) drawerIconBadgeBackground.getLayoutParams();
                lp.width = width;
                lp.height = (int) getResources().getDimension(R.dimen.sesl_menu_item_badge_size);
                drawerIconBadgeBackground.setLayoutParams(lp);
                drawerIconBadgeBackground.setVisibility(View.VISIBLE);
            } else if (count == N_BADGE) {
                drawerIconBadgeText.setText(getResources().getString(R.string.sesl_action_menu_overflow_badge_text_n));
                drawerIconBadgeBackground.setVisibility(View.VISIBLE);
            } else {
                drawerIconBadgeBackground.setVisibility(View.GONE);
            }
        }
    }

    public void setDrawerOpen(Boolean open, Boolean animate) {
        if (open) {
            drawerLayout.openDrawer(drawer, animate);
        } else {
            drawerLayout.closeDrawer(drawer, animate);
        }

    }
}
