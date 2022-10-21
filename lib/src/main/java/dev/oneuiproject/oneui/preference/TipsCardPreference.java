package dev.oneuiproject.oneui.preference;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import java.util.ArrayList;

import dev.oneuiproject.oneui.design.R;

public class TipsCardPreference extends Preference {
    private Context mContext;
     @ColorInt private int mTextColor;
     private View.OnClickListener mCancelBtnOCL;
     private final ArrayList<TextView> mBottomBarBtns = new ArrayList<>();

     private View mItemView;
     private RelativeLayout mTitleContainer;
     private AppCompatImageView mCancelButton;
     private View mEmptyBottom;
     private LinearLayout mBottomBar;

    public TipsCardPreference(@NonNull Context context, @Nullable AttributeSet attrs,
                                int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        setSelectable(false);
        setLayoutResource(R.layout.oui_preference_tips_layout);
        mTextColor = ContextCompat.getColor(mContext, R.color.oui_primary_text_color);
    }

    public TipsCardPreference(@NonNull Context context, @Nullable AttributeSet attrs,
                                int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TipsCardPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.preferenceStyle);
    }

    public TipsCardPreference(@NonNull Context context) {
        this(context, null);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        mItemView = preferenceViewHolder.itemView;

        // workaround since we can't use setSelectable here
        if (getOnPreferenceClickListener() != null) {
            mItemView.setOnClickListener(v -> getOnPreferenceClickListener()
                    .onPreferenceClick(TipsCardPreference.this));
        }

        ((TextView) mItemView.findViewById(android.R.id.title)).setTextColor(mTextColor);
        ((TextView) mItemView.findViewById(android.R.id.summary)).setTextColor(mTextColor);

        mTitleContainer = mItemView.findViewById(R.id.tips_title_container);
        mCancelButton = mItemView.findViewById(R.id.tips_cancel_button);
        mEmptyBottom = mItemView.findViewById(R.id.tips_empty_bottom);
        mBottomBar = mItemView.findViewById(R.id.tips_bottom_bar);

        if (!TextUtils.isEmpty(getTitle())) {
            mTitleContainer.setVisibility(View.VISIBLE);
        }

        if (mCancelBtnOCL != null) {
            mCancelButton.setVisibility(View.VISIBLE);
            mCancelButton.setOnClickListener(mCancelBtnOCL);
        }

        if (mBottomBarBtns.size() > 0) {
            mBottomBar.setVisibility(View.VISIBLE);
            ((ViewGroup) mItemView).removeView(mEmptyBottom);
            mEmptyBottom = null;

            for (TextView txtView : mBottomBarBtns) {
                mBottomBar.addView(txtView);
            }
            mBottomBarBtns.clear();
        }
    }

    public TextView addButton(@Nullable CharSequence text, @Nullable View.OnClickListener listener) {
        TextView txtView = new TextView(mContext, null, 0,
                R.style.OneUI_TipsCardTextButtonStyle);
        txtView.setText(text);
        txtView.setOnClickListener(listener);

        if (mBottomBar != null) {
            mBottomBar.setVisibility(View.VISIBLE);
            if (mEmptyBottom != null) {
                ((ViewGroup) mItemView).removeView(mEmptyBottom);
                mEmptyBottom = null;
            }
            mBottomBar.addView(txtView);
        } else {
            mBottomBarBtns.add(txtView);
        }

        return txtView;
    }

    public void setOnCancelClickListener(@Nullable View.OnClickListener listener) {
        mCancelBtnOCL = listener;
        if (mCancelButton != null) {
            mCancelButton.setVisibility(mCancelBtnOCL == null
                    ? View.GONE
                    : View.VISIBLE);
            mCancelButton.setOnClickListener(mCancelBtnOCL);
        }
    }
}
