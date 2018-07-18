package com.app.msc.gpsjournal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.net.URI;

/**
 * Created by Michael on 8/14/2016.
 * Bitmap display portion was created using the PhotoCapture practical
 * application module sample code.
 * Launching the google map portion was created using the Google Maps practical
 * application module sample code.
 */
public class DisplayEntryActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_entry);

        // Pick up the Intent used to start this activity.
        Intent intent = getIntent();

        // Link ImageView and TextViews to their layout ids.
        ImageView imageView = (ImageView)findViewById(R.id.image_display);
        TextView txtDate = (TextView)findViewById(R.id.txt_date_display);
        TextView txtNote = (TextView)findViewById(R.id.txt_note_display);
        TextView txtLatitude = (TextView)findViewById(R.id.txt_lat_display);
        TextView txtLongitude = (TextView)findViewById(R.id.txt_long_display);

        // Set TextViews using extras from the intent passed to the activity based on
        // the item clicked on the list.
        txtDate.setText(intent.getStringExtra("date"));
        txtNote.setText(intent.getStringExtra("note"));
        txtLatitude.setText(String.valueOf(intent.getDoubleExtra("latitude", 0)));
        txtLongitude.setText(String.valueOf(intent.getDoubleExtra("longitude", 0)));

        // Get extra for photoString variable.
        String photoString = intent.getStringExtra("photo");

        // If photoString acquired a value for the photo storage location, create a file
        // path and Uri, then update the ImageView with the photo.
        if(photoString != null) {
            String inputFilePath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + photoString;
            Uri fileUri = Uri.fromFile(new File(inputFilePath));

            try {
                // Create matrix to check for unnecessary rotation.
                Matrix matrix = new Matrix();
                ExifInterface ei = new ExifInterface(fileUri.getPath());

                // Gets orientation.
                int orientation = ei.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);

                // Rotates a rotated image back to initial rotation.
                switch (orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                }

                // Options object created to perform downsize to avoid throwing
                // OutOfMemory Exception w/large files.
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;

                // Decode file from storage to bitmap.
                Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);

                // Create rotated bitmap object using above matrix.
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                Log.i("Bitmap testing: ", bitmap.getWidth() + " " + bitmap.getHeight() + " " + rotatedBitmap.getWidth() + " " + rotatedBitmap.getHeight());

                // Set ImageView to rotated bitmap object.
                imageView.setImageBitmap(rotatedBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void launchMap(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        Intent originalIntent = getIntent();

        intent.putExtra("date", originalIntent.getStringExtra("date"));
        intent.putExtra("note", originalIntent.getStringExtra("note"));
        intent.putExtra("latitude", originalIntent.getDoubleExtra("latitude", 0));
        intent.putExtra("longitude", originalIntent.getDoubleExtra("longitude", 0));

        startActivity(intent);
    }
};
