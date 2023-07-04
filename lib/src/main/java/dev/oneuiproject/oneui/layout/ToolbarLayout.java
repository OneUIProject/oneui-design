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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.PathInterpolator;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.IdRes;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import dev.oneuiproject.oneui.design.R;
import dev.oneuiproject.oneui.utils.internal.ToolbarLayoutUtils;
import dev.oneuiproject.oneui.view.internal.NavigationBadgeIcon;

/**
 * Custom collapsing Appbar like in any App from Samsung. Includes a {@link SearchView} and Samsung's ActionMode.
 */
public class ToolbarLayout extends LinearLayout {
    private static final String TAG = "ToolbarLayout";


    public static final int AMT_GROUP_MENU_ID = 9999;
    private int mAMTMenuShowAlwaysMax = 2;
    private boolean switchActionModeMenu = false;
    private int mSelectedItemsCount = 0;

    public interface ActionModeCallback {
        void onShow(ToolbarLayout toolbarLayout);
        void onDismiss(ToolbarLayout toolbarLayout);
    }

    private ActionModeCallback mActionModeCallback;

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

    /**
     * Callback for the Toolbar's SearchMode.
     * Notification that the {@link SearchView}'s text has been edited or it's visibility changed.
     *
     * @see #showSearchMode()
     * @see #dismissSearchMode()
     */
    public interface SearchModeListener {
        boolean onQueryTextSubmit(String query);

        boolean onQueryTextChange(String newText);

        void onSearchModeToggle(SearchView searchView, boolean visible);
    }

    public ToolbarLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mActivity = getActivity();
        mContext = context;

        setOrientation(VERTICAL);

        //App windowBackground is enough
        /*TypedValue bgColor = new TypedValue();
        context.getTheme()
                .resolveAttribute(android.R.attr.windowBackground, bgColor, true);
        if (bgColor.resourceId > 0) {
            setBackgroundColor(mContext.getColor(bgColor.resourceId));
        } else {
            setBackgroundColor(bgColor.data);
        }*/

        initLayoutAttrs(attrs);
        inflateChildren();
        initAppBar();

        if (!isInEditMode()) {
            mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            mActivity.getOnBackPressedDispatcher().addCallback(mOnBackPressedCallback);
        }

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

