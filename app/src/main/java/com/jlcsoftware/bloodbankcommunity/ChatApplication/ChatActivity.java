package com.jlcsoftware.bloodbankcommunity.ChatApplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.jlcsoftware.bloodbankcommunity.Adapter.MessagesAdapter;
import com.jlcsoftware.bloodbankcommunity.GetTimeAgo;
import com.jlcsoftware.bloodbankcommunity.Models.MessagesModels;
import com.jlcsoftware.bloodbankcommunity.NotCurrentUser.UserProfile;
import com.jlcsoftware.bloodbankcommunity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    String img_uri_str;

    private Toolbar toolbar;
    DatabaseReference reference;
    private String userId;

    private CircleImageView chat_user_img;
    private TextView chat_username_tv,last_seen;

    private FirebaseAuth firebaseAuth;

    DatabaseReference ref;

    private ImageView chat_attachment,chat_send_btn;
    private EditText chat_et;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private final List<MessagesModels> messageList = new ArrayList<>();

    private LinearLayoutManager linearLayoutManager;

    private MessagesAdapter messagesAdapter;

    private static int TOTAL_ITEM_TO_LOAD=10;

    private int mCurrentPage = 1;

    DatabaseReference ref2;

    //solution for pagination

    private int itemPos = 0;
    private String last_key ="";
    private String prev_key = "";


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


        firebaseAuth = FirebaseAuth.getInstance();

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.refresh){
                    Toast.makeText(ChatActivity.this, "hiii", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });



        ref2= FirebaseDatabase.getInstance()
                .getReference("users").child("user_details")
                .child(firebaseAuth.getCurrentUser().getUid());



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





        recyclerView = findViewById(R.id.chat_recycler_view);

        swipeRefreshLayout = findViewById(R.id.message_swipe_up);

        linearLayoutManager = new LinearLayoutManager(this);

        messagesAdapter = new MessagesAdapter(messageList,ChatActivity.this);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(messagesAdapter);



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

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                itemPos=0;
                loadMoreMessages();
            }
        });


        chat_attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent,1024);


            }
        });

        loadMessages();



    }


    private void loadMoreMessages() {
        DatabaseReference messageRef = ref.child("messages")
                .child(firebaseAuth.getCurrentUser().getUid())
                .child(userId);

        Query messageQuery = messageRef.orderByKey().endAt(last_key).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                MessagesModels messagesModels = snapshot.getValue(MessagesModels.class);

                String messageKey = snapshot.getKey();



                if(!prev_key.equals(messageKey)){

                    messageList.add(itemPos++,messagesModels);
                }else{

                    prev_key =last_key;
                }


                if(itemPos==1){
                    last_key = messageKey;
                }



                messagesAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);

                linearLayoutManager.scrollToPositionWithOffset(10,0);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void loadMessages() {

        DatabaseReference messageRef = ref.child("messages").child(firebaseAuth.getCurrentUser()
                .getUid()).child(userId);

        Query messageQuery = messageRef
                .limitToLast(mCurrentPage*TOTAL_ITEM_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


                MessagesModels messagesModels = snapshot.getValue(MessagesModels.class);

                itemPos++;

                if(itemPos==1){
                    String messageKey = snapshot.getKey();
                    last_key = messageKey;
                    prev_key = messageKey;
                }

                messageList.add(messagesModels);
                messagesAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messageList.size()-1);
                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendMessages(String messages) {



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
        messageMap.put("from",firebaseAuth.getCurrentUser().getUid());




        Map messageUserMap = new HashMap();

        messageUserMap.put(current_user_ref+"/"+push_id,messageMap);

        messageUserMap.put(chat_user_ref+"/"+push_id,messageMap);



        ref.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                if(error!=null){
                    Log.d("chat",error.getMessage());
                }

            }
        });

        chat_et.setText("");

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


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser!=null){
            ref2.child("online").setValue(true);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1024 && resultCode == RESULT_OK){
            Uri imgUri = data.getData();

            String current_user_ref = "messages/"+firebaseAuth.getCurrentUser().getUid()+"/"+userId;
            String chat_user_ref = "messages/"+userId+"/"+firebaseAuth.getCurrentUser().getUid();


            DatabaseReference user_message_push =ref.child("messages").child(firebaseAuth.getCurrentUser().getUid())
                    .child(userId).push();


            String push_id = user_message_push.getKey();

            StorageReference filepath = FirebaseStorage.getInstance()
                    .getReference().child("messages_image").child(push_id+".jpg");


            Map messageMap = new HashMap();



            messageMap.put("message",img_uri_str);
            messageMap.put("type","image");
            messageMap.put("seen",false);
            messageMap.put("time",ServerValue.TIMESTAMP);
            messageMap.put("from",firebaseAuth.getCurrentUser().getUid());


            Map messageUserMap = new HashMap();

            messageUserMap.put(current_user_ref+"/"+push_id,messageMap);

            messageUserMap.put(chat_user_ref+"/"+push_id,messageMap);



            ref.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                    if(error!=null){
                        Log.d("chat",error.getMessage());
                    }

                }
            });


            StorageTask uploadTask = filepath.putFile(imgUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if(task.isSuccessful())
                    {
                        Uri downloadUri = task.getResult();
                        img_uri_str=downloadUri.toString();
                        messageMap.put("message",img_uri_str);

                        ref.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                                if(error!=null){
                                    Log.d("chat",error.getMessage());
                                }

                            }
                        });

                    }

                }
            });




        }
    }
}