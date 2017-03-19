package com.cse110.eventlit;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cse110.eventlit.db.Organization;
import com.cse110.eventlit.db.User;
import com.cse110.utils.OrganizationUtils;
import com.cse110.utils.UserUtils;

import java.util.ArrayList;

/**
 * Created by rahulsabnis on 2/17/17.
 */

public class OrganizationsAdapter extends RecyclerView.Adapter<OrganizationsAdapter.MyViewHolder> {

    // Member Variable ArrayList of Organizations
    private ArrayList<Organization> mOrganizations;
    public static ArrayList<String> selectedOrganizationids = new ArrayList<String>();
    private Context mContext;

    public OrganizationsAdapter(Context context, ArrayList<Organization> organizations){
        this.mOrganizations = organizations;
        this.mContext = context;
        //populateOrganizations();
    }

    public OrganizationsAdapter(Context context)
    {
        this.mContext = context;
        this.mOrganizations = new ArrayList<>();
        populateOrganizations();
    }

    public ArrayList<Organization> getmOrganizations(){
        return mOrganizations;
    }

    public static ArrayList<String> getSelectedOrganization(){return selectedOrganizationids;}

    public static void clearSelectedOrganization(){selectedOrganizationids.clear();}

    public void setmOrganizations(ArrayList<Organization> filter){
        mOrganizations = filter;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public AppCompatCheckedTextView mOrgName;
        public AppCompatButton mbutton;

        public MyViewHolder(View view) {
            super(view);
            mOrgName = (AppCompatCheckedTextView) view.findViewById(R.id.org_name);
            mbutton = (AppCompatButton) view.findViewById(R.id.save_button);
            mOrgName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOrgName.isChecked())
                    {
                        User user = UserUtils.getCurrentUser();
                        int index = getAdapterPosition();
                        if (user != null && mOrganizations.size() != 0) {
                            Log.d("removing ord", mOrganizations.get(index).getOrgId());
                            user.removeOrgFollowing(mOrganizations.get(index).getOrgId());
                            UserUtils.updateCurrentUser(user);
                            String temp = mOrganizations.get(index).getOrgId();
                            selectedOrganizationids.remove(temp);
                        }
                        mOrgName.setChecked(false);
                    }

                    else
                    {
                        User user = UserUtils.getCurrentUser();
                        // Get the organization selected in the checkbox
                        // Add it to the list of orgs the user is following
                        int index = getAdapterPosition();
                        if (user != null && mOrganizations.size() != 0) {
                            Log.w("Added Org", mOrganizations.get(index).getOrgId());
                            String temp = mOrganizations.get(index).getOrgId();
                            selectedOrganizationids.add(temp);
                            user.addOrgFollowing(mOrganizations.get(index).getOrgId());
                            UserUtils.updateCurrentUser(user);
                        }
                        mOrgName.setChecked(true);
                    }
                }
            });

        }

        public void onClick(View view)
        {
            Log.d("workflow", "click method is being called");
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
        User user = UserUtils.getCurrentUser();
        String orgId = mOrganizations.get(position).getOrgId();
        if (user != null && user.isFollowingOrg(orgId)) {
            holder.mOrgName.setChecked(true);
        }
        else {
            holder.mOrgName.setChecked(false);
        }

    }

    /**
     * Populates the ArrayList associated with the organizations
     */
    void populateOrganizations() {
        // Makes a call to the Firebase utility to get all organizations and stores in the reference
        // Adapter will be notified of changes.
        //mOrganizations.clear();
        OrganizationUtils.getAllStudentOrganizations(this, mOrganizations);

        Log.d("size", String.valueOf(mOrganizations.size()));
    }

    @Override
    public int getItemCount() {
        return mOrganizations.size();
    }
}
