package dev.oneuiproject.oneui.preference;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.SeslSwitchBar;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.TwoStatePreference;

import dev.oneuiproject.oneui.design.R;

public class SwitchBarPreference extends TwoStatePreference {

    public SwitchBarPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setLayoutResource(R.layout.oui_preference_switch_bar_layout);
    }

    public SwitchBarPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SwitchBarPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchBarPreference(Context context) {
        this(context, null);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        SeslSwitchBar switchBar = (SeslSwitchBar) holder.itemView;
        switchBar.setChecked(mChecked);
        switchBar.addOnSwitchChangeListener((switchView, isChecked) -> {
            if (isChecked == mChecked) return;
            if (!callChangeListener(isChecked)) {
                switchBar.setChecked(!isChecked);
                return;
            }
            setChecked(isChecked);
        });
        holder.setDividerAllowedAbove(false);
        holder.setDividerAllowedBelow(false);
    }

}
