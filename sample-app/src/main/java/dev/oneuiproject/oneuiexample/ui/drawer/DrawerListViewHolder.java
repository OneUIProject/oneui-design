package dev.oneuiproject.oneuiexample.ui.drawer;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
            mNormalTypeface = Typeface.create("sans-serif", Typeface.NORMAL);
            mSelectedTypeface = Typeface.create("sans-serif-medium", Typeface.NORMAL);
        }
    }

    public boolean isSeparator() {
        return mIsSeparator;
    }

    public void setIcon(@Nullable Drawable icon) {
        if (!mIsSeparator) {
            mIconView.setImageDrawable(icon);
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
