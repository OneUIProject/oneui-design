package dev.oneuiproject.oneui.dialog;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.DialogTitle;
import androidx.appcompat.widget.SeslProgressBar;

import java.text.NumberFormat;

import dev.oneuiproject.oneui.design.R;

public class ProgressDialog extends AlertDialog {
    public static final int STYLE_SPINNER = 0;
    public static final int STYLE_HORIZONTAL = 1;
    public static final int STYLE_CIRCLE = 2;

    private Context mContext;

    private View mContentView;
    private SeslProgressBar mProgress;
    private TextView mMessageView;
    
    private int mProgressStyle = STYLE_SPINNER;

    private TextView mProgressNumber;
    private String mProgressNumberFormat;
    private TextView mProgressPercent;
    private NumberFormat mProgressPercentFormat;
    
    private int mMax;
    private int mProgressVal;
    private int mSecondaryProgressVal;
    private int mIncrementBy;
    private int mIncrementSecondaryBy;
    private Drawable mProgressDrawable;
    private Drawable mIndeterminateDrawable;
    private CharSequence mMessage;
    private boolean mIndeterminate;
    
    private boolean mHasStarted;
    private Handler mViewUpdateHandler;

    /**
     * Creates a Progress dialog.
     *
     * @param context the parent context
     */
    public ProgressDialog(Context context) {
        super(context);
        mContext = context;
        initFormats();
    }

