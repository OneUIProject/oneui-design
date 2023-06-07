package dev.oneuiproject.oneuiexample.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sec.sesl.tester.R;
import dev.oneuiproject.oneuiexample.base.BaseFragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class IconsFragment extends BaseFragment {
    /*todo search*/
    private final List<Integer> mIconsId = new ArrayList<>();

    public IconsFragment() {
        super();

        Class<dev.oneuiproject.oneui.R.drawable> rClass = dev.oneuiproject.oneui.R.drawable.class;
        for (Field field : rClass.getDeclaredFields()) {
            try {
                mIconsId.add(field.getInt(null));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView iconListView = (RecyclerView) getView();
        iconListView.setLayoutManager(new LinearLayoutManager(mContext));
        iconListView.setAdapter(new ImageAdapter());
        iconListView.addItemDecoration(new ItemDecoration(mContext));
        iconListView.setItemAnimator(null);
        iconListView.seslSetFillBottomEnabled(true);
        iconListView.seslSetLastRoundedCorner(true);
        iconListView.seslSetFastScrollerEnabled(true);
        iconListView.seslSetGoToTopEnabled(true);
        iconListView.seslSetSmoothScrollEnabled(true);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.sample3_fragment_icons;
    }

    @Override
    public int getIconResId() {
        return R.drawable.ic_oui_emoji_2;
    }

    @Override
    public CharSequence getTitle() {
        return "Icons";
    }


    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder>
            implements SectionIndexer {
        List<String> mSections = new ArrayList<>();
        List<Integer> mPositionForSection = new ArrayList<>();
        List<Integer> mSectionForPosition = new ArrayList<>();

        ImageAdapter() {
            for (int i = 0; i < mIconsId.size(); i++) {
                String letter = getResources().getResourceEntryName(mIconsId.get(i))
                        .replace("ic_oui_", "").substring(0, 1).toUpperCase();

                if (Character.isDigit(letter.charAt(0))) {
                    letter = "#";
                }

                if (i == 0 || !mSections.get(mSections.size() - 1).equals(letter)) {
                    mSections.add(letter);
                    mPositionForSection.add(i);
                }
                mSectionForPosition.add(mSections.size() - 1);
            }
        }

        @Override
        public int getItemCount() {
            return mIconsId.size();
        }

        @NonNull
        @Override
        public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(
                    R.layout.sample3_view_icon_listview_item, parent, false);
            return new ImageAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ImageAdapter.ViewHolder holder, final int position) {
            holder.imageView.setImageResource(mIconsId.get(position));
            holder.textView.setText(getResources().getResourceEntryName(mIconsId.get(position)));
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
            ImageView imageView;
            TextView textView;

            ViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.icon_list_item_icon);
                textView = itemView.findViewById(R.id.icon_list_item_text);
            }
        }
    }

    private class ItemDecoration extends RecyclerView.ItemDecoration {
        private final Drawable mDivider;

        public ItemDecoration(@NonNull Context context) {
            TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.isLightTheme, outValue, true);

            mDivider = context.getDrawable(outValue.data == 0
                    ? R.drawable.sesl_list_divider_dark
                    : R.drawable.sesl_list_divider_light);
        }

        public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent,
                           @NonNull RecyclerView.State state) {
            super.onDraw(c, parent, state);

            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                final int top = child.getBottom()
                        + ((ViewGroup.MarginLayoutParams) child.getLayoutParams()).bottomMargin;
                final int bottom = mDivider.getIntrinsicHeight() + top;

                mDivider.setBounds(parent.getLeft(), top, parent.getRight(), bottom);
                mDivider.draw(c);
            }
        }
    }
}
