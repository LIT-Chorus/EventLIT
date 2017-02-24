package com.cse110.eventlit;

import android.app.Fragment;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cse110.eventlit.db.Event;
import com.cse110.utils.EventUtils;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by rahulsabnis on 2/22/17.
 */

public class CardFragment extends android.support.v4.app.Fragment {
    ArrayList<Event> listEvents = new ArrayList<>();
    RecyclerView MyRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_card, container, false);
        MyRecyclerView = (RecyclerView) view.findViewById(R.id.cardView);
        MyRecyclerView.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        MyAdapter adapter = new MyAdapter(listEvents);
        EventUtils.getAllEvents(adapter, listEvents);
//        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onChanged() {
//                super.onChanged();
//            }
//        });
        MyRecyclerView.setAdapter(adapter);
        MyRecyclerView.setLayoutManager(MyLayoutManager);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private ArrayList<Event> list;

        public MyAdapter(ArrayList<Event> Data) {
            list = Data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycle_items, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {

            Event e = list.get(position);

            java.util.Calendar startEventCal = e.getStartDate();
            java.util.Calendar endEventCal = e.getEndDate();

            // Gets start and end times
            int startHour = startEventCal.get(java.util.Calendar.HOUR);
            int startMinute = startEventCal.get(java.util.Calendar.MINUTE);

            int endHour = endEventCal.get(java.util.Calendar.HOUR);
            int endMinute = endEventCal.get(java.util.Calendar.MINUTE);

            // Gets month and date
            int day = startEventCal.get(java.util.Calendar.DAY_OF_MONTH);
            int month = startEventCal.get(java.util.Calendar.MONTH);
            String monthStr = new DateFormatSymbols().getMonths()[month];

            String category = e.getCategory();

            String eventName = e.getTitle();


            holder.timeTextView.setText(String.format(Locale.getDefault(), "%d:%02d - %d:%02d",
                    startHour, startMinute, endHour, endMinute));
            holder.locationTextView.setText(list.get(position).getLocation());
            holder.categoriesTextView.setText(category);
            holder.eventNameTextView.setText(eventName);

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public AppCompatTextView locationTextView;
        public AppCompatTextView timeTextView;
        public AppCompatTextView categoriesTextView;
        public AppCompatTextView eventNameTextView;

        public MyViewHolder(View v) {
            super(v);
            timeTextView = (AppCompatTextView) v.findViewById(R.id.time);
            locationTextView = (AppCompatTextView) v.findViewById(R.id.location);
            categoriesTextView = (AppCompatTextView) v.findViewById(R.id.categories);
            eventNameTextView = (AppCompatTextView) v.findViewById(R.id.eventName);

        }
    }
}
