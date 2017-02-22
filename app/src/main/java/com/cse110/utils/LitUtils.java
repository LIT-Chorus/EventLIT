package com.cse110.utils;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by rahulsabnis on 2/15/17.
 */

public class LitUtils {

    public static void hideSoftKeyboard (AppCompatActivity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

}
