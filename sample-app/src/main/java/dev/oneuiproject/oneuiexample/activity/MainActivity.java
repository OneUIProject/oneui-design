package dev.oneuiproject.oneuiexample.activity;

import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sec.sesl.tester.databinding.ActivityMainBinding;

import dev.oneuiproject.oneuiexample.ui.home.HomeListAdapter;
import dev.oneuiproject.oneuiexample.ui.home.HomeListItemDecoration;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initListView();
    }

    @Override
    public void onBackPressed() {
        // Fix O memory leak
        if (Build.VERSION.SDK_INT
                == Build.VERSION_CODES.O && isTaskRoot()) {
            finishAfterTransition();
        } else {
            super.onBackPressed();
        }
    }

    private void initListView() {
        mBinding.homeListView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.homeListView.setAdapter(new HomeListAdapter(this));
        mBinding.homeListView.setItemAnimator(null);
        mBinding.homeListView.addItemDecoration(new HomeListItemDecoration(this));
        mBinding.homeListView.setHasFixedSize(true);
        mBinding.homeListView.seslSetFillBottomEnabled(true);
        mBinding.homeListView.seslSetLastRoundedCorner(false);
    }
}
