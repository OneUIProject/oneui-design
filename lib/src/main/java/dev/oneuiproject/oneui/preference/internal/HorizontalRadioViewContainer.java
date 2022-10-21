package dev.oneuiproject.oneui.preference.internal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;

import dev.oneuiproject.oneui.design.R;

public class HorizontalRadioViewContainer extends LinearLayout {
    private Boolean mIsDividerEnabled;

    public HorizontalRadioViewContainer(Context context) {
        super(context);
    }

    public HorizontalRadioViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalRadioViewContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (mIsDividerEnabled) {
            Drawable divider = getContext().getDrawable(R.drawable.oui_divider_vertical);

            int marginTop = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    24.f, getContext().getResources().getDisplayMetrics()));
            int height = (getHeight() - marginTop) - Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    61.f, getContext().getResources().getDisplayMetrics()));
            int width = Math.round(getContext().getResources().getDimension(R.dimen.sesl_list_divider_height));

            for (int i = 0; i < getChildCount() - 1; i++) {
                divider.setBounds(0, 0, width, height);
                canvas.save();
                canvas.translate((float) Math.round((((float) getWidth()) / ((float) getChildCount())) * ((float) i + 1)), (float) marginTop);
                divider.draw(canvas);
                canvas.restore();
            }
        }
    }

    public void setDividerEnabled(boolean enabled) {
        mIsDividerEnabled = enabled;
    }
}
