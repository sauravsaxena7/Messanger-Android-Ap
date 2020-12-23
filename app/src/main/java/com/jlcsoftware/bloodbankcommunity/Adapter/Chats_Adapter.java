package com.jlcsoftware.bloodbankcommunity.Adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

public class Chats_Adapter extends RecyclerView.Adapter<Chats_Adapter.MyViewHolder>{




    ArrayList<Links_Model> arrayList;
    Context context;

    final private RecyclerViewClickListener clickListener;



    public Chats_Adapter(ArrayList<Links_Model> arrayList, Context context, RecyclerViewClickListener clickListener) {
        this.arrayList = arrayList;
        this.context = context;
        this.clickListener = clickListener;
    }



//

    @NonNull
    @Override
    public Chats_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.chats_item,parent,false);

        final Chats_Adapter.MyViewHolder myViewHolder = new Chats_Adapter.MyViewHolder(view);







        return myViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull Chats_Adapter.MyViewHolder holder, int position) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        reference.child("user_details").child(arrayList.get(position).getUserId()).addValueEventListener(new ValueEventListener() {
            @SuppressLint({"CheckResult", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                holder.fullName_tv.setText(snapshot.child("first_name").getValue(String.class)+" "+snapshot.child("last_name").getValue(String.class));



                if(snapshot.child("verify_user").getValue(String.class).equals("verify")){
                    holder.fullName_tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.verify_user, 0);
                }




                RequestOptions requestOptions = new RequestOptions();

                requestOptions.placeholder(R.drawable.teamwork_symbol);
                Glide.with(context.getApplicationContext())
                        .setDefaultRequestOptions(requestOptions)
                        .load(snapshot.child("img_uri").getValue(String.class)).into(holder.user_img);



                if(snapshot.child("online").getValue(Boolean.class)){

                    holder.online_status_img.setVisibility(View.VISIBLE);

                }else{

                    holder.online_status_img.setVisibility(View.GONE);

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

        TextView status_tv,fullName_tv;
        CircleImageView user_img;

        CircleImageView online_status_img;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


            fullName_tv = itemView.findViewById(R.id.chat_profile_name);
            user_img = itemView.findViewById(R.id.chat_user_image);
            online_status_img = itemView.findViewById(R.id.online_status);
            status_tv = itemView.findViewById(R.id.chat_status);


            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    clickListener.setOnItemClickListener(getAdapterPosition());
                }
            });



        }
    }

}
