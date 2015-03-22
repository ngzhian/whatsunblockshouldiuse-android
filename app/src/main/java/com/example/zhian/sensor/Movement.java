package com.example.zhian.sensor;

/**
 * Created by ZhiAn on 13/3/2015.
 */
public class Movement {
    public static double MOVING_THRESHOLD = 0.00001;
    
    public static boolean isMoving(Location[] locations) {
        if (averageDistance(locations) < MOVING_THRESHOLD) {
            return false;
        } else{
            return true;
        }
    }
    
    public static double averageDistance(Location[] locations) {
        double totalDistance = 0;
        int counts = 0;
        for (int i = 0; i < locations.length; i++) {
            for (int j = i; j < locations.length; j++) {
                counts += 1;
                totalDistance += locations[i].euclideanDistance(locations[j]);
            }
        }
        return totalDistance / counts;
    }
    
}
