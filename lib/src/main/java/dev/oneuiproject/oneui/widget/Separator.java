package dev.oneuiproject.oneui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dev.oneuiproject.oneui.design.R;

public class Separator extends TextView {
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
    }

    @Override
    public void dispatchDraw(Canvas c) {
        super.dispatchDraw(c);

        LayoutParams lp = getLayoutParams();
        lp.height = getText().length() != 0 ?
                LayoutParams.WRAP_CONTENT
                : getResources().getDimensionPixelSize(R.dimen.sesl_list_subheader_min_height);
        setLayoutParams(lp);
    }
}
