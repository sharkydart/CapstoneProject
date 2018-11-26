package com.homebrewforlife.sharkydart.anyonecanfish.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.homebrewforlife.sharkydart.anyonecanfish.MyLocationListener;

public class LocationService extends IntentService {
    public static MyLocationListener listener_obj;
    public LocationService(){
        super("LocationService");
    }
    public LocationService(MyLocationListener theListener){
        super("LocationService");
        listener_obj = theListener;
    }
    @Override
    protected void onHandleIntent(@Nullable Intent theIntent) {
        if(theIntent != null) {
            String theIntentAction = theIntent.getAction();
            LocationTasks.getLastLocationTask(this, theIntentAction);
        }
    }
}
