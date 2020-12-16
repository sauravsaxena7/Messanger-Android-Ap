package com.jlcsoftware.bloodbankcommunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.jlcsoftware.bloodbankcommunity.MainFragment.ProfileFragment;
import com.jlcsoftware.bloodbankcommunity.MainFragment.SearchFragment;
import com.jlcsoftware.bloodbankcommunity.MainFragment.SettingsFragment;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    DatabaseReference reference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.setItemIconTintList(null);


        reference = FirebaseDatabase.getInstance().getReference("users");


        reference.child("user_details").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child("online")!=null){
                    reference.child("user_details").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").onDisconnect().setValue(false);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_layout, new ProfileFragment()).commit();





    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment selectedFragment = null;
                    switch (item.getItemId()){

                        case R.id.settings:
                            selectedFragment = new SettingsFragment();
                            break;
                        case R.id.profile:
                            selectedFragment = new ProfileFragment();
                            break;

                        case R.id.search:
                            selectedFragment = new SearchFragment();
                            break;


                    }


                    //begin Transaction
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_layout,selectedFragment).commit();



                    return true;
                }
            };


    @Override
    protected void onStart() {
        super.onStart();
        Map<String ,Object> updateValues = new HashMap<>();
        updateValues.put("online",true);
        reference.child("user_details").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(updateValues);

    }



    @Override
    protected void onStop() {
        super.onStop();

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            Map<String ,Object> updateValues = new HashMap<>();
            updateValues.put("online",false);
            updateValues.put("lastSeen", ServerValue.TIMESTAMP);
            reference.child("user_details").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(updateValues);

        }
    }



    }