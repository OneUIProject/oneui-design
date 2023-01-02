package dev.oneuiproject.oneuiexample.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sec.sesl.tester.R;
import com.sec.sesl.tester.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

import dev.oneuiproject.oneuiexample.base.FragmentInfo;
import dev.oneuiproject.oneuiexample.fragment.AppPickerFragment;
import dev.oneuiproject.oneuiexample.fragment.IconsFragment;
import dev.oneuiproject.oneuiexample.fragment.IndexScrollFragment;
import dev.oneuiproject.oneuiexample.fragment.PickersFragment;
import dev.oneuiproject.oneuiexample.fragment.PreferencesFragment;
import dev.oneuiproject.oneuiexample.fragment.ProgressBarFragment;
import dev.oneuiproject.oneuiexample.fragment.QRCodeFragment;
import dev.oneuiproject.oneuiexample.fragment.SeekBarFragment;
import dev.oneuiproject.oneuiexample.fragment.SwipeRefreshFragment;
import dev.oneuiproject.oneuiexample.fragment.TabsFragment;
import dev.oneuiproject.oneuiexample.fragment.WidgetsFragment;
import dev.oneuiproject.oneuiexample.ui.drawer.DrawerListAdapter;
import dev.oneuiproject.oneuiexample.utils.DarkModeUtils;

public class MainActivity extends AppCompatActivity
        implements DrawerListAdapter.DrawerListener {
    private ActivityMainBinding mBinding;
    private FragmentManager mFragmentManager;
    private final List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initFragmentList();
        initDrawer();
        initFragments();
    }

    @Override
    public void attachBaseContext(Context context) {
        // pre-OneUI
        if (Build.VERSION.SDK_INT <= 28) {
            super.attachBaseContext(DarkModeUtils.createDarkModeContextWrapper(context));
        } else {
            super.attachBaseContext(context);
        }
    }

    private void initFragmentList() {
        fragments.add(new WidgetsFragment());
        fragments.add(new ProgressBarFragment());
        fragments.add(new SeekBarFragment());
        fragments.add(new SwipeRefreshFragment());
        fragments.add(new PreferencesFragment());
        fragments.add(null);
        fragments.add(new TabsFragment());
        fragments.add(null);
        fragments.add(new AppPickerFragment());
        fragments.add(new IndexScrollFragment());
        fragments.add(new PickersFragment());
        fragments.add(null);
        fragments.add(new QRCodeFragment());
        fragments.add(new IconsFragment());
    }

    @Override
    public void onBackPressed() {
        // Fix O memory leak
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O
                && isTaskRoot()
                && mFragmentManager.getBackStackEntryCount() == 0) {
            finishAfterTransition();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // pre-OneUI
        if (Build.VERSION.SDK_INT <= 28) {
            final Resources res = getResources();
            res.getConfiguration().setTo(DarkModeUtils.createDarkModeConfig(this, newConfig));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.sample3_menu_main, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_about_app) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return false;
    }

    private void initDrawer() {
        mBinding.drawerLayout.setDrawerButtonIcon(getDrawable(R.drawable.ic_oui_ab_app_info));
        mBinding.drawerLayout.setDrawerButtonTooltip("About page");
        mBinding.drawerLayout.setDrawerButtonOnClickListener(view
                -> startActivity(new Intent(MainActivity.this, SampleAboutActivity.class)));

        mBinding.drawerListView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.drawerListView.setAdapter(new DrawerListAdapter(this, fragments, this));
        mBinding.drawerListView.setItemAnimator(null);
        mBinding.drawerListView.setHasFixedSize(true);
        mBinding.drawerListView.seslSetLastRoundedCorner(false);
    }

    private void initFragments() {
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        for (Fragment fragment : fragments) {
            if (fragment != null) transaction.add(R.id.main_content, fragment);
        }
        transaction.commit();
        mFragmentManager.executePendingTransactions();

        onDrawerItemSelected(0);
    }

    @Override
    public boolean onDrawerItemSelected(int position) {
        Fragment newFragment = fragments.get(position);
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        for (Fragment fragment : mFragmentManager.getFragments()) {
            transaction.hide(fragment);
        }
        transaction.show(newFragment).commit();

        if (newFragment instanceof FragmentInfo) {
            if (!((FragmentInfo) newFragment).isAppBarEnabled()) {
                mBinding.drawerLayout.setExpanded(false, false);
                mBinding.drawerLayout.setExpandable(false);
            } else {
                mBinding.drawerLayout.setExpandable(true);
                mBinding.drawerLayout.setExpanded(false, false);
            }
            mBinding.drawerLayout.setTitle(getString(R.string.app_name), ((FragmentInfo) newFragment).getTitle());
            mBinding.drawerLayout.setExpandedSubtitle(((FragmentInfo) newFragment).getTitle());
        }
        mBinding.drawerLayout.setDrawerOpen(false, true);

        return true;
    }
}
