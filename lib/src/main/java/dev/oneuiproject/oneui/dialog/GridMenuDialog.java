package dev.oneuiproject.oneui.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.SupportMenuInflater;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import java.util.ArrayList;

import dev.oneuiproject.oneui.design.R;

public class GridMenuDialog extends AlertDialog {
    private static final String TAG = "GridMenuDialog";
    private static final int DEFAULT_SPAN_COUNT = 4;

    private Context mContext;
    private CharSequence mMessage;
    private int mSpanCount = DEFAULT_SPAN_COUNT;
    private ArrayList<GridMenuItem> mMenuList = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    private LinearLayout mContentView;
    private RecyclerView mGridListView;
    private GridListAdapter mAdapter;

    public class GridMenuItem {
        private boolean mEnabled = true;
        private CharSequence mTooltipText;

        @IdRes
        private int mId;
        private Drawable mIcon;
        private CharSequence mTitle;
        private boolean mShowBadge = false;

        GridMenuItem(@NonNull MenuItem menuItem) {
            mId = menuItem.getItemId();
            mIcon = menuItem.getIcon();
            mTitle = menuItem.getTitle();
            mEnabled = menuItem.isEnabled();
            mTooltipText = MenuItemCompat.getTooltipText(menuItem);
        }

        GridMenuItem(@IdRes int id, @Nullable Drawable icon,
                     @Nullable CharSequence title) {
            mId = id;
            mIcon = icon;
            mTitle = title;
        }

        @IdRes
        public int getItemId() {
            return mId;
        }

        @Nullable
        public Drawable getIcon() {
            return mIcon;
        }

        @NonNull
        public GridMenuItem setIcon(@Nullable Drawable icon) {
            mIcon = icon;
            return this;
        }

        @Nullable
        public CharSequence getTitle() {
            return mTitle;
        }

        @NonNull
        public GridMenuItem setTitle(@Nullable CharSequence title) {
            mTitle = title;
            return this;
        }

        public boolean isBadgeVisible() {
            return mShowBadge;
        }

        @NonNull
        public GridMenuItem showBadge(boolean show) {
            mShowBadge = show;
            return this;
        }

        public boolean isEnabled() {
            return mEnabled;
        }

        @NonNull
        public GridMenuItem setEnabled(boolean enabled) {
            mEnabled = enabled;
            return this;
        }

        @Nullable
        public CharSequence getTooltipText() {
            return mTooltipText;
        }

        @NonNull
        public GridMenuItem setTooltipText(@Nullable CharSequence title) {
            mTooltipText = title;
            return this;
        }
    }

    public interface OnItemClickListener {
        boolean onClick(GridMenuItem item);
    }

