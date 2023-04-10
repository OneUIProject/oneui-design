package dev.oneuiproject.oneui.dialog.internal;

import android.content.Context;
import android.util.AttributeSet;

import androidx.arch.core.util.Function;

import com.google.android.material.tabs.TabLayout;

import java.util.Locale;
import java.util.Objects;

public class StartEndTabLayout extends TabLayout {
    private int mTabIndex;
    private Function<Integer, String> mTimeFormatter;
    private final int[] mTimes;

    public interface OnTabSelectedListener {
        void onTabSelected(int index, int time);
    }

    public StartEndTabLayout(Context context) {
        this(context, null);
    }

    public StartEndTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StartEndTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mTimes = new int[2];
        this.mTabIndex = 0;
        this.mTimeFormatter = new Function() {
            @Override
            public Object apply(Object obj) {
                String defaultFormatter;
                defaultFormatter = StartEndTabLayout.this.defaultFormatter((Integer) obj);
                return defaultFormatter;
            }
        };
        seslSetSubTabStyle();
    }

    public void init(int startTime, int endTime, final OnTabSelectedListener onTabSelectedListener, Function<Integer, String> function) {
        addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabReselected(Tab tab) {
            }

            @Override
            public void onTabUnselected(Tab tab) {
            }

            @Override
            public void onTabSelected(Tab tab) {
                StartEndTabLayout.this.mTabIndex = tab.getPosition();
                onTabSelectedListener.onTabSelected(StartEndTabLayout.this.mTabIndex, StartEndTabLayout.this.mTimes[StartEndTabLayout.this.mTabIndex]);
            }
        });
        this.mTimes[0] = startTime;
        this.mTimes[1] = endTime;
        this.mTimeFormatter = function;
        reload();
        Tab tabAt = getTabAt(this.mTabIndex);
        if (tabAt.isSelected()) {
            onTabSelectedListener.onTabSelected(this.mTabIndex, this.mTimes[this.mTabIndex]);
            return;
        }
        tabAt.select();
    }

    public void updateTime(int time) {
        updateTime(this.mTabIndex, time);
    }

    public void select(int index) {
        Tab tabAt = getTabAt(index);
        Objects.requireNonNull(tabAt);
        tabAt.select();
    }

    public void reload() {
        for (int i = 0; i < this.mTimes.length; i++) {
            updateTime(i, this.mTimes[i]);
        }
    }

    public int[] getTimes() {
        return this.mTimes;
    }

    private void updateTime(int index, int time) {
        this.mTimes[index] = time;
        Tab tabAt = getTabAt(index);
        Objects.requireNonNull(tabAt);
        tabAt.seslSetSubText(this.mTimeFormatter.apply(time));
    }

    public String defaultFormatter(int time) {
        return String.format(Locale.getDefault(), "%02d:%02d", time / 60, time % 60);
    }
}
