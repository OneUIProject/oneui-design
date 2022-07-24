package dev.oneuiproject.oneuiexample.fragment;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.SeslMenuItem;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SeslProgressBar;
import androidx.appcompat.widget.SwitchCompat;
import androidx.apppickerview.widget.AppPickerView;

import com.sec.sesl.tester.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dev.oneuiproject.oneuiexample.base.BaseFragment;

public class AppPickerFragment extends BaseFragment
        implements AppPickerView.OnBindListener, AdapterView.OnItemSelectedListener {
    private boolean mListInitialized = false;
    private int mListType = AppPickerView.TYPE_LIST;
    private boolean mShowSystemApps = false;

    private final List<Boolean> mItems = new ArrayList<>();
    private boolean mIsAllAppsSelected = false;
    private int mCheckedPosition = 0;

    private AppPickerView mAppPickerView;
    private SeslProgressBar mProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgress = view.findViewById(R.id.apppicker_progress);
        mAppPickerView = view.findViewById(R.id.apppicker_list);
        mAppPickerView.setItemAnimator(null);
        mAppPickerView.seslSetSmoothScrollEnabled(true);
        initSpinner(view);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && !mListInitialized) {
            fillListView();
            mListInitialized = true;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem systemAppsItem = menu.findItem(R.id.menu_apppicker_system);
        systemAppsItem.setVisible(true);
        if (mShowSystemApps) {
            systemAppsItem.setTitle("Hide system apps");
        } else {
            systemAppsItem.setTitle("Show system apps");
        }
        ((SeslMenuItem) systemAppsItem)
                .setBadgeText(getString(R.string.oui_new_badge_text));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_apppicker_system) {
            ((SeslMenuItem) item)
                    .setBadgeText(null);

            mShowSystemApps = !mShowSystemApps;
            if (mShowSystemApps) {
                item.setTitle("Hide system apps");
            } else {
                item.setTitle("Show system apps");
            }

            refreshListView();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.sample3_fragment_apppicker;
    }

    @Override
    public int getIconResId() {
        return R.drawable.ic_oui_all_apps;
    }

    @Override
    public CharSequence getTitle() {
        return "AppPickerView";
    }

    private void initSpinner(@NonNull View view) {
        AppCompatSpinner spinner = view.findViewById(R.id.apppicker_spinner);

        List<String> categories = new ArrayList<>();
        categories.add("List");
        categories.add("List, Action Button");
        categories.add("List, CheckBox");
        categories.add("List, CheckBox, All apps");
        categories.add("List, RadioButton");
        categories.add("List, Switch");
        categories.add("List, Switch, All apps");
        categories.add("Grid");
        categories.add("Grid, CheckBox");

        ArrayAdapter<String> adapter
                = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mListType = position;
        fillListView();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    private void fillListView() {
        mIsAllAppsSelected = false;
        showProgressCircle(true);
        new Thread() {
            @Override
            public void run() {
                if (!mListInitialized) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) { }
                }
                requireActivity().runOnUiThread(() -> {
                    ArrayList<String> installedAppSet
                            = new ArrayList<>(getInstalledPackageNameUnmodifiableSet());

                    if (mAppPickerView.getItemDecorationCount() > 0) {
                        for (int i = 0; i < mAppPickerView.getItemDecorationCount(); i++) {
                            mAppPickerView.removeItemDecorationAt(i);
                        }
                    }

                    mAppPickerView.setAppPickerView(mListType,
                            installedAppSet, AppPickerView.ORDER_ASCENDING_IGNORE_CASE);
                    mAppPickerView.setOnBindListener(AppPickerFragment.this);

                    mItems.clear();
                    if (mListType == AppPickerView.TYPE_LIST_CHECKBOX_WITH_ALL_APPS
                            || mListType == AppPickerView.TYPE_LIST_SWITCH_WITH_ALL_APPS) {
                        mItems.add(Boolean.FALSE);
                    }
                    for (String app : installedAppSet) {
                        mItems.add(Boolean.FALSE);
                    }

                    showProgressCircle(false);
                });
            }
        }.start();
    }

    private void refreshListView() {
        showProgressCircle(true);
        new Thread() {
            @Override
            public void run() {
                requireActivity().runOnUiThread(() -> {
                    ArrayList<String> installedAppSet
                            = new ArrayList<>(getInstalledPackageNameUnmodifiableSet());
                    mAppPickerView.resetPackages(installedAppSet);

                    mItems.clear();
                    if (mListType == AppPickerView.TYPE_LIST_CHECKBOX_WITH_ALL_APPS
                            || mListType == AppPickerView.TYPE_LIST_SWITCH_WITH_ALL_APPS) {
                        mItems.add(Boolean.FALSE);
                    }
                    for (String app : installedAppSet) {
                        mItems.add(Boolean.FALSE);
                    }

                    showProgressCircle(false);
                });
            }
        }.start();
    }

    @Override
    public void onBindViewHolder(AppPickerView.ViewHolder holder,
                                 int position, String packageName) {
        switch (mListType) {
            case AppPickerView.TYPE_LIST: {
                holder.getItem().setOnClickListener(view -> { });
            } break;

            case AppPickerView.TYPE_LIST_ACTION_BUTTON: {
                holder.getActionButton().setOnClickListener(view
                        -> Toast.makeText(mContext, "onClick", Toast.LENGTH_SHORT).show());
            } break;

            case AppPickerView.TYPE_LIST_CHECKBOX: {
                CheckBox checkBox = holder.getCheckBox();
                checkBox.setChecked(mItems.get(position));
                checkBox.setOnCheckedChangeListener((buttonView, isChecked)
                        -> mItems.set(position, isChecked));
            } break;

            case AppPickerView.TYPE_LIST_CHECKBOX_WITH_ALL_APPS: {
                CheckBox checkBox = holder.getCheckBox();
                if (position == 0) {
                    holder.getAppLabel().setText("All apps");
                    checkBox.setChecked(mIsAllAppsSelected);
                    checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (mIsAllAppsSelected != isChecked) {
                            mIsAllAppsSelected = isChecked;
                            for (int i = 0; i < mItems.size(); i++){
                                mItems.set(i, mIsAllAppsSelected);
                            }
                            mAppPickerView.refreshUI();
                        }
                    });
                } else {
                    checkBox.setChecked(mItems.get(position));
                    checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        mItems.set(position, isChecked);
                        checkAllAppsToggle();
                    });
                }
            } break;

            case AppPickerView.TYPE_LIST_RADIOBUTTON: {
                RadioButton radioButton = holder.getRadioButton();
                radioButton.setChecked(mItems.get(position));
                holder.getRadioButton().setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        if (mCheckedPosition != position) {
                            mItems.set(mCheckedPosition, false);
                            mAppPickerView.refreshUI(mCheckedPosition);
                        };
                        mItems.set(position, true);
                        mCheckedPosition = position;
                    }
                });
            } break;

            case AppPickerView.TYPE_LIST_SWITCH: {
                SwitchCompat switchWidget = holder.getSwitch();
                switchWidget.setChecked(mItems.get(position));
                switchWidget.setOnCheckedChangeListener((buttonView, isChecked)
                        -> mItems.set(position, isChecked));
            } break;

            case AppPickerView.TYPE_LIST_SWITCH_WITH_ALL_APPS: {
                SwitchCompat switchWidget = holder.getSwitch();
                if (position == 0) {
                    holder.getAppLabel().setText("All apps");
                    switchWidget.setChecked(mIsAllAppsSelected);
                    switchWidget.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (mIsAllAppsSelected != isChecked) {
                            mIsAllAppsSelected = isChecked;
                            for (int i = 0; i < mItems.size(); i++){
                                mItems.set(i, mIsAllAppsSelected);
                            }
                            mAppPickerView.refreshUI();
                        }
                    });
                } else {
                    switchWidget.setChecked(mItems.get(position));
                    switchWidget.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        mItems.set(position, isChecked);
                        checkAllAppsToggle();
                    });
                }
            } break;

            case AppPickerView.TYPE_GRID: {
                holder.getItem().setOnClickListener(view -> { });
            } break;

            case AppPickerView.TYPE_GRID_CHECKBOX: {
                CheckBox checkBox = holder.getCheckBox();
                checkBox.setChecked(mItems.get(position));
                checkBox.setOnCheckedChangeListener((buttonView, isChecked)
                        -> mItems.set(position, isChecked));
                holder.getItem().setOnClickListener(view
                        -> checkBox.setChecked(!checkBox.isChecked()));
            } break;
        }
    }

    private void checkAllAppsToggle() {
        mIsAllAppsSelected = true;
        for (boolean selected : mItems) {
            if (!selected) {
                mIsAllAppsSelected = false;
                break;
            }
        }
        mAppPickerView.refreshUI(0);
    }

    private void showProgressCircle(boolean show) {
        mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        mAppPickerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private Set<String> getInstalledPackageNameUnmodifiableSet() {
        HashSet<String> set = new HashSet<>();
        for (ApplicationInfo appInfo : getInstalledAppList()) {
            set.add(appInfo.packageName);
        }
        return Collections.unmodifiableSet(set);
    }

    private List<ApplicationInfo> getInstalledAppList() {
        ArrayList<ApplicationInfo> list = new ArrayList<>();
        List<ApplicationInfo> apps = mContext.getPackageManager()
                .getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo appInfo : apps) {
            if ((appInfo.flags & (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP
                    | ApplicationInfo.FLAG_SYSTEM)) > 0 && !mShowSystemApps) {
                continue;
            }
            list.add(appInfo);
        }
        return list;
    }
}