    /**
     * Creates a Progress dialog.
     *
     * @param context the parent context
     * @param theme the resource ID of the theme against which to inflate
     *              this dialog, or {@code 0} to use the parent
     *              {@code context}'s default alert dialog theme
     */
    public ProgressDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
        initFormats();
    }

    private void initFormats() {
        mProgressNumberFormat = "%1d/%1d";
        mProgressPercentFormat = NumberFormat.getPercentInstance();
        mProgressPercentFormat.setMaximumFractionDigits(0);
    }

    /**
     * Creates and shows a ProgressDialog.
     *
     * @param context the parent context
     * @param title the title text for the dialog's window
     * @param message the text to be displayed in the dialog
     * @return the ProgressDialog
     */
    public static ProgressDialog show(Context context, CharSequence title,
            CharSequence message) {
        return show(context, title, message, false);
    }

    /**
     * Creates and shows a ProgressDialog.
     *
     * @param context the parent context
     * @param title the title text for the dialog's window
     * @param message the text to be displayed in the dialog
     * @param indeterminate true if the dialog should be {@link #setIndeterminate(boolean)
     *        indeterminate}, false otherwise
     * @return the ProgressDialog
     */
    public static ProgressDialog show(Context context, CharSequence title,
            CharSequence message, boolean indeterminate) {
        return show(context, title, message, indeterminate, false, null);
    }

    /**
     * Creates and shows a ProgressDialog.
     *
     * @param context the parent context
     * @param title the title text for the dialog's window
     * @param message the text to be displayed in the dialog
     * @param indeterminate true if the dialog should be {@link #setIndeterminate(boolean)
     *        indeterminate}, false otherwise
     * @param cancelable true if the dialog is {@link #setCancelable(boolean) cancelable},
     *        false otherwise
     * @return the ProgressDialog
     */
    public static ProgressDialog show(Context context, CharSequence title,
            CharSequence message, boolean indeterminate, boolean cancelable) {
        return show(context, title, message, indeterminate, cancelable, null);
    }

    /**
     * Creates and shows a ProgressDialog.
     *
     * @param context the parent context
     * @param title the title text for the dialog's window
     * @param message the text to be displayed in the dialog
     * @param indeterminate true if the dialog should be {@link #setIndeterminate(boolean)
     *        indeterminate}, false otherwise
     * @param cancelable true if the dialog is {@link #setCancelable(boolean) cancelable},
     *        false otherwise
     * @param cancelListener the {@link #setOnCancelListener(OnCancelListener) listener}
     *        to be invoked when the dialog is canceled
     * @return the ProgressDialog
     */
    public static ProgressDialog show(Context context, CharSequence title,
            CharSequence message, boolean indeterminate,
            boolean cancelable, OnCancelListener cancelListener) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIndeterminate(indeterminate);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        dialog.show();
        return dialog;
    }

    @Override
    public void show() {
        super.show();

        DialogTitle dialogTitle = findViewById(R.id.alertTitle);
        if (dialogTitle != null && !dialogTitle.getText().toString().isEmpty()) {
            if (mContentView != null) {
                final int topPadding = mContext.getResources()
                        .getDimensionPixelSize(R.dimen.sesl_dialog_title_padding_bottom);
                mContentView.setPaddingRelative(
                        mContentView.getPaddingStart(), topPadding,
                        mContentView.getPaddingEnd(), mContentView.getPaddingBottom());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        TypedArray a = mContext.obtainStyledAttributes(null,
                R.styleable.AlertDialog,
                R.attr.alertDialogStyle, 0);
        if (mProgressStyle == STYLE_HORIZONTAL) {
            
            /* Use a separate handler to update the text views as they
             * must be updated on the same thread that created them.
             */
            mViewUpdateHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    
                    /* Update the number and percent */
                    int progress = mProgress.getProgress();
                    int max = mProgress.getMax();
                    if (mProgressNumberFormat != null) {
                        String format = mProgressNumberFormat;
                        if (mProgressNumber.getLayoutDirection()
                                == View.LAYOUT_DIRECTION_RTL) {
                            mProgressNumber.setText(String.format(format, max, progress));
                        } else {
                            mProgressNumber.setText(String.format(format, progress, max));
                        }
                    } else {
                        mProgressNumber.setText("");
                    }
                    if (mProgressPercentFormat != null) {
                        double percent = (double) progress / (double) max;
                        SpannableString tmp = new SpannableString(mProgressPercentFormat.format(percent));
                        tmp.setSpan(new StyleSpan(Typeface.NORMAL),
                                0, tmp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mProgressPercent.setText(tmp);
                    } else {
                        mProgressPercent.setText("");
                    }
                }
            };
            mContentView = inflater.inflate(R.layout.oui_dialog_progress_dialog_horizontal, null);
            mProgress = (SeslProgressBar) mContentView.findViewById(R.id.progress);
            mProgressNumber = (TextView) mContentView.findViewById(R.id.progress_number);
            mProgressPercent = (TextView) mContentView.findViewById(R.id.progress_percent);
            mMessageView = (TextView) mContentView.findViewById(R.id.message);
            setView(mContentView);
        } else if (mProgressStyle == STYLE_CIRCLE) {
            setTitle(null);
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            View view = inflater.inflate(R.layout.oui_dialog_progress_dialog_circle, null);
            mProgress = (SeslProgressBar) view.findViewById(R.id.progress);
            mMessageView = (TextView) view.findViewById(R.id.message);
            setView(view);
        } else {
            View view = inflater.inflate(R.layout.oui_dialog_progress_dialog_spinner, null);
            mProgress = (SeslProgressBar) view.findViewById(R.id.progress);
            mMessageView = (TextView) view.findViewById(R.id.message);
            setView(view);
        }
        a.recycle();
        if (mMax > 0) {
            setMax(mMax);
        }
        if (mProgressVal > 0) {
            setProgress(mProgressVal);
        }
        if (mSecondaryProgressVal > 0) {
            setSecondaryProgress(mSecondaryProgressVal);
        }
        if (mIncrementBy > 0) {
            incrementProgressBy(mIncrementBy);
        }
        if (mIncrementSecondaryBy > 0) {
            incrementSecondaryProgressBy(mIncrementSecondaryBy);
        }
        if (mProgressDrawable != null) {
            setProgressDrawable(mProgressDrawable);
        }
        if (mIndeterminateDrawable != null) {
            setIndeterminateDrawable(mIndeterminateDrawable);
        }
        if (mMessage != null) {
            setMessage(mMessage);
        }
        setIndeterminate(mIndeterminate);
        onProgressChanged();
        super.onCreate(savedInstanceState);

        if (mProgressStyle == STYLE_CIRCLE) {
            int size = (int) ((70 * mContext.getResources().getDisplayMetrics().density));
            getWindow().setGravity(Gravity.CENTER);
            getWindow().setLayout(size, size);
        }
    }
    
    @Override
    public void onStart() {
        super.onStart();
        mHasStarted = true;
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        mHasStarted = false;
    }

    /**
     * Sets the current progress.
     *
     * @param value the current progress, a value between 0 and {@link #getMax()}
     *
     * @see SeslProgressBar#setProgress(int)
     */
    public void setProgress(int value) {
        if (mHasStarted) {
            mProgress.setProgress(value);
            onProgressChanged();
        } else {
            mProgressVal = value;
        }
    }

    /**
     * Sets the secondary progress.
     *
     * @param secondaryProgress the current secondary progress, a value between 0 and
     * {@link #getMax()}
     *
     * @see SeslProgressBar#setSecondaryProgress(int)
     */
    public void setSecondaryProgress(int secondaryProgress) {
        if (mProgress != null) {
            mProgress.setSecondaryProgress(secondaryProgress);
            onProgressChanged();
        } else {
            mSecondaryProgressVal = secondaryProgress;
        }
    }

    /**
     * Gets the current progress.
     *
     * @return the current progress, a value between 0 and {@link #getMax()}
     */
    public int getProgress() {
        if (mProgress != null) {
            return mProgress.getProgress();
        }
        return mProgressVal;
    }

    /**
     * Gets the current secondary progress.
     *
     * @return the current secondary progress, a value between 0 and {@link #getMax()}
     */
    public int getSecondaryProgress() {
        if (mProgress != null) {
            return mProgress.getSecondaryProgress();
        }
        return mSecondaryProgressVal;
    }

    /**
     * Gets the maximum allowed progress value. The default value is 100.
     *
     * @return the maximum value
     */
    public int getMax() {
        if (mProgress != null) {
            return mProgress.getMax();
        }
        return mMax;
    }

    /**
     * Sets the maximum allowed progress value.
     */
    public void setMax(int max) {
        if (mProgress != null) {
            mProgress.setMax(max);
            onProgressChanged();
        } else {
            mMax = max;
        }
    }

    /**
     * Increments the current progress value.
     *
     * @param diff the amount by which the current progress will be incremented,
     * up to {@link #getMax()}
     */
    public void incrementProgressBy(int diff) {
        if (mProgress != null) {
            mProgress.incrementProgressBy(diff);
            onProgressChanged();
        } else {
            mIncrementBy += diff;
        }
    }

    /**
     * Increments the current secondary progress value.
     *
     * @param diff the amount by which the current secondary progress will be incremented,
     * up to {@link #getMax()}
     */
    public void incrementSecondaryProgressBy(int diff) {
        if (mProgress != null) {
            mProgress.incrementSecondaryProgressBy(diff);
            onProgressChanged();
        } else {
            mIncrementSecondaryBy += diff;
        }
    }

    /**
     * Sets the drawable to be used to display the progress value.
     *
     * @param d the drawable to be used
     *
     * @see SeslProgressBar#setProgressDrawable(Drawable)
     */
    public void setProgressDrawable(Drawable d) {
        if (mProgress != null) {
            mProgress.setProgressDrawable(d);
        } else {
            mProgressDrawable = d;
        }
    }

    /**
     * Sets the drawable to be used to display the indeterminate progress value.
     *
     * @param d the drawable to be used
     *
     * @see SeslProgressBar#setProgressDrawable(Drawable)
     * @see #setIndeterminate(boolean)
     */
    public void setIndeterminateDrawable(Drawable d) {
        if (mProgress != null) {
            mProgress.setIndeterminateDrawable(d);
        } else {
            mIndeterminateDrawable = d;
        }
    }

    /**
     * Change the indeterminate mode for this ProgressDialog. In indeterminate
     * mode, the progress is ignored and the dialog shows an infinite
     * animation instead.
     *
     * <p><strong>Note:</strong> A ProgressDialog with style {@link #STYLE_SPINNER}
     * is always indeterminate and will ignore this setting.</p>
     *
     * @param indeterminate true to enable indeterminate mode, false otherwise
     *
     * @see #setProgressStyle(int)
     */
    public void setIndeterminate(boolean indeterminate) {
        if (mProgress != null) {
            mProgress.setIndeterminate(indeterminate);
        } else {
            mIndeterminate = indeterminate;
        }
    }

    /**
     * Whether this ProgressDialog is in indeterminate mode.
     *
     * @return true if the dialog is in indeterminate mode, false otherwise
     */
    public boolean isIndeterminate() {
        if (mProgress != null) {
            return mProgress.isIndeterminate();
        }
        return mIndeterminate;
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (mContentView != null) {
            final int topPaddingWithoutTitle = mContext.getResources()
                    .getDimensionPixelSize(R.dimen.sesl_dialog_padding_vertical);
            final int topPaddingWithTitle = mContext.getResources()
                    .getDimensionPixelSize(R.dimen.sesl_dialog_title_padding_bottom);
            final int paddingTop = title.toString().isEmpty()
                    ? topPaddingWithoutTitle
                    : topPaddingWithTitle;
            mContentView.setPaddingRelative(
                    mContentView.getPaddingStart(), paddingTop,
                    mContentView.getPaddingEnd(), mContentView.getPaddingBottom());
        }
    }
    
    @Override
    public void setMessage(CharSequence message) {
        if (mProgress != null) {
            if (mProgressStyle == STYLE_HORIZONTAL) {
                if (mMessageView != null) {
                    mMessageView.setText(message);
                    mMessageView.setVisibility(message != "" ? View.VISIBLE : View.GONE);
                } else {
                    super.setMessage(message);
                }
            } else if (mProgressStyle == STYLE_CIRCLE) {
                mMessageView.setText(message);
                mMessageView.setVisibility(message != "" ? View.VISIBLE : View.GONE);
            } else {
                mMessageView.setText(message);
            }
        } else {
            mMessage = message;
        }
    }

    /**
     * Sets the style of this ProgressDialog, either {@link #STYLE_SPINNER} or
     * {@link #STYLE_HORIZONTAL}. The default is {@link #STYLE_SPINNER}.
     *
     * <p><strong>Note:</strong> A ProgressDialog with style {@link #STYLE_SPINNER}
     * is always indeterminate and will ignore the {@link #setIndeterminate(boolean)
     * indeterminate} setting.</p>
     *
     * @param style the style of this ProgressDialog, either {@link #STYLE_SPINNER} or
     * {@link #STYLE_HORIZONTAL}
     */
    public void setProgressStyle(int style) {
        mProgressStyle = style;
    }

    /**
     * Change the format of the small text showing current and maximum units
     * of progress.  The default is "%1d/%2d".
     * Should not be called during the number is progressing.
     * @param format A string passed to {@link String#format String.format()};
     * use "%1d" for the current number and "%2d" for the maximum.  If null,
     * nothing will be shown.
     */
    public void setProgressNumberFormat(String format) {
        mProgressNumberFormat = format;
        onProgressChanged();
    }

    /**
     * Change the format of the small text showing the percentage of progress.
     * The default is
     * {@link NumberFormat#getPercentInstance() NumberFormat.getPercentageInstnace().}
     * Should not be called during the number is progressing.
     * @param format An instance of a {@link NumberFormat} to generate the
     * percentage text.  If null, nothing will be shown.
     */
    public void setProgressPercentFormat(NumberFormat format) {
        mProgressPercentFormat = format;
        onProgressChanged();
    }
    
    private void onProgressChanged() {
        if (mProgressStyle == STYLE_HORIZONTAL) {
            if (mViewUpdateHandler != null && !mViewUpdateHandler.hasMessages(0)) {
                mViewUpdateHandler.sendEmptyMessage(0);
            }
        }
    }
}
