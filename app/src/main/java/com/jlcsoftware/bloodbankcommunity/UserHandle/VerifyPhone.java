package com.jlcsoftware.bloodbankcommunity.UserHandle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jlcsoftware.bloodbankcommunity.R;
import com.jlcsoftware.bloodbankcommunity.UserDetails.User_Details;
import com.jlcsoftware.bloodbankcommunity.ValidateUserDetails.KeyBoardServices;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class VerifyPhone extends AppCompatActivity {


    private static final String TAG = "phoneAuth";

    private EditText phone_et,otp_et;

    private CountryCodePicker countryCodePicker;

    private TextView resend_tv,error_content_tv;

    private FirebaseAuth firebaseAuth;

    private MaterialButton verify_next_btn,login_email_next_verify_now_btn,error_btn;


    private String phoneNumber, ccp , OTP;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;


    private ProgressBar verify_progress_bar,verify_email_progressBar;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


    private FirebaseUser user;
    Task<Void> usertask;

    private boolean userEmailVerified;

    private LinearLayout phone_verification_page , email_verification_page,phone_verify_otp_layout;

    private TextView email_verification_error_tv;

    private Dialog error_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        phone_et=findViewById(R.id.login_Phone_verify_et);
        countryCodePicker=findViewById(R.id.login_verify_ccp);

        otp_et=findViewById(R.id.login_otp_verify_et);
        resend_tv=findViewById(R.id.login_resend_verify_otp_tv);


        firebaseAuth = FirebaseAuth.getInstance();

        verify_next_btn=findViewById(R.id.login_next_verify_btn_id);

        verify_progress_bar=findViewById(R.id.phone_verify_progressBar);



        error_dialog=new Dialog(VerifyPhone.this);
        error_dialog.setContentView(R.layout.error_dialog);
        error_content_tv=error_dialog.findViewById(R.id.error_content_tv);
        error_btn=error_dialog.findViewById(R.id.error_button_id);


        phone_verification_page = findViewById(R.id.phone_verification_page);
        email_verification_page = findViewById(R.id.email_verification_page);

        phone_verify_otp_layout = findViewById(R.id.phone_verification_otp_or_resend_page);


        email_verification_error_tv = findViewById(R.id.email_verification_error_tv);

        if(firebaseAuth.getCurrentUser()!=null){

            usertask = firebaseAuth.getCurrentUser().reload();
            usertask.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    user = firebaseAuth.getCurrentUser();
                    userEmailVerified = user.isEmailVerified();
                }
            });


        }






        userEmailVerified = true;


        if(userEmailVerified){
            phone_verification_page.setVisibility(View.VISIBLE);
            email_verification_page.setVisibility(View.GONE);
        }else{
            email_verification_page.setVisibility(View.VISIBLE);
            phone_verification_page.setVisibility(View.GONE);
        }



        verify_next_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                KeyBoardServices keyBoardServices = new KeyBoardServices();
                keyBoardServices.hideKeyboardMethod(v,VerifyPhone .this);
                if(verify_next_btn.getText().toString().equals("Next")){
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

                        verify_progress_bar.setVisibility(View.VISIBLE);
                        verify_next_btn.setText("");
                        verify_next_btn.setEnabled(false);
                        sendOtp();

                    }

                }else{
                    OTP = otp_et.getText().toString();
                    if(TextUtils.isEmpty(OTP) || OTP.length()!=6){
                        otp_et.setBackground(getDrawable(R.drawable.error_edittext_background));
                        otp_et.setError("Enter Valid OTP");
                    }else{

                        verify_progress_bar.setVisibility(View.VISIBLE);
                        verify_next_btn.setText("");
                        otp_et.setBackground(getDrawable(R.drawable.simple_edit_input_background));
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,OTP);
                        signInWithPhoneAuthCredential(credential);
                    }
                }
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


            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

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
                verify_progress_bar.setVisibility(View.GONE);
                phone_verify_otp_layout.setVisibility(View.VISIBLE);
                resend_tv.setVisibility(View.VISIBLE);
                otp_et.setVisibility(View.VISIBLE);
                Toast.makeText(VerifyPhone.this, "hiiii", Toast.LENGTH_SHORT).show();
                verify_next_btn.setEnabled(true);
                verify_next_btn.setText("Verify");


                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // ...
            }
        };






        login_email_next_verify_now_btn=findViewById(R.id.login_email_next_verify_now_btn_id);
        verify_email_progressBar=findViewById(R.id.login_email_verify_progressBar);




        login_email_next_verify_now_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                verify_email_progressBar.setVisibility(View.VISIBLE);

                login_email_next_verify_now_btn.setText("");

                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(Void aVoid) {

                        verify_email_progressBar.setVisibility(View.GONE);
                        login_email_next_verify_now_btn.setEnabled(true);
                        login_email_next_verify_now_btn.setText("Verify Now");


                        email_verification_error_tv.setText("Email Verification link has been sent to your G-mail go and check and verify it");
                        email_verification_error_tv.setTextColor(getColor(R.color.robert));
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d("error7","Error! "+e.getMessage());

                    }
                });


            }
        });





        resend_tv.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(phone_et.getText().toString()) || phone_et.getText().toString().length()!=10){

                    phone_et.setBackground(getDrawable(R.drawable.error_edittext_background));
                    phone_et.setError("Please enter a valid phone number");


                }else{

                    phone_et.setBackground(getDrawable(R.drawable.simple_edit_input_background));

                    ccp = countryCodePicker.getSelectedCountryCode();
                    phoneNumber = "+"+countryCodePicker.getSelectedCountryCode()+phone_et.getText().toString();


                    phone_verify_otp_layout.setVisibility(View.GONE);
                    verify_next_btn.setText("");
                    verify_progress_bar.setVisibility(View.VISIBLE);
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





    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        firebaseAuth.getCurrentUser().linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                resend_tv.setVisibility(View.GONE);
                Toast.makeText(VerifyPhone.this, "Phone is verified now", Toast.LENGTH_SHORT).show();



                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                Map<String ,Object> updateValues = new HashMap<>();
                updateValues.put("phone_number",firebaseAuth.getCurrentUser().getPhoneNumber());
                updateValues.put("country_code",ccp);
                updateValues.put("country_name",countryCodePicker.getSelectedCountryName());


                reference.child("user_details").child(firebaseAuth.getCurrentUser().getUid()).updateChildren(updateValues)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(VerifyPhone.this,User_Details.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        verify_next_btn.setText("Next");
                        resend_tv.setVisibility(View.VISIBLE);
                        verify_progress_bar.setVisibility(View.GONE);
                        error_content_tv.setText(e.getMessage());
                        error_dialog.show();
                    }
                });

                // send to dashboard.
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


                verify_next_btn.setText("Next");
                resend_tv.setVisibility(View.VISIBLE);
                verify_progress_bar.setVisibility(View.GONE);
                error_content_tv.setText(e.getMessage());
                error_dialog.show();


            }
        });

        Toast.makeText(this, "OTP fetched", Toast.LENGTH_SHORT).show();

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





    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }




    @Override
    protected void onStart() {
        super.onStart();
        if(userEmailVerified){
            phone_verification_page.setVisibility(View.VISIBLE);
            email_verification_page.setVisibility(View.GONE);

        }
    }




}