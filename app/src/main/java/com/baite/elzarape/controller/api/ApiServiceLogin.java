package com.baite.elzarape.controller.api;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiServiceLogin {
    @POST("login/validate")
    Call<JsonObject> validateLogin(@Body JsonObject loginRequest);
}