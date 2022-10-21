package dev.oneuiproject.oneui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import dev.oneuiproject.oneui.design.R;

public class RelatedCard extends FrameLayout {
    private static final String TAG = "RelatedCard";
    private LinearLayout mCardViewsContainer;
    private TextView mCardTitle;
    private String mTitle;

    public RelatedCard(@NonNull Context context) {
        this(context, null);
    }

    public RelatedCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RelatedCard(@NonNull Context context, @Nullable AttributeSet attrs,
                            int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RelatedCard(@NonNull Context context, @Nullable AttributeSet attrs,
                            int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.oui_view_relative_link, this, true);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RelatedCard,
                0, 0);
        try {
            mTitle = a.getString(R.styleable.RelatedCard_title);
        } finally {
            a.recycle();
        }

        mCardTitle = findViewById(R.id.link_title);
        mCardViewsContainer = findViewById(R.id.link_container);

        if (mTitle != null && !mTitle.isEmpty()) {
            mCardTitle.setText(mTitle);
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (mCardViewsContainer == null) {
            super.addView(child, index, params);
        } else {
            if (child instanceof TextView) {
                child.setFocusable(true);
                child.setClickable(true);
                child.setBackgroundResource(R.drawable.oui_relative_link_item_background);
                ((TextView) child).setTextAppearance(R.style.OneUI_RelativeLinkTextViewTextStyle);
                mCardViewsContainer.addView(child, index,
                        new LinearLayout.LayoutParams(
                                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            } else {
                Log.e(TAG, "Error while trying to add " + child.getClass().getSimpleName() +
                        ", only TextView can be added as child");
            }

        }
    }

    public String getTitleText() {
        return mTitle;
    }

    public void setTitleText(@StringRes int resid) {
        setTitleText(getContext().getResources().getString(resid));
    }

    public void setTitleText(String title) {
        mTitle = title;
        mCardTitle.setText(mTitle);
    }
}
