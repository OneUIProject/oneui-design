package dev.oneuiproject.oneuiexample.ui.drawer;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.sec.sesl.tester.R;

public class DrawerListAdapter extends RecyclerView.Adapter<DrawerListViewHolder> {
    private static final String TAG = "DrawerListAdapter";
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private Context mContext;
    private DrawerListener mListener;
    private int mSelectedPos;

    private int[] mViewTypes;
    private TypedArray mIcons;
    private TypedArray mTitles;

    public DrawerListAdapter(@NonNull Context context, @Nullable DrawerListener listener) {
        mContext = context;
        mListener = listener;

        final Resources res = mContext.getResources();
        mViewTypes = res.getIntArray(R.array.sample3_drawer_list_items_view_type);
        mIcons = res.obtainTypedArray(R.array.sample3_drawer_list_items_icon);
        mTitles = res.obtainTypedArray(R.array.sample3_drawer_list_items_title);
    }

    @NonNull
    @Override
    public DrawerListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        final boolean isSeparator = viewType == TYPE_SEPARATOR;
        View view;
        if (isSeparator) {
            view = inflater.inflate(R.layout.sample3_view_drawer_list_separator, parent, false);
        } else {
            view = inflater.inflate(R.layout.sample3_view_drawer_list_item, parent, false);
        }

        return new DrawerListViewHolder(view, isSeparator);
    }

    @Override
    public void onBindViewHolder(@NonNull DrawerListViewHolder holder, int position) {
        if (!holder.isSeparator()) {
            holder.setIcon(mIcons.getDrawable(position));
            holder.setTitle(mTitles.getString(position));
            holder.setSelected(position == mSelectedPos);
            holder.itemView.setOnClickListener(v -> {
                final int itemPos = holder.getBindingAdapterPosition();
                boolean result = false;
                if (mListener != null) {
                    result = mListener.onDrawerItemSelected(itemPos);
                }
                if (result) {
                    setSelectedItem(itemPos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mTitles.length();
    }


    @Override
    public int getItemViewType(int position) {
        return mViewTypes[position];
    }

    public void setSelectedItem(int position) {
        mSelectedPos = position;
        notifyItemRangeChanged(0, getItemCount());
    }
}
