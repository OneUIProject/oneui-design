package dev.oneuiproject.oneuiexample.ui.drawer;

import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.sec.sesl.tester.R;

public class DrawerListViewHolder extends RecyclerView.ViewHolder {
    private final boolean mIsSeparator;
    private Typeface mNormalTypeface;
    private Typeface mSelectedTypeface;

    private AppCompatImageView mIconView;
    private TextView mTitleView;

    public DrawerListViewHolder(@NonNull View itemView, boolean isSeparator) {
        super(itemView);
        mIsSeparator = isSeparator;
        if (!mIsSeparator) {
            mIconView = itemView.findViewById(R.id.drawer_item_icon);
            mTitleView = itemView.findViewById(R.id.drawer_item_title);
            mNormalTypeface = Typeface.create("sec-roboto-light", Typeface.NORMAL);
            mSelectedTypeface = Typeface.create("sec-roboto-light", Typeface.BOLD);
        }
    }

    public boolean isSeparator() {
        return mIsSeparator;
    }

    public void setIcon(@DrawableRes int resId) {
        if (!mIsSeparator) {
            mIconView.setImageResource(resId);
        }
    }

    public void setTitle(@Nullable CharSequence title) {
        if (!mIsSeparator) {
            mTitleView.setText(title);
        }
    }

    public void setSelected(boolean selected) {
        if (!mIsSeparator) {
            itemView.setSelected(selected);
            mTitleView.setTypeface(selected ? mSelectedTypeface : mNormalTypeface);
            mTitleView.setEllipsize(selected ?
                    TextUtils.TruncateAt.MARQUEE : TextUtils.TruncateAt.END);
        }
    }
}
