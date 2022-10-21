package dev.oneuiproject.oneui.view.internal;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import dev.oneuiproject.oneui.design.R;

/**
 * @hide
 */
@RestrictTo(LIBRARY)
public class NavigationBadgeIcon extends Drawable {
    public static final float BADGE_ICON_X = 0.8125f;
    public static final float BADGE_ICON_Y = 0.1458f;
    public static final float Y_DIFF = 4.f;
    public static final float BADGE_ICON_RADIUS = 0.3542f;

    private Context mContext;
    private Paint mCirclePaint;
    private Paint mTextPaint;
    private String mBadgeText = null;

    private boolean mIsLandscape = false;

    public NavigationBadgeIcon(@NonNull Context context) {
        mContext = context;

        mTextPaint = new Paint();
        mTextPaint.setColor(mContext
                .getColor(R.color.oui_n_badge_text_color));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTypeface(Typeface
                .create("sec-roboto-light", Typeface.NORMAL));
        mTextPaint.setTextSize(mContext.getResources()
                .getDimensionPixelSize(R.dimen.oui_n_badge_text_size));
        mTextPaint.setAntiAlias(true);

        mCirclePaint = new Paint();
        mCirclePaint.setColor(mContext
                .getColor(R.color.oui_n_badge_background_color));
        mCirclePaint.setAntiAlias(true);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mBadgeText != null && mBadgeText.length() != 0) {
            final Rect bounds = getBounds();

            final float x = bounds.width() * BADGE_ICON_X;
            final float y = mIsLandscape
                    ? (bounds.height() * BADGE_ICON_Y) - Y_DIFF
                    : -Y_DIFF;
            final float radius = bounds.width() * BADGE_ICON_RADIUS;
            canvas.drawCircle(x, y, radius, mCirclePaint);

            Rect textBounds = new Rect();
            mTextPaint.getTextBounds(mBadgeText, 0, mBadgeText.length(), textBounds);
            canvas.drawText(mBadgeText, x, y + (textBounds.height() / 2.f), mTextPaint);
        }
    }

    public void setOrientation(boolean isLandscape) {
        mIsLandscape = isLandscape;
    }

    public void setText(@Nullable String text) {
        mBadgeText = text;
    }

    @Override
    public void setAlpha(int alpha) {
        mCirclePaint.setAlpha(alpha);
        mTextPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mCirclePaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
