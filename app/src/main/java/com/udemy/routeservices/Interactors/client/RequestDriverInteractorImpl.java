package com.udemy.routeservices.Interactors.client;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.udemy.routeservices.Interfaces.client.request_driver.RequestDriverInteractor;
import com.udemy.routeservices.Interfaces.client.request_driver.RequestDriverPresenter;
import com.udemy.routeservices.Models.ClientBooking;
import com.udemy.routeservices.Models.FCMBody;
import com.udemy.routeservices.Models.FCMResponse;
import com.udemy.routeservices.R;
import com.udemy.routeservices.Utils.providers.AuthProvider;
import com.udemy.routeservices.Utils.providers.ClientBookingProvider;
import com.udemy.routeservices.Utils.providers.GeofireProvider;
import com.udemy.routeservices.Utils.providers.TokenNotiProvider;
import com.udemy.routeservices.WebServices.APIServices;
import com.udemy.routeservices.WebServices.NotificationServiceAPI;
import com.udemy.routeservices.WebServices.WebServiceAPI;
import org.json.JSONArray;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestDriverInteractorImpl implements RequestDriverInteractor {

    //Variable para poder finalizar el listener cuando acabemos.
    public static ValueEventListener valueEventListener;
    private RequestDriverPresenter requestDriverPresenter;
    private GeofireProvider geofireProvider;
    private TokenNotiProvider tokenNotiProvider;
    private static ClientBookingProvider clientBookingProvider;

    public RequestDriverInteractorImpl(RequestDriverPresenter requestDriverPresenter){
        this.requestDriverPresenter = requestDriverPresenter;
        geofireProvider = new GeofireProvider("active_drivers");
        tokenNotiProvider = new TokenNotiProvider();
        clientBookingProvider = new ClientBookingProvider();
    }

    @Override
    public void getClosestDrivers(LatLng originLatLng, double radius) {
        geofireProvider.getActiveDrivers(originLatLng, radius).addGeoQueryEventListener(new GeoQueryEventListener() {

            //ESTE METODO SE ACTIVA CUANDO ENCUENTRA UN REGISTRO, DEVUELVE EL KEY QUE ES EL ID DEL DRIVER
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                requestDriverPresenter.driverFound(key, location);
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                //INGRESA CUANDO TERMINA LAS BUSQUEDAS DE CONDUCTORES EN UN RADIO DE 0.1 KM.
                requestDriverPresenter.driverNotFound();
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @Override
    public void createClientBooking(LatLng originLatLng, LatLng driverFoundLatLng, String driverFoundID, Context context) {
        String origin = originLatLng.latitude + "," + originLatLng.longitude;
        String destination = driverFoundLatLng.latitude + "," + driverFoundLatLng.longitude;
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
                    JSONArray legs = route.getJSONArray("legs");
                    JSONObject leg = legs.getJSONObject(0);
                    JSONObject distance = leg.getJSONObject("distance");
                    JSONObject duration = leg.getJSONObject("duration");
                    String distanceText = distance.getString("text");
                    String durationText = duration.getString("text");

                    buildNotification(driverFoundID, durationText, distanceText);
                }catch (Exception e){
                    Log.d("ERROR", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    @Override
    public void sendNotification(FCMBody body, ClientBooking clientBooking, AuthProvider authProvider) {
        Call<FCMResponse> call = NotificationServiceAPI.getInstance().getAPI(APIServices.class).sendFCM(body);
        call.enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                if(response.body() != null){
                    if(response.body().getSuccess() == 1){
                        clientBookingProvider.create(clientBooking).addOnSuccessListener(unused -> {
                            requestDriverPresenter.success("La peticion se creo correctamente.");
                            checkStatusClientBooking(clientBookingProvider, authProvider);
                        });
                    }else
                        requestDriverPresenter.showError("Error al enviar a conductor.");
                }
            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {
                Log.d("TAG1Error", t.getMessage());
            }
        });
    }

    @Override
    public void removeListener(AuthProvider authProvider) {
        if(valueEventListener != null)
            clientBookingProvider.getStatus(authProvider.getID()).removeEventListener(valueEventListener);
    }

    private void buildNotification(String idDriver, String time, String km){
        /*
         * El metodo addListenerForSingl...... sirve para obtener el valor de la DB pero solo 1 vez
         * AL PARECER EN TIEMPO REAL.
         *
         * */
        if(idDriver != null){
            tokenNotiProvider.getToken(idDriver).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        if(snapshot.hasChild("token")){           //para evitar errores
                            String token = snapshot.child("token").getValue().toString();
                            requestDriverPresenter.buildNotification(token, time, km);
                        }else//Si no existe
                            requestDriverPresenter.toMapClient();
                    }else
                        requestDriverPresenter.showError("No se pudo enviar la notificacion, error en la sesion.");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else
            requestDriverPresenter.toMapClient();
    }

    private void checkStatusClientBooking(ClientBookingProvider clientBookingProvider, AuthProvider authProvider){
        //.addValueEvent..... Escucha en tiempo real.
        valueEventListener = clientBookingProvider.getStatus(authProvider.getID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String status = snapshot.getValue().toString();
                    if(status.equals("ACCEPT")){
                        requestDriverPresenter.toMapClientBooking();
                    }else if(status.equals("CANCEL")){
                        requestDriverPresenter.showError("El conductor no acepto el viaje");
                        requestDriverPresenter.toMapClient();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
