package com.jlcsoftware.bloodbankcommunity.Adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jlcsoftware.bloodbankcommunity.Models.MessagesModels;
import com.jlcsoftware.bloodbankcommunity.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter {




    private List<MessagesModels> messageList;
    Context context;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    public MessagesAdapter(List<MessagesModels> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        MessagesModels  message =  messageList.get(position);
       if (message.getFrom().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
         // If the current user is the sender of the message
           Log.e("getItemViewType","0");
           return VIEW_TYPE_MESSAGE_SENT;
       } else {
           // If some other user sent the message
       }
           Log.e("getItemViewType","1");
           return VIEW_TYPE_MESSAGE_RECEIVED;
      }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sender_layout, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.receiver_layout, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MessagesModels message = messageList.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }



    private class SentMessageHolder extends RecyclerView.ViewHolder{

        TextView message;
        TextView time;

        LinearLayout message_text_layout,message_img_layout;
        ImageView imageView;


        public SentMessageHolder(@NonNull View itemView) {
            super(itemView);
            message = (TextView)itemView.findViewById(R.id.message);
            time = (TextView)itemView.findViewById(R.id.time);

            message_text_layout = itemView.findViewById(R.id.messageLayout_text_view);

            message_img_layout = itemView.findViewById(R.id.messageLayout_img);
            imageView = itemView.findViewById(R.id.sender_message_img);


        }

        @SuppressLint("CheckResult")
        void bind(MessagesModels messageModel) {

            if(messageModel.getType().equals("text")){
                message_img_layout.setVisibility(View.GONE);

                message.setText(messageModel.getMessage());
                message_text_layout.setVisibility(View.VISIBLE);

            }else if(messageModel.getType().equals("image")){

                message_text_layout.setVisibility(View.GONE);

                message_img_layout.setVisibility(View.VISIBLE);
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.teamwork_symbol);
                Glide.with(context.getApplicationContext())
                        .setDefaultRequestOptions(requestOptions)
                        .load(messageModel.getMessage()).into(imageView);

            }

            time.setText(String.valueOf(messageModel.getTime()));

        }

    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder{
        TextView message;
        TextView time;
        CircleImageView user_img;
        ImageView imageView;

        LinearLayout message_text_layout,message_img_layout;

        public ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
            user_img = itemView.findViewById(R.id.chat_user_profile_image);
            message_text_layout = itemView.findViewById(R.id.message_text);
            message_img_layout = itemView.findViewById(R.id.message_img);

            imageView = itemView.findViewById(R.id.receiver_message_img);
        }


        @SuppressLint("CheckResult")
        void bind(MessagesModels messageModel){

            if(messageModel.getType().equals("text")){
                message_img_layout.setVisibility(View.GONE);
                message.setText(messageModel.getMessage());
                message_text_layout.setVisibility(View.VISIBLE);
            }else if(messageModel.getType().equals("image")){

                message_text_layout.setVisibility(View.GONE);

                message_img_layout.setVisibility(View.VISIBLE);
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.teamwork_symbol);
                Glide.with(context.getApplicationContext())
                        .setDefaultRequestOptions(requestOptions)
                        .load(messageModel.getMessage()).into(imageView);
            }


            time.setText(String.valueOf(messageModel.getTime()));

            DatabaseReference reference  = FirebaseDatabase.getInstance().getReference("users");

            reference.keepSynced(true);
            reference.child("user_details").child(messageModel.getFrom()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String img_uri = snapshot.child("img_uri").getValue(String.class);
                    if(!img_uri.equals("")){
                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.placeholder(R.drawable.teamwork_symbol);
                        Glide.with(context.getApplicationContext())
                                .setDefaultRequestOptions(requestOptions)
                                .load(img_uri).into(user_img);



                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
    }




}
