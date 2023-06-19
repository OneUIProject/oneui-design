package dev.oneuiproject.oneui.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import java.util.HashMap;

import dev.oneuiproject.oneui.design.R;
import dev.oneuiproject.oneui.preference.internal.HorizontalRadioViewContainer;

public class HorizontalRadioPreference extends Preference {
    private static final int IMAGE = 0;
    private static final int NO_IMAGE = 1;
    private final int SELECTED_COLOR;
    private final int UNSELECTED_COLOR;

    protected int mType;
    private boolean mIsDividerEnabled = false;
    private boolean mIsColorFilterEnabled = false;
    private boolean mIsTouchEffectEnabled = true;

    private final int paddingStartEnd;
    private final int paddingTop;
    private final int paddingBottom;

    protected ViewGroup mContainerLayout;

    private int[] mEntriesImage;
    private CharSequence[] mEntries;
    private CharSequence[] mEntriesSubTitle;
    private CharSequence[] mEntryValues;
    private String mValue;
    private boolean mValueSet;
    private HashMap<String, Boolean> mIsItemEnabledMap = new HashMap<>();

    public HorizontalRadioPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorPrimaryDark, outValue, true);
        SELECTED_COLOR = outValue.data;
        UNSELECTED_COLOR = getContext().getColor(R.color.oui_horizontalradiopref_text_unselected_color);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HorizontalRadioPreference);

        mType = a.getInt(R.styleable.HorizontalRadioPreference_viewType, IMAGE);
        mEntries = a.getTextArray(R.styleable.HorizontalRadioPreference_entries);
        mEntryValues = a.getTextArray(R.styleable.HorizontalRadioPreference_entryValues);

        if (mType == IMAGE) {
            final int entriesImageResId = a.getResourceId(R.styleable.HorizontalRadioPreference_entriesImage, 0);
            if (entriesImageResId != 0) {
                TypedArray ta = context.getResources().obtainTypedArray(entriesImageResId);
                mEntriesImage = new int[ta.length()];
                for (int i = 0; i < ta.length(); i++) {
                    mEntriesImage[i] = ta.getResourceId(i, 0);
                }
                ta.recycle();
            }
        } else if (mType == NO_IMAGE) {
            mEntriesSubTitle = a.getTextArray(R.styleable.HorizontalRadioPreference_entriesSubtitle);
        }

        a.recycle();

        setSelectable(false);

        paddingStartEnd = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,
                getContext().getResources().getDimension(
                        R.dimen.oui_horizontalradiopref_padding_start_end),
                getContext().getResources().getDisplayMetrics());
        paddingTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,
                getContext().getResources().getDimension(
                        R.dimen.oui_horizontalradiopref_padding_top),
                getContext().getResources().getDisplayMetrics());
        paddingBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,
                getContext().getResources().getDimension(
                        R.dimen.oui_horizontalradiopref_padding_bottom),
                getContext().getResources().getDisplayMetrics());

        setLayoutResource(R.layout.oui_preference_horizontal_radio_layout);
    }

    @Override
    public void onBindViewHolder(final PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        final int itemSize = mEntries.length;
        if (itemSize > 3) {
            throw new IllegalArgumentException("Out of index");
        }

        mContainerLayout = holder.itemView.findViewById(R.id.horizontal_radio_layout);
        ((HorizontalRadioViewContainer) mContainerLayout).setDividerEnabled(mIsDividerEnabled);

        int index = 0;
        for (final CharSequence value : mEntryValues) {
            ViewGroup itemLayout = mContainerLayout.findViewById(getResId(index));

            switch (mType) {
                case NO_IMAGE:
                    ((TextView) itemLayout.findViewById(R.id.title))
                            .setText(mEntries[index]);
                    ((TextView) itemLayout.findViewById(R.id.sub_title))
                            .setText(mEntriesSubTitle[index]);
                    itemLayout.findViewById(R.id.text_frame)
                            .setVisibility(View.VISIBLE);
                    break;
                case IMAGE:
                    ((ImageView) itemLayout.findViewById(R.id.icon))
                            .setImageResource(mEntriesImage[index]);
                    ((TextView) itemLayout.findViewById(R.id.icon_title))
                            .setText(mEntries[index]);
                    itemLayout.findViewById(R.id.image_frame)
                            .setVisibility(View.VISIBLE);
                    break;
            }

            itemLayout.setVisibility(View.VISIBLE);
            if (!mIsTouchEffectEnabled) {
                itemLayout.setBackground(null);
            }
            itemLayout.setOnTouchListener((v, event) -> {
                final int action = event.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        if (!mIsTouchEffectEnabled) {
                            v.setAlpha(0.6f);
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (!mIsTouchEffectEnabled) {
                            v.setAlpha(1.0f);
                        }
                        v.callOnClick();
                        return false;
                    case MotionEvent.ACTION_CANCEL:
                        if (!mIsTouchEffectEnabled) {
                            v.setAlpha(1.0f);
                        }
                        return false;
                }

                return false;
            });
            itemLayout.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_ENTER
                        || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER
                        || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                    final int action = event.getAction();
                    switch (action) {
                        case KeyEvent.ACTION_DOWN:
                            if (!mIsTouchEffectEnabled) {
                                v.setAlpha(0.6f);
                            }
                            return true;
                        case KeyEvent.ACTION_UP:
                            if (!mIsTouchEffectEnabled) {
                                v.setAlpha(1.0f);
                            }
                            v.playSoundEffect(SoundEffectConstants.CLICK);
                            v.callOnClick();
                            return false;
                    }
                }

                return false;
            });
            itemLayout.setOnClickListener(v -> {
                setValue((String) value);
                callChangeListener((String) value);
            });

            int itemPadding = paddingStartEnd;
            if (!mIsDividerEnabled) {
                itemPadding = Math.round(itemPadding / 2.f);
            }

            if (index == 0) {
                itemLayout.setPadding(paddingStartEnd, paddingTop, itemPadding, paddingBottom);
            } else if (index == itemSize - 1) {
                itemLayout.setPadding(itemPadding, paddingTop, paddingStartEnd, paddingBottom);
            } else {
                itemLayout.setPadding(itemPadding, paddingTop, itemPadding, paddingBottom);
            }

            index++;
        }

        invalidate();
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            return superState;
        }

        final SavedState myState = new SavedState(superState);
        myState.value = getValue();
        return myState;
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedString(mValue) : (String) defaultValue);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        setValue(myState.value);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        invalidate();
    }

    public void setViewType(int viewType) {
        mType = viewType;
    }

    public CharSequence getEntry() {
        int index = getValueIndex();
        return index >= 0 && mEntries != null ? mEntries[index] : null;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        final boolean changed = !TextUtils.equals(mValue, value);
        if (changed || !mValueSet) {
            mValue = value;
            mValueSet = true;
            persistString(value);
            if (changed) {
                notifyChanged();
                invalidate();
            }
        }
    }

    private int getValueIndex() {
        return findIndexOfValue(mValue);
    }

    public void setEntryEnabled(String entry, boolean enabled) {
        mIsItemEnabledMap.put(entry, Boolean.valueOf(enabled));
        invalidate();
    }

    public void setDividerEnabled(boolean enabled) {
        mIsDividerEnabled = enabled;
    }

    public void setColorFilterEnabled(boolean enabled) {
        mIsColorFilterEnabled = enabled;
    }

    public void setTouchEffectEnabled(boolean enabled) {
        mIsTouchEffectEnabled = enabled;
    }

    public int findIndexOfValue(String value) {
        if (value != null && mEntryValues != null) {
            for (int i = mEntryValues.length - 1; i >= 0; i--) {
                if (mEntryValues[i].equals(value)) {
                    return i;
                }
            }
        }
        return -1;
    }

    // kang from com.samsung.android.settings
    private void invalidate() {
        int var3 = 0;
        TextView var4 = null;
        ImageView var6 = null;
        TextView var7 = null;

        for(TextView var5 = var4; var3 < mEntryValues.length; var7 = var4) {
            if (mContainerLayout == null) break;

            String var8 = (String) mEntryValues[var3];
            ViewGroup var9 = mContainerLayout.findViewById(this.getResId(var3));
            RadioButton var10 = var9.findViewById(R.id.radio_button);
            int var11 = this.mType;
            boolean var12 = true;
            if (var11 == 1) {
                var5 = var9.findViewById(R.id.title);
                var4 = var9.findViewById(R.id.sub_title);
                var9.findViewById(R.id.text_frame).setVisibility(View.VISIBLE);
            } else {
                var4 = var7;
                if (var11 == 0) {
                    var6 = var9.findViewById(R.id.icon);
                    var5 = var9.findViewById(R.id.icon_title);
                    var9.findViewById(R.id.image_frame).setVisibility(View.VISIBLE);
                    var4 = var7;
                }
            }

            boolean var14 = TextUtils.equals(var8, mValue);
            var10.setChecked(var14);
            if (!this.mIsTouchEffectEnabled) {
                var10.jumpDrawablesToCurrentState();
            }

            var5.setSelected(var14);
            var5.setTypeface(Typeface.create("sec-roboto-light", var14 ? 1 : 0));
            if (var4 != null) {
                var4.setSelected(var14);
                var4.setTypeface(Typeface.create("sec-roboto-light", var14 ? 1 : 0));
            }

            if (this.mIsColorFilterEnabled && var6 != null) {
                if (var14) {
                    var11 = this.SELECTED_COLOR;
                } else {
                    var11 = this.UNSELECTED_COLOR;
                }

                var6.setColorFilter(var11);
            }

            if (this.mIsItemEnabledMap.get(var8) == Boolean.FALSE || !isEnabled()) {
                var12 = false;
            }

            var9.setEnabled(var12);
            float var13;
            if (var12) {
                var13 = 1.0F;
            } else {
                var13 = 0.6F;
            }

            var9.setAlpha(var13);
            ++var3;
        }
    }
    // kang from com.samsung.android.settings

    private int getResId(int index) {
        switch (index) {
            case 0:
                return R.id.item1;
            case 1:
                return R.id.item2;
            case 2:
                return R.id.item3;
        }

        throw new IllegalArgumentException("Out of index");
    }

    private static class SavedState extends BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    @Override
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
        String value;

        public SavedState(Parcel source) {
            super(source);
            value = source.readString();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(value);
        }
    }
}
