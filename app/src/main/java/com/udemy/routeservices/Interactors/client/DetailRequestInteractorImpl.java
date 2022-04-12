package com.udemy.routeservices.Interactors.client;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.udemy.routeservices.Interfaces.client.detail_request.DetailRequestInteractor;
import com.udemy.routeservices.Interfaces.client.detail_request.DetailRequestPresenter;
import com.udemy.routeservices.R;
import com.udemy.routeservices.Utils.providers.GoogleAPIProvider;
import com.udemy.routeservices.WebServices.APIServices;
import com.udemy.routeservices.WebServices.WebServiceAPI;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailRequestInteractorImpl implements DetailRequestInteractor {

    private DetailRequestPresenter detailRequestPresenter;
    private static List<LatLng> polyLineList;
    private static PolylineOptions polylineOptions;

    public DetailRequestInteractorImpl(DetailRequestPresenter detailRequestPresenter){
        this.detailRequestPresenter = detailRequestPresenter;
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

                    detailRequestPresenter.setRoute(distanceText, durationText, polylineOptions);
                }catch (Exception e){
                    Log.d("ERROR", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

}
