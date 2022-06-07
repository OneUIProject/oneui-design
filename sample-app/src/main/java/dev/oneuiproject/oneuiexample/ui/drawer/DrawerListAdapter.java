package dev.oneuiproject.oneuiexample.ui.drawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.sec.sesl.tester.R;

import java.util.List;

import dev.oneuiproject.oneuiexample.base.FragmentInfo;

public class DrawerListAdapter extends RecyclerView.Adapter<DrawerListViewHolder> {
    private Context mContext;
    private List<Fragment> mFragments;
    private DrawerListener mListener;
    private int mSelectedPos;

    public interface DrawerListener {
        boolean onDrawerItemSelected(int position);
    }

    public DrawerListAdapter(@NonNull Context context, List<Fragment> fragments,
                             @Nullable DrawerListener listener) {
        mContext = context;
        mFragments = fragments;
        mListener = listener;
    }

    @NonNull
    @Override
    public DrawerListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        final boolean isSeparator = viewType == 0;
        View view;
        if (isSeparator) {
            view = inflater.inflate(
                    R.layout.sample3_view_drawer_list_separator, parent, false);
        } else {
            view = inflater.inflate(
                    R.layout.sample3_view_drawer_list_item, parent, false);
        }

        return new DrawerListViewHolder(view, isSeparator);
    }

    @Override
    public void onBindViewHolder(@NonNull DrawerListViewHolder holder, int position) {
        if (!holder.isSeparator()) {
            Fragment fragment = mFragments.get(position);
            if (fragment instanceof FragmentInfo) {
                holder.setIcon(((FragmentInfo) fragment).getIconResId());
                holder.setTitle(((FragmentInfo) fragment).getTitle());
            }
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
        return mFragments.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (mFragments.get(position) == null) ? 0 : 1;
    }

    public void setSelectedItem(int position) {
        mSelectedPos = position;
        notifyItemRangeChanged(0, getItemCount());
    }
}
