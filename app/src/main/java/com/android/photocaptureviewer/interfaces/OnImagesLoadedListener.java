package com.android.photocaptureviewer.interfaces;

import com.android.photocaptureviewer.data.ImageFolder;

import java.util.List;

public interface OnImagesLoadedListener {
    void onImagesLoaded(List<ImageFolder> imageSetList);
}
