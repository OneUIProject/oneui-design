package dev.oneuiproject.oneui.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import dev.oneuiproject.oneui.R;

public class SplashLayout extends LinearLayout {

    private boolean animated;
    private Drawable mImage_foreground;
    private Drawable mImage_background;
    private String mText;
    private Animation splash_anim;
    private AppCompatTextView textView;
    private AppCompatImageView imageview;
    private AppCompatImageView imageview_foreground;
    private AppCompatImageView imageview_background;
    private Drawable mImage;

    public SplashLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray attr = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SplashLayout, 0, 0);

        try {
            animated = attr.getBoolean(R.styleable.SplashLayout_animated, true);
            mText = attr.getString(R.styleable.SplashLayout_title);

            if (animated) {
                mImage_foreground = attr.getDrawable(R.styleable.SplashLayout_foreground_image);
                mImage_background = attr.getDrawable(R.styleable.SplashLayout_background_image);
                splash_anim = AnimationUtils.loadAnimation(context, attr.getResourceId(R.styleable.SplashLayout_animation, R.anim.oui_splash_animation));
            } else {
                mImage = attr.getDrawable(R.styleable.SplashLayout_image);
            }

        } finally {
            attr.recycle();
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(animated ? R.layout.oui_layout_splash_animated : R.layout.oui_layout_splash_simple, this, true);


        textView = findViewById(R.id.oui_splash_text);
        textView.setText(mText);

        if (animated) {
            imageview_foreground = findViewById(R.id.oui_splash_image_foreground);
            imageview_background = findViewById(R.id.oui_splash_image_background);

            imageview_foreground.setImageDrawable(mImage_foreground);
            imageview_background.setImageDrawable(mImage_background);
        } else {
            imageview = findViewById(R.id.oui_splash_image);
            imageview.setImageDrawable(mImage);
        }


    }

    public void setSplashAnimationListener(Animation.AnimationListener listener) {
        if (animated) splash_anim.setAnimationListener(listener);
    }

    public void startSplashAnimation() {
        if (animated) imageview_foreground.startAnimation(splash_anim);
    }

    public void clearSplashAnimation() {
        if (animated) imageview_foreground.clearAnimation();
    }

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
        textView.setText(mText);
    }

    public void setImage(Drawable foreground, Drawable background) {
        if (animated) {
            this.mImage_foreground = foreground;
            this.mImage_background = background;
            imageview_foreground.setImageDrawable(foreground);
            imageview_background.setImageDrawable(background);
        }
    }

    public void setImage(Drawable image) {
        if (!animated) {
            this.mImage = image;
            imageview.setImageDrawable(image);
        }
    }
}
