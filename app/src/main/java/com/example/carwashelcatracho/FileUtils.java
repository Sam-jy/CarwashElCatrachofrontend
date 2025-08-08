package com.example.carwashelcatracho;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class FileUtils {
    public static String getPath(Context context, Uri uri) {
        String result = null;
        String[] proj = { MediaStore.Video.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = uri.getPath();
        }
        return result;
    }
} 