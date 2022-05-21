package dev.oneuiproject.oneuiexample.fragment;

import com.sec.sesl.tester.R;

public class IconsFragment extends BaseFragment {
    @Override
    public int getLayoutResId() {
        return R.layout.sample3_fragment_icons;
    }

    @Override
    public int getIconResId() {
        return R.drawable.drawer_page_icon_icons;
    }

    @Override
    public CharSequence getTitle() {
        return "Icons";
    }
}
