package dev.oneuiproject.oneuiexample.fragment;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.DropDownPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeslSwitchPreferenceScreen;

import com.sec.sesl.tester.R;

import dev.oneuiproject.oneui.preference.internal.PreferenceRelatedCard;
import dev.oneuiproject.oneui.utils.PreferenceUtils;
import dev.oneuiproject.oneuiexample.base.FragmentInfo;

public class PreferencesFragment extends PreferenceFragmentCompat
        implements FragmentInfo, Preference.OnPreferenceClickListener,
        Preference.OnPreferenceChangeListener {
    private Context mContext;
    private PreferenceRelatedCard mRelativeLinkCard;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.sample3_preferences, rootKey);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initPreferences();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getView().setBackgroundColor(mContext.getColor(R.color.oui_background_color));
        getListView().seslSetLastRoundedCorner(false);
    }

    @Override
    public void onResume() {
        setRelativeLinkCard();
        super.onResume();
    }

    @Override
    public int getLayoutResId() {
        return -1;
    }

    @Override
    public int getIconResId() {
        return R.drawable.ic_oui_settings_outline;
    }

    @Override
    public CharSequence getTitle() {
        return "Preferences";
    }

    private void initPreferences() {
        SeslSwitchPreferenceScreen key2 = findPreference("key2");
        boolean enabled = key2.isChecked();
        key2.setSummary(enabled ? "Enabled" : "Disabled");
        key2.seslSetSummaryColor(getColoredSummaryColor(enabled));
        key2.setOnPreferenceClickListener(this);
        key2.setOnPreferenceChangeListener(this);

        EditTextPreference key4 = findPreference("key4");
        key4.seslSetSummaryColor(getColoredSummaryColor(key4.getText().length() > 0));
        key4.setOnPreferenceChangeListener(this);

        DropDownPreference key5 = findPreference("key5");
        key5.seslSetSummaryColor(getColoredSummaryColor(true));
        ListPreference key6 = findPreference("key6");
        key6.seslSetSummaryColor(getColoredSummaryColor(true));
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals("key2")) {
            Toast.makeText(mContext, "onPreferenceClick", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case "key2":
                Boolean enabled = (Boolean) newValue;
                preference.setSummary(enabled ? "Enabled" : "Disabled");
                preference.seslSetSummaryColor(getColoredSummaryColor(enabled));
                return true;
            case "key4":
                String text = (String) newValue;
                preference.seslSetSummaryColor(getColoredSummaryColor(text.length() > 0));
                return true;
        }

        return false;
    }

    private ColorStateList getColoredSummaryColor(boolean enabled) {
        if (enabled) {
            TypedValue colorPrimaryDark = new TypedValue();
            mContext.getTheme().resolveAttribute(
                    R.attr.colorPrimaryDark, colorPrimaryDark, true);

            int[][] states = new int[][] {
                    new int[] {android.R.attr.state_enabled},
                    new int[] {-android.R.attr.state_enabled}
            };
            int[] colors = new int[] {
                    Color.argb(0xff,
                            Color.red(colorPrimaryDark.data),
                            Color.green(colorPrimaryDark.data),
                            Color.blue(colorPrimaryDark.data)),
                    Color.argb(0x4d,
                            Color.red(colorPrimaryDark.data),
                            Color.green(colorPrimaryDark.data),
                            Color.blue(colorPrimaryDark.data))
            };
            return new ColorStateList(states, colors);
        } else {
            TypedValue outValue = new TypedValue();
            mContext.getTheme().resolveAttribute(
                    R.attr.isLightTheme, outValue, true);
            return mContext.getColorStateList(outValue.data != 0
                    ? R.color.sesl_secondary_text_light
                    : R.color.sesl_secondary_text_dark);
        }
    }

    private void setRelativeLinkCard() {
        if (mRelativeLinkCard == null) {
            mRelativeLinkCard = PreferenceUtils.createRelatedCard(mContext);
            mRelativeLinkCard.addButton("This", null)
                    .addButton("That", null)
                    .addButton("There", null)
                    .show(this);
        }
    }
}
