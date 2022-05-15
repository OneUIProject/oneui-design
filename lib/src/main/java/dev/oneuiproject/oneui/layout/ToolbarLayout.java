package dev.oneuiproject.oneui.layout;

import android.app.SearchManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.PathInterpolator;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import dev.oneuiproject.oneui.R;
import dev.oneuiproject.oneui.utils.internal.ToolbarLayoutUtils;
import dev.oneuiproject.oneui.view.internal.NavigationBadgeIcon;

public class ToolbarLayout extends LinearLayout {
    private static final String TAG = "ToolbarLayout";

    private static final int MAIN_CONTENT = 0;
    private static final int APPBAR_HEADER = 1;
    private static final int FOOTER = 2;
    private static final int ROOT = 3;

    public static final int N_BADGE = -1;

    protected AppCompatActivity mActivity;
    protected Context mContext;

    private final OnBackPressedCallback mOnBackPressedCallback
            = new OnBackPressedCallback(false) {
        @Override
        public void handleOnBackPressed() {
            if (mIsSearchMode) dismissSearchMode();
            if (mIsActionMode) dismissActionMode();
        }
    };
    private AppBarOffsetListener mActionModeTitleFadeListener = new AppBarOffsetListener();

    protected int mLayout;
    protected boolean mExpandable;
    protected boolean mExpanded;
    protected Drawable mNavigationIcon;
    private LayerDrawable mNavigationBadgeIcon;
    protected CharSequence mTitleCollapsed;
    protected CharSequence mTitleExpanded;
    protected CharSequence mSubtitleCollapsed;
    protected CharSequence mSubtitleExpanded;

    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mMainToolbar;
    private Toolbar mSearchToolbar;
    private Toolbar mActionModeToolbar;
    private CoordinatorLayout mCoordinatorLayout;
    protected FrameLayout mMainContainer;
    private FrameLayout mFooterContainer;
    private BottomNavigationView mBottomActionModeBar;

    private SearchView mSearchView;
    private LinearLayout mActionModeSelectAll;
    private AppCompatCheckBox mActionModeCheckBox;
    private TextView mActionModeTitleTextView;

    private SearchModeListener mSearchModeListener;

    private boolean mIsSearchMode = false;
    private boolean mIsActionMode = false;

    public interface SearchModeListener {
        boolean onQueryTextSubmit(String query);

        boolean onQueryTextChange(String newText);

        void onSearchModeToggle(SearchView searchView, boolean visible);
    }

    public ToolbarLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mActivity = getActivity();
        mContext = context;

        mActivity.getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        setOrientation(VERTICAL);

        TypedValue bgColor = new TypedValue();
        context.getTheme()
                .resolveAttribute(android.R.attr.windowBackground, bgColor, true);
        if (bgColor.resourceId > 0) {
            setBackgroundColor(mContext.getColor(bgColor.resourceId));
        } else {
            setBackgroundColor(bgColor.data);
        }

        initLayoutAttrs(attrs);
        inflateChildren();
        initAppBar();

        mActivity.getOnBackPressedDispatcher().addCallback(mOnBackPressedCallback);

