package com.jlcsoftware.bloodbankcommunity.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jlcsoftware.bloodbankcommunity.Interface.RecyclerViewClickListener;
import com.jlcsoftware.bloodbankcommunity.Models.Links_Model;
import com.jlcsoftware.bloodbankcommunity.R;

import java.util.ArrayList;



import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class Links_Adapter extends RecyclerView.Adapter<Links_Adapter.MyViewHolder>{






    ArrayList<Links_Model> arrayList;
    Context context;

    final private RecyclerViewClickListener clickListener;



    public Links_Adapter(ArrayList<Links_Model> arrayList, Context context, RecyclerViewClickListener clickListener) {
        this.arrayList = arrayList;
        this.context = context;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public Links_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(context).inflate(R.layout.links_item,parent,false);

        final MyViewHolder myViewHolder = new MyViewHolder(view);







        return myViewHolder;
    }





    @SuppressLint({"SetTextI18n", "CheckResult"})
    @Override
    public void onBindViewHolder(@NonNull Links_Adapter.MyViewHolder holder, int position) {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        reference.child("user_details").child(arrayList.get(position).getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.fullName_tv.setText(snapshot.child("first_name").getValue(String.class)+" "+snapshot.child("last_name").getValue(String.class));
                holder.username_tv.setText(snapshot.child("username").getValue(String.class));

                if(snapshot.child("verify_user").getValue(String.class).equals("verify")){
                    holder.username_tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.verify_user, 0);
                }

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.teamwork_symbol);
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(snapshot.child("img_uri").getValue(String.class)).into(holder.user_img);


                if(arrayList.get(position).getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                    holder.make_links.setVisibility(View.GONE);

                }else{

                    holder.make_links.setEnabled(false);
                    mainTainButton_Button_State_Resolution(holder.make_links,arrayList.get(position).getUserId());

                }





            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    @Override
    public int getItemCount() {

        return arrayList.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView username_tv,fullName_tv;
        CircleImageView user_img;

        MaterialButton make_links;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            username_tv = itemView.findViewById(R.id.row_user_username_tv_id);
            fullName_tv = itemView.findViewById(R.id.row_user_profile_name_tv);
            user_img = itemView.findViewById(R.id.row_user_image);
            make_links = itemView.findViewById(R.id.make_links_or_linked_btn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.setOnItemClickListener(getAdapterPosition());
                }
            });

            make_links.setEnabled(false);

            make_links.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    clickListener.MakeLinksClickListener(getAdapterPosition() ,make_links);
                }
            });


        }
    }




    private void mainTainButton_Button_State_Resolution(MaterialButton make_links,String userId) {


       DatabaseReference linksRequest = FirebaseDatabase.getInstance()
                .getReference("All_Links_Request").child("links_request");

        linksRequest.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(userId)){

                            String request_type = snapshot.child(userId).child("request_type").getValue(String.class);

                            if(request_type.equals("sent")){
                                make_links.setBackgroundColor(context.getResources().getColor(R.color.link_sent,null));
                                make_links.setText("Links Sent");
                            } else if(request_type.equals("received")){
                                make_links.setBackgroundColor(context.getResources().getColor(R.color.links_invititation,null));
                                make_links.setText("Invitations");
                            }
                            else{
                                make_links.setBackgroundColor(R.color.make_links);
                                make_links.setText("Make Links");
                            }

                        }






                     DatabaseReference   link_ref = FirebaseDatabase.getInstance().getReference("AllLinks");

                        link_ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Total_Links")
                                .child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if(snapshot.exists()){
                                    make_links.setBackgroundColor(context.getResources().getColor(R.color.Linked,null));
                                    make_links.setText("Linked");
                                    make_links.setEnabled(true);



                                }else {
                                    make_links.setEnabled(true);

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



}
