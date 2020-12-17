package com.jlcsoftware.bloodbankcommunity.MainFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.jlcsoftware.bloodbankcommunity.MainActivity;
import com.jlcsoftware.bloodbankcommunity.R;
import com.jlcsoftware.bloodbankcommunity.UserDetails.DatePickerFragment;
import com.jlcsoftware.bloodbankcommunity.UserDetails.User_Details;
import com.jlcsoftware.bloodbankcommunity.ValidateUserDetails.ValidateEditUserProfile;
import com.jlcsoftware.bloodbankcommunity.ValidateUserDetails.ValidateUserDetails;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class EditProfileFragment extends Fragment {

    private Toolbar toolbar;
    private Dialog loading_dialog;

    private TextInputLayout username_et,first_name_et,last_name_et,current_address_et,weight_et,work_as_et;

    private String username,first_name,last_name,current_address,weight,work_as,gender,dob,blood_group,dob_year;

    private ProgressBar progressBar,img_progress_bar;

    private LinearLayout edit_user_profile_layout;


    private FirebaseAuth firebaseAuth;


    private CircleImageView edit_img_icon,uploaded_image;


    private DatabaseReference databaseReference_all;

    String img_uri_str;
    Uri img_uri;

    private TextView date_of_birth_tv,blood_group_tv;

    private Dialog blood_group_picker_dialog,error_dialog;

    private RadioGroup radioGroup,gender_radioGroup;

    String toUpdateFirst_Name,toUpdateUsername,toUpdateLast_Name,toUpdateCurrent_Address,toUpdateWeight,toUpdateWork_As;

    RadioButton radioButton;

    private View view;

    private TextView error_content_tv;
    private MaterialButton error_btn;

    public EditProfileFragment() {
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
        bottomNavigationView.setVisibility(View.GONE);


        view=inflater.inflate(R.layout.fragment_edit_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference_all = FirebaseDatabase.getInstance()
                .getReference("users");

        databaseReference_all.keepSynced(true);

        toolbar = view.findViewById(R.id.edit_main_toolbar);
        setHasOptionsMenu(true);


        error_dialog = new Dialog(getActivity());
        error_dialog.setContentView(R.layout.error_dialog);
        error_content_tv=error_dialog.findViewById(R.id.error_content_tv);
        error_btn=error_dialog.findViewById(R.id.error_button_id);

        error_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error_dialog.dismiss();
            }
        });


        loading_dialog=new Dialog(getActivity());
        loading_dialog.setContentView(R.layout.loading_dialog_layout);

        loading_dialog.setCanceledOnTouchOutside(false);

        loading_dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // Prevent dialog close on back press button
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });



        edit_img_icon = view.findViewById(R.id.edit_user_profile_edit_image_icon);



        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){

                    case R.id.cancel:
                        //begin Transaction

                        startActivity(new Intent(getActivity(),MainActivity.class));
                        getActivity().finish();

                        break;

                    case R.id.checked:


                        if(isValidProfile() && (img_progress_bar.getVisibility() == View.INVISIBLE)){

                            if(username.equals(toUpdateUsername)){
                                loading_dialog.show();
                                updateProfile();
                            }else{
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child("user_details");
                                ref.orderByChild("username").equalTo(toUpdateUsername).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            username_et.getEditText().setError("Username not available");
                                        }else{
                                            loading_dialog.show();
                                            updateProfile();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                        }


                        break;

                }

                return true;
            }
        });


        username_et = view.findViewById(R.id.edit_user_profile_username_et);
        first_name_et = view.findViewById(R.id.edit_user_profile_first_name_et);
        last_name_et = view.findViewById(R.id.edit_user_profile_last_name_et);
        current_address_et = view.findViewById(R.id.edit_user_profile_current_address_et);
        weight_et = view.findViewById(R.id.edit_user_profile_weight_et);
        work_as_et = view.findViewById(R.id.edit_user_profile_work_as_et);


        blood_group_tv = view.findViewById(R.id.edit_profile_blood_group_plus);
        date_of_birth_tv = view.findViewById(R.id.edit_profile_date_of_birth_tv);


        blood_group_picker_dialog=new Dialog(getActivity());
        blood_group_picker_dialog.setContentView(R.layout.blood_group_picker_layout);

        radioGroup = blood_group_picker_dialog.findViewById(R.id.blood_radioGroup);

        gender_radioGroup = view.findViewById(R.id.edit_profile_gender_radioGroup);


        blood_group_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                blood_group_picker_dialog.show();

                blood_group_picker_dialog.findViewById(R.id.button_picker_donelButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        blood_group=((RadioButton) blood_group_picker_dialog.findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();
                        blood_group_tv.setText(blood_group);
                        blood_group_picker_dialog.dismiss();
                    }

                });

                blood_group_picker_dialog.findViewById(R.id.button_picker_cancelButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        blood_group_picker_dialog.dismiss();
                    }
                });
            }
        });


        date_of_birth_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });



        progressBar = view.findViewById(R.id.edit_user_profile_progressBar);
        edit_user_profile_layout = view.findViewById(R.id.edit_user_profile_layout);

        uploaded_image = view.findViewById(R.id.edit_user_profile_upload_imageView);
        progressBar.setVisibility(View.VISIBLE);





        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        reference.child("user_details").child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                first_name = snapshot.child("first_name").getValue(String.class);
                last_name = snapshot.child("last_name").getValue(String.class);
                username = snapshot.child("username").getValue(String.class);
                weight = snapshot.child("weight").getValue(String.class);
                work_as = snapshot.child("work_as").getValue(String.class);


                current_address = snapshot.child("current_address").getValue(String.class);
                gender = snapshot.child("gender").getValue(String.class);
                blood_group = snapshot.child("blood_group").getValue(String.class);
                dob = snapshot.child("dob_date").getValue(String.class);
                dob_year = snapshot.child("dob_year").getValue(String.class);
                img_uri_str = snapshot.child("img_uri").getValue(String.class);

                if(!img_uri_str.equals("")){
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.teamwork_symbol);
                    Glide.with(getActivity())
                            .setDefaultRequestOptions(requestOptions)
                            .load(img_uri_str).into(uploaded_image);
                }



                if(gender.equals("Male")){
                    radioButton = view.findViewById(R.id.edit_profile_male_id);
                    radioButton.setChecked(true);
                }
                else if(gender.equals("Female")){
                    radioButton = view.findViewById(R.id.edit_profile_female_id);
                    radioButton.setChecked(true);
                }else{
                    radioButton = view.findViewById(R.id.edit_profile_custom_id);
                    radioButton.setChecked(true);
                }


                username_et.getEditText().setText(username);
                first_name_et.getEditText().setText(first_name);
                last_name_et.getEditText().setText(last_name);
                weight_et.getEditText().setText(weight);
                work_as_et.getEditText().setText(work_as);
                current_address_et.getEditText().setText(current_address);

                blood_group_tv.setText(blood_group);
                date_of_birth_tv.setText(dob);


                progressBar.setVisibility(View.GONE);
                edit_user_profile_layout.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        img_progress_bar = view.findViewById(R.id.edit_user_profile_imageProgressbar);



        edit_img_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent,1024);
            }
        });




        return view;

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.edit_profile_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1024){
            if(resultCode == Activity.RESULT_OK){
                img_uri = data.getData();
                img_progress_bar.setVisibility(View.VISIBLE);
                UploadImageToFirebaseStorage(img_uri);

            }
        }

    }



    
    @SuppressWarnings("rawtypes")
    private void UploadImageToFirebaseStorage(Uri img_uri) {
        //upload image to firebase
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Profile Image of user");
        final StorageReference fileReference = storageReference.child("users/" + firebaseAuth.getCurrentUser().getUid() + "profile.jpg");



        StorageTask uploadTask = fileReference.putFile(img_uri);




        uploadTask.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception {

                if(!task.isSuccessful())
                {
                    throw task.getException();
                }
                return fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                if(task.isSuccessful())
                {
                    Uri downloadUri = task.getResult();
                    img_uri_str=downloadUri.toString();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

                    Map<String,Object> updateValues = new HashMap<>();
                    updateValues.put("img_uri",img_uri_str);

                    reference.child("user_details")
                            .child(firebaseAuth.getCurrentUser().getUid())
                            .updateChildren(updateValues)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @SuppressLint("CheckResult")
                                @Override
                                public void onSuccess(Void aVoid) {

                                    RequestOptions requestOptions = new RequestOptions();
                                    requestOptions.placeholder(R.drawable.teamwork_symbol);
                                    Glide.with(getActivity().getApplicationContext())
                                            .setDefaultRequestOptions(requestOptions)
                                            .load(img_uri_str).into(uploaded_image);
                                    img_progress_bar.setVisibility(View.INVISIBLE);

                                }
                            });


                }

            }
        });





    }


    private void showDatePicker() {

        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(ondate);
        date.show(getFragmentManager(), "Date Picker");
    }


    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

        @SuppressLint("SetTextI18n")
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, monthOfYear);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            dob = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

            dob_year = String.valueOf(year);

            date_of_birth_tv.setText(dob);


        }
    };



    public static class DatePickerFragment extends DialogFragment {
        DatePickerDialog.OnDateSetListener ondateSet;
        private int year, month, day;

        public DatePickerFragment() {}

        public void setCallBack(DatePickerDialog.OnDateSetListener ondate) {
            ondateSet = ondate;
        }

        @SuppressLint("NewApi")
        @Override
        public void setArguments(Bundle args) {
            super.setArguments(args);
            year = args.getInt("year");
            month = args.getInt("month");
            day = args.getInt("day");
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new DatePickerDialog(getActivity(), ondateSet, year, month, day);
        }
    }


    private boolean isValidProfile() {

        boolean isValid = false;

        boolean isValidUsername;
        boolean isValidFirst_Name;
        boolean isValidLast_Name;
        boolean isValidCurrent_Address;
        boolean isValidWeight;
        boolean isValidWork_As;

        ValidateEditUserProfile validateEditUserProfile = new ValidateEditUserProfile();

        toUpdateFirst_Name = first_name_et.getEditText().getText().toString().trim();
        toUpdateUsername = username_et.getEditText().getText().toString().trim();
        toUpdateLast_Name = last_name_et.getEditText().getText().toString().trim();
        toUpdateCurrent_Address = current_address_et.getEditText().getText().toString().trim();
        toUpdateWeight = weight_et.getEditText().getText().toString();

        toUpdateWork_As = work_as_et.getEditText().getText().toString().trim();




        if(validateEditUserProfile.validFirstName(first_name_et,toUpdateFirst_Name)){
            isValidFirst_Name=true;
        }else{
            isValidFirst_Name=false;
        }

        if(validateEditUserProfile.isValidUserName(username_et,toUpdateUsername)){
            isValidUsername = true;
        }else{
            isValidUsername = false;
        }

        if(validateEditUserProfile.validLastName(last_name_et,toUpdateLast_Name)){
            isValidLast_Name = true;
        }else{
            isValidLast_Name =false;
        }

        if(validateEditUserProfile.validCurrentAddress(current_address_et,toUpdateCurrent_Address)){
            isValidCurrent_Address = true;

        }else{
            isValidCurrent_Address =false;
        }

        if(validateEditUserProfile.validWeight(weight_et,toUpdateWeight)){
            isValidWeight=true;
        }else{
            isValidWeight=false;
        }

        if(validateEditUserProfile.validWorkAs(work_as_et,toUpdateWork_As)){
            isValidWork_As=true;
        }else{
            isValidWork_As=false;
        }


        if(isValidFirst_Name && isValidUsername && isValidLast_Name && isValidCurrent_Address && isValidWeight && isValidWork_As){
            isValid = true;
        }else {
            isValid=false;
        }

        return isValid;
    }


    private void updateProfile() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");


        gender= ((RadioButton)view.findViewById(gender_radioGroup.getCheckedRadioButtonId())).getText().toString();






        Map<String,Object> updateValues = new HashMap<>();


        updateValues.put("username",toUpdateUsername);
        updateValues.put("blood_group",blood_group);
        updateValues.put("current_address",toUpdateCurrent_Address);
        updateValues.put("dob_date",dob);
        updateValues.put("dob_year",dob_year);
        updateValues.put("first_name",toUpdateFirst_Name);
        updateValues.put("last_name",toUpdateLast_Name);
        updateValues.put("gender",gender);
        updateValues.put("img_uri",img_uri_str);
        updateValues.put("weight",toUpdateWeight);
        updateValues.put("work_as",toUpdateWork_As);



        reference.child("user_details")
                .child(firebaseAuth.getCurrentUser().getUid())
                .updateChildren(updateValues)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        loading_dialog.dismiss();
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        getActivity().finish();


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Exception e) {

                loading_dialog.dismiss();
                error_content_tv.setText(e.getMessage());
                error_dialog.show();

            }
        });

    }




}

