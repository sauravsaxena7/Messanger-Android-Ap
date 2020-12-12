package com.jlcsoftware.bloodbankcommunity.MainFragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.jlcsoftware.bloodbankcommunity.Models.Links_Model;
import com.jlcsoftware.bloodbankcommunity.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;


public class LinkedListFragment extends Fragment implements RecyclerViewClickListener {


    ArrayList<Links_Model> arrayList;
    private RecyclerView recyclerView;

    private ProgressBar search_progressBar;


    String userId,userId2;

    TextView no_user_found;

    private TextView links_count, cancel_request_username_tv,accept_username_tv,unlinked_username_tv;

    private DatabaseReference linksRequest,link_ref;

    private FirebaseAuth firebaseAuth;

    private CircleImageView cancel_request_user_img , accept_user_img,unlinked_user_img;

    private Dialog cancel_request_dialog,accept_dialog,unlinked_dialog;

    private MaterialButton make_links,cancel_request_btn,never_btn,accept_links_btn ,delete_links,not_for_now_btn,unlinked_btn,unlinked_never_btn;

    public LinkedListFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        View view=inflater.inflate(R.layout.fragment_linked_list, container, false);

        recyclerView = view.findViewById(R.id.users_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);


        firebaseAuth = FirebaseAuth.getInstance();

        link_ref = FirebaseDatabase.getInstance().getReference("AllLinks");
        linksRequest = FirebaseDatabase.getInstance()
                .getReference("All_Links_Request").child("links_request");

        links_count = view.findViewById(R.id.link_list_count_tv);

        userId2 = getArguments().getString("userId");


        link_ref.child(userId2).child("Total_Links").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                links_count.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        no_user_found = view.findViewById(R.id.no_user_found);
        search_progressBar = view.findViewById(R.id.user_search_progressBar);




        cancel_request_dialog=new Dialog(getActivity());
        cancel_request_dialog.setContentView(R.layout.cancel_request_layout);

        accept_dialog=new Dialog(getActivity());
        accept_dialog.setContentView(R.layout.accept_links_layout);



        unlinked_dialog = new Dialog(getActivity());
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







        getAllLinkedUsers();


        return view;
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

                for(DataSnapshot ds : snapshot.getChildren()){
                    Links_Model links_model = ds.getValue(Links_Model.class);
                        arrayList.add(links_model);
                }

                Links_Adapter links_adapter = new Links_Adapter(arrayList,getActivity(),LinkedListFragment.this);
                recyclerView.setAdapter(links_adapter);
                search_progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }



    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.linked_list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void MakeLinksClickListener(int position, MaterialButton make_links2) {



        make_links=make_links2;
        userId = arrayList.get(position).getUserId();

        if(make_links.getText().toString().trim().equals("Make Links")){

            sendLinks();


        }else if(make_links.getText().toString().trim().equals("Links Sent")){

            dialog_Builder(cancel_request_dialog,userId,cancel_request_user_img,cancel_request_username_tv);


        }else if(make_links.getText().toString().trim().equals("Invitations")){

            dialog_Builder(accept_dialog,userId,accept_user_img,accept_username_tv);


        }else{
            dialog_Builder(unlinked_dialog,userId,unlinked_user_img,unlinked_username_tv);
        }


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
                Glide.with(Objects.requireNonNull(getActivity()))
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
    public void setOnItemClickListener(int position) {

        UserProfileFragment userProfileFragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString("userId",arrayList.get(position).getUserId());
        userProfileFragment.setArguments(args);

        if(!(arrayList.get(position).getUserId() == null)){
            if(arrayList.get(position).getUserId().equals(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())){
                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction().add(R.id.fragment_layout, new ProfileFragment()).commit();
            }else{
                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction().add(R.id.fragment_layout, userProfileFragment).addToBackStack(null).commit();
            }
        }



    }




    private void sendLinks() {

        make_links.setBackgroundColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.link_sent,null));
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


        make_links.setBackgroundColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.make_links,null));
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

        make_links.setBackgroundColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.Linked,null));
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
        make_links.setBackgroundColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.make_links,null));
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