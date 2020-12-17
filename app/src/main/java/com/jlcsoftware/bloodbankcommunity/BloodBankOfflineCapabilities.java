package com.jlcsoftware.bloodbankcommunity;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;

public class BloodBankOfflineCapabilities extends Application {

    private DatabaseReference reference;

    private FirebaseAuth firebaseAuth;
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


         firebaseAuth = FirebaseAuth.getInstance();


         if(firebaseAuth.getCurrentUser()!=null){

             reference= FirebaseDatabase.getInstance()
                     .getReference("users").child("user_details")
                     .child(firebaseAuth.getCurrentUser().getUid());



             reference.addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot snapshot) {

                     if (snapshot!=null){
                         reference.child("online").onDisconnect().setValue(false);
                         reference.child("lastSeen").onDisconnect().setValue(ServerValue.TIMESTAMP);
                     }

                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError error) {

                 }
             });


         }

    }



}
