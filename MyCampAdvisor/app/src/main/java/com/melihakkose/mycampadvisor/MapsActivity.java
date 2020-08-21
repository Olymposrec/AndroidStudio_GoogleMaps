package com.melihakkose.mycampadvisor;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    //GOOGLE MAP OLUSTURUCU KODLAR
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // KULLANICININ YERINI SAPTAMAK ISTERSEK KULLANMAMIZ GEREKEN SINIF
         locationManager= (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        // LocationManager YARDIMCISI LocationListener
         locationListener= new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                System.out.println("Location: "+location.toString());

                //KULLANICI YERINI DEGISTIRINCE MARKERLARI SILER
                mMap.clear();

                LatLng userLocation =new LatLng(location.getLatitude(),location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));

                //ADRESLERI ENLEM BOYLAMA DONDUREN VEYA TERSINE CEVIREN ISLEM

            }
        };



        // IZIN ALMA IF-ELSE
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            // IZINLER LISTESI
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,20,20,locationListener);

            //SON BILINEN LOKASYON Location degiskenini LatLng cevirip mMap e yonlendirdik
            Location lastLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            System.out.println("Last Location:"+ lastLocation);
            LatLng userLastLocation=new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().title("Your Location").position(userLastLocation));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLastLocation,15));
        }
        mMap.setOnMapLongClickListener(this);

        // Add a marker in Sydney and move the camera
        // KIRMIZI MARKER EKLEYEN KODLAR ve MARKER EKLENECEK YERIN ENLEM BOYLAMI
        LatLng izmirsaat = new LatLng(38.419001, 27.128679);
        mMap.addMarker(new MarkerOptions().position(izmirsaat).title("Marker in İzmir Saat Kulesi"));
        //newLatLngZoom == kullanici uygulamayi actiginda zoomlar
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(izmirsaat,17));
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // IZINLER GELDIYSE
        if(grantResults.length>0){
            if(requestCode==1){
                //IZNI VERILDI MI?
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,20,20,locationListener);
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.clear();
        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());

        String address="";
        try {
            List<Address> addressList=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if(addressList!=null && addressList.size()>0){
                if(addressList.get(0).getSubThoroughfare()!=null){
                    address+=addressList.get(0).getAddressLine(0);

                    if(addressList.get(0).getSubThoroughfare()!=null){
                        address+=addressList.get(0).getSubThoroughfare();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        if(address.matches("")){
            address="No Address";
        }
        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
    }
}