package dev.oneuiproject.oneui.preference;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.RestrictTo;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import dev.oneuiproject.oneui.design.R;

public class LayoutPreference extends Preference {
    private View mRootView;

    private boolean mAllowDividerAbove;
    private boolean mAllowDividerBelow;
    private boolean mIsRelativeLinkView = false;

    private final View.OnClickListener mClickListener = this::performClick;

    public LayoutPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public LayoutPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public LayoutPreference(Context context, int resource) {
        this(context, LayoutInflater.from(context).inflate(resource, null, false));
    }

    public LayoutPreference(Context context, View view) {
        super(context);
        setView(view);
    }

    /**
     * @hide
     */
    @RestrictTo(LIBRARY)
    public LayoutPreference(Context context, View view, boolean isRelativeLinkView) {
        super(context);
        setView(view);
        mIsRelativeLinkView = isRelativeLinkView;
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Preference);
        mAllowDividerAbove = TypedArrayUtils.getBoolean(array,
                R.styleable.Preference_allowDividerAbove,
                R.styleable.Preference_allowDividerAbove,
                false);
        mAllowDividerBelow = TypedArrayUtils.getBoolean(array,
                R.styleable.Preference_allowDividerBelow,
                R.styleable.Preference_allowDividerBelow,
                false);
        array.recycle();

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.Preference, defStyleAttr, 0);
        int layoutResource = a.getResourceId(R.styleable.Preference_android_layout,
                0);
        if (layoutResource == 0) {
            throw new IllegalArgumentException("LayoutPreference requires a layout to be defined");
        }
        a.recycle();
        // Need to create view now so that findViewById can be called immediately.
        final View view = LayoutInflater.from(getContext())
                .inflate(layoutResource, null, false);
        setView(view);
    }

    private void setView(View view) {
        setLayoutResource(R.layout.oui_preference_layout_frame);
        mRootView = view;
        setShouldDisableView(false);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder view) {
        if (mIsRelativeLinkView) {
            view.itemView.setOnClickListener(null);
            view.itemView.setFocusable(false);
            view.itemView.setClickable(false);
        } else {
            view.itemView.setOnClickListener(mClickListener);

            final boolean isSelectable = isSelectable();
            view.itemView.setFocusable(isSelectable);
            view.itemView.setClickable(isSelectable);

            view.setDividerAllowedAbove(mAllowDividerAbove);
            view.setDividerAllowedBelow(mAllowDividerBelow);
        }
        
        FrameLayout layout = (FrameLayout) view.itemView;
        layout.removeAllViews();
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        layout.addView(mRootView);
    }

    public <T extends View> T findViewById(int id) {
        return mRootView.findViewById(id);
    }

    public void setAllowDividerAbove(boolean allowed) {
        mAllowDividerAbove = allowed;
    }

    public void setAllowDividerBelow(boolean allowed) {
        mAllowDividerBelow = allowed;
    }

    /**
     * @hide
     */
    @RestrictTo(LIBRARY)
    public boolean isRelativeLinkView() {
        return mIsRelativeLinkView;
    }
}
