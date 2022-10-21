package dev.oneuiproject.oneui.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.util.SeslRoundedCorner;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import dev.oneuiproject.oneui.design.R;

public class DescriptionPreference extends Preference {
    private static final int POSITION_NORMAL = 0;
    private static final int POSITION_FIRST_ITEM = 1;
    private static final int POSITION_SUBHEADER = 2;

    private Context mContext;
    private int mPositionMode = POSITION_NORMAL;

    public DescriptionPreference(@NonNull Context context, @Nullable AttributeSet attrs,
                                 int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        setSelectable(false);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Preference);
            setLayoutResource(a.getResourceId(R.styleable.Preference_android_layout,
                    R.layout.oui_preference_unclickable_layout));
            a.recycle();

            a = context.obtainStyledAttributes(attrs, R.styleable.DescriptionPreference);
            seslSetSubheaderRoundedBackground(a.getInt(R.styleable.DescriptionPreference_roundedCorners,
                    SeslRoundedCorner.ROUNDED_CORNER_ALL));
            mPositionMode = a.getInt(R.styleable.DescriptionPreference_positionMode,
                    POSITION_NORMAL);
            a.recycle();
        } else {
            setLayoutResource(R.layout.oui_preference_unclickable_layout);
            seslSetSubheaderRoundedBackground(SeslRoundedCorner.ROUNDED_CORNER_ALL);
        }
    }

    public DescriptionPreference(@NonNull Context context, @Nullable AttributeSet attrs,
                                 int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DescriptionPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DescriptionPreference(@NonNull Context context) {
        this(context, null);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        TextView titleTextView = (TextView) holder.findViewById(R.id.title);
        titleTextView.setText(getTitle());
        titleTextView.setVisibility(View.VISIBLE);

        if (mContext != null) {
            LinearLayout.LayoutParams lp
                    = (LinearLayout.LayoutParams) titleTextView.getLayoutParams();

            final int top;
            final int bottom;
            switch (mPositionMode) {
                case POSITION_FIRST_ITEM:
                    top = mContext.getResources().getDimensionPixelSize(
                            R.dimen.oui_unclickablepref_first_margin_top);
                    bottom = mContext.getResources().getDimensionPixelSize(
                            R.dimen.oui_unclickablepref_first_margin_bottom);
                    break;
                case POSITION_SUBHEADER:
                    top = mContext.getResources().getDimensionPixelSize(
                            R.dimen.oui_unclickablepref_subheader_margin_top);
                    bottom = mContext.getResources().getDimensionPixelSize(
                            R.dimen.oui_unclickablepref_subheader_margin_bottom);
                    break;
                case POSITION_NORMAL:
                default:
                    top = mContext.getResources().getDimensionPixelSize(
                            R.dimen.oui_unclickablepref_margin_top);
                    bottom = mContext.getResources().getDimensionPixelSize(
                            R.dimen.oui_unclickablepref_margin_bottom);
                    break;
            }

            final int horizontal = mContext.getResources().getDimensionPixelSize(
                    R.dimen.oui_unclickablepref_text_padding_start_end);

            lp.setMargins(horizontal, top, horizontal, bottom);
            titleTextView.setLayoutParams(lp);
        }

        holder.setDividerAllowedAbove(false);
        holder.setDividerAllowedBelow(false);
    }

    public void setPositionMode(int mode) {
        mPositionMode = mode;
    }
}
