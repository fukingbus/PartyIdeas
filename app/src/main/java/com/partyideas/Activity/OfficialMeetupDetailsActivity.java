package com.partyideas.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.partyideas.Adapter.OfficialEventResponseObject;
import com.partyideas.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OfficialMeetupDetailsActivity extends AppCompatActivity {

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official_meetup_details);
        final OfficialEventResponseObject eventObj = (OfficialEventResponseObject) getIntent().getSerializableExtra("eventObj");
        setTitle(eventObj.name);
        MapFragment mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                LatLng latInfo = new LatLng(eventObj.venueLat,eventObj.venueLon);
                Marker venue = map.addMarker(new MarkerOptions()
                        .position(latInfo)
                        .title(eventObj.venueCity)
                        .snippet("Party Ideas")
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.mipmap.ic_launcher)));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latInfo, 18));
                //map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
            }
        });
        ((TextView)findViewById(R.id.timeSlot)).setText(unixToDateTime(eventObj.unix));
        ((TextView)findViewById(R.id.rsvp)).setText(eventObj.yes_rsvp + " going");
        ((TextView)findViewById(R.id.avaspot)).setText(eventObj.rsvp_limit!=0 ? eventObj.rsvp_limit+" slots" : "Unlimited");
        ((TextView)findViewById(R.id.address)).setText(eventObj.venueAddress);
        String html = "<html><head><style>img{display: inline; height: auto; max-width: 90%;}</style></head><body>"+eventObj.desc+"</body></html>";
        ((WebView)findViewById(R.id.webView)).loadData(html, "text/html; charset=UTF-8", null);
    }
    private String unixToDateTime(long unix){
        Date df = new java.util.Date(unix);
        return new SimpleDateFormat("MM, dd, yyyy hh:mm a").format(df);
    }
}
