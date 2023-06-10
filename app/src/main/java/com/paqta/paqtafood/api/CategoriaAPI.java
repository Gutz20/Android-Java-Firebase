package com.paqta.paqtafood.api;

import com.paqta.paqtafood.model.Categoria;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CategoriaAPI {

    public static final String PREFIX = "api/categorias";

    @GET(PREFIX + "/listar")
    Call<List<Categoria>> listar();

    @POST(PREFIX + "/nuevo")
    Call<Boolean> add(@Body HashMap<String, Object> categoria);

    @PUT(PREFIX + "/[id}/update")
    Call<Boolean> edit(@Path("id") String id, @Body HashMap<String, Object> categoria);

    @PUT(PREFIX + "/{id}/disable")
    Call<Boolean> disable(@Path("id") String id);

    @PUT(PREFIX + "/{id}/enable")
    Call<Boolean> enable(@Path("id") String id);

    @DELETE(PREFIX + "/{id}/delete")
    Call<Boolean> delete(@Path("id") String id);

}