            mSearchView.setSearchableInfo(
                    ((SearchManager) mContext.getSystemService(Context.SEARCH_SERVICE))
                            .getSearchableInfo(mActivity.getComponentName()));
        }

        mSearchView.seslSetUpButtonVisibility(View.VISIBLE);
        mSearchView.seslSetOnUpButtonClickListener(v -> dismissSearchMode());

        setNavigationButtonIcon(mNavigationIcon);
        setTitle(mTitleExpanded, mTitleCollapsed);
        setExpandedSubtitle(mSubtitleExpanded);

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
        updateActionModeMenuVisibility(newConfig);
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

    /**
     * Returns the {@link AppBarLayout}.
     */
    @NonNull
    public AppBarLayout getAppBarLayout() {
        return mAppBarLayout;
    }

    /**
     * Returns the {@link Toolbar}.
     */
    @NonNull
    public Toolbar getToolbar() {
        return mMainToolbar;
    }

    /**
     * Set the title of both the collapsed and expanded Toolbar.
     * The expanded title might not be visible in landscape or on devices with small dpi.
     */
    public void setTitle(@Nullable CharSequence title) {
        setTitle(title, title);
    }

    /**
     * Set the title of the collapsed and expanded Toolbar independently.
     * The expanded title might not be visible in landscape or on devices with small dpi.
     */
    public void setTitle(@Nullable CharSequence expandedTitle,
                         @Nullable CharSequence collapsedTitle) {
        mMainToolbar.setTitle(mTitleCollapsed = collapsedTitle);
        mCollapsingToolbarLayout.setTitle(mTitleExpanded = expandedTitle);
    }

    /**
     * Set the subtitle of the {@link CollapsingToolbarLayout}.
     * This might not be visible in landscape or on devices with small dpi.
     */
    public void setExpandedSubtitle(@Nullable CharSequence expandedSubtitle) {
        mCollapsingToolbarLayout.seslSetSubtitle(mSubtitleExpanded = expandedSubtitle);
    }

    /**
     * Set the subtitle of the collapsed Toolbar.
     */
    public void setCollapsedSubtitle(@Nullable CharSequence collapsedSubtitle) {
        mMainToolbar.setSubtitle(mSubtitleCollapsed = collapsedSubtitle);

    }

    /**
     * Enable or disable the expanding Toolbar functionality.
     * If you simply want to programmatically expand or collapse the toolbar.
     *
     * @see #setExpanded(boolean)
     */
    public void setExpandable(boolean expandable) {
        if (mExpandable != expandable) {
            mExpandable = expandable;
            resetAppBar();
        }
    }

    /**
     * Returns if the expanding Toolbar functionality is enabled or not.
     *
     * @see #setExpandable(boolean)
     */
    public boolean isExpandable() {
        return mExpandable;
    }

    /**
     * Programmatically expand or collapse the Toolbar.
     */
    public void setExpanded(boolean expanded) {
        setExpanded(expanded, ViewCompat.isLaidOut(mAppBarLayout));
    }

    /**
     * Programmatically expand or collapse the Toolbar with an optional animation.
     *
     * @param animate whether or not to animate the expanding or collapsing.
     */
    public void setExpanded(boolean expanded, boolean animate) {
        if (mExpandable) {
            mExpanded = expanded;
            mAppBarLayout.setExpanded(expanded, animate);
        } else
            Log.d(TAG, "setExpanded: mExpandable is " + mExpandable);
    }

    /**
     * Get the current state of the toolbar.
     *
     * @see #setExpanded(boolean)
     * @see #setExpanded(boolean, boolean)
     */
    public boolean isExpanded() {
        return mExpandable && !mAppBarLayout.seslIsCollapsed();
    }

    /**
     * Replace the title of the expanded Toolbar with a custom View.
     * This might not be visible in landscape or on devices with small dpi.
     */
    public void setCustomTitleView(@NonNull View view) {
        setCustomTitleView(view,
                new CollapsingToolbarLayout.LayoutParams(view.getLayoutParams()));
    }

    /**
     * Replace the title of the expanded Toolbar with a custom View including LayoutParams.
     * This might not be visible in landscape or on devices with small dpi.
     */
    public void setCustomTitleView(@NonNull View view,
                                   @Nullable CollapsingToolbarLayout.LayoutParams params) {
        if (params == null) {
            params = new CollapsingToolbarLayout
                    .LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
        params.seslSetIsTitleCustom(true);
        mCollapsingToolbarLayout.seslSetCustomTitleView(view, params);
    }

    /**
     * Replace the subtitle of the expanded Toolbar with a custom View.
     * This might not be visible in landscape or on devices with small dpi.
     */
    public void setCustomSubtitle(@NonNull View view) {
        mCollapsingToolbarLayout.seslSetCustomSubtitle(view);
    }

    /**
     * Enable or disable the immersive scroll of the Toolbar.
     * When this is enabled the Toolbar will completely hide when scrolling up.
     */
    public void setImmersiveScroll(boolean activate) {
        if (Build.VERSION.SDK_INT >= 30) {
            mAppBarLayout.seslSetImmersiveScroll(activate);
        } else {
            Log.e(TAG, "setImmersiveScroll: immersive scroll is available only on api 30 and above");
        }
    }

    /**
     * Returns true if the immersive scroll is enabled.
     *
     * @see #setImmersiveScroll(boolean)
     */
    public boolean isImmersiveScroll() {
        return mAppBarLayout.seslGetImmersiveScroll();
    }


    /**
     * Set the badge of a Toolbar MenuItem. Only use this for MenuItems which show as action! It won't work for overflow items.
     */
    public void setMenuItemBadgeText(@IdRes int id, String text) {
        for (int i = 0; i < mMainToolbar.getChildCount(); i++) {
            View v1 = mMainToolbar.getChildAt(i);
            if (v1 instanceof ActionMenuView) {
                ActionMenuView menuView = (ActionMenuView) v1;
                for (int j = 0; j < menuView.getChildCount(); j++) {
                    View v2 = menuView.getChildAt(j);

                    if (v2 instanceof ActionMenuItemView) {
                        ActionMenuItemView menuItemView = (ActionMenuItemView) v2;
                        if (menuItemView.getItemData().getItemId() == id) {

                            menuView.removeView(menuItemView);
                            FrameLayout fl = new FrameLayout(mContext);
                            fl.addView(menuItemView);

                            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            ViewGroup mBadgeBackground = (ViewGroup) inflater.inflate(androidx.appcompat.R.layout.sesl_action_menu_item_badge, fl, false);
                            TextView mBadgeText = (TextView) mBadgeBackground.getChildAt(0);
                            fl.addView(mBadgeBackground);

                            setMenuItemBadgeText(mBadgeBackground, mBadgeText, text);

                            menuView.addView(fl, j);
                            return;
                        }
                    } else if (v2 instanceof FrameLayout) {
                        FrameLayout fl = (FrameLayout) v2;
                        View v3 = fl.getChildAt(0);
                        if (v3 instanceof ActionMenuItemView && ((ActionMenuItemView) v3).getItemData().getItemId() == id) {
                            ViewGroup mBadgeBackground = (ViewGroup) fl.getChildAt(1);
                            TextView mBadgeText = (TextView) mBadgeBackground.getChildAt(0);
                            setMenuItemBadgeText(mBadgeBackground, mBadgeText, text);
                            return;
                        }
                    }
                }

                Log.e(TAG, "no MenuItem with id " + id);
                return;
            }
        }

        Log.e(TAG, "no ActionMenuView in Toolbar");
    }

    private void setMenuItemBadgeText(ViewGroup mBadgeBackground, TextView mBadgeText, String text) {
        mBadgeText.setText(text);
        mBadgeBackground.setVisibility(text == null || text.isEmpty() ? GONE : VISIBLE);
        if (text == null) return;
        ViewGroup.MarginLayoutParams lp = (MarginLayoutParams) mBadgeBackground.getLayoutParams();
        lp.setMarginEnd(0);
        lp.width = (int) (getResources().getDimension(androidx.appcompat.R.dimen.sesl_badge_default_width) + (text.length() * getResources().getDimension(androidx.appcompat.R.dimen.sesl_badge_additional_width)));
        mBadgeBackground.setLayoutParams(lp);
    }


    //
    // Navigation Button methods
    //

    /**
     * Set the navigation icon of the Toolbar.
     * Don't forget to also set a Tooltip with {@link #setNavigationButtonTooltip(CharSequence)}.
     */
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

    /**
     * Change the visibility of the navigation button.
     */
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

    /**
     * Add a badge to the navigation button.
     * The badge is small orange circle in the top right of the icon which contains text.
     * It can either be a 'N' or a number up to 99.
     *
     * @param count {@link #N_BADGE} to show a 'N', 0 to hide the badge or any number up to 99.
     */
    public void setNavigationButtonBadge(int count) {
        if (mNavigationIcon != null) {
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
        } else
            Log.d(TAG, "setNavigationButtonBadge: no navigation icon" +
                    " has been set");
    }

    /**
     * Set the Tooltip of the navigation button.
     */
    public void setNavigationButtonTooltip(@Nullable CharSequence tooltipText) {
        mMainToolbar.setNavigationContentDescription(tooltipText);
    }

    /**
     * Callback for the navigation button click event.
     */
    public void setNavigationButtonOnClickListener(@Nullable OnClickListener listener) {
        mMainToolbar.setNavigationOnClickListener(listener);
    }

    /**
     * Sets the icon the a back icon, the tooltip to 'Navigate up' and calls {@link AppCompatActivity#onBackPressed()} when clicked.
     *
     * @see #setNavigationButtonIcon(Drawable)
     * @see #setNavigationButtonTooltip(CharSequence)
     * @see android.app.ActionBar#setDisplayHomeAsUpEnabled(boolean)
     */
    public void setNavigationButtonAsBack() {
        if (!isInEditMode()) {
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setNavigationButtonOnClickListener(v -> mActivity.onBackPressed());
        }
    }

    //
    // Search Mode methods
    //

    /**
     * Show the {@link SearchView} in the Toolbar.
     * To enable the voice input icon in the SearchView, please refer to the project wiki.
     * TODO: link to the wiki on how to use the voice input feature.
     */
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

    /**
     * Dismiss the {@link SearchView} in the Toolbar.
     *
     * @see #showSearchMode()
     */
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

    /**
     * Check if SearchMode is enabled(=the {@link SearchView} in the Toolbar is visible).
     */
    public boolean isSearchMode() {
        return mIsSearchMode;
    }

    /**
     * Returns the {@link SearchView} of the Toolbar.
     */
    @NonNull
    public SearchView getSearchView() {
        return mSearchView;
    }

    /**
     * Set the {@link SearchModeListener} for the Toolbar's SearchMode.
     */
    public void setSearchModeListener(SearchModeListener listener) {
        mSearchModeListener = listener;
    }

    /**
     * Forward the voice input result to the Toolbar.
     * TODO: link to the wiki on how to use the voice input feature.
     */
    public void onSearchModeVoiceInputResult(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mSearchView.setQuery(intent.getStringExtra(SearchManager.QUERY), true);
        }
    }


    public void setActionModeToolbarShowAlwaysMax(int max){
        mAMTMenuShowAlwaysMax = max;
    }


    public void setOnActionModeListener (ActionModeCallback callback) {
        mActionModeCallback  = callback;
    }


    //
    // Action Mode methods
    //

    /**
     * Show the Toolbar's ActionMode. This will show a 'All' Checkbox instead of the navigation button,
     * temporarily replace the Toolbar's title with a counter ('x selected')
     * and show a {@link BottomNavigationView} in the footer.
     * The ActionMode is useful when the user can select items in a list.
     *
     * @see #setActionModeCount(int, int)
     * @see #setActionModeCheckboxListener(CompoundButton.OnCheckedChangeListener)
     * @see #setActionModeMenu(int)
     * @see #setActionModeMenuListener(NavigationBarView.OnItemSelectedListener)
     * @see #setActionModeToolbarMenu(int)
     * @see #setActionModeToolbarMenuListener(Toolbar.OnMenuItemClickListener) (int)
     * @see #setActionModeBottomMenu(int)
     * @see #setActionModeBottomMenuListener(NavigationBarView.OnItemSelectedListener)
     */
    public void showActionMode() {
        mIsActionMode = true;
        if (mIsSearchMode) dismissSearchMode();
        mOnBackPressedCallback.setEnabled(true);
        animatedVisibility(mMainToolbar, GONE);
        animatedVisibility(mActionModeToolbar, VISIBLE);
        mFooterContainer.setVisibility(GONE);
        mBottomActionModeBar.setVisibility(VISIBLE);

        // setActionModeCount(0, -1);
        mAppBarLayout.addOnOffsetChangedListener(mActionModeTitleFadeListener);
        mCollapsingToolbarLayout.seslSetSubtitle(null);
        mMainToolbar.setSubtitle(null);

        updateActionModeMenuVisibility(mContext.getResources().getConfiguration());

        if ( mActionModeCallback != null) {
            mActionModeCallback.onShow(this);
        }
    }


    private void updateActionModeMenuVisibility(Configuration config) {
        if (isActionMode()) {
            if (mSelectedItemsCount > 0) {
                if (switchActionModeMenu && config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mBottomActionModeBar.setVisibility(GONE);
                    mActionModeToolbar.getMenu().setGroupVisible(AMT_GROUP_MENU_ID, true);
                } else {
                    mBottomActionModeBar.setVisibility(VISIBLE);
                    mActionModeToolbar.getMenu().setGroupVisible(AMT_GROUP_MENU_ID, false);
                }
            }else{
                mBottomActionModeBar.setVisibility(GONE);
                mActionModeToolbar.getMenu().setGroupVisible(AMT_GROUP_MENU_ID, false);
            }
        }
    }



    /**
     * Dismiss the ActionMode.
     *
     * @see #showActionMode()
     */
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
        setActionModeAllSelector(0,  true,  false);
        if (mActionModeCallback != null) {
            mActionModeCallback.onDismiss(this);
        }
    }

    /**
     * Checks if the ActionMode is enabled.
     */
    public boolean isActionMode() {
        return mIsActionMode;
    }

    /**
     * Set the menu resource for the ActionMode's {@link BottomNavigationView}
     * @deprecated Use {@link #setActionModeMenu(int)}
     */
    @Deprecated
    public void setActionModeBottomMenu(@MenuRes int menuRes) {
        mBottomActionModeBar.inflateMenu(menuRes);
    }


    /**
     * Set the menu resource for the ActionMode's {@link BottomNavigationView}.
     * On landscape orientation where ActionMode's {@link BottomNavigationView} will be hidden,
     * the visible items from this menu resource we be shown to ActionMode's {@link Toolbar} {@link Menu}
     */
    public void setActionModeMenu(@MenuRes int menuRes){
        getActionModeBottomMenu().clear();
        getActionModeToolbarMenu().removeGroup(AMT_GROUP_MENU_ID);
        mBottomActionModeBar.inflateMenu(menuRes);
        Menu AMToolbarMenu =  mActionModeToolbar.getMenu();
        AMToolbarMenu.removeGroup(AMT_GROUP_MENU_ID);
        Menu AMBottomMenu = mBottomActionModeBar.getMenu();
        int size = AMBottomMenu.size();
        int menuItemsAdded = 0;
        for (int a=0; a<size; a++){
            MenuItem ambMenuItem = AMBottomMenu.getItem(a);
            if (ambMenuItem.isVisible()){
                menuItemsAdded++;
                MenuItem amtMenuItem = AMToolbarMenu.add(AMT_GROUP_MENU_ID, ambMenuItem.getItemId(), Menu.NONE, ambMenuItem.getTitle());
                if (menuItemsAdded <= mAMTMenuShowAlwaysMax){
                    amtMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                }
            }
        }
        switchActionModeMenu = true;
    }

    /**
     * Returns the {@link Menu} of the ActionMode's {@link BottomNavigationView}.
     */
    public Menu getActionModeBottomMenu() {
        return mBottomActionModeBar.getMenu();
    }

    /**
     * Set the listener for the ActionMode's {@link BottomNavigationView}.
     * @deprecated See {@link #setActionModeMenuListener(NavigationBarView.OnItemSelectedListener)}
     */
    public void setActionModeBottomMenuListener(NavigationBarView.OnItemSelectedListener listener) {
        mBottomActionModeBar.setOnItemSelectedListener(listener);
    }


    /**
     * Set the listener for the ActionMode's {@link BottomNavigationView}.
     * On landscape orientation, the same listener will be invoke for ActionMode's {@link Toolbar} {@link MenuItem}s
     * which are copied from ActionMode's {@link BottomNavigationView}
     */
    public void setActionModeMenuListener(NavigationBarView.OnItemSelectedListener listener) {
        mBottomActionModeBar.setOnItemSelectedListener(listener);
        mActionModeToolbar.setOnMenuItemClickListener(item ->
                listener.onNavigationItemSelected(mActionModeToolbar.getMenu().findItem(item.getItemId()))
        );
    }


    /**
     * Set the menu resource for the ActionMode's {@link Toolbar}.
     */
    public void setActionModeToolbarMenu(@MenuRes int menuRes) {
        mActionModeToolbar.inflateMenu(menuRes);
    }


    /**
     * Set the listener for the ActionMode's {@link Toolbar}.
     */
    public void setActionModeToolbarMenuListener(Toolbar.OnMenuItemClickListener listener) {
        mActionModeToolbar.setOnMenuItemClickListener(listener);
    }


    /**
     * Returns the {@link Menu} of the ActionMode's {@link Toolbar}.
     *
     */
    public Menu getActionModeToolbarMenu() {
        return mActionModeToolbar.getMenu();
    }


    /**
     * Set the ActionMode's count and  checkbox enabled state.
     * Check state will stay.
     *
     * @param count number of selected items in the list
     * @param enabled enabled click
     */
    public void  setActionModeAllSelector(int count,  Boolean enabled) {
        setActionModeAllSelector(count, enabled, null);
    }


    /**
     * Set the ActionMode's count and Select all checkBox's enabled state and check state
     *
     * @param count number of selected items in the list
     * @param enabled enable or disable click
     * @param checked
     */
    public void  setActionModeAllSelector(int count,  Boolean enabled,  @Nullable Boolean checked) {
        if (mSelectedItemsCount != count) {
            mSelectedItemsCount = count;
            String title = count > 0
                    ? getResources().getString(R.string.oui_action_mode_n_selected, count)
                    : getResources().getString(R.string.oui_action_mode_select_items);
            mCollapsingToolbarLayout.setTitle(title);
            mActionModeTitleTextView.setText(title);
            updateActionModeMenuVisibility(mContext.getResources().getConfiguration());
        }
        if (checked != null && checked != mActionModeCheckBox.isChecked()) {
            mActionModeCheckBox.setChecked(checked);
        }
        if (enabled != mActionModeSelectAll.isEnabled()) {
            mActionModeSelectAll.setEnabled(enabled);
        }
    }


    /**
     * Set the ActionMode's count. This will change the count in the Toolbar's title
     * and if count = total, the 'All' Checkbox will be checked.
     *
     * @param count number of selected items in the list
     * @param total number of total items in the list
     * @deprecated use {@link #setActionModeAllSelector(int, Boolean, Boolean)}
     */
    @Deprecated
    public void setActionModeCount(int count, int total) {
        mSelectedItemsCount = count;
        String title = count > 0
                ? getResources().getString(R.string.oui_action_mode_n_selected, count)
                : getResources().getString(R.string.oui_action_mode_select_items);

        mCollapsingToolbarLayout.setTitle(title);
        mActionModeTitleTextView.setText(title);
        updateActionModeMenuVisibility(mContext.getResources().getConfiguration());
        mActionModeCheckBox.setChecked(count == total);
    }

    /**
     * Set the listener for the 'All' Checkbox of the ActionMode.
     */
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
