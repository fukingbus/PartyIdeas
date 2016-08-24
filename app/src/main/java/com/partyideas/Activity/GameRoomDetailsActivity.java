package com.partyideas.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.partyideas.Adapter.CustomEventResponseObject;
import com.partyideas.R;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class GameRoomDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private String BASE_API_SERVER;
    public CustomEventResponseObject CMObject;
    LinearLayout mapField;
    LinearLayout membersField;
    LinearLayout joinField;
    LinearLayout saveField;
    TextView mapTitle;
    TextView mapAddress;
    TextView time;
    TextView duration;
    TextView members;
    TextView desc;
    TextView status;
    CardView joincard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BASE_API_SERVER  = getResources().getString(R.string.base_api_server);
        setContentView(R.layout.activity_game_room_details);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CMObject = (CustomEventResponseObject) getIntent().getSerializableExtra("data");
        mapField = (LinearLayout)findViewById(R.id.mapField);
        membersField = (LinearLayout)findViewById(R.id.membersField);
        mapTitle = (TextView)findViewById(R.id.maptitle);
        mapAddress = (TextView)findViewById(R.id.mapaddress);
        time = (TextView)findViewById(R.id.time);
        duration = (TextView)findViewById(R.id.duration);
        members = (TextView)findViewById(R.id.members);
        desc = (TextView)findViewById(R.id.desc);
        status = (TextView)findViewById(R.id.statusTxt);
        joincard = (CardView) findViewById(R.id.joincard) ;
        joinField = (LinearLayout)findViewById(R.id.joinfield);
        saveField = (LinearLayout)findViewById(R.id.savefield);
        joinField.setOnClickListener(this);
        saveField.setOnClickListener(this);
        setTitle(CMObject.name);
        time.setText(unixToDateTime(CMObject.unix*1000L));
        duration.setText(getDuration(CMObject.duration));
        desc.setText(CMObject.desc.isEmpty() ? getResources().getString(R.string.no_desc) : CMObject.desc);
        setStatusText(CMObject.status);

        getLocation();
    }
    private void setStatusText(String str){
        switch (str){
            case "PENDING":
                status.setText(getResources().getString(R.string.pending));
                break;
            case "APPROVED":
                status.setText(getResources().getString(R.string.approved));
                status.setTextColor(Color.GREEN);
                joincard.setVisibility(View.GONE);
                break;
            case "CANCELLED":
                status.setText(getResources().getString(R.string.cancelled));
                status.setTextColor(Color.RED);
                joincard.setVisibility(View.GONE);
                break;
        }
    }
    private void getLocation(){
        Ion.with(getApplicationContext())
                .load(BASE_API_SERVER + "/api/location/"+CMObject.venueID)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if(result.get("status").getAsBoolean()){
                            JsonObject obj = result.get("data").getAsJsonArray().get(0).getAsJsonObject();
                            CMObject.venueID = obj.get("id").getAsInt();
                            CMObject.venueAddress = obj.get("address").getAsString();
                            CMObject.venueLat = obj.get("lat").getAsLong();
                            CMObject.venueLon = obj.get("lon").getAsLong();
                            CMObject.venueName = obj.get("name").getAsString();
                            mapTitle.setText(CMObject.venueName);
                            mapAddress.setText(CMObject.venueAddress);
                        }
                    }
                });
    }
    private String unixToDateTime(long unix){
        Date df = new java.util.Date(unix);
        return new SimpleDateFormat("MM, dd, yyyy hh:mm a").format(df);
    }
    private String getDuration(long duration){
        return String.format("%d Hours %d Minutes",
                TimeUnit.MILLISECONDS.toHours(duration),
                TimeUnit.MILLISECONDS.toMinutes(duration) -
                        TimeUnit.MILLISECONDS.toHours(duration)*60
        );
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.joinfield:
                SharedPreferences spf = getSharedPreferences("account",MODE_PRIVATE);
                if(spf.getBoolean("status",false)){
                    final ProgressDialog pdialog = ProgressDialog.show(getApplicationContext(),"Processing","Occupying the seat for you",true);

                }
                else {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.you_must_log_in_first),Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.savefield:
                break;
        }
    }
}
