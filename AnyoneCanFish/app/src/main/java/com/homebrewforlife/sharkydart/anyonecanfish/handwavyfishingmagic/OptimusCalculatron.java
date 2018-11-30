package com.homebrewforlife.sharkydart.anyonecanfish.handwavyfishingmagic;

import android.icu.text.StringSearch;

import com.homebrewforlife.sharkydart.anyonecanfish.models.Fire_GameFish;
import com.homebrewforlife.sharkydart.anyonecanfish.models.ForecastPeriod;
import com.homebrewforlife.sharkydart.anyonecanfish.models.SolunarData;

public class OptimusCalculatron {
    public static double howWillTheFishingBe(ForecastPeriod thePeriod, SolunarData earthlyBodies){
        double start = 50.0; //50%
        //is it cold?
        start = start + (thePeriod.getTemperature() - 60);

        //is there rain, snow, ice, hail, sleet in the forecast?
        if(thePeriod.getDetailedForecast().contains("rain") ||
                thePeriod.getDetailedForecast().contains("drizzle") ||
                thePeriod.getDetailedForecast().contains("shower"))
            start = start * 0.67;
        if(thePeriod.getDetailedForecast().contains("hail") ||
                thePeriod.getDetailedForecast().contains(" ice ") ||
                thePeriod.getDetailedForecast().contains("snow") || thePeriod.getDetailedForecast().contains("sleet"))
            start = start * 0.6;

        //if windspeed(-mph) is >= 6 and <= 16
        if(thePeriod.getWindSpeed().contains("to")) {
            String[] spds = thePeriod.getWindSpeed().split("to");
            if (Double.valueOf(spds[0].replace("mph","")) >= 6 && Double.valueOf(spds[1].replace("mph","")) <= 16)
                start = start * 1.12;
        }
        else {
            String windSpd = thePeriod.getWindSpeed().replace("mph", "");
            Double wndSpd = Double.valueOf(windSpd);
            if (wndSpd >= 6 && wndSpd <= 16)
                start = start * 1.12;
        }
        if(thePeriod.getWindDirection().contains("W"))
            start = start * 1.12;
        if(thePeriod.getWindDirection().contains("E"))
            start = start * 0.80;

        if(earthlyBodies.getFracillum() != null && !earthlyBodies.getFracillum().isEmpty())
        {
            Double thePercentPhase = Double.valueOf(earthlyBodies.getFracillum().replace("%","")) /100;
            Double changedOdds = (thePercentPhase - 0.5)*20;
            start = start + changedOdds;
        }

        return start;
    }
    public static double howWillTheFishingBe(ForecastPeriod thePeriod, SolunarData earthlyBodies, Fire_GameFish fishType)
    {
        double start = howWillTheFishingBe(thePeriod, earthlyBodies);
        //some fish are more or less active at night
        if(thePeriod.getIsDayTime()){
            switch (fishType.getSpecies()){
                case "largemouth bass":
                case "smallmouth bass":
                case "northern pike":
                case "muskellunge":
                case "rainbow trout":
                    start=start+10;
                    break;
                case "walleye":
                    start=start-10;
                    break;
                case "channel catfish":
                    start=start-5;
                    break;
            }
        }
        else
        {
            switch (fishType.getSpecies()){
                case "largemouth bass":
                case "smallmouth bass":
                case "northern pike":
                case "muskellunge":
                case "rainbow trout":
                    start=start-10;
                    break;
                case "walleye":
                    start=start+15;
                    break;
                case "channel catfish":
                    start=start+5;
                    break;
            }
        }

        return start;
    }
}
