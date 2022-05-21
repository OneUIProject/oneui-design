package dev.oneuiproject.oneuiexample.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SeslSeekBar;

import com.sec.sesl.tester.R;

import dev.oneuiproject.oneui.utils.SeekBarUtils;
import dev.oneuiproject.oneui.widget.HapticSeekBar;

public class SeekBarFragment extends BaseFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        HapticSeekBar seekBar_1 = view.findViewById(R.id.fragment_seekbar_1);
        SeslSeekBar seekBar_2 = view.findViewById(R.id.fragment_seekbar_2);

        //seekBar_1.setSeamless(true);
        SeekBarUtils.showTickMark(seekBar_1, true);
        seekBar_2.setOverlapPointForDualColor(70);
        seekBar_2.setSecondaryProgress(30);
        SeekBarUtils.showOverlapPreview(seekBar_2, true);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.sample3_fragment_seek_bar;
    }

    @Override
    public int getIconResId() {
        return R.drawable.drawer_page_icon_seekbar;
    }

    @Override
    public CharSequence getTitle() {
        return "SeekBar";
    }
}
