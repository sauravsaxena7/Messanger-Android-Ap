   package com.jlcsoftware.bloodbankcommunity.NotCurrentUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jlcsoftware.bloodbankcommunity.Adapter.Links_Adapter;
import com.jlcsoftware.bloodbankcommunity.Interface.RecyclerViewClickListener;
import com.jlcsoftware.bloodbankcommunity.MainActivity;
import com.jlcsoftware.bloodbankcommunity.Models.Links_Model;
import com.jlcsoftware.bloodbankcommunity.R;
import com.jlcsoftware.bloodbankcommunity.UserHandle.Login;
import com.jlcsoftware.bloodbankcommunity.ValidateUserDetails.InternetCheck;

import java.util.ArrayList;
import java.util.Objects;

public class UserLinkedList extends AppCompatActivity implements RecyclerViewClickListener {


    ArrayList<Links_Model> arrayList;
    private RecyclerView recyclerView;

    private ProgressBar search_progressBar;


    String userId,userId2,username,verify_user;

    TextView no_user_found;

    private TextView links_count,links_tv, cancel_request_username_tv,accept_username_tv,unlinked_username_tv,error_content_tv;

    private DatabaseReference linksRequest,link_ref;

    private FirebaseAuth firebaseAuth;



    private CircleImageView cancel_request_user_img , accept_user_img,unlinked_user_img;

    private Dialog cancel_request_dialog,accept_dialog,unlinked_dialog;

    private MaterialButton make_links,cancel_request_btn,never_btn,accept_links_btn ,delete_links,not_for_now_btn,unlinked_btn,unlinked_never_btn,error_btn;


    private Toolbar toolbar;

    DatabaseReference reference;

    LinearLayout search_layout;

