package com.cse110.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

/**
 * Created by vansh on 2/28/17.
 */

public class FileStorageUtils {
    private static final String UPLOAD_IMAGE = "uploadImage";


    private static StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    public static void uploadImageFromLocalFile(String id, Bitmap bitmap) throws FileNotFoundException{

        Log.d(UPLOAD_IMAGE, "beginning of method");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);

        byte[] data = baos.toByteArray();

        StorageReference imageFolderRef = storageRef.child("images");
        StorageReference imageRef = imageFolderRef.child(id + ".jpeg");

        UploadTask uploadTask = imageRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                Log.d(UPLOAD_IMAGE, "fail");

                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Log.d(UPLOAD_IMAGE, "upload success");
            }
        });
    }

    public static boolean getImageView(ImageView imageView, Context context, String id) {
        StorageReference imageRef = storageRef.child("images");
        imageRef = imageRef.child(id + ".jpeg");

        if (imageRef.getDownloadUrl().isSuccessful()) {

            // Load the image using Glide
            Glide.with(context /* context */)
                    .using(new FirebaseImageLoader())
                    .load(imageRef)
                    .into(imageView);
            return true;
        }
        return false;
    }


    /*
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
    */
}
