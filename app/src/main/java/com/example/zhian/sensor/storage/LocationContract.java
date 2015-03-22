package com.example.zhian.sensor.storage;

import android.provider.BaseColumns;

/**
 * Created by ZhiAn on 14/3/2015.
 */
public final class LocationContract {
    public LocationContract() {}
    
    public static abstract class LocationEntry implements BaseColumns {
        public static final String TABLE_NAME = "location";
        public static final String COLUMN_NAME_LOCATION_LAT = "locationlat";
        public static final String COLUMN_NAME_LOCATION_LON = "locationlon";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }
}
