package dev.oneuiproject.oneuiexample.ui.home;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.util.SeslRoundedCorner;
import androidx.appcompat.util.SeslSubheaderRoundedCorner;
import androidx.recyclerview.widget.RecyclerView;

import com.sec.sesl.tester.R;

public class HomeListItemDecoration extends RecyclerView.ItemDecoration {
    private Context mContext;
    private Drawable mDivider;
    private SeslSubheaderRoundedCorner mRoundedCorner;

    public HomeListItemDecoration(@NonNull Context context) {
        mContext = context;
        mDivider = mContext.getDrawable(R.drawable.home_page_divider);
        mRoundedCorner = new SeslSubheaderRoundedCorner(context);
        mRoundedCorner.setRoundedCorners(SeslRoundedCorner.ROUNDED_CORNER_ALL);
    }

    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent,
                       @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);

        final Resources res = mContext.getResources();

        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            HomeListViewHolder holder
                    = (HomeListViewHolder) parent.getChildViewHolder(child);
            if (!holder.isSubHeader()) {
                final int left;
                final int right;
                if (parent.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                    left = res.getDimensionPixelSize(R.dimen.home_list_item_view_padding);
                    right = parent.getWidth()
                            - res.getDimensionPixelSize(R.dimen.home_list_item_icon_size)
                            - res.getDimensionPixelSize(R.dimen.home_list_item_icon_padding_start)
                            - res.getDimensionPixelSize(R.dimen.home_list_item_icon_padding_end);
                } else {
                    left = parent.getLeft()
                            + res.getDimensionPixelSize(R.dimen.home_list_item_icon_size)
                            + res.getDimensionPixelSize(R.dimen.home_list_item_icon_padding_start)
                            + res.getDimensionPixelSize(R.dimen.home_list_item_icon_padding_end);
                    right = parent.getRight()
                            - res.getDimensionPixelSize(R.dimen.home_list_item_view_padding);
                }

                final int top = child.getBottom()
                        + ((ViewGroup.MarginLayoutParams) child.getLayoutParams()).bottomMargin;
                final int bottom = mDivider.getIntrinsicHeight() + top;

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    @Override
    public void seslOnDispatchDraw(Canvas c, RecyclerView parent,
                                   RecyclerView.State state) {
        super.seslOnDispatchDraw(c, parent, state);

        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            HomeListViewHolder holder
                    = (HomeListViewHolder) parent.getChildViewHolder(child);
            if (holder.isSubHeader()) {
                mRoundedCorner.drawRoundedCorner(child, c);
            }
        }
    }
}
