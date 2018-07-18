package com.app.msc.gpsjournal;

import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Michael on 8/13/2016.
 * Source 1: http://stackoverflow.com/questions/657962/a-non-deprecated-exact-equivalent-of-datestring-s-in-java
 */
public class ViewerActivity extends AppCompatActivity {
    private ListView listView;

    private ArrayAdapter<String> adapter;
    ArrayList<JournalEntry> data;
    private CustomListAdapter listAdapter;

    String URL = "content://com.msc.contentprovider.EntriesProvider/entries";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        listView = (ListView)findViewById(R.id.list_saved_entries);
        data = new ArrayList<>();

        Uri entries = Uri.parse(URL);

        // Query database to get all entries for display and store cursor.
        Cursor c = getContentResolver().query(entries, null, null, null, "date");

        // If the cursor is not null and can move to an item, then the cursor will fill in
        // JournalEntry information from each cursor row.
        if (c != null && c.moveToFirst()) {
            do{
                JournalEntry entry = new JournalEntry();
                entry.setDate(c.getString(c.getColumnIndex(EntriesProvider.DATE)));
                entry.setNote(c.getString(c.getColumnIndex(EntriesProvider.NOTE)));
                entry.setLatitude(Double.parseDouble(c.getString(c.getColumnIndex(EntriesProvider.XCOORD))));
                entry.setLongitude(Double.parseDouble(c.getString(c.getColumnIndex(EntriesProvider.YCOORD))));
                entry.setPhoto(c.getString(c.getColumnIndex(EntriesProvider.PHOTO)));

                //
                try {
                    data.add(entry);
                } catch(NullPointerException e) {
                    Log.e("NullPointerEx:", "catching add entry");
                };
            } while (c.moveToNext());

            c.close();
        }

        //Collections.addAll(data, test);
        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        //listView.setAdapter(adapter);

        // Set listAdapter to a CustomListAdapter interacting with the data ArrayList.
        listAdapter = new CustomListAdapter(ViewerActivity.this, 0, data);
        // Set the ListView's adapter to the listAdapter.
        listView.setAdapter(listAdapter);

        // Set on click listener for the list items to launch the entry display activity.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Here we pull the clicked on entry from the array and then cast it from
                // an Object to a JournalEntry.
                Object listItem = listView.getItemAtPosition(i);
                JournalEntry entry = (JournalEntry) listItem;

                Intent intent = new Intent(getApplicationContext(), DisplayEntryActivity.class);

                // Add JournalEntry information to intent for use in entry display.
                intent.putExtra("date", entry.getDate());
                intent.putExtra("note", entry.getNote());
                intent.putExtra("latitude", entry.getLatitude());
                intent.putExtra("longitude", entry.getLongitude());
                intent.putExtra("photo", entry.getPhoto());

                startActivity(intent);
            }
        });

        // Set on click listener for the button to launch the splash menu.
        Button btnSplash = (Button)findViewById(R.id.btn_splash);
        btnSplash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                startActivity(intent);
            }
        });
    }
}
