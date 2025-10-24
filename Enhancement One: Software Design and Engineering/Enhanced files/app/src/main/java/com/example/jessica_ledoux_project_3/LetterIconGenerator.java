package com.example.jessica_ledoux_project_3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class LetterIconGenerator {

    public static Bitmap create(Context context, String itemName, int dpSize) {
        float scale = context.getResources().getDisplayMetrics().density;
        int pxSize = (int) (dpSize * scale + 0.5f);

        Bitmap bitmap = Bitmap.createBitmap(pxSize, pxSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw background circle
        Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(Color.parseColor("#E7D506")); // Yellow background
        canvas.drawCircle(pxSize / 2f, pxSize / 2f, pxSize / 2f, bgPaint);

        // Draw letter
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(pxSize * 0.6f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        String letter = itemName != null && itemName.length() > 0
                ? itemName.substring(0, 1).toUpperCase()
                : "?";

        Rect bounds = new Rect();
        textPaint.getTextBounds(letter, 0, letter.length(), bounds);
        float x = pxSize / 2f;
        float y = pxSize / 2f - bounds.exactCenterY();

        canvas.drawText(letter, x, y, textPaint);
        return bitmap;
    }
}
