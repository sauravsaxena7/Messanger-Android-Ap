package com.jlcsoftware.bloodbankcommunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jlcsoftware.bloodbankcommunity.MainFragment.ProfileFragment;
import com.jlcsoftware.bloodbankcommunity.MainFragment.SearchFragment;
import com.jlcsoftware.bloodbankcommunity.MainFragment.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.setItemIconTintList(null);

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

}