package dev.oneuiproject.oneuiexample.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayoutResId(), container, false);
    }

    public int getLayoutResId() {
        return -1;
    }

    public int getIconResId() {
        return -1;
    }

    public CharSequence getTitle() {
        return null;
    }


}
