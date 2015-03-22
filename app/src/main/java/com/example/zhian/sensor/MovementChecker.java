package com.example.zhian.sensor;

import java.util.List;

public class MovementChecker {
    private static final double THRESHOLD = 0.00001;
    private List<Location> locations;

    public MovementChecker(List<Location> locations) {
        this.locations = locations;
    }

    protected boolean check() {
        double totalDistanceDifferent = 0;
        for (int i = 0; i < locations.size(); i++) {
            for (int j = i; j < locations.size(); j++) {
                totalDistanceDifferent += locations.get(i).euclideanDistance(locations.get(j));
            }
        }
        double averageDistanceDifferent = totalDistanceDifferent / locations.size();

        if (averageDistanceDifferent > THRESHOLD) {
            return true;
        } else {
            return false;
        }
    }
}
