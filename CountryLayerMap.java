package com.arman.mapapp;

import java.util.HashMap;
import java.util.Map;

public class CountryLayerMap {
    private Map<String, CountryLayer> map;

    public CountryLayerMap() {
        this.map = new HashMap<>();
    }

    public void addCountryLayer(CountryLayer countryLayer) {
        this.map.put(countryLayer.getName(), countryLayer);
    }

    public void removeCountryLayer(String name) {
        this.map.remove(name);
    }
}
