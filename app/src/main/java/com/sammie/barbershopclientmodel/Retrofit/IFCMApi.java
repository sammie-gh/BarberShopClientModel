package com.sammie.barbershopclientmodel.Retrofit;


import com.sammie.barbershopclientmodel.Model.FCMResponse;
import com.sammie.barbershopclientmodel.Model.FCMSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {
    @Headers({
            "content-Type:application/json",
            "Authorization:key=AAAAAd4Tccc:APA91bEIsZzQQ2r9YVs79iYz9H1Xy8plbIH7wsiYcDAG-vvybtbDc2XTQ00oGc208kaVB-6stQ5FmllyJcMgJBzTJyt0f4Msobb6zG7pmwYkCAwf02Y4m8_824l8RrI2vS_cM-1uxykD"
    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);

}
