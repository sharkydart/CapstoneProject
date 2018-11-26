package com.homebrewforlife.sharkydart.anyonecanfish.services;

import android.app.IntentService;
import android.content.Intent;

public class LocationService extends IntentService {
    public LocationService(){
        super("LocationService");
    }
    @Override
    protected void onHandleIntent(Intent theIntent) {
        if(theIntent != null) {
            String theIntentAction = theIntent.getAction();
            LocationTasks.getLastLocationTask(this, theIntentAction);
        }
    }
}
