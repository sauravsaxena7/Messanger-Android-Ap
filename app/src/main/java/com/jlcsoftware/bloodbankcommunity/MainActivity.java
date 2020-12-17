package com.jlcsoftware.bloodbankcommunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.setItemIconTintList(null);


        firebaseAuth = FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance()
                .getReference("users").child("user_details")
                .child(firebaseAuth.getCurrentUser().getUid());



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

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser!=null){
            reference.child("online").setValue(true);
        }

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        reference.child("online").setValue(false);
        reference.child("lastSeen").setValue(ServerValue.TIMESTAMP);


    }




}