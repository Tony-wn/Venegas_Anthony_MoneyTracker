package com.venegas.moneytracker.data.remote.api;

import com.venegas.moneytracker.data.remote.model.ExchangeRateResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ExchangeRateApi {
    @GET("v6/{apiKey}/latest/{currency}")
    Call<ExchangeRateResponse> getExchangeRates(
            @Path("apiKey") String apiKey,
            @Path("currency") String currency
    );
}
