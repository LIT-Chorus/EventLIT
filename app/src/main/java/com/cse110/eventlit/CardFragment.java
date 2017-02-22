package com.cse110.eventlit;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cse110.eventlit.db.Event;
import com.cse110.utils.FirebaseUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by rahulsabnis on 2/22/17.
 */

public class CardFragment extends Fragment {
    ArrayList<Event> listEvents = new ArrayList<>();
    RecyclerView MyRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("7 Wonders of the Modern World");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_card, container, false);
        MyRecyclerView = (RecyclerView) view.findViewById(R.id.cardView);
        MyRecyclerView.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        if (listEvents.size() > 0 & MyRecyclerView != null) {
            //TODO: GET RECYCLER VIEW ADAPTER INSTEAD OF ARRAYADAPTER FROM BACKEND
            //MyRecyclerView.setAdapter(FirebaseUtils.getAllStudentOrganizations(new MyAdapter(listEvents)));
        }
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

            holder.dateTextView.setText(list.get(position).gStartDate().toString());
            holder.locationTextView.setText(list.get(position).location);

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView locationTextView;
        public TextView dateTextView;

        public MyViewHolder(View v) {
            super(v);
            dateTextView = (TextView) v.findViewById(R.id.date);
            locationTextView = (TextView) v.findViewById(R.id.location);

        }
    }
}
