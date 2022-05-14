package dev.oneuiproject.oneui.layout;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
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
import dev.oneuiproject.oneui.utils.ViewSupport;
import dev.oneuiproject.oneui.utils.ViewUtils;
import dev.oneuiproject.oneui.utils.WindowManagerSupport;
import dev.oneuiproject.oneui.widget.RoundFrameLayout;

public class ToolbarLayout extends LinearLayout {
    private static final String TAG = "ToolbarLayout";
    public static final int N_BADGE = -1;
    protected AppCompatActivity mActivity;
    protected Context mContext;

    private OnBackPressedCallback mOnBackPressedCallback;
    private SearchModeListener searchModeListener;

    protected int mLayout;
    protected boolean mExpandable;
    protected boolean mExpanded;
    private boolean mNavBackButton;
    protected CharSequence mTitleCollapsed;
    protected CharSequence mTitleExpanded;
    protected CharSequence mSubtitleCollapsed;
    protected CharSequence mSubtitleExpanded;

    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar mainToolbar;
    private Toolbar searchToolbar;
    private Toolbar actionModeToolbar;
    private CoordinatorLayout root_layout;
    protected RoundFrameLayout main_layout;
    private FrameLayout footer_layout;
    private BottomNavigationView bottomNavigationView;

    private SearchView searchView;
    private AppCompatCheckBox actionModeCheckBox;
    private TextView actionModeTitle;

    private boolean mSearchMode = false;
    private boolean mActionMode = false;

    public interface SearchModeListener {
        boolean onQueryTextSubmit(String query);

        boolean onQueryTextChange(String newText);

        void onSearchModeToggle(SearchView searchView, boolean visible);
    }

    public ToolbarLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mActivity = getActivity();
        mContext = context;

        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        ViewUtils.semSetRoundedCorners(mActivity.getWindow().getDecorView(), 0);

