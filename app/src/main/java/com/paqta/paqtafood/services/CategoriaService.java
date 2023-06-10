package com.paqta.paqtafood.services;

import android.app.Activity;
import android.widget.Toast;

import com.paqta.paqtafood.api.Apis;
import com.paqta.paqtafood.api.CategoriaAPI;
import com.paqta.paqtafood.model.Categoria;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriaService {
    private final CategoriaAPI categoriaAPI = Apis.getCategoriaService();

    public void inhabilitarCategoria(String id, final Activity activity) {
        Call<Boolean> call = categoriaAPI.disable(id);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(activity, "Categoria inhabilitada", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(activity, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
