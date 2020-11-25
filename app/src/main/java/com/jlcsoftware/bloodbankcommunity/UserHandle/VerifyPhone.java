package com.jlcsoftware.bloodbankcommunity.UserHandle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
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

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.jlcsoftware.bloodbankcommunity.R;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.concurrent.TimeUnit;


public class VerifyPhone extends AppCompatActivity {


    private static final String TAG = "phoneAuth";

    private EditText phone_et,otp_et;

    private CountryCodePicker countryCodePicker;

    private TextView resend_tv;

    private FirebaseAuth firebaseAuth;

    private Button verify_next_btn;


    private String phoneNumber, ccp,OTP,verificationId;

    PhoneAuthProvider.ForceResendingToken token;


    private ProgressBar verify_progress_bar;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private LinearLayout phone_verify_page_layout;


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

        phone_verify_page_layout = findViewById(R.id.phone_verify_login_otp_layout);



        verify_next_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                if(verify_next_btn.getText().toString().equals("Next")){

                    if(TextUtils.isEmpty(phone_et.getText().toString()) || phone_et.getText().toString().length()!=10){

                        phone_et.setBackground(getDrawable(R.drawable.error_edittext_background));
                        phone_et.setError("Please enter a valid phone number");


                    }else{

                        phone_et.setBackground(getDrawable(R.drawable.simple_edit_input_background));

                        phoneNumber = "+"+countryCodePicker.getSelectedCountryCode()+phone_et.getText().toString();

                        verify_progress_bar.setVisibility(View.VISIBLE);
                        verify_next_btn.setText("");
                        verify_next_btn.setEnabled(false);
                        sendOtp();

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

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);
                verify_progress_bar.setVisibility(View.GONE);
                phone_verify_page_layout.setVisibility(View.VISIBLE);
                verify_next_btn.setEnabled(true);
                verify_next_btn.setText("Verify");


                // Save verification ID and resending token so we can use them later
                verificationId = verificationId;
                token = token;

                // ...
            }
        };



    }



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

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
}