package com.homebrewforlife.sharkydart.anyonecanfish.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.homebrewforlife.sharkydart.anyonecanfish.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class WeatherInfoTasks {
    public static final String ACTION_GET_WEATHER_FORECAST = "get-weather-forecast";
    public static final String ACTION_FOUND_WEATHER_FORECAST = "found-weather-forecast";
    public static final String EXTRA_FORECAST_API_URL = "extra-next-weather-api-url";
    /*
    //url for somewhere in northern kentucky
    public static final String DEFAULT_RETURN_URL = "https://api.weather.gov/gridpoints/ILN/36,36/forecast";
    */
    public static void queryWeatherDotGovTask(Context theContext, String theAction, double theLat, double theLon){
        if(ACTION_GET_WEATHER_FORECAST.equals(theAction)){
            //get the current gps location
            makeFirstWeatherAPIQuery(theContext, theLat, theLon);
        }
    }
    //url formatting
    private static URL buildFirstWeatherAPIUrl(final Context theContext, double theLat, double theLon) {
        /*  Example API call
        api.weather.gov/points/
        lat
        ,
        lon
        */

        Uri builtUri = Uri.parse(theContext.getString(R.string.first_weather_api_baseurl)).buildUpon()
                .appendEncodedPath(theLat + "," + theLon)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();

        }
        return url;
    }
    private static String getResponseFromUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    //actually performing the query
    private static void makeFirstWeatherAPIQuery(final Context theContext, double theLat, double theLon){
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager)theContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager != null) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

                if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                    URL firstWeatherAPIUrl = buildFirstWeatherAPIUrl(theContext, theLat, theLon);
                    try {
                        Log.d("fart","trying getresponsefromurl: " + firstWeatherAPIUrl + "...");
                        String response = getResponseFromUrl(firstWeatherAPIUrl);
                        Log.d("fart","got response: " + response);
                        Log.d("fart","trying parseFirstWeatherJSON...");
                        parseFirstWeatherJSON(theContext, response);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("fart", "weather json parse bork:" + e.toString());
                    }
                }
            }
        }catch(NullPointerException e){
            e.printStackTrace();
        }
    }
    private static void parseFirstWeatherJSON(final Context theContext, String weatherDataJSON){
        if (weatherDataJSON != null) {
            try {
                JSONObject weatherPropertiesObj = new JSONObject(weatherDataJSON).getJSONObject(theContext.getString(R.string.weather_properties_OBJ));
                Log.d("fart", "forecastURL: " + weatherPropertiesObj.getString(theContext.getString(R.string.weather_properties_forecast_STR)));
                //TODO - call the Second weather JSON, using the forecast URL
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(theContext, "First Weather Data Issue", Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(theContext, "First Weather Network #2", Toast.LENGTH_LONG).show();
        }
    }

}
