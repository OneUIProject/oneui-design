package dev.oneuiproject.oneuiexample.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.SeslMenuItem;
import androidx.core.view.MenuCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sec.sesl.tester.R;

import java.util.ArrayList;
import java.util.List;

import dev.oneuiproject.oneui.layout.DrawerLayout;
import dev.oneuiproject.oneuiexample.fragment.BaseFragment;
import dev.oneuiproject.oneuiexample.fragment.CompoundButtonsFragment;
import dev.oneuiproject.oneuiexample.fragment.IconsFragment;
import dev.oneuiproject.oneuiexample.fragment.ProgressBarFragment;
import dev.oneuiproject.oneuiexample.fragment.SeekBarFragment;
import dev.oneuiproject.oneuiexample.ui.drawer.DrawerListAdapter;

public class MainActivity extends AppCompatActivity implements DrawerListAdapter.DrawerListener {

    private FragmentManager mFragmentManager;

    private DrawerLayout drawerLayout;
    private RecyclerView drawerListView;

    private List<BaseFragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawerLayout);
        drawerListView = findViewById(R.id.drawer_list_view);

        initFragmentList();
        initDrawer();
        initFragments();
    }

    private void initFragmentList() {
        fragments.add(new IconsFragment());
        fragments.add(null);
        fragments.add(new ProgressBarFragment());
        fragments.add(new SeekBarFragment());
        fragments.add(null);
        fragments.add(new CompoundButtonsFragment());
    }

    @Override
    public void onBackPressed() {
        // Fix O memory leak
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTaskRoot() && mFragmentManager.getBackStackEntryCount() == 0) {
            finishAfterTransition();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerLayout.setDrawerOpen(false, false);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.sample3_menu_drawer, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);

        SeslMenuItem item = (SeslMenuItem) menu.findItem(R.id.menu_test_item_1);
        item.setBadgeText("1");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_about_app) {
            startActivity(new Intent(this, AboutActivity2.class));
            return true;
        }
        return false;
    }

    private void initDrawer() {
        drawerLayout.setDrawerButtonIcon(getDrawable(R.drawable.ic_oui_ab_app_info));
        drawerLayout.setDrawerButtonTooltip("About page");

        drawerListView.setLayoutManager(new LinearLayoutManager(this));
        drawerListView.setAdapter(new DrawerListAdapter(this, fragments, this));
        drawerListView.setItemAnimator(null);
        drawerListView.setHasFixedSize(true);
        drawerListView.seslSetLastRoundedCorner(false);
    }

    private void initFragments() {
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        for (BaseFragment fragment : fragments) {
            if (fragment != null) transaction.add(R.id.main_content, fragment);
        }
        transaction.commit();
        mFragmentManager.executePendingTransactions();

        onDrawerItemSelected(0);
    }

    @Override
    public boolean onDrawerItemSelected(int position) {
        BaseFragment newFragment = fragments.get(position);
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        for (Fragment fragment : mFragmentManager.getFragments()) {
            transaction.hide(fragment);
        }
        transaction.show(newFragment).commit();

        drawerLayout.setTitle(getString(R.string.app_name), newFragment.getTitle());
        drawerLayout.setExpandedSubtitle(newFragment.getTitle());
        drawerLayout.setDrawerOpen(false, true);
        return true;
    }
}
