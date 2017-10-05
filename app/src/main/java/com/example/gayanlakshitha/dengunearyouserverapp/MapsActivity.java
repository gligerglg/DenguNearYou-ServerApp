package com.example.gayanlakshitha.dengunearyouserverapp;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Geocoder geo;
    Button btn_getLocation;
    TextView txt_location;
    List<Address> addresses;
    double latitude=0;
    double longitude=0;
    String location="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btn_getLocation = (Button)findViewById(R.id.btn_getLocation);
        txt_location = (TextView) findViewById(R.id.txt_maplocation);
        geo = new Geocoder(this, Locale.getDefault());
        btn_getLocation.setEnabled(false);
    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
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
        btn_getLocation.setEnabled(true);
        // Add a marker in Sydney and move the camera
        LatLng srilanka = new LatLng(6.9218386, 79.8562055);
        mMap.addMarker(new MarkerOptions().position(srilanka).title("Sri Lanka"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(srilanka,7));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng));
                try {
                    addresses = geo.getFromLocation(latitude,longitude,1);
                    location = addresses.get(0).getSubAdminArea();
                    txt_location.setText(""+location);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btn_getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = txt_location.getText().toString();
                Intent intent = new Intent(MapsActivity.this,AddNewEntry.class);
                Bundle b = new Bundle();
                b.putString("location",location);
                b.putDouble("latitude",latitude);
                b.putDouble("longitude",longitude);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }


}
