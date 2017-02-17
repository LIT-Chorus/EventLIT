package com.cse110.eventlit;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by rahulsabnis on 2/17/17.
 */

public class OrganizationsAdapter extends RecyclerView.Adapter<OrganizationsAdapter.MyViewHolder> {

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

    @Override
    public int getItemCount() {
        return 0;
    }
}
