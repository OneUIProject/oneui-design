package dev.oneuiproject.oneui.dialog.internal;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StartEndTimePickerUtils {
    public static String getTimeText(Context context, Calendar calendar, boolean is24HourView) {
        String pattern = ((SimpleDateFormat) android.text.format.DateFormat.getTimeFormat(context)).toPattern();
        if (is24HourView) {
            pattern = pattern.replace("a", "").replace("h", "H").trim();
        }
        return new SimpleDateFormat(pattern, getDisplayLocale(context)).format(new Date(calendar.getTimeInMillis()));
    }

    public static Calendar getCustomCalendarInstance(int hourOfDay, int minute, boolean is24HourView) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(is24HourView ? 11 : 10, hourOfDay);
        calendar.set(12, minute);
        return calendar;
    }

    private static Locale getDisplayLocale(Context context) {
        Locale locale = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        }
        return locale == null ? Locale.getDefault() : locale;
    }
}
