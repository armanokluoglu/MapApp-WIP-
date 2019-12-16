package com.arman.mapapp;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.data.Geometry;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import java.util.ArrayList;
import java.util.List;

public class CountryLayer {
    private GoogleMap map;
    private String name;
    private GeoJsonLayer layer;
    private BinaryTree coordinates;
    private LatLng smallest;
    private LatLng largest;

    public CountryLayer(String name, GeoJsonLayer layer, GoogleMap map) {
        this.name = name;
        this.layer = layer;
        this.map = map;
        this.coordinates = new BinaryTree();
        for(GeoJsonFeature feature: layer.getFeatures()) {
            if(feature.hasGeometry()) {
                Geometry geometry = feature.getGeometry();
                List<LatLng> coords = getCoordinatesFromGeometry(geometry);
                for(int i = 0; i < coords.size(); i = i + 1){
                    try {
                        coordinates.insert(coords.get(i));
                    } catch (DuplicateException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        smallest = BinaryTree.smallest(coordinates.getRoot());
        largest = BinaryTree.largest(coordinates.getRoot());
    }

    public LatLng getSmallest() {
        return smallest;
    }

    public LatLng getLargest() {
        return largest;
    }

    public String getName() {
        return name;
    }

    public GeoJsonLayer getLayer() {
        return layer;
    }

    public BinaryTree getCoordinates() {
        return coordinates;
    }

    public void addLayerToMap() {
        layer.addLayerToMap();
    }

    private List<LatLng> getCoordinatesFromGeometry(Geometry geo) {
        List<LatLng> list = new ArrayList<>();
        String geoStr = ((ArrayList) geo.getGeometryObject()).get(0).toString();
        geoStr = geoStr.replaceAll("[^0-9.,]", "");
        String[] strings = geoStr.split(",");
        for(int i = 0; i < strings.length; i = i + 2000) {
            LatLng newLatLng = new LatLng(Double.parseDouble(strings[i]), Double.parseDouble(strings[i+1]));
            list.add(newLatLng);
        }
        return list;
    }
}
