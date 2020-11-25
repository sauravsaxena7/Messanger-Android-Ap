package com.jlcsoftware.bloodbankcommunity.UserHandle;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.jlcsoftware.bloodbankcommunity.R;

public class Login extends AppCompatActivity {


    private LinearLayout linearLayout_email_login_page,linearLayout_phone_login_page;
    private TextView login_with_email_or_phone_tv,login_resend_otp_tv, forgot_your_password_tv ;

    private MaterialButton go_to_register_btn,login_phone_next_btn,login_email_next_btn;

    private EditText login_otp_et,login_phone_et,login_email_et,login_password_et;



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




    }
}