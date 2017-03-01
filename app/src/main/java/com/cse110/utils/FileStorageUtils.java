package com.cse110.utils;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

/**
 * Created by vansh on 2/28/17.
 */

public class FileStorageUtils {
    private static final String UPLOAD_IMAGE = "uploadImage";


    private static StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    public static void uploadImageFromLocalFile(String id, String file_path) throws FileNotFoundException{

        Log.d(UPLOAD_IMAGE, "beginning of method");

        InputStream stream = new FileInputStream(new File(file_path));


        Log.d(UPLOAD_IMAGE, "read in file");

        StorageReference imageFolderRef = storageRef.child("images");
        StorageReference imageRef = imageFolderRef.child(id + ".png");

        Log.d(UPLOAD_IMAGE, "created storage references");

        UploadTask uploadTask = imageRef.putStream(stream);

        Log.d(UPLOAD_IMAGE, "started task");

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                Log.d(UPLOAD_IMAGE, "failure to upload file");
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                Log.d(UPLOAD_IMAGE, "upload success");
            }
        });
    }

    public static byte[] downloadImageInMemorySynch(String id) {
        StorageReference imageRef = storageRef.child("images/" + id + ".jpg");

        Log.d("filestorage", imageRef.getBucket());

        final long ONE_MEGABYTE = 1024 * 1024;

        final CountDownLatch synchLatch = new CountDownLatch(1);

        // hack to pass in double array
        final byte[][] returnArr = new byte[1][];

        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                returnArr[0] = bytes;
                synchLatch.countDown();
                // Data for "images/island.jpg" is returns, use this as needed
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                synchLatch.countDown();
            }
        });

        try {
            synchLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return returnArr[0];
    }


}
