package com.paqta.paqtafood.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paqta.paqtafood.serialization.TimestampDeserializer;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RetrofitClient {

    public static Retrofit getCliente(String url) {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(loggingInterceptor);

        ObjectMapper objectMapper = new ObjectMapper();
        TimestampDeserializer.registerDeserializer(objectMapper);

        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(httpClientBuilder.build())
                .build();
    }

}
