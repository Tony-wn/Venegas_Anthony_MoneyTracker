package com.venegas.moneytracker.data.remote.model;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class ExchangeRateResponse {
    @SerializedName("base_code")
    private String base;

    @SerializedName("time_last_update_utc")
    private String date;

    @SerializedName("conversion_rates")
    private Map<String, Double> rates;
    public ExchangeRateResponse() {}

    public String getBase() { return base; }
    public void setBase(String base) { this.base = base; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public Map<String, Double> getRates() { return rates; }
    public void setRates(Map<String, Double> rates) { this.rates = rates; }
    public Double getRate(String currency) {
        if (rates != null && rates.containsKey(currency)) {
            return rates.get(currency);
        }
        return null;
    }
}
