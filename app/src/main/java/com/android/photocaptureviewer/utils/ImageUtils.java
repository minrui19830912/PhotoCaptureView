package com.android.photocaptureviewer.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Display;

public class ImageUtils {
    public static Bitmap getImageBitmap(Activity activity, String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        Display display = activity.getWindow().getWindowManager().getDefaultDisplay();
        int windowWidth = display.getWidth();
        int windowHeight = display.getHeight();
        int valueX = outWidth / windowWidth;
        int valueY = outHeight / windowHeight;
        int value = valueX > valueY ? valueX : valueY;
        if (value <= 0) {
            value = 1;
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = value;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }
}
