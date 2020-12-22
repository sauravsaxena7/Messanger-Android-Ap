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
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jlcsoftware.bloodbankcommunity.Adapter.RecyclerViewAdapter;
import com.jlcsoftware.bloodbankcommunity.ChatApplication.ChatActivity;
import com.jlcsoftware.bloodbankcommunity.MainActivity;
import com.jlcsoftware.bloodbankcommunity.Models.User_details_item;
import com.jlcsoftware.bloodbankcommunity.R;
import com.jlcsoftware.bloodbankcommunity.ValidateUserDetails.InternetCheck;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserProfile extends AppCompatActivity {


    private TextView username_tv,fullName_tv,cancel_request_username_tv,accept_username_tv,links_count_tv,unlinked_username_tv,error_content_tv;
    private RecyclerView user_details_recyclerview;


    private List<User_details_item> detailsList;

    private FirebaseAuth firebaseAuth;

    private ProgressBar progressBar;

    private LinearLayout profile_layout;

    private Toolbar toolbar;

    private CircleImageView profile_img,current_user_img,cancel_request_user_img , accept_user_img,unlinked_user_img;

    private DatabaseReference databaseReference_all,linksRequest,link_ref;


    private MaterialButton make_links,message_btn,
            cancel_request_btn,never_btn,accept_links_btn ,
            delete_links,not_for_now_btn,unlinked_btn,unlinked_never_btn,error_btn;





    private LinearLayout links_linear_layout;
    String userId,img_uri,username,verify_user;

    private Dialog cancel_request_dialog,accept_dialog,unlinked_dialog;

    private String save_current_date;


    private Dialog error_dialog;

    private DatabaseReference notificationRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        firebaseAuth = FirebaseAuth.getInstance();




        userId = getIntent().getStringExtra("userId");


        error_dialog=new Dialog(UserProfile.this);
        error_dialog.setContentView(R.layout.error_dialog);
        error_content_tv=error_dialog.findViewById(R.id.error_content_tv);
        error_btn=error_dialog.findViewById(R.id.error_button_id);

        error_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error_dialog.dismiss();
            }
        });



        cancel_request_dialog=new Dialog(UserProfile.this);
        cancel_request_dialog.setContentView(R.layout.cancel_request_layout);

        accept_dialog=new Dialog(UserProfile.this);
        accept_dialog.setContentView(R.layout.accept_links_layout);



        unlinked_dialog = new Dialog(UserProfile.this);
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



        databaseReference_all = FirebaseDatabase.getInstance()
                .getReference("users");




        links_count_tv = findViewById(R.id.user_profile_links_count);




        databaseReference_all.keepSynced(true);





        links_count_tv = findViewById(R.id.user_profile_links_count);




        databaseReference_all.keepSynced(true);





        linksRequest = FirebaseDatabase.getInstance()
                .getReference("All_Links_Request").child("links_request");

        link_ref = FirebaseDatabase.getInstance().getReference("AllLinks");

        notificationRef = FirebaseDatabase.getInstance().getReference().child("notifications");







        profile_layout = findViewById(R.id.profile_layout);
        progressBar = findViewById(R.id.user_details_progressBar);

        profile_img = findViewById(R.id.profile_image);

        current_user_img = findViewById(R.id.current_user_image_id);

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

        ref.child("user_details").child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.teamwork_symbol);
                Glide.with(UserProfile.this)
                        .setDefaultRequestOptions(requestOptions)
                        .load(snapshot.child("img_uri").getValue(String.class)).into(current_user_img);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        current_user_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition( 0, 0);
                startActivity(new Intent(UserProfile.this, MainActivity.class));
                overridePendingTransition( 0, 0);
            }
        });


        fullName_tv = findViewById(R.id.user_profile_fullName);
        username_tv = findViewById(R.id.user_profile_username);




        progressBar.setVisibility(View.VISIBLE);

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");







        links_linear_layout = findViewById(R.id.links_linear_layout);

        links_linear_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               Intent intent = new Intent(UserProfile.this,UserLinkedList.class);
               intent.putExtra("userId",userId);
               intent.putExtra("username",username);
               intent.putExtra("verify_user",verify_user);
                overridePendingTransition( 0, 0);
                startActivity(intent);
                overridePendingTransition( 0, 0);

                //getFragmentManager().beginTransaction().add(R.id.fragment_layout, linkedListFragment).addToBackStack(null).commit();
            }
        });


        reference.keepSynced(true);

        reference.child("user_details").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "CheckResult"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String first_name = snapshot.child("first_name").getValue(String.class);
                String last_name = snapshot.child("last_name").getValue(String.class);
                username = snapshot.child("username").getValue(String.class);





                username_tv.setText(username);
                fullName_tv.setText(first_name+" "+last_name);

                cancel_request_username_tv.setText(username);
                accept_username_tv.setText(username);
                unlinked_username_tv.setText(username);

                verify_user = snapshot.child("verify_user").getValue(String.class);


                if(snapshot.child("verify_user").getValue(String.class).equals("verify")){
                    username_tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.verify_user, 0);
                    cancel_request_username_tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.verify_user, 0);
                    accept_username_tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.verify_user, 0);
                    unlinked_username_tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.verify_user, 0);
                }


                String email = firebaseAuth.getCurrentUser().getEmail();
                String blood_group = snapshot.child("blood_group").getValue(String.class);
                String country_name = snapshot.child("country_name").getValue(String.class);
                String dob = snapshot.child("dob_date").getValue(String.class);
                String dob_year = snapshot.child("dob_year").getValue(String.class);
                String gender = snapshot.child("gender").getValue(String.class);
                String phone_number = firebaseAuth.getCurrentUser().getPhoneNumber();
                String weight = snapshot.child("weight").getValue(String.class);
                img_uri = snapshot.child("img_uri").getValue(String.class);
                String work_as = snapshot.child("work_as").getValue(String.class);
                String current_address = snapshot.child("current_address").getValue(String.class);





                if(!img_uri.equals("")){
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.teamwork_symbol);
                    Glide.with(UserProfile.this)
                            .setDefaultRequestOptions(requestOptions)
                            .load(img_uri).into(profile_img);



                    Glide.with(UserProfile.this)
                            .setDefaultRequestOptions(requestOptions)
                            .load(img_uri).into(cancel_request_user_img);



                    Glide.with(UserProfile.this)
                            .setDefaultRequestOptions(requestOptions)
                            .load(img_uri).into(accept_user_img);

                    Glide.with(UserProfile.this)
                            .setDefaultRequestOptions(requestOptions)
                            .load(img_uri).into(unlinked_user_img);


                }

                int current_year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);



                int birth_year= Integer.parseInt(dob_year);

                int age=current_year-birth_year;
                String Age=String.valueOf(age);


                //here we extract all details of profile of other user
                detailsList=new ArrayList<>();

                detailsList.add(new User_details_item(R.drawable.email_icon,email));
                detailsList.add(new User_details_item(R.drawable.blood_group,blood_group));
                detailsList.add(new User_details_item(R.drawable.birth_icon,dob));
                detailsList.add(new User_details_item(R.drawable.gender_icon,gender));
                detailsList.add(new User_details_item(R.drawable.location,current_address));
                detailsList.add(new User_details_item(R.drawable.nationality_icon,country_name));
                detailsList.add(new User_details_item(R.drawable.phone,phone_number));
                detailsList.add(new User_details_item(R.drawable.work,work_as));
                detailsList.add(new User_details_item(R.drawable.wight_icon,weight+" Kg"));
                detailsList.add(new User_details_item(R.drawable.age_icon,Age+" years old"));
                detailsList.add(new User_details_item(R.drawable.medical_report,"can't say..."));


                user_details_recyclerview = findViewById(R.id.user_details_recycler_view);



                RecyclerViewAdapter recyclerViewAdapter=new RecyclerViewAdapter(UserProfile.this,detailsList);

                user_details_recyclerview.setLayoutManager(new LinearLayoutManager(UserProfile.this));

                user_details_recyclerview.setAdapter(recyclerViewAdapter);



                link_ref.child(userId).child("Total_Links").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        links_count_tv.setText(String.valueOf(snapshot.getChildrenCount()));
                        mainTainButton_Button_State_Resolution();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        make_links  = findViewById(R.id.make_links_or_linked_btn);

        make_links.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new InternetCheck(internet -> {

                    if(internet.booleanValue()){

                        if(make_links.getText().toString().trim().equals("Make Links")){


                            make_links.setBackgroundColor(getResources().getColor(R.color.browser_actions_title_color));
                            make_links.setEnabled(false);

                            sendLinks();


                        }else if(make_links.getText().toString().trim().equals("Links Sent")){

                            cancel_request_dialog.show();

                        }else if(make_links.getText().toString().trim().equals("Invitations")){

                            accept_dialog.show();

                        }else{
                            unlinked_dialog.show();
                        }

                    }else {
                        error_content_tv.setText("No Internet Connection");
                        error_dialog.show();
                    }

                });


            }
        });







        message_btn = findViewById(R.id.message_btn);

        message_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent intent = new Intent(UserProfile.this, ChatActivity.class);
                intent.putExtra("userId",userId);
                startActivity(intent);
            }
        });






        toolbar = findViewById(R.id.user_profile_toolbar);

        setSupportActionBar(toolbar);



        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                if(item.getItemId()==R.id.refresh){
                    finish();
                    overridePendingTransition( 0, 0);
                    startActivity(getIntent());
                    overridePendingTransition( 0, 0);
                }

                return true;
            }
        });






    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    private void mainTainButton_Button_State_Resolution() {


        linksRequest.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(userId)){

                            String request_type = snapshot.child(userId).child("request_type").getValue(String.class);

                            if(request_type.equals("sent")){
                                make_links.setBackgroundColor(getResources().getColor(R.color.link_sent,null));
                                make_links.setText("Links Sent");
                            } else if(request_type.equals("received")){
                                make_links.setBackgroundColor(getResources().getColor(R.color.links_invititation,null));
                                make_links.setText("Invitations");
                            }
                            else{
                                make_links.setBackgroundColor(R.color.make_links);
                                make_links.setText("Make Links");
                            }

                        }






                        link_ref.child(firebaseAuth.getCurrentUser().getUid()).child("Total_Links")
                                .child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if(snapshot.exists()){
                                    make_links.setBackgroundColor(getResources().getColor(R.color.Linked,null));
                                    make_links.setText("Linked");

                                    progressBar.setVisibility(View.GONE);
                                    profile_layout.setVisibility(View.VISIBLE);
                                }else{
                                    progressBar.setVisibility(View.GONE);
                                    profile_layout.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }







    private void sendLinks() {
        linksRequest.child(firebaseAuth.getCurrentUser().getUid())
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

                                        make_links.setBackgroundColor(getResources().getColor(R.color.link_sent,null));
                                        make_links.setEnabled(true);
                                        make_links.setText("Links Sent");

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

                                        make_links.setBackgroundColor(getResources().getColor(R.color.make_links,null));
                                        make_links.setEnabled(true);
                                        make_links.setText("Make Links");
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
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new  SimpleDateFormat("dd-MMMM-yyyy");
        save_current_date = simpleDateFormat.format(calendar.getTime());


        link_ref.child(firebaseAuth.getCurrentUser().getUid()).child("Total_Links").child(userId)
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

                                                                        make_links.setBackgroundColor(getResources().getColor(R.color.Linked,null));
                                                                        make_links.setEnabled(true);
                                                                        make_links.setText("Linked");
                                                                        link_ref.child(userId).child("Total_Links").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                links_count_tv.setText(String.valueOf(snapshot.getChildrenCount()));


                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });

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



        link_ref.child(firebaseAuth.getCurrentUser().getUid()).child("Total_Links")
                .child(userId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        link_ref.child(userId).child("Total_Links").child(firebaseAuth.getCurrentUser().getUid()).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        make_links.setText("Make Links");
                                        make_links.setBackgroundColor(getResources().getColor(R.color.make_links,null));
                                        make_links.setEnabled(true);
                                        link_ref.child(userId).child("Total_Links").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                links_count_tv.setText(String.valueOf(snapshot.getChildrenCount()));


                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.user_profile_menu,menu);


        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}