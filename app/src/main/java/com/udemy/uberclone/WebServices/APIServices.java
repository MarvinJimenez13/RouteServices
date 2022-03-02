package com.udemy.uberclone.WebServices;

import com.udemy.uberclone.Models.FCMBody;
import com.udemy.uberclone.Models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface APIServices {

    @GET
    Call<String> getDirections(@Url String url);

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAnJttMIE:APA91bEcprSpxl6Nk4u0VqJxb0iyBrJoO6wHIGEyOf_9SuQ74tqo4pz8xzVG6vuk5-kiPMOyNoBXbL_iys53tOfl1VPQxYuhe6HayUe47FeZenPFjuQn6xTdYwNF3S4KlYlxGmBO4RqS"
    })
    @POST("/fcm/send")
    Call<FCMResponse> sendFCM(@Body FCMBody body);

}
