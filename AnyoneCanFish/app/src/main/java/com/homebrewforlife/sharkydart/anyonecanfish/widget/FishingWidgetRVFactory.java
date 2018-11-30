package com.homebrewforlife.sharkydart.anyonecanfish.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.homebrewforlife.sharkydart.anyonecanfish.MainActivity;
import com.homebrewforlife.sharkydart.anyonecanfish.R;
import com.homebrewforlife.sharkydart.anyonecanfish.handwavyfishingmagic.OptimusCalculatron;
import com.homebrewforlife.sharkydart.anyonecanfish.models.ForecastPeriod;
import com.homebrewforlife.sharkydart.anyonecanfish.models.SolunarData;
import com.homebrewforlife.sharkydart.anyonecanfish.models.SolunarPhenomena;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FishingWidgetRVFactory implements RemoteViewsService.RemoteViewsFactory {

    private ArrayList<ForecastPeriod> myForecastObj = new ArrayList<>();
    private SolunarData mySolunarObj = new SolunarData();

    private Context theContext;
    private Intent theIntent;

    public FishingWidgetRVFactory(Context context, Intent intent) {
        this.theContext = context;
        this.theIntent = intent;
    }

    @Override
    public int getCount() {
        int theReturnCount;
//        if(FishingWidgetProvider.mMyForecastPeriod != null)
//            theReturnCount = FishingWidgetProvider.mTheIngredients.size();
//        else
            theReturnCount = myForecastObj.size();
        return theReturnCount;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public RemoteViews getLoadingView() {
        return null;
    }


    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteView = new RemoteViews(theContext.getPackageName(), R.layout.fishing_widget_forecast_detail);

//        remoteView.setTextViewText(android.R.id.text1, collection.get(position).getTheName());
//        if(FishingWidgetProvider.mTheIngredients != null && FishingWidgetProvider.mTheIngredients.size() > 0) {
//            remoteView.setTextViewText(android.R.id.text1, FishingWidgetProvider.mTheIngredients.get(position).getInfo());
//            remoteView.setTextColor(android.R.id.text1, Color.BLUE);
//            final Intent thisIntent = new Intent();
//            thisIntent.setAction(FishingWidgetProvider.ACTION_GOBACK);
//            remoteView.setOnClickFillInIntent(android.R.id.text1, thisIntent);
//        }
//        else {
        ImageView img = new ImageView(theContext);
        Picasso.get().load(myForecastObj.get(position).getIconURL()).into(img);
            remoteView.setImageViewResource(R.id.widgetForecastIcon, img.getId());
//            if(goodChancesToday(position) == 1)
//                remoteView.setImageViewResource(R.id.widgetOutlookImg, );

        Log.d("fart", "firing up OptimusCalculatron: " +
                OptimusCalculatron.howWillTheFishingBe(myForecastObj.get(position), mySolunarObj));

            remoteView.setTextViewText(R.id.widgetDay, myForecastObj.get(position).getName());
            remoteView.setTextViewText(R.id.widgetWindDir, myForecastObj.get(position).getWindDirection());
            remoteView.setTextViewText(R.id.widgetWindSpeed, myForecastObj.get(position).getWindSpeed());
            String temp_withUnits = myForecastObj.get(position).getTemperature() + " " + myForecastObj.get(position).getTemperatureUnit();
            remoteView.setTextViewText(R.id.widgetTemperature, temp_withUnits);

            remoteView.setTextViewText(android.R.id.text1, myForecastObj.get(position).getName());
            remoteView.setTextColor(android.R.id.text1, Color.BLACK);

            //adds information unique to the view item at the position into a bundle, which is put in the intent for the list item
            // ...defining the unique action
            final Intent fillInIntent = new Intent();
            fillInIntent.setAction(FishingWidgetProvider.ACTION_TOAST);
            final Bundle theBundle = new Bundle();
            theBundle.putParcelable(FishingWidgetProvider.THE_FORECAST_PERIOD_DETAILS, myForecastObj.get(position));
            fillInIntent.putExtras(theBundle);
            remoteView.setOnClickFillInIntent(android.R.id.text1, fillInIntent);
//        }
        return remoteView;
    }

    @Override
    public void onCreate() {
        if(FishingWidgetProvider.mMyForecastPeriod == null)
            initData();
    }

    @Override
    public void onDataSetChanged() {
        if(FishingWidgetProvider.mMyForecastPeriod == null)
            initData();
    }

    private void initData(){
        if(myForecastObj != null)
            myForecastObj.clear();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(theContext);
        String theForecastD = sharedPref.getString(MainActivity.RAW_FORECAST_DATA_SHAREDPREFS_CACHE, "A widget doesn't have a forecast.");
        String theSolunarD = sharedPref.getString(MainActivity.RAW_SOLUNAR_DATA_SHAREDPREFS_CACHE, "A widget doesn't have a sun or a moon.");

        Log.d("fart", "theForecastD: " + theForecastD);
        Log.d("fart", "theSolunarD: " + theSolunarD);

        ArrayList<ForecastPeriod> myForecastObj = parseLocalWeatherForecastJSON(theForecastD);
        SolunarData mySolunarObj = parseSolunarApiResponseJSON(theSolunarD);
    }
    private ArrayList<ForecastPeriod> parseLocalWeatherForecastJSON(String forecastApiData){
        if (forecastApiData != null && !forecastApiData.isEmpty()) {
            try {
                //try and find JUST the periods data
                JSONObject forecastPropertiesObj = new JSONObject(forecastApiData).getJSONObject("properties");
                JSONArray pArray = forecastPropertiesObj.getJSONArray("periods");
                ArrayList<ForecastPeriod> theForecast = new ArrayList<>();
                for(int i=0; i<pArray.length(); i++){
                    ForecastPeriod tPeriod = new ForecastPeriod();
                    tPeriod.setNumber(pArray.getJSONObject(i).getInt("number"));
                    tPeriod.setName(pArray.getJSONObject(i).getString("name"));
                    tPeriod.setStartTime(pArray.getJSONObject(i).getString("startTime"));
                    tPeriod.setEndTime(pArray.getJSONObject(i).getString("endTime"));
                    tPeriod.setIsDayTime(pArray.getJSONObject(i).getBoolean("isDaytime"));
                    tPeriod.setTemperature(pArray.getJSONObject(i).getDouble("temperature"));
                    tPeriod.setTemperatureUnit(pArray.getJSONObject(i).getString("temperatureUnit"));
                    tPeriod.setWindSpeed(pArray.getJSONObject(i).getString("windSpeed"));
                    tPeriod.setWindDirection(pArray.getJSONObject(i).getString("windDirection"));
                    tPeriod.setIconURL(pArray.getJSONObject(i).getString("icon"));
                    tPeriod.setShortForecast(pArray.getJSONObject(i).getString("shortForecast"));
                    tPeriod.setDetailedForecast(pArray.getJSONObject(i).getString("detailedForecast"));
                    theForecast.add(tPeriod);
                }
                return theForecast;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            Log.d("fart", "error");
        }
        return null;
    }
    private SolunarData parseSolunarApiResponseJSON(String solunarJSONData){
        if (solunarJSONData != null && !solunarJSONData.isEmpty()) {
            try {
                SolunarData solunarData = new SolunarData();
                //try and find the data
                JSONObject solDataObj = new JSONObject(solunarJSONData);

                JSONArray sunArray = solDataObj.getJSONArray("sundata");
                ArrayList<SolunarPhenomena> theSundata = new ArrayList<>();
                for(int i=0; i<sunArray.length(); i++){
                    SolunarPhenomena tPheno = new SolunarPhenomena();
                    tPheno.setPhen(sunArray.getJSONObject(i).getString("phen"));
                    tPheno.setTime(sunArray.getJSONObject(i).getString("time"));
                    theSundata.add(tPheno);
                }
                solunarData.setSundata(theSundata);

                JSONArray moonArray = solDataObj.getJSONArray("moondata");
                ArrayList<SolunarPhenomena> theMoondata = new ArrayList<>();
                for(int i=0; i<moonArray.length(); i++){
                    SolunarPhenomena tPheno = new SolunarPhenomena();
                    tPheno.setPhen(moonArray.getJSONObject(i).getString("phen"));
                    tPheno.setTime(moonArray.getJSONObject(i).getString("time"));
                    theMoondata.add(tPheno);
                }
                solunarData.setMoondata(theMoondata);

                String dayofweek = solDataObj.getString("dayofweek");
                solunarData.setDayOfWeek(dayofweek);
                String closestPhase = solDataObj.getJSONObject("closestphase").getString("phase");
                solunarData.setClosestPhase(closestPhase);

                //there might not be a fracillum
                String fracillum = "";
                if(solDataObj.has("fracillum"))
                    fracillum = solDataObj.getString("fracillum");
                solunarData.setFracillum(fracillum);

                //there might not be a curphase
                String curphase = "";
                if(solDataObj.has("curphase"))
                    curphase = solDataObj.getString("curphase");
                solunarData.setCurphase(curphase);

                return solunarData;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            Log.d("fart", "solunar data issue");
        }
        return null;
    }


    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
