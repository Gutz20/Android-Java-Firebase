package com.paqta.paqtafood.api;

import com.paqta.paqtafood.network.RetrofitClient;

public class Apis {

    public static final String URL_001="http://192.168.100.7:8080/";

    public static UserAPI getUserService() {
        return RetrofitClient.getCliente(URL_001).create(UserAPI.class);
    }

    public static ProductoAPI getProductoService() {
        return RetrofitClient.getCliente(URL_001).create(ProductoAPI.class);
    }

    public static CategoriaAPI getCategoriaService() {
        return RetrofitClient.getCliente(URL_001).create(CategoriaAPI.class);
    }

    public static  PedidoAPI getPedidoService() {
        return RetrofitClient.getCliente(URL_001).create(PedidoAPI.class);
    }

}
