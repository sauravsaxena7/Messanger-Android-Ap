package com.jlcsoftware.bloodbankcommunity.UserHandle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jlcsoftware.bloodbankcommunity.MainActivity;
import com.jlcsoftware.bloodbankcommunity.Models.Model_user_details;
import com.jlcsoftware.bloodbankcommunity.R;
import com.jlcsoftware.bloodbankcommunity.UserDetails.User_Details;
import com.jlcsoftware.bloodbankcommunity.ValidateUserDetails.InternetCheck;
import com.jlcsoftware.bloodbankcommunity.ValidateUserDetails.KeyBoardServices;
import com.jlcsoftware.bloodbankcommunity.ValidateUserDetails.ValidateUserCredentials;
import com.jlcsoftware.bloodbankcommunity.ValidateUserDetails.ValidateUserDetails;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {


    private static final String TAG = "saurav";
    private LinearLayout linearLayout_email_login_page,linearLayout_phone_login_page,login_otp_and_resend_page;
    private TextView login_with_email_or_phone_tv,login_resend_otp_tv, forgot_your_password_tv ;

    private MaterialButton go_to_register_btn,login_phone_next_btn,login_email_next_btn,error_btn;

    private EditText login_otp_et,login_phone_et,login_email_et,login_password_et;


    private ProgressBar login_email_progress_bar,login_phone_progressBar;

    private Dialog error_dialog;
    private TextView error_content_tv;



    private FirebaseAuth firebaseAuth;

    private CountryCodePicker countryCodePicker;
    private EditText phone_et,otp_et;

    private TextView resend_tv;

    private MaterialButton login_phone_btn;

    private String ccp,phoneNumber,OTP,mVerificationId;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @SuppressLint("CutPasteId")
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

                KeyBoardServices keyBoardServices = new KeyBoardServices();
                keyBoardServices.hideKeyboardMethod(v,Login.this);

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

                        go_to_register_btn.setEnabled(false);
                        login_email_progress_bar.setVisibility(View.VISIBLE);
                        login_email_next_btn.setText("");
                        login_email_next_btn.setBackgroundColor(getColor(R.color.disable_color));
                        login_email_next_btn.setEnabled(false);
                        Query query = FirebaseDatabase.getInstance().getReference("users").child("user_details");
                        query.orderByChild("username").equalTo(username_or_email).addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){


                                    for(DataSnapshot ds : snapshot.getChildren()){
                                        Model_user_details model_user_details = ds.getValue(Model_user_details.class);
                                        if(model_user_details.getEmail()!=null){
                                            signInWithEmailAndPassword(model_user_details.getEmail(),pass);
                                        }


                                    }

                                }else{
                                    go_to_register_btn.setEnabled(true);
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

                                go_to_register_btn.setEnabled(true);
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




        phone_et = findViewById(R.id.login_Phone_et);
        countryCodePicker = findViewById(R.id.login_ccp);
        otp_et = findViewById(R.id.login_otp_et);
        resend_tv = findViewById(R.id.login_resend_otp_tv);
        login_phone_progressBar = findViewById(R.id.login_phone_progressBar);

        login_otp_and_resend_page = findViewById(R.id.login_otp_layout);


        login_phone_btn = findViewById(R.id.login_phone_next_btn_id);

        login_phone_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
            @Override
            public void onClick(View v) {

                KeyBoardServices keyBoardServices = new KeyBoardServices();
                keyBoardServices.hideKeyboardMethod(v,Login.this);


                new InternetCheck(internet -> {


                    if(internet.booleanValue()){


                        if(login_phone_btn.getText().toString().equals("Next")){
                            resend_tv.setVisibility(View.GONE);
                            otp_et.setVisibility(View.GONE);

                            otp_et.setText("");
                            if(TextUtils.isEmpty(phone_et.getText().toString()) || phone_et.getText().toString().length()!=10){

                                phone_et.setBackground(getDrawable(R.drawable.error_edittext_background));
                                phone_et.setError("Please enter a valid phone number");


                            }else{

                                phone_et.setBackground(getDrawable(R.drawable.simple_edit_input_background));

                                ccp = countryCodePicker.getSelectedCountryCode();
                                phoneNumber = "+"+countryCodePicker.getSelectedCountryCode()+phone_et.getText().toString();

                                go_to_register_btn.setEnabled(false);
                                login_phone_progressBar.setVisibility(View.VISIBLE);
                                login_phone_btn.setText("");
                                login_phone_btn.setEnabled(false);
                                sendOtp();

                            }

                        }else{

                            OTP = otp_et.getText().toString();
                            if(TextUtils.isEmpty(OTP) || OTP.length()!=6){
                                otp_et.setBackground(getDrawable(R.drawable.error_edittext_background));
                                otp_et.setError("Enter Valid OTP");
                            }else{

                                login_phone_progressBar.setVisibility(View.VISIBLE);
                                login_phone_btn.setText("");
                                otp_et.setBackground(getDrawable(R.drawable.simple_edit_input_background));
                                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,OTP);
                                signInWithPhoneAuthCredential(credential);
                            }
                        }
                    }else{

                        error_content_tv.setText("No Internet Connection");
                        error_dialog.show();

                    }
                });

            }
        });



        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }



            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);

                go_to_register_btn.setEnabled(true);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                go_to_register_btn.setEnabled(true);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }



            @SuppressLint("SetTextI18n")
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);
                login_phone_progressBar.setVisibility(View.GONE);
                login_otp_and_resend_page.setVisibility(View.VISIBLE);
                resend_tv.setVisibility(View.VISIBLE);
                otp_et.setVisibility(View.VISIBLE);
                Toast.makeText(Login.this, "hiiii", Toast.LENGTH_SHORT).show();
                login_phone_btn.setEnabled(true);
                login_phone_btn.setText("Verify");


                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // ...
            }
        };


        resend_tv.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(phone_et.getText().toString()) || phone_et.getText().toString().length()!=10){

                    phone_et.setBackground(getDrawable(R.drawable.error_edittext_background));
                    phone_et.setError("Please enter a valid phone number");


                }else{

                    phone_et.setBackground(getDrawable(R.drawable.simple_edit_input_background));

                    go_to_register_btn.setEnabled(false);
                    ccp = countryCodePicker.getSelectedCountryCode();
                    phoneNumber = "+"+countryCodePicker.getSelectedCountryCode()+phone_et.getText().toString();


                    login_otp_and_resend_page.setVisibility(View.GONE);
                    login_phone_btn.setText("");
                    login_phone_progressBar.setVisibility(View.VISIBLE);
                    resendVerificationCode(phoneNumber,mResendToken);

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

                go_to_register_btn.setEnabled(true);
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

                                Toast.makeText(Login.this, "user login successfully!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Login.this, VerifyPhone.class));
                                finish();

                            }else if(snapshot.child("blood_group").getValue(String.class).equals("")){


                                Toast.makeText(Login.this, "user login successfully!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Login.this, User_Details.class));
                                finish();
                            }else{

                                Toast.makeText(Login.this, "user login successfully!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Login.this, MainActivity.class));
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


    private void sendOtp() {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken mResendToken) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(mResendToken)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        firebaseAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                checkUserProfile();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                error_content_tv.setText(e.getMessage());
                error_dialog.show();
            }
        });

    }




}