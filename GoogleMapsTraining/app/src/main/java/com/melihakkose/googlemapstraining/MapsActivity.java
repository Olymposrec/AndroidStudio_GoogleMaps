package com.melihakkose.googlemapstraining;

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
import java.sql.SQLOutput;
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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //LongClickListener KULLANMAK ICIN ACTIVITY ILE BAGLAMAMIZ GEREKLI
        mMap.setOnMapLongClickListener(this);

        //KULLANICININ KONUMUNU ALMA
         locationManager= (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //LocationManager ile calisir. KONUM DEGISTIGINDE CALISAN ARAYUZDUR
         locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                /*
                HER KONUM CALISTIGINDA YENI MARKER EKLEMEMESI ICIN HARITAYI TEMIZLIYORUZ
                mMap.clear();
                KULLANICININ OLDUGU YERE MARKER EKLEME
                KULLANICININ YERINI LatLng OBJESINE CEVIRIYORUZ
                LatLng userLocation= new LatLng(location.getLatitude(),location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location!"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,17f));
                 */

                /*GEREKLI BILGILERI BIZIM ICIN ALIR CEVIRIR VE TEKRAR GOSTERMEYE YARAR (Adres Bilgilerini Almaya Calisiyoruz)
                Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());

                try {
                    List<Address> addressList=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                    if(addressList!=null && addressList.size()>0){
                        System.out.println("address: "+addressList.get(0).toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                 */


            }
        };

        //ContextCompat SDK Level' ina gore Sorgu yapmamizi sagliyor
        //IZIN VERILMEDIYSE
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            //IZIN YOK ISE
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else{
            //IZIN VAR ISE
            //Kullanicinin konumunu GPS_PROVIDER ile alarak 0 sürede,0 uzaklikta her zaman guncelleyecegiz
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

            //KULLANICININ BILINEN SON KONUMUNU ALMA
            Location lastLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            /*
            SON KONUM ICIN:
            1- Null exception goz onunde bulundurulmali
            2- son konum yok ise default olarak bir konum tanimlanabilir.
            3- Null deger kontrolu yapilarak Null ise Hata mesaji dondurulebilir.
             */
            if(lastLocation!=null){
                LatLng userLastLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(userLastLocation).title("Your Location!"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLastLocation,17f));
            }

        }

        /* MARKER OLUSTURMA. KAMERA HAREKETI (ZOOM)

        LatLng = ENLEM/BOYLAM
        LatLng izmirClockTower = new LatLng(38.419005, 27.128684);

        mMap.addMarker(new MarkerOptions().position(izmirClockTower).title("Marker in İzmir Clock Tower"));

        ZOOMSUZ KAMERA AYARI => mMap.moveCamera(CameraUpdateFactory.newLatLng(izmirClockTower));

        ZOOM ICIN => mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(izmirClockTower,17f));

         */
    }


    //IZINLERIN KONTROLU
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(grantResults.length>0){
            if(requestCode==1){
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //HARITAYA UZUN BASILDIGI ZAMAN YAPILACAK ISLEMLERI LatLng OLARAK ALIYORUZ.
    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.clear();

        //ENLEM BOYLAMI ALARAK ADRESS LISTESI DONDURUR => Geocoder
        Geocoder geocoder= new Geocoder(getApplicationContext(),Locale.getDefault());

        String address="";
        try {
            List<Address> addressList= geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);

            if(addressList!=null && addressList.size()>0){
                if(addressList.get(0).getThoroughfare()!=null){
                    address+=addressList.get(0).getThoroughfare();

                    if(addressList.get(0).getThoroughfare()!=null){
                        address+=addressList.get(0).getSubThoroughfare();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        mMap.addMarker(new MarkerOptions().position(latLng).title(address));

    }
}