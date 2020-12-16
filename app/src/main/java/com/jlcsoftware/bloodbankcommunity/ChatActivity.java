package com.jlcsoftware.bloodbankcommunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private TextView username_tv,online_tv;

    private CircleImageView user_img;

    private String userId,img_uri,username;



    private DatabaseReference reference;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                onBackPressed();
                return false;
            }
        });

        userId = getIntent().getStringExtra("userId");
        username =getIntent().getStringExtra("username");
        img_uri = getIntent().getStringExtra("img_uri");

        username_tv = findViewById(R.id.chat_username);
        user_img = findViewById(R.id.chat_img);
        online_tv  = findViewById(R.id.chat_online);
        username_tv.setText(username);


        reference = FirebaseDatabase.getInstance().getReference("users");

        reference.child("user_details").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue(true);



        if(!img_uri.equals("")) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.teamwork_symbol);
            Glide.with(ChatActivity.this)
                    .setDefaultRequestOptions(requestOptions)
                    .load(img_uri).into(user_img);
        }


        reference.child("user_details").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                img_uri = snapshot.child("img_uri").getValue(String.class);
                if(snapshot.child("online")!=null){

                    if(snapshot.child("online").getValue(Boolean.class)){
                       online_tv.setText("online");
                    }else{

                        GetTimeAgo getTimeAgo = new GetTimeAgo();

                        String getTime = getTimeAgo.getTimeAgo(snapshot.child("lastSeen").getValue(Long.class),getApplicationContext());

                        online_tv.setText(getTime);

                    }
                }

                if(!img_uri.equals("")) {
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



        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chat");

        databaseReference.child("usersChat").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChild(userId)){
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp",ServerValue.TIMESTAMP);


                    Map chatUserMap = new HashMap();




                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


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