    public GridMenuDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public GridMenuDialog(@NonNull Context context,
                          @StyleRes int theme) {
        super(context, theme);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mContentView = (LinearLayout) inflater
                .inflate(R.layout.oui_dialog_grid_menu, null);
        resetContentPadding();

        mGridListView = mContentView.findViewById(R.id.grid_menu_view);
        mAdapter = new GridListAdapter();
        mGridListView.setLayoutManager(new GridLayoutManager(mContext, mSpanCount));
        mGridListView.setAdapter(mAdapter);
        mGridListView.addItemDecoration(new GridListItemDecoration());

        RecyclerView.ItemAnimator animator = mGridListView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        setView(mContentView);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void show() {
        if (mMenuList != null
                && mMenuList.size() > 0) {
            super.show();
        } else {
            Log.e(TAG, "show(): this GridMenu has no items.");
        }
    }

    @Override
    public void setMessage(CharSequence message) {
        super.setMessage(message);
        mMessage = message;
        resetContentPadding();
    }

    @Override
    public void setButton(int whichButton, CharSequence text, Message msg) {
        Log.e(TAG, "setButton: GridMenuDialog doesn't supports buttons");
    }

    @Override
    public void setButton(int whichButton, CharSequence text, OnClickListener listener) {
        Log.e(TAG, "setButton: GridMenuDialog doesn't supports buttons");
    }

    @Override
    public void setButton(int whichButton, CharSequence text, Drawable icon,
                          OnClickListener listener) {
        Log.e(TAG, "setButton: GridMenuDialog doesn't supports buttons");
    }

    private void resetContentPadding() {
        if (mContentView != null) {
            final int horizontalPadding = mContext.getResources()
                    .getDimensionPixelSize(R.dimen.oui_grid_menu_dialog_horizontal_padding);
            final int verticalPadding = mContext.getResources()
                    .getDimensionPixelSize(R.dimen.oui_grid_menu_dialog_vertical_padding);

            final boolean hasMessage
                    = mMessage != null && mMessage.length() > 0;
            mContentView.setPaddingRelative(
                    horizontalPadding,
                    hasMessage ? 0 : verticalPadding,
                    horizontalPadding,
                    verticalPadding);
        }
    }

    @SuppressLint("RestrictedApi")
    public void inflateMenu(@MenuRes int menuRes) {
        Menu menu = new MenuBuilder(mContext);
        new SupportMenuInflater(mContext).inflate(menuRes, menu);
        inflateMenu(menu);
    }

    public void inflateMenu(@NonNull Menu menu) {
        if (menu != null) {
            mMenuList.clear();

            for (int i = 0; i < menu.size(); i++) {
                MenuItem menuItem = menu.getItem(i);
                if (menuItem != null
                        && menuItem.isVisible()) {
                    GridMenuItem gridItem = new GridMenuItem(menuItem);
                    mMenuList.add(gridItem);
                }
            }

            updateAllItems();
        } else {
            Log.e(TAG, "inflateMenu: menu is null");
        }
    }

    public void setSpanCount(int spanCount) {
        mSpanCount = spanCount;

        if (mGridListView != null) {
            GridLayoutManager layoutManager
                    = (GridLayoutManager) mGridListView.getLayoutManager();
            if (layoutManager != null) {
                layoutManager.setSpanCount(mSpanCount);
            }
            updateAllItems();
        }
    }

    public void addItem(@NonNull GridMenuItem gridItem) {
        if (gridItem != null) {
            mMenuList.add(gridItem);
            updateAllItems();
        } else {
            Log.e(TAG, "addItem: gridItem is null");
        }
    }

    public void addItem(int index, @NonNull GridMenuItem gridItem) {
        if (gridItem != null) {
            mMenuList.add(index, gridItem);
            updateAllItems();
        } else {
            Log.e(TAG, "addItem: gridItem is null");
        }
    }

    @Nullable
    public GridMenuItem getItem(int index) {
        GridMenuItem gridItem = mMenuList.get(index);
        if (gridItem != null) {
            return gridItem;
        } else {
            Log.e(TAG, "getItem: gridItem is null");
            return null;
        }
    }

    @Nullable
    public GridMenuItem findItem(@IdRes int id) {
        for (int i = 0; i < mMenuList.size(); i++) {
            GridMenuItem item = mMenuList.get(i);
            if (item != null && item.getItemId() == id) {
                return item;
            }
        }

        Log.e(TAG, "findItem: couldn't find item with id 0x"
                + Integer.toHexString(id));
        return null;
    }

    public void removeItem(int index) {
        mMenuList.remove(index);
        updateAllItems();
    }

    public void removeItem(@NonNull GridMenuItem gridItem) {
        if (gridItem != null && mMenuList.contains(gridItem)) {
            mMenuList.remove(gridItem);
            updateAllItems();
        } else {
            Log.e(TAG, "removeItem: gridItem is either null " +
                    "or not in this GridMenu");
        }
    }

    public void removeItemWithId(@IdRes int id) {
        GridMenuItem gridItem = findItem(id);
        if (gridItem != null) {
            mMenuList.remove(gridItem);
            updateAllItems();
        } else {
            Log.e(TAG, "removeItemWithId: couldn't find item with id 0x"
                    + Integer.toHexString(id));
        }
    }

    @NonNull
    public GridMenuItem newItem(@IdRes int id, @Nullable Drawable icon,
                                @Nullable CharSequence title) {
        GridMenuItem gridItem = new GridMenuItem(id, icon, title);
        return gridItem;
    }

    public void updateItem(int index) {
        if (mAdapter != null) {
            mAdapter.notifyItemChanged(index);
        } else {
            Log.e(TAG, "updateItem: list adapter " +
                    "has not been initiated yet");
        }
    }

    public void updateItem(@NonNull GridMenuItem gridItem) {
        if (gridItem != null && mMenuList.contains(gridItem)) {
            final int index = mMenuList.indexOf(gridItem);

            if (mAdapter != null) {
                mAdapter.notifyItemChanged(index);
            } else {
                Log.e(TAG, "updateItem: list adapter " +
                        "has not been initiated yet");
            }
        } else {
            Log.e(TAG, "updateItem: gridItem is either null " +
                    "or not in this GridMenu");
        }
    }

    public void updateItemWithId(@IdRes int id) {
        GridMenuItem gridItem = findItem(id);
        if (gridItem != null) {
            final int index = mMenuList.indexOf(gridItem);

            if (mAdapter != null) {
                mAdapter.notifyItemChanged(index);
            } else {
                Log.e(TAG, "updateItemWithId: list adapter " +
                        "has not been initiated yet");
            }
        } else {
            Log.e(TAG, "updateItemWithId: couldn't find item with id 0x"
                    + Integer.toHexString(id));
        }
    }

    private void updateAllItems() {
        if (mAdapter != null) {
            mAdapter.notifyItemRangeChanged(0, mMenuList.size());
        } else {
            Log.e(TAG, "updateAllItems: list adapter " +
                    "has not been initiated yet");
        }
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    private class GridListAdapter extends RecyclerView.Adapter<GridListViewHolder> {
        @NonNull
        @Override
        public GridListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater
                    .inflate(R.layout.oui_view_grid_menu_dialog_item, null, false);
            return new GridListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GridListViewHolder holder, int position) {
            GridMenuItem gridItem = mMenuList.get(position);
            holder.itemView.setOnClickListener(v -> {
                boolean result = false;
                if (mOnItemClickListener != null) {
                    result = mOnItemClickListener.onClick(gridItem);
                }
                if (result) {
                    dismiss();
                }
            });
            holder.icon.setImageDrawable(gridItem.getIcon());
            holder.title.setText(gridItem.getTitle());
            holder.showBadge(gridItem.isBadgeVisible());
            holder.setEnabled(gridItem.isEnabled());
            holder.setTooltipText(gridItem.getTooltipText());
        }

        @Override
        public int getItemCount() {
            return mMenuList.size();
        }
    }

    private class GridListViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout itemView;
        public ImageView icon;
        private TextView mBadge;
        public TextView title;

        public GridListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = (LinearLayout) itemView;
            icon = itemView.findViewById(R.id.grid_menu_item_icon);
            mBadge = itemView.findViewById(R.id.grid_menu_item_badge);
            title = itemView.findViewById(R.id.grid_menu_item_title);
        }

        public void showBadge(boolean show) {
            if (mBadge != null) {
                mBadge.setVisibility(show
                        ? View.VISIBLE
                        : View.GONE);
            }
        }

        public void setEnabled(boolean enabled) {
            itemView.setEnabled(enabled);
            itemView.setAlpha(enabled ? 1.0f : 0.4f);
        }

        public void setTooltipText(CharSequence tooltipText) {
            TooltipCompat.setTooltipText(itemView, tooltipText);
        }
    }

    private class GridListItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            final int position = parent.getChildAdapterPosition(view);
            final int column = position % mSpanCount;

            final int itemGap = view.getResources()
                    .getDimensionPixelSize(R.dimen.oui_grid_menu_dialog_item_gap);

            outRect.left = (column * itemGap) / mSpanCount;
            outRect.right = itemGap - (((column + 1) * itemGap) / mSpanCount);
            if (position >= mSpanCount) {
                outRect.top = itemGap;
            }
        }
    }

}
