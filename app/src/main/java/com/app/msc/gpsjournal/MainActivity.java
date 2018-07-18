package com.app.msc.gpsjournal;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Michael on 8/13/2016.
 * Photo capture portion was created using the PhotoCapture practical application module.
 * GPS portion was created using the GPS practical application module.
 *
 * Source 1: http://stackoverflow.com/questions/657962/a-non-deprecated-exact-equivalent-of-datestring-s-in-java
 * Source 2:
 */
public class MainActivity extends AppCompatActivity {
    /**
     * A string constant to use in calls to the "log" methods. Its
     * value is often given by the name of the class, as this will
     * allow you to easily determine where log methods are coming
     * from when you analyze your logcat output.
     */
    private static final String TAG = "SampleActivity";

    /**
     * Toggle this boolean constant's value to turn on/off logging
     * within the class.
     */
    private static final boolean VERBOSE = true;


    @Override
    public void onStart() {
        super.onStart();
        if (VERBOSE) Log.v(TAG, "++ ON START ++");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (VERBOSE) Log.v(TAG, "+ ON RESUME +");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (VERBOSE) Log.v(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (VERBOSE) Log.v(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (VERBOSE) Log.v(TAG, "- ON DESTROY -");
    }
    // Used to format date for display and file names.
    SimpleDateFormat initialDateFormat;

    // Variable used to store note.
    EditText note;

    // Variables used for GPS portion of entry.
    TextView labelLatitude,labelLongitude,showLatitude,showLongitude;
    Tracker gpsTracker;
    double latitude, longitude;

    // Variables used to store and display image or video.
    String _filename;
    private Uri fileUri;
    private ImageView imgPreview;
    Bitmap bitmap;

    JournalEntry entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link variables to TextViews.
        labelLatitude = (TextView)findViewById(R.id.txt_x_coord);
        labelLongitude = (TextView)findViewById(R.id.txt_y_coord);
        showLatitude = (TextView)findViewById(R.id.txt_x_coord_disp);
        showLongitude = (TextView)findViewById(R.id.txt_y_coord_disp);

        // Link variable to ImageView.
        imgPreview = (ImageView)findViewById(R.id.imageView);

        // Initialize the JournalEntry object and add Date information here.
        entry = new JournalEntry();
        initialDateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a z");
        entry.setDate(initialDateFormat.format(new Date()));
    }



    // This function is used to get the gps coordinates. It fires when the
    // button is clicked in the activity or when the entry is saved. If
    // location is off, this will prompt the user to turn location on.
    // Precondition: None.
    // Post-condition: Latitude and longitude are updated on the screen and in
    // the JournalEntry.
    public boolean getGPS(View view) {
        gpsTracker = new Tracker(MainActivity.this);

        // If statement based on whether gps is available.
        if(gpsTracker.canGetLocation()){

            // Acquie latitude and longitude from Tracker.
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();

            // Make latitude and longitude results visible.
            labelLongitude.setVisibility(View.VISIBLE);
            labelLatitude.setVisibility(View.VISIBLE);
            showLatitude.setVisibility(View.VISIBLE);
            showLongitude.setVisibility(View.VISIBLE);

            // Display latitude and longitude in TextViews.
            showLatitude.setText(Double.toString(latitude));
            showLongitude.setText(Double.toString(longitude));
            entry.setLatitude(latitude);
            entry.setLongitude(longitude);

            //Toast.makeText(getApplicationContext(), "*....GPS....*", Toast.LENGTH_LONG).show();

            // Return true for successful gps operation.
            return true;
        }else{
            // Ask user to enable gps in case of failure.
            gpsTracker.showSettingsAlert();

            // Return false for unsuccessful gps operation.
            return false;
        }
    }

    // This function runs when the Take Photo button is clicked. It allows the
    // user to take a picture which is used in the next method to update the
    // ImageView with the result.
    // Precondition: Button was clicked. Device has camera.
    // Post-condition: Picture is taken and saved with JournalEntry.getDate() info.
    public void launchCamera(View view) {
        // Set file name to store photo using date information.
        SimpleDateFormat formatDate = new SimpleDateFormat("MMddyyyy-hmmssa");

        // Used to parse the date stored as a String.
        // From source 1.
        Date parsed;
        try {
            parsed = initialDateFormat.parse(entry.getDate());
        }
        catch(ParseException pe) {
            throw new IllegalArgumentException(pe);
        }

        String date = formatDate.format(parsed);
        // End source 1 material.

        // Form the filename string here and store the value in the JournalEntry.
        _filename = "/entryPhoto" + date + ".jpg";
        entry.setPhoto(_filename);

        // Create file path using external storage directory and file name.
        String outputFilePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + _filename;

        // Convert file path to Uri.
        fileUri = Uri.fromFile(new File(outputFilePath));

        // Check for camera on device.
        boolean deviceHasCamera = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA);

        // If cameria exists, start basic camera activity.
        if (deviceHasCamera) {

            // Intent used to launch camera activity.
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // Add file path to intent for storage of picture output.
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // Starts camera activity. Method onActivityResult(...) is called
            // after the camera activty finishes.
            startActivityForResult(intent, 1);

        } else {
            Log.i("CAMERA_APP", "No camera found");
        }
    }

