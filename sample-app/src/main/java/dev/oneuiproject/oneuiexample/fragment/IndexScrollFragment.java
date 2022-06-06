package dev.oneuiproject.oneuiexample.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.database.MatrixCursor;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.util.SeslRoundedCorner;
import androidx.appcompat.util.SeslSubheaderRoundedCorner;
import androidx.indexscroll.widget.SeslCursorIndexer;
import androidx.indexscroll.widget.SeslIndexScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sec.sesl.tester.R;

import java.util.ArrayList;
import java.util.List;

import dev.oneuiproject.oneui.widget.Separator;
import dev.oneuiproject.oneuiexample.base.BaseFragment;

public class IndexScrollFragment extends BaseFragment {
    private RecyclerView mListView;
    private SeslIndexScrollView mIndexScrollView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mIndexScrollView = view.findViewById(R.id.indexscroll_view);
        initListView(view);
        initIndexScroll();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        final boolean isRtl = newConfig
                .getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
        if (mIndexScrollView != null) {
            mIndexScrollView.setIndexBarGravity(isRtl
                    ? SeslIndexScrollView.GRAVITY_INDEX_BAR_LEFT
                    : SeslIndexScrollView.GRAVITY_INDEX_BAR_RIGHT);
        }
    }

    @Override
    public int getLayoutResId() {
        return R.layout.sample3_fragment_indexscroll;
    }

    @Override
    public int getIconResId() {
        return R.drawable.ic_oui_list_extended;
    }

    @Override
    public CharSequence getTitle() {
        return "IndexScroll";
    }

    private void initListView(@NonNull View view) {
        mListView = view.findViewById(R.id.indexscroll_list);
        mListView.setLayoutManager(new LinearLayoutManager(mContext));
        mListView.setAdapter(new IndexAdapter());
        mListView.addItemDecoration(new ItemDecoration(mContext));
        mListView.setItemAnimator(null);
        mListView.seslSetFillBottomEnabled(true);
        mListView.seslSetLastRoundedCorner(true);
        mListView.seslSetIndexTipEnabled(true);
        mListView.seslSetGoToTopEnabled(true);
    }

    private void initIndexScroll() {
        final boolean isRtl = getResources().getConfiguration()
                .getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;

        mIndexScrollView.setIndexBarGravity(isRtl
                ? SeslIndexScrollView.GRAVITY_INDEX_BAR_LEFT
                : SeslIndexScrollView.GRAVITY_INDEX_BAR_RIGHT);

        MatrixCursor cursor = new MatrixCursor(new String[]{"item"});
        for (String item : listItems) {
            cursor.addRow(new String[] {item});
        }

        cursor.moveToFirst();

        SeslCursorIndexer indexer = new SeslCursorIndexer(cursor, 0, new String[]{"A", "B", "C"}, 0);
        indexer.setGroupItemsCount(1);
        indexer.setMiscItemsCount(3);

        mIndexScrollView.setIndexer(indexer);
        mIndexScrollView.setOnIndexBarEventListener(new SeslIndexScrollView.OnIndexBarEventListener() {
            @Override
            public void onIndexChanged(int sectionIndex) {
                ((LinearLayoutManager) mListView.getLayoutManager()).scrollToPositionWithOffset(sectionIndex, 0);
            }

            @Override
            public void onPressed(float v) {

            }

            @Override
            public void onReleased(float v) {

            }
        });
        mIndexScrollView.attachToRecyclerView(mListView);
    }

    public class IndexAdapter extends RecyclerView.Adapter<IndexAdapter.ViewHolder>
            implements SectionIndexer {
        List<String> mSections = new ArrayList<>();
        List<Integer> mPositionForSection = new ArrayList<>();
        List<Integer> mSectionForPosition = new ArrayList<>();

        IndexAdapter() {
            mSections.add("");
            mPositionForSection.add(0);
            mSectionForPosition.add(0);

            for (int i = 1; i < listItems.length; i++) {
                String letter = listItems[i];
                if (letter.length() == 1) {
                    mSections.add(letter);
                    mPositionForSection.add(i);
                }
                mSectionForPosition.add(mSections.size() - 1);
            }
        }

        @Override
        public int getItemCount() {
            return listItems.length;
        }

        @Override
        public int getItemViewType(int position) {
            return (listItems[position].length() == 1) ? 1 : 0;
        }

        @NonNull
        @Override
        public IndexAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 0) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                View view = inflater.inflate(R.layout.sample3_view_indexscroll_listview_item, parent, false);
                return new IndexAdapter.ViewHolder(view, false);
            } else {
                return new IndexAdapter.ViewHolder(new Separator(mContext), true);
            }
        }

        @Override
        public void onBindViewHolder(IndexAdapter.ViewHolder holder, final int position) {
            if (holder.isSeparator) {
                holder.textView.setLayoutParams(new LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            } else {
                if (position == 0) {
                    holder.imageView.setImageResource(R.drawable.indexscroll_group_icon);

                } else  {
                    holder.imageView.setImageResource(R.drawable.indexscroll_item_icon);

                }
            }
            holder.textView.setText(listItems[position]);
        }

        @Override
        public Object[] getSections() {
            return mSections.toArray();
        }

        @Override
        public int getPositionForSection(int sectionIndex) {
            return mPositionForSection.get(sectionIndex);
        }

        @Override
        public int getSectionForPosition(int position) {
            return mSectionForPosition.get(position);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            boolean isSeparator;
            ImageView imageView;
            TextView textView;

            ViewHolder(View itemView, boolean isSeparator) {
                super(itemView);
                this.isSeparator = isSeparator;
                if (isSeparator) {
                    textView = (TextView) itemView;
                } else {
                    imageView = itemView.findViewById(R.id.indexscroll_list_item_icon);
                    textView = itemView.findViewById(R.id.indexscroll_list_item_text);
                }
            }
        }
    }

    private class ItemDecoration extends RecyclerView.ItemDecoration {
        private final Drawable mDivider;
        private final SeslSubheaderRoundedCorner mRoundedCorner;

        public ItemDecoration(@NonNull Context context) {
            TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.isLightTheme, outValue, true);

            mDivider = context.getDrawable(outValue.data == 0
                    ? R.drawable.sesl_list_divider_dark
                    : R.drawable.sesl_list_divider_light);

            mRoundedCorner = new SeslSubheaderRoundedCorner(mContext);
            mRoundedCorner.setRoundedCorners(SeslRoundedCorner.ROUNDED_CORNER_ALL);
        }

        @Override
        public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent,
                           @NonNull RecyclerView.State state) {
            super.onDraw(c, parent, state);

            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                IndexAdapter.ViewHolder holder
                        = (IndexAdapter.ViewHolder) mListView.getChildViewHolder(child);
                if (!holder.isSeparator) {
                    final int top = child.getBottom()
                            + ((ViewGroup.MarginLayoutParams) child.getLayoutParams()).bottomMargin;
                    final int bottom = mDivider.getIntrinsicHeight() + top;

                    mDivider.setBounds(parent.getLeft(), top, parent.getRight(), bottom);
                    mDivider.draw(c);
                }
            }
        }

        @Override
        public void seslOnDispatchDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                IndexAdapter.ViewHolder holder
                        = (IndexAdapter.ViewHolder) mListView.getChildViewHolder(child);
                if (holder.isSeparator) {
                    mRoundedCorner.drawRoundedCorner(child, c);
                }
            }
        }
    }


    String[] listItems = {
            "Groups",
            "A",
            "Alberto",
            "Aldo",
            "Alfredo",
            "Andrea",
            "Antonio",
            "Armando",
            "Arturo",
            "B",
            "Barbara",
            "Bernardo",
            "C",
            "Carlo",
            "Carmela",
            "#",
            "1 ",
            "2 "
    };
}
