package dev.oneuiproject.oneuiexample.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.res.ResourcesCompat;
import androidx.picker.widget.SeslDatePicker;
import androidx.picker.widget.SeslNumberPicker;
import androidx.picker.widget.SeslTimePicker;
import androidx.picker3.app.SeslColorPickerDialog;

import com.sec.sesl.tester.R;
import dev.oneuiproject.oneuiexample.base.BaseFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PickersFragment extends BaseFragment
        implements AdapterView.OnItemSelectedListener, SeslColorPickerDialog.OnColorSetListener {
    private int mCurrentColor;
    private List<Integer> mRecentColors = new ArrayList<>();

    private int mCurrentPos = 0;

    private LinearLayout mNumberPickers;
    private SeslTimePicker mTimePicker;
    private SeslDatePicker mDatePicker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentColor = -16547330; // #0381fe
        mRecentColors.add(mCurrentColor);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initNumberPicker(view);
        initTimePicker(view);
        initDatePicker(view);
        initSpinner(view);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.sample3_fragment_pickers;
    }

    @Override
    public int getIconResId() {
        return R.drawable.drawer_page_icon_pickers;
    }

    @Override
    public CharSequence getTitle() {
        return "Pickers";
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                mNumberPickers.setVisibility(View.VISIBLE);
                mTimePicker.setVisibility(View.GONE);
                mDatePicker.setVisibility(View.GONE);
                break;
            case 1:
                mNumberPickers.setVisibility(View.GONE);
                mTimePicker.setVisibility(View.VISIBLE);
                mTimePicker.startAnimation(200, null);
                mDatePicker.setVisibility(View.GONE);
                break;
            case 2:
                mNumberPickers.setVisibility(View.GONE);
                mTimePicker.setVisibility(View.GONE);
                mDatePicker.setVisibility(View.VISIBLE);
                break;
        }

        mCurrentPos = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    private void initNumberPicker(@NonNull View view) {
        mNumberPickers = view.findViewById(R.id.pickers_number);

        SeslNumberPicker numberPickerThree = mNumberPickers.findViewById(R.id.picker_number_3);
        numberPickerThree.setTextTypeface(ResourcesCompat.getFont(mContext, R.font.samsungsharpsans_bold));
        numberPickerThree.setMinValue(0);
        numberPickerThree.setMaxValue(2);
        numberPickerThree.setTextSize(40f);
        numberPickerThree.setDisplayedValues(new String[]{"A", "B", "C"});
        EditText et3 = numberPickerThree.getEditText();
        et3.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                numberPickerThree.setEditTextMode(false);
            }
            return false;
        });

        SeslNumberPicker numberPickerTwo = mNumberPickers.findViewById(R.id.picker_number_2);
        numberPickerTwo.setTextTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        numberPickerTwo.setMinValue(0);
        numberPickerTwo.setMaxValue(10);
        numberPickerTwo.setValue(8);
        numberPickerTwo.setTextSize(50f);
        EditText et2 = numberPickerTwo.getEditText();
        et2.setImeOptions(EditorInfo.IME_FLAG_NO_FULLSCREEN | EditorInfo.IME_ACTION_NEXT);
        et2.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                numberPickerTwo.setEditTextMode(false);
                numberPickerThree.setEditTextMode(true);
                numberPickerThree.requestFocus();
            }
            return false;
        });

        SeslNumberPicker numberPickerOne = mNumberPickers.findViewById(R.id.picker_number_1);
        numberPickerOne.setMinValue(1);
        numberPickerOne.setMaxValue(100);
        numberPickerOne.setValue(50);
        numberPickerOne.setTextSize(40f);
        EditText et1 = numberPickerOne.getEditText();
        et1.setImeOptions(EditorInfo.IME_FLAG_NO_FULLSCREEN | EditorInfo.IME_ACTION_NEXT);
        et1.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                numberPickerOne.setEditTextMode(false);
                numberPickerTwo.setEditTextMode(true);
                numberPickerTwo.requestFocus();
            }
            return false;
        });
    }

    private void initTimePicker(@NonNull View view) {
        mTimePicker = view.findViewById(R.id.picker_time);
        mTimePicker.setIs24HourView(DateFormat.is24HourFormat(mContext));
    }

    private void initDatePicker(@NonNull View view) {
        mDatePicker = view.findViewById(R.id.picker_date);

        Calendar calendar = Calendar.getInstance();
        mDatePicker.init(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), null);
    }

    private void initSpinner(@NonNull View view) {
        AppCompatSpinner spinner = view.findViewById(R.id.pickers_spinner);

        List<String> categories = new ArrayList<>();
        categories.add("NumberPicker");
        categories.add("TimePicker");
        categories.add("DatePicker");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    private void openColorPickerDialog() {
        SeslColorPickerDialog dialog = new SeslColorPickerDialog(mContext, this,
                mCurrentColor, buildIntArray(mRecentColors), true);
        dialog.setTransparencyControlEnabled(true);
        dialog.show();
    }

    @Override
    public void onColorSet(int color) {
        mCurrentColor = color;
        if (mRecentColors.size() == 6) {
            mRecentColors.remove(5);
        }
        mRecentColors.add(0, color);
    }

    private int[] buildIntArray(List<Integer> integers) {
        int[] ints = new int[integers.size()];
        int i = 0;
        for (Integer n : integers) {
            ints[i++] = n;
        }
        return ints;
    }
}
