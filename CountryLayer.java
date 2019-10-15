package com.arman.mapapp;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.data.Geometry;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import java.util.ArrayList;
import java.util.List;

public class CountryLayer {
    private String name;
    private GeoJsonLayer layer;
    private List<LatLng> coordinates;
    private List<LatLng> edges;

    public CountryLayer(String name, GeoJsonLayer layer) {
        this.name = name;
        this.layer = layer;
        this.edges = new ArrayList<>();
        for(GeoJsonFeature feature: layer.getFeatures()) {
            if(feature.hasGeometry()) {
                Geometry geometry = feature.getGeometry();
                this.coordinates = getCoordinatesFromGeometry(geometry);
            }
        }
        detectEdges();
    }

    public String getName() {
        return name;
    }

    public GeoJsonLayer getLayer() {
        return layer;
    }

    public List<LatLng> getCoordinates() {
        return coordinates;
    }

    public List<LatLng> getEdges() {
        return edges;
    }

    public void setEdges(List<LatLng> edges) {
        this.edges = edges;
    }

    private void detectEdges() {
        LatLng south = new LatLng(0,0);
        LatLng west = new LatLng(0,0);
        LatLng north = new LatLng(0,0);
        LatLng east = new LatLng(0,0);
        LatLng southWest = new LatLng(0,0);
        LatLng southEast = new LatLng(0,0);
        LatLng northWest = new LatLng(0,0);
        LatLng northEast = new LatLng(0,0);
        for(LatLng coord: coordinates) {
            if(coord.latitude > north.latitude) {
                north = coord;
            } else if(coord.latitude < south.latitude){
                south = coord;
            }
            if(coord.longitude > east.longitude) {
                east = coord;
            } else if(coord.longitude < west.longitude) {
                west = coord;
            }
            if((coord.latitude > northEast.latitude) && (coord.longitude > northEast.longitude)) {
                northEast = coord;
            }
            if((coord.latitude < southWest.latitude) && (coord.longitude < southWest.longitude)) {
                southWest = coord;
            }
            if((coord.latitude > northWest.latitude) && (coord.longitude < northWest.longitude)) {
                northWest = coord;
            }
            if((coord.latitude < southEast.latitude) && (coord.longitude > southEast.longitude)) {
                southEast = coord;
            }
        }
        edges.add(south);
        edges.add(west);
        edges.add(north);
        edges.add(east);
        edges.add(southWest);
        edges.add(southWest);
        edges.add(northWest);
        edges.add(northEast);
    }

    private List<LatLng> getCoordinatesFromGeometry(Geometry geo) {
        List<LatLng> list = new ArrayList<>();
        String geoStr = ((ArrayList) geo.getGeometryObject()).get(0).toString();
        geoStr = geoStr.replaceAll("[^0-9.,]", "");
        String[] strings = geoStr.split(",");
        for(int i = 0; i < strings.length; i = i + 2) {
            LatLng newLatLng = new LatLng(Double.parseDouble(strings[i]), Double.parseDouble(strings[i+1]));
            list.add(newLatLng);
        }
        return list;
    }
}
