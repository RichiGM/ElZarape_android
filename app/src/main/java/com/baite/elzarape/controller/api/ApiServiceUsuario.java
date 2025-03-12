package com.baite.elzarape.controller.api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiServiceUsuario {
    @GET("usuario/cheecky")
    Call<JsonObject> checkingUser(@Query("nombre") String nombre);
    @FormUrlEncoded
    @POST("usuario/logout")
    Call<JsonObject> logout(@Field("nombreUsuario") String username);
}
