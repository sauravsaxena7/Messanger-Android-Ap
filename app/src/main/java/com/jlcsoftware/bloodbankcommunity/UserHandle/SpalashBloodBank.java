package com.jlcsoftware.bloodbankcommunity.UserHandle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jlcsoftware.bloodbankcommunity.MainActivity;
import com.jlcsoftware.bloodbankcommunity.R;
import com.jlcsoftware.bloodbankcommunity.UserDetails.User_Details;

public class SpalashBloodBank extends AppCompatActivity {

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalash_blood_bank);



        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }





        Thread thread = new Thread(){
            public void run(){
                try {
                    sleep(2000);
                    if(FirebaseAuth.getInstance().getCurrentUser()!=null){

                        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
                        ref.child("user_details").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            if(snapshot.child("phone_number").getValue(String.class).equals("")){

                                                startActivity(new Intent(SpalashBloodBank.this, VerifyPhone.class));
                                                finish();

                                            }else if(snapshot.child("blood_group").getValue(String.class).equals("")){

                                                startActivity(new Intent(SpalashBloodBank.this, User_Details.class));
                                                finish();
                                            }else{

                                                startActivity(new Intent(SpalashBloodBank.this, MainActivity.class));
                                                finish();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                        Toast.makeText(SpalashBloodBank.this, "Error! "+error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }else{
                        startActivity(new Intent(SpalashBloodBank.this, Login.class));
                        finish();
                    }

                }catch (Exception e){

                }
            }
        };

        thread.start();



    }
}