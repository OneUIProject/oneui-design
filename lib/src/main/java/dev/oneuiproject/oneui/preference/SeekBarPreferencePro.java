package dev.oneuiproject.oneui.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SeslSeekBar;
import androidx.appcompat.widget.SeslSeekBar.OnSeekBarChangeListener;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import java.lang.ref.WeakReference;

import dev.oneuiproject.oneui.design.R;
import dev.oneuiproject.oneui.utils.SeekBarUtils;
import dev.oneuiproject.oneui.widget.HapticSeekBar;

public class SeekBarPreferencePro extends Preference
        implements View.OnLongClickListener, View.OnTouchListener {
    private static final String TAG = "SeekBarPreferencePro";
    @SuppressWarnings("WeakerAccess")
    int mSeekBarValue;
    @SuppressWarnings("WeakerAccess")
    int mMin;
    private int mMax;
    private int mOverlapPoint;
    private int mSeekBarIncrement;
    @SuppressWarnings("WeakerAccess")
    boolean mTrackingTouch;
    private AppCompatImageView mAddButton;
    private AppCompatImageView mDeleteButton;
    private boolean mIsLongKeyProcessing = false;
    private final Handler mLongPressHandler = new LongPressHandler(this);
    @SuppressWarnings("WeakerAccess")
    HapticSeekBar mSeekBar;
    private int mSeekBarMode;
    private boolean mSeekBarSeamless;
    private boolean mShowTickMark;
    private TextView mSeekBarValueTextView;
    @SuppressWarnings("WeakerAccess")
    boolean mAdjustable;
    private boolean mShowSeekBarValue;
    private String mUnits;
    @SuppressWarnings("WeakerAccess")
    boolean mUpdatesContinuously;

    private final OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeslSeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser && (mUpdatesContinuously || !mTrackingTouch)) {
                syncValueInternal(seekBar);
            } else {
                updateLabelValue(progress + mMin);
            }
        }

        @Override
        public void onStartTrackingTouch(SeslSeekBar seekBar) {
            mTrackingTouch = true;
        }

        @Override
        public void onStopTrackingTouch(SeslSeekBar seekBar) {
            mTrackingTouch = false;
            if (seekBar.getProgress() + mMin != mSeekBarValue) {
                syncValueInternal(seekBar);
            }
        }
    };

    private final View.OnKeyListener mSeekBarKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() != KeyEvent.ACTION_DOWN) {
                return false;
            }

            if (!mAdjustable && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                    || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
                return false;
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                return false;
            }

            if (mSeekBar == null) {
                Log.e(TAG, "SeekBar view is null and hence cannot be adjusted.");
                return false;
            }
            return mSeekBar.onKeyDown(keyCode, event);
        }
    };

    public SeekBarPreferencePro(@NonNull Context context, @Nullable AttributeSet attrs,
                                int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setLayoutResource(R.layout.oui_preference_seekbar_pro_layout);

        TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.SeekBarPreferencePro, defStyleAttr, defStyleRes);

        mMin = a.getInt(R.styleable.SeekBarPreferencePro_min, 0);
        setMax(a.getInt(R.styleable.SeekBarPreferencePro_android_max, 100));
        mOverlapPoint = a.getInt(R.styleable.SeekBarPreferencePro_overlap, -1);
        setSeekBarIncrement(a.getInt(R.styleable.SeekBarPreferencePro_seekBarIncrement, 1));
        mAdjustable = a.getBoolean(R.styleable.SeekBarPreferencePro_adjustable, false);
        mShowSeekBarValue = a.getBoolean(R.styleable.SeekBarPreferencePro_showSeekBarValue, true);
        mSeekBarMode = a.getInt(R.styleable.SeekBarPreferencePro_seekBarMode, 0);
        mSeekBarSeamless = a.getBoolean(R.styleable.SeekBarPreferencePro_seekBarSeamless, false);
        mShowTickMark = a.getBoolean(R.styleable.SeekBarPreferencePro_showTickMark, false);

        mUnits = a.getString(R.styleable.SeekBarPreferencePro_units);
        if (mUnits == null) {
            mUnits = "";
        }

        mUpdatesContinuously = a.getBoolean(R.styleable.SeekBarPreferencePro_updatesContinuously, false);
        a.recycle();
    }

    public SeekBarPreferencePro(@NonNull Context context, @Nullable AttributeSet attrs,
                                int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SeekBarPreferencePro(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, androidx.preference.R.attr.seekBarPreferenceStyle);
    }

    public SeekBarPreferencePro(@NonNull Context context) {
        this(context, null);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.itemView.setOnKeyListener(mSeekBarKeyListener);
        mSeekBar = (HapticSeekBar) holder.findViewById(R.id.seekbar);
        mSeekBarValueTextView = (TextView) holder.findViewById(R.id.seekbar_value);
        if (mShowSeekBarValue) {
            mSeekBarValueTextView.setVisibility(View.VISIBLE);
        } else {
            mSeekBarValueTextView.setVisibility(View.GONE);
            mSeekBarValueTextView = null;
        }

        if (mSeekBar == null) {
            Log.e(TAG, "SeekBar view is null in onBindViewHolder.");
            return;
        }
        mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mSeekBar.setMode(mSeekBarMode);
        mSeekBar.setMax(mMax - mMin);
        mSeekBar.setOverlapPointForDualColor(mOverlapPoint);
        mSeekBar.setSeamless(mSeekBarSeamless);
        if (mShowTickMark) {
            SeekBarUtils.showTickMark(mSeekBar, true);
        }

        if (mSeekBarIncrement != 0) {
            mSeekBar.setKeyProgressIncrement(mSeekBarIncrement);
        } else {
            mSeekBarIncrement = mSeekBar.getKeyProgressIncrement();
        }

        mSeekBar.setProgress(mSeekBarValue - mMin);
        updateLabelValue(mSeekBarValue);
        mSeekBar.setEnabled(isEnabled());

        mAddButton = (AppCompatImageView) holder.findViewById(R.id.add_button);
        mDeleteButton = (AppCompatImageView) holder.findViewById(R.id.delete_button);
        if (mAdjustable) {
            mAddButton.setVisibility(View.VISIBLE);
            mDeleteButton.setVisibility(View.VISIBLE);
            mAddButton.setOnLongClickListener(this);
            mDeleteButton.setOnLongClickListener(this);
            mAddButton.setOnTouchListener(this);
            mDeleteButton.setOnTouchListener(this);
            mAddButton.setEnabled(isEnabled());
            mDeleteButton.setEnabled(isEnabled());
        }
    }

    @Override
    protected void onSetInitialValue(@Nullable Object defaultValue) {
        if (defaultValue == null) {
            defaultValue = 0;
        }
        setValue(getPersistedInt((Integer) defaultValue));
    }

    @Override
    protected @Nullable Object onGetDefaultValue(@NonNull TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    public int getMin() {
        return mMin;
    }

    public void setMin(int min) {
        if (min > mMax) {
            min = mMax;
        }
        if (min != mMin) {
            mMin = min;
            notifyChanged();
        }
    }

    public final int getSeekBarIncrement() {
        return mSeekBarIncrement;
    }

    public final void setSeekBarIncrement(int seekBarIncrement) {
        if (seekBarIncrement != mSeekBarIncrement) {
            mSeekBarIncrement = Math.min(mMax - mMin, Math.abs(seekBarIncrement));
            notifyChanged();
        }
    }

    public int getMax() {
        return mMax;
    }

    public final void setMax(int max) {
        if (max < mMin) {
            max = mMin;
        }
        if (max != mMax) {
            mMax = max;
            notifyChanged();
        }
    }

    public boolean isAdjustable() {
        return mAdjustable;
    }

    public void setAdjustable(boolean adjustable) {
        mAdjustable = adjustable;
    }

    public boolean getUpdatesContinuously() {
        return mUpdatesContinuously;
    }

    public void setUpdatesContinuously(boolean updatesContinuously) {
        mUpdatesContinuously = updatesContinuously;
    }

    public boolean getShowSeekBarValue() {
        return mShowSeekBarValue;
    }

    public void setShowSeekBarValue(boolean showSeekBarValue) {
        mShowSeekBarValue = showSeekBarValue;
        notifyChanged();
    }

    private void setValueInternal(int seekBarValue, boolean notifyChanged) {
        if (seekBarValue < mMin) {
            seekBarValue = mMin;
        }
        if (seekBarValue > mMax) {
            seekBarValue = mMax;
        }

        if (seekBarValue != mSeekBarValue) {
            mSeekBarValue = seekBarValue;
            updateLabelValue(mSeekBarValue);
            persistInt(seekBarValue);
            if (notifyChanged) {
                notifyChanged();
            }
        }
    }

    public int getValue() {
        return mSeekBarValue;
    }

    public void setValue(int seekBarValue) {
        setValueInternal(seekBarValue, true);
    }

    @SuppressWarnings("WeakerAccess")
    void syncValueInternal(@NonNull SeslSeekBar seekBar) {
        int seekBarValue = mMin + seekBar.getProgress();
        if (seekBarValue != mSeekBarValue) {
            if (callChangeListener(seekBarValue)) {
                setValueInternal(seekBarValue, false);
            } else {
                seekBar.setProgress(mSeekBarValue - mMin);
                updateLabelValue(mSeekBarValue);
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    void updateLabelValue(int value) {
        if (mSeekBarValueTextView != null) {
            mSeekBarValueTextView.setText(mUnits.isEmpty() ? String.valueOf(value) : value + mUnits);
        }
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            return superState;
        }

        final SavedState myState = new SavedState(superState);
        myState.mSeekBarValue = mSeekBarValue;
        myState.mMin = mMin;
        myState.mMax = mMax;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(@Nullable Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        mSeekBarValue = myState.mSeekBarValue;
        mMin = myState.mMin;
        mMax = myState.mMax;
        notifyChanged();
    }

    @Override
    public boolean onLongClick(View view) {
        mIsLongKeyProcessing = true;

        if (view.getId() != R.id.delete_button && view.getId() != R.id.add_button) {
            return false;
        }

        new Thread(() -> {
            while (mIsLongKeyProcessing) {
                mLongPressHandler.sendEmptyMessage(view.getId() == R.id.delete_button ? 1 : 2);
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    Log.w(SeekBarPreferencePro.class.getSimpleName(), "InterruptedException!", e);
                }
            }
        }).start();

        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        final int action = motionEvent.getAction();

        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!mIsLongKeyProcessing) {
                    mLongPressHandler.sendEmptyMessage(view.getId() == R.id.delete_button
                            ? 1
                            : 2);
                    view.playSoundEffect(SoundEffectConstants.CLICK);
                } else {
                    mIsLongKeyProcessing = false;
                    mLongPressHandler.removeMessages(1);
                    mLongPressHandler.removeMessages(2);
                }
                break;
        }

        return false;
    }

    private void onAddButtonClicked() {
        int value = getValue() + mSeekBarIncrement;

        if (value > getMax()) {
            value = getMax();
        }

        if (value != getValue() && callChangeListener(value)) {
            setValue(value);
            if (mIsLongKeyProcessing) {
                mAddButton.playSoundEffect(SoundEffectConstants.CLICK);
            }
        }
    }

    private void onDeleteButtonClicked() {
        int value = getValue() - mSeekBarIncrement;

        if (value < getMin()) {
            value = getMin();
        }

        if (value != getValue() && callChangeListener(value)) {
            setValue(value);
            if (mIsLongKeyProcessing) {
                mDeleteButton.playSoundEffect(SoundEffectConstants.CLICK);
            }
        }
    }

    private static class LongPressHandler extends Handler {
        private final WeakReference<SeekBarPreferencePro> weakReference;

        LongPressHandler(SeekBarPreferencePro seekBarPref) {
            super(Looper.getMainLooper());
            weakReference = new WeakReference<>(seekBarPref);
        }

        @Override
        public void handleMessage(Message msg) {
            SeekBarPreferencePro seekBarPref = weakReference.get();
            if (msg.what == 1) {
                seekBarPref.onDeleteButtonClicked();
            } else if (msg.what == 2) {
                seekBarPref.onAddButtonClicked();
            }
        }
    }

    private static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    @Override
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };

        int mSeekBarValue;
        int mMin;
        int mMax;

        SavedState(Parcel source) {
            super(source);
            mSeekBarValue = source.readInt();
            mMin = source.readInt();
            mMax = source.readInt();
        }

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mSeekBarValue);
            dest.writeInt(mMin);
            dest.writeInt(mMax);
        }
    }
}
