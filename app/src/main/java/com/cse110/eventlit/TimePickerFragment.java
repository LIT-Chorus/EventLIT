package com.cse110.eventlit;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by jocelyn on 3/10/17.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
    Calendar c = Calendar.getInstance();
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Use the current time as the default values for the time picker

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        //Create and return a new instance of TimePickerDialog
        return new TimePickerDialog(getActivity(),this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    //onTimeSet() callback method
    public void onTimeSet(TimePicker view, int hourOfDay, int minute){

        //Get reference of host activity (XML Layout File) TextView widget
        TextView tv = (TextView) getActivity().findViewById(getArguments().getInt("timetext"));

        //Get the AM or PM for current time
        String aMpM = "am";
        if (hourOfDay >11) {
            aMpM = "pm";
        }

        //Make the 24 hour time format to 12 hour time format
        int currentHour = hourOfDay;
        if (currentHour == 0) {
            currentHour = 12;
        }
        if (hourOfDay>12) {
            currentHour = hourOfDay - 12;
        }

        String minString = "" + minute;
        if (minute < 10) {
            minString = "0" + minute;
        }

        Calendar today = Calendar.getInstance();
        c.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH),hourOfDay,minute);
        //Display the user changed time on TextView
        tv.setText(String.valueOf(currentHour) + ":" + minString + " " +
                aMpM);
    }

    public Calendar getCalendar() {
        return c;
    }
    public void setCalendar(Calendar c) {this.c = c; }
}
