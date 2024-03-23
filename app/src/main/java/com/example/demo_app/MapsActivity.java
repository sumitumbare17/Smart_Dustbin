package com.example.demo_app;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.demo_app.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener  {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
       /* LatLng pune = new LatLng(18, 73);
        mMap.addMarker(new MarkerOptions().position(pune).title("Marker in pune"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pune));*/
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        // Adding a marker at the touched location


        if(Build.VERSION.SDK_INT >= 29){
            //we need Background permission

            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED){
                handleMapLongClick(latLng);
            }
            else{
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)){
                    // we show a dialog and ask permission

                    ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_BACKGROUND_LOCATION},BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
                else
                {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);

                }
            }
        }
        else{
            handleMapLongClick(latLng);
        }

       /* Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title("New Marker"));

        // Display a toast message with the latitude and longitude of the marker
        Toast.makeText(this, "Latitude: " + latLng.latitude + ", Longitude: " + latLng.longitude, Toast.LENGTH_SHORT).show();*/
    }

    private void handleMapLongClick(LatLng latLng){

        addMarker(latLng);
    }

    public void addMarker(LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);
        // Display a toast message with the latitude and longitude of the marker
        Toast.makeText(this, "Latitude: " + latLng.latitude + ", Longitude: " + latLng.longitude, Toast.LENGTH_SHORT).show();
    }
}