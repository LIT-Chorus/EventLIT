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

    /* Returns month, ex: Jan, Feb, Mar */
    public static String getMonthString(long unixSeconds) {
        Date date = new Date(unixSeconds);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM"); // the format of the date
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    /* Returns day, ex: 01, 12, 31 */
    public static String getDay(long unixSeconds) {
        Date date = new Date(unixSeconds);
        SimpleDateFormat sdf = new SimpleDateFormat("dd"); // the format of the date
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    /* Returns time, ex: 12:35pm, 5:30am */
    public static String getTime(long unixSeconds) {
        Date date = new Date(unixSeconds);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mma"); // the format of the date
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    /* Returns date and time, ex: Jan 31 at 8:30 pm */
    public static String getDateAndTime(long unixSeconds) {
        Date date = new Date(unixSeconds);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd 'at' hh:mma"); // the format of the date
        String formattedDate = sdf.format(date);
        return formattedDate;
    }


}
