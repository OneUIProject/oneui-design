package dev.oneuiproject.oneui.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;

import androidx.annotation.Px;
import androidx.appcompat.util.SeslRoundedCorner;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceViewHolder;

import dev.oneuiproject.oneui.design.R;

public class InsetPreferenceCategory extends PreferenceCategory {
    private int mHeight = 0;

    public InsetPreferenceCategory(Context context) {
        this(context, null);
    }

    public InsetPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);

        mHeight = context.getResources()
                .getDimensionPixelSize(R.dimen.sesl_list_subheader_min_height);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.InsetPreferenceCategory);
            mHeight = a.getDimensionPixelSize(
                    R.styleable.InsetPreferenceCategory_height,
                    mHeight);
            seslSetSubheaderRoundedBackground(a.getInt(
                    R.styleable.InsetPreferenceCategory_roundedCorners,
                    SeslRoundedCorner.ROUNDED_CORNER_ALL));
            a.recycle();
        }
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        lp.height = mHeight;
        holder.itemView.setLayoutParams(lp);

        holder.itemView.setImportantForAccessibility(
                View.IMPORTANT_FOR_ACCESSIBILITY_NO);
    }

    public void setHeight(@Px int height) {
        if (height >= 0) {
            mHeight = height;
        }
    }
}