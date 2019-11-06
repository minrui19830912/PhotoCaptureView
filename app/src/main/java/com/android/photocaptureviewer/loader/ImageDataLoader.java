package com.android.photocaptureviewer.loader;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

import com.android.photocaptureviewer.R;
import com.android.photocaptureviewer.data.ImageFolder;
import com.android.photocaptureviewer.data.ImageItem;
import com.android.photocaptureviewer.interfaces.OnImagesLoadedListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;


public class ImageDataLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media._ID};

    public static final int LOADER_ALL = 0;
    public static final int LOADER_CATEGORY = 1;

    OnImagesLoadedListener imagesLoadedListener;
    Context mContext;
    private ArrayList<ImageFolder> mImageSetList = new ArrayList<>();

    public ImageDataLoader(Context ctx, OnImagesLoadedListener imagesLoadedListener) {
        this.mContext = ctx;
        this.imagesLoadedListener = imagesLoadedListener;

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ALL) {
            CursorLoader cursorLoader = new CursorLoader(mContext,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                    null, null, IMAGE_PROJECTION[2] + " DESC");
            return cursorLoader;
        } else if (id == LOADER_CATEGORY) {
            CursorLoader cursorLoader = new CursorLoader(mContext,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                    IMAGE_PROJECTION[0] + " like '%" + args.getString("path") + "%'", null, IMAGE_PROJECTION[2] + " DESC");
            return cursorLoader;
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mImageSetList.clear();
        if (data != null) {
            List<ImageItem> allImages = new ArrayList<>();
            int count = data.getCount();
            if (count <= 0) {
                return;
            }

            data.moveToFirst();
            do {
                String imagePath = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                String imageName = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                long imageAddedTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));

                ImageItem item = new ImageItem(imagePath, imageName, imageAddedTime);
                allImages.add(item);

                File imageFile = new File(imagePath);
                File imageParentFile = imageFile.getParentFile();

                ImageFolder imageFolder = new ImageFolder();
                imageFolder.name = imageParentFile.getName();
                imageFolder.path = imageParentFile.getAbsolutePath();
                imageFolder.cover = item;

                if (!mImageSetList.contains(imageFolder)) {
                    List<ImageItem> imageList = new ArrayList<>();
                    imageList.add(item);
                    imageFolder.imageItems = imageList;
                    mImageSetList.add(imageFolder);
                } else {
                    mImageSetList.get(mImageSetList.indexOf(imageFolder)).imageItems.add(item);
                }

            } while (data.moveToNext());

            ImageFolder imageSetAll = new ImageFolder();
            imageSetAll.name = mContext.getResources().getString(R.string.all_images);
            imageSetAll.cover = allImages.get(0);
            imageSetAll.imageItems = allImages;
            imageSetAll.path = "/";

            if (mImageSetList.contains(imageSetAll)) {
                mImageSetList.remove(imageSetAll);
            }
            mImageSetList.add(0, imageSetAll);

            imagesLoadedListener.onImagesLoaded(mImageSetList);//notify the data changed

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


}
