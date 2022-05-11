package dev.oneuiproject.oneui.utils;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.appcompat.util.SeslRoundedCorner;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView;

import dev.oneuiproject.oneui.preference.InsetPreferenceCategory;
import dev.oneuiproject.oneui.preference.LayoutPreference;
import dev.oneuiproject.oneui.preference.internal.PreferenceRelatedCard;

public class PreferenceUtils {
    private static final String TAG = "PreferenceUtils";

    @Nullable
    public static PreferenceRelatedCard createRelatedCard(
            @NonNull Context context) {
        if (context != null) {
            return PreferenceRelatedCard.createRelatedCard(context);
        } else {
            Log.e(TAG, "createRelatedCard: context is null");
            return null;
        }
    }

    /**
     * @hide
     */
    @RestrictTo(LIBRARY)
    public static void addRelatedCardToFooter(@NonNull PreferenceFragmentCompat fragment,
                                      @NonNull View view) {
        PreferenceScreen prefScreen = fragment.getPreferenceScreen();
        if (prefScreen != null) {
            InsetPreferenceCategory insetCategory
                    = new InsetPreferenceCategory(fragment.getContext());
            insetCategory.setOrder(Integer.MAX_VALUE - 2);
            insetCategory.seslSetSubheaderRoundedBackground(
                    SeslRoundedCorner.ROUNDED_CORNER_BOTTOM_LEFT
                            | SeslRoundedCorner.ROUNDED_CORNER_BOTTOM_RIGHT);

            LayoutPreference relativeCard = new LayoutPreference(
                    prefScreen.getContext(), view, true);
            relativeCard.setOrder(Integer.MAX_VALUE - 1);
            relativeCard.seslSetSubheaderRoundedBackground(
                    SeslRoundedCorner.ROUNDED_CORNER_NONE);

            prefScreen.addPreference(insetCategory);
            prefScreen.addPreference(relativeCard);

            RecyclerView listView = fragment.getListView();
            if (listView != null) {
                listView.seslSetLastRoundedCorner(false);
            }
        } else {
            Log.e(TAG, "addRelatedCardToFooter: prefScreen is null");
        }
    }

}
