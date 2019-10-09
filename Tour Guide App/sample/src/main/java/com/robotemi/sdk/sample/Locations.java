package com.robotemi.sdk.sample;
import java.util.Arrays;
import java.util.List;

/*
 * Created by Shubham Jindal
 */

public class Locations {

    static int currentLocation = 0;

    public class LocationStructure {
        String Name, Description, Image;
        public LocationStructure(String name, String description, String image) {
            Name = name;
            Description = description;
            Image = image;
        }
    }

    public List<LocationStructure> LocationData = Arrays.asList(
            new LocationStructure("Launchpad Squad", "We have reached the Launchpad Squad", "screen2"),
            new LocationStructure("Student Central", "We have reached the Student Central", "screen4"),
            new LocationStructure("Cafe", "We have reached the Cafe", "screen3")

    );
}
