package com.jlcsoftware.bloodbankcommunity.ChatApplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.jlcsoftware.bloodbankcommunity.GetTimeAgo;
import com.jlcsoftware.bloodbankcommunity.NotCurrentUser.UserProfile;
import com.jlcsoftware.bloodbankcommunity.R;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {


    private Toolbar toolbar;
    DatabaseReference reference;
    private String userId;

    private CircleImageView chat_user_img;
    private TextView chat_username_tv,last_seen;

    private FirebaseAuth firebaseAuth;

    DatabaseReference ref;

    private ImageView chat_attachment,chat_send_btn;
    private EditText chat_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        toolbar = findViewById(R.id.chat_view_toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        last_seen = findViewById(R.id.chat_online);

        chat_attachment = findViewById(R.id.chat_attachment);
        chat_et = findViewById(R.id.chat_messageET);
        chat_send_btn = findViewById(R.id.chat_sendBtn);


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.refresh){
                    Toast.makeText(ChatActivity.this, "hiii", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });





        userId = getIntent().getStringExtra("userId");

        chat_user_img = findViewById(R.id.chat_view_img);
        chat_username_tv = findViewById(R.id.chat_view_username);

        reference = FirebaseDatabase.getInstance().getReference("users");

        reference.child("user_details").child(userId)
                .addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String img_uri = snapshot.child("img_uri").getValue(String.class);

                chat_username_tv.setText(snapshot.child("username").getValue(String.class));


                if(!img_uri.equals("")){
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.teamwork_symbol);
                    Glide.with(getApplicationContext())
                            .setDefaultRequestOptions(requestOptions)
                            .load(img_uri).into(chat_user_img);
                }

                if(snapshot.child("online").getValue(Boolean.class)){
                    last_seen.setText("online");
                }else{



                    String last_seen_str = GetTimeAgo.getTimeAgo(snapshot.child("lastSeen").getValue(Long.class),ChatActivity.this);


                    last_seen.setText(last_seen_str);

                }



            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        firebaseAuth = FirebaseAuth.getInstance();

        ref = FirebaseDatabase.getInstance().getReference();

        ref.child("Chat").child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(!snapshot.hasChild(userId)){
                    Map chatAddMap = new HashMap();

                    chatAddMap.put("seen",false);
                    chatAddMap.put("timeStamp",ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/"+firebaseAuth.getCurrentUser().getUid()+"/"+userId,chatAddMap);
                    chatUserMap.put("Chat/"+userId+"/"+firebaseAuth.getCurrentUser().getUid(),chatAddMap);


                    ref.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                            if(error!=null){

                                Log.d("chat",error.getMessage());

                            }

                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        chat_send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String messages = chat_et.getText().toString().trim();

                Toast.makeText(ChatActivity.this, ""+messages, Toast.LENGTH_SHORT).show();
                if(!TextUtils.isEmpty(messages)){
                    sendMessages(messages);
                }

            }
        });






    }

    private void sendMessages(String messages) {

        Toast.makeText(this, "lllll", Toast.LENGTH_SHORT).show();


        String current_user_ref = "messages/"+firebaseAuth.getCurrentUser().getUid()+"/"+userId;
        String chat_user_ref = "messages/"+userId+"/"+firebaseAuth.getCurrentUser().getUid();


        DatabaseReference user_message_push =ref.child("messages").child(firebaseAuth.getCurrentUser().getUid())
                .child(userId).push();


        String push_id = user_message_push.getKey();


        Map messageMap = new HashMap();

        messageMap.put("message",messages);
        messageMap.put("type","text");
        messageMap.put("seen",false);
        messageMap.put("time",ServerValue.TIMESTAMP);



        Map messageUserMap = new HashMap();

        messageUserMap.put(current_user_ref+"/"+push_id,messageMap);

        messageMap.put(chat_user_ref+"/"+push_id,messageMap);



        ref.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                if(error!=null){
                    Log.d("chat",error.getMessage());
                }

            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.chat_menu,menu);


        return true;
    }

}