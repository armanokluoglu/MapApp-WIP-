package com.arman.mapapp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;
import org.json.JSONException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Set;

public class ColoredCountries extends AsyncTask<String, Integer, Long> {
    private Activity mapsActivity;
    private GoogleMap mMap;
    private Context context;
    private Set<String> countries;
    private CountryLayerMap map;
    private final long color_countries_done = 25;

    public ColoredCountries(GoogleMap mMap, Set<String> countries, Context context, Activity activity) {
        this.mapsActivity = activity;
        this.mMap = mMap;
        this.map = new CountryLayerMap();
        this.countries = countries;
        this.context = context;
    }

    @Override
    protected Long doInBackground(String... params) {
        for(String string: countries) {
            String newString = string.toLowerCase().trim();
            int resID = getResId(newString, R.raw.class);
            try {
                GeoJsonLayer layer = new GeoJsonLayer(mMap, resID, context);
                GeoJsonPolygonStyle style = layer.getDefaultPolygonStyle();
                style.setFillColor(Color.argb(130, 250, 0, 0));
                style.setStrokeColor(Color.BLACK);
                style.setStrokeWidth(1F);
                style.setVisible(true);
                CountryLayer countryLayer = new CountryLayer(newString, layer, mMap);
                map.addCountryLayer(countryLayer);
                runOnMainThread(countryLayer);
            } catch (IOException ex) {
                Log.e("IOException", ex.getLocalizedMessage());
            } catch (JSONException ex) {
                Log.e("JSONException", ex.getLocalizedMessage());
            } catch (Resources.NotFoundException ex) {
                break;
            }
        }
        return color_countries_done;
    }

    private void runOnMainThread(CountryLayer layer) {
        final CountryLayer countryLayer = layer;
        DataHolder.getInstance().addLayer(countryLayer);
        mapsActivity.runOnUiThread(new Runnable(){
            @Override
            public void run(){
                countryLayer.addLayerToMap();
            }
        });
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
// receive progress updates from doInBackground
    }

    @Override
    protected void onPostExecute(Long result) {
// update the UI after background processes completes
    }

    private int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
