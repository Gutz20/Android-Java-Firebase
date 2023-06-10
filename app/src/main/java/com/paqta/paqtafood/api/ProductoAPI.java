package com.paqta.paqtafood.api;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProductoAPI {

    public static final String PREFIX = "api/productos";

    @GET(PREFIX + "/{id}/get")
    Call<Boolean> findById(@Path("id") String id);

    @POST(PREFIX + "/add")
    Call<Boolean> add(@Body HashMap<String, Object> user);

    @PUT(PREFIX + "/{id}/update")
    Call<Boolean> edit(@Path("id") String id, @Body HashMap<String, Object> user);

    @PUT(PREFIX + "/{id}/disable")
    Call<Boolean> disable(@Path("id") String id);

    @PUT(PREFIX + "/{id}/enable")
    Call<Boolean> enable(@Path("id") String id);

    @DELETE(PREFIX + "/{id}/delete")
    Call<Boolean> delete(@Path("id") String id);

}
