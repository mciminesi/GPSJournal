package com.app.msc.gpsjournal;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Michael on 8/15/2016.
 * Created from Google Maps practical application module with minor modifications.
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    JournalEntry entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        entry = new JournalEntry();

        // Link MapFragment to fragment manager.
        MapFragment map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_display));
        // Get map.
        map.getMapAsync(this);

        // Get information from passed in intent and put in object.
        Intent intent = getIntent();
        entry.setDate(intent.getStringExtra("date"));
        entry.setNote(intent.getStringExtra("note"));
        entry.setLatitude(intent.getDoubleExtra("latitude", 0));
        entry.setLongitude(intent.getDoubleExtra("longitude", 0));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Create a LatLng object using passed in gps.
        LatLng latlong = new LatLng(entry.getLatitude(),entry.getLongitude());

        // Check for permission and enable current location if available.
        if(ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        // Move map focus to LatLng values above.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 17));
        // Add a marker with the entry date and note using the LatLng values.
        mMap.addMarker(new MarkerOptions()
                .title(entry.getDate())
                .snippet(entry.getNote())
                .position(latlong));
    }
}
