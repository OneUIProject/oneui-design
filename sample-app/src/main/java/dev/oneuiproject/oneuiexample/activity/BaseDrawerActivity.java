package dev.oneuiproject.oneuiexample.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.SeslMenuItem;
import androidx.core.view.MenuCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sec.sesl.tester.R;
import com.sec.sesl.tester.databinding.ActivityBaseDrawerBinding;

import java.util.Arrays;
import java.util.List;

import dev.oneuiproject.oneuiexample.ui.drawer.DrawerListAdapter;
import dev.oneuiproject.oneuiexample.ui.drawer.DrawerListener;
import dev.oneuiproject.oneuiexample.ui.samples.EasterEgg;
import dev.oneuiproject.oneuiexample.ui.samples.ProgressBar;

public class BaseDrawerActivity extends AppCompatActivity
        implements DrawerListener {
    private static final String FRAGMENT_EE = "fragment_ee";
    private static final String FRAGMENT_PROGRESS_BAR = "fragment_progress_bar";

    private Context mContext;
    private ActivityBaseDrawerBinding mBinding;
    private DrawerListAdapter mListAdapter;
    private FragmentManager mFragmentManager;
    private Fragment mCurrentFragment;

    private TypedArray mTitles;
    private List<String> mTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mBinding = ActivityBaseDrawerBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initDrawer();

        mTitles = getResources().obtainTypedArray(R.array.sample3_drawer_list_items_title);
        mTags = Arrays.asList(getResources().getStringArray(R.array.sample3_drawer_items_tag));

        initFragments();
    }

    @Override
    public void onBackPressed() {
        // Fix O memory leak
        if (Build.VERSION.SDK_INT
                == Build.VERSION_CODES.O && isTaskRoot()
                && mFragmentManager.getBackStackEntryCount() == 0) {
            finishAfterTransition();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mBinding.drawerDrawerlayout.setDrawerOpen(false, false);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sample3_menu_drawer, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);

        SeslMenuItem item = (SeslMenuItem) menu.findItem(R.id.menu_test_item_1);
        item.setBadgeText("1");

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
        mBinding.drawerDrawerlayout.setDrawerButtonIcon(
                getDrawable(R.drawable.ic_oui_ab_app_info));
        mBinding.drawerDrawerlayout.setDrawerButtonTooltip("About page");

        mBinding.drawerListView.setLayoutManager(new LinearLayoutManager(this));
        mListAdapter = new DrawerListAdapter(this, this);
        mBinding.drawerListView.setAdapter(mListAdapter);
        mBinding.drawerListView.setItemAnimator(null);
        mBinding.drawerListView.setHasFixedSize(true);
        mBinding.drawerListView.seslSetLastRoundedCorner(false);
    }

    private void initFragments() {
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.add(R.id.drawer_content, new ProgressBar(), FRAGMENT_PROGRESS_BAR);
        transaction.add(R.id.drawer_content, new EasterEgg(), FRAGMENT_EE);
        transaction.commit();
        mFragmentManager.executePendingTransactions();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            final String currentTag = extras.getString("fragment_class_tag");
            final int itemPos = mTags.indexOf(currentTag);
            mListAdapter.setSelectedItem(itemPos);
            mBinding.drawerDrawerlayout.setTitle(
                            getString(R.string.app_name),
                    mTitles.getString(itemPos));
            mBinding.drawerDrawerlayout.setExpandedSubtitle(
                    mTitles.getString(itemPos));
            mCurrentFragment = mFragmentManager
                    .findFragmentByTag(currentTag);
        }

        if (mCurrentFragment == null) {
            mCurrentFragment = mFragmentManager.findFragmentByTag(FRAGMENT_EE);
        }

        transaction = mFragmentManager.beginTransaction();
        for (Fragment fragment : mFragmentManager.getFragments()) {
            transaction.hide(fragment);
        }
        transaction.show(mCurrentFragment);
        transaction.commit();
        mFragmentManager.executePendingTransactions();
    }

    @Override
    public boolean onDrawerItemSelected(int position) {
        final String currentTag = mTags.get(position);

        if (currentTag.startsWith("fragment")) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.hide(mCurrentFragment).commit();

            mBinding.drawerDrawerlayout.setTitle(
                    getString(R.string.app_name),
                    mTitles.getString(mTags.indexOf(currentTag)));
            mBinding.drawerDrawerlayout.setExpandedSubtitle(
                    mTitles.getString(mTags.indexOf(currentTag)));

            mCurrentFragment = mFragmentManager.findFragmentByTag(currentTag);
            if (mCurrentFragment == null) {
                mCurrentFragment = mFragmentManager.findFragmentByTag(FRAGMENT_EE);
            }

            mFragmentManager.beginTransaction().show(mCurrentFragment).commit();

            mBinding.drawerDrawerlayout.setDrawerOpen(false, true);
            return true;
        } else if (currentTag.startsWith("activity")) {
            Class<?> activity = null;
            switch (currentTag) {
                case "activity_about":
                    activity = AboutActivity.class;
                    break;
            }
            mContext.startActivity(new Intent(mContext, activity));
        }

        return false;
    }
}
