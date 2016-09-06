package com.partyideas.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.partyideas.R;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by xeonyan on 10/8/2016.
 */
public class CustomMeetupRecyclerViewAdapter extends RecyclerView.Adapter<CustomMeetupRecyclerViewAdapter.ViewHolder>  {

    ArrayList<CustomEventResponseObject> dataset;

    public CMRecyclerViewListener cmrListener;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView titleView;
        public TextView timeView;
        public TextView slotView;
        public LinearLayout root;
        public ImageView gameImg;

        public ViewHolder(View v) {
            super(v);
            titleView = (TextView) v.findViewById(R.id.eventTitle);
            timeView = (TextView)v.findViewById(R.id.eventTime);
            slotView = (TextView)v.findViewById(R.id.eventSlot);
            root = (LinearLayout) v.findViewById(R.id.root);
            gameImg = (ImageView) v.findViewById(R.id.gameImg);
        }
    }

    public CustomMeetupRecyclerViewAdapter(ArrayList<CustomEventResponseObject> dataset,CMRecyclerViewListener listener){
        this.dataset = dataset;
        this.cmrListener = listener;
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
        try {
            holder.titleView.setText(obj.name);
            holder.timeView.setText(unixToDateTime(obj.unix * 1000L));
            holder.slotView.setText(obj.yes_rsvp + "/" + obj.rsvp_limit + " going");
            String imgsrc = new JSONObject(obj.gameJson).getString("imgsrc");
            imgsrc = imgsrc.replaceFirst("^(https://)|^(http://)?(www\\.)?", "");
            Ion.with(holder.gameImg)
                    .placeholder(R.mipmap.ic_picture)
                    .load("http://images.weserv.nl/?url="+imgsrc+"&w=240&h=240&t=square&a=center");
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cmrListener.onDetailClick(obj);
                }
            });
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
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
