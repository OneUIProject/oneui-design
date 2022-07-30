package dev.oneuiproject.oneuiexample.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;

import com.google.android.material.appbar.AppBarLayout;
import com.sec.sesl.tester.BuildConfig;
import com.sec.sesl.tester.R;
import com.sec.sesl.tester.databinding.ActivityAboutBinding;
import com.sec.sesl.tester.databinding.ActivityAboutContentBinding;

import dev.oneuiproject.oneui.utils.ViewUtils;
import dev.oneuiproject.oneui.utils.internal.ToolbarLayoutUtils;
import dev.oneuiproject.oneui.widget.Toast;

public class AboutActivity extends AppCompatActivity
        implements View.OnClickListener {
    @ColorInt
    private final static int SEM_PRIMARY_COLOR = -16547330; // #0381fe

    private boolean mEnableBackToHeader;
    private long mLastClickTime;

    private ActivityAboutBinding mBinding;
    private ActivityAboutContentBinding mBottomContent;

    private AboutAppBarListener mAppBarListener = new AboutAppBarListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mBottomContent = mBinding.aboutBottomContent;

        setSupportActionBar(mBinding.aboutToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mBinding.aboutToolbar.setNavigationOnClickListener(v -> onBackPressed());

        resetAppBar(getResources().getConfiguration());
        initContent();
    }

    @Override
    public void onBackPressed() {
        if (mEnableBackToHeader && mBinding.aboutAppBar.seslIsCollapsed()) {
            mBinding.aboutAppBar.setExpanded(true);
        } else {
            // Fix O memory leak
            if (Build.VERSION.SDK_INT
                    == Build.VERSION_CODES.O && isTaskRoot()) {
                finishAfterTransition();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        resetAppBar(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sample3_menu_about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_app_info) {
            Intent intent = new Intent(
                    "android.settings.APPLICATION_DETAILS_SETTINGS",
                    Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));
            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    public boolean isInMultiWindowMode() {
        return Build.VERSION.SDK_INT >= 24 && super.isInMultiWindowMode();
    }

    private void resetAppBar(Configuration config) {
        ToolbarLayoutUtils.hideStatusBarForLandscape(this, config.orientation);
        ToolbarLayoutUtils.updateListBothSideMargin(this,
                mBinding.aboutBottomContainer);

        if (config.orientation != Configuration.ORIENTATION_LANDSCAPE
                && !isInMultiWindowMode()) {
            mBinding.aboutAppBar.seslSetCustomHeightProportion(true, 0.5f);
            mEnableBackToHeader = true;
            mBinding.aboutAppBar.addOnOffsetChangedListener(mAppBarListener);
            mBinding.aboutAppBar.setExpanded(true, false);
            mBinding.aboutSwipeUpContainer.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams lp = mBinding.aboutSwipeUpContainer.getLayoutParams();
            lp.height = getResources().getDisplayMetrics().heightPixels / 2;
        } else {
            mBinding.aboutAppBar.setExpanded(false, false);
            mEnableBackToHeader = false;
            mBinding.aboutAppBar.seslSetCustomHeightProportion(true, 0);
            mBinding.aboutAppBar.removeOnOffsetChangedListener(mAppBarListener);
            mBinding.aboutBottomContainer.setAlpha(1f);
            setBottomContentEnabled(true);
            mBinding.aboutSwipeUpContainer.setVisibility(View.GONE);
        }
    }

    private void initContent() {
        ViewUtils.semSetRoundedCorners(
                mBinding.aboutBottomContent.getRoot(),
                ViewUtils.SEM_ROUNDED_CORNER_TOP_LEFT | ViewUtils.SEM_ROUNDED_CORNER_TOP_RIGHT);
        ViewUtils.semSetRoundedCornerColor(mBinding.aboutBottomContent.getRoot(),
                ViewUtils.SEM_ROUNDED_CORNER_TOP_LEFT | ViewUtils.SEM_ROUNDED_CORNER_TOP_RIGHT,
                getColor(R.color.oui_round_and_bgcolor));

        Drawable appIcon = getThemedIcon();
        mBinding.aboutHeaderAppIcon.setImageDrawable(appIcon);
        mBinding.aboutBottomAppIcon.setImageDrawable(appIcon);

        mBinding.aboutHeaderAppVersion.setText("Version " + BuildConfig.VERSION_NAME);
        mBinding.aboutBottomAppVersion.setText("Version " + BuildConfig.VERSION_NAME);

        mBinding.aboutHeaderGithub.setOnClickListener(this);
        TooltipCompat.setTooltipText(mBinding.aboutHeaderGithub, "GitHub");
        mBinding.aboutHeaderTelegram.setOnClickListener(this);
        TooltipCompat.setTooltipText(mBinding.aboutHeaderTelegram, "Telegram");

        mBottomContent.aboutBottomDevYann.setOnClickListener(this);
        mBottomContent.aboutBottomDevMesa.setOnClickListener(this);

        mBottomContent.aboutBottomOssApache.setOnClickListener(this);
        mBottomContent.aboutBottomOssMit.setOnClickListener(this);

        mBottomContent.aboutBottomRelativeJetpack.setOnClickListener(this);
        mBottomContent.aboutBottomRelativeMaterial.setOnClickListener(this);
        mBottomContent.aboutBottomRelativeOuip.setOnClickListener(this);
    }

    private void setBottomContentEnabled(boolean enabled) {
        mBinding.aboutHeaderGithub.setEnabled(!enabled);
        mBinding.aboutHeaderTelegram.setEnabled(!enabled);
        mBottomContent.aboutBottomDevYann.setEnabled(enabled);
        mBottomContent.aboutBottomDevMesa.setEnabled(enabled);
        mBottomContent.aboutBottomOssApache.setEnabled(enabled);
        mBottomContent.aboutBottomOssMit.setEnabled(enabled);
        mBottomContent.aboutBottomRelativeJetpack.setEnabled(enabled);
        mBottomContent.aboutBottomRelativeMaterial.setEnabled(enabled);
        mBottomContent.aboutBottomRelativeOuip.setEnabled(enabled);
    }

    @Override
    public void onClick(View v) {
        long uptimeMillis = SystemClock.uptimeMillis();
        if (uptimeMillis - mLastClickTime > 600L) {
            String url = null;
            if (v.getId() == mBinding.aboutHeaderGithub.getId()) {
                url = "https://github.com/OneUIProject/oneui-design";
            } else if (v.getId() == mBinding.aboutHeaderTelegram.getId()) {
                url = "https://t.me/oneuiproject";
            } else if (v.getId() == mBottomContent.aboutBottomDevYann.getId()) {
                url = "https://github.com/Yanndroid";
            } else if (v.getId() == mBottomContent.aboutBottomDevMesa.getId()) {
                url = "https://github.com/BlackMesa123";
            } else if (v.getId() == mBottomContent.aboutBottomOssApache.getId()) {
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt";
            } else if (v.getId() == mBottomContent.aboutBottomOssMit.getId()) {
                url = "https://github.com/OneUIProject/sesl/blob/main/LICENSE";
            } else if (v.getId() == mBottomContent.aboutBottomRelativeJetpack.getId()) {
                url = "https://developer.android.com/jetpack";
            } else if (v.getId() == mBottomContent.aboutBottomRelativeMaterial.getId()) {
                url = "https://material.io/develop/android";
            } else if (v.getId() == mBottomContent.aboutBottomRelativeOuip.getId()) {
                url = "https://github.com/OneUIProject";
            }

            if (url != null) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(
                            this, "No suitable activity found", Toast.LENGTH_SHORT).show();
                }
            }
        }
        mLastClickTime = uptimeMillis;
    }

    private Drawable getThemedIcon() {
        if (Build.VERSION.SDK_INT >= 31) {
            final boolean needApplyColorThemeForIcon = Settings.System
                    .getInt(getContentResolver(), "colortheme_app_icon", 0) != 0;

            if (needApplyColorThemeForIcon) {
                LayerDrawable icon = (LayerDrawable) getDrawable(R.drawable.about_page_icon);

                Drawable bg = icon.getDrawable(0);
                bg.setTint(getColor(android.R.color.system_accent1_400));

                Drawable fg = icon.getDrawable(1);
                fg.setColorFilter(getIconFgColorFilter(getColor(android.R.color.system_accent1_700)));

                return icon;
            }
        }

        LayerDrawable icon = (LayerDrawable) getDrawable(R.drawable.about_page_icon);
        Drawable bg = icon.getDrawable(0);
        bg.setTint(SEM_PRIMARY_COLOR);
        return icon;
    }

    private ColorMatrixColorFilter getIconFgColorFilter(int primaryColor) {
        final int[] filterParam = getFilterParam(
                true, primaryColor, 0, 100, true);
        return createFilter(
                filterParam[0], filterParam[2] / 100.0f, filterParam[1]);
    }

    // kang from com.sec.android.app.launcher
    private int[] getFilterParam(
            boolean enable, int iconColor, int iconSaturationVal, int iconBlendVal, boolean style) {
        int[] iArr = new int[3];
        if (enable) {
            int i = (iconColor >> 16) & 255;
            int i2 = (iconColor >> 8) & 255;
            int i3 = iconColor & 255;
            int i4 = 255 - i;
            int i5 = 255 - i2;
            int i6 = 255 - i3;
            float f = iconBlendVal / 100.0f;
            int i7 = (int) ((1.0f - f) * 255.0f);
            if (style) {
                float f2 = i7;
                iArr[0] = Color
                        .rgb((int) ((i4 * f) + f2), (int) ((i5 * f) + f2), (int) ((i6 * f) + f2));
                iArr[1] = Color
                        .rgb((int) (i * f), (int) (i2 * f), (int) (i3 * f));
            } else {
                float f3 = i7;
                iArr[0] = Color
                        .rgb((int) ((i * f) + f3), (int) ((i2 * f) + f3), (int) ((i3 * f) + f3));
                iArr[1] = -16777216;
            }
            iArr[2] = iconSaturationVal;
        } else {
            iArr[0] = -1;
            iArr[1] = -16777216;
            iArr[2] = 100;
        }

        return iArr;
    }

    private ColorMatrixColorFilter createFilter(
            int multipliedColor, float saturationVal, int addedColor) {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(saturationVal);
        float[] array = colorMatrix.getArray();
        float f = ((multipliedColor >> 16) & 255) / 255.0f;
        float f2 = ((multipliedColor >> 8) & 255) / 255.0f;
        float f3 = (multipliedColor & 255) / 255.0f;
        array[0] = array[0] * f;
        array[1] = array[1] * f;
        array[2] = array[2] * f;
        array[5] = array[5] * f2;
        array[6] = array[6] * f2;
        array[7] = array[7] * f2;
        array[10] = array[10] * f3;
        array[11] = array[11] * f3;
        array[12] = array[12] * f3;
        array[4] = (addedColor >> 16) & 255;
        array[9] = (addedColor >> 8) & 255;
        array[14] = addedColor & 255;
        return new ColorMatrixColorFilter(colorMatrix);
    }
    // kang from com.sec.android.app.launcher

    private class AboutAppBarListener implements AppBarLayout.OnOffsetChangedListener {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            // Handle the SwipeUp anim view
            final int totalScrollRange = appBarLayout.getTotalScrollRange();
            final int abs = Math.abs(verticalOffset);

            if (abs >= totalScrollRange / 2) {
                mBinding.aboutSwipeUpContainer.setAlpha(0f);
                setBottomContentEnabled(true);
            } else if (abs == 0) {
                mBinding.aboutSwipeUpContainer.setAlpha(1f);
                setBottomContentEnabled(false);
            } else {
                float offsetAlpha = (appBarLayout.getY() / totalScrollRange);
                float arrowAlpha = 1 - (offsetAlpha * -3);
                if (arrowAlpha < 0) {
                    arrowAlpha = 0;
                } else if (arrowAlpha > 1) {
                    arrowAlpha = 1;
                }
                mBinding.aboutSwipeUpContainer.setAlpha(arrowAlpha);
            }

            // Handle the bottom part of the UI
            final float alphaRange = mBinding.aboutCtl.getHeight() * 0.143f;
            final float layoutPosition = Math.abs(appBarLayout.getTop());
            float bottomAlpha = (150.0f / alphaRange)
                    * (layoutPosition - (mBinding.aboutCtl.getHeight() * 0.35f));

            if (bottomAlpha < 0) {
                bottomAlpha = 0;
            } else if (bottomAlpha >= 255) {
                bottomAlpha = 255;
            }

            mBinding.aboutBottomContainer.setAlpha(bottomAlpha / 255);
        }
    }
}