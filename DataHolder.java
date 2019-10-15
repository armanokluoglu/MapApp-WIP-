package com.arman.mapapp;

import com.google.maps.android.data.geojson.GeoJsonLayer;

import java.util.ArrayList;
import java.util.List;

public class DataHolder {
    private List<GeoJsonLayer> data = new ArrayList<>();

    private DataHolder(){};
    public List<GeoJsonLayer> getData() {return data;}
    public void setData(List<GeoJsonLayer> data) {this.data = data;}
    public void addLayer(GeoJsonLayer layer) {data.add(layer);}

    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {return holder;}
}