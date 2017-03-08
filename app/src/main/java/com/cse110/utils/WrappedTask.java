package com.cse110.utils;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

/**
 * Created by vansh on 3/7/17.
 */

// Wrapper to get around `final` restrictions when passing values to inner classes
public class WrappedTask<T> extends TaskCompletionSource<T> {
    /**
     * User-set Task that this WrappedTask contains
     */
    private Task<T> wrapped;

    /**
     * Wraps the task so that calls to getTask will return the task, and returned Tasks will
     * return the wrapped tasks result on getResult.
     * @param newTask
     */
    public void wrap(Task<T> newTask) {
        wrapped = newTask;
        super.setResult(wrapped.getResult());       // Will mark task as complete
        super.setException(wrapped.getException()); // Set success based on existence of Exception
    }

    public Task<T> unwrap() {
        return getTask();
    }

    @Override
    public Task<T> getTask() {
        if (wrapped == null) {
            return super.getTask();
        } else {
            return wrapped;
        }
    }

    @Override
    public void setResult(T result) {
        super.setResult(result);
        // If the person is setting the result, they don't want the wrapped task anymore.
        wrapped = null;
    }
}
