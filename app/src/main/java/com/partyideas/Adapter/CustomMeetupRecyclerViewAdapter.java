package com.partyideas.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.partyideas.R;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by xeonyan on 10/8/2016.
 */
public class CustomMeetupRecyclerViewAdapter extends RecyclerView.Adapter<CustomMeetupRecyclerViewAdapter.ViewHolder>  {

    ArrayList<CustomEventResponseObject> dataset;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView titleView;
        public TextView timeView;
        public TextView rsvpView;
        public TextView rsvpLimitView;
        public TextView details;

        public ViewHolder(View v) {
            super(v);
            titleView = (TextView) v.findViewById(R.id.eventTitle);
            timeView = (TextView)v.findViewById(R.id.timeSlot);
            rsvpView = (TextView)v.findViewById(R.id.rsvp);
            rsvpLimitView = (TextView)v.findViewById(R.id.avaspot);
            details = (TextView)v.findViewById(R.id.details);
        }
    }

    public CustomMeetupRecyclerViewAdapter(ArrayList<CustomEventResponseObject> dataset){
        this.dataset = dataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cm_recycler_card, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(CustomMeetupRecyclerViewAdapter.ViewHolder holder, int position) {
        final CustomEventResponseObject obj = dataset.get(position);
        holder.titleView.setText(obj.name);
        holder.timeView.setText(unixToDateTime(obj.unix*1000L));
        holder.rsvpView.setText(obj.yes_rsvp + " going");
        holder.rsvpLimitView.setText(obj.rsvp_limit!=0 ? obj.rsvp_limit+" slots" : "Unlimited");
    }
    private String unixToDateTime(long unix){
        Date df = new java.util.Date(unix);
        return new SimpleDateFormat("MM, dd, yyyy hh:mm a").format(df);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
