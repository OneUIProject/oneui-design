package dev.oneuiproject.oneuiexample.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.sec.sesl.tester.R;

import dev.oneuiproject.oneui.dialog.GridMenuDialog;
import dev.oneuiproject.oneui.utils.TabLayoutUtils;
import dev.oneuiproject.oneuiexample.base.BaseFragment;

public class TabsFragment extends BaseFragment {
    private TabLayout mSubTabs;
    private BottomNavigationView mBottomNavView;
    private BottomNavigationView mBottomNavViewText;
    private TabLayout mTabs;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSubTabs(view);
        initBNV(view);
        initMainTabs(view);
        initToggles(view);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.sample3_fragment_tabs;
    }

    @Override
    public int getIconResId() {
        return R.drawable.ic_oui_prompt_from_menu;
    }

    @Override
    public CharSequence getTitle() {
        return "Navigation";
    }

    private void initSubTabs(@NonNull View view) {
        mSubTabs = view.findViewById(R.id.tabs_subtab);
        mSubTabs.seslSetSubTabStyle();
        mSubTabs.setTabMode(TabLayout.SESL_MODE_WEIGHT_AUTO);
        mSubTabs.addTab(mSubTabs.newTab().setText("Tab 1"));
        mSubTabs.addTab(mSubTabs.newTab().setText("Tab 2"));
        mSubTabs.addTab(mSubTabs.newTab().setText("Tab 3"));
    }

    private void initBNV(@NonNull View view) {
        mBottomNavView = view.findViewById(R.id.tabs_bottomnav);
        mBottomNavViewText = view.findViewById(R.id.tabs_bottomnav_text);
        mBottomNavView.seslSetGroupDividerEnabled(true);
    }

    private void initMainTabs(@NonNull View view) {
        mTabs = view.findViewById(R.id.tabs_tabs);
        mTabs.addTab(mTabs.newTab().setText("Tab 1"));
        mTabs.addTab(mTabs.newTab().setText("Tab 2"));
        mTabs.addTab(mTabs.newTab().setText("Tab 3"));

        GridMenuDialog gridMenuDialog = new GridMenuDialog(mContext);
        gridMenuDialog.inflateMenu(R.menu.sample3_tabs_grid_menu);
        gridMenuDialog.setOnItemClickListener(item -> true);

        TabLayoutUtils.addCustomButton(mTabs, R.drawable.ic_oui_drawer,
                v -> gridMenuDialog.show());
    }

    private void initToggles(@NonNull View view) {
        SwitchCompat subTabSwitch = view.findViewById(R.id.tabs_subtab_switch);
        subTabSwitch.setOnCheckedChangeListener((buttonView, isChecked)
                -> mSubTabs.setVisibility(isChecked
                ? View.VISIBLE
                : View.GONE));
        subTabSwitch.setChecked(true);

        RadioGroup tabsRadioGroup = view.findViewById(R.id.tabs_radio_group);
        tabsRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.tabs_radio_bvnt) {
                mBottomNavViewText.setVisibility(View.VISIBLE);
                mBottomNavView.setVisibility(View.GONE);
                mTabs.setVisibility(View.GONE);
            } else if (checkedId == R.id.tabs_radio_bvni) {
                mBottomNavViewText.setVisibility(View.GONE);
                mBottomNavView.setVisibility(View.VISIBLE);
                mTabs.setVisibility(View.GONE);
            } else if (checkedId == R.id.tabs_radio_tabs) {
                mBottomNavViewText.setVisibility(View.GONE);
                mBottomNavView.setVisibility(View.GONE);
                mTabs.setVisibility(View.VISIBLE);
            }
        });
    }
}
