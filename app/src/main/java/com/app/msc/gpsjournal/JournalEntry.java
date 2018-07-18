package com.app.msc.gpsjournal;

import java.io.Serializable;

/**
 * Created by Michael on 8/13/2016.
 */
public class JournalEntry implements Serializable {
    public int entryID;
    private double latitude;
    private double longitude;
    private String note;
    private String date;
    private String photo;


    public double getLatitude() { return latitude; }
    public void setLatitude(double val) { latitude = val; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double val) { longitude = val; }

    public String getNote() { return note; }
    public void setNote(String val) { note = val; }

    public String getDate() { return date; }
    public void setDate(String val) { date = val; }

    public String getPhoto() { return photo; }
    public void setPhoto(String val) { photo = val; }
}
