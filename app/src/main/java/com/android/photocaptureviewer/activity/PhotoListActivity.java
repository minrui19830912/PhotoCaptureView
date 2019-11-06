package com.android.photocaptureviewer.activity;

import android.os.Bundle;
import com.android.photocaptureviewer.R;
import com.android.photocaptureviewer.adapter.PhotoAdapter;
import com.android.photocaptureviewer.data.ImageFolder;
import com.android.photocaptureviewer.interfaces.OnImagesLoadedListener;
import com.android.photocaptureviewer.loader.ImageDataLoader;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PhotoListActivity extends AppCompatActivity {
    private ImageDataLoader loader;
    private RecyclerView recycleListView;
    private PhotoAdapter mPhotoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);
        recycleListView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycleListView.setLayoutManager(layoutManager);
        mPhotoAdapter = new PhotoAdapter(PhotoListActivity.this);
        recycleListView.setAdapter(mPhotoAdapter);
        loader = new ImageDataLoader(this, new OnImagesLoadedListener() {
            @Override
            public void onImagesLoaded(List<ImageFolder> imageSetList) {
                mPhotoAdapter.setData(imageSetList);
                mPhotoAdapter.notifyDataSetChanged();
            }
        });
        LoaderManager.getInstance(this).initLoader(ImageDataLoader.LOADER_ALL, null, loader);
    }
}