        TypedValue bgColor = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.windowBackground, bgColor, true);
        setOrientation(VERTICAL);
        if (bgColor.resourceId > 0) {
            setBackgroundColor(getResources().getColor(bgColor.resourceId));
        } else {
            setBackgroundColor(bgColor.data);
        }

        initLayoutAttrs(attrs);
        inflateChildren();
        initAppBar();

        mActivity.getOnBackPressedDispatcher().addCallback(mOnBackPressedCallback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                if (mSearchMode) dismissSearchMode();
                if (mActionMode) dismissActionMode();
            }
        });

        refreshLayout(getResources().getConfiguration());
    }

    protected void initLayoutAttrs(@Nullable AttributeSet attrs) {
        TypedArray attr = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.ToolBarLayout, 0, 0);
        try {
            mLayout = attr.getResourceId(R.styleable.ToolBarLayout_android_layout, R.layout.oui_layout_toolbarlayout);
            mExpandable = attr.getBoolean(R.styleable.ToolBarLayout_expandable, true);
            mExpanded = attr.getBoolean(R.styleable.ToolBarLayout_expanded, mExpandable);
            mNavBackButton = attr.getBoolean(R.styleable.ToolBarLayout_backButton, false);
            mTitleExpanded = mTitleCollapsed = attr.getString(R.styleable.ToolBarLayout_title);
            mSubtitleExpanded = mSubtitleCollapsed = attr.getString(R.styleable.ToolBarLayout_subtitle);
        } finally {
            attr.recycle();
        }
    }

    protected void inflateChildren() {
        if (mLayout != R.layout.oui_layout_toolbarlayout) {
            Log.w(TAG, "Inflating custom " + TAG);
        }

        LayoutInflater inflater = LayoutInflater.from(mContext);
        inflater.inflate(mLayout, this, true);
        addView(inflater.inflate(R.layout.oui_layout_toolbar_footer, this, false));
    }

    private void initAppBar() {
        root_layout = findViewById(R.id.toolbar_layout_coordinator_layout);
        appBarLayout = findViewById(R.id.toolbar_layout_app_bar);
        collapsingToolbarLayout = findViewById(R.id.toolbar_layout_collapsing_toolbar);
        mainToolbar = findViewById(R.id.toolbar_layout_main_toolbar);
        searchToolbar = findViewById(R.id.toolbar_layout_search_toolbar);
        actionModeToolbar = findViewById(R.id.toolbar_layout_action_mode_toolbar);

        searchView = findViewById(R.id.toolbar_layout_search_view);
        actionModeCheckBox = findViewById(R.id.toolbar_layout_select_all_checkbox);
        actionModeTitle = findViewById(R.id.toolbar_layout_action_mode_title);

        main_layout = findViewById(R.id.toolbar_layout_main_container);
        footer_layout = findViewById(R.id.toolbar_layout_footer);
        bottomNavigationView = findViewById(R.id.toolbar_layout_bottom_nav_view);

        if (!isInEditMode()) {
            mActivity.setSupportActionBar(mainToolbar);
            setNavigationAsBackButton();
        }

        setTitle(mTitleExpanded, mTitleCollapsed);
        setSubtitle(mSubtitleExpanded, mSubtitleCollapsed);

        searchView.seslSetUpButtonVisibility(View.VISIBLE);
        searchView.seslSetOnUpButtonClickListener(v -> dismissSearchMode());
        searchView.setSearchableInfo(((SearchManager) mContext.getSystemService(Context.SEARCH_SERVICE)).getSearchableInfo(mActivity.getComponentName()));

    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (main_layout == null || footer_layout == null) {
            super.addView(child, index, params);
        } else {
            switch (((ToolbarLayoutParams) params).layout_location) {
                default:
                case 0:
                    main_layout.addView(child, params);
                    break;
                case 1:
                    setCustomTitleView(child, new CollapsingToolbarLayout.LayoutParams(params));
                    break;
                case 2:
                    footer_layout.addView(child, params);
                    break;
                case 3:
                    root_layout.addView(child, CLLPWrapper((LayoutParams) params));
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
            WindowManagerSupport.hideStatusBarForLandscape(mActivity, newConfig.orientation);

        ViewSupport.updateListBothSideMargin(mActivity, main_layout);
        ViewSupport.updateListBothSideMargin(mActivity, findViewById(R.id.toolbar_layout_bottom_corners));
        ViewSupport.updateListBothSideMargin(mActivity, findViewById(R.id.toolbar_layout_footer_container));

        setExpanded(newConfig.orientation != Configuration.ORIENTATION_LANDSCAPE & mExpanded);
    }

    @SuppressLint("LongLogTag")
    private void resetAppBar() {
        if (appBarLayout != null) {
            if (mExpandable) {
                appBarLayout.setEnabled(true);
                appBarLayout.seslSetCustomHeightProportion(false, 0);
            } else {
                appBarLayout.setEnabled(false);
                appBarLayout.seslSetCustomHeight(mContext.getResources().getDimensionPixelSize(R.dimen.sesl_action_bar_height_with_padding));
            }
        } else
            Log.w(TAG + ".resetAppBar", "appBarLayout is null.");
    }

    //
    // AppBar methods
    //
    public AppBarLayout getAppBarLayout() {
        return appBarLayout;
    }

    public Toolbar getToolbar() {
        return mainToolbar;
    }

    public void setTitle(CharSequence title) {
        setTitle(title, title);
    }

    public void setTitle(CharSequence expandedTitle, CharSequence collapsedTitle) {
        mainToolbar.setTitle(mTitleCollapsed = collapsedTitle);
        collapsingToolbarLayout.setTitle(mTitleExpanded = expandedTitle);
    }

    public void setSubtitle(CharSequence subtitle) {
        setSubtitle(subtitle, subtitle);
    }

    public void setSubtitle(CharSequence expandedSubtitle, CharSequence collapsedSubtitle) {
        mainToolbar.setSubtitle(mSubtitleCollapsed = collapsedSubtitle);
        collapsingToolbarLayout.seslSetSubtitle(mSubtitleExpanded = expandedSubtitle);
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
        setExpanded(expanded, ViewCompat.isLaidOut(appBarLayout));
    }

    @SuppressLint("LongLogTag")
    public void setExpanded(boolean expanded, boolean animate) {
        if (mExpandable) {
            mExpanded = expanded;
            appBarLayout.setExpanded(expanded, animate);
        } else
            Log.d(TAG + ".setExpanded", "mExpandable is " + mExpandable);
    }

    public boolean isExpanded() {
        return mExpandable && !appBarLayout.seslIsCollapsed();
    }

    public void setCustomTitleView(View view) {
        setCustomTitleView(view, new CollapsingToolbarLayout.LayoutParams(view.getLayoutParams()));
    }

    public void setCustomTitleView(View view, CollapsingToolbarLayout.LayoutParams params) {
        if (params == null) {
            params = new CollapsingToolbarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        params.seslSetIsTitleCustom(true);
        collapsingToolbarLayout.seslSetCustomTitleView(view, params);
    }

    public void setCustomSubtitle(View view) {
        collapsingToolbarLayout.seslSetCustomSubtitle(view);
    }

    public void setImmersiveScroll(boolean activate) {
        appBarLayout.seslSetImmersiveScroll(activate);
    }

    public boolean isImmersiveScroll() {
        return appBarLayout.seslGetImmersiveScroll();
    }


    //
    // Search Mode methods
    //
    public void showSearchMode() {
        mSearchMode = true;
        if (mActionMode) dismissActionMode();
        mOnBackPressedCallback.setEnabled(true);
        animatedVisibility(mainToolbar, GONE);
        animatedVisibility(searchToolbar, VISIBLE);
        footer_layout.setVisibility(GONE);

        collapsingToolbarLayout.setTitle(getResources().getString(R.string.sesl_searchview_description_search));
        setExpanded(false, true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (searchModeListener != null) return searchModeListener.onQueryTextSubmit(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (searchModeListener != null)
                    return searchModeListener.onQueryTextChange(newText);
                return false;
            }
        });
        searchView.setIconified(false);

        if (searchModeListener != null) searchModeListener.onSearchModeToggle(searchView, true);
    }

    public void dismissSearchMode() {
        if (searchModeListener != null) searchModeListener.onSearchModeToggle(searchView, false);
        mSearchMode = false;
        mOnBackPressedCallback.setEnabled(false);
        searchView.setQuery("", false);
        animatedVisibility(searchToolbar, GONE);
        animatedVisibility(mainToolbar, VISIBLE);
        footer_layout.setVisibility(VISIBLE);

        setTitle(mTitleExpanded, mTitleCollapsed);
    }

    public boolean isSearchMode() {
        return mSearchMode;
    }

    public void setSearchModeListener(SearchModeListener listener) {
        searchModeListener = listener;
    }

    public void onSearchModeVoiceInputResult(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchView.setQuery(intent.getStringExtra(SearchManager.QUERY), true);
        }
    }

    //
    // Action Mode methods
    //
    public void showActionMode() {
        mActionMode = true;
        if (mSearchMode) dismissSearchMode();
        mOnBackPressedCallback.setEnabled(true);
        animatedVisibility(mainToolbar, GONE);
        animatedVisibility(actionModeToolbar, VISIBLE);
        footer_layout.setVisibility(GONE);
        bottomNavigationView.setVisibility(VISIBLE);

        setActionModeCount(0, -1);
    }

    public void dismissActionMode() {
        mActionMode = false;
        mOnBackPressedCallback.setEnabled(false);
        animatedVisibility(actionModeToolbar, GONE);
        animatedVisibility(mainToolbar, VISIBLE);
        footer_layout.setVisibility(VISIBLE);
        bottomNavigationView.setVisibility(GONE);

        setTitle(mTitleExpanded, mTitleCollapsed);
    }

    public boolean isActionMode() {
        return mActionMode;
    }

    public void setActionModeBottomMenu(@MenuRes int menuRes) {
        bottomNavigationView.inflateMenu(menuRes);
    }

    public Menu getActionModeBottomMenu() {
        return bottomNavigationView.getMenu();
    }

    public void setActionModeBottomMenuListener(NavigationBarView.OnItemSelectedListener listener) {
        bottomNavigationView.setOnItemSelectedListener(listener);
    }

    public void setActionModeCount(int count, int total) {
        String title = count > 0 ? getResources().getString(R.string.oui_action_mode_n_selected, count) : getResources().getString(R.string.oui_action_mode_select_items);

        collapsingToolbarLayout.setTitle(title);
        actionModeTitle.setText(title);
        bottomNavigationView.setVisibility(count > 0 ? VISIBLE : GONE);

        actionModeCheckBox.setChecked(count == total);
    }

    public void setActionModeCheckboxListener(CompoundButton.OnCheckedChangeListener listener) {
        actionModeCheckBox.setOnCheckedChangeListener(listener);
    }

    //
    // Navigation Button methods
    //
    public void setNavigationAsBackButton() {
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(mNavBackButton);
        if (mNavBackButton) setNavigationOnClickListener(v -> mActivity.onBackPressed());
    }

    public void setNavigationButton(Drawable navigationIcon, CharSequence tooltip) {
        mainToolbar.setNavigationIcon(navigationIcon);
        mainToolbar.setNavigationContentDescription(tooltip);
    }

    public void setNavigationOnClickListener(OnClickListener listener) {
        mainToolbar.setNavigationOnClickListener(listener);
    }

    /*public void setNavigationButtonBadge(int count) {
        if (navigationBadgeBackground == null) {
            navigationBadgeBackground = (ViewGroup) ((LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.oui_navigation_button_badge_layout, navigationButtonContainer, false);
            navigationBadgeText = (TextView) navigationBadgeBackground.getChildAt(0);
            navigationBadgeText.setTextSize(0, (float) ((int) getResources().getDimension(R.dimen.sesl_menu_item_badge_text_size)));
            navigationButtonContainer.addView(navigationBadgeBackground);
        }
        if (navigationBadgeText != null) {
            if (count > 0) {
                if (count > 99) {
                    count = 99;
                }
                String countString = mNumberFormat.format((long) count);
                navigationBadgeText.setText(countString);
                int width = (int) (getResources().getDimension(R.dimen.sesl_badge_default_width) + (float) countString.length() * getResources().getDimension(R.dimen.sesl_badge_additional_width));
                MarginLayoutParams lp = (MarginLayoutParams) navigationBadgeBackground.getLayoutParams();
                lp.width = width;
                lp.height = (int) getResources().getDimension(R.dimen.sesl_menu_item_badge_size);
                navigationBadgeBackground.setLayoutParams(lp);
                navigationBadgeBackground.setVisibility(View.VISIBLE);
            } else if (count == N_BADGE) {
                navigationBadgeText.setText(getResources().getString(R.string.sesl_action_menu_overflow_badge_text_n));
                navigationBadgeBackground.setVisibility(View.VISIBLE);
            } else {
                navigationBadgeBackground.setVisibility(View.GONE);
            }
        }
    }*/


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

    private void animatedVisibility(View view, @Visibility int visibility) {
        view.setVisibility(VISIBLE);
        view.animate().alphaBy(1.0f).alpha(visibility == VISIBLE ? 1.0f : 0.0f).setDuration(200).setInterpolator(new PathInterpolator(0.33f, 0.0f, 0.1f, 1.0f)).withEndAction(() -> view.setVisibility(visibility)).start();
    }

}
