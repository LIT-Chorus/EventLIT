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
import java.util.concurrent.CountDownLatch;

/**
 * Created by vansh on 2/28/17.
 */

public class FileStorageUtils {
    private static StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    public static void uploadImageFromLocalFile(String id, String file_path) {

        Uri file = Uri.fromFile(new File(file_path));
        StorageReference imageRef = storageRef.child("images/" + id + ".jpg");
        UploadTask uploadTask = imageRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                Log.d("uploadImage", "failure to upload file");
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                Log.d("uploadImage", "upload success");
            }
        });
    }

    public static byte[] downloadImageInMemorySynch(String id) {
        StorageReference imageRef = storageRef.child("images/" + id + ".jpg");

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
