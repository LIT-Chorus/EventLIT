package com.cse110.eventlit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cse110.eventlit.db.Organization;
import com.cse110.utils.OrganizationUtils;

import java.util.ArrayList;

/**
 * Created by rahulsabnis on 2/17/17.
 */

public class OrganizationsAdapter extends RecyclerView.Adapter<OrganizationsAdapter.MyViewHolder> {

    // Member Variable ArrayList of Organizations
    private ArrayList<Organization> mOrganizations;

    private Context mContext;

    public OrganizationsAdapter(Context context, ArrayList<Organization> organizations){
        this.mOrganizations = organizations;
        this.mContext = context;
        populateOrganizations();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mOrgName;
        public SwitchCompat mSwitch;

        public MyViewHolder(View view) {
            super(view);
            mOrgName = (TextView) view.findViewById(R.id.title);
            mSwitch = (SwitchCompat) view.findViewById(R.id.selection);
        }
    }

    @Override
    public OrganizationsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(OrganizationsAdapter.MyViewHolder holder, int position) {

    }

    /**
     * Populates the ArrayList associated with the organizations
     */
    private void populateOrganizations() {
        // Makes a call to the Firebase utility to get all organizations and stores in the reference
        // Adapter will be notified of changes. 
        OrganizationUtils.getAllStudentOrganizations(this, mOrganizations);
    }

    @Override
    public int getItemCount() {
        return mOrganizations.size();
    }
}
