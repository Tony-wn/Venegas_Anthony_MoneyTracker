package com.venegas.moneytracker.data.remote;

import com.venegas.moneytracker.data.remote.api.ExchangeRateApi;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // --- CORRECCIÓN ---
    // La URL base correcta para la v6 de la API
    private static final String BASE_URL = "https://v6.exchangerate-api.com/";

    private static RetrofitClient instance;
    private Retrofit retrofit;

    private RetrofitClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public ExchangeRateApi getApi() {
        return retrofit.create(ExchangeRateApi.class);
    }
}
