package dev.oneuiproject.oneui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.animation.SeslAnimationUtils;
import androidx.reflect.content.res.SeslConfigurationReflector;
import androidx.reflect.widget.SeslTextViewReflector;

import dev.oneuiproject.oneui.design.R;

public class TipPopup {
    private static final int ANIMATION_DURATION_BOUNCE_SCALE1 = 167;
    private static final int ANIMATION_DURATION_BOUNCE_SCALE2 = 250;
    private static final int ANIMATION_DURATION_DISMISS_ALPHA = 166;
    private static final int ANIMATION_DURATION_DISMISS_SCALE = 166;
    private static final int ANIMATION_DURATION_EXPAND_ALPHA = 83;
    private static final int ANIMATION_DURATION_EXPAND_SCALE = 500;
    private static final int ANIMATION_DURATION_EXPAND_TEXT = 167;
    private static final int ANIMATION_DURATION_SHOW_SCALE = 500;
    private static final int ANIMATION_OFFSET_BOUNCE_SCALE = 3000;
    private static final int ANIMATION_OFFSET_EXPAND_TEXT = 100;
    public static final int DIRECTION_BOTTOM_LEFT = 2;
    public static final int DIRECTION_BOTTOM_RIGHT = 3;
    public static final int DIRECTION_DEFAULT = -1;
    public static final int DIRECTION_TOP_LEFT = 0;
    public static final int DIRECTION_TOP_RIGHT = 1;
    public static final int MODE_NORMAL = 0;
    public static final int MODE_TRANSLUCENT = 1;
    private static final int MSG_DISMISS = 1;
    private static final int MSG_SCALE_UP = 2;
    private static final int MSG_TIMEOUT = 0;
    public static final int STATE_DISMISSED = 0;
    public static final int STATE_EXPANDED = 2;
    public static final int STATE_HINT = 1;
    private static final String TAG = "TipPopup";
    private static final int TIMEOUT_DURATION_MS = 7100;
    private static final int TYPE_BALLOON_ACTION = 1;
    private static final int TYPE_BALLOON_CUSTOM = 2;
    private static final int TYPE_BALLOON_SIMPLE = 0;
    private static final boolean localLOGD = true;
    private static Handler mHandler;
    private View.OnClickListener mActionClickListener;
    private CharSequence mActionText;
    private Integer mActionTextColor;
    private final Button mActionView;
    private int mArrowDirection;
    private final int mArrowHeight;
    private int mArrowPositionX;
    private int mArrowPositionY;
    private final int mArrowWidth;
    private int mBackgroundColor;
    private ImageView mBalloonBg1;
    private ImageView mBalloonBg2;
    private FrameLayout mBalloonBubble;
    private ImageView mBalloonBubbleHint;
    private ImageView mBalloonBubbleIcon;
    private FrameLayout mBalloonContent;
    private int mBalloonHeight;
    private FrameLayout mBalloonPanel;
    private TipWindow mBalloonPopup;
    private int mBalloonPopupX;
    private int mBalloonPopupY;
    private final View mBalloonView;
    private int mBalloonWidth;
    private int mBalloonX;
    private int mBalloonY;
    private Integer mBorderColor;
    private ImageView mBubbleBackground;
    private int mBubbleHeight;
    private ImageView mBubbleIcon;
    private TipWindow mBubblePopup;
    private int mBubblePopupX;
    private int mBubblePopupY;
    private final View mBubbleView;
    private int mBubbleWidth;
    private int mBubbleX;
    private int mBubbleY;
    private final Context mContext;
    private final Rect mDisplayFrame;
    private DisplayMetrics mDisplayMetrics;
    private boolean mForceRealDisplay;
    private CharSequence mHintDescription;
    private final int mHorizontalTextMargin;
    private int mInitialmMessageViewWidth;
    private boolean mIsDefaultPosition;
    private boolean mIsMessageViewMeasured;
    private CharSequence mMessageText;
    private Integer mMessageTextColor;
    private final TextView mMessageView;
    private final int mMode;
    private boolean mNeedToCallParentViewsOnClick;
    private OnDismissListener mOnDismissListener;
    private OnStateChangeListener mOnStateChangeListener;
    private final View mParentView;
    private final Resources mResources;
    private int mScaleMargin;
    private int mSideMargin;
    private int mState;
    private int mType;
    private final int mVerticalTextMargin;
    private final WindowManager mWindowManager;
    private static Interpolator INTERPOLATOR_SINE_IN_OUT_33 = null;
    private static Interpolator INTERPOLATOR_SINE_IN_OUT_70 = null;
    private static Interpolator INTERPOLATOR_ELASTIC_50 = null;
    private static Interpolator INTERPOLATOR_ELASTIC_CUSTOM = null;

    public interface OnDismissListener {
        void onDismiss();
    }

    public interface OnStateChangeListener {
        void onStateChanged(int i);
    }

    public void setOnStateChangeListener(OnStateChangeListener changeListener) {
        this.mOnStateChangeListener = changeListener;
    }

    public TipPopup(View parentView) {
        this(parentView, MODE_NORMAL);
    }

