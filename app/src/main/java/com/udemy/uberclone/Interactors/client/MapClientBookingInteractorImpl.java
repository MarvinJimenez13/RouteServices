package com.udemy.uberclone.Interactors.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.udemy.uberclone.Interfaces.client.map_client_booking.MapClientBookingInteractor;
import com.udemy.uberclone.Interfaces.client.map_client_booking.MapClientBookingPresenter;
import com.udemy.uberclone.R;
import com.udemy.uberclone.Utils.preferences.SharedPreferencesUber;
import com.udemy.uberclone.Utils.providers.AuthProvider;
import com.udemy.uberclone.Utils.providers.ClientBookingProvider;
import com.udemy.uberclone.Utils.providers.DriverProvider;
import com.udemy.uberclone.Utils.providers.GeofireProvider;
import com.udemy.uberclone.Utils.providers.GoogleAPIProvider;
import com.udemy.uberclone.WebServices.APIServices;
import com.udemy.uberclone.WebServices.WebServiceAPI;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapClientBookingInteractorImpl implements MapClientBookingInteractor {

    private MapClientBookingPresenter mapClientBookingPresenter;
    private ClientBookingProvider clientBookingProvider;
    private DriverProvider driverProvider;
    private ValueEventListener valueEventListener, valueStatusBooking;
    private GeofireProvider geofireProvider;
    private List<LatLng> polyLineList;
    private PolylineOptions polylineOptions;

    public MapClientBookingInteractorImpl(MapClientBookingPresenter mapClientBookingPresenter){
        this.mapClientBookingPresenter = mapClientBookingPresenter;
        clientBookingProvider = new ClientBookingProvider();
        driverProvider = new DriverProvider();
        geofireProvider = new GeofireProvider("drivers_working");
    }

    @Override
    public void getClientBooking(AuthProvider authProvider) {
        clientBookingProvider.getClientBooking(authProvider.getID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String destination = snapshot.child("destination").getValue().toString();
                    String origin = snapshot.child("origin").getValue().toString();
                    String idDriver = snapshot.child("idDriver").getValue().toString();
                    getDriverData(idDriver);
                    //ORIGEN DEL CLIENTE
                    double originLat = Double.parseDouble(snapshot.child("originLat").getValue().toString());
                    double originLng = Double.parseDouble(snapshot.child("originLng").getValue().toString());

                    double destinationLat = Double.parseDouble(snapshot.child("destinationLat").getValue().toString());
                    double destinationLng = Double.parseDouble(snapshot.child("destinationLng").getValue().toString());
                    //UBICACION DEL CONDUCTOR EN TIEMPO REAL
                    getDriverLocation(idDriver, authProvider);

                    mapClientBookingPresenter.setLugarRecogida(origin, destination, originLat, originLng, destinationLat, destinationLng);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getDriverLocation(String idDriver, AuthProvider authProvider){
        //METODO PARA TIEMPO REAL
        valueEventListener = geofireProvider.getDriverLocation(idDriver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    double lat = Double.parseDouble(snapshot.child("0").getValue().toString());
                    double lng = Double.parseDouble(snapshot.child("1").getValue().toString());

                    mapClientBookingPresenter.setDriverLocation(lat, lng);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void getStatusBooking(AuthProvider authProvider, Context context){
        valueStatusBooking = clientBookingProvider.getStatus(authProvider.getID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.getValue().toString().equals("START")){
                        String status = SharedPreferencesUber.getInstance(context).getStatusClientBooking();
                        if(!status.equals("START"))
                            mapClientBookingPresenter.startBooking();
                    }else if(snapshot.getValue().toString().equals("FINISH"))
                        mapClientBookingPresenter.finishBooking();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void removeListeners(String idDriver, AuthProvider authProvider) {
        if(valueEventListener != null && valueStatusBooking != null){
            if(idDriver != null)
                geofireProvider.getDriverLocation(idDriver).removeEventListener(valueEventListener);
            clientBookingProvider.getStatus(authProvider.getID()).removeEventListener(valueStatusBooking);
        }
    }

    @Override
    public void drawRoute(LatLng originLatLng, LatLng destinationLatLng, Context context) {
        String origin = originLatLng.latitude + "," + originLatLng.longitude;
        String destination = destinationLatLng.latitude + "," + destinationLatLng.longitude;
        String key = context.getResources().getString(R.string.google_maps_key);
        String url = "/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&origin=" + origin + "&destination=" + destination + "&key=" + key;
        Call<String> call = WebServiceAPI.getInstance().getAPI(APIServices.class).getDirections(url);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try{
                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONArray jsonArray = jsonObject.getJSONArray("routes");
                    JSONObject route = jsonArray.getJSONObject(0);
                    JSONObject polyLines = route.getJSONObject("overview_polyline");
                    String points = polyLines.getString("points");
                    Log.d("POLY", points);
                    polyLineList = GoogleAPIProvider.decodePoly(points);
                    polylineOptions = new PolylineOptions();
                    polylineOptions.addAll(polyLineList);
                    polylineOptions.color(Color.DKGRAY);
                    polylineOptions.width(10);
                    polylineOptions.startCap(new SquareCap());
                    polylineOptions.jointType(JointType.ROUND);

                    JSONArray legs = route.getJSONArray("legs");
                    JSONObject leg = legs.getJSONObject(0);
                    JSONObject distance = leg.getJSONObject("distance");
                    JSONObject duration = leg.getJSONObject("duration");
                    String distanceText = distance.getString("text");
                    String durationText = duration.getString("text");

                    mapClientBookingPresenter.setPolylineRoute(polylineOptions);
                }catch (Exception e){
                    Log.d("ERROR", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void getDriverData(String idDriver){
        driverProvider.getDriver(idDriver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String name = snapshot.child("name").getValue().toString();
                    String email = snapshot.child("email").getValue().toString();
                    mapClientBookingPresenter.setDatosConductor(name, email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
