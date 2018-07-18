package com.app.msc.gpsjournal;

/**
 * Created by Michael on 8/14/2016.
 */
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomListAdapter extends ArrayAdapter<JournalEntry> {


    private Activity activity;
    private ArrayList<JournalEntry> listEntries;
    private static LayoutInflater inflater = null;
    int textViewResourceId = R.layout.row_layout;

    public CustomListAdapter(Activity activity, int textViewResourceId, ArrayList<JournalEntry> entries) {
        super(activity, textViewResourceId, entries);
        try {
            this.activity = activity;
            this.listEntries= entries;

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } catch (Exception e) {
            // Don't need to do anything.
        }

    }

    public int getCount() {
        return listEntries.size();
    }

    public JournalEntry getItem(JournalEntry position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView display_date;
        public TextView display_note;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.row_layout, null);
                holder = new ViewHolder();

                holder.display_date = (TextView) vi.findViewById(R.id.txt_dates);
                holder.display_note = (TextView) vi.findViewById(R.id.txt_notes);


                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }

            holder.display_date.setText(listEntries.get(position).getDate());
            holder.display_note.setText(listEntries.get(position).getNote());

        } catch (Exception e) {


        }
        return vi;
    }
}