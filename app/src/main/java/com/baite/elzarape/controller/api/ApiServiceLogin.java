package com.baite.elzarape.controller.api;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiServiceLogin {
    @POST("login/validate")
    Call<JsonObject> validateLogin(@Body JsonObject loginRequest);
}