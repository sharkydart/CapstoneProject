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
import com.homebrewforlife.sharkydart.anyonecanfish.models.Fire_GameFish;

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

    public FirestoreStuff(){}
    public FirestoreStuff(@NonNull Context theContext, FirebaseUser theUser, FirebaseFirestore theFS, ArrayList<Fire_GameFish> gfArray){
        this.mContext = theContext;
        this.mCurUser = theUser;
        this.mFS_Store = theFS;
        this.mFire_GameFish_arraylist = gfArray;
    }
    public FirestoreStuff(@NonNull Context theContext, FirebaseUser theUser, FirebaseFirestore theFS){
        this.mContext = theContext;
        this.mCurUser = theUser;
        this.mFS_Store = theFS;
        this.mFire_GameFish_arraylist = new ArrayList<Fire_GameFish>();
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

    public void Firestore_Get_UserInfo(){
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
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Log.d("fart", document.getId()
                                    + " => " + document.getString("species")
                                    + " => " + document.getString("wiki")
                                    + " => " + document.getString("image_url")
                                    + " => " + document.getString("information")
                            );
                            Fire_GameFish bork = document.toObject(Fire_GameFish.class);
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
    public void FirestoreLoadData(){
        //firestore references
        mFS_Store = FirebaseFirestore.getInstance();
        //get specifically user firestore db data
        Firestore_Get_UserInfo();
        //get specifically game_fish firestore db data
        Firestore_Get_GameFish(mFire_GameFish_arraylist);
    }

}
