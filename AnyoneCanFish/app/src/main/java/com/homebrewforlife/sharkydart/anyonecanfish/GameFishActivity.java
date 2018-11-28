package com.homebrewforlife.sharkydart.anyonecanfish;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.homebrewforlife.sharkydart.anyonecanfish.adapters.GameFishRVAdapter;
import com.homebrewforlife.sharkydart.anyonecanfish.fireX.FirestoreStuff;
import com.homebrewforlife.sharkydart.anyonecanfish.models.Fire_GameFish;

import java.util.ArrayList;

public class GameFishActivity extends AppCompatActivity {

    ArrayList<Fire_GameFish> mGameFishArrayList;
    GameFishRVAdapter mGameFishRvAdapter;
    RecyclerView mGameFishRV;

    //Firestore Database Reference
    FirebaseAuth mAuth;
    FirebaseUser mCurUser;
    FirebaseFirestore mFS_Store;

//    @Override
//    protected void onStart() {
//        super.onStart();
//        mCurUser = mAuth.getCurrentUser();
//        if(mCurUser == null){
//            finish();
//            Toast.makeText(this, "You need to be authenticated.", Toast.LENGTH_LONG).show();
//            //immediately try to sign the user in via FirebaseUI
////            FirebaseSignIn();
//        }
//        else{
//            FirestoreLoadData();
//        }
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_fish);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mAuth = FirebaseAuth.getInstance();


        if(savedInstanceState == null){
            mGameFishArrayList = new ArrayList<>();
//            FirestoreLoadData();
        }else if(savedInstanceState.containsKey(MainActivity.GAME_FISH_ARRAYLIST)){
            mGameFishArrayList = savedInstanceState.getParcelableArrayList(MainActivity.GAME_FISH_ARRAYLIST);
        }

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }else {
            mGameFishArrayList = intent.getParcelableArrayListExtra(MainActivity.GAME_FISH_ARRAYLIST);
        }

        mGameFishRV = findViewById(R.id.rvGameFish);
        assert mGameFishRV != null;
        setupRecyclerView(mGameFishRV);
    }
    private void FirestoreLoadData(){
        //firestore references
        mFS_Store = FirebaseFirestore.getInstance();
        //get specifically game_fish firestore db data
//        Firestore_Get_GameFish();
        mGameFishArrayList = new ArrayList<Fire_GameFish>();
        FirestoreStuff myFirestore = new FirestoreStuff(this,mCurUser,mFS_Store);
        myFirestore.Firestore_Get_GameFish(mGameFishArrayList);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mGameFishRvAdapter = new GameFishRVAdapter(this, mGameFishArrayList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mGameFishRvAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MainActivity.GAME_FISH_ARRAYLIST, mGameFishArrayList);
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, "Can't find Game Fish", Toast.LENGTH_SHORT).show();
    }
}
