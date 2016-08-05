package com.partyideas.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.partyideas.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by xeonyan on 5/8/2016.
 */
public class OfficialMeetupRecyclerViewAdapter extends RecyclerView.Adapter<OfficialMeetupRecyclerViewAdapter.ViewHolder> {
    ArrayList<OfficialEventResponseObject> data;
    private OMRecyclerViewListener listener;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView titleView;
        public TextView timeSlot;
        public TextView rsvpStatus;
        public TextView availableSpot;
        public TextView detailsButt;

        public ViewHolder(View v) {
            super(v);
            titleView = (TextView) v.findViewById(R.id.eventTitle);
            timeSlot = (TextView) v.findViewById(R.id.timeSlot);
            rsvpStatus = (TextView) v.findViewById(R.id.rsvp);
            availableSpot = (TextView)v.findViewById(R.id.avaspot);
            detailsButt = (TextView)v.findViewById(R.id.details);
        }
    }
    public OfficialMeetupRecyclerViewAdapter(ArrayList<OfficialEventResponseObject> data){
        this.data = data;
    }
    public void setDetailsOnclickLisener(OMRecyclerViewListener lisener){
        this.listener = lisener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.om_recycler_card, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final OfficialEventResponseObject obj = data.get(position);
        holder.titleView.setText(obj.name);
        holder.timeSlot.setText(unixToDateTime(obj.unix));
        holder.rsvpStatus.setText(obj.yes_rsvp + " going");
        holder.availableSpot.setText(obj.rsvp_limit!=0 ? obj.rsvp_limit+" slots" : "Unlimited");
        holder.detailsButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDetailClick(obj);
            }
        });
    }
    private String unixToDateTime(long unix){
        Date df = new java.util.Date(unix);
        return new SimpleDateFormat("MM, dd, yyyy hh:mm a").format(df);
    }
    @Override
    public int getItemCount() {
        return data.size();
    }
}
