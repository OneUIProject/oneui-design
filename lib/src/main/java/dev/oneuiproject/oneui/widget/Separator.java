package dev.oneuiproject.oneui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dev.oneuiproject.oneui.design.R;

public class Separator extends TextView {
    private int minHeight;

    public Separator(@NonNull Context context) {
        this(context, null);
    }

    public Separator(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.listSeparatorTextViewStyle);
    }

    public Separator(@NonNull Context context, @Nullable AttributeSet attrs,
                       int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.Widget_AppCompat_Light_TextView_ListSeparator);
    }

    public Separator(@NonNull Context context, @Nullable AttributeSet attrs,
                       int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        minHeight = getResources().getDimensionPixelSize(androidx.appcompat.R.dimen.sesl_list_subheader_min_height);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final CharSequence text = getText();
        final int heightSpecOverride = (text != null && !text.toString().isEmpty())
                ? heightMeasureSpec
                : MeasureSpec.makeMeasureSpec(minHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightSpecOverride);
    }
}
