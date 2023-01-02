package dev.oneuiproject.oneui.qr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;

import androidx.annotation.DrawableRes;

import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;

import java.util.Hashtable;

import dev.oneuiproject.oneui.design.R;

public class QREncoder {

    private Context mContext;

    private String mContent;
    private int mSize;
    private Drawable mIcon;
    private int mIconSize;
    private boolean mFrame = true;

    private int mFGColor = Color.BLACK;
    private int mBGColor = Color.parseColor("#fcfcfc");
    private boolean mTintAnchor = false;
    private boolean mTintBorder = false;

    public QREncoder(Context context, String content) {
        this.mContext = context;
        this.mContent = content;
        this.mSize = getPixel(200);
        this.mIconSize = getPixel(40);
    }

    public QREncoder setSize(int size) {
        this.mSize = size;
        return this;
    }

    public QREncoder setFrame(boolean frame) {
        this.mFrame = frame;
        return this;
    }

    public QREncoder setIcon(@DrawableRes int id) {
        return this.setIcon(getDrawable(id));
    }

    public QREncoder setIcon(Drawable icon) {
        this.mIcon = icon;
        return this;
    }

    public QREncoder setBGColor(int color) {
        this.mBGColor = color;
        return this;
    }

    public QREncoder setFGColor(int color, boolean tintAnchor, boolean tintBorder) {
        this.mFGColor = color;
        this.mTintAnchor = tintAnchor;
        this.mTintBorder = tintBorder;
        return this;
    }


    public Bitmap generate() {
        try {
            Hashtable hashtable = new Hashtable();
            hashtable.put(EncodeHintType.CHARACTER_SET, "utf-8");
            ByteMatrix matrix = Encoder.encode(mContent, ErrorCorrectionLevel.H, hashtable).getMatrix();
            Bitmap qrcode = Bitmap.createBitmap(mSize, mSize, Bitmap.Config.ARGB_8888);
            qrcode.eraseColor(mBGColor);

            drawQrImage(qrcode, matrix);
            drawAnchor(qrcode, matrix);
            if (mIcon != null) drawIcon(qrcode);

            if (mFrame) return addFrame(qrcode);
            return qrcode;
        } catch (WriterException e) {
            Log.e("QREncoder", "Exception in encoding QR code");
            e.printStackTrace();
            return null;
        }

    }

    private void drawQrImage(Bitmap qrcode, ByteMatrix byteMatrix) {
        Canvas canvas = new Canvas(qrcode);
        Paint paint = getPaint();
        paint.setColor(mFGColor);
        float width = (float) ((qrcode.getWidth() * 1.0d) / byteMatrix.getWidth());
        float radius = (float) (0.382d * (double) width);
        float offset = (float) ((double) width / 2.0d);
        for (int i = 0; i < byteMatrix.getHeight(); i++) {
            for (int i2 = 0; i2 < byteMatrix.getWidth(); i2++) {
                if (byteMatrix.get(i2, i) == 1) {
                    canvas.drawCircle((i2 * width) + offset, (i * width) + offset, radius, paint);
                }
            }
        }
    }


    private void drawAnchor(Bitmap qrcode, ByteMatrix byteMatrix) {
        Bitmap anchor = getBitmap(getDrawable(R.drawable.oui_qr_code_anchor));
        int width = qrcode.getWidth();
        int height = qrcode.getHeight();

        int anchorWidth = (int) (getAnchorWidth(byteMatrix) * ((float) ((width * 1.0d) / byteMatrix.getWidth())));
        Paint paint = getPaint();
        Canvas canvas = new Canvas(qrcode);
        canvas.drawRect(new RectF(0.0f, 0.0f, (float) anchorWidth, (float) anchorWidth), paint);
        canvas.drawRect(new RectF(width - anchorWidth, 0.0f, width, (float) anchorWidth), paint);
        canvas.drawRect(new RectF(0.0f, height - anchorWidth, (float) anchorWidth, height), paint);

        Paint anchorTint = new Paint();
        anchorTint.setAntiAlias(true);
        anchorTint.setStyle(Paint.Style.FILL);
        if (mTintAnchor) {
            anchorTint.setColorFilter(new PorterDuffColorFilter(mFGColor, PorterDuff.Mode.SRC_IN));
        }

        Bitmap scaleBitmap = getScaleBitmap(anchor, ((float) anchorWidth) / anchor.getWidth());
        canvas.drawBitmap(scaleBitmap, 0.0f, 0.0f, anchorTint);
        canvas.drawBitmap(scaleBitmap, width - anchorWidth, 0.0f, anchorTint);
        canvas.drawBitmap(scaleBitmap, 0.0f, height - anchorWidth, anchorTint);
        scaleBitmap.recycle();
        anchor.recycle();
    }

    private int getAnchorWidth(ByteMatrix byteMatrix) {
        int i = 0;
        for (int i2 = 0; i2 < byteMatrix.getWidth() && byteMatrix.get(i2, 0) == 1; i2++) i++;
        return i;
    }

    private void drawIcon(Bitmap qrcode) {
        int height = mIconSize;
        int width = mIconSize;
        int icon_top = (qrcode.getHeight() / 2) - (height / 2);
        int icon_left = (qrcode.getWidth() / 2) - (width / 2);
        int icon_radius = getPixel(20);
        int icon_padding = getPixel(5);
        Canvas canvas = new Canvas(qrcode);
        Paint paint = getPaint();
        RectF rectF = new RectF(icon_left - icon_padding, icon_top - icon_padding, width + icon_left + icon_padding, height + icon_top + icon_padding);
        canvas.drawRoundRect(rectF, (float) icon_radius, (float) icon_radius, paint);
        mIcon.setBounds(icon_left, icon_top, icon_left + width, icon_top + height);
        mIcon.draw(canvas);
    }

    private Bitmap addFrame(Bitmap qrcode) {
        int border = getPixel(12);
        int radius = getPixel(32);

        int newWidth = qrcode.getWidth() + border * 2;
        int newHeight = qrcode.getHeight() + border * 2;
        Bitmap output = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = getPaint();
        RectF rectF = new RectF(0, 0, newWidth, newHeight);
        canvas.drawRoundRect(rectF, radius, radius, paint);

        canvas.drawBitmap(qrcode, border, border, null);

        paint.setColor(mTintBorder ? mFGColor : Color.parseColor("#d0d0d0"));
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        rectF.set(1.0f, 1.0f, newWidth - 1, newHeight - 1);
        canvas.drawRoundRect(rectF, radius, radius, paint);

        return output;
    }


    private Paint getPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(mBGColor);
        return paint;
    }

    private Drawable getDrawable(@DrawableRes int id) {
        return mContext.getDrawable(id);
    }

    private Bitmap getBitmap(Drawable drawable) {
        Bitmap createBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return createBitmap;
    }

    private Bitmap getScaleBitmap(Bitmap bitmap, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private int getPixel(int dp) {
        if (mContext.getResources() == null) return 0;
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mContext.getResources().getDisplayMetrics());
    }
}
