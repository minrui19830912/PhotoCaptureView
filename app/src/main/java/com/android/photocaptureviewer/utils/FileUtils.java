package com.android.photocaptureviewer.utils;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

public class FileUtils {
    public static final String IMG_SUFFIX = ".jpg";

    public static File renameFile(File oldFile, String newName) {
        if (oldFile == null || !oldFile.exists()) {
            return null;
        }
        String newPath = getCaptureFileParentPath() + "/" + newName + IMG_SUFFIX;
        if (TextUtils.isEmpty(newPath)) {
            return null;
        }

        File newFile = new File(newPath);
        if (oldFile.renameTo(newFile)) {
            return newFile;
        }

        return null;
    }

    public static String getCaptureFileParentPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }
}
