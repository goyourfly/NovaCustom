package com.archos.filecorelibrary.localstorage;

    /*
 * Copyright (C) 2014 NextApp, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */



import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.MediaStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Wrapper for manipulating files via the Android Media Content Provider. As of Android 4.4 KitKat, applications can no longer write
 * to the "secondary storage" of a device. Write operations using the java.io.File API will thus fail. This class restores access to
 * those write operations by way of the Media Content Provider.
 *
 * Note that this class relies on the internal operational characteristics of the media content provider API, and as such is not
 * guaranteed to be future-proof. Then again, we did all think the java.io.File API was going to be future-proof for media card
 * access, so all bets are off.
 *
 * If you're forced to use this class, it's because Google/AOSP made a very poor API decision in Android 4.4 KitKat.
 * Read more at https://plus.google.com/+TodLiebeck/posts/gjnmuaDM8sn
 *
 * Your application must declare the permission "android.permission.WRITE_EXTERNAL_STORAGE".
 */
public class ExternalSDFileWriter {

    private static final Logger log = LoggerFactory.getLogger(ExternalSDFileWriter.class);

    private final File file;
    private final ContentResolver contentResolver;
    private final Uri filesUri;
    private final Uri imagesUri;

    public ExternalSDFileWriter(ContentResolver contentResolver, File file) {
        log.debug("ExternalSDFileWriter " + file.getPath());
        this.file = file;
        this.contentResolver = contentResolver;
        filesUri = MediaStore.Files.getContentUri("external");
        imagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

    /**
     * Deletes the file. Returns true if the file has been successfully deleted or otherwise does not exist. This operation is not
     * recursive.
     */
    public boolean delete() throws IOException {
        if (!file.exists()) {
            return true;
        }

        boolean directory = file.isDirectory();
        if (directory) {
            // Verify directory does not contain any files/directories within it.
            String[] files = file.list();
            if (files != null && files.length > 0) {
                return false;
            }
        }

        String where = MediaStore.MediaColumns.DATA + "=?";
        String[] selectionArgs = new String[] { file.getAbsolutePath() };

        // Delete the entry from the media database. This will actually delete media files (images, audio, and video).
        contentResolver.delete(filesUri, where, selectionArgs);

        if (file.exists()) {
            // If the file is not a media file, create a new entry suggesting that this location is an image, even
            // though it is not.
            ContentValues values = new ContentValues();
            values.put(MediaStore.Files.FileColumns.DATA, file.getAbsolutePath());
            // TODO: leads to a crash on external usb storage on recent Android
            log.debug("delete: insert like it was an image imagesUri " + imagesUri + ", filesUri " + filesUri);
            contentResolver.insert(imagesUri, values);

            // TODO MARC the real delete is there
            // Delete the created entry, such that content provider will delete the file.
            contentResolver.delete(filesUri, where, selectionArgs);
        }

        return !file.exists();
    }

    public File getFile() {
        return file;
    }

    /**
     * Creates a new directory. Returns true if the directory was successfully created or exists.
     */
    public boolean mkdir()
             {
        if (file.exists()) {
            return file.isDirectory();
        }

        ContentValues values;
        Uri uri;

        // Create a media database entry for the directory. This step will not actually cause the directory to be created.
        values = new ContentValues();
        values.put(MediaStore.Files.FileColumns.DATA, file.getAbsolutePath());
        contentResolver.insert(filesUri, values);
        addFolderToDB();

        return file.exists();
    }

    public void addFolderToDB(){
        // Create an entry for a temporary image file within the created directory.
        // This step actually causes the creation of the directory.
        ContentValues values;
        Uri uri;
        values = new ContentValues();
        values.put(MediaStore.Files.FileColumns.DATA, file.getAbsolutePath() + "/temp.jpg");
        uri = contentResolver.insert(imagesUri, values);

        // Delete the temporary entry.
        contentResolver.delete(uri, null, null);
    }
    /**
     * Returns an OutputStream to write to the file. The file will be truncated immediately.
     */
    public OutputStream write()
            throws IOException {
        if (file.exists() && file.isDirectory()) {
            throw new IOException("File exists and is a directory.");
        }

        // Delete any existing entry from the media database.
        // This may also delete the file (for media types), but that is irrelevant as it will be truncated momentarily in any case.
        String where = MediaStore.MediaColumns.DATA + "=?";
        String[] selectionArgs = new String[] { file.getAbsolutePath() };
        contentResolver.delete(filesUri, where, selectionArgs);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Files.FileColumns.DATA, file.getAbsolutePath());
        Uri uri = contentResolver.insert(filesUri, values);

        if (uri == null) {
            // Should not occur.
            throw new IOException("Internal error.");
        }

        return contentResolver.openOutputStream(uri);
    }

}
