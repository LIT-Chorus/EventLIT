package com.cse110.eventlit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cse110.eventlit.db.Organization;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rahulsabnis on 3/8/17.
 */

public class OrganizationSelectionFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private List<Organization> list = new ArrayList<>();
    private OrganizationsAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void onClick(View arg0){
        Button btn = (Button) arg0;
        Log.d("click", btn.getText().toString());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_select_orgs, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_orgs);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        adapter = new OrganizationsAdapter(getContext());
        mRecyclerView.setAdapter(adapter);
        list = adapter.getmOrganizations();

        return view;

    }


    public void filter(String prefix) {
        int squery = prefix.length();
        String query = prefix.toLowerCase();

        ArrayList<Organization> filteredList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {

            final String text = list.get(i).getName().toLowerCase();
            if (text.startsWith(query)) {
                Log.d("filter", String.valueOf(squery));
                filteredList.add(list.get(i));
            }
        }

        for(int i = 0; i < list.size(); i++){
            final String text = list.get(i).getName().toLowerCase();

            if(text.contains(query) && !filteredList.contains(list.get(i))){
                filteredList.add(list.get(i));
            }
        }

        Log.d("filter size", String.valueOf(list.size()));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new OrganizationsAdapter(getActivity(), filteredList);
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();  // data set changed
    }

}
