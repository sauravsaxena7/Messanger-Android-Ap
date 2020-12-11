package com.jlcsoftware.bloodbankcommunity.ChatApplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jlcsoftware.bloodbankcommunity.R;

import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private String userId="";
    private Toolbar toolbar;

    private DatabaseReference reference;

    private TextView username_tv,online_or_offline_status_tv;
    private CircleImageView user_img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        userId = getIntent().getStringExtra("userId");
        toolbar =  findViewById(R.id.chat_toolbar);

        username_tv=findViewById(R.id.chat_user_profile_username);
        user_img=findViewById(R.id.chat_user_image_id);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        reference = FirebaseDatabase.getInstance().getReference("users");

        reference.child("user_details").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                username_tv.setText(snapshot.child("username").getValue(String.class));
                String img_uri = snapshot.child("img_uri").getValue(String.class);

                if(!img_uri.equals("")){
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.teamwork_symbol);
                    Glide.with(ChatActivity.this)
                            .setDefaultRequestOptions(requestOptions)
                            .load(img_uri).into(user_img);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }
}