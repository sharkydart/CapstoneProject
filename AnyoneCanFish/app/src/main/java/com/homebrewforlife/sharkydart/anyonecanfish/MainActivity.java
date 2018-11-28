package com.homebrewforlife.sharkydart.anyonecanfish;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.homebrewforlife.sharkydart.anyonecanfish.fireX.FirestoreStuff;
import com.homebrewforlife.sharkydart.anyonecanfish.models.Fire_GameFish;
import com.homebrewforlife.sharkydart.anyonecanfish.models.Fire_TackleBox;
import com.homebrewforlife.sharkydart.anyonecanfish.models.Fire_Trip;
import com.homebrewforlife.sharkydart.anyonecanfish.services.LocationService;
import com.homebrewforlife.sharkydart.anyonecanfish.services.LocationTasks;
import com.homebrewforlife.sharkydart.anyonecanfish.services.WeatherInfoService;
import com.homebrewforlife.sharkydart.anyonecanfish.services.WeatherInfoTasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{

    private int RC_SIGN_IN = 0;   //request code
    private final int RC_SWEET_PERMISSIONS = 1337;
    private Context mContext;
    public static final String GPS_SHAREDPREFS_CACHE = "gps-sharedpreferences-cache";
    public static final String SHAREDPREFS_LAT = "sharedpreferences-lat";
    public static final String SHAREDPREFS_LON = "sharedpreferences-lon";
    SharedPreferences mSharedPreferences;

    //Receivers and Intent Filters
    MainLocationReceiver mLocReceiver;
    IntentFilter mLocFilter;
    MainWeatherFirstReceiver mWeatherFirstReceiver;
    IntentFilter mWeatherFirstReceiverFilter;

    //Firebase Authentication
    FirebaseAuth mAuth;
    FirebaseUser mCurUser;

    //Firestore Database Reference
    FirebaseFirestore mFS_Store;
    DocumentReference mFS_User_document_ref;
    CollectionReference mFS_GameFish_collection_ref;

    ArrayList<Fire_GameFish> mGameFishArrayList;
    public static final String GAME_FISH_ARRAYLIST = "game-fish-array-list";
    ArrayList<Fire_Trip> mFishingTrips;
    public static final String FISHING_TRIPS_ARRAYLIST = "game-fish-array-list";
    ArrayList<Fire_TackleBox> mTackleBoxes;
    public static final String TACKLE_BOXES_ARRAYLIST = "game-fish-array-list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.theMainToolbar);
        setSupportActionBar(toolbar);
        mContext = this;

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);
        mAuth = FirebaseAuth.getInstance();

        //verify location permissions and start getting location and weather data
        verifyLocationPermissions();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Make a new trip!", Snackbar.LENGTH_LONG)
                        .setAction("Action", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(mContext, "Making a Trip...", Toast.LENGTH_SHORT).show();
                                Log.i("fart", "clicked Snackbar");
                            }
                        }).show();
                Log.i("fart", "clicked FAB");
            }
        });

        //handle the clicks
        findViewById(R.id.mcFishingBasics).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), FishingBasicsActivity.class);
                //intent.putExtra(/* name of data */, /* data */);
                view.getContext().startActivity(intent);
            }
        });
        findViewById(R.id.mcGameFish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), GameFishActivity.class);
                intent.putParcelableArrayListExtra(GAME_FISH_ARRAYLIST, mGameFishArrayList);
                view.getContext().startActivity(intent);
            }
        });
        findViewById(R.id.mcFishingTrips).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), FishingTripsActivity.class);
                intent.putParcelableArrayListExtra(FISHING_TRIPS_ARRAYLIST, mFishingTrips);
                view.getContext().startActivity(intent);
            }
        });
        findViewById(R.id.mcTackleBoxes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), TackleBoxesActivity.class);
                intent.putParcelableArrayListExtra(TACKLE_BOXES_ARRAYLIST, mTackleBoxes);
                view.getContext().startActivity(intent);
            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        mCurUser = mAuth.getCurrentUser();
        if(mCurUser == null){
            //immediately try to sign the user in via FirebaseUI
            FirebaseSignIn();
        }
        else{
            Firestore_LoadData();
        }
    }

    private void verifyLocationPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    RC_SWEET_PERMISSIONS);
        }
        else{
            //try and get GPS coords, and send them back to get the weather
            GeoPoint coords = getCoordsFromSharedPrefs();
            if(coords == null)
                Log.d("fart", "no coords in shared prefs");
            else
                Log.d("fart", "lat: " + coords.getLatitude() + ", lon:" + coords.getLongitude());

            startGettingLatLon();
        }
    }
    private void saveCoordsToSharedPrefs(double lat, double lon){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(SHAREDPREFS_LAT, Double.toString(lat));
        editor.putString(SHAREDPREFS_LON, Double.toString(lon));
        editor.apply();
    }
    private GeoPoint getCoordsFromSharedPrefs(){
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lat = mSharedPreferences.getString(SHAREDPREFS_LAT, null);
        String lon = mSharedPreferences.getString(SHAREDPREFS_LAT, null);
        GeoPoint coords;
        if(lat != null && lon != null)
            coords = new GeoPoint(Double.valueOf(lat), Double.valueOf(lon));
        else
            coords = null;
        return coords;
    }

    private void startGettingLatLon(){
        mLocFilter = new IntentFilter();
        mLocFilter.addAction(LocationTasks.ACTION_FOUND_GPS_LOCATION);
        mLocReceiver = new MainLocationReceiver();
        mContext.registerReceiver(mLocReceiver, mLocFilter);

        Intent getLatLonIntent = new Intent(this, LocationService.class);
        getLatLonIntent.setAction(LocationTasks.ACTION_GET_GPS_LOCATION);
        startService(getLatLonIntent);
    }
    private void startGettingFirstWeather(double lat, double lon){
        mWeatherFirstReceiverFilter = new IntentFilter();
        mWeatherFirstReceiverFilter.addAction(WeatherInfoTasks.ACTION_FOUND_WEATHER_FORECAST);
        mWeatherFirstReceiver = new MainWeatherFirstReceiver();
        mContext.registerReceiver(mWeatherFirstReceiver, mWeatherFirstReceiverFilter);
        Log.i("fart", "-weather receiver registered with to filter for: " + WeatherInfoTasks.ACTION_FOUND_WEATHER_FORECAST + "<=");

        Intent getWeatherFirstIntent = new Intent(this, WeatherInfoService.class);
        getWeatherFirstIntent.putExtra(LocationTasks.EXTRA_LATITUDE, lat);
        getWeatherFirstIntent.putExtra(LocationTasks.EXTRA_LONGITUDE, lon);
        getWeatherFirstIntent.setAction(WeatherInfoTasks.ACTION_GET_WEATHER_FORECAST);
        startService(getWeatherFirstIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case RC_SWEET_PERMISSIONS:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startGettingLatLon();
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //Firebase Functions
    private void FirebaseSignOut(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(mContext,"You have been signed out.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void FirebaseSignIn(){
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    private void FirebaseGetUserInfo(){
        //FirebaseUser user = mAuth.getCurrentUser();
        try {
            if(mCurUser != null) {
                Log.d("fart", "Name: " + mCurUser.getDisplayName()
                        + " Email: " + mCurUser.getEmail()
                        + " UID: " + mCurUser.getUid());
                if(mCurUser.getDisplayName() == null)
                    FirebaseUpdateUserInfo(mCurUser);   //just to set a name, if there is none
                else {
                    //Firestore_PrepUserInfo
                    //sets the reference to the users' specific document
                    mFS_User_document_ref = mFS_Store.collection(getString(R.string.db_users)).document(mCurUser.getUid());
                }
            }
        }
        catch(NullPointerException np){
            np.printStackTrace();
        }
    }
    private void FirebaseUpdateUserInfo(final FirebaseUser theUser){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName("Curious Angler")
//                .setPhotoUri(Uri.parse(""))
                .build();

        theUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("fart", "User profile updated.");
                            Log.d("fart", "Name: " + theUser.getDisplayName()
                                    + " Email: " + theUser.getEmail()
                                    + " UID: " + theUser.getUid());
                        }
                    }
                });
    }
    private void Firestore_Get_GameFish(){
        mFS_GameFish_collection_ref = mFS_Store.collection(getString(R.string.db_game_fish));
        mFS_GameFish_collection_ref.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Log.d("fart", document.getId() + " => " + document.getData());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("fart", "collection grabbing error");
                    }
                });
    }
    private void Firestore_LoadData(){
        //firestore references
        mFS_Store = FirebaseFirestore.getInstance();
        //get specifically user firestore db data
        FirebaseGetUserInfo();
        //get specifically game_fish firestore db data
//        Firestore_Get_GameFish();
        mGameFishArrayList = new ArrayList<Fire_GameFish>();
        FirestoreStuff myFirestore = new FirestoreStuff(mContext,mCurUser,mFS_Store);
        myFirestore.Firestore_Get_GameFish(mGameFishArrayList);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                Firestore_LoadData();
            }
            else {
                if(response == null || resultCode == Activity.RESULT_CANCELED) {
                    Log.d("fart", "User cancelled sign in");
                    return;
                }
                if(response.getError() == null)
                    return;
                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Snackbar.make(findViewById(android.R.id.content), "No Network!", Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Log.d("fart", "Sign-in failed");
                finish();
            }
        }
    }

