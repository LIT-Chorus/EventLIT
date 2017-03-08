package com.cse110.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.Executor;

// Task to notify that the password reset was unsuccessful
public class PasswordTask extends Task<String> {
    String result;
    PasswordTask(String errorMessage){
        result = errorMessage;
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public boolean isSuccessful() {
        return true;
    }

    @Override
    public String getResult() {
        return result;
    }

    @Override
    public <X extends Throwable> String getResult(@NonNull Class<X> aClass) throws X {
        return null;
    }

    @Nullable
    @Override
    public Exception getException() {
        return null;
    }

    @NonNull
    @Override
    public Task<String> addOnSuccessListener(@NonNull OnSuccessListener<? super String> onSuccessListener) {
        return null;
    }

    @NonNull
    public Task<String> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super String> onSuccessListener) {
        return null;
    }

    @NonNull
    public Task<String> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super String> onSuccessListener) {
        return null;
    }

    @NonNull
    @Override
    public Task<String> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
        return null;
    }

    @NonNull
    @Override
    public Task<String> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
        return null;
    }

    @NonNull
    @Override
    public Task<String> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
        return null;
    }
}