package com.jlcsoftware.bloodbankcommunity.ChatApplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jlcsoftware.bloodbankcommunity.Adapter.Chats_Adapter;
import com.jlcsoftware.bloodbankcommunity.Adapter.Links_Adapter;
import com.jlcsoftware.bloodbankcommunity.Interface.RecyclerViewClickListener;
import com.jlcsoftware.bloodbankcommunity.Models.Links_Model;
import com.jlcsoftware.bloodbankcommunity.NotCurrentUser.UserLinkedList;
import com.jlcsoftware.bloodbankcommunity.NotCurrentUser.UserProfile;
import com.jlcsoftware.bloodbankcommunity.R;

import java.util.ArrayList;

public class Chats extends AppCompatActivity implements RecyclerViewClickListener {

    ArrayList<Links_Model> arrayList;
    private RecyclerView recyclerView;

    private ProgressBar search_progressBar;

    DatabaseReference reference;
    private Toolbar toolbar;

    private String userId2;

    private DatabaseReference link_ref;

    TextView no_user_found;
    private FirebaseAuth firebaseAuth;

    private LinearLayout search_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);


        recyclerView = findViewById(R.id.users_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(Chats.this));
        recyclerView.setHasFixedSize(true);



        toolbar = findViewById(R.id.linked_list_main_toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.sort){
                    Toast.makeText(Chats.this, "h", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        link_ref = FirebaseDatabase.getInstance().getReference("AllLinks");

        userId2 = firebaseAuth.getCurrentUser().getUid();

        no_user_found = findViewById(R.id.no_user_found);
        search_progressBar = findViewById(R.id.user_search_progressBar);

        reference= FirebaseDatabase.getInstance()
                .getReference("users").child("user_details")
                .child(firebaseAuth.getCurrentUser().getUid());


        search_layout = findViewById(R.id.search_layout);



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

                Chats_Adapter chats_adapters = new Chats_Adapter(arrayList, Chats.this, Chats.this);
                recyclerView.setAdapter(chats_adapters);
                search_progressBar.setVisibility(View.GONE);
                search_layout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.linked_list_menu,menu);
        return true;
    }



    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void MakeLinksClickListener(int position, MaterialButton make_links) {

    }

    @Override
    public void setOnItemClickListener(int position) {

        Intent intent = new Intent(Chats.this, ChatActivity.class);
        intent.putExtra("userId",arrayList.get(position).getUserId());
        overridePendingTransition( 0, 0);
        startActivity(intent);
        overridePendingTransition( 0, 0);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}