    public TipPopup(View parentView, int mode) {
        this.mIsDefaultPosition = true;
        this.mMessageText = null;
        this.mActionText = null;
        this.mHintDescription = null;
        this.mActionClickListener = null;
        this.mMessageTextColor = null;
        this.mActionTextColor = null;
        this.mBorderColor = null;
        this.mInitialmMessageViewWidth = 0;
        this.mIsMessageViewMeasured = false;
        this.mForceRealDisplay = false;
        this.mNeedToCallParentViewsOnClick = false;
        if (mode < MODE_NORMAL || mode > MODE_TRANSLUCENT) {
            throw new IllegalArgumentException("Invalid SmartTip mode : " + mode + " ,mode can either be 0 (MODE_NORMAL) or 1 (MODE_TRANSLUCENT)");
        }
        Context context = parentView.getContext();
        this.mContext = context;
        Resources resources = context.getResources();
        this.mResources = resources;
        this.mParentView = parentView;
        this.mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.mDisplayMetrics = resources.getDisplayMetrics();
        debugLog("mDisplayMetrics = " + this.mDisplayMetrics);
        this.mState = STATE_HINT;
        this.mType = TYPE_BALLOON_SIMPLE;
        this.mMode = mode;
        this.mBackgroundColor = context.getColor(R.color.sem_tip_popup_background_color);
        initInterpolator();
        LayoutInflater inflater = LayoutInflater.from(context);
        this.mBubbleView = inflater.inflate(R.layout.sem_tip_popup_bubble, (ViewGroup) null);
        View inflate = inflater.inflate(R.layout.sem_tip_popup_balloon, (ViewGroup) null);
        this.mBalloonView = inflate;
        initBubblePopup(mode);
        initBalloonPopup(mode);
        TextView textView = (TextView) inflate.findViewById(R.id.sem_tip_popup_message);
        this.mMessageView = textView;
        Button button = (Button) inflate.findViewById(R.id.sem_tip_popup_action);
        this.mActionView = button;
        textView.setVisibility(View.GONE);
        button.setVisibility(View.GONE);
        this.mArrowPositionX = -1;
        this.mArrowPositionY = -1;
        this.mArrowDirection = DIRECTION_DEFAULT;
        this.mBalloonX = -1;
        if (mode == MODE_TRANSLUCENT) {
            textView.setTextColor(resources.getColor(R.color.sem_tip_popup_text_color_translucent, null));
            button.setTextColor(resources.getColor(R.color.sem_tip_popup_text_color_translucent, null));
        }
        this.mScaleMargin = resources.getDimensionPixelSize(R.dimen.sem_tip_popup_scale_margin);
        this.mSideMargin = resources.getDimensionPixelSize(R.dimen.sem_tip_popup_side_margin);
        this.mArrowHeight = resources.getDimensionPixelSize(R.dimen.sem_tip_popup_balloon_arrow_height);
        this.mArrowWidth = resources.getDimensionPixelSize(R.dimen.sem_tip_popup_balloon_arrow_width);
        this.mHorizontalTextMargin = resources.getDimensionPixelSize(R.dimen.sem_tip_popup_balloon_message_margin_horizontal);
        this.mVerticalTextMargin = resources.getDimensionPixelSize(R.dimen.sem_tip_popup_balloon_message_margin_vertical);
        this.mDisplayFrame = new Rect();
        this.mBubblePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (TipPopup.this.mState == STATE_HINT) {
                    TipPopup.this.mState = STATE_DISMISSED;
                    if (TipPopup.this.mOnStateChangeListener != null) {
                        TipPopup.this.mOnStateChangeListener.onStateChanged(TipPopup.this.mState);
                        TipPopup.this.debugLog("mIsShowing : " + TipPopup.this.isShowing());
                    }
                    if (TipPopup.mHandler != null) {
                        TipPopup.mHandler.removeCallbacksAndMessages(null);
                        TipPopup.mHandler = null;
                    }
                    TipPopup.this.debugLog("onDismiss - BubblePopup");
                }
            }
        });
        this.mBalloonPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                TipPopup.this.mState = STATE_DISMISSED;
                if (TipPopup.this.mOnStateChangeListener != null) {
                    TipPopup.this.mOnStateChangeListener.onStateChanged(TipPopup.this.mState);
                    TipPopup.this.debugLog("mIsShowing : " + TipPopup.this.isShowing());
                }
                TipPopup.this.debugLog("onDismiss - BalloonPopup");
                TipPopup.this.dismissBubble(false);
                if (TipPopup.mHandler != null) {
                    TipPopup.mHandler.removeCallbacksAndMessages(null);
                    TipPopup.mHandler = null;
                }
            }
        });
        inflate.setAccessibilityDelegate(new View.AccessibilityDelegate() {
            @Override
            public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                info.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, TipPopup.this.mContext.getString(R.string.oui_common_close)));
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void initInterpolator() {
        if (INTERPOLATOR_SINE_IN_OUT_33 == null) {
            INTERPOLATOR_SINE_IN_OUT_33 = AnimationUtils.loadInterpolator(this.mContext, R.interpolator.sine_in_out_33);
        }
        if (INTERPOLATOR_SINE_IN_OUT_70 == null) {
            INTERPOLATOR_SINE_IN_OUT_70 = AnimationUtils.loadInterpolator(this.mContext, R.interpolator.sine_in_out_70);
        }
        if (INTERPOLATOR_ELASTIC_50 == null) {
            INTERPOLATOR_ELASTIC_50 = SeslAnimationUtils.ELASTIC_50;
        }
        if (INTERPOLATOR_ELASTIC_CUSTOM == null) {
            INTERPOLATOR_ELASTIC_CUSTOM = SeslAnimationUtils.ELASTIC_40; //TODO: new SeslElasticInterpolator(1.0f, 1.3f);
        }
    }

    private void initBubblePopup(int mode) {
        this.mBubbleBackground = (ImageView) this.mBubbleView.findViewById(R.id.sem_tip_popup_bubble_bg);
        this.mBubbleIcon = (ImageView) this.mBubbleView.findViewById(R.id.sem_tip_popup_bubble_icon);
        if (mode == MODE_TRANSLUCENT) {
            this.mBubbleBackground.setImageResource(R.drawable.sem_tip_popup_hint_background_translucent);
            this.mBubbleBackground.setImageTintList(null);
            if (isRTL()) {
                this.mBubbleIcon.setImageResource(R.drawable.sem_tip_popup_hint_icon_translucent_rtl);
            } else {
                this.mBubbleIcon.setImageResource(R.drawable.sem_tip_popup_hint_icon_translucent);
            }
            this.mBubbleIcon.setImageTintList(null);
            this.mBubbleWidth = this.mResources.getDimensionPixelSize(R.dimen.sem_tip_popup_bubble_width_translucent);
            this.mBubbleHeight = this.mResources.getDimensionPixelSize(R.dimen.sem_tip_popup_bubble_height_translucent);
        } else {
            this.mBubbleWidth = this.mResources.getDimensionPixelSize(R.dimen.sem_tip_popup_bubble_width);
            this.mBubbleHeight = this.mResources.getDimensionPixelSize(R.dimen.sem_tip_popup_bubble_height);
        }
        TipWindow tipWindow = new TipWindow(this.mBubbleView, this.mBubbleWidth, this.mBubbleHeight, false);
        this.mBubblePopup = tipWindow;
        tipWindow.setTouchable(true);
        this.mBubblePopup.setOutsideTouchable(true);
        this.mBubblePopup.setAttachedInDecor(false);
    }

    private void initBalloonPopup(int mode) {
        this.mBalloonBubble = (FrameLayout) this.mBalloonView.findViewById(R.id.sem_tip_popup_balloon_bubble);
        this.mBalloonBubbleHint = (ImageView) this.mBalloonView.findViewById(R.id.sem_tip_popup_balloon_bubble_hint);
        this.mBalloonBubbleIcon = (ImageView) this.mBalloonView.findViewById(R.id.sem_tip_popup_balloon_bubble_icon);
        this.mBalloonPanel = (FrameLayout) this.mBalloonView.findViewById(R.id.sem_tip_popup_balloon_panel);
        this.mBalloonContent = (FrameLayout) this.mBalloonView.findViewById(R.id.sem_tip_popup_balloon_content);
        this.mBalloonBg1 = (ImageView) this.mBalloonView.findViewById(R.id.sem_tip_popup_balloon_bg_01);
        this.mBalloonBg2 = (ImageView) this.mBalloonView.findViewById(R.id.sem_tip_popup_balloon_bg_02);
        if (mode == MODE_TRANSLUCENT) {
            this.mBalloonBg1.setBackgroundResource(R.drawable.sem_tip_popup_balloon_background_left_translucent);
            this.mBalloonBg1.setBackgroundTintList(null);
            this.mBalloonBg2.setBackgroundResource(R.drawable.sem_tip_popup_balloon_background_right_translucent);
            this.mBalloonBg2.setBackgroundTintList(null);
        }
        this.mBalloonBubble.setVisibility(View.VISIBLE);
        this.mBalloonPanel.setVisibility(View.GONE);
        TipWindow tipWindow = new TipWindow(this.mBalloonView, this.mBalloonWidth, this.mBalloonHeight, true);
        this.mBalloonPopup = tipWindow;
        tipWindow.setFocusable(true);
        this.mBalloonPopup.setTouchable(true);
        this.mBalloonPopup.setOutsideTouchable(true);
        this.mBalloonPopup.setAttachedInDecor(false);
        this.mBalloonPopup.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (TipPopup.this.mNeedToCallParentViewsOnClick && TipPopup.this.mParentView.hasOnClickListeners() && (event.getAction() == 0 || event.getAction() == 4)) {
                    Rect parentViewBounds = new Rect();
                    int[] outLocation = new int[2];
                    TipPopup.this.mParentView.getLocationOnScreen(outLocation);
                    parentViewBounds.set(outLocation[0], outLocation[1], outLocation[0] + TipPopup.this.mParentView.getWidth(), outLocation[1] + TipPopup.this.mParentView.getHeight());
                    boolean isTouchContainedInParentView = parentViewBounds.contains((int) event.getRawX(), (int) event.getRawY());
                    if (isTouchContainedInParentView) {
                        TipPopup.this.debugLog("callOnClick for parent view");
                        TipPopup.this.mParentView.callOnClick();
                    }
                }
                return false;
            }
        });
    }

    public void show(int direction) {
        setInternal();
        if (this.mArrowPositionX == -1 || this.mArrowPositionY == -1) {
            calculateArrowPosition();
        }
        if (direction == -1) {
            calculateArrowDirection(this.mArrowPositionX, this.mArrowPositionY);
        } else {
            this.mArrowDirection = direction;
        }
        calculatePopupSize();
        calculatePopupPosition();
        setBubblePanel();
        setBalloonPanel();
        showInternal();
    }

    public void setMessage(CharSequence message) {
        this.mMessageText = message;
    }

    public void setAction(CharSequence actionText, View.OnClickListener listener) {
        this.mActionText = actionText;
        this.mActionClickListener = listener;
    }

    public void semCallParentViewsOnClick(boolean needToCall) {
        this.mNeedToCallParentViewsOnClick = needToCall;
    }

    public boolean isShowing() {
        boolean isBubbleShowing = false;
        boolean isBalloonShowing = false;
        TipWindow tipWindow = this.mBubblePopup;
        if (tipWindow != null) {
            isBubbleShowing = tipWindow.isShowing();
        }
        TipWindow tipWindow2 = this.mBalloonPopup;
        if (tipWindow2 != null) {
            isBalloonShowing = tipWindow2.isShowing();
        }
        return isBubbleShowing || isBalloonShowing;
    }

    public void dismiss(boolean withAnimation) {
        TipWindow tipWindow = this.mBubblePopup;
        if (tipWindow != null) {
            tipWindow.setUseDismissAnimation(withAnimation);
            debugLog("mBubblePopup.mIsDismissing = " + this.mBubblePopup.mIsDismissing);
            this.mBubblePopup.dismiss();
        }
        TipWindow tipWindow2 = this.mBalloonPopup;
        if (tipWindow2 != null) {
            tipWindow2.setUseDismissAnimation(withAnimation);
            debugLog("mBalloonPopup.mIsDismissing = " + this.mBalloonPopup.mIsDismissing);
            this.mBalloonPopup.dismiss();
        }
        OnDismissListener onDismissListener = this.mOnDismissListener;
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
        }
        Handler handler = mHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    public void setExpanded(boolean expanded) {
        if (expanded) {
            this.mState = STATE_EXPANDED;
            this.mScaleMargin = 0;
            return;
        }
        this.mScaleMargin = this.mResources.getDimensionPixelSize(R.dimen.sem_tip_popup_scale_margin);
    }

    public void setTargetPosition(int x, int y) {
        if (x < 0 || y < 0) {
            return;
        }
        this.mIsDefaultPosition = false;
        this.mArrowPositionX = x;
        this.mArrowPositionY = y;
    }

    public void setHintDescription(CharSequence hintDescription) {
        this.mHintDescription = hintDescription;
    }

    public void update() {
        update(this.mArrowDirection, false);
    }

    public void update(int direction, boolean resetHintTimer) {
        TipWindow tipWindow;
        TipWindow tipWindow2;
        if (!isShowing() || this.mParentView == null) {
            return;
        }
        setInternal();
        this.mBalloonX = -1;
        this.mBalloonY = -1;
        if (this.mIsDefaultPosition) {
            debugLog("update - default position");
            calculateArrowPosition();
        }
        if (direction == DIRECTION_DEFAULT) {
            calculateArrowDirection(this.mArrowPositionX, this.mArrowPositionY);
        } else {
            this.mArrowDirection = direction;
        }
        calculatePopupSize();
        calculatePopupPosition();
        setBubblePanel();
        setBalloonPanel();
        int i = this.mState;
        if (i == STATE_HINT && (tipWindow2 = this.mBubblePopup) != null) {
            tipWindow2.update(this.mBubblePopupX, this.mBubblePopupY, tipWindow2.getWidth(), this.mBubblePopup.getHeight());
            if (resetHintTimer) {
                debugLog("Timer Reset!");
                scheduleTimeout();
            }
        } else if (i == STATE_EXPANDED && (tipWindow = this.mBalloonPopup) != null) {
            tipWindow.update(this.mBalloonPopupX, this.mBalloonPopupY, tipWindow.getWidth(), this.mBalloonPopup.getHeight());
        }
    }

    public void setMessageTextColor(int color) {
        this.mMessageTextColor = Integer.valueOf((-16777216) | color);
    }

    public void setActionTextColor(int color) {
        this.mActionTextColor = Integer.valueOf((-16777216) | color);
    }

    public void setBackgroundColor(int color) {
        this.mBackgroundColor = (-16777216) | color;
    }

    public void setBackgroundColorWithAlpha(int color) {
        this.mBackgroundColor = color;
    }

    public void setBorderColor(int color) {
        this.mBorderColor = Integer.valueOf((-16777216) | color);
    }

    public void setOutsideTouchEnabled(boolean enabled) {
        this.mBubblePopup.setFocusable(enabled);
        this.mBubblePopup.setOutsideTouchable(enabled);
        this.mBalloonPopup.setFocusable(enabled);
        this.mBalloonPopup.setOutsideTouchable(enabled);
        debugLog("outside enabled : " + enabled);
    }

    public void setPopupWindowClippingEnabled(boolean enabled) {
        this.mBubblePopup.setClippingEnabled(enabled);
        this.mBalloonPopup.setClippingEnabled(enabled);
        this.mForceRealDisplay = !enabled;
        this.mSideMargin = enabled ? this.mResources.getDimensionPixelSize(R.dimen.sem_tip_popup_side_margin) : 0;
        debugLog("clipping enabled : " + enabled);
    }

    private void setInternal() {
        CharSequence charSequence;
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message message) {
                    switch (message.what) {
                        case MSG_TIMEOUT:
                            TipPopup.this.dismissBubble(true);
                            return;
                        case MSG_DISMISS:
                            TipPopup.this.dismissBubble(false);
                            return;
                        case MSG_SCALE_UP:
                            TipPopup.this.animateScaleUp();
                            return;
                        default:
                            return;
                    }
                }
            };
        }
        if (this.mMessageView == null || this.mActionView == null) {
            return;
        }
        float currentFontScale = this.mResources.getConfiguration().fontScale;
        int messageTextSize = this.mResources.getDimensionPixelOffset(R.dimen.sem_tip_popup_balloon_message_text_size);
        int actionTextSize = this.mResources.getDimensionPixelOffset(R.dimen.sem_tip_popup_balloon_action_text_size);
        if (currentFontScale > 1.2f) {
            this.mMessageView.setTextSize(0, (float) Math.floor(Math.ceil(messageTextSize / currentFontScale) * 1.2f));
            this.mActionView.setTextSize(0, (float) Math.floor(Math.ceil(actionTextSize / currentFontScale) * 1.2f));
        }
        this.mMessageView.setText(this.mMessageText);
        if (TextUtils.isEmpty(this.mActionText) || this.mActionClickListener == null) {
            this.mActionView.setVisibility(View.GONE);
            this.mActionView.setOnClickListener(null);
            this.mType = TYPE_BALLOON_SIMPLE;
        } else {
            this.mActionView.setVisibility(View.VISIBLE);
            SeslTextViewReflector.semSetButtonShapeEnabled(mActionView, true, mBackgroundColor);
            this.mActionView.setText(this.mActionText);
            this.mActionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TipPopup.this.mActionClickListener != null) {
                        TipPopup.this.mActionClickListener.onClick(view);
                    }
                    TipPopup.this.dismiss(true);
                }
            });
            this.mType = TYPE_BALLOON_ACTION;
        }
        ImageView imageView = this.mBubbleIcon;
        if (imageView != null && (charSequence = this.mHintDescription) != null) {
            imageView.setContentDescription(charSequence);
        }
        if (this.mMode == MODE_TRANSLUCENT || this.mBubbleIcon == null || this.mBubbleBackground == null || this.mBalloonBubble == null || this.mBalloonBg1 == null || this.mBalloonBg2 == null) {
            return;
        }
        Integer num = this.mMessageTextColor;
        if (num != null) {
            this.mMessageView.setTextColor(num.intValue());
        }
        Integer num2 = this.mActionTextColor;
        if (num2 != null) {
            this.mActionView.setTextColor(num2.intValue());
        }
        this.mBubbleBackground.setColorFilter(this.mBackgroundColor);
        this.mBalloonBubbleHint.setColorFilter(this.mBackgroundColor);
        this.mBalloonBg1.setBackgroundTintList(ColorStateList.valueOf(this.mBackgroundColor));
        this.mBalloonBg2.setBackgroundTintList(ColorStateList.valueOf(this.mBackgroundColor));
        Integer num3 = this.mBorderColor;
        if (num3 != null) {
            this.mBubbleIcon.setColorFilter(num3.intValue());
            this.mBalloonBubbleIcon.setColorFilter(this.mBorderColor.intValue());
        }
    }

    private void showInternal() {
        if (this.mState != STATE_EXPANDED) {
            this.mState = STATE_HINT;
            OnStateChangeListener onStateChangeListener = this.mOnStateChangeListener;
            if (onStateChangeListener != null) {
                onStateChangeListener.onStateChanged(STATE_HINT);
                debugLog("mIsShowing : " + isShowing());
            }
            TipWindow tipWindow = this.mBubblePopup;
            if (tipWindow != null) {
                tipWindow.showAtLocation(this.mParentView, 0, this.mBubblePopupX, this.mBubblePopupY);
                animateViewIn();
            }
            this.mBubbleView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    TipPopup.this.mState = STATE_EXPANDED;
                    if (TipPopup.this.mOnStateChangeListener != null) {
                        TipPopup.this.mOnStateChangeListener.onStateChanged(TipPopup.this.mState);
                    }
                    if (TipPopup.this.mBalloonPopup != null) {
                        TipPopup.this.mBalloonPopup.showAtLocation(TipPopup.this.mParentView, 0, TipPopup.this.mBalloonPopupX, TipPopup.this.mBalloonPopupY);
                    }
                    if (TipPopup.mHandler != null) {
                        TipPopup.mHandler.removeMessages(0);
                        TipPopup.mHandler.sendMessageDelayed(Message.obtain(TipPopup.mHandler, MSG_DISMISS), 10L);
                        TipPopup.mHandler.sendMessageDelayed(Message.obtain(TipPopup.mHandler, MSG_SCALE_UP), 20L);
                    }
                    return false;
                }
            });
        } else {
            this.mBalloonBubble.setVisibility(View.GONE);
            this.mBalloonPanel.setVisibility(View.VISIBLE);
            this.mMessageView.setVisibility(View.VISIBLE);
            OnStateChangeListener onStateChangeListener2 = this.mOnStateChangeListener;
            if (onStateChangeListener2 != null) {
                onStateChangeListener2.onStateChanged(this.mState);
            }
            TipWindow tipWindow2 = this.mBalloonPopup;
            if (tipWindow2 != null) {
                tipWindow2.showAtLocation(this.mParentView, 0, this.mBalloonPopupX, this.mBalloonPopupY);
            }
        }
        this.mBalloonView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (TipPopup.this.mType == TYPE_BALLOON_SIMPLE) {
                    TipPopup.this.dismiss(true);
                    return false;
                }
                return false;
            }
        });
    }

    private void setBubblePanel() {
        if (this.mBubblePopup == null) {
            return;
        }
        FrameLayout.LayoutParams paramBubblePanel = (FrameLayout.LayoutParams) this.mBubbleBackground.getLayoutParams();
        if (this.mMode == MODE_TRANSLUCENT) {
            paramBubblePanel.width = this.mResources.getDimensionPixelSize(R.dimen.sem_tip_popup_bubble_width_translucent);
            paramBubblePanel.height = this.mResources.getDimensionPixelSize(R.dimen.sem_tip_popup_bubble_height_translucent);
        }
        switch (this.mArrowDirection) {
            case DIRECTION_TOP_LEFT:
                TipWindow tipWindow = this.mBubblePopup;
                tipWindow.setPivot(tipWindow.getWidth(), this.mBubblePopup.getHeight());
                paramBubblePanel.gravity = 85;
                int i = this.mBubbleX;
                int i2 = this.mScaleMargin;
                this.mBubblePopupX = i - (i2 * 2);
                this.mBubblePopupY = this.mBubbleY - (i2 * 2);
                if (this.mMode == MODE_NORMAL) {
                    this.mBubbleBackground.setImageResource(R.drawable.sem_tip_popup_hint_background_03);
                    if (isRTL()) {
                        this.mBubbleIcon.setImageResource(R.drawable.sem_tip_popup_hint_icon_rtl);
                    } else {
                        this.mBubbleIcon.setImageResource(R.drawable.sem_tip_popup_hint_icon);
                    }
                } else {
                    this.mBubbleBackground.setRotationX(180.0f);
                }
                break;
            case DIRECTION_TOP_RIGHT:
                TipWindow tipWindow2 = this.mBubblePopup;
                tipWindow2.setPivot(0.0f, tipWindow2.getHeight());
                paramBubblePanel.gravity = 83;
                this.mBubblePopupX = this.mBubbleX;
                this.mBubblePopupY = this.mBubbleY - (this.mScaleMargin * 2);
                if (this.mMode == MODE_NORMAL) {
                    this.mBubbleBackground.setImageResource(R.drawable.sem_tip_popup_hint_background_04);
                    if (isRTL()) {
                        this.mBubbleIcon.setImageResource(R.drawable.sem_tip_popup_hint_icon_rtl);
                    } else {
                        this.mBubbleIcon.setImageResource(R.drawable.sem_tip_popup_hint_icon);
                    }
                } else {
                    this.mBubbleBackground.setRotation(180.0f);
                }
                break;
            case DIRECTION_BOTTOM_LEFT:
                TipWindow tipWindow3 = this.mBubblePopup;
                tipWindow3.setPivot(tipWindow3.getWidth(), 0.0f);
                paramBubblePanel.gravity = 53;
                this.mBubblePopupX = this.mBubbleX - (this.mScaleMargin * 2);
                this.mBubblePopupY = this.mBubbleY;
                if (this.mMode == MODE_NORMAL) {
                    this.mBubbleBackground.setImageResource(R.drawable.sem_tip_popup_hint_background_01);
                    if (isRTL()) {
                        this.mBubbleIcon.setImageResource(R.drawable.sem_tip_popup_hint_icon_rtl);
                    } else {
                        this.mBubbleIcon.setImageResource(R.drawable.sem_tip_popup_hint_icon);
                    }
                }
                break;
            case DIRECTION_BOTTOM_RIGHT:
                this.mBubblePopup.setPivot(0.0f, 0.0f);
                paramBubblePanel.gravity = 51;
                this.mBubblePopupX = this.mBubbleX;
                this.mBubblePopupY = this.mBubbleY;
                if (this.mMode == MODE_NORMAL) {
                    this.mBubbleBackground.setImageResource(R.drawable.sem_tip_popup_hint_background_02);
                    if (isRTL()) {
                        this.mBubbleIcon.setImageResource(R.drawable.sem_tip_popup_hint_icon_rtl);
                    } else {
                        this.mBubbleIcon.setImageResource(R.drawable.sem_tip_popup_hint_icon);
                    }
                } else {
                    this.mBubbleBackground.setRotationY(180.0f);
                }
                break;
        }
        this.mBubbleBackground.setLayoutParams(paramBubblePanel);
        this.mBubbleIcon.setLayoutParams(paramBubblePanel);
        this.mBubblePopup.setWidth(this.mBubbleWidth + (this.mScaleMargin * 2));
        this.mBubblePopup.setHeight(this.mBubbleHeight + (this.mScaleMargin * 2));
    }

    private void setBalloonPanel() {
        int scaleFactor;
        float f;
        FrameLayout.LayoutParams paramBalloonContent;
        float f2;
        if (this.mBalloonPopup != null) {
            debugLog("setBalloonPanel()");
            int i = this.mBubbleX;
            int i2 = this.mBalloonX;
            int leftMargin = i - i2;
            int rightMargin = (i2 + this.mBalloonWidth) - i;
            int i3 = this.mBubbleY;
            int i4 = this.mBalloonY;
            int topMargin = i3 - i4;
            int bottomMargin = (i4 + this.mBalloonHeight) - (i3 + this.mBubbleHeight);
            DisplayMetrics realMetrics = new DisplayMetrics();
            this.mWindowManager.getDefaultDisplay().getRealMetrics(realMetrics);
            int scaleFactor2 = (int) Math.ceil(realMetrics.density);
            int minBackgroundWidth = this.mResources.getDimensionPixelSize(R.dimen.sem_tip_popup_balloon_background_minwidth);
            debugLog("leftMargin[" + leftMargin + "]");
            debugLog("rightMargin[" + rightMargin + "] mBalloonWidth[" + this.mBalloonWidth + "]");
            int horizontalContentMargin = this.mHorizontalTextMargin - this.mResources.getDimensionPixelSize(R.dimen.sem_tip_popup_button_padding_horizontal);
            int verticalButtonPadding = this.mActionView.getVisibility() == View.VISIBLE ? this.mResources.getDimensionPixelSize(R.dimen.sem_tip_popup_button_padding_vertical) : 0;
            FrameLayout.LayoutParams paramBalloonBubble = (FrameLayout.LayoutParams) this.mBalloonBubble.getLayoutParams();
            FrameLayout.LayoutParams paramBalloonPanel = (FrameLayout.LayoutParams) this.mBalloonPanel.getLayoutParams();
            FrameLayout.LayoutParams paramBalloonContent2 = (FrameLayout.LayoutParams) this.mBalloonContent.getLayoutParams();
            FrameLayout.LayoutParams paramBalloonBg1 = (FrameLayout.LayoutParams) this.mBalloonBg1.getLayoutParams();
            FrameLayout.LayoutParams paramBalloonBg2 = (FrameLayout.LayoutParams) this.mBalloonBg2.getLayoutParams();
            if (this.mMode == MODE_TRANSLUCENT) {
                this.mBalloonBubbleHint.setImageResource(R.drawable.sem_tip_popup_hint_background_translucent);
                this.mBalloonBubbleHint.setImageTintList(null);
                if (isRTL()) {
                    this.mBalloonBubbleIcon.setImageResource(R.drawable.sem_tip_popup_hint_icon_translucent_rtl);
                } else {
                    this.mBalloonBubbleIcon.setImageResource(R.drawable.sem_tip_popup_hint_icon_translucent);
                }
                this.mBalloonBubbleIcon.setImageTintList(null);
                paramBalloonBubble.width = this.mResources.getDimensionPixelSize(R.dimen.sem_tip_popup_bubble_width_translucent);
                paramBalloonBubble.height = this.mResources.getDimensionPixelSize(R.dimen.sem_tip_popup_bubble_height_translucent);
                scaleFactor = 0;
            } else if (Color.alpha(this.mBackgroundColor) < 255) {
                debugLog("Updating scaleFactor to 0 because transparency is applied to background.");
                scaleFactor = 0;
            } else {
                scaleFactor = scaleFactor2;
            }
            switch (this.mArrowDirection) {
                case DIRECTION_TOP_LEFT:
                    TipWindow tipWindow = this.mBalloonPopup;
                    int i5 = this.mArrowPositionX - this.mBalloonX;
                    int i6 = this.mScaleMargin;
                    tipWindow.setPivot(i5 + i6, this.mBalloonHeight + i6);
                    if (this.mMode == MODE_NORMAL) {
                        this.mBalloonBubbleHint.setImageResource(R.drawable.sem_tip_popup_hint_background_03);
                        this.mBalloonBubbleIcon.setImageResource(R.drawable.sem_tip_popup_hint_icon);
                        f = 180.0f;
                    } else {
                        f = 180.0f;
                        this.mBalloonBubbleHint.setRotationX(180.0f);
                    }
                    this.mBalloonBg1.setRotationX(f);
                    this.mBalloonBg2.setRotationX(f);
                    paramBalloonBg2.gravity = 85;
                    paramBalloonBg1.gravity = 85;
                    paramBalloonBubble.gravity = 85;
                    int i7 = this.mBubbleWidth;
                    if (rightMargin - i7 < minBackgroundWidth) {
                        int scaledLeftMargin = this.mBalloonWidth - minBackgroundWidth;
                        paramBalloonBg1.setMargins(0, 0, minBackgroundWidth, 0);
                        paramBalloonBg2.setMargins(scaledLeftMargin - scaleFactor, 0, 0, 0);
                        debugLog("Right Margin is less then minimum background width!");
                        debugLog("updated !! leftMargin[" + scaledLeftMargin + "],  rightMargin[" + minBackgroundWidth + "]");
                    } else {
                        paramBalloonBg1.setMargins(0, 0, rightMargin - i7, 0);
                        paramBalloonBg2.setMargins((this.mBubbleWidth + leftMargin) - scaleFactor, 0, 0, 0);
                    }
                    int i8 = this.mVerticalTextMargin;
                    paramBalloonContent = paramBalloonContent2;
                    paramBalloonContent.setMargins(horizontalContentMargin, i8, horizontalContentMargin, (this.mArrowHeight + i8) - verticalButtonPadding);
                    break;
                case DIRECTION_TOP_RIGHT:
                    TipWindow tipWindow2 = this.mBalloonPopup;
                    int i9 = this.mArrowPositionX - this.mBalloonX;
                    int i10 = this.mScaleMargin;
                    tipWindow2.setPivot(i9 + i10, this.mBalloonHeight + i10);
                    if (this.mMode == MODE_NORMAL) {
                        this.mBalloonBubbleHint.setImageResource(R.drawable.sem_tip_popup_hint_background_04);
                        this.mBalloonBubbleIcon.setImageResource(R.drawable.sem_tip_popup_hint_icon);
                        f2 = 180.0f;
                    } else {
                        f2 = 180.0f;
                        this.mBalloonBubbleHint.setRotation(180.0f);
                    }
                    this.mBalloonBg1.setRotation(f2);
                    this.mBalloonBg2.setRotation(f2);
                    paramBalloonBg2.gravity = 83;
                    paramBalloonBg1.gravity = 83;
                    paramBalloonBubble.gravity = 83;
                    if (leftMargin < minBackgroundWidth) {
                        int scaledRightMargin = this.mBalloonWidth - minBackgroundWidth;
                        paramBalloonBg1.setMargins(minBackgroundWidth, 0, 0, 0);
                        paramBalloonBg2.setMargins(0, 0, scaledRightMargin - scaleFactor, 0);
                        debugLog("Left Margin is less then minimum background width!");
                        debugLog("updated !! leftMargin[" + minBackgroundWidth + "],  rightMargin[" + scaledRightMargin + "]");
                    } else {
                        paramBalloonBg1.setMargins(leftMargin, 0, 0, 0);
                        paramBalloonBg2.setMargins(0, 0, rightMargin - scaleFactor, 0);
                    }
                    int i11 = this.mVerticalTextMargin;
                    paramBalloonContent = paramBalloonContent2;
                    paramBalloonContent.setMargins(horizontalContentMargin, i11, horizontalContentMargin, (this.mArrowHeight + i11) - verticalButtonPadding);
                    break;
                case DIRECTION_BOTTOM_LEFT:
                    TipWindow tipWindow3 = this.mBalloonPopup;
                    int i12 = this.mArrowPositionX - this.mBalloonX;
                    int i13 = this.mScaleMargin;
                    tipWindow3.setPivot(i12 + i13, i13);
                    if (this.mMode == MODE_NORMAL) {
                        this.mBalloonBubbleHint.setImageResource(R.drawable.sem_tip_popup_hint_background_01);
                        this.mBalloonBubbleIcon.setImageResource(R.drawable.sem_tip_popup_hint_icon);
                    }
                    paramBalloonBg2.gravity = 53;
                    paramBalloonBg1.gravity = 53;
                    paramBalloonBubble.gravity = 53;
                    paramBalloonBg1.setMargins(0, 0, rightMargin - this.mBubbleWidth, 0);
                    paramBalloonBg2.setMargins((this.mBubbleWidth + leftMargin) - scaleFactor, 0, 0, 0);
                    int i14 = this.mArrowHeight;
                    int i15 = this.mVerticalTextMargin;
                    paramBalloonContent2.setMargins(horizontalContentMargin, i14 + i15, horizontalContentMargin, i15 - verticalButtonPadding);
                    paramBalloonContent = paramBalloonContent2;
                    break;
                case DIRECTION_BOTTOM_RIGHT:
                    TipWindow tipWindow4 = this.mBalloonPopup;
                    int i16 = this.mArrowPositionX - this.mBalloonX;
                    int i17 = this.mScaleMargin;
                    tipWindow4.setPivot(i16 + i17, i17);
                    if (this.mMode == MODE_NORMAL) {
                        this.mBalloonBubbleHint.setImageResource(R.drawable.sem_tip_popup_hint_background_02);
                        this.mBalloonBubbleIcon.setImageResource(R.drawable.sem_tip_popup_hint_icon);
                    } else {
                        this.mBalloonBubbleHint.setRotationY(180.0f);
                    }
                    this.mBalloonBg1.setRotationY(180.0f);
                    this.mBalloonBg2.setRotationY(180.0f);
                    paramBalloonBg2.gravity = 51;
                    paramBalloonBg1.gravity = 51;
                    paramBalloonBubble.gravity = 51;
                    paramBalloonBg1.setMargins(leftMargin, 0, 0, 0);
                    paramBalloonBg2.setMargins(0, 0, rightMargin - scaleFactor, 0);
                    int i18 = this.mArrowHeight;
                    int i19 = this.mVerticalTextMargin;
                    paramBalloonContent2.setMargins(horizontalContentMargin, i18 + i19, horizontalContentMargin, i19 - verticalButtonPadding);
                    paramBalloonContent = paramBalloonContent2;
                    break;
                default:
                    paramBalloonContent = paramBalloonContent2;
                    break;
            }
            int i20 = this.mScaleMargin;
            paramBalloonBubble.setMargins(leftMargin + i20, topMargin + i20, (rightMargin - this.mBubbleWidth) + i20, bottomMargin + i20);
            int balloonPanelMargin = this.mScaleMargin;
            paramBalloonPanel.setMargins(balloonPanelMargin, balloonPanelMargin, balloonPanelMargin, balloonPanelMargin);
            int i21 = this.mBalloonX;
            int i22 = this.mScaleMargin;
            this.mBalloonPopupX = i21 - i22;
            this.mBalloonPopupY = this.mBalloonY - i22;
            this.mBalloonBubble.setLayoutParams(paramBalloonBubble);
            this.mBalloonPanel.setLayoutParams(paramBalloonPanel);
            this.mBalloonBg1.setLayoutParams(paramBalloonBg1);
            this.mBalloonBg2.setLayoutParams(paramBalloonBg2);
            this.mBalloonContent.setLayoutParams(paramBalloonContent);
            this.mBalloonPopup.setWidth(this.mBalloonWidth + (this.mScaleMargin * 2));
            this.mBalloonPopup.setHeight(this.mBalloonHeight + (this.mScaleMargin * 2));
        }
    }

    private void calculateArrowDirection(int arrowX, int arrowY) {
        View view = this.mParentView;
        if (view != null && this.mIsDefaultPosition) {
            int[] location = new int[2];
            view.getLocationInWindow(location);
            int parentY = location[1] + (this.mParentView.getHeight() / 2);
            if (arrowX * 2 <= this.mDisplayMetrics.widthPixels) {
                if (arrowY <= parentY) {
                    this.mArrowDirection = DIRECTION_TOP_RIGHT;
                } else {
                    this.mArrowDirection = DIRECTION_BOTTOM_RIGHT;
                }
            } else if (arrowY <= parentY) {
                this.mArrowDirection = DIRECTION_TOP_LEFT;
            } else {
                this.mArrowDirection = DIRECTION_BOTTOM_LEFT;
            }
        } else if (arrowX * 2 <= this.mDisplayMetrics.widthPixels && arrowY * 2 <= this.mDisplayMetrics.heightPixels) {
            this.mArrowDirection = DIRECTION_BOTTOM_RIGHT;
        } else if (arrowX * 2 > this.mDisplayMetrics.widthPixels && arrowY * 2 <= this.mDisplayMetrics.heightPixels) {
            this.mArrowDirection = DIRECTION_TOP_LEFT;
        } else if (arrowX * 2 <= this.mDisplayMetrics.widthPixels && arrowY * 2 > this.mDisplayMetrics.heightPixels) {
            this.mArrowDirection = DIRECTION_TOP_RIGHT;
        } else if (arrowX * 2 > this.mDisplayMetrics.widthPixels && arrowY * 2 > this.mDisplayMetrics.heightPixels) {
            this.mArrowDirection = DIRECTION_TOP_LEFT;
        }
        debugLog("calculateArrowDirection : arrow position (" + arrowX + ", " + arrowY + ") / mArrowDirection = " + this.mArrowDirection);
    }

    private void calculateArrowPosition() {
        View view = this.mParentView;
        if (view == null) {
            this.mArrowPositionX = 0;
            this.mArrowPositionY = 0;
            return;
        }
        int[] location = new int[2];
        view.getLocationInWindow(location);
        debugLog("calculateArrowPosition anchor location : " + location[0] + ", " + location[1]);
        int x = location[0] + (this.mParentView.getWidth() / 2);
        int y = location[1] + (this.mParentView.getHeight() / 2);
        if (y * 2 <= this.mDisplayMetrics.heightPixels) {
            this.mArrowPositionY = (this.mParentView.getHeight() / 2) + y;
        } else {
            this.mArrowPositionY = y - (this.mParentView.getHeight() / 2);
        }
        this.mArrowPositionX = x;
        debugLog("calculateArrowPosition mArrowPosition : " + this.mArrowPositionX + ", " + this.mArrowPositionY);
    }

    @SuppressLint("RestrictedApi")
    private void calculatePopupSize() {
        int balloonMaxWidth;
        this.mDisplayMetrics = this.mResources.getDisplayMetrics();
        int screenWidthDp = this.mResources.getConfiguration().screenWidthDp;
        int balloonMinWidth = this.mArrowWidth + (this.mHorizontalTextMargin * 2);
        if (SeslConfigurationReflector.isDexEnabled(mContext.getResources().getConfiguration())) {
            int windowWidthInDexMode = this.mParentView.getRootView().getMeasuredWidth();
            int[] windowLocation = new int[2];
            this.mParentView.getRootView().getLocationOnScreen(windowLocation);
            if (windowLocation[0] < 0) {
                windowWidthInDexMode += windowLocation[0];
            }
            debugLog("Window width in DexMode " + windowWidthInDexMode);
            if (windowWidthInDexMode <= 480) {
                balloonMaxWidth = (int) (windowWidthInDexMode * 0.83f);
            } else if (windowWidthInDexMode <= 960) {
                balloonMaxWidth = (int) (windowWidthInDexMode * 0.6f);
            } else if (windowWidthInDexMode <= 1280) {
                balloonMaxWidth = (int) (windowWidthInDexMode * 0.45f);
            } else {
                balloonMaxWidth = (int) (windowWidthInDexMode * 0.25f);
            }
        } else {
            debugLog("screen width DP " + screenWidthDp);
            if (screenWidthDp <= 480) {
                balloonMaxWidth = (int) (this.mDisplayMetrics.widthPixels * 0.83f);
            } else if (screenWidthDp <= 960) {
                balloonMaxWidth = (int) (this.mDisplayMetrics.widthPixels * 0.6f);
            } else if (screenWidthDp <= 1280) {
                balloonMaxWidth = (int) (this.mDisplayMetrics.widthPixels * 0.45f);
            } else {
                balloonMaxWidth = (int) (this.mDisplayMetrics.widthPixels * 0.25f);
            }
        }
        if (!this.mIsMessageViewMeasured) {
            this.mMessageView.measure(0, 0);
            this.mInitialmMessageViewWidth = this.mMessageView.getMeasuredWidth();
            this.mIsMessageViewMeasured = true;
        }
        int i = this.mInitialmMessageViewWidth;
        int i2 = this.mHorizontalTextMargin;
        int balloonWidth = i + (i2 * 2);
        if (balloonWidth < balloonMinWidth) {
            balloonWidth = balloonMinWidth;
        } else if (balloonWidth > balloonMaxWidth) {
            balloonWidth = balloonMaxWidth;
        }
        this.mBalloonWidth = balloonWidth;
        this.mMessageView.setWidth(balloonWidth - (i2 * 2));
        this.mMessageView.measure(0, 0);
        this.mBalloonHeight = this.mMessageView.getMeasuredHeight() + (this.mVerticalTextMargin * 2) + this.mArrowHeight;
        if (this.mType == TYPE_BALLOON_ACTION) {
            this.mActionView.measure(0, 0);
            if (this.mBalloonWidth < this.mActionView.getMeasuredWidth()) {
                this.mBalloonWidth = this.mActionView.getMeasuredWidth() + (this.mResources.getDimensionPixelSize(R.dimen.sem_tip_popup_button_padding_horizontal) * 2);
            }
            this.mBalloonHeight += this.mActionView.getMeasuredHeight() - this.mResources.getDimensionPixelSize(R.dimen.sem_tip_popup_button_padding_vertical);
        }
    }

    @SuppressLint("RestrictedApi")
    private void calculatePopupPosition() {
        getDisplayFrame(this.mDisplayFrame);
        if (this.mBalloonX < 0) {
            int i = this.mArrowDirection;
            if (i == 3 || i == 1) {
                this.mBalloonX = (this.mArrowPositionX + this.mArrowWidth) - (this.mBalloonWidth / 2);
            } else {
                this.mBalloonX = (this.mArrowPositionX - this.mArrowWidth) - (this.mBalloonWidth / 2);
            }
        }
        int i2 = this.mArrowDirection;
        if (i2 == 3 || i2 == 1) {
            if (this.mArrowPositionX < this.mDisplayFrame.left + this.mSideMargin + this.mHorizontalTextMargin) {
                debugLog("Target position is too far to the left!");
                this.mArrowPositionX = this.mDisplayFrame.left + this.mSideMargin + this.mHorizontalTextMargin;
            } else if (this.mArrowPositionX > ((this.mDisplayFrame.right - this.mSideMargin) - this.mHorizontalTextMargin) - this.mArrowWidth) {
                debugLog("Target position is too far to the right!");
                this.mArrowPositionX = ((this.mDisplayFrame.right - this.mSideMargin) - this.mHorizontalTextMargin) - this.mArrowWidth;
            }
        } else if (this.mArrowPositionX < this.mDisplayFrame.left + this.mSideMargin + this.mHorizontalTextMargin + this.mArrowWidth) {
            debugLog("Target position is too far to the left!");
            this.mArrowPositionX = this.mDisplayFrame.left + this.mSideMargin + this.mHorizontalTextMargin + this.mArrowWidth;
        } else if (this.mArrowPositionX > (this.mDisplayFrame.right - this.mSideMargin) - this.mHorizontalTextMargin) {
            debugLog("Target position is too far to the right!");
            this.mArrowPositionX = (this.mDisplayFrame.right - this.mSideMargin) - this.mHorizontalTextMargin;
        }
        if (SeslConfigurationReflector.isDexEnabled(mContext.getResources().getConfiguration())) {
            int windowWidthInDexMode = this.mParentView.getRootView().getMeasuredWidth();
            int[] windowLocation = new int[2];
            this.mParentView.getRootView().getLocationOnScreen(windowLocation);
            if (windowLocation[0] < 0) {
                windowWidthInDexMode += windowLocation[0];
            }
            int i3 = this.mBalloonX;
            int i4 = this.mDisplayFrame.left;
            int i5 = this.mSideMargin;
            if (i3 < i4 + i5) {
                this.mBalloonX = this.mDisplayFrame.left + this.mSideMargin;
            } else {
                int i6 = this.mBalloonX;
                int i7 = this.mBalloonWidth;
                if (i6 + i7 > windowWidthInDexMode - i5) {
                    int i8 = (windowWidthInDexMode - i5) - i7;
                    this.mBalloonX = i8;
                    if (windowLocation[0] < 0) {
                        this.mBalloonX = i8 - windowLocation[0];
                    }
                }
            }
        } else if (this.mBalloonX < this.mDisplayFrame.left + this.mSideMargin) {
            this.mBalloonX = this.mDisplayFrame.left + this.mSideMargin;
        } else if (this.mBalloonX + this.mBalloonWidth > this.mDisplayFrame.right - this.mSideMargin) {
            this.mBalloonX = (this.mDisplayFrame.right - this.mSideMargin) - this.mBalloonWidth;
        }
        switch (this.mArrowDirection) {
            case DIRECTION_TOP_LEFT:
                this.mBubbleX = this.mArrowPositionX - this.mBubbleWidth;
                int i9 = this.mArrowPositionY;
                this.mBubbleY = i9 - this.mBubbleHeight;
                this.mBalloonY = i9 - this.mBalloonHeight;
                break;
            case DIRECTION_TOP_RIGHT:
                this.mBubbleX = this.mArrowPositionX;
                int i10 = this.mArrowPositionY;
                this.mBubbleY = i10 - this.mBubbleHeight;
                this.mBalloonY = i10 - this.mBalloonHeight;
                break;
            case DIRECTION_BOTTOM_LEFT:
                this.mBubbleX = this.mArrowPositionX - this.mBubbleWidth;
                int i11 = this.mArrowPositionY;
                this.mBubbleY = i11;
                this.mBalloonY = i11;
                break;
            case DIRECTION_BOTTOM_RIGHT:
                this.mBubbleX = this.mArrowPositionX;
                int i12 = this.mArrowPositionY;
                this.mBubbleY = i12;
                this.mBalloonY = i12;
                break;
        }
        debugLog("QuestionPopup : " + this.mBubbleX + ", " + this.mBubbleY + ", " + this.mBubbleWidth + ", " + this.mBubbleHeight);
        debugLog("BalloonPopup : " + this.mBalloonX + ", " + this.mBalloonY + ", " + this.mBalloonWidth + ", " + this.mBalloonHeight);
    }

    public void dismissBubble(boolean withAnimation) {
        TipWindow tipWindow = this.mBubblePopup;
        if (tipWindow != null) {
            tipWindow.setUseDismissAnimation(withAnimation);
            this.mBubblePopup.dismiss();
        }
        OnDismissListener onDismissListener = this.mOnDismissListener;
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
        }
    }

    public void scheduleTimeout() {
        if (mHandler != null) {
            mHandler.removeMessages(0);
            mHandler.sendMessageDelayed(Message.obtain(mHandler, MSG_TIMEOUT), TIMEOUT_DURATION_MS);
        }
    }

    private void animateViewIn() {
        float pivotX = 0.0f;
        float pivotY = 0.0f;
        switch (this.mArrowDirection) {
            case DIRECTION_TOP_LEFT:
                pivotX = 1.0f;
                pivotY = 1.0f;
                break;
            case DIRECTION_TOP_RIGHT:
                pivotX = 0.0f;
                pivotY = 1.0f;
                break;
            case DIRECTION_BOTTOM_LEFT:
                pivotX = 1.0f;
                pivotY = 0.0f;
                break;
            case DIRECTION_BOTTOM_RIGHT:
                pivotX = 0.0f;
                pivotY = 0.0f;
                break;
        }
        Animation animScale = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, 1, pivotX, 1, pivotY);
        animScale.setInterpolator(INTERPOLATOR_ELASTIC_50);
        animScale.setDuration(ANIMATION_DURATION_EXPAND_SCALE);
        animScale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                TipPopup.this.scheduleTimeout();
                TipPopup.this.animateBounce();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        this.mBubbleView.startAnimation(animScale);
    }

    public void animateBounce() {
        float pivotX = 0.0f;
        float pivotY = 0.0f;
        switch (this.mArrowDirection) {
            case DIRECTION_TOP_LEFT:
                pivotX = this.mBubblePopup.getWidth();
                pivotY = this.mBubblePopup.getHeight();
                break;
            case DIRECTION_TOP_RIGHT:
                pivotX = 0.0f;
                pivotY = this.mBubblePopup.getHeight();
                break;
            case DIRECTION_BOTTOM_LEFT:
                pivotX = this.mBubblePopup.getWidth();
                pivotY = 0.0f;
                break;
            case DIRECTION_BOTTOM_RIGHT:
                pivotX = 0.0f;
                pivotY = 0.0f;
                break;
        }
        final AnimationSet animationSet = new AnimationSet(false);
        float f = pivotX;
        float f2 = pivotY;
        Animation anim1 = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f, 0, f, 0, f2);
        anim1.setDuration(ANIMATION_DURATION_BOUNCE_SCALE1);
        anim1.setInterpolator(INTERPOLATOR_SINE_IN_OUT_70);
        Animation anim2 = new ScaleAnimation(1.0f, 0.833f, 1.0f, 0.833f, 0, f, 0, f2);
        anim2.setStartOffset(ANIMATION_DURATION_BOUNCE_SCALE1);
        anim2.setDuration(ANIMATION_DURATION_BOUNCE_SCALE2);
        anim2.setInterpolator(INTERPOLATOR_SINE_IN_OUT_33);
        anim2.setAnimationListener(new Animation.AnimationListener() {
            int count = 0;

            @Override
            public void onAnimationStart(Animation animation) {
                this.count++;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                TipPopup.this.debugLog("repeat count " + this.count);
                TipPopup.this.mBubbleView.startAnimation(animationSet);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        animationSet.addAnimation(anim1);
        animationSet.addAnimation(anim2);
        animationSet.setStartOffset(ANIMATION_OFFSET_BOUNCE_SCALE);
        this.mBubbleView.startAnimation(animationSet);
    }

    public void animateScaleUp() {
        float deltaHintY = 0.0f;
        float pivotHintX = 0.0f;
        float pivotHintY = 0.0f;
        float pivotPanelX = 0.0f;
        float pivotPanelY = 0.0f;
        int questionHeight = this.mResources.getDimensionPixelSize(R.dimen.sem_tip_popup_bubble_height);
        float panelScale = questionHeight / this.mBalloonHeight;
        switch (this.mArrowDirection) {
            case DIRECTION_TOP_LEFT:
                pivotHintX = this.mBalloonBubble.getWidth();
                pivotHintY = this.mBalloonBubble.getHeight();
                pivotPanelX = this.mArrowPositionX - this.mBalloonX;
                pivotPanelY = this.mBalloonHeight;
                deltaHintY = 0.0f - (this.mArrowHeight / 2.0f);
                break;
            case DIRECTION_TOP_RIGHT:
                pivotHintX = 0.0f;
                pivotHintY = this.mBalloonBubble.getHeight();
                pivotPanelX = this.mArrowPositionX - this.mBalloonX;
                pivotPanelY = this.mBalloonHeight;
                deltaHintY = 0.0f - (this.mArrowHeight / 2.0f);
                break;
            case DIRECTION_BOTTOM_LEFT:
                pivotHintX = this.mBalloonBubble.getWidth();
                pivotHintY = 0.0f;
                pivotPanelX = this.mArrowPositionX - this.mBalloonX;
                pivotPanelY = 0.0f;
                deltaHintY = this.mArrowHeight / 2.0f;
                break;
            case DIRECTION_BOTTOM_RIGHT:
                pivotHintX = 0.0f;
                pivotHintY = 0.0f;
                pivotPanelX = this.mBubbleX - this.mBalloonX;
                pivotPanelY = 0.0f;
                deltaHintY = this.mArrowHeight / 2.0f;
                break;
        }
        AnimationSet animationBubble = new AnimationSet(false);
        TranslateAnimation animationBubbleMove = new TranslateAnimation(0, 0.0f, 0, 0.0f, 0, 0.0f, 0, deltaHintY);
        animationBubbleMove.setDuration(500L);
        animationBubbleMove.setInterpolator(INTERPOLATOR_ELASTIC_CUSTOM);
        Animation animationBubbleScale = new ScaleAnimation(1.0f, 1.7f, 1.0f, 1.7f, 0, pivotHintX, 0, pivotHintY);
        animationBubbleScale.setDuration(500L);
        animationBubbleScale.setInterpolator(INTERPOLATOR_ELASTIC_CUSTOM);
        Animation animationBubbleAlpha = new AlphaAnimation(1.0f, 0.0f);
        animationBubbleAlpha.setDuration(166L);
        animationBubbleAlpha.setInterpolator(INTERPOLATOR_SINE_IN_OUT_33);
        animationBubble.addAnimation(animationBubbleMove);
        animationBubble.addAnimation(animationBubbleScale);
        animationBubble.addAnimation(animationBubbleAlpha);
        animationBubble.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                TipPopup.this.mBalloonPanel.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                TipPopup.this.mBalloonBubble.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        this.mBalloonBubble.startAnimation(animationBubble);
        AnimationSet animationPanel = new AnimationSet(false);
        Animation animationPanelScale = new ScaleAnimation(0.27f, 1.0f, panelScale, 1.0f, 0, pivotPanelX, 0, pivotPanelY);
        animationPanelScale.setInterpolator(INTERPOLATOR_ELASTIC_CUSTOM);
        animationPanelScale.setDuration(ANIMATION_DURATION_SHOW_SCALE);
        Animation animationPanelAlpha = new AlphaAnimation(0.0f, 1.0f);
        animationPanelAlpha.setInterpolator(INTERPOLATOR_SINE_IN_OUT_33);
        animationPanelAlpha.setDuration(ANIMATION_DURATION_EXPAND_ALPHA);
        animationPanel.addAnimation(animationPanelScale);
        animationPanel.addAnimation(animationPanelAlpha);
        this.mBalloonPanel.startAnimation(animationPanel);
        Animation animationText = new AlphaAnimation(0.0f, 1.0f);
        animationText.setInterpolator(INTERPOLATOR_SINE_IN_OUT_33);
        animationText.setStartOffset(ANIMATION_OFFSET_EXPAND_TEXT);
        animationText.setDuration(ANIMATION_DURATION_EXPAND_TEXT);
        animationText.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                TipPopup.this.mMessageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                TipPopup.this.dismissBubble(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        this.mMessageView.startAnimation(animationText);
        this.mActionView.startAnimation(animationText);
    }

    private boolean isNavigationbarHide() {
        Context context = this.mContext;
        return context != null && Settings.Global.getInt(context.getContentResolver(), "navigationbar_hide_bar_enabled", 0) == 1;
    }

    private int getNavagationbarHeight() {
        int resourceId = this.mResources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return this.mResources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private boolean isTablet() {
        DisplayMetrics realMetrics = new DisplayMetrics();
        this.mWindowManager.getDefaultDisplay().getRealMetrics(realMetrics);
        int shortSize = realMetrics.widthPixels > realMetrics.heightPixels ? realMetrics.heightPixels : realMetrics.widthPixels;
        int shortSizeDp = (shortSize * 160) / realMetrics.densityDpi;
        debugLog("short size dp  = " + shortSizeDp);
        return shortSizeDp >= 600;
    }

    private void getDisplayFrame(Rect screenRect) {
        DisplayCutout displayCutout;
        int navigationbarHeight = getNavagationbarHeight();
        boolean navigationbarHide = isNavigationbarHide();
        int displayRotation = this.mWindowManager.getDefaultDisplay().getRotation();
        DisplayMetrics realMetrics = new DisplayMetrics();
        this.mWindowManager.getDefaultDisplay().getRealMetrics(realMetrics);
        debugLog("realMetrics = " + realMetrics);
        debugLog("is tablet? = " + isTablet());
        if (this.mForceRealDisplay) {
            screenRect.left = 0;
            screenRect.top = 0;
            screenRect.right = realMetrics.widthPixels;
            screenRect.bottom = realMetrics.heightPixels;
            debugLog("Screen Rect = " + screenRect + " mForceRealDisplay = " + this.mForceRealDisplay);
            return;
        }
        screenRect.left = 0;
        screenRect.top = 0;
        screenRect.right = this.mDisplayMetrics.widthPixels;
        screenRect.bottom = this.mDisplayMetrics.heightPixels;
        Rect bounds = new Rect();
        this.mParentView.getRootView().getWindowVisibleDisplayFrame(bounds);
        debugLog("Bounds = " + bounds);
        if (isTablet()) {
            debugLog("tablet");
            if (realMetrics.widthPixels == this.mDisplayMetrics.widthPixels && realMetrics.heightPixels - this.mDisplayMetrics.heightPixels == navigationbarHeight && navigationbarHide) {
                screenRect.bottom += navigationbarHeight;
            }
        } else {
            debugLog("phone");
            switch (displayRotation) {
                case Surface.ROTATION_0:
                    if (realMetrics.widthPixels == this.mDisplayMetrics.widthPixels && realMetrics.heightPixels - this.mDisplayMetrics.heightPixels == navigationbarHeight && navigationbarHide) {
                        screenRect.bottom += navigationbarHeight;
                        break;
                    }
                    break;
                case Surface.ROTATION_90:
                    if (realMetrics.heightPixels == this.mDisplayMetrics.heightPixels && realMetrics.widthPixels - this.mDisplayMetrics.widthPixels == navigationbarHeight && navigationbarHide) {
                        screenRect.right += navigationbarHeight;
                    }
                    WindowInsets windowInsets = this.mParentView.getRootWindowInsets();
                    if (windowInsets != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && (displayCutout = windowInsets.getDisplayCutout()) != null) {
                        screenRect.left += displayCutout.getSafeInsetLeft();
                        screenRect.right += displayCutout.getSafeInsetLeft();
                        debugLog("displayCutout.getSafeInsetLeft() :  " + displayCutout.getSafeInsetLeft());
                        break;
                    }
                    break;
                case Surface.ROTATION_180:
                    if (realMetrics.widthPixels == this.mDisplayMetrics.widthPixels && realMetrics.heightPixels - this.mDisplayMetrics.heightPixels == navigationbarHeight) {
                        if (navigationbarHide) {
                            screenRect.bottom += navigationbarHeight;
                            break;
                        } else {
                            screenRect.top += navigationbarHeight;
                            screenRect.bottom += navigationbarHeight;
                            break;
                        }
                    } else if (realMetrics.widthPixels == this.mDisplayMetrics.widthPixels && bounds.top == navigationbarHeight) {
                        debugLog("Top Docked");
                        screenRect.top += navigationbarHeight;
                        screenRect.bottom += navigationbarHeight;
                        break;
                    }
                    break;
                case Surface.ROTATION_270:
                    if (realMetrics.heightPixels == this.mDisplayMetrics.heightPixels && realMetrics.widthPixels - this.mDisplayMetrics.widthPixels == navigationbarHeight) {
                        if (navigationbarHide) {
                            screenRect.right += navigationbarHeight;
                            break;
                        } else {
                            screenRect.left += navigationbarHeight;
                            screenRect.right += navigationbarHeight;
                            break;
                        }
                    } else if (realMetrics.heightPixels == this.mDisplayMetrics.heightPixels && bounds.left == navigationbarHeight) {
                        debugLog("Left Docked");
                        screenRect.left += navigationbarHeight;
                        screenRect.right += navigationbarHeight;
                        break;
                    }
                    break;
            }
        }
        debugLog("Screen Rect = " + screenRect);
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
    }

    public static class TipWindow extends PopupWindow {
        private boolean mIsDismissing;
        private boolean mIsUsingDismissAnimation;
        private float mPivotX;
        private float mPivotY;

        private TipWindow(View contentView, int width, int height, boolean focusable) {
            super(contentView, width, height, focusable);
            this.mIsUsingDismissAnimation = true;
            this.mIsDismissing = false;
            this.mPivotX = 0.0f;
            this.mPivotY = 0.0f;
        }

        public void setUseDismissAnimation(boolean useAnimation) {
            this.mIsUsingDismissAnimation = useAnimation;
        }

        public void setPivot(float pivotX, float pivotY) {
            this.mPivotX = pivotX;
            this.mPivotY = pivotY;
        }

        @Override
        public void dismiss() {
            if (this.mIsUsingDismissAnimation && !this.mIsDismissing) {
                animateViewOut();
            } else {
                super.dismiss();
            }
        }

        private void animateViewOut() {
            AnimationSet animationSet = new AnimationSet(true);
            Animation animScale = new ScaleAnimation(1.0f, 0.81f, 1.0f, 0.81f, 0, this.mPivotX, 0, this.mPivotY);
            animScale.setInterpolator(TipPopup.INTERPOLATOR_ELASTIC_CUSTOM);
            animScale.setDuration(ANIMATION_DURATION_DISMISS_SCALE);
            Animation animAlpha = new AlphaAnimation(1.0f, 0.0f);
            animAlpha.setInterpolator(TipPopup.INTERPOLATOR_SINE_IN_OUT_33);
            animAlpha.setDuration(ANIMATION_DURATION_DISMISS_ALPHA);
            animationSet.addAnimation(animScale);
            animationSet.addAnimation(animAlpha);
            animationSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    TipWindow.this.mIsDismissing = true;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    TipWindow.super.dismiss();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            getContentView().startAnimation(animationSet);
        }
    }

    private boolean isRTL() {
        boolean rtl = this.mContext.getResources().getConfiguration().getLayoutDirection() == 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return rtl && !this.mContext.getResources().getConfiguration().getLocales().get(0).toString().equals("iw_IL");
        } else {
            return rtl;
        }
    }

    public void debugLog(String msg) {
        if (localLOGD) {
            Log.d(TAG, " #### " + msg);
        }
    }

    public PopupWindow semGetBubblePopupWindow() {
        return this.mBubblePopup;
    }

    public PopupWindow semGetBalloonPopupWindow() {
        return this.mBalloonPopup;
    }
}