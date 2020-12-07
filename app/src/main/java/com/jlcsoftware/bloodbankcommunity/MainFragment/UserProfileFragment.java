package com.jlcsoftware.bloodbankcommunity.MainFragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jlcsoftware.bloodbankcommunity.Adapter.RecyclerViewAdapter;
import com.jlcsoftware.bloodbankcommunity.Models.User_details_item;
import com.jlcsoftware.bloodbankcommunity.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserProfileFragment extends Fragment {
    private TextView username_tv,fullName_tv;
    private RecyclerView user_details_recyclerview;


    private List<User_details_item> detailsList;

    private FirebaseAuth firebaseAuth;

    private ProgressBar progressBar;

    private LinearLayout profile_layout;

    private Dialog logout_dialog;
    private MaterialButton sure_btn,not_sure_btn;

    private Toolbar toolbar;

    private CircleImageView profile_img,current_user_img;

    private DatabaseReference databaseReference_all;

    public UserProfileFragment() {
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

        View view=inflater.inflate(R.layout.fragment_user_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();


        databaseReference_all = FirebaseDatabase.getInstance()
                .getReference("users");

        databaseReference_all.keepSynced(true);

        profile_layout = view.findViewById(R.id.profile_layout);
        progressBar = view.findViewById(R.id.user_details_progressBar);

        profile_img = view.findViewById(R.id.profile_image);

        current_user_img = view.findViewById(R.id.current_user_image_id);

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

        ref.child("user_details").child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.teamwork_symbol);
                Glide.with(getActivity())
                        .setDefaultRequestOptions(requestOptions)
                        .load(snapshot.child("img_uri").getValue(String.class)).into(current_user_img);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        fullName_tv = view.findViewById(R.id.user_profile_fullName);
        username_tv = view.findViewById(R.id.user_profile_username);


        progressBar.setVisibility(View.VISIBLE);

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");


        String userId = getArguments().getString("userId");


        reference.child("user_details").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String first_name = snapshot.child("first_name").getValue(String.class);
                String last_name = snapshot.child("last_name").getValue(String.class);
                String username = snapshot.child("username").getValue(String.class);





                username_tv.setText(username);
                fullName_tv.setText(first_name+" "+last_name);


                if(snapshot.child("verify_user").getValue(String.class).equals("verify")){
                    username_tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.verify_user, 0);
                }

                String email = firebaseAuth.getCurrentUser().getEmail();
                String blood_group = snapshot.child("blood_group").getValue(String.class);
                String country_name = snapshot.child("country_name").getValue(String.class);
                String dob = snapshot.child("dob_date").getValue(String.class);
                String dob_year = snapshot.child("dob_year").getValue(String.class);
                String gender = snapshot.child("gender").getValue(String.class);
                String phone_number = firebaseAuth.getCurrentUser().getPhoneNumber();
                String weight = snapshot.child("weight").getValue(String.class);
                String img_uri = snapshot.child("img_uri").getValue(String.class);
                String work_as = snapshot.child("work_as").getValue(String.class);
                String current_address = snapshot.child("current_address").getValue(String.class);




                if(!img_uri.equals("")){
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.teamwork_symbol);
                    Glide.with(getActivity())
                            .setDefaultRequestOptions(requestOptions)
                            .load(img_uri).into(profile_img);
                }

                int current_year = Calendar.getInstance().get(Calendar.YEAR);



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


                user_details_recyclerview = view.findViewById(R.id.user_details_recycler_view);



                RecyclerViewAdapter recyclerViewAdapter=new RecyclerViewAdapter(getActivity(),detailsList);

                user_details_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

                user_details_recyclerview.setAdapter(recyclerViewAdapter);


                progressBar.setVisibility(View.GONE);
                profile_layout.setVisibility(View.VISIBLE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}