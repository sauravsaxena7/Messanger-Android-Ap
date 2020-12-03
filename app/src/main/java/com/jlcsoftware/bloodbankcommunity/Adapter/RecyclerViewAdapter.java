package com.jlcsoftware.bloodbankcommunity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jlcsoftware.bloodbankcommunity.Models.User_details_item;
import com.jlcsoftware.bloodbankcommunity.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {




    Context mContext;

    List<User_details_item> mData;




    public RecyclerViewAdapter(Context context, List<User_details_item> contactList) {
        mContext=context;
        mData=contactList;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        view= LayoutInflater.from(mContext).inflate(R.layout.user_details_item_layout,parent,false);

        final MyViewHolder vHolder = new MyViewHolder(view);




        vHolder.linearLayout_item_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Text click"+String.valueOf(vHolder.getAdapterPosition()), Toast.LENGTH_SHORT).show();

            }
        });



        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        holder.item_name_tv.setText(mData.get(position).getDetails());

        holder.item_img.setImageResource(mData.get(position).getPhoto());



    }



    @Override
    public int getItemCount() {


        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{


        private TextView item_name_tv;

        private LinearLayout linearLayout_item_contact;



        private ImageView item_img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout_item_contact=itemView.findViewById(R.id.linear_layout_contact_item);

            item_name_tv=(TextView) itemView.findViewById(R.id.item_text);
            item_img=(ImageView) itemView.findViewById(R.id.item_image);
        }
    }


}