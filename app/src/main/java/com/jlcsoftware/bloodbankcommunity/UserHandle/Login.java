package com.jlcsoftware.bloodbankcommunity.UserHandle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jlcsoftware.bloodbankcommunity.R;
import com.jlcsoftware.bloodbankcommunity.UserDetails.User_Details;
import com.jlcsoftware.bloodbankcommunity.UserProfile.CurrentUserProfile;
import com.jlcsoftware.bloodbankcommunity.ValidateUserDetails.ValidateUserCredentials;
import com.jlcsoftware.bloodbankcommunity.ValidateUserDetails.ValidateUserDetails;

public class Login extends AppCompatActivity {


    private LinearLayout linearLayout_email_login_page,linearLayout_phone_login_page;
    private TextView login_with_email_or_phone_tv,login_resend_otp_tv, forgot_your_password_tv ;

    private MaterialButton go_to_register_btn,login_phone_next_btn,login_email_next_btn,error_btn;

    private EditText login_otp_et,login_phone_et,login_email_et,login_password_et;


    private ProgressBar login_email_progress_bar;

    private Dialog error_dialog;
    private TextView error_content_tv;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        go_to_register_btn=findViewById(R.id.go_to_register_id);

        go_to_register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,Register.class));
                finish();
            }
        });



        linearLayout_email_login_page = findViewById(R.id.email_login_page);
        linearLayout_phone_login_page = findViewById(R.id.phone_login_page);

        login_with_email_or_phone_tv=findViewById(R.id.login_with_phone_or_email);

        login_otp_et=findViewById(R.id.login_otp_et);
        login_resend_otp_tv=findViewById(R.id.login_resend_otp_tv);
        login_email_et=findViewById(R.id.login_Username_or_email_et);

        login_phone_et=findViewById(R.id.login_Phone_et);
        login_password_et=findViewById(R.id.login_Password_et);



        error_dialog=new Dialog(Login.this);
        error_dialog.setContentView(R.layout.error_dialog);
        error_content_tv=error_dialog.findViewById(R.id.error_content_tv);
        error_btn=error_dialog.findViewById(R.id.error_button_id);


        firebaseAuth = FirebaseAuth.getInstance();

        login_with_email_or_phone_tv=findViewById(R.id.login_with_phone_or_email);

        forgot_your_password_tv = findViewById(R.id.login_forgot_your_password_tv);

        login_with_email_or_phone_tv.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if (linearLayout_email_login_page.getVisibility()==View.VISIBLE){

                    linearLayout_email_login_page.setVisibility(View.GONE);
                    linearLayout_phone_login_page.setVisibility(View.VISIBLE);
                    login_with_email_or_phone_tv.setText("Or Login with Email");
                    forgot_your_password_tv.setVisibility(View.GONE);
                    login_email_et.setText("");
                    login_password_et.setText("");


                }else{

                    linearLayout_email_login_page.setVisibility(View.VISIBLE);
                    forgot_your_password_tv.setVisibility(View.VISIBLE);
                    linearLayout_phone_login_page.setVisibility(View.GONE);
                    login_with_email_or_phone_tv.setText("Or Login with Phone");
                    login_otp_et.setVisibility(View.GONE);
                    login_resend_otp_tv.setVisibility(View.GONE);
                    login_otp_et.setText("");
                    login_phone_et.setText("");
                }
            }
        });





        login_email_progress_bar = findViewById(R.id.login_email_progressBar);


        login_email_next_btn = findViewById(R.id.login_email_next_btn_id);
        login_email_next_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {

                ValidateUserCredentials validateUserCredentials = new ValidateUserCredentials();
                String username_or_email = login_email_et.getText().toString().trim();
                String pass=login_password_et.getText().toString().trim();


                boolean isValidUsernameOrEmail;
                boolean isValidPassword;
                if(validateUserCredentials.isValidUsernameOrEmail(login_email_et,username_or_email)){

                    isValidUsernameOrEmail=true;
                    drawableSetEditTextBackgroundNormal(login_email_et);

                }else{
                    isValidUsernameOrEmail=false;
                    drawableSetEditTextBackgroundError(login_email_et);
                }


                if(validateUserCredentials.isValidPassword(login_password_et,pass)){
                    isValidPassword=true;
                    drawableSetEditTextBackgroundNormal(login_password_et);
                }else {
                    isValidPassword=false;
                    drawableSetEditTextBackgroundError(login_password_et);
                }


                if(isValidPassword && isValidUsernameOrEmail){
                    if(username_or_email.contains("@")){
                        login_email_progress_bar.setVisibility(View.VISIBLE);
                        login_email_next_btn.setText("");
                        login_email_next_btn.setBackgroundColor(getColor(R.color.disable_color));
                        login_email_next_btn.setEnabled(false);
                        signInWithEmailAndPassword(username_or_email,pass);
                    }else{
                        Query query = FirebaseDatabase.getInstance().getReference("users").child("user_details");

                        query.orderByChild("username").equalTo(username_or_email).addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){


                                    login_email_progress_bar.setVisibility(View.VISIBLE);
                                    login_email_next_btn.setText("");
                                    login_email_next_btn.setBackgroundColor(getColor(R.color.disable_color));
                                    login_email_next_btn.setEnabled(false);

                                    if(!TextUtils.isEmpty(snapshot.child("email").getValue(String.class))){

                                        signInWithEmailAndPassword(snapshot.child("email").getValue(String.class),pass);
                                    }

                                    Toast.makeText(Login.this, ""+snapshot.child("email").getValue(String.class), Toast.LENGTH_SHORT).show();


                                }else{
                                    login_email_progress_bar.setVisibility(View.GONE);
                                    login_email_next_btn.setText("Next");
                                    login_email_next_btn.setBackgroundColor(getColor(R.color.robert));
                                    login_email_next_btn.setEnabled(true);
                                    error_content_tv.setText("This user is not belongs to our community, Please go for registration");
                                    error_dialog.show();
                                }
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                                error_content_tv.setText(error.getMessage());
                                error_dialog.show();

                                login_email_progress_bar.setVisibility(View.GONE);
                                login_email_next_btn.setText("Next");
                                login_email_next_btn.setBackgroundColor(getColor(R.color.robert));
                                login_email_next_btn.setEnabled(true);



                            }
                        });

                    }
                }


            }
        });


        error_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                error_dialog.dismiss();
            }
        });


    }

    private void signInWithEmailAndPassword(String username_or_email, String pass) {


        firebaseAuth.signInWithEmailAndPassword(username_or_email,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {

            @Override
            public void onSuccess(AuthResult authResult) {
                checkUserProfile();
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                error_content_tv.setText(e.getMessage());
                error_dialog.show();

                login_email_progress_bar.setVisibility(View.GONE);
                login_email_next_btn.setText("Next");
                login_email_next_btn.setBackgroundColor(getColor(R.color.robert));
                login_email_next_btn.setEnabled(true);

            }
        });

    }




    private void checkUserProfile() {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.child("user_details").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            if(snapshot.child("phone_number").getValue(String.class).equals("")){

                                startActivity(new Intent(Login.this, VerifyPhone.class));
                                finish();

                            }else if(snapshot.child("blood_group").getValue(String.class).equals("")){

                                startActivity(new Intent(Login.this, User_Details.class));
                                finish();
                            }else{

                                startActivity(new Intent(Login.this, CurrentUserProfile.class));
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(Login.this, "Error! "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private void drawableSetEditTextBackgroundError(EditText editText){

        editText.setBackground(getDrawable(R.drawable.error_edittext_background));
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private void drawableSetEditTextBackgroundNormal(EditText editText){
        editText.setBackground(getDrawable(R.drawable.simple_edit_input_background));
    }


}