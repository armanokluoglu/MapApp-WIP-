package com.arman.mapapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.common.Feature;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.maps.android.data.Geometry;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonGeometryCollection;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonMultiPolygon;
import com.google.maps.android.data.geojson.GeoJsonPolygon;
import com.google.maps.android.data.geojson.GeoJsonRenderer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private Location lastLocation;
    private LocationManager mLocationManager;
    private FloatingActionButton fab;
    private Context context = this;
    private Set<String> countries;
    private TextView countryCount;
    private View textEntryView;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private boolean isGPS;
    private final int GPS_REQUEST = 5;
    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 10;

    @SuppressLint({"MissingPermission", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        countries = new HashSet<>();
        fab = findViewById(R.id.fab);
        dl = findViewById(R.id.activity_main);
        t = new ActionBarDrawerToggle(this, dl, R.string.open, R.string.close);

        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.account:
                        Toast.makeText(MapsActivity.this, "My Account", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.settings:
                        Toast.makeText(MapsActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.mycart:
                        Toast.makeText(MapsActivity.this, "My Cart", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });

        textEntryView = nv.getHeaderView(0);
        countryCount = textEntryView.findViewById(R.id.countryCount);

        fab.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new GpsUtils(context).turnGPSOn(new GpsUtils.onGpsListener() {
                    @Override
                    public void gpsStatus(boolean isGPSEnable) {
                        // turn on GPS
                        isGPS = isGPSEnable;
                    }
                });
                lastLocation = getLocation();
                onLocationChanged(lastLocation);
            }
        });

        setCountries();
    }

    @SuppressLint("StaticFieldLeak")
    public List<Address> getAddress(Location location) {
        Geocoder coder = new Geocoder(context, Locale.ENGLISH);
        List<Address> results = null;
        try {
            results = coder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            // nothing
        }
        return results;
    }

    private void setCountries() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        if(sharedPref.contains("com.mapapp.countries")) {
            Set<String> defaultValue = new HashSet<>();
            Set<String> result = sharedPref.getStringSet("com.mapapp.countries", defaultValue);
            countries = result;
            countryCount.setText(countries.size() + "/197");
        }
    }

    private void setColoredCountries(Set<String> countries) {
        /*FLAG listRaw --> countries*/
        Set<String> set = new HashSet<>(listRaw());
        new ColoredCountries(mMap, set, context, this).execute();
    }

    public List<String> listRaw(){
        List<String> jsonFiles = new ArrayList<>();
        Field[] fields = R.raw.class.getFields();
        for(int count=0; count < fields.length; count++){
            jsonFiles.add(fields[count].getName());
        }
        return jsonFiles;

    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Address address = getAddress(location).get(0);
        String country = address.getCountryName();
        String city = address.getAdminArea();
        if(city == null) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(country));
        } else {
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(city + ", " + country));
        }
        if(!countries.contains(country + "\n")) {
            countries.add(country + "\n");
            countryCount.setText(countries.size() + "/197");
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putStringSet("com.mapapp.countries", countries);
            editor.apply();
            Set<String> set = new HashSet<>();
            set.add(country);
            setColoredCountries(set);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        mMap = googleMap;
        setColoredCountries(countries);
        checkPermissions();
        /*mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                final List<CountryLayer> data = DataHolder.getInstance().getData();
                //if(mMap.getCameraPosition().zoom > 12) { // Your required zoom level to show polygons
                    LatLngBounds latLngBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                    for(CountryLayer layer: data) {
                        if(latLngBounds.contains(layer.getSmallest()) || latLngBounds.contains(layer.getLargest()) && !layer.getLayer().getDefaultPolygonStyle().isVisible()) {
                            layer.getLayer().getDefaultPolygonStyle().setVisible(true);
                        }
                    }
                //}
            }
        });*/
    }

    @SuppressLint("MissingPermission")
    public Location getLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean network_enabled = false;
        try {
            network_enabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}
        if(!network_enabled) {
            //displayLocationSettingsRequest(this);
        }
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        Location oldLoc;
        while (true){
            oldLoc = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (oldLoc != null){
                break;
            }
        }
        return oldLoc;
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    mMap.setMyLocationEnabled(true);
                }
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    mMap.setMyLocationEnabled(true);
                }
                return;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(t.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GPS_REQUEST) {
                isGPS = true; // flag maintain before get location        }
            }
        }
    }
}