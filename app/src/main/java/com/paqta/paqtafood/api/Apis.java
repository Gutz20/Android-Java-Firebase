package com.paqta.paqtafood.api;

import com.paqta.paqtafood.network.RetrofitClient;
import com.paqta.paqtafood.services.UserService;

public class Apis {

    public static final String URL_001="http://192.168.100.7:8080/";

    public static UserService getUserService() {
        return RetrofitClient.getCliente(URL_001).create(UserService.class);
    }
}
