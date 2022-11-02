package dev.oneuiproject.oneui.qr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;

import dev.oneuiproject.oneui.design.R;

public class QREncoder {

    public static Bitmap generateQRCode(Context context, String content) {
        return generateQRCode(context, content, null);
    }

    public static Bitmap generateQRCode(Context context, String content, Drawable icon) {
        return generateQRCode(context, content, getPixel(context, 188), icon);
    }

    public static Bitmap generateQRCode(Context context, String content, int size) {
        return generateQRCode(context, content, size, null);
    }

    public static Bitmap generateQRCode(Context context, String content, int size, Drawable icon) {
        Drawable corner = context.getDrawable(R.drawable.oui_qr_code_anchor);

        try {
            QRCode code = Encoder.encode(content, ErrorCorrectionLevel.H);
            ByteMatrix matrix = code.getMatrix();

            int pixelCount = matrix.getWidth();
            int pixelSize = size / pixelCount;
            int newSize = size - (size % pixelCount);

            Bitmap bitmap = Bitmap.createBitmap(newSize, newSize, Bitmap.Config.RGB_565);
            for (int i = 0; i < newSize; i++) {
                for (int j = 0; j < newSize; j++) {
                    bitmap.setPixel(i, j, matrix.get(i / pixelSize, j / pixelSize) == 1 ? Color.BLACK : Color.WHITE);
                }
            }

            return decorate(context, bitmap, corner, icon, pixelCount);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }


    }

    private static Bitmap decorate(Context context, Bitmap bitmap, Drawable corner, Drawable icon, int pixelCount) {
        int border = getPixel(context, 6);
        float radius = getPixel(context, 12);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = width + border * 2;
        int newHeight = height + border * 2;

        Bitmap output = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#fcfcfc"));
        RectF rectF = new RectF(0.0f, 0.0f, newWidth, newHeight);
        Canvas canvas = new Canvas(output);
        canvas.drawRoundRect(rectF, radius, radius, paint);
        canvas.drawBitmap(bitmap, border, border, (Paint) null);

        if (corner != null) {
            float width2 = (7.0f / pixelCount) * bitmap.getWidth();
            float f3 = width2 + border;
            canvas.drawRect(border, border, f3, f3, paint);
            int i6 = width + border;
            canvas.drawRect((width - width2) + border, border, i6, f3, paint);
            int i7 = height + border;
            canvas.drawRect(border, (height - width2) + border, f3, i7, paint);
            int i8 = (int) width2;
            int i9 = i8 + border;
            corner.setBounds(border, border, i9, i9);
            corner.draw(canvas);
            corner.setBounds((width - i8) + border, border, i6, i9);
            corner.draw(canvas);
            corner.setBounds(border, (height - i8) + border, i9, i7);
            corner.draw(canvas);
        }

        paint.setColor(Color.parseColor("#d0d0d0"));
        paint.setStrokeWidth(2.0f);
        paint.setStyle(Paint.Style.STROKE);
        rectF.set(1.0f, 1.0f, newWidth - 1, newHeight - 1);
        canvas.drawRoundRect(rectF, radius, radius, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#fcfcfc"));
        Math.sqrt(width * width * 2);
        float f4 = border / 2;
        rectF.set(f4, f4, width + border, height + border);
        paint.setStrokeWidth(f4);
        if (icon != null) {
            float pixel3 = getPixel(context, 3);
            int pixel4 = getPixel(context, 40);
            int i10 = (newWidth - pixel4) / 2;
            int i11 = (newHeight - pixel4) / 2;
            rectF.set(i10 - pixel3, i11 - pixel3, ((newWidth + pixel4) / 2) + pixel3, ((newHeight + pixel4) / 2) + pixel3);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.parseColor("#fcfcfc"));
            float pixel5 = (getPixel(context, 12) * 3.0f) / 2.0f;
            canvas.drawRoundRect(rectF, pixel5, pixel5, paint);
            icon.setBounds(i10, i11, i10 + pixel4, pixel4 + i11);
            icon.draw(canvas);
        }

        canvas.save();
        canvas.restore();
        return output;
    }

    private static int getPixel(Context context, int i) {
        if (context.getResources() == null) return 0;
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, context.getResources().getDisplayMetrics());
    }

}
