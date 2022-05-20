package dev.oneuiproject.oneuiexample.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.sec.sesl.tester.R;

import dev.oneuiproject.oneuiexample.activity.AboutActivity;
import dev.oneuiproject.oneuiexample.activity.BaseDrawerActivity;

public class HomeListAdapter extends RecyclerView.Adapter<HomeListViewHolder> {
    private static final String TAG = "HomeListAdapter";
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SUBHEADER = 1;
    private Context mContext;

    private int[] mViewTypes;
    private TypedArray mIcons;
    private TypedArray mTitles;
    private String[] mTags;

    public HomeListAdapter(@NonNull Context context) {
        mContext = context;

        final Resources res = mContext.getResources();
        mViewTypes = res.getIntArray(R.array.sample3_home_list_items_view_type);
        mIcons = res.obtainTypedArray(R.array.sample3_home_list_items_icon_home);
        mTitles = res.obtainTypedArray(R.array.sample3_home_list_items_title);
        mTags = res.getStringArray(R.array.sample3_home_items_tag);
    }

    @NonNull
    @Override
    public HomeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        final boolean isSubHeader = viewType == TYPE_SUBHEADER;
        View view;
        if (isSubHeader) {
            view = inflater.inflate(R.layout.sample3_view_home_list_subheader, parent, false);
        } else {
            view = inflater.inflate(R.layout.sample3_view_home_list_item, parent, false);
        }

        return new HomeListViewHolder(view, isSubHeader);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeListViewHolder holder, int position) {
        if (holder.isSubHeader()) {
            String subHeaderText = mTitles.getString(position);
            if (subHeaderText != null && !subHeaderText.isEmpty()) {
                holder.setSubHeaderText(subHeaderText);
            }
        } else {
            int iconResId = mIcons.getResourceId(position, 0);
            if (iconResId != 0) holder.setIcon(mContext.getDrawable(iconResId));
            holder.setTitle(mTitles.getString(position));
            holder.itemView.setOnClickListener(view -> openActivity(position));
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

    private void openActivity(int position) {
        final String currentTag = mTags[position];

        if (currentTag.startsWith("fragment")) {
            Intent i = new Intent(mContext, BaseDrawerActivity.class);
            i.putExtra("fragment_class_tag", currentTag);
            mContext.startActivity(i);
        } else if (currentTag.startsWith("activity")) {
            Class<?> activity = null;
            switch (currentTag) {
                case "activity_about":
                    activity = AboutActivity.class;
                    break;
            }
            mContext.startActivity(new Intent(mContext, activity));
        }
    }
}
