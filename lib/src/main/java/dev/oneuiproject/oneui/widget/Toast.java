package dev.oneuiproject.oneui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.StringRes;

import dev.oneuiproject.oneui.design.R;

public class Toast extends android.widget.Toast {
    public Toast(Context context) {
        super(context);
    }

    public static Toast makeText(Context context, CharSequence text, int duration) {
        Toast result = new Toast(context);

        LayoutInflater inflate = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.oui_transient_notification, null);
        TextView tv = v.findViewById(android.R.id.message);
        tv.setText(text);

        result.setView(v);
        result.setDuration(duration);

        return result;
    }

    public static Toast makeText(Context context, @StringRes int resId, int duration)
            throws Resources.NotFoundException {
        return makeText(context, context.getResources().getText(resId), duration);
    }
}
