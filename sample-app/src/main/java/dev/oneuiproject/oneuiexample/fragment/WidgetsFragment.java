package dev.oneuiproject.oneuiexample.fragment;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SearchView;

import com.sec.sesl.tester.R;

import java.util.ArrayList;
import java.util.List;

import dev.oneuiproject.oneuiexample.activity.MainActivity;
import dev.oneuiproject.oneuiexample.base.BaseFragment;

public class WidgetsFragment extends BaseFragment
        implements View.OnClickListener {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int[] Ids = {R.id.fragment_btn_1,
                R.id.fragment_btn_2,
                R.id.fragment_btn_3,
                R.id.fragment_btn_4,
                R.id.fragment_btn_5};
        for (int id : Ids) view.findViewById(id).setOnClickListener(this);

        AppCompatSpinner spinner = view.findViewById(R.id.fragment_spinner);
        List<String> items = new ArrayList<>();
        for (int i = 1; i < 5; i++)
            items.add("Spinner Item " + i);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext,
                android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        SearchView searchView = view.findViewById(R.id.fragment_searchview);
        SearchManager manager = (SearchManager) mContext.getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(manager.getSearchableInfo(
                new ComponentName(mContext, MainActivity.class)));
        searchView.seslSetUpButtonVisibility(View.VISIBLE);
        searchView.seslSetOnUpButtonClickListener(this);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.sample3_fragment_widgets;
    }

    @Override
    public int getIconResId() {
        return R.drawable.ic_oui_game_launcher;
    }

    @Override
    public CharSequence getTitle() {
        return "Widgets";
    }

    @Override
    public void onClick(View v) {
        // no-op
    }

}
