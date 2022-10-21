package dev.oneuiproject.oneui.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import dev.oneuiproject.oneui.design.R;

public class DialogUtils {
    private static final String TAG = "DialogUtils";

    public interface DialogProgressCallback {
        void onClick(View v);
    }

    public static void setDialogButtonTextColor(@NonNull AlertDialog dialog, int whichButton,
                                                  @ColorInt int textColor) {
        if (dialog != null) {
            Button dialogBtn = dialog.getButton(whichButton);
            if (dialogBtn == null) {
                Log.e(TAG, "setDialogButtonTextColor: dialog button is null, " +
                        "Ensure you're calling this after dialog.show()");
                return;
            }
            dialogBtn.setTextColor(textColor);
        } else {
            Log.e(TAG, "setDialogButtonTextColor: dialog is null");
        }
    }

    public static void setDialogButtonTextColor(@NonNull AlertDialog dialog, int whichButton,
                                                        @Nullable ColorStateList textColor) {
        if (dialog != null) {
            Button dialogBtn = dialog.getButton(whichButton);
            if (dialogBtn == null) {
                Log.e(TAG, "setDialogButtonTextColor: dialog button is null, " +
                        "Ensure you're calling this after dialog.show()");
                return;
            }
            dialogBtn.setTextColor(textColor);
        } else {
            Log.e(TAG, "setDialogButtonTextColor: dialog is null");
        }
    }

    public static void setDialogProgressForButton(@NonNull AlertDialog dialog, int whichButton,
                                         @Nullable DialogProgressCallback callback) {
        if (dialog != null) {
            Button dialogBtn = dialog.getButton(whichButton);
            if (dialogBtn == null) {
                Log.e(TAG, "setDialogProgressForButton: dialog button is null, " +
                        "Ensure you're calling this after dialog.show()");
                return;
            }

            dialogBtn.setOnClickListener(v -> {
                replaceDialogBtnWithProgress(dialog, dialogBtn);

                if (callback != null) {
                    callback.onClick(v);
                }
            });
        } else {
            Log.e(TAG, "setDialogProgressForButton: dialog is null");
        }
    }

    private static void replaceDialogBtnWithProgress(@NonNull AlertDialog dialog,
                                                     @NonNull Button dialogBtn) {
        Context context = dialog.getContext();

        Button positiveBtn = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button negativeBtn = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        Button neutralBtn = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if (positiveBtn != null) positiveBtn.setEnabled(false);
        if (negativeBtn != null) negativeBtn.setEnabled(false);
        if (neutralBtn != null) neutralBtn.setEnabled(false);

        ViewGroup buttonBar = (ViewGroup) dialogBtn.getParent();
        if (buttonBar != null) {
            final int btnIndex = buttonBar.indexOfChild(dialogBtn);

            ViewGroup.LayoutParams lp = dialogBtn.getLayoutParams();
            lp.width = context.getResources()
                    .getDimensionPixelSize(R.dimen.sesl_dialog_button_min_height);
            lp.height = context.getResources()
                    .getDimensionPixelSize(R.dimen.sesl_dialog_button_min_height);

            LayoutInflater inflater = LayoutInflater.from(context);
            View progressView = inflater.inflate(
                    R.layout.oui_view_dialog_progress_bar, buttonBar, false);
            progressView.setLayoutParams(lp);

            buttonBar.removeView(dialogBtn);
            buttonBar.addView(progressView, btnIndex);
        }
    }

}
