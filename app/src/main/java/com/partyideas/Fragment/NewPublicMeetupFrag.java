package com.partyideas.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.partyideas.Activity.MainActivity;
import com.partyideas.Adapter.LocationObject;
import com.partyideas.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerController;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewPublicMeetupFrag extends Fragment implements View.OnClickListener ,DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private String BASE_API_SERVER;
    EditText eventTitle;
    EditText eventDesc;
    TextView dateText;
    TextView timeText;
    Calendar eventDate;
    Spinner locSpinner;
    TextView addressView;
    TextView seekBarDurationView;
    SeekBar durationSeekBar;
    TextView slotText;
    SeekBar slotSeekBar;

    Button confirmButton;

    ArrayList<String> locList = new ArrayList<>();
    ArrayList<LocationObject> locData = new ArrayList<>();
    int selectedLocation = 0;
    ArrayAdapter<String> dataAdapter;

    public NewPublicMeetupFrag() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        BASE_API_SERVER  = getResources().getString(R.string.base_api_server);
        View root = inflater.inflate(R.layout.fragment_new_public_meetup, container, false);
        eventTitle = (EditText)root.findViewById(R.id.eventTitle);
        eventDesc = (EditText)root.findViewById(R.id.descField);
        dateText = (TextView)root.findViewById(R.id.dateText);
        timeText = (TextView)root.findViewById(R.id.timeText);
        locSpinner = (Spinner)root.findViewById(R.id.locationSpinner);
        addressView = (TextView)root.findViewById(R.id.addressView);
        durationSeekBar = (SeekBar)root.findViewById(R.id.durationSeekBar);
        seekBarDurationView = (TextView)root.findViewById(R.id.durationText);
        slotSeekBar = (SeekBar) root.findViewById(R.id.slotSeekBar);
        slotText = (TextView) root.findViewById(R.id.slotText);
        confirmButton = (Button)root.findViewById(R.id.confirm);
        slotSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                slotText.setText(""+(progress+1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        durationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarDurationView.setText(""+(progress+1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(     eventTitle.getText().length()>2 &&
                        eventDesc.getText().length() > 8 &&
                        System.currentTimeMillis() < eventDate.getTimeInMillis()
                  )
                postEvent();
                else
                    Toast.makeText(getContext(),"Invalid data",Toast.LENGTH_SHORT).show();
            }
        });
        dataAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, locList);
        locSpinner.setAdapter(dataAdapter);
        locSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addressView.setText(locData.get(position).address);
                selectedLocation = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        locSpinner.setEnabled(false);
        dateText.setOnClickListener(this);
        timeText.setOnClickListener(this);
        eventDate = Calendar.getInstance();
        getLocation();
        return root;
}
    private void postEvent(){
        SharedPreferences spf = getActivity().getSharedPreferences("account", Context.MODE_PRIVATE);
        JsonObject json = new JsonObject();
        json.addProperty("username",spf.getString("username","foobar"));
        json.addProperty("eventName",eventTitle.getText().toString());
        json.addProperty("rsvpSlot",slotText.getText().toString());
        json.addProperty("unix",eventDate.getTimeInMillis()/1000);
        json.addProperty("duration",Integer.parseInt(seekBarDurationView.getText().toString()) * 3600000);
        json.addProperty("idLoc",locData.get(selectedLocation).id);
        json.addProperty("desc",eventDesc.getText().toString());
        Ion.with(getContext())
                .load(BASE_API_SERVER+"/api/room")
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            JSONObject res = new JSONObject(result.toString());
                            boolean status = res.getBoolean("status");
                            if(status){
                                Toast.makeText(getContext(),"Event created",Toast.LENGTH_SHORT).show();
                                ((MainActivity)getActivity()).togglePublicMeetup();
                            }
                            else{
                                JSONObject err = res.getJSONObject("err");
                                Toast.makeText(getContext(),err.getString("msg"),Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (Exception ex){
                            ex.printStackTrace();
                            Toast.makeText(getContext(),"Network error",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
    private void getLocation(){
        Ion.with(getContext())
                .load(BASE_API_SERVER+"/api/location")
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
                                    int length = obj.getInt("size");
                                    JSONArray data = obj.getJSONArray("data");
                                    for (int i=0;i<length;i++){
                                        JSONObject ApiLocObj = data.getJSONObject(i);
                                        LocationObject locobj = new LocationObject();
                                        locobj.name = ApiLocObj.getString("name");
                                        locobj.address = ApiLocObj.getString("address");
                                        locobj.id = ApiLocObj.getString("id");
                                        locobj.lat = ApiLocObj.getDouble("lat");
                                        locobj.lon = ApiLocObj.getDouble("lon");

                                        locList.add(ApiLocObj.getString("name"));
                                        locData.add(locobj);
                                    }
                                }
                                dataAdapter.notifyDataSetChanged();
                                locSpinner.setEnabled(true);
                            }
                            catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dateText:
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getActivity().getFragmentManager(),"datePicker");
                break;
            case R.id.timeText:
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        this,
                        18,
                        0,
                        false);
                tpd.show(getActivity().getFragmentManager(),"timePicker");
                break;
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        eventDate.set(Calendar.YEAR,year);
        eventDate.set(Calendar.MONTH,monthOfYear);
        eventDate.set(Calendar.DATE,dayOfMonth);
        dateText.setText(new SimpleDateFormat("dd-MM-yyyy").format(eventDate.getTime()));
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        eventDate.set(Calendar.HOUR_OF_DAY,hourOfDay);
        eventDate.set(Calendar.MINUTE,minute);
        timeText.setText(new SimpleDateFormat("HH:mm").format(eventDate.getTime()));
    }
}
