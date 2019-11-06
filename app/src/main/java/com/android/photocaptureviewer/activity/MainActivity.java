package com.android.photocaptureviewer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.android.photocaptureviewer.R;
import com.android.photocaptureviewer.utils.FileUtils;
import com.android.photocaptureviewer.utils.PermissionUtils;
import java.io.File;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.content.PermissionChecker;

public class MainActivity extends AppCompatActivity {
    private Context mContext;
    private File mCaptureFile;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private static final int CAMERA_START_REQUEST_CODE = 1002;
    private static final int SDCARD_PERMISSION_REQUEST_CODE = 1002;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

    }


    public void onCapture(View view) {
        if (PermissionUtils.hasCameraPermission(MainActivity.this)) {
            openCamera();
        } else {
            PermissionUtils.requestCameraPermissions(MainActivity.this, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    private void openCamera() {
        mCaptureFile = new File(FileUtils.getCaptureFileParentPath(), System.currentTimeMillis() + FileUtils.IMG_SUFFIX);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(mContext, getApplicationInfo().packageName, mCaptureFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCaptureFile));
        }
        startActivityForResult(intent, CAMERA_START_REQUEST_CODE);
    }

    public void onViewPhoto(View view) {
        if (PermissionUtils.hasSDCardPermission(MainActivity.this)) {
            showPhotoListPage();
        } else {
            PermissionUtils.requestSDCardPermissions(MainActivity.this, SDCARD_PERMISSION_REQUEST_CODE);
        }
    }

    private void showPhotoListPage() {
        startActivity(new Intent(mContext, PhotoListActivity.class));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PermissionChecker.PERMISSION_DENIED) {
                    Toast.makeText(mContext, mContext.getString(R.string.permission_request_ind), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            openCamera();
        } else if (requestCode == SDCARD_PERMISSION_REQUEST_CODE) {
            showPhotoListPage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_START_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            final EditText editText = new EditText(this);
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(R.string.change_name).setView(editText).
                    setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            File newFile = FileUtils.renameFile(mCaptureFile, editText.getText().toString());
                            if (newFile != null) {
                                ContentValues values = new ContentValues(7);
                                values.put(MediaStore.Images.Media.TITLE, editText.getText().toString());
                                values.put(MediaStore.Images.Media.DISPLAY_NAME, editText.getText().toString());
                                values.put(MediaStore.Images.Media.DATE_TAKEN, "");
                                values.put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis());
                                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                                values.put(MediaStore.Images.Media.ORIENTATION, 0);
                                values.put(MediaStore.Images.Media.DATA, newFile.getPath());
                                values.put(MediaStore.Images.Media.SIZE, newFile.length());
                                getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                Toast.makeText(mContext, R.string.rename_success, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, R.string.rename_fail, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).setNegativeButton(R.string.cancel, null);
            builder.show();
        }
    }
}
