package com.partyideas.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.partyideas.Activity.MainActivity;
import com.partyideas.Adapter.CustomEventResponseObject;
import com.partyideas.Adapter.CustomMeetupRecyclerViewAdapter;
import com.partyideas.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomMeetupFrag extends Fragment {
    private String BASE_API_SERVER;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private LinearLayoutManager mLayoutManager;
    private CustomMeetupRecyclerViewAdapter adapter;
    private ArrayList<CustomEventResponseObject> dataset = new ArrayList<>();
    public CustomMeetupFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        BASE_API_SERVER  = getResources().getString(R.string.base_api_server);
        View root = inflater.inflate(R.layout.fragment_custom_meetup, container, false);
        recyclerView = (RecyclerView)root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new CustomMeetupRecyclerViewAdapter(dataset);
        recyclerView.setAdapter(adapter);
        fab = (FloatingActionButton)root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences spf = getActivity().getSharedPreferences("account", Context.MODE_PRIVATE);
                if(spf.getBoolean("status",false))
                    ((MainActivity)getActivity()).toggleCreatePublicMeetup();
                else
                    Toast.makeText(getActivity(),getResources().getString(R.string.you_must_log_in_first),Toast.LENGTH_SHORT).show();
            }
        });

        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        getRoom();
        return root;
    }

    private void getRoom(){
        Ion.with(getContext())
                .load(BASE_API_SERVER+"/api/room")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if(e!= null)
                            e.printStackTrace();
                        else {
                            try {
                                JSONObject obj = new JSONObject(result.toString());
                                if(obj.getBoolean("status")){
                                    JSONArray data = obj.getJSONArray("data");
                                    for (int i=0;i<data.length();i++){
                                        JSONObject item = data.getJSONObject(i);
                                        CustomEventResponseObject cero = new CustomEventResponseObject();
                                        cero.name = item.getString("name");
                                        cero.yes_rsvp = item.getInt("rsvp_yes");
                                        cero.rsvp_limit = item.getInt("rsvp_limit");
                                        cero.unix = item.getLong("unix");
                                        cero.duration = item.getLong("length");
                                        cero.desc = item.getString("description");
                                        cero.status = item.getString("status");
                                        dataset.add(cero);

                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }

}
