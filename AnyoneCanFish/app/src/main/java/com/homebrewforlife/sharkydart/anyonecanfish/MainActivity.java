package com.homebrewforlife.sharkydart.anyonecanfish;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.homebrewforlife.sharkydart.anyonecanfish.services.LocationService;
import com.homebrewforlife.sharkydart.anyonecanfish.services.LocationTasks;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{

    private int RC_SIGN_IN = 0;   //request code
    private Context mContext;
    MainLocationReceiver mLocReceiver;
    IntentFilter mLocFilter;
    FirebaseFirestore mFB_Store;
    DocumentReference mFB_UserRef;
    CollectionReference mFB_GameFishCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.theMainToolbar);
        setSupportActionBar(toolbar);
        mContext = this;

        //firestore references
        mFB_Store = FirebaseFirestore.getInstance();
        mFB_GameFishCollection = mFB_Store.collection(getString(R.string.fb_game_fish));


        //immediately try to sign the user in via FirebaseUI
        FirebaseSignIn();
        //getLocation
        StartGettingLatLon();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void StartGettingLatLon(){
        mLocFilter = new IntentFilter();
        mLocFilter.addAction(LocationTasks.ACTION_FOUND_GPS_LOCATION);
        mLocReceiver = new MainLocationReceiver();

        Intent getLatLonIntent = new Intent(this, LocationService.class);
        getLatLonIntent.setAction(LocationTasks.ACTION_GET_GPS_LOCATION);
    }

    /*
        Firebase functions
    */
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
//    TODO - 1) get basic user info from firebase, using the logged in user

//    TODO - 2) get weather data: forecast temperature      (api.weather.gov)
//    TODO -    2a: if a trip is selected (settings), use that info to query, otherwise, get current GPS coords
//    TODO -    2b: 1st query - get 'office' and 'grid position'.  2nd query - get forecast data

//    TODO - 3) get weather data: one day sun and moon data (api.usno.navy.mil/rstt/oneday)

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // ...
                try {
                    if(user != null) {
                        Log.d("fart", "Name: " + user.getDisplayName()
                                + " Email: " + user.getEmail()
                                + " UID: " + user.getUid()
                                + "IDTOKEN: " + user.getIdToken(false));
                        //sets the reference to the users' specific document
                        mFB_UserRef = mFB_Store.collection(getString(R.string.db_users))
                                .document(user.getIdToken(false).toString());
                    }
                }
                catch(NullPointerException np){
                    np.printStackTrace();
                }
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Log.d("fart", "Sign-in failed");
                finish();
            }
        }
    }

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
            String action = intent.getAction();
            if(LocationTasks.ACTION_FOUND_GPS_LOCATION.equals(action)){
                double theLat, theLon;
                theLat = intent.getDoubleExtra(LocationTasks.EXTRA_LATITUDE, LocationTasks.DEFAULT_LAT);
                theLon = intent.getDoubleExtra(LocationTasks.EXTRA_LONGITUDE, LocationTasks.DEFAULT_LON);
                ((TextView)findViewById(R.id.tvTempGPSDisplay)).setText(String.format(Locale.US,"%f, %f",theLat,theLon));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mLocReceiver, mLocFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mLocReceiver);
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
