package com.arman.mapapp;

import com.google.maps.android.data.geojson.GeoJsonLayer;

import java.util.ArrayList;
import java.util.List;

public class DataHolder {
    private List<CountryLayer> data = new ArrayList<>();

    private DataHolder(){};
    public List<CountryLayer> getData() {return data;}
    public void setData(List<CountryLayer> data) {this.data = data;}
    public void addLayer(CountryLayer layer) {data.add(layer);}

    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {return holder;}
}