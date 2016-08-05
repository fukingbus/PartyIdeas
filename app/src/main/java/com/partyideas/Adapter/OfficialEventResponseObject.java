package com.partyideas.Adapter;

import java.io.Serializable;

/**
 * Created by xeonyan on 5/8/2016.
 */
public class OfficialEventResponseObject  implements Serializable {
    public String eventID;
    public String name;
    public String status;
    public long unix;
    public long unixOffset;
    public int rsvp_limit;
    public int yes_rsvp;
    public String venueAddress;
    public String venueName;
    public double venueLat;
    public double venueLon;
    public String venueCity;
    public String link;
    public String desc;
}
