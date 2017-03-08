package com.cse110.utils;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

/**
 * Created by vansh on 3/7/17.
 */

// Wrapper to get around `final` restrictions when passing values to inner classes
public class WrappedTask<T> extends TaskCompletionSource<T> {
    private Task<T> wrapped;

    public void wrap(Task<T> newTask) {
        wrapped = newTask;
        super.setResult(wrapped.getResult()); // This will mark task as complete
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
