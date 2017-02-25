package com.cse110.eventlit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

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

    public OrganizationsAdapter(Context context)
    {
        this.mContext = context;
        populateOrganizations();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public CheckedTextView mOrgName;


        public MyViewHolder(View view) {
            super(view);
            mOrgName = (CheckedTextView) view.findViewById(R.id.org_name);

        }

        public void onClick(View view)
        {
            if(mOrgName.isChecked())
            {
                mOrgName.setChecked(false);
            }

            else
            {
                mOrgName.setChecked(true);
            }
        }
    }

    @Override
    public OrganizationsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_orgs_item_row, parent, false);
        MyViewHolder mvh = new MyViewHolder(itemView);
        return mvh;
    }

    @Override
    public void onBindViewHolder(OrganizationsAdapter.MyViewHolder holder, int position) {
        holder.mOrgName.setText(mOrganizations.get(position).getName());
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
