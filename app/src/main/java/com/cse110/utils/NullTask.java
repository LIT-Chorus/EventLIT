package com.cse110.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.Executor;

/**
 * A Task that does nothing. For tasks when no data is necessary, but success needs to be modifiable.
 *
 * By default, isSuccessful() will return false, unless withSuccess() sets to another value.
 *
 * Completion is not modifiable, because this class should only be used to notify success, and
 * having isComplete() return false will simply cause delay.
 */
public class NullTask<T> extends Task<T> {
    private boolean success = false;
    @Override
    public boolean isComplete() {
        return true;
    }

    /**
     * Helper function to set the success value, like:
     * <pre>
     * new NullTask&lt;Object&gt;().withSuccess(true);
     * </pre>
     * @param set
     * @return
     */
    public NullTask<T> withSuccess(boolean set) {
        success = set;
        return this;
    }

    @Override
    public boolean isSuccessful() {
        return success;
    }

    @Override
    public T getResult() {
        return null;
    }

    @Override
    public <X extends Throwable> T getResult(@NonNull Class<X> aClass) throws X {
        return null;
    }

    @Nullable
    @Override
    public Exception getException() {
        return success ? null : new Exception();
    }

    @NonNull
    @Override
    public Task<T> addOnSuccessListener(@NonNull OnSuccessListener<? super T> onSuccessListener) {
        return null;
    }

    @NonNull
    @Override
    public Task<T> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super T> onSuccessListener) {
        return null;
    }

    @NonNull
    @Override
    public Task<T> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super T> onSuccessListener) {
        return null;
    }

    @NonNull
    @Override
    public Task<T> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
        return null;
    }

    @NonNull
    @Override
    public Task<T> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
        return null;
    }

    @NonNull
    @Override
    public Task<T> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
        return null;
    }
}
