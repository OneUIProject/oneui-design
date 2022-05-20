package dev.oneuiproject.oneuiexample.ui.home;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.sec.sesl.tester.R;

public class HomeListViewHolder extends RecyclerView.ViewHolder {
    private final boolean mIsSubHeader;
    private TextView mSubheaderTextView;

    private FrameLayout mIconContainer;
    private AppCompatImageView mIconView;
    private TextView mTitleView;
    private TextView mSummaryView;

    public HomeListViewHolder(@NonNull View itemView, boolean isSubHeader) {
        super(itemView);

        mIsSubHeader = isSubHeader;
        if (mIsSubHeader) {
            if (itemView instanceof TextView) {
                mSubheaderTextView = (TextView) itemView;
            }
        } else {
            mIconContainer = itemView.findViewById(R.id.home_item_icon_container);
            mIconView = itemView.findViewById(R.id.home_item_icon);
            mTitleView = itemView.findViewById(R.id.home_item_title);
            mSummaryView = itemView.findViewById(R.id.home_item_summary);
        }
    }

    public boolean isSubHeader() {
        return mIsSubHeader;
    }

    public void setSubHeaderText(@Nullable CharSequence text) {
        if (mIsSubHeader) {
            ViewGroup.LayoutParams lp = mSubheaderTextView.getLayoutParams();
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mSubheaderTextView.setLayoutParams(lp);
            mSubheaderTextView.setText(text);
        }
    }

    public void setIcon(@Nullable Drawable icon) {
        if (!mIsSubHeader) {
            if (icon == null) {
                mIconContainer.setVisibility(View.GONE);
                mIconView.setImageDrawable(null);
            } else {
                mIconContainer.setVisibility(View.VISIBLE);
                mIconView.setImageDrawable(icon);
            }
        }
    }

    public void setTitle(@Nullable CharSequence title) {
        if (!mIsSubHeader) {
            mTitleView.setText(title);
        }
    }

    public void setSummary(@Nullable CharSequence summary) {
        if (!mIsSubHeader) {
            mSummaryView.setText(summary);
        }
    }
}
