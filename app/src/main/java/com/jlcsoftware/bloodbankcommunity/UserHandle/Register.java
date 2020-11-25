package com.jlcsoftware.bloodbankcommunity.UserHandle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jlcsoftware.bloodbankcommunity.Models.User_Details_Models;
import com.jlcsoftware.bloodbankcommunity.R;
import com.jlcsoftware.bloodbankcommunity.ValidateUserDetails.KeyBoardServices;
import com.jlcsoftware.bloodbankcommunity.ValidateUserDetails.ValidateUserCredentials;

public class Register extends AppCompatActivity {


    private MaterialButton go_to_login_btn,register_button,error_btn;
    private EditText username_et,email_et,password_et;
    private FirebaseAuth firebaseAuth;
    private Dialog error_dialog;
    private TextView error_content_tv;
    private ProgressBar progressBar;


    private KeyBoardServices keyBoardServices;
    private ValidateUserCredentials validateUserCredentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username_et=findViewById(R.id.register_Username_et);
        email_et=findViewById(R.id.register_emailAddress_et);
        password_et=findViewById(R.id.register_Password_et);


        go_to_login_btn=findViewById(R.id.go_to_login_id);

        go_to_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this, Login.class));
                finish();
            }
        });


        firebaseAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.register_progressBar);
        error_dialog=new Dialog(Register.this);
        error_dialog.setContentView(R.layout.error_dialog);
        error_content_tv=error_dialog.findViewById(R.id.error_content_tv);
        error_btn=error_dialog.findViewById(R.id.error_button_id);



        validateUserCredentials= new ValidateUserCredentials();
        keyBoardServices = new KeyBoardServices();


        register_button=findViewById(R.id.register_next_btn_id);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                keyBoardServices.hideKeyboardMethod(view,Register.this);

                final String username = username_et.getText().toString();
                final String email = email_et.getText().toString();
                final String password = password_et.getText().toString();

                boolean isValidEmail;
                boolean isValidUsername;
                boolean isValidPass;

                if(validateUserCredentials.isValidUserName(username_et,username.trim())){

                    drawableSetEditTextBackgroundNormal(username_et);
                    isValidUsername=true;

                }else{
                    drawableSetEditTextBackgroundError(username_et);
                    isValidUsername=false;

                }


                if(validateUserCredentials.isValidEmailAddress(email_et,email)){

                    drawableSetEditTextBackgroundNormal(email_et);
                    isValidEmail=true;

                }else{
                    drawableSetEditTextBackgroundError(email_et);
                    isValidEmail=false;

                }

                if(validateUserCredentials.isValidPassword(password_et,password)){

                    drawableSetEditTextBackgroundNormal(password_et);
                    isValidPass=true;

                }else{
                    drawableSetEditTextBackgroundError(password_et);
                    isValidPass=false;

                }

                if(isValidEmail && isValidPass && isValidUsername){

                    progressBar.setVisibility(View.VISIBLE);
                    register_button.setEnabled(false);
                    register_button.setBackgroundColor(getColor(R.color.disable_color));
                    register_button.setText("");
                    go_to_login_btn.setEnabled(false);

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child("user_details");


                    ref.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){

                                register_button.setEnabled(true);
                                register_button.setBackgroundColor(getColor(R.color.robert));
                                register_button.setText("Next");
                                error_content_tv.setText("Username Already Taken");
                                progressBar.setVisibility(View.GONE);
                                error_dialog.show();
                                Log.d("error2","error2");
                                go_to_login_btn.setEnabled(true);

                                Toast.makeText(Register.this, "username is not available", Toast.LENGTH_SHORT).show();
                            }else {


                                Register_User(username,email,password);
                            }
                        }

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                            error_content_tv.setText(error.getMessage());
                            register_button.setEnabled(true);
                            register_button.setBackgroundColor(getResources().getColor(R.color.robert));
                            register_button.setText("Next");
                            progressBar.setVisibility(View.GONE);
                            error_dialog.show();
                            Log.d("error1","error1");
                            go_to_login_btn.setEnabled(true);
                        }
                    });



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


    private void Register_User(final String username, final String email, String password) {


        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {


                User_Details_Models userDetailsModels = new User_Details_Models(username,firebaseAuth.getCurrentUser().getUid(),
                        "","","","",email,
                        "",
                        "","","",
                        "","","","","",
                        "","","","null","NO");



                ref.child("user_details").child(firebaseAuth.getCurrentUser().getUid())
                        .setValue(userDetailsModels).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        FirebaseUser user  = firebaseAuth.getCurrentUser();

                        user.sendEmailVerification()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(Register.this,
                                                "User Created Successfully and Verification email has been sent.",
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent = new  Intent(Register.this, VerifyPhone.class);
                                        error_dialog.dismiss();
                                        startActivity(intent);
                                        finish();


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Log.d("error5","Error! "+e.getMessage());
                            }
                        });





                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        error_content_tv.setText(e.getMessage());
                        register_button.setEnabled(true);
                        register_button.setBackgroundColor(getResources().getColor(R.color.robert));
                        register_button.setText("Next");
                        progressBar.setVisibility(View.GONE);
                        Log.d("error3","error3");
                        error_dialog.show();
                        go_to_login_btn.setEnabled(true);

                    }
                });

            }

        }).addOnFailureListener(new OnFailureListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Exception e) {

                error_content_tv.setText(e.getMessage());
                register_button.setEnabled(true);
                register_button.setBackgroundColor(getResources().getColor(R.color.robert));
                register_button.setText("Next");
                progressBar.setVisibility(View.GONE);
                error_dialog.show();
                Log.d("error4","error4");
                go_to_login_btn.setEnabled(true);


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