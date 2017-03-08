package com.cse110.eventlit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cse110.eventlit.db.Event;
import com.cse110.eventlit.db.Organization;
import com.cse110.eventlit.db.User;
import com.cse110.utils.EventUtils;
import com.cse110.utils.OrganizationUtils;
import com.cse110.utils.UserUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

/**
 * Created by rahulsabnis on 2/22/17.
 */

public class CardFragment extends android.support.v4.app.Fragment {

    ArrayList<Event> listEvents = new ArrayList<>();
    RecyclerView MyRecyclerView;

    private String mDescriptionText;
    private int mNumAttendees;
    private int mMaxCapacity;

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

        Bundle pageType = getArguments();
        String type = pageType.getString("type");

        if (type.equals("feed")) {
            // TODO: Only get subscribed events instead of all events
            User user = UserUtils.getCurrentUser();
            ArrayList<Event> list = UserUtils.getEventsFollowingSynch(user);
            adapter = new MyAdapter(list);
        } else {
            EventUtils.getAllEvents(adapter, listEvents);
        }


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
            String category = e.getCategory();
            String eventName = e.getTitle();

            holder.timeTextView.setText(String.format("%s-%s",
                    e.formattedStartTime("hh:mma"), e.formattedEndTime("hh:mma")));
            holder.locationTextView.setText(list.get(position).getLocation());
            holder.categoriesTextView.setText(category);
            holder.eventNameTextView.setText(eventName);
            holder.dateTextView.setText(e.formattedStartTime("LLL\nd"));

            OrganizationUtils.getOrgFromIdAsync(e.getOrgid(), new OnCompleteListener<Organization>() {
                @Override
                public void onComplete(@NonNull Task<Organization> task) {
                    if (task.isSuccessful()) {
                        Organization org = task.getResult();
                        holder.orgNameTextView.setText(org.getName());
                    }

                }
            });

            mDescriptionText = e.getDescription();
            mNumAttendees = e.getAttendees().size();
            mMaxCapacity = e.getMaxCapacity();
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
        public AppCompatTextView dateTextView;
        public AppCompatTextView orgNameTextView;

        public MyViewHolder(View v) {
            super(v);
            timeTextView = (AppCompatTextView) v.findViewById(R.id.time);
            locationTextView = (AppCompatTextView) v.findViewById(R.id.location);
            categoriesTextView = (AppCompatTextView) v.findViewById(R.id.categories);
            eventNameTextView = (AppCompatTextView) v.findViewById(R.id.eventName);
            dateTextView = (AppCompatTextView) v.findViewById(R.id.dateView);
            orgNameTextView = (AppCompatTextView) v.findViewById(R.id.orgName);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent openDetailedView = new Intent(getActivity(), StudentDetailedEventActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("time", timeTextView.getText().toString());
                    extras.putString("location", locationTextView.getText().toString());
                    extras.putString("category", categoriesTextView.getText().toString());
                    extras.putString("eventName", eventNameTextView.getText().toString());
                    extras.putString("date", dateTextView.getText().toString());
                    extras.putString("description", mDescriptionText);
                    extras.putInt("num_attending", mNumAttendees);
                    extras.putInt("max_capacity", mMaxCapacity);
                    extras.putString("org_name", orgNameTextView.getText().toString());
                    openDetailedView.putExtras(extras);
                    startActivity(openDetailedView);
                }
            });
        }
    }
}