//    TODO - 2) get weather data: forecast temperature      (api.weather.gov)
//    TODO -    2a: if a trip is selected (settings), use that info to query, otherwise, get current GPS coords
//    TODO -    2b: 1st query - get 'office' and 'grid position'.  2nd query - get forecast data

//    TODO - 3) get weather data: one day sun and moon data (api.usno.navy.mil/rstt/oneday)


    //menu stuff
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_open_settings) {
            return true;
        }
        else if(id == R.id.action_firebase_signout){
            //disable the menu button
            item.setEnabled(false);
            //sign out of Firebase
            FirebaseSignOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //receiver that changes location displayed when triggered
    private class MainLocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("fart", "[[Location]] Received Something...");
            String action = intent.getAction();
            if(LocationTasks.ACTION_FOUND_GPS_LOCATION.equals(action)){
                double theLat, theLon;
                theLat = intent.getDoubleExtra(LocationTasks.EXTRA_LATITUDE, -99);
                theLon = intent.getDoubleExtra(LocationTasks.EXTRA_LONGITUDE, -99);
                saveCoordsToSharedPrefs(theLat, theLon);
                Log.d("fart","calling 'startGettingFirstWeather' with Coordinates: " + theLat + ", " + theLon);
                ((TextView)findViewById(R.id.tvTempGPSDisplay)).setText(String.format(Locale.US,"%f, %f",theLat,theLon));
                startGettingFirstWeather(theLat, theLon);
            }
            else{
                Log.d("fart", "broadcast: " + action);
            }
        }
    }
    //receiver that gets the result from sending location data to the first weather api
    private class MainWeatherFirstReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("fart", "[[Weather First]] Received Something...");
            String action = intent.getAction();
            if(WeatherInfoTasks.ACTION_FOUND_WEATHER_FORECAST.equals(action)){
                String theWeatherAPIURL;
                theWeatherAPIURL = intent.getStringExtra(WeatherInfoTasks.EXTRA_FORECAST_API_URL);
                Log.d("fart","theWeatherAPIURL: " + theWeatherAPIURL);
            }
            else{
                Log.d("fart", "broadcast: " + action);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        registerReceiver(mLocReceiver, mLocFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(mLocReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("fart", "onDestroy called");
        unregisterReceiver(mLocReceiver);
        unregisterReceiver(mWeatherFirstReceiver);
    }

    /*
    private static final int RC_PHOTO_PICKER =  2;

    // ImagePickerButton shows an image picker to upload a image for a message
    mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
        }
    });
*/
}
