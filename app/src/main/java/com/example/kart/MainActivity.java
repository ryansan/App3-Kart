package com.example.kart;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.kart.fragments.AddBuildingFragment;
import com.example.kart.fragments.AddOrderFragment;
import com.example.kart.fragments.BuildingInfoFragment;
import com.example.kart.fragments.DeleteBuildingFragment;
import com.example.kart.fragments.models.Building;
import com.example.kart.fragments.models.WebHandler;
import com.example.kart.fragments.models.interfaces.AsyncPostResponse;
import com.example.kart.fragments.models.interfaces.AsyncRepsonse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, DialogInterface.OnDismissListener, GoogleMap.OnMarkerClickListener, AsyncRepsonse, AsyncPostResponse {

    public WebHandler webHandler;
    private GoogleMap mMap;
    public HashMap<String, LatLng> latLngs = new HashMap<>();
    List<Building> buildings = new ArrayList<>();


    Context context;
    Activity activity;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

/*//        latLngs.put("Frogner", new LatLng(59.917610,10.710250));
        latLngs.put("Grefsen", new LatLng(59.949970,10.781900));
        latLngs.put("Ekeberg", new LatLng(59.897682,10.779931));*/

        //buildings = WebHandler.getBuildings();
        webHandler = new WebHandler();
        webHandler.delegate = this;
        webHandler.postDelegate = this;
        webHandler.getAsyncBuildings();
        this.activity = this;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng oslo = new LatLng(59.92007100,10.73578000);
        //mMap.addMarker(new MarkerOptions().position(oslo).title("Marker in Oslo"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(oslo));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(oslo.latitude, oslo.longitude), 17.0f));


        /*for (Map.Entry<String, LatLng> entry : latLngs.entrySet()) {
            String key = entry.getKey();
            LatLng value = entry.getValue();

            mMap.addMarker(new MarkerOptions().position(value).title(key));
        }*/



        //setMarkersForBuildings();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {



                mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude), 16.0f));
                Log.d("TAG",String.valueOf(marker.getPosition().latitude));
                Log.d("TAG",String.valueOf(marker.getPosition().longitude));

                Building currentBuilding = null;
                
                for (Building b: buildings) {
                    if(b.getLatLng().equals(marker.getPosition())){
                        currentBuilding = b;
                    }
                }
                
                if(currentBuilding == null){
                    Toast.makeText(MainActivity.this, "Noe gikk feil", Toast.LENGTH_SHORT).show();
                    return false;
                }
                
                BuildingInfoFragment buildingInfoFragment = new BuildingInfoFragment(currentBuilding, buildings);
                buildingInfoFragment.show(getSupportFragmentManager(), "BuildingInfoFragment");

                return true;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                webHandler.getAddress(latLng, activity);

                AddBuildingFragment addBuildingFragment = new AddBuildingFragment(address, latLng);
                addBuildingFragment.show(getSupportFragmentManager(), "AddBuildingFragment");

            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                for (Building b: buildings) {
                    if(Math.abs(b.getLatLng().latitude - latLng.latitude) < 0.0003 && Math.abs(b.getLatLng().longitude - latLng.longitude) < 0.0003) {
                        DeleteBuildingFragment deleteBuildingFragment = new DeleteBuildingFragment(b);
                        deleteBuildingFragment.show(getSupportFragmentManager(),"DeleteBuildignFragment");
                        break;
                    }
                }
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //Toast.makeText(this, String.valueOf(marker.isInfoWindowShown()), Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        //webHandler.postDelegate = null;
        webHandler.postDelegate = this;
        mMap.clear();
        buildings.clear();
        Log.d("Disminss","dismised");

        webHandler.getAsyncBuildings();
    }

    @Override
    public void processFinish(String output) {
        Log.d("PF:", "FINS");
        Log.d("Process Finsihed", output);
        buildings = webHandler.asyncBuildings;
        address = webHandler.address;

        if(!output.equals("POSt ROOM")){
            Toast.makeText(getBaseContext(), " ikke room", Toast.LENGTH_SHORT).show();
            setMarkersForBuildings();
        }else{
            Toast.makeText(getBaseContext(), " room", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void postFinish(String output) {
        Log.d("PF:", "FINS");
        Log.d("Process Finsihed in pst", output);

        if(output.length() < 4) {
            buildings.get(buildings.size()-1).setID(Integer.parseInt(output));
            mMap.clear();
            setMarkersForBuildings();
        }else{
            Log.d("post error", output);
        }
    }

    public void setMarkersForBuildings(){
        for (Building b: buildings) {
            mMap.addMarker(new MarkerOptions().position(b.getLatLng()).title(b.getName()));
        }
    }

    @Override
    protected void onResume() {
        Log.d("MAIN", "ON RESUME");
        super.onResume();
    }
}
