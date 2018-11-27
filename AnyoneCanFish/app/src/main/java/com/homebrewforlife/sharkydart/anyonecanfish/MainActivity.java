package com.homebrewforlife.sharkydart.anyonecanfish;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.homebrewforlife.sharkydart.anyonecanfish.services.LocationService;
import com.homebrewforlife.sharkydart.anyonecanfish.services.LocationTasks;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{

    private int RC_SIGN_IN = 0;   //request code
    private final int RC_SWEET_PERMISSIONS = 1337;
    private Context mContext;
    MainLocationReceiver mLocReceiver;
    IntentFilter mLocFilter;
    FirebaseAuth mAuth;
    FirebaseFirestore mFB_Store;
    DocumentReference mFB_UserRef;
    CollectionReference mFB_GameFishCollection;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser curUser = mAuth.getCurrentUser();
        if(curUser == null){
            //immediately try to sign the user in via FirebaseUI
            FirebaseSignIn();
        }
    }

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
                        .setAction("Action", null).show();
            }
        });
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
            startGettingLatLon();
        }
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

    private void FirebaseGetUserInfo(){
        FirebaseUser user = mAuth.getCurrentUser();
        try {
            if(user != null) {
                Log.d("fart", "Name: " + user.getDisplayName()
                        + " Email: " + user.getEmail()
                        + " UID: " + user.getUid());
                if(user.getDisplayName() == null)
                    FirebaseUpdateUserInfo(user);   //just to set a name, if there is none
                //sets the reference to the users' specific document
                mFB_UserRef = mFB_Store.collection(getString(R.string.db_users)).document(user.getUid());
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
    private void FirebaseGet_GameFish(){
        mFB_GameFishCollection.get()
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
    private void doFirebaseLoading(){
        FirebaseGetUserInfo();
        FirebaseGet_GameFish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                //firestore references
                mFB_Store = FirebaseFirestore.getInstance();
                mFB_GameFishCollection = mFB_Store.collection(getString(R.string.db_game_fish));
                // Successfully signed in
                doFirebaseLoading();
            }
            else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Log.d("fart", "Sign-in failed");
                finish();
            }
        }
    }

//    TODO - 1) get basic user info from firebase, using the logged in user

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
            Log.d("fart", "Received Something...");
            String action = intent.getAction();
            if(LocationTasks.ACTION_FOUND_GPS_LOCATION.equals(action)){
                double theLat, theLon;
                theLat = intent.getDoubleExtra(LocationTasks.EXTRA_LATITUDE, LocationTasks.DEFAULT_LAT);
                theLon = intent.getDoubleExtra(LocationTasks.EXTRA_LONGITUDE, LocationTasks.DEFAULT_LON);
                Log.d("fart","Coordinates: " + theLat + ", " + theLon);
                ((TextView)findViewById(R.id.tvTempGPSDisplay)).setText(String.format(Locale.US,"%f, %f",theLat,theLon));
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
