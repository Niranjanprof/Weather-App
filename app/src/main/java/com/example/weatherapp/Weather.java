package com.example.weatherapp;

public class Weather {
    private String weatherName;
    private String url;

    public Weather(String weatherName, String urls) {
        this.weatherName = weatherName;
        this.url = urls;
    }

    public String getWeatherName() {
        return weatherName;
    }

    public void setWeatherName(String weatherName) {
        this.weatherName = weatherName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
