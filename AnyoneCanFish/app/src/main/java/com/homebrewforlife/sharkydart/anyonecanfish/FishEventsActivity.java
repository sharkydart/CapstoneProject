package com.homebrewforlife.sharkydart.anyonecanfish;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.homebrewforlife.sharkydart.anyonecanfish.adapters.LuresRVAdapter;
import com.homebrewforlife.sharkydart.anyonecanfish.fireX.FirestoreAdds;
import com.homebrewforlife.sharkydart.anyonecanfish.models.Fire_Lure;
import com.homebrewforlife.sharkydart.anyonecanfish.models.Fire_TackleBox;
import com.homebrewforlife.sharkydart.anyonecanfish.models.Fire_User;

import java.util.ArrayList;

public class FishEventsActivity extends AppCompatActivity {

    ArrayList<Fire_Lure> mLuresArrayList;
    LuresRVAdapter mLuresRVAdapter;
    RecyclerView mLuresRV;
    private Context mContext;
    Fire_TackleBox mInTacklebox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lures);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;
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
            mInTacklebox = intent.getParcelableExtra(TackleBoxesActivity.THE_TACKLEBOX);
            mLuresArrayList = intent.getParcelableArrayListExtra(TackleBoxesActivity.LURES_ARRAYLIST);
        }

        mLuresRV = findViewById(R.id.rvLures);
        assert mLuresRV != null;
        setupRecyclerView(mLuresRV);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.lures_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add a lure!", Snackbar.LENGTH_LONG)
                        .setAction("Add lure", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                FirebaseFirestore mFS_Store = FirebaseFirestore.getInstance();
                                FirebaseUser mCurUser = FirebaseAuth.getInstance().getCurrentUser();
                                if(mCurUser != null) {
                                    /*
                                    * uid,
                                      name,
                                      size,
                                      type,
                                      desc,
                                      image_url,
                                      hook_type,
                                      hook_count
                                     */
                                    FirestoreAdds.addFS_lure(mContext, mFS_Store, new Fire_User(mCurUser), mInTacklebox, new Fire_Lure());
                                    Toast.makeText(mContext, "Making a User...", Toast.LENGTH_SHORT).show();
                                }
                                else
                                    Toast.makeText(mContext, "mCurUser is null", Toast.LENGTH_LONG).show();
                            }
                        }).show();
                Log.i("fart", "clicked FAB");
            }
        });
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mLuresRVAdapter = new LuresRVAdapter(this, mLuresArrayList);
        recyclerView.setHasFixedSize(true);
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