        refreshLayout(getResources().getConfiguration());
    }

    protected void initLayoutAttrs(@Nullable AttributeSet attrs) {
        TypedArray a = mContext.getTheme()
                .obtainStyledAttributes(
                        attrs, R.styleable.ToolbarLayout, 0, 0);
        try {
            mLayout = a.getResourceId(R.styleable.ToolbarLayout_android_layout,
                    R.layout.oui_layout_toolbarlayout_appbar);
            mExpandable = a.getBoolean(R.styleable.ToolbarLayout_expandable, true);
            mExpanded = a.getBoolean(R.styleable.ToolbarLayout_expanded, mExpandable);
            mNavigationIcon = a.getDrawable(R.styleable.ToolbarLayout_navigationIcon);
            mTitleExpanded
                    = mTitleCollapsed = a.getString(R.styleable.ToolbarLayout_title);
            mSubtitleExpanded = a.getString(R.styleable.ToolbarLayout_subtitle);
        } finally {
            a.recycle();
        }
    }

    protected void inflateChildren() {
        if (mLayout != R.layout.oui_layout_toolbarlayout_appbar) {
            Log.w(TAG, "Inflating custom " + TAG);
        }

        LayoutInflater inflater = LayoutInflater.from(mContext);
        inflater.inflate(mLayout, this, true);
        addView(inflater.inflate(
                R.layout.oui_layout_toolbarlayout_footer, this, false));
    }

    private void initAppBar() {
        mCoordinatorLayout = findViewById(R.id.toolbarlayout_coordinator_layout);
        mAppBarLayout = mCoordinatorLayout.findViewById(R.id.toolbarlayout_app_bar);
        mCollapsingToolbarLayout = mAppBarLayout.findViewById(R.id.toolbarlayout_collapsing_toolbar);
        mMainToolbar = mCollapsingToolbarLayout.findViewById(R.id.toolbarlayout_main_toolbar);
        mSearchToolbar = mCollapsingToolbarLayout.findViewById(R.id.toolbarlayout_search_toolbar);
        mActionModeToolbar = mCollapsingToolbarLayout.findViewById(R.id.toolbarlayout_action_mode_toolbar);

        mSearchView = mSearchToolbar.findViewById(R.id.toolbarlayout_search_view);
        mActionModeSelectAll = mActionModeToolbar.findViewById(R.id.toolbarlayout_selectall);
        mActionModeCheckBox = mActionModeSelectAll.findViewById(R.id.toolbarlayout_selectall_checkbox);
        mActionModeTitleTextView = mActionModeToolbar.findViewById(R.id.toolbar_layout_action_mode_title);

        mActionModeSelectAll.setOnClickListener(
                view -> mActionModeCheckBox.setChecked(!mActionModeCheckBox.isChecked()));

        mMainContainer = findViewById(R.id.toolbarlayout_main_container);
        mFooterContainer = findViewById(R.id.toolbarlayout_footer_container);
        mBottomActionModeBar = findViewById(R.id.toolbarlayout_bottom_nav_view);

        if (!isInEditMode()) {
            mActivity.setSupportActionBar(mMainToolbar);
            mActivity.getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(false);
            mActivity.getSupportActionBar()
                    .setDisplayShowTitleEnabled(false);
        }

        setNavigationButtonIcon(mNavigationIcon);
        setTitle(mTitleExpanded, mTitleCollapsed);
        setExpandedSubtitle(mSubtitleExpanded);

        mSearchView.seslSetUpButtonVisibility(View.VISIBLE);
        mSearchView.seslSetOnUpButtonClickListener(v -> dismissSearchMode());
        mSearchView.setSearchableInfo(
                ((SearchManager) mContext.getSystemService(Context.SEARCH_SERVICE))
                .getSearchableInfo(mActivity.getComponentName()));
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (mMainContainer == null || mFooterContainer == null) {
            super.addView(child, index, params);
        } else {
            switch (((ToolbarLayoutParams) params).layout_location) {
                default:
                case MAIN_CONTENT:
                    mMainContainer.addView(child, params);
                    break;
                case APPBAR_HEADER:
                    setCustomTitleView(child,
                            new CollapsingToolbarLayout.LayoutParams(params));
                    break;
                case FOOTER:
                    mFooterContainer.addView(child, params);
                    break;
                case ROOT:
                    mCoordinatorLayout
                            .addView(child, CLLPWrapper((LayoutParams) params));
                    break;
            }
        }
    }

    @Override
    public LayoutParams generateDefaultLayoutParams() {
        return new ToolbarLayoutParams(getContext(), null);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new ToolbarLayoutParams(getContext(), attrs);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        resetAppBar();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        refreshLayout(newConfig);
        resetAppBar();
    }

    @Nullable
    private AppCompatActivity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof AppCompatActivity) {
                return (AppCompatActivity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    private void refreshLayout(Configuration newConfig) {
        if (!isInEditMode())
            ToolbarLayoutUtils
                    .hideStatusBarForLandscape(mActivity, newConfig.orientation);

        ToolbarLayoutUtils.updateListBothSideMargin(mActivity,
                mMainContainer);
        ToolbarLayoutUtils.updateListBothSideMargin(mActivity,
                findViewById(R.id.toolbarlayout_bottom_corners));
        ToolbarLayoutUtils.updateListBothSideMargin(mActivity,
                findViewById(R.id.toolbarlayout_footer_content));

        final boolean isLandscape
                = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;

        setExpanded(!isLandscape & mExpanded);

        if (mNavigationBadgeIcon != null) {
            NavigationBadgeIcon badgeIcon
                    = (NavigationBadgeIcon) mNavigationBadgeIcon
                    .getDrawable(1);
            badgeIcon.setOrientation(isLandscape);
        }
    }

    private void resetAppBar() {
        if (mAppBarLayout != null) {
            if (mExpandable) {
                mAppBarLayout.setEnabled(true);
                mAppBarLayout.seslSetCustomHeightProportion(false, 0);
            } else {
                mAppBarLayout.setEnabled(false);
                mAppBarLayout.seslSetCustomHeight(mContext.getResources()
                                .getDimensionPixelSize(R.dimen.sesl_action_bar_height_with_padding));
            }
        } else
            Log.w(TAG, "resetAppBar: mAppBarLayout is null.");
    }

    //
    // AppBar methods
    //
    @NonNull
    public AppBarLayout getAppBarLayout() {
        return mAppBarLayout;
    }

    @NonNull
    public Toolbar getToolbar() {
        return mMainToolbar;
    }

    public void setTitle(@Nullable CharSequence title) {
        setTitle(title, title);
    }

    public void setTitle(@Nullable CharSequence expandedTitle,
                         @Nullable CharSequence collapsedTitle) {
        mMainToolbar.setTitle(mTitleCollapsed = collapsedTitle);
        mCollapsingToolbarLayout.setTitle(mTitleExpanded = expandedTitle);
    }

    public void setExpandedSubtitle(@Nullable CharSequence expandedSubtitle) {
        mCollapsingToolbarLayout.seslSetSubtitle(mSubtitleExpanded = expandedSubtitle);
    }

    public void setCollapsedSubtitle(@Nullable CharSequence collapsedSubtitle) {
        mMainToolbar.setSubtitle(mSubtitleCollapsed = collapsedSubtitle);

    }

    public void setExpandable(boolean expandable) {
        if (mExpandable != expandable) {
            mExpandable = expandable;
            resetAppBar();
        }
    }

    public boolean isExpandable() {
        return mExpandable;
    }

    public void setExpanded(boolean expanded) {
        setExpanded(expanded, ViewCompat.isLaidOut(mAppBarLayout));
    }

    public void setExpanded(boolean expanded, boolean animate) {
        if (mExpandable) {
            mExpanded = expanded;
            mAppBarLayout.setExpanded(expanded, animate);
        } else
            Log.d(TAG, "setExpanded: mExpandable is " + mExpandable);
    }

    public boolean isExpanded() {
        return mExpandable && !mAppBarLayout.seslIsCollapsed();
    }

    public void setCustomTitleView(@NonNull View view) {
        setCustomTitleView(view,
                new CollapsingToolbarLayout.LayoutParams(view.getLayoutParams()));
    }

    public void setCustomTitleView(@NonNull View view,
                                   @Nullable CollapsingToolbarLayout.LayoutParams params) {
        if (params == null) {
            params = new CollapsingToolbarLayout
                    .LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
        params.seslSetIsTitleCustom(true);
        mCollapsingToolbarLayout.seslSetCustomTitleView(view, params);
    }

    public void setCustomSubtitle(@NonNull View view) {
        mCollapsingToolbarLayout.seslSetCustomSubtitle(view);
    }

    public void setImmersiveScroll(boolean activate) {
        if (Build.VERSION.SDK_INT >= 30) {
            mAppBarLayout.seslSetImmersiveScroll(activate);
        } else {
            Log.e(TAG, "setImmersiveScroll: immersive scroll is " +
                    "available only on api 30 and above");
        }
    }

    public boolean isImmersiveScroll() {
        return mAppBarLayout.seslGetImmersiveScroll();
    }

    //
    // Navigation Button methods
    //
    public void setNavigationButtonIcon(@Nullable Drawable icon) {
        mNavigationIcon = icon;
        if (mNavigationBadgeIcon != null) {
            mNavigationBadgeIcon.setDrawable(0, mNavigationIcon);
            mNavigationBadgeIcon.invalidateSelf();
            mMainToolbar.setNavigationIcon(mNavigationBadgeIcon);
        } else {
            mMainToolbar.setNavigationIcon(mNavigationIcon);
        }
    }

    public void setNavigationButtonVisible(boolean visible) {
        if (mNavigationBadgeIcon != null) {
            mMainToolbar.setNavigationIcon(visible
                    ? mNavigationBadgeIcon : null);
        } else if (mNavigationIcon != null) {
            mMainToolbar.setNavigationIcon(visible
                    ? mNavigationIcon : null);
        } else {
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(visible);
        }
    }

    public void setNavigationButtonBadge(int count) {
        if (count != 0) {
            NavigationBadgeIcon badgeIcon;
            if (mNavigationBadgeIcon == null) {
                badgeIcon = new NavigationBadgeIcon(mContext);
                mNavigationBadgeIcon = new LayerDrawable(
                        new Drawable[]{mNavigationIcon, badgeIcon});
            } else {
                badgeIcon = (NavigationBadgeIcon) mNavigationBadgeIcon
                        .getDrawable(1);
            }

            badgeIcon.setOrientation(getResources().getConfiguration()
                    .orientation == Configuration.ORIENTATION_LANDSCAPE);

            if (count == N_BADGE) {
                badgeIcon.setText(mContext.getResources()
                        .getString(R.string.oui_new_badge_text));
            } else {
                badgeIcon.setText(count > 99
                        ? "99" : String.valueOf(count));
            }

            mNavigationBadgeIcon.invalidateSelf();
            mMainToolbar.setNavigationIcon(mNavigationBadgeIcon);
        } else {
            mNavigationBadgeIcon = null;
            mMainToolbar.setNavigationIcon(mNavigationIcon);
        }
    }

    public void setNavigationButtonTooltip(@Nullable CharSequence tooltipText) {
        mMainToolbar.setNavigationContentDescription(tooltipText);
    }

    public void setNavigationButtonOnClickListener(@Nullable OnClickListener listener) {
        mMainToolbar.setNavigationOnClickListener(listener);
    }

    //
    // Search Mode methods
    //
    public void showSearchMode() {
        mIsSearchMode = true;
        if (mIsActionMode) dismissActionMode();
        mOnBackPressedCallback.setEnabled(true);
        animatedVisibility(mMainToolbar, GONE);
        animatedVisibility(mSearchToolbar, VISIBLE);
        mFooterContainer.setVisibility(GONE);

        mCollapsingToolbarLayout.setTitle(getResources()
                .getString(R.string.sesl_searchview_description_search));
        mCollapsingToolbarLayout.seslSetSubtitle(null);
        setExpanded(false, true);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (mSearchModeListener != null)
                    return mSearchModeListener.onQueryTextSubmit(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (mSearchModeListener != null)
                    return mSearchModeListener.onQueryTextChange(newText);
                return false;
            }
        });
        mSearchView.setIconified(false);

        if (mSearchModeListener != null)
            mSearchModeListener.onSearchModeToggle(mSearchView, true);
    }

    public void dismissSearchMode() {
        if (mSearchModeListener != null)
            mSearchModeListener.onSearchModeToggle(mSearchView, false);
        mIsSearchMode = false;
        mOnBackPressedCallback.setEnabled(false);
        mSearchView.setQuery("", false);
        animatedVisibility(mSearchToolbar, GONE);
        animatedVisibility(mMainToolbar, VISIBLE);
        mFooterContainer.setVisibility(VISIBLE);

        setTitle(mTitleExpanded, mTitleCollapsed);
        mCollapsingToolbarLayout.seslSetSubtitle(mSubtitleExpanded);
    }

    public boolean isSearchMode() {
        return mIsSearchMode;
    }

    @NonNull
    public SearchView getSearchView() {
        return mSearchView;
    }

    public void setSearchModeListener(SearchModeListener listener) {
        mSearchModeListener = listener;
    }

    public void onSearchModeVoiceInputResult(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mSearchView.setQuery(intent.getStringExtra(SearchManager.QUERY), true);
        }
    }

    //
    // Action Mode methods
    //
    public void showActionMode() {
        mIsActionMode = true;
        if (mIsSearchMode) dismissSearchMode();
        mOnBackPressedCallback.setEnabled(true);
        animatedVisibility(mMainToolbar, GONE);
        animatedVisibility(mActionModeToolbar, VISIBLE);
        mFooterContainer.setVisibility(GONE);
        mBottomActionModeBar.setVisibility(VISIBLE);

        setActionModeCount(0, -1);
        mAppBarLayout.addOnOffsetChangedListener(mActionModeTitleFadeListener);
        mCollapsingToolbarLayout.seslSetSubtitle(null);
        mMainToolbar.setSubtitle(null);
    }

    public void dismissActionMode() {
        mIsActionMode = false;
        mOnBackPressedCallback.setEnabled(false);
        animatedVisibility(mActionModeToolbar, GONE);
        animatedVisibility(mMainToolbar, VISIBLE);
        mFooterContainer.setVisibility(VISIBLE);
        mBottomActionModeBar.setVisibility(GONE);

        setTitle(mTitleExpanded, mTitleCollapsed);
        mAppBarLayout.removeOnOffsetChangedListener(mActionModeTitleFadeListener);
        mCollapsingToolbarLayout.seslSetSubtitle(mSubtitleExpanded);
        mMainToolbar.setSubtitle(mSubtitleCollapsed);
    }

    public boolean isActionMode() {
        return mIsActionMode;
    }

    public void setActionModeBottomMenu(@MenuRes int menuRes) {
        mBottomActionModeBar.inflateMenu(menuRes);
    }

    public Menu getActionModeBottomMenu() {
        return mBottomActionModeBar.getMenu();
    }

    public void setActionModeBottomMenuListener(NavigationBarView.OnItemSelectedListener listener) {
        mBottomActionModeBar.setOnItemSelectedListener(listener);
    }

    public void setActionModeCount(int count, int total) {
        String title = count > 0
                ? getResources().getString(R.string.oui_action_mode_n_selected, count)
                : getResources().getString(R.string.oui_action_mode_select_items);

        mCollapsingToolbarLayout.setTitle(title);
        mActionModeTitleTextView.setText(title);
        mBottomActionModeBar.setVisibility(count > 0 ? VISIBLE : GONE);

        mActionModeCheckBox.setChecked(count == total);
    }

    public void setActionModeCheckboxListener(CompoundButton.OnCheckedChangeListener listener) {
        mActionModeCheckBox.setOnCheckedChangeListener(listener);
    }

    //
    // others
    //
    public static class ToolbarLayoutParams extends LayoutParams {
        public int layout_location;

        public ToolbarLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            if (c != null && attrs != null) {
                TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.ToolbarLayoutParams);
                layout_location = a.getInteger(R.styleable.ToolbarLayoutParams_layout_location, 0);
                a.recycle();
            }
        }
    }

    private CoordinatorLayout.LayoutParams CLLPWrapper(LayoutParams oldLp) {
        CoordinatorLayout.LayoutParams newLp = new CoordinatorLayout.LayoutParams(oldLp);
        newLp.width = oldLp.width;
        newLp.height = oldLp.height;
        newLp.leftMargin = oldLp.leftMargin;
        newLp.topMargin = oldLp.topMargin;
        newLp.rightMargin = oldLp.rightMargin;
        newLp.bottomMargin = oldLp.bottomMargin;
        newLp.gravity = oldLp.gravity;
        return newLp;
    }

    private void animatedVisibility(View view, int visibility) {
        view.setVisibility(VISIBLE);
        view.animate()
                .alphaBy(1.0f)
                .alpha(visibility == VISIBLE ? 1.0f : 0.0f)
                .setDuration(200)
                .setInterpolator(
                        new PathInterpolator(0.33f, 0.0f, 0.1f, 1.0f))
                .withEndAction(() -> view.setVisibility(visibility))
                .start();
    }

    private class AppBarOffsetListener implements AppBarLayout.OnOffsetChangedListener {
        @Override
        public void onOffsetChanged(AppBarLayout layout, int verticalOffset) {
            if (mActionModeToolbar.getVisibility() == View.VISIBLE) {
                int layoutPosition = Math.abs(mAppBarLayout.getTop());
                float alphaRange = ((float) mCollapsingToolbarLayout.getHeight()) * 0.17999999f;
                float toolbarTitleAlphaStart = ((float) mCollapsingToolbarLayout.getHeight()) * 0.35f;

                if (mAppBarLayout.seslIsCollapsed()) {
                    mActionModeTitleTextView.setAlpha(1.0f);
                } else {
                    float collapsedTitleAlpha = ((150.0f / alphaRange)
                            * (((float) layoutPosition) - toolbarTitleAlphaStart));

                    if (collapsedTitleAlpha >= 0.0f && collapsedTitleAlpha <= 255.0f) {
                        collapsedTitleAlpha /= 255.0f;
                        mActionModeTitleTextView.setAlpha(collapsedTitleAlpha);
                    } else if (collapsedTitleAlpha < 0.0f)
                        mActionModeTitleTextView.setAlpha(0.0f);
                    else
                        mActionModeTitleTextView.setAlpha(1.0f);
                }
            }
        }
    }

}
