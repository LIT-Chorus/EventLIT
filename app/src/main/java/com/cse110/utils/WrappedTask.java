package com.cse110.utils;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;

/**
 * Created by vansh on 3/7/17.
 */

// Wrapper to get around `final` restrictions when passing values to inner classes
public class WrappedTask<T> {
    /**
     * User-set Task that this WrappedTask contains
     */
    private TaskCompletionSource<T> source;

    public WrappedTask() {
        source = new TaskCompletionSource<>();
    }

    /**
     * Wraps the task so that calls to getTask will return the task, and returned Tasks will
     * return the wrapped tasks result on getResult.
     * @param newTask
     */
    public void wrap(Task<T> newTask) {
        source.setResult(newTask.getResult());
        if (!newTask.isSuccessful()) {
            source.setException(new Exception());
        }
    }

    public void wrapResult(T result) {
        Task<T> taskResult = Tasks.forResult(result);
        wrap(taskResult);
    }

    public void wrapResultFail(T result) {
        source.setException(new Exception());
        wrapResult(result);
    }

    public Task<T> unwrap() {
        return source.getTask();
    }
}
