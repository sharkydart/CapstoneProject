package com.homebrewforlife.sharkydart.anyonecanfish.fireX;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.homebrewforlife.sharkydart.anyonecanfish.R;
import com.homebrewforlife.sharkydart.anyonecanfish.models.Fire_FishEvent;
import com.homebrewforlife.sharkydart.anyonecanfish.models.Fire_GameFish;
import com.homebrewforlife.sharkydart.anyonecanfish.models.Fire_Lure;
import com.homebrewforlife.sharkydart.anyonecanfish.models.Fire_TackleBox;
import com.homebrewforlife.sharkydart.anyonecanfish.models.Fire_Trip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirestoreStuff {
    @NonNull
    private Context mContext;
    FirebaseUser mCurUser;
    FirebaseFirestore mFS_Store;
    static DocumentReference mFS_User_document_ref;
    static CollectionReference mFS_GameFish_collection_ref;
    ArrayList<Fire_GameFish> mFire_GameFish_arraylist;
    static CollectionReference mFS_TackleBox_collection_ref;
    ArrayList<Fire_TackleBox> mFire_TackleBox_arraylist;
    static CollectionReference mFS_Trips_collection_ref;
    ArrayList<Fire_Trip> mFire_Trips_arraylist;

    public FirestoreStuff(){}
    public FirestoreStuff(@NonNull Context theContext, FirebaseUser theUser, FirebaseFirestore theFS,
                          ArrayList<Fire_GameFish> gfArray, ArrayList<Fire_TackleBox> tbArray, ArrayList<Fire_Trip> tpArray){
        this.mContext = theContext;
        this.mCurUser = theUser;
        this.mFS_Store = theFS;
        this.mFire_GameFish_arraylist = gfArray;
        this.mFire_TackleBox_arraylist = tbArray;
        this.mFire_Trips_arraylist = tpArray;
    }
    public FirestoreStuff(@NonNull Context theContext, FirebaseUser theUser, FirebaseFirestore theFS){
        this.mContext = theContext;
        this.mCurUser = theUser;
        this.mFS_Store = theFS;
        this.mFire_GameFish_arraylist = new ArrayList<>();
        this.mFire_TackleBox_arraylist = new ArrayList<>();
        this.mFire_Trips_arraylist = new ArrayList<>();
    }

    //Firebase Functions
    public void FirebaseSignOut(){
        AuthUI.getInstance()
                .signOut(mContext)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(mContext,"You have been signed out.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void FirebaseSignIn(){
        int RC_SIGN_IN = 0;   //request code
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        ((AppCompatActivity)mContext).startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public void FirebaseGetUserInfo(){
        //FirebaseUser user = mAuth.getCurrentUser();
        try {
            if(mCurUser != null) {
                Log.d("fart", "Name: " + mCurUser.getDisplayName()
                        + " Email: " + mCurUser.getEmail()
                        + " UID: " + mCurUser.getUid());
                if(mCurUser.getDisplayName() == null)
                    FirebaseUpdateUserInfo(mCurUser);   //just to set a name, if there is none
                //sets the reference to the users' specific document
                mFS_User_document_ref = mFS_Store.collection(mContext.getString(R.string.db_users)).document(mCurUser.getUid());
            }
        }
        catch(NullPointerException np){
            np.printStackTrace();
        }
    }
    public void FirebaseUpdateUserInfo(final FirebaseUser theUser){
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
    public void Firestore_Get_GameFish(final ArrayList<Fire_GameFish> theGameFish){
        mFS_GameFish_collection_ref = mFS_Store.collection(mContext.getString(R.string.db_game_fish));
        mFS_GameFish_collection_ref.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(theGameFish != null)
                            theGameFish.clear();
                        for (QueryDocumentSnapshot gamefish : queryDocumentSnapshots) {
                            Log.d("fart", gamefish.getId()
                                    + " => " + gamefish.getString("species")
                                    + " => " + gamefish.getString("wiki")
                                    + " => " + gamefish.getString("image_url")
                                    + " => " + gamefish.getString("information")
                            );
                            Fire_GameFish bork = gamefish.toObject(Fire_GameFish.class);
                            Log.i("fart", bork.getQuickDescription());
                            if(theGameFish != null) {
                                theGameFish.add(bork);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("fart", "collection grabbing error");
            }
        });
    }
    public void Firestore_Get_FishingTrips(final ArrayList<Fire_Trip> theFishingTrips){
        mFS_Trips_collection_ref = mFS_Store.collection(mContext.getString(R.string.db_trips));
        mFS_Trips_collection_ref.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(theFishingTrips != null)
                            theFishingTrips.clear();
                        for (QueryDocumentSnapshot trip : queryDocumentSnapshots){
                            Log.d("fart", trip.getId()
                                    + " => " + trip.getString("desc")
                                    + " => " + trip.getString("name")
                                    + " => " + trip.getGeoPoint("geo_loc")
                                    + " => " + trip.getTimestamp("dateStart")
                                    + " => " + trip.getTimestamp("dateEnd")
                            );
                            Fire_Trip bork = trip.toObject(Fire_Trip.class);
                            Log.i("fart", bork.getQuickDescription());
                            if(theFishingTrips != null) {
                                theFishingTrips.add(bork);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("fart", "trip collection grabbing error");
            }
        });
    }
    public void Firestore_Get_Trip_FishEvents(String uid_trip, final ArrayList<Fire_FishEvent> theFishEvents){
        CollectionReference FS_Trip_FishEvents = mFS_Store
                .collection(mContext.getString(R.string.db_trips))
                .document(uid_trip)
                .collection(mContext.getString(R.string.db_fish_events));
        FS_Trip_FishEvents.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(theFishEvents != null)
                            theFishEvents.clear();
                        for(QueryDocumentSnapshot fishEvent : queryDocumentSnapshots){
                            Log.d("fart", fishEvent.getId()
                                    + " => " + fishEvent.getTimestamp("date")
                                    + " => " + fishEvent.getString("species")
                                    + " => " + fishEvent.getString("desc")
                                    + " => " + fishEvent.getBoolean("released")
                                    + " => " + fishEvent.getGeoPoint("geo_loc")
                                    + " => " + fishEvent.getString("image_url")
                            );
                            Fire_FishEvent bork = fishEvent.toObject(Fire_FishEvent.class);
                            Log.i("fart", bork.getQuickDescription());
                            if(theFishEvents != null) {
                                theFishEvents.add(bork);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("fart", "fishevents grabbing error");
            }
        });
    }
    public void Firestore_Get_TackleBoxes(final ArrayList<Fire_TackleBox> theTackleBoxes){
        mFS_TackleBox_collection_ref = mFS_Store.collection(mContext.getString(R.string.db_tackle_boxes));
        mFS_TackleBox_collection_ref.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(theTackleBoxes != null)
                            theTackleBoxes.clear();
                        for (QueryDocumentSnapshot tacklebox : queryDocumentSnapshots){
                            Log.d("fart", tacklebox.getId()
                                    + " => " + tacklebox.getString("desc")
                                    + " => " + tacklebox.getString("name")
                            );
                            Fire_TackleBox bork = tacklebox.toObject(Fire_TackleBox.class);
                            Log.i("fart", bork.getQuickDescription());
                            if(theTackleBoxes != null) {
                                theTackleBoxes.add(bork);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("fart", "tacklebox collection grabbing error");
            }
        });
    }
    public void Firestore_Get_TackleBox_Lures(String uid_tacklebox, final ArrayList<Fire_Lure> theLures){
        CollectionReference FS_tacklebox_lures = mFS_Store
                .collection(mContext.getString(R.string.db_tackle_boxes))
                .document(uid_tacklebox)
                .collection(mContext.getString(R.string.db_lures));
        FS_tacklebox_lures.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(theLures != null)
                            theLures.clear();
                        for(QueryDocumentSnapshot lure : queryDocumentSnapshots){
                            Log.d("fart", lure.getId()
                                    + " => " + lure.getString("name")
                                    + " => " + lure.getString("size")
                                    + " => " + lure.getString("type")
                                    + " => " + lure.getString("desc")
                                    + " => " + lure.getString("hook_type")
                                    + " => " + lure.getDouble("hook_count")
                                    + " => " + lure.getString("image_url")
                            );
                            Fire_Lure bork = lure.toObject(Fire_Lure.class);
                            Log.i("fart", bork.getQuickDescription());
                            if(theLures != null) {
                                theLures.add(bork);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("fart", "lures collection grabbing error");
            }
        });
    }
    public void FirestoreLoadData(){
        //firestore references
        mFS_Store = FirebaseFirestore.getInstance();
        //get specifically user firestore db data
        FirebaseGetUserInfo();
        //get specifically game_fish firestore db data
        Firestore_Get_GameFish(mFire_GameFish_arraylist);
        Firestore_Get_TackleBoxes(mFire_TackleBox_arraylist);
        Firestore_Get_FishingTrips(mFire_Trips_arraylist);
    }

}
