package com.udemy.uberclone.Views.client;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.maps.android.SphericalUtil;
import com.udemy.uberclone.Interfaces.client.map_client.MapClientPresenter;
import com.udemy.uberclone.Interfaces.client.map_client.MapClientView;
import com.udemy.uberclone.Presenters.client.MapClientPresenterImpl;
import com.udemy.uberclone.R;
import com.udemy.uberclone.Utils.includes.MyToolbar;
import com.udemy.uberclone.Utils.preferences.SharedPreferencesUber;
import com.udemy.uberclone.Utils.providers.AuthProvider;
import com.udemy.uberclone.Utils.providers.PermissionsProvider;
import com.udemy.uberclone.Utils.providers.TokenNotiProvider;
import com.udemy.uberclone.Views.MainActivity;
import com.udemy.uberclone.Utils.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MapClientActivity extends AppCompatActivity implements OnMapReadyCallback, MapClientView {

    public static GoogleMap map;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    public static LatLng currentLatLng;
    private boolean isFirstTime = true;
    public static boolean mOriginSelect = true;
    private SupportMapFragment supportMapFragment;
    private Unbinder mUnbinder;
    private AuthProvider authProvider;
    public static AutocompleteSupportFragment autocompleteSupportFragment, autocompleteSupportFragmentDestino;
    private PlacesClient placesClient;
    public static String origin, destination;
    public static LatLng originLatLng, destinationLatLng;
    public TokenNotiProvider tokenNotiProvider;
    private MapClientPresenter mapClientPresenter;
    private List<Marker> driversMarkers = new ArrayList<>();
    private GoogleMap.OnCameraIdleListener onCameraIdleListener;

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    //OBTENER LOCALIZACION DEL USUARIO EN TIEMPO REAL
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(15f)
                                    .build()
                    ));

                    if (isFirstTime) {
                        isFirstTime = false;
                        mapClientPresenter.getActiveDrivers(currentLatLng, map, driversMarkers);
                        limitSearch();
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_client);
        mUnbinder = ButterKnife.bind(this);

        MyToolbar.show(this, "Cliente", false, toolbar);

        authProvider = new AuthProvider();
        tokenNotiProvider = new TokenNotiProvider();

        autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.places);
        autocompleteSupportFragmentDestino = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.placesDestino);

        mapClientPresenter = new MapClientPresenterImpl(MapClientActivity.this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        inicializarAutoComplete();

        //SharedPref para recuperar el viaje
        String status = SharedPreferencesUber.getInstance(MapClientActivity.this).getStatusClientBooking();
        String idDriver = SharedPreferencesUber.getInstance(MapClientActivity.this).getIDDriverBookingClient();
        if(status.equals("RIDE") || status.equals("START"))
            goToMapClientBookingActivity(idDriver);
        else {
            generateTokenNoti();
            mapClientPresenter.onCameraListener(this, map);
        }

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapClient);
        supportMapFragment.getMapAsync(this);
    }

    private void goToMapClientBookingActivity(String idDriver){
        Intent intent = new Intent(MapClientActivity.this, MapClientBookingActivity.class);
        intent.putExtra("idDriver", idDriver);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (PermissionsProvider.gpsActived(this)){
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                        map.setMyLocationEnabled(true);
                    }else
                        PermissionsProvider.showAlertDialogGPS(this, this);
                } else
                    PermissionsProvider.checkLocationPermissions(this);
            } else
                PermissionsProvider.checkLocationPermissions(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.SETTINGS_REQUEST_CODE && PermissionsProvider.gpsActived(this)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            map.setMyLocationEnabled(true);
        } else if (requestCode == Constants.SETTINGS_REQUEST_CODE && !PermissionsProvider.gpsActived(this))
            PermissionsProvider.showAlertDialogGPS(this, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.client_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout)
            logout();

        if(item.getItemId() == R.id.action_history)
            verHistorial();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setOnCameraIdleListener(onCameraIdleListener);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(5);

        startLocation();
    }


    @OnClick(R.id.floatingActionChange)
    public void onClickFloat(){
        Log.d("TAG1", "CLICK");
        if(mOriginSelect){
            Toast.makeText(MapClientActivity.this, "Estas seleccionando el lugar de destino.", Toast.LENGTH_LONG).show();
            mOriginSelect = false;
        }else{
            Toast.makeText(MapClientActivity.this, "Estas seleccionando el lugar de recogida.", Toast.LENGTH_LONG).show();
            mOriginSelect = true;
        }
    }

    @OnClick(R.id.btnSolicitar)
    public void onClick(){
        if(originLatLng != null && destinationLatLng != null){
            Intent intent = new Intent(this, DetailRequestActivity.class);
            intent.putExtra("origin_lat", originLatLng.latitude);
            intent.putExtra("origin_lng", originLatLng.longitude);
            intent.putExtra("destination_lat", destinationLatLng.latitude);
            intent.putExtra("destination_lng", destinationLatLng.longitude);
            intent.putExtra("origin", origin);
            intent.putExtra("destination", destination);
            Log.d("TAG1", originLatLng.latitude + " " + originLatLng.longitude);
            startActivity(intent);
        }else
            Toast.makeText(this, "Debes seleccionar tu lugar de recogida y destino.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void addActiveDriverMarkers(GeoLocation location, DataSnapshot dataSnapshot) {
        for(Marker marker: driversMarkers){
            if(marker.getTag() != null){
                if(marker.getTag().equals(dataSnapshot.getKey())){
                    return;
                }
            }
        }

        LatLng driverLatLng = new LatLng(location.latitude, location.longitude);
        Marker marker = map.addMarker(new MarkerOptions().position(driverLatLng).title("Conductor disponible").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_vehicle)));
        marker.setTag(dataSnapshot.getKey());
        driversMarkers.add(marker);
    }

    @Override
    public void removeDriverMarkers(DataSnapshot dataSnapshot) {
        for(Marker marker: driversMarkers){
            if(marker.getTag() != null){
                if(marker.getTag().equals(dataSnapshot.getKey())){
                    marker.remove();
                    driversMarkers.remove(marker);
                    return;
                }
            }
        }
    }

    @Override
    public void setCameraIDListener(GoogleMap.OnCameraIdleListener onCameraIdleListener) {
        this.onCameraIdleListener = onCameraIdleListener;
    }

    @Override
    public void setAutoCompleteInfoOrigin(String adress, String city, LatLng originLatLng) {
        autocompleteSupportFragment.setText(adress + " " + city);
        origin = adress + " " + city;
        this.originLatLng = originLatLng;
    }

    @Override
    public void setAutoCompleteInfoDestination(String adress, String city, LatLng destinationlatLng) {
        autocompleteSupportFragmentDestino.setText(adress + " " + city);
        destination = adress + " " + city;
        this.destinationLatLng = destinationlatLng;
    }

    @Override
    public void setDestinationSelected(String place, LatLng latLng) {
        Log.d("TAG1", "DESTINATION:" + place);
        destination = place;
        destinationLatLng = latLng;
    }

    @Override
    public void setOriginSelected(String place, LatLng latLng) {
        Log.d("TAG1", "ORIGIN:" + place);
        origin = place;
        originLatLng = latLng;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    public void logout(){
        authProvider.logout();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void verHistorial(){
        startActivity(new Intent(MapClientActivity.this, HistoryBookingClientActivity.class));
    }

    private void inicializarAutoComplete(){
        if(!Places.isInitialized())
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));

        placesClient = Places.createClient(this);
        //ORIGEN
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        autocompleteSupportFragment.setHint("Origen");
        //DESTINO
        autocompleteSupportFragmentDestino.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        autocompleteSupportFragmentDestino.setHint("Destino");

        mapClientPresenter.setOnPlaceSelectListener();
    }

    private void startLocation(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                if(PermissionsProvider.gpsActived(this)){
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    map.setMyLocationEnabled(true);
                }else
                    PermissionsProvider.showAlertDialogGPS(this, this);
            }else
                PermissionsProvider.checkLocationPermissions(this);
        }else{
            if(PermissionsProvider.gpsActived(this)){
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                map.setMyLocationEnabled(true);
            }else
                PermissionsProvider.showAlertDialogGPS(this, this);
        }
    }

    public void generateTokenNoti(){
        tokenNotiProvider.create(authProvider.getID());
    }

    public static void limitSearch(){
        LatLng northSide = SphericalUtil.computeOffset(currentLatLng, 5000, 0);
        LatLng southSide = SphericalUtil.computeOffset(currentLatLng, 500, 180);
        autocompleteSupportFragment.setCountry("MX");
        autocompleteSupportFragment.setLocationBias(RectangularBounds.newInstance(southSide, northSide));
        autocompleteSupportFragmentDestino.setCountry("MX");
        autocompleteSupportFragmentDestino.setLocationBias(RectangularBounds.newInstance(southSide, northSide));
    }

}