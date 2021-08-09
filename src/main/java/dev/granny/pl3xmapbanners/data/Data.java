package dev.granny.pl3xmapbanners.data;

import net.pl3x.map.api.Key;
import net.pl3x.map.api.marker.Marker;

public class Data {
    private final Marker marker;
    private final Key key;
    private final String name;

    public Data(Marker marker, Key key, String name) {
        this.marker = marker;
        this.key = key;
        this.name = name;
    }

    public Marker getMarker() {
        return marker;
    }

    public Key getKey() {
        return this.key;
    }

    public String getName() {
        return name;
    }
}
