package com.example.zhian.sensor.storage;

import android.os.Environment;
import android.util.Log;

import com.example.zhian.sensor.Constants;
import com.example.zhian.sensor.Location;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhiAn on 14/3/2015.
 */
public class Storage {
//    private static final String LOG_TAG = "com.example.zhian";
    private static final String FILENAME = "sensor.csv";

    public static boolean saveLocation(Location location) {
        return saveLocation(location, true);
    }
    
    public static boolean saveLocation(Location location, boolean append) {
        File storage = getLocationStorage();
        if (storage == null) {
            Log.d(Constants.LOG_TAG, "Unable to get storage");
            return false;
        }
        
        File file = new File(storage + File.separator + FILENAME);
        Writer fo = null;
        try {
            file.createNewFile();
            fo = new BufferedWriter(
                    new OutputStreamWriter(
                        new FileOutputStream(file, append)));
            if (file.exists()) {
                fo.write(location.toCsv());
                fo.write("\n");
            }
        } catch (IOException ex) {
            Log.d(Constants.LOG_TAG, "IOException in LocationSampler");
        } finally {
            try {fo.close();} catch (Exception ex) {}
        }
        Log.d(Constants.LOG_TAG, "LocationSampler WROTE handleActionSample started.");
        return true;
    }
    
    public static List readLastFourLocation() {
        File storage = getLocationStorage();
        File file = new File(storage + File.separator + FILENAME);

        List input = new ArrayList<String>();
        List lastFour = new ArrayList<String>();
        String line = null;
        try {
            InputStreamReader irs = new InputStreamReader(new FileInputStream(file));
            BufferedReader br = new BufferedReader(irs);
            while ((line = br.readLine()) != null) {
                input.add(line);
            }
            for (int i = input.size() - 1; i >= 0; i--) {
                lastFour.add(input.get(i));
                if (lastFour.size() >= 4) {
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lastFour;
    }

    public static boolean clearLocation() {
        File storage = getLocationStorage();
        if (storage == null) {
            Log.d(Constants.LOG_TAG, "Unable to get storage");
            return false;
        }

        File file = new File(storage + File.separator + FILENAME);
        return file.delete();
    }
    
    public static File getLocationStorage() {
        if (!isExternalStorageWritable()) { return null; }
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Sensor");
        if (file.exists()) {
            return file;
        }
        if (!file.mkdirs()) {
            Log.e(Constants.LOG_TAG, "Directory not created");
        }
        return file;
    }
    
    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
