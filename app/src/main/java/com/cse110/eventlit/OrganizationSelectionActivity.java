package com.cse110.eventlit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.cse110.eventlit.db.Organization;

import java.util.ArrayList;
import java.util.List;

public class OrganizationSelectionActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    public EditText search;
    private LinearLayoutManager mLinearLayoutManager;
    private List<Organization> list = new ArrayList<>();
    private OrganizationsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_selection);
        search = (EditText) findViewById(R.id.search);
        mRecyclerView = (RecyclerView) findViewById(R.id.list_orgs);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

       adapter = new OrganizationsAdapter(getApplicationContext());
        mRecyclerView.setAdapter(adapter);
        list = adapter.getmOrganizations();
        addTextListener();

    }

    public void addTextListener(){

        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                //set the list to have all of the organizations before filtering
                int squery = query.length();
                query = query.toString().toLowerCase();

                ArrayList<Organization> filteredList = new ArrayList<>();

                for (int i = 0; i < list.size(); i++) {

                    final String text = list.get(i).getName().toLowerCase();
                    if (text.contains(query)) {
                        Log.d("filter", String.valueOf(squery));
                        filteredList.add(list.get(i));
                    }
                }

                Log.d("filter size", String.valueOf(list.size()));

                mRecyclerView.setLayoutManager(new LinearLayoutManager(OrganizationSelectionActivity.this));
                adapter = new OrganizationsAdapter(OrganizationSelectionActivity.this, filteredList);
                mRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();  // data set changed
            }
        });
    }

}
