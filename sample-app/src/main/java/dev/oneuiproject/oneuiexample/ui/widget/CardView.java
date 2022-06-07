package dev.oneuiproject.oneuiexample.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sec.sesl.tester.R;

public class CardView extends LinearLayout {
    boolean mIsIconView;
    boolean mIsDividerViewVisible;
    private Context mContext;

    private FrameLayout mParentView;
    private LinearLayout mContainerView;
    private ImageView mIconImageView;
    private TextView mTitleTextView;
    private TextView mSummaryTextView;
    private View mDividerView;

    private int mIconColor;
    private Drawable mIconDrawable;
    private String mTitleText;
    private String mSummaryText;

    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setStyleable(attrs);
        init();
    }

    private void setStyleable(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.CardView);
        mIconDrawable = a.getDrawable(R.styleable.CardView_IconDrawable);
        mIconColor = a.getColor(R.styleable.CardView_IconColor, -1);
        mTitleText = a.getString(R.styleable.CardView_TitleText);
        mSummaryText = a.getString(R.styleable.CardView_SummaryText);
        mIsIconView = mIconDrawable != null;
        mIsDividerViewVisible = a.getBoolean(R.styleable.CardView_isDividerViewVisible, false);
        a.recycle();
    }

    private void init() {
        removeAllViews();

        if (mIsIconView) {
            inflate(mContext, R.layout.sample3_widget_cardview_icon, this);

            mIconImageView = findViewById(R.id.cardview_icon);
            mIconImageView.setImageDrawable(mIconDrawable);
            if (mIconColor != -1) {
                mIconImageView.getDrawable().setTint(mIconColor);
            }
        } else {
            inflate(mContext, R.layout.sample3_widget_cardview, this);
        }

        mParentView = findViewById(R.id.cardview_main_container);

        mContainerView = findViewById(R.id.cardview_container);

        mTitleTextView = findViewById(R.id.cardview_title);
        mTitleTextView.setText(mTitleText);

        mSummaryTextView = findViewById(R.id.cardview_summary);
        if (mSummaryText != null && !mSummaryText.isEmpty()) {
            mSummaryTextView.setText(mSummaryText);
            mSummaryTextView.setVisibility(View.VISIBLE);
        }

        mDividerView = findViewById(R.id.cardview_divider);
        MarginLayoutParams lp = (MarginLayoutParams) mDividerView.getLayoutParams();
        lp.setMarginStart(mIsIconView ?
                getResources().getDimensionPixelSize(R.dimen.cardview_icon_divider_margin_end)
                        + getResources().getDimensionPixelSize(R.dimen.cardview_icon_size)
                        + getResources().getDimensionPixelSize(R.dimen.cardview_icon_margin_end)
                        - getResources().getDimensionPixelSize(R.dimen.cardview_icon_margin_vertical)
                : getResources().getDimensionPixelSize(R.dimen.cardview_icon_divider_margin_end));
        lp.setMarginEnd(getResources().getDimensionPixelSize(R.dimen.cardview_icon_divider_margin_end));
        mDividerView.setVisibility(mIsDividerViewVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setFocusable(enabled);
        setClickable(enabled);
        mParentView.setEnabled(enabled);
        mContainerView.setAlpha(enabled ? 1.0f : 0.4f);
    }

    public String getTitleText() {
        return mTitleText;
    }

    public void setTitleText(String title) {
        mTitleText = title;
        mTitleTextView.setText(mTitleText);
    }

    public String getSummaryText() {
        return mSummaryText;
    }

    public void setSummaryText(String text) {
        if (text == null)
            text = "";

        mSummaryText = text;
        mSummaryTextView.setText(mSummaryText);
        if (mSummaryText.isEmpty())
            mSummaryTextView.setVisibility(View.GONE);
        else
            mSummaryTextView.setVisibility(View.VISIBLE);
    }

    public void setDividerVisible(boolean visible) {
        mDividerView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
