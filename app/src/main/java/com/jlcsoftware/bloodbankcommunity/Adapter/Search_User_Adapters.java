package com.jlcsoftware.bloodbankcommunity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jlcsoftware.bloodbankcommunity.Interface.RecyclerViewClickListener;
import com.jlcsoftware.bloodbankcommunity.Models.Model_user_details;
import com.jlcsoftware.bloodbankcommunity.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class Search_User_Adapters  extends RecyclerView.Adapter<Search_User_Adapters.MyViewHolder> {


    ArrayList<Model_user_details> arrayList;
    Context context;

    final private RecyclerViewClickListener clickListener;

    public Search_User_Adapters(ArrayList<Model_user_details> arrayList, Context context, RecyclerViewClickListener clickListener) {

        this.arrayList = arrayList;
        this.context = context;
        this.clickListener = clickListener;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);

        final MyViewHolder myViewHolder = new MyViewHolder(view);






        return myViewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull Search_User_Adapters.MyViewHolder holder, int position) {

        final String username = arrayList.get(position).getUsername();
        final String first_name = arrayList.get(position).getFirst_name();

        final String last_name = arrayList.get(position).getLast_name();

        final String img_uri = arrayList.get(position).getImg_uri();

        holder.fullName_tv.setText(first_name+" "+last_name);
        holder.username_tv.setText(username);

        if(arrayList.get(position).getVerify_user().equals("verify")){
            holder.username_tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.verify_user, 0);
        }

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.teamwork_symbol);
        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(img_uri).into(holder.user_img);



    }



    @Override
    public int getItemCount() {
        return arrayList.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView username_tv,fullName_tv;
        CircleImageView user_img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            username_tv = itemView.findViewById(R.id.row_user_username_tv_id);
            fullName_tv = itemView.findViewById(R.id.row_user_profile_name_tv);
            user_img = itemView.findViewById(R.id.row_user_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.setOnItemClickListener(getAdapterPosition());
                }
            });


        }
    }

}