    private Dialog error_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_linked_list);



        recyclerView = findViewById(R.id.users_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(UserLinkedList.this));
        recyclerView.setHasFixedSize(true);

        error_dialog=new Dialog(UserLinkedList.this);
        error_dialog.setContentView(R.layout.error_dialog);
        error_content_tv=error_dialog.findViewById(R.id.error_content_tv);
        error_btn=error_dialog.findViewById(R.id.error_button_id);

        error_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error_dialog.dismiss();
            }
        });


        firebaseAuth = FirebaseAuth.getInstance();


        reference= FirebaseDatabase.getInstance()
                .getReference("users").child("user_details")
                .child(firebaseAuth.getCurrentUser().getUid());

        link_ref = FirebaseDatabase.getInstance().getReference("AllLinks");
        linksRequest = FirebaseDatabase.getInstance()
                .getReference("All_Links_Request").child("links_request");

        links_count = findViewById(R.id.link_list_username_tv);
        links_tv = findViewById(R.id.link_list_tv);

        userId2 = getIntent().getStringExtra("userId");


        username = getIntent().getStringExtra("username");
        verify_user = getIntent().getStringExtra("verify_user");

        links_count.setText(username);

        if(verify_user.equals("verify")){
            links_count.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.verify_user, 0);
        }






        link_ref.child(userId2).child("Total_Links").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                links_tv.setText(String.valueOf(snapshot.getChildrenCount())+" "+" "+"Links");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        no_user_found = findViewById(R.id.no_user_found);
        search_progressBar = findViewById(R.id.user_search_progressBar);


        search_layout = findViewById(R.id.search_links_layout);


        cancel_request_dialog=new Dialog(UserLinkedList.this);
        cancel_request_dialog.setContentView(R.layout.cancel_request_layout);

        accept_dialog=new Dialog(UserLinkedList.this);
        accept_dialog.setContentView(R.layout.accept_links_layout);



        unlinked_dialog = new Dialog(UserLinkedList.this);
        unlinked_dialog.setContentView(R.layout.unlinked_layout);




        never_btn = cancel_request_dialog.findViewById(R.id.never_btn);
        never_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel_request_dialog.dismiss();
            }
        });

        cancel_request_btn = cancel_request_dialog.findViewById(R.id.cancel_requested_links);
        cancel_request_user_img = cancel_request_dialog.findViewById(R.id.cancel_request_user_image);
        cancel_request_username_tv = cancel_request_dialog.findViewById(R.id.cancel_request_username_tv);






        cancel_request_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                make_links.setBackgroundColor(getResources().getColor(R.color.browser_actions_title_color,null));
                make_links.setEnabled(false);
                cancel_request_dialog.dismiss();
                cancel_Request();
            }
        });





        accept_links_btn = accept_dialog.findViewById(R.id.accept_links);
        not_for_now_btn = accept_dialog.findViewById(R.id.not_now_btn);
        delete_links = accept_dialog.findViewById(R.id.decline_btn);

        accept_user_img = accept_dialog.findViewById(R.id.accept_user_image);

        accept_username_tv = accept_dialog.findViewById(R.id.accept_username_tv);



        accept_links_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                make_links.setEnabled(false);
                make_links.setBackgroundColor(getResources().getColor(R.color.browser_actions_title_color,null));
                accept_dialog.dismiss();
                acceptLinks();



            }
        });



        delete_links.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                make_links.setBackgroundColor(getResources().getColor(R.color.browser_actions_title_color,null));
                make_links.setEnabled(false);
                accept_dialog.dismiss();
                cancel_Request();

            }
        });






        not_for_now_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accept_dialog.dismiss();
            }
        });

        unlinked_btn = unlinked_dialog.findViewById(R.id.unlinked_btn);

        unlinked_never_btn = unlinked_dialog.findViewById(R.id.unLinked_never_btn);
        unlinked_user_img = unlinked_dialog.findViewById(R.id.unlinked_user_image);
        unlinked_username_tv = unlinked_dialog.findViewById(R.id.unlinked_username_tv);



        unlinked_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unlinked_dialog.dismiss();
                make_links.setEnabled(false);
                make_links.setBackgroundColor(getResources().getColor(R.color.browser_actions_title_color,null));

                unLinkedThisAccount();

            }
        });

        unlinked_never_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unlinked_dialog.dismiss();
            }
        });



        toolbar = findViewById(R.id.linked_list_main_toolbar);

        setSupportActionBar(toolbar);



        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.sort){
                    Toast.makeText(UserLinkedList.this, "hiii", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });


        getAllLinkedUsers();



    }







    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser!=null){
            reference.child("online").setValue(true);
        }

    }



    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.linked_list_menu,menu);
        return true;


    }


    private void getAllLinkedUsers() {


        arrayList = new ArrayList<Links_Model>();
        search_progressBar.setVisibility(View.VISIBLE);
        //get Current user
        //get Path of database named users defining in users info;


        link_ref.keepSynced(true);

        link_ref.child(userId2).child("Total_Links").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Links_Model links_model = ds.getValue(Links_Model.class);
                    arrayList.add(links_model);
                }

                Links_Adapter links_adapter = new Links_Adapter(arrayList, UserLinkedList.this, UserLinkedList.this);
                recyclerView.setAdapter(links_adapter);
                search_progressBar.setVisibility(View.GONE);

                search_layout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }






    private void dialog_Builder(Dialog dialog, String userId, CircleImageView user_img, TextView username_tv) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");


        reference.child("user_details").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.child("username").getValue(String.class);


                username_tv.setText(username);

                if(Objects.equals(snapshot.child("verify_user").getValue(String.class), "verify")){
                    username_tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.verify_user, 0);

                }


                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.teamwork_symbol);
                Glide.with(UserLinkedList.this)
                        .setDefaultRequestOptions(requestOptions)
                        .load(snapshot.child("img_uri").getValue(String.class)).into(user_img);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dialog.show();


    }





    @Override
    public void MakeLinksClickListener(int position, MaterialButton make_links2) {

        make_links=make_links2;
        userId = arrayList.get(position).getUserId();

        new InternetCheck(internet -> {
            if(internet.booleanValue()){
                if(make_links.getText().toString().trim().equals("Make Links")){

                    sendLinks();


                }else if(make_links.getText().toString().trim().equals("Links Sent")){

                    dialog_Builder(cancel_request_dialog,userId,cancel_request_user_img,cancel_request_username_tv);


                }else if(make_links.getText().toString().trim().equals("Invitations")){

                    dialog_Builder(accept_dialog,userId,accept_user_img,accept_username_tv);


                }else{
                    dialog_Builder(unlinked_dialog,userId,unlinked_user_img,unlinked_username_tv);
                }

            }else {
                error_content_tv.setText("No Internet Connection");
                error_dialog.show();
            }

        });




    }

    @Override
    public void setOnItemClickListener(int position) {



        if(!(arrayList.get(position).getUserId() == null)){
            if(arrayList.get(position).getUserId().equals(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())){
                Intent intent = new Intent(UserLinkedList.this, MainActivity.class);
                overridePendingTransition( 0, 0);
                startActivity(intent);
                overridePendingTransition( 0, 0);
                finish();

            }else{
                Intent intent = new Intent(UserLinkedList.this,UserProfile.class);
                intent.putExtra("userId",arrayList.get(position).getUserId());
                overridePendingTransition( 0, 0);
                startActivity(intent);
                overridePendingTransition( 0, 0);
            }
        }





    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }




    private void sendLinks() {

        make_links.setBackgroundColor(getResources().getColor(R.color.link_sent,null));
        make_links.setEnabled(true);
        make_links.setText("Links Sent");
        linksRequest.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())
                .child(userId).child("request_type")
                .setValue("sent")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        linksRequest.child(userId).child(firebaseAuth.getCurrentUser().getUid())
                                .child("request_type").setValue("received")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }



    private void cancel_Request() {


        make_links.setBackgroundColor(getResources().getColor(R.color.make_links));
        make_links.setEnabled(true);
        make_links.setText("Make Links");

        linksRequest.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())
                .child(userId).child("request_type")
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        linksRequest.child(userId).child(firebaseAuth.getCurrentUser().getUid())
                                .child("request_type")
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
                                    @Override
                                    public void onSuccess(Void aVoid) {


                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }



    private void acceptLinks() {

        make_links.setBackgroundColor(getResources().getColor(R.color.Linked,null));
        make_links.setEnabled(true);
        make_links.setText("Linked");

        link_ref.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child("Total_Links").child(userId)
                .child("userId").setValue(userId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        link_ref.child(userId).child("Total_Links").child(firebaseAuth.getCurrentUser().getUid()).child("userId")
                                .setValue(firebaseAuth.getCurrentUser().getUid())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        linksRequest.child(firebaseAuth.getCurrentUser().getUid())
                                                .child(userId).child("request_type")
                                                .removeValue()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        linksRequest.child(userId).child(firebaseAuth.getCurrentUser().getUid())
                                                                .child("request_type")
                                                                .removeValue()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {



                                                                    }
                                                                });
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });

                                    }

                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void unLinkedThisAccount() {


        make_links.setText("Make Links");
        make_links.setBackgroundColor(getResources().getColor(R.color.make_links,null));
        make_links.setEnabled(true);

        link_ref.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child("Total_Links")
                .child(userId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        link_ref.child(userId).child("Total_Links").child(firebaseAuth.getCurrentUser().getUid()).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onSuccess(Void aVoid) {





                                    }

                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }






}