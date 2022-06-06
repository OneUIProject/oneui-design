package dev.oneuiproject.oneuiexample.fragment;

import com.sec.sesl.tester.R;
import dev.oneuiproject.oneuiexample.base.BaseFragment;

public class WidgetsFragment extends BaseFragment {

    @Override
    public int getLayoutResId() {
        return R.layout.sample3_fragment_widgets;
    }

    @Override
    public int getIconResId() {
        return R.drawable.drawer_page_icon_widgets;
    }

    @Override
    public CharSequence getTitle() {
        return "Widgets";
    }

}
