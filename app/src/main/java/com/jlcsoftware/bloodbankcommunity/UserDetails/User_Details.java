package com.jlcsoftware.bloodbankcommunity.UserDetails;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import de.hdodenhof.circleimageview.CircleImageView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
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
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;


import com.jlcsoftware.bloodbankcommunity.MainActivity;
import com.jlcsoftware.bloodbankcommunity.R;
import com.jlcsoftware.bloodbankcommunity.ValidateUserDetails.KeyBoardServices;
import com.jlcsoftware.bloodbankcommunity.ValidateUserDetails.ValidateUserDetails;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class User_Details extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    //saurav suman


    private TextView blood_group_tv,user_details_date_of_birth_tv;

    private Dialog blood_group_picker_dialog;

    private RadioGroup radioGroup,gender_radio_group;

    public MaterialButton save_user_details_btn_id,error_btn;

    private String dob_year,dob;

    private MaterialEditText first_name_et,last_name_et,current_address_et,weight_et,work_as_et;



    private ImageView browse_img;
    private CircleImageView user_upload_img;

    private Uri imgUri;

    private Bitmap bitmap;

    private ProgressBar imgProgressBar,btn_progressbar;

    private FirebaseAuth firebaseAuth;


    private String img_uri = "";

    private Dialog error_dialog;
    private TextView error_content_tv;


    private StorageTask uploadTask;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__details);



        blood_group_tv=findViewById(R.id.user_details_blood_group_plus);

        blood_group_picker_dialog=new Dialog(User_Details.this);
        blood_group_picker_dialog.setContentView(R.layout.blood_group_picker_layout);


        radioGroup = blood_group_picker_dialog.findViewById(R.id.blood_radioGroup);

        gender_radio_group = findViewById(R.id.user_details_gender_radioGroup);

        btn_progressbar = findViewById(R.id.user_details_progressBar);




        first_name_et = findViewById(R.id.user_details_first_name_et);
        last_name_et = findViewById(R.id.user_details_last_name_et);
        current_address_et= findViewById(R.id.user_details_current_address_et);
        work_as_et=findViewById(R.id.user_details_work_as_et);
        weight_et = findViewById(R.id.user_details_blood_weight_et);




        firebaseAuth = FirebaseAuth.getInstance();


        error_dialog = new Dialog(User_Details.this);
        error_dialog.setContentView(R.layout.error_dialog);
        error_content_tv=error_dialog.findViewById(R.id.error_content_tv);
        error_btn=error_dialog.findViewById(R.id.error_button_id);


        blood_group_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blood_group_picker_dialog.show();


                blood_group_picker_dialog.findViewById(R.id.button_picker_donelButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String blood_group__str=((RadioButton) blood_group_picker_dialog.findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();
                        blood_group_tv.setText(blood_group__str);
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






        user_details_date_of_birth_tv=findViewById(R.id.user_details_date_of_birth_tv);

        user_details_date_of_birth_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });




        user_upload_img = findViewById(R.id.user_details_upload_imageView);
        imgProgressBar = findViewById(R.id.user_details_imageProgressbar);



        browse_img = findViewById(R.id.user_details_image_edit_button);



        browse_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Toast.makeText(User_Details.this, "saurav suman", Toast.LENGTH_SHORT).show();
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery,1024);



            }
        });




        ValidateUserDetails validateUserDetails = new ValidateUserDetails();



        save_user_details_btn_id = findViewById(R.id.save_user_details_btn_id);
        save_user_details_btn_id.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {


                KeyBoardServices keyBoardServices = new KeyBoardServices();
                keyBoardServices.hideKeyboardMethod(view, User_Details.this);

                String first_name = first_name_et.getText().toString().trim();
                String last_name = last_name_et.getText().toString().trim();
                String current_address = current_address_et.getText().toString().trim();
                String weight = weight_et.getText().toString().trim();
                String work_as = work_as_et.getText().toString().trim();
                String blood_group = blood_group_tv.getText().toString();
                String gender_str=((RadioButton) findViewById(gender_radio_group.getCheckedRadioButtonId())).getText().toString();





                boolean isValidFirstName=false;
                boolean isValidLastName=false;
                boolean isValidCurrentAddress = false;
                boolean isValidWorkAs=false;
                boolean isValidWeight=false;




                if(validateUserDetails.validFirstName(first_name_et,first_name)){
                    isValidFirstName=true;
                }

                if(validateUserDetails.validLastName(last_name_et,last_name)){
                    isValidLastName=true;
                }

                if(validateUserDetails.validCurrentAddress(current_address_et,current_address)){
                    isValidCurrentAddress=true;
                }

                if(validateUserDetails.validWeight(weight_et,weight)){
                    isValidWeight=true;
                }

                if(validateUserDetails.validWorkAs(work_as_et,work_as)){
                    isValidWorkAs=true;
                }




                boolean isValidBB = false;
                boolean isValidDOB = false;


                if(blood_group_tv.getText().toString().trim().equals("Blood Group +")){

                    blood_group_tv.setBackground(getDrawable(R.drawable.error_edittext_background));
                    Toast.makeText(User_Details.this, "Choose blood group", Toast.LENGTH_SHORT).show();
                }else{
                    blood_group_tv.setBackground(getDrawable(R.drawable.simple_edit_input_background));
                    isValidBB = true;
                }



                if(user_details_date_of_birth_tv.getText().toString().trim().equals("Date Of Birth.")){
                    user_details_date_of_birth_tv.setBackground(getDrawable(R.drawable.error_edittext_background));
                    Toast.makeText(User_Details.this, "Choose Date of Birth", Toast.LENGTH_SHORT).show();
                }else{

                    user_details_date_of_birth_tv.setBackground(getDrawable(R.drawable.simple_edit_input_background));
                    isValidDOB = true;
                }






                if(isValidCurrentAddress && isValidFirstName && isValidWeight && isValidWorkAs && isValidLastName && isValidBB && isValidDOB){

                    btn_progressbar.setVisibility(View.VISIBLE);
                    save_user_details_btn_id.setEnabled(false);
                    save_user_details_btn_id.setText("");


                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

                    Map<String ,Object> updateValues = new HashMap<>();



                    updateValues.put("blood_group",blood_group);
                    updateValues.put("current_address",current_address);
                    updateValues.put("dob_date",dob);
                    updateValues.put("dob_year",dob_year);
                    updateValues.put("first_name",first_name);
                    updateValues.put("last_name",last_name);
                    updateValues.put("gender",gender_str);
                    updateValues.put("img_uri",img_uri);
                    updateValues.put("weight",weight);
                    updateValues.put("work_as",work_as);

                    reference.child("user_details")
                            .child(firebaseAuth.getCurrentUser().getUid())
                            .updateChildren(updateValues)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            startActivity(new Intent(User_Details.this, MainActivity.class));
                            finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            error_content_tv.setText(e.getMessage());
                            error_dialog.show();
                            btn_progressbar.setVisibility(View.GONE);
                            save_user_details_btn_id.setEnabled(true);
                            save_user_details_btn_id.setText("Save Details");
                        }
                    });





                }


            }
        });


        error_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error_dialog.dismiss();
            }
        });



    }





    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        dob = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        dob_year = String.valueOf(year);
        user_details_date_of_birth_tv.setText(dob);


    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1024 && resultCode == RESULT_OK) {
            assert data != null;
            imgUri = data.getData();

            try {
                imgProgressBar.setVisibility(View.VISIBLE);
                browse_img.setEnabled(false);
                uploadImageToFirebase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }






    }



    @SuppressWarnings("unchecked")
    private void uploadImageToFirebase() throws IOException {


        //upload image to firebase
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Profile Image of user");
        final StorageReference fileReference = storageReference.child("users/" + firebaseAuth.getCurrentUser().getUid() + "profile.jpg");



        //uploading the image
        UploadTask uploadTask = fileReference.putFile(imgUri);


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
            @SuppressLint("CheckResult")
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                if(task.isSuccessful())
                {
                    Uri downloadUri = task.getResult();



                    img_uri=downloadUri.toString();

                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.teamwork_symbol);
                    Glide.with(User_Details.this)
                            .setDefaultRequestOptions(requestOptions)
                            .load(img_uri).into(user_upload_img);


                    browse_img.setEnabled(true);
                    imgProgressBar.setVisibility(View.INVISIBLE);
                }else {

                    imgProgressBar.setVisibility(View.INVISIBLE);
                    error_content_tv.setText(task.getException().getMessage());
                    browse_img.setEnabled(true);
                    error_dialog.show();

                }

            }
        });


    }



}