package com.jlcsoftware.bloodbankcommunity.MainFragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jlcsoftware.bloodbankcommunity.Adapter.Search_User_Adapters;
import com.jlcsoftware.bloodbankcommunity.Interface.RecyclerViewClickListener;
import com.jlcsoftware.bloodbankcommunity.Models.Model_user_details;
import com.jlcsoftware.bloodbankcommunity.NotCurrentUser.UserProfile;
import com.jlcsoftware.bloodbankcommunity.R;

import java.util.ArrayList;


public class SearchFragment extends Fragment implements RecyclerViewClickListener{




    ArrayList<Model_user_details> arrayList;
    private RecyclerView recyclerView;

    private ProgressBar search_progressBar;

    private Toolbar toolbar;

    TextView no_user_found;

    private EditText search_et;

    SearchView searchView;


    public SearchFragment() {
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

        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);

        View view=inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.users_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);


        no_user_found = view.findViewById(R.id.no_user_found);
        search_progressBar = view.findViewById(R.id.user_search_progressBar);


        EditText editText = view.findViewById(R.id.user_search_et);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                searchUser(s);

            }
        });




        toolbar = view.findViewById(R.id.search_main_toolbar);
        setHasOptionsMenu(true);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(TextUtils.isEmpty(editText.getText().toString().trim())){
                    editText.setError("Wrong Input");
                }else{
                    searchUser(editText.getText().toString().trim());
                }
                return true;
            }
        });









        getAllUsers();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu_user, menu);
        MenuItem searchItem = menu.findItem(R.id.search);


        super.onCreateOptionsMenu(menu, inflater);

    }



    private void getAllUsers(){

        arrayList = new ArrayList<>();
        search_progressBar.setVisibility(View.VISIBLE);
        //get Current user
        final FirebaseUser fUser= FirebaseAuth.getInstance().getCurrentUser();
        //get Path of database named users defining in users info;

        DatabaseReference reference= FirebaseDatabase.getInstance()
                .getReference("users");
        reference.keepSynced(true);



        reference.keepSynced(true);
        reference.child("user_details").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds : snapshot.getChildren()){
                    Model_user_details model_user_details = ds.getValue(Model_user_details.class);
                    if(!model_user_details.getUserId().equals(fUser.getUid())){
                        if(!model_user_details.getLast_name().equals("")){
                            arrayList.add(model_user_details);
                        }


                    }

                }

                Search_User_Adapters  search_user_adapters = new Search_User_Adapters(arrayList,getActivity(),SearchFragment.this);
                recyclerView.setAdapter(search_user_adapters);
                search_progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }



    private void searchUser(final CharSequence query) {


        arrayList = new ArrayList<>();


        DatabaseReference reference= FirebaseDatabase.getInstance()
                .getReference("users");
        reference.keepSynced(true);

        reference.keepSynced(true);
        reference.child("user_details").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds : snapshot.getChildren()) {
                    Model_user_details model_user_details = ds.getValue(Model_user_details.class);
                    if (!model_user_details.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                        String fullName = model_user_details.getFirst_name() + " " + model_user_details.getLast_name();


                        if (model_user_details.getUsername().contains(query)
                                || fullName.contains(query)
                                || model_user_details.getEmail().contains(query)) {



                            if(!model_user_details.getLast_name().equals("")){

                                arrayList.add(model_user_details);

                            }



                        }


                    }
                }



                if(arrayList.isEmpty()){
                   no_user_found.setVisibility(View.VISIBLE);
                }else{
                    no_user_found.setVisibility(View.GONE);
                }
                Search_User_Adapters  search_user_adapters = new Search_User_Adapters(arrayList,getActivity(),SearchFragment.this);
                recyclerView.setAdapter(search_user_adapters);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public void MakeLinksClickListener(int position, MaterialButton make_links) {

    }

    @Override
    public void setOnItemClickListener(int position) {


        Intent intent = new Intent(getActivity(),UserProfile.class);
        intent.putExtra("userId",arrayList.get(position).getUserId());

        getActivity().overridePendingTransition( 0, 0);
        startActivity(intent);
        getActivity().overridePendingTransition( 0, 0);



    }





}