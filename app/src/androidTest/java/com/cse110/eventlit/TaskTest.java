package com.cse110.eventlit;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class TaskTest {
    TaskCompletionSource<String> incompleteTask;
    Task<String> task;
    @Before
    public void setUp() {
        incompleteTask = new TaskCompletionSource<>();
    }

    @Test
    public void testTaskAsync() {
        String complete = "u complete me";
        task = incompleteTask.getTask();
        incompleteTask.setResult(complete);
        assertEquals(complete, task.getResult());
    }
}
