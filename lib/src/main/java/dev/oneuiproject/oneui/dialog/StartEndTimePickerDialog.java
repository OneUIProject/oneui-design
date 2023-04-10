package dev.oneuiproject.oneui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.arch.core.util.Function;
import androidx.picker.widget.SeslTimePicker;

import dev.oneuiproject.oneui.design.R;
import dev.oneuiproject.oneui.dialog.internal.StartEndTabLayout;
import dev.oneuiproject.oneui.dialog.internal.StartEndTimePickerUtils;


public class StartEndTimePickerDialog extends AlertDialog implements SeslTimePicker.OnTimeChangedListener, SeslTimePicker.OnEditTextModeChangedListener {
    private final Context mContext;
    private boolean mIs24HourFormat;
    private StartEndTabLayout mTabLayout;
    private SeslTimePicker mTimePicker;
    private TimePickerChangeListener mTimePickerChangeListener;
    private View mTimePickerDialog;

    public interface TimePickerChangeListener {
        void onTimeSet(int startTime, int endTime);
    }

    @Override
    public void onEditTextModeChanged(SeslTimePicker view, boolean isEditTextMode) {
    }

    public StartEndTimePickerDialog(Context context, int startTime, int endTime, boolean is24HourView, TimePickerChangeListener listener) {
        super(context);
        this.mTimePickerDialog = null;
        this.mContext = context;
        this.mIs24HourFormat = is24HourView;
        this.mTimePickerChangeListener = listener;
        init();
        this.mTabLayout.init(startTime, endTime, new StartEndTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int index, int time) {
                mTimePicker.setEditTextMode(false);
                updatePicker(time);
            }
        }, new Function() {
            public Object apply(Object obj) {
                String timeFormatter;
                timeFormatter = StartEndTimePickerDialog.this.timeFormatter((Integer) obj);
                return timeFormatter;
            }
        });
        this.mTabLayout.select(0);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            onUpdateHourFormat();
            View view = this.mTimePickerDialog;
            if (view != null) {
                view.requestLayout();
            }
        }
    }

    private void init() {
        initMainView();
        initDialogButton();
        onUpdateHourFormat();
    }

    private void initMainView() {
        View inflate = LayoutInflater.from(this.mContext).inflate(R.layout.oui_dialog_start_end_time_picker, (ViewGroup) null);
        this.mTimePickerDialog = inflate;
        setView(inflate);
        SeslTimePicker seslTimePicker = (SeslTimePicker) this.mTimePickerDialog.findViewById(R.id.sec_dark_mode_time_picker);
        this.mTimePicker = seslTimePicker;
        seslTimePicker.setOnEditTextModeChangedListener(this);
        this.mTimePicker.setOnTimeChangedListener(this);
        this.mTabLayout = (StartEndTabLayout) this.mTimePickerDialog.findViewById(R.id.sec_dark_mode_time_picker_tab);
    }

    private void initDialogButton() {
        setButton(BUTTON_POSITIVE, this.mContext.getResources().getString(R.string.oui_common_done), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mTimePicker.clearFocus();
                int[] times = mTabLayout.getTimes();
                int startTime = times[0];
                int endTime = times[1];
                if (mTimePickerChangeListener != null) {
                    mTimePickerChangeListener.onTimeSet(startTime, endTime);
                }
            }
        });
        setButton(BUTTON_NEGATIVE, this.mContext.getResources().getString(R.string.oui_common_cancel), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mTimePicker.clearFocus();
            }
        });
    }


    @Override
    public void onTimeChanged(SeslTimePicker view, int hourOfDay, int minute) {
        this.mTabLayout.updateTime(getTimeInt(hourOfDay, minute));
    }

    private static int getTimeInt(int hourOfDay, int minute) {
        return (hourOfDay * 60) + minute;
    }

    private void updatePicker(int time) {
        this.mTimePicker.setHour(time / 60);
        this.mTimePicker.setMinute(time % 60);
    }

    private void onUpdateHourFormat() {
        this.mTimePicker.setIs24HourView(this.mIs24HourFormat);
        this.mTabLayout.reload();
    }

    public String timeFormatter(int i) {
        return StartEndTimePickerUtils.getTimeText(getContext(), StartEndTimePickerUtils.getCustomCalendarInstance(i / 60, i % 60, this.mIs24HourFormat), this.mIs24HourFormat);
    }
}