    // This function is called after the camera is closed and is used to update
    // the ImageView of the activity.
    // Precondition: launchCamera() was called with a RESULT_OK code passed as a parameter.
    // Post-condition: ImageView is updated with result from camera activity.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If camera activity passed RESULT_OK, we update the ImageView with the image.
        if (resultCode == RESULT_OK) {
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
                bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                        options);

                // Create rotated bitmap object using above matrix.
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                // Used for image size information.
                Log.i("bitmap: ", bitmap.getWidth() + " "+ bitmap.getHeight() + " " + rotatedBitmap.getWidth() + " " + rotatedBitmap.getHeight());

                // Set ImageView to rotated bitmap object.
                imgPreview.setImageBitmap(rotatedBitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // This function runs when the save entry button is clicked. It completes
    // the JournalEntry object and saves it.
    // Precondition: None.
    // Postcondition: A new JournalEntry object is created using the
    // information entered by the user.
    public void saveEntry(View view) {
        // Link the note variable to its view and use it to set the JournalEntry.
        note = (EditText)findViewById(R.id.editText_note);
        entry.setNote(note.getText().toString());

        // Run getGPS() to ensure gps is updated. If successful, store entry.
        if(getGPS(view)) {
            // Add a new student record.
            // ContentValues variable is created here to store values for storage
            // in database.
            ContentValues values = new ContentValues();

            // Add JournalEntry values to ContentValues object.
            values.put(EntriesProvider.DATE, entry.getDate());
            values.put(EntriesProvider.NOTE, entry.getNote());
            values.put(EntriesProvider.XCOORD, entry.getLatitude());
            values.put(EntriesProvider.YCOORD, entry.getLongitude());
            values.put(EntriesProvider.PHOTO, entry.getPhoto());

            // Store values as an entry using EntriesProvider.
            Uri uri = getContentResolver().insert(EntriesProvider.CONTENT_URI, values);

            // Create intent and pass it to start activity to launch entry viewer.
            Intent intent = new Intent(this, ViewerActivity.class);
            startActivity(intent);
        } else {
            // if getGPS() was unsuccessful, Toast a message that entry was not created.
            Toast.makeText(getApplicationContext(),
                    "Your entry was not created because no GPS coordinates were obtained.",
                    Toast.LENGTH_LONG).show();
        }
    }


    // This function is used to handle changes in configuration.
    // Precondition:
    // Postcondition:
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // ignore orientation change
        if (newConfig.orientation != Configuration.ORIENTATION_LANDSCAPE) {
            super.onConfigurationChanged(newConfig);
        }
    }
//    @Override
//    public void onPause() {
//        super.onPause();
//    }
}
