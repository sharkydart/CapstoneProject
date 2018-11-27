package com.homebrewforlife.sharkydart.anyonecanfish.models;

public class forecast_period {
    /*
        represents weather data for a less-than 24 hr period of time
     */
    private int number; //id
    private String name;
    private String startTime;
    private String endTime;
    private String isDayTime;   //lowercase boolean
    private double temperature;
    private String temperatureUnit;
    private String windSpeed;
    private String windDirection;   //wind from the west, fishing is best; wind from the east, fishing is least
    private String iconURL;
    private String shortForecast;
    private String detailedForecast;

    public forecast_period(){}

    public forecast_period(int number, String name, String startTime, String endTime,
                           String isDayTime, double temperature, String temperatureUnit, String windSpeed,
                           String windDirection, String iconURL, String shortForecast, String detailedForecast) {
        this.number = number;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isDayTime = isDayTime;
        this.temperature = temperature;
        this.temperatureUnit = temperatureUnit;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.iconURL = iconURL;
        this.shortForecast = shortForecast;
        this.detailedForecast = detailedForecast;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getIsDayTime() {
        return isDayTime;
    }

    public void setIsDayTime(String isDayTime) {
        this.isDayTime = isDayTime;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getTemperatureUnit() {
        return temperatureUnit;
    }

    public void setTemperatureUnit(String temperatureUnit) {
        this.temperatureUnit = temperatureUnit;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public String getShortForecast() {
        return shortForecast;
    }

    public void setShortForecast(String shortForecast) {
        this.shortForecast = shortForecast;
    }

    public String getDetailedForecast() {
        return detailedForecast;
    }

    public void setDetailedForecast(String detailedForecast) {
        this.detailedForecast = detailedForecast;
    }
}
