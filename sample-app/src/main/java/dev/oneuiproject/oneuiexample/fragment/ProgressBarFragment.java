package dev.oneuiproject.oneuiexample.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SeslProgressBar;

import com.sec.sesl.tester.R;
import dev.oneuiproject.oneuiexample.base.BaseFragment;

public class ProgressBarFragment extends BaseFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int[] Ids = {R.id.fragment_progressbar_1,
                R.id.fragment_progressbar_2,
                R.id.fragment_progressbar_3,
                R.id.fragment_progressbar_4};
        for (int id : Ids) {
            SeslProgressBar progressBar = view.findViewById(id);
            progressBar.setMode(SeslProgressBar.MODE_CIRCLE);
            progressBar.setProgress(40);
        }
    }

    @Override
    public int getLayoutResId() {
        return R.layout.sample3_fragment_progress_bar;
    }

    @Override
    public int getIconResId() {
        return R.drawable.drawer_page_icon_progressbar;
    }

    @Override
    public CharSequence getTitle() {
        return "ProgressBar";
    }

}
