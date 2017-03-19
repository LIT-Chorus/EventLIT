package com.cse110.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.cse110.eventlit.db.Organization;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by rahulsabnis on 2/15/17.
 */

public class LitUtils {

    public static void hideSoftKeyboard (AppCompatActivity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    public static String getMonth(long unixSeconds) {
        Date date = new Date(unixSeconds*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-3M-dd HH:mm a"); // the format of the date
        String formattedDate = sdf.format(date);
        return formattedDate.substring(5, 8);
    }

    public static String getDay(long unixSeconds) {
        Date date = new Date(unixSeconds*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-3M-dd HH:mm a"); // the format of the date
        String formattedDate = sdf.format(date);
        return formattedDate.substring(10, 12);
    }

    public static String getTime(long unixSeconds) {
        Date date = new Date(unixSeconds*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-3M-dd HH:mm a"); // the format of the date
        String formattedDate = sdf.format(date);
        return formattedDate.substring(12, formattedDate.length() - 1);
    }



}
