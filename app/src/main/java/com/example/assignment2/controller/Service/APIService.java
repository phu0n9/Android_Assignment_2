package com.example.assignment2.controller.Service;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAb9LeXcQ:APA91bF6WKzE6ULNChTuzo1D_nYrEJs9T8mHvTRt6-efXuMCDzlwIFaIA8vNB2pyMLCf4tn39hwxjW4rB6d-G4j_0GJg_-dnCkYk3dHLAXvb6TLu5YNNFIgFXIijAS2X4njy5JupO0ar" // Your server key refer to video for finding your server key
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}
