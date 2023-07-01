package dev.oneuiproject.oneui.layout;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SeslProgressBar;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import dev.oneuiproject.oneui.design.R;

/**
 * Custom App Info Layout like in any App from Samsung. App name and version are automatically added by default.
 */
public class AppInfoLayout extends ToolbarLayout {
    private static final String TAG = "AppInfoLayout";

    /**
     * Updates aren't possible in this app. Buttons and status text won't be shown.
     */
    public static final int NOT_UPDATEABLE = -1;
    /**
     * The app is checking for updates. A {@link SeslProgressBar} will be shown.
     */
    public static final int LOADING = 0;
    /**
     * There is a update available and the update button will be visible.
     *
     * @see #setMainButtonClickListener(OnClickListener)
     */
    public static final int UPDATE_AVAILABLE = 1;
    /**
     * There are now updates available.
     */
    public static final int NO_UPDATE = 2;
    /**
     * The device has no internet connection. Show a retry button.
     *
     * @see #setMainButtonClickListener(OnClickListener)
     */
    public static final int NO_CONNECTION = 3;

    /**
     * {@link #NOT_UPDATEABLE}, {@link #LOADING}, {@link #UPDATE_AVAILABLE}, {@link #NO_UPDATE}, {@link #NO_CONNECTION}
     */
    @IntDef({LOADING, UPDATE_AVAILABLE, NO_UPDATE, NOT_UPDATEABLE, NO_CONNECTION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {
    }

    /**
     * Listener for the update and retry button.
     */
    public interface OnClickListener {
        void onUpdateClicked(View v);

        void onRetryClicked(View v);
    }

    private static final int MAIN_CONTENT = 0;

    private OnClickListener mButtonListener;
    private CharSequence mAppName;
    private int mStatus;

    private LinearLayout mAILContainer;
    private TextView mAppNameTextView, mVersionTextView, mUpdateNotice;
    private AppCompatButton mUpdateButton;
    private SeslProgressBar mProgressBar;

    public AppInfoLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setNavigationButtonAsBack();
        if (!isInEditMode()) {
            mActivity.setSupportActionBar(null);
        }
        LayoutInflater.from(mContext)
                .inflate(R.layout.oui_layout_app_info, mMainContainer, true);
        mAILContainer = findViewById(R.id.app_info_lower_layout);
        mAppNameTextView = findViewById(R.id.app_info_name);
        mVersionTextView = findViewById(R.id.app_info_version);
        mUpdateNotice = findViewById(R.id.app_info_update_notice);
        mUpdateButton = findViewById(R.id.app_info_update);
        mProgressBar = findViewById(R.id.app_info_progress);

        setLayoutMargins();
        initButtonWidth(mUpdateButton);

        setTitle(mAppName);
        setVersionText();
        setStatus(LOADING);

        getToolbar().inflateMenu(R.menu.app_info_menu);
        getToolbar().setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_app_info) {
                openSettingsAppInfo();
                return true;
            }
            return false;
        });

        mUpdateButton.setOnClickListener(v -> {
            if (mButtonListener != null) {
                if (mStatus == UPDATE_AVAILABLE) mButtonListener.onUpdateClicked(v);
                if (mStatus == NO_CONNECTION) mButtonListener.onRetryClicked(v);
            }
        });
    }

    @Override
    protected void initLayoutAttrs(@Nullable AttributeSet attrs) {
        super.initLayoutAttrs(attrs);
        mTitleExpanded = mTitleCollapsed = null;

        TypedArray a = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.ToolbarLayout, 0, 0);
        try {
            mExpanded = a.getBoolean(R.styleable.ToolbarLayout_expanded, false);
            mAppName = a.getString(R.styleable.ToolbarLayout_title);
        } finally {
            a.recycle();
        }
        if (mAppName == null) mAppName = mContext.getString(R.string.app_name);
    }

    private void setLayoutMargins() {
        View mEmptyTop = findViewById(R.id.app_info_empty_view_top);
        View mEmptyBottom = findViewById(R.id.app_info_empty_view_bottom);
        if (mEmptyTop != null && mEmptyBottom != null
                && getResources().getConfiguration().orientation == 1) {
            int h = getResources().getDisplayMetrics().heightPixels;
            mEmptyTop.getLayoutParams().height = (int) (h * 0.12d);
            mEmptyBottom.getLayoutParams().height = (int) (h * 0.10d);
        }
    }

    private void setVersionText() {
        String version = "unknown";
        if ((!isInEditMode())) try {
            version = mContext.getPackageManager().getPackageInfo(
                    mContext.getApplicationContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        mVersionTextView.setText(mContext.getString(R.string.version_info, version));
    }

    /**
     * Set a custom App Info title. The default will be your App's name.
     */
    @Override
    public void setTitle(@Nullable CharSequence title) {
        mAppNameTextView.setText(mAppName = title);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (mAILContainer == null) {
            super.addView(child, index, params);
        } else {
            if (((ToolbarLayoutParams) params).layout_location == MAIN_CONTENT) {
                mAILContainer.addView(child, params);
                if (child instanceof Button) initButtonWidth((Button) child);
            } else {
                super.addView(child, index, params);
            }
        }
    }

    /**
     * Set the App Info's update state.
     *
     * @param status {@link #NOT_UPDATEABLE}, {@link #LOADING}, {@link #UPDATE_AVAILABLE}, {@link #NO_UPDATE}, {@link #NO_CONNECTION}
     */
    public void setStatus(@Status int status) {
        switch (mStatus = status) {
            case NOT_UPDATEABLE:
                mProgressBar.setVisibility(GONE);
                mUpdateNotice.setVisibility(GONE);
                mUpdateButton.setVisibility(GONE);
                break;
            case LOADING:
                mProgressBar.setVisibility(VISIBLE);
                mUpdateNotice.setVisibility(GONE);
                mUpdateButton.setVisibility(GONE);
                break;
            case UPDATE_AVAILABLE:
                mProgressBar.setVisibility(GONE);
                mUpdateNotice.setVisibility(VISIBLE);
                mUpdateButton.setVisibility(VISIBLE);
                mUpdateNotice.setText(mContext.getText(R.string.new_version_is_available));
                mUpdateButton.setText(mContext.getText(R.string.update));
                break;
            case NO_UPDATE:
                mProgressBar.setVisibility(GONE);
                mUpdateNotice.setVisibility(VISIBLE);
                mUpdateButton.setVisibility(GONE);
                mUpdateNotice.setText(mContext.getText(R.string.latest_version));
                break;
            case NO_CONNECTION:
                mProgressBar.setVisibility(GONE);
                mUpdateNotice.setVisibility(VISIBLE);
                mUpdateButton.setVisibility(VISIBLE);
                mUpdateNotice.setText(mContext.getText(R.string.cant_check_for_updates_phone));
                mUpdateButton.setText(mContext.getText(R.string.retry));
                break;
        }
    }

    /**
     * Get the App Info's current update state.
     *
     * @see Status
     */
    public int getStatus() {
        return mStatus;
    }

    /**
     * Set the listener for the update and retry button.
     */
    public void setMainButtonClickListener(OnClickListener listener) {
        mButtonListener = listener;
    }

    /**
     * Add another TextView below the version text. Returns this TextView.
     *
     * @param text the text for the TextView
     */
    public TextView addOptionalText(CharSequence text) {
        LinearLayout parent = findViewById(R.id.app_info_upper_layout);
        TextView optionalText = new TextView(mContext);
        optionalText.setText(text);
        optionalText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        optionalText.setTextColor(mContext.getColor(R.color.oui_appinfolayout_info_text_color));
        optionalText.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        optionalText.setLayoutParams(mVersionTextView.getLayoutParams());
        parent.addView(optionalText, parent.indexOfChild(mProgressBar));
        return optionalText;
    }

    /**
     * Open the App Info page in the system settings.
     */
    private void openSettingsAppInfo() {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS",
                Uri.fromParts("package", mContext.getPackageName(), null));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
    }

    /**
     * Apply the correct width to buttons. This shouldn't be necessary, as it is done automatically.
     * Only use it if buttons at the bottom of the AppInfoLayout don't have the correct width.
     *
     * @param button Button which should be resized
     */
    public void initButtonWidth(Button button) {
        Resources res = getResources();
        final int w = res.getDisplayMetrics().widthPixels
                / res.getConfiguration().orientation;
        ViewGroup.LayoutParams lp = button.getLayoutParams();
        lp.width = (int) (w * 0.61d);
    }
}
