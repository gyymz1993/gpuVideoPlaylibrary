package com.gup.video;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import jp.co.cyberagent.android.gpuimage.GPUImageAddBlendFilter;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * Created by baidu on 2017/6/30.
 */

public class GPUImageBurnTextFilter extends GPUImageAddBlendFilter {

    public GPUImageBurnTextFilter() {
        super();
    }

    public void setText(String text, float textSize, int color) {
        Bitmap bitmap = textAsBitmap(text, textSize, color, 200, 200);
        setBitmap(bitmap);
    }

    public Bitmap textAsBitmap(String text, float textSize, int textColor, int width, int height) {
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        // add now
        paint.setAntiAlias(true);
        // set textfont, you can Typeface.createFromAsset(getContext().getAssets(),"fonts/samplefont.ttf"); to load
        Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        paint.setTypeface(font);

        float baseline = -paint.ascent(); // ascent() is negative
//        int width = (int) (paint.measureText(text) + 0.5f); // round
//        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);

        return image;
    }
}
