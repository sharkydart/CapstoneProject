package com.homebrewforlife.sharkydart.anyonecanfish.models;

import java.util.List;

public class MyForecast {
    private String forecastURL;
    private List<ForecastPeriod> forecastPeriods;

    public MyForecast(){}

    public MyForecast(String forecastURL, List<ForecastPeriod> forecastPeriods) {
        this.forecastURL = forecastURL;
        this.forecastPeriods = forecastPeriods;
    }

    public String getForecastURL() {
        return forecastURL;
    }

    public void setForecastURL(String forecastURL) {
        this.forecastURL = forecastURL;
    }

    public List<ForecastPeriod> getForecastPeriods() {
        return forecastPeriods;
    }

    public void setForecastPeriods(List<ForecastPeriod> forecastPeriods) {
        this.forecastPeriods = forecastPeriods;
    }
}
