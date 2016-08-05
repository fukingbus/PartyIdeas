package com.partyideas.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.partyideas.Activity.OfficialMeetupDetailsActivity;
import com.partyideas.Adapter.OMRecyclerViewListener;
import com.partyideas.Adapter.OfficialEventResponseObject;
import com.partyideas.Adapter.OfficialMeetupRecyclerViewAdapter;
import com.partyideas.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class OfficialMeetupFrag extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    OfficialMeetupRecyclerViewAdapter adapter;
    ArrayList<OfficialEventResponseObject> responseDataSet = new ArrayList<>();
    public OfficialMeetupFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_official_meetup, container, false);
        recyclerView = (RecyclerView)root.findViewById(R.id.recyclerView);

        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new OfficialMeetupRecyclerViewAdapter(responseDataSet);
        recyclerView.setAdapter(adapter);
        adapter.setDetailsOnclickLisener(new OMRecyclerViewListener() {
            @Override
            public void onDetailClick(OfficialEventResponseObject obj) {
                Intent intent = new Intent(getActivity(), OfficialMeetupDetailsActivity.class);
                intent.putExtra("eventObj", obj);
                getActivity().startActivity(intent);
            }
        });
        getResponse();
        return root;
    }

    private void getResponse(){
        Ion.with(getContext())
                .load("http://api.meetup.com/partyideas/events?sign=true&photo-host=public&page=20")
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        if(e!= null)
                            e.printStackTrace();
                        else {
                            try {
                                JSONArray obj = new JSONArray(result.toString());
                                parseResponse(obj);
                            }
                            catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }
    private void parseResponse(JSONArray root){
        try {
            for (int i = 0; i < root.length(); i++) {
                JSONObject obj = root.getJSONObject(i);
                OfficialEventResponseObject respObj = new OfficialEventResponseObject();
                respObj.eventID = obj.getString("id");
                respObj.name = obj.getString("name");
                respObj.status = obj.getString("status");
                respObj.unix = obj.getLong("time");
                respObj.unixOffset = obj.getLong("utc_offset");
                respObj.rsvp_limit = obj.has("rsvp_limit") ? obj.getInt("rsvp_limit") : 0;
                respObj.yes_rsvp = obj.getInt("yes_rsvp_count");
                respObj.link = obj.getString("link");
                respObj.desc = obj.getString("description");
                JSONObject venueObj = obj.getJSONObject("venue");
                respObj.venueName = venueObj.getString("name");
                respObj.venueLat = venueObj.getDouble("lat");
                respObj.venueLon = venueObj.getDouble("lon");
                respObj.venueAddress = venueObj.getString("address_1");
                respObj.venueCity = venueObj.getString("city");
                responseDataSet.add(respObj);
            }
            adapter.notifyDataSetChanged();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
