package com.homebrewforlife.sharkydart.anyonecanfish;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.homebrewforlife.sharkydart.anyonecanfish.adapters.LuresRVAdapter;
import com.homebrewforlife.sharkydart.anyonecanfish.models.Fire_Lure;

import java.util.ArrayList;

public class LuresActivity extends AppCompatActivity {

    ArrayList<Fire_Lure> mLuresArrayList;
    LuresRVAdapter mLuresRVAdapter;
    RecyclerView mLuresRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lures);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if(savedInstanceState == null){
            mLuresArrayList = new ArrayList<>();
        }else if(savedInstanceState.containsKey(TackleBoxesActivity.LURES_ARRAYLIST)){
            mLuresArrayList = savedInstanceState.getParcelableArrayList(TackleBoxesActivity.LURES_ARRAYLIST);
        }

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }else {
            mLuresArrayList = intent.getParcelableArrayListExtra(TackleBoxesActivity.LURES_ARRAYLIST);
        }

        mLuresRV = findViewById(R.id.rvLures);
        assert mLuresRV != null;
        setupRecyclerView(mLuresRV);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mLuresRVAdapter = new LuresRVAdapter(this, mLuresArrayList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mLuresRVAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(TackleBoxesActivity.LURES_ARRAYLIST, mLuresArrayList);
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, "Can't find Game Fish", Toast.LENGTH_SHORT).show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
