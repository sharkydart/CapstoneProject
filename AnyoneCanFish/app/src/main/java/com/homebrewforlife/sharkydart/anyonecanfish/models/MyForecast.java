package com.homebrewforlife.sharkydart.anyonecanfish.models;

import java.util.List;

public class MyForecast {
    private String forecastURL;
    private List<forecast_period> forecast_Periods;

    public MyForecast(){}

    public MyForecast(String forecastURL, List<forecast_period> forecast_Periods) {
        this.forecastURL = forecastURL;
        this.forecast_Periods = forecast_Periods;
    }

    public String getForecastURL() {
        return forecastURL;
    }

    public void setForecastURL(String forecastURL) {
        this.forecastURL = forecastURL;
    }

    public List<forecast_period> getForecast_Periods() {
        return forecast_Periods;
    }

    public void setForecast_Periods(List<forecast_period> forecast_Periods) {
        this.forecast_Periods = forecast_Periods;
    }
}
