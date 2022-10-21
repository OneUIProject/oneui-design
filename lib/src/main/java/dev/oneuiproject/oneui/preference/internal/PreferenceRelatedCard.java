package dev.oneuiproject.oneui.preference.internal;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.preference.PreferenceFragmentCompat;

import dev.oneuiproject.oneui.design.R;
import dev.oneuiproject.oneui.utils.PreferenceUtils;

public class PreferenceRelatedCard extends LinearLayout {
    private static final String TAG = "PreferenceRelatedCard";
    private Context mContext;
    private View mParentView;
    private TextView mCardTitleText;
    private LinearLayout mLinkContainer;

    PreferenceRelatedCard(@NonNull Context context) {
        this(context, null);
    }

    PreferenceRelatedCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    PreferenceRelatedCard(@NonNull Context context, @Nullable AttributeSet attrs,
                                 int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mParentView = LayoutInflater.from(mContext)
                .inflate(R.layout.oui_view_relative_link_preference, this);
        mParentView.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mCardTitleText = mParentView.findViewById(R.id.link_title);
        mLinkContainer = mParentView.findViewById(R.id.link_container);
    }

    @NonNull
    public static PreferenceRelatedCard createRelatedCard(
            @NonNull Context context) {
        return new PreferenceRelatedCard(context);
    }

    @NonNull
    public PreferenceRelatedCard setTitleText(@StringRes int resid) {
        mCardTitleText.setText(resid);
        return this;
    }

    @NonNull
    public PreferenceRelatedCard setTitleText(@Nullable CharSequence text) {
        mCardTitleText.setText(text);
        return this;
    }

    @NonNull
    public PreferenceRelatedCard addButton(@Nullable CharSequence text,
                                           @Nullable OnClickListener onClickListener) {
        TextView button = new TextView(
                new ContextThemeWrapper(mContext, R.style.OneUI_RelativeLinkTextViewTextStyle));
        button.setFocusable(true);
        button.setClickable(true);
        button.setBackgroundResource(R.drawable.oui_relative_link_item_background);
        button.setText(text);
        button.setOnClickListener(onClickListener);
        mLinkContainer.addView(button, new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        return this;
    }

    @NonNull
    public PreferenceRelatedCard removeCardButtons() {
        mLinkContainer.removeAllViews();
        return this;
    }

    public void show(@NonNull PreferenceFragmentCompat fragment) {
        if (fragment != null) {
            if (mLinkContainer != null && mLinkContainer.getChildCount() > 0) {
                PreferenceUtils.addRelatedCardToFooter(fragment, mParentView);
            } else {
                Log.e(TAG, "show(): Failed to add RelatedCard, " +
                        "this RelatedCard doesn't have any buttons.");
            }
        } else {
            Log.e(TAG, "show(): fragment is null");
        }
    }
}
