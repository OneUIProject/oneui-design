package dev.oneuiproject.oneui.preference;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.picker3.app.SeslColorPickerDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.internal.PreferenceImageView;

import java.util.ArrayList;
import java.util.Collections;

import dev.oneuiproject.oneui.design.R;

public class ColorPickerPreference extends Preference implements Preference.OnPreferenceClickListener,
        SeslColorPickerDialog.OnColorSetListener {
    private Dialog mDialog;
    private PreferenceImageView mPreview;

    private int mValue = Color.BLACK;
    private final ArrayList<Integer> mUsedColors = new ArrayList<>();
    private long mLastClickTime;
    private boolean mAlphaSliderEnabled = false;

    public ColorPickerPreference(Context context) {
        this(context, null);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public static int convertToColorInt(String argb) throws IllegalArgumentException {
        if (!argb.startsWith("#")) {
            argb = "#" + argb;
        }

        return Color.parseColor(argb);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        int colorInt;
        String mHexDefaultValue = a.getString(index);
        if (mHexDefaultValue != null && mHexDefaultValue.startsWith("#")) {
            colorInt = convertToColorInt(mHexDefaultValue);
            return colorInt;
        } else {
            return a.getColor(index, Color.BLACK);
        }
    }

    @Override
    protected void onSetInitialValue(Object defaultValue) {
        onColorSet(defaultValue == null
                ? getPersistedInt(mValue)
                : (Integer) defaultValue);
    }

    private void init(Context context, AttributeSet attrs) {
        setWidgetLayoutResource(R.layout.oui_preference_color_picker_widget);

        setOnPreferenceClickListener(this);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorPickerPreference);
        mAlphaSliderEnabled = a.getBoolean(R.styleable.ColorPickerPreference_showAlphaSlider, false);
        a.recycle();
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        mPreview = (PreferenceImageView) holder.findViewById(R.id.imageview_widget);
        setPreviewColor();
    }

    private void setPreviewColor() {
        if (mPreview == null) {
            return;
        }

        GradientDrawable drawable = (GradientDrawable) getContext()
                .getDrawable(R.drawable.oui_preference_color_picker_preview).mutate();
        drawable.setColor(mValue);

        mPreview.setBackground(drawable);
    }

    @Override
    public void onColorSet(int color) {
        if (isPersistent()) {
            persistInt(color);
        }
        mValue = color;

        callChangeListener(color);
        addRecentColor(color);
        setPreviewColor();
    }

    @Override
    public boolean onPreferenceClick(@NonNull Preference preference) {
        long uptimeMillis = SystemClock.uptimeMillis();
        if (uptimeMillis - mLastClickTime > 600L) {
            showDialog(null);
        }
        mLastClickTime = uptimeMillis;
        return false;
    }

    private void showDialog(Bundle state) {
        SeslColorPickerDialog dialog = new SeslColorPickerDialog(
                getContext(), this, mValue, getRecentColors(), mAlphaSliderEnabled);
        dialog.setNewColor(mValue);
        dialog.setTransparencyControlEnabled(mAlphaSliderEnabled);
        if (state != null) {
            dialog.onRestoreInstanceState(state);
        }
        dialog.show();

        mDialog = dialog;
    }

    public void setAlphaSliderEnabled(boolean enable) {
        mAlphaSliderEnabled = enable;
    }

    private void addRecentColor(int color) {
        for (int i = 0; i < mUsedColors.size(); i++) {
            if (mUsedColors.get(i) == color)
                mUsedColors.remove(i);
        }

        if (mUsedColors.size() > 5) {
            mUsedColors.remove(0);
        }

        mUsedColors.add(color);
    }

    private int[] getRecentColors() {
        int[] usedColors = new int[mUsedColors.size()];
        ArrayList<Integer> reverseUsedColor = new ArrayList<>(mUsedColors);

        Collections.reverse(reverseUsedColor);

        for (int i = 0; i < reverseUsedColor.size(); i++) {
            usedColors[i] = reverseUsedColor.get(i);
        }
        return usedColors;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (mDialog == null || !mDialog.isShowing()) {
            return superState;
        }

        final SavedState myState = new SavedState(superState);
        myState.dialogBundle = mDialog.onSaveInstanceState();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            SavedState myState = (SavedState) state;
            super.onRestoreInstanceState(myState.getSuperState());
            showDialog(myState.dialogBundle);
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    private static class SavedState extends BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
        Bundle dialogBundle;

        public SavedState(Parcel source) {
            super(source);
            dialogBundle = source.readBundle();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeBundle(dialogBundle);
        }
    }
}
