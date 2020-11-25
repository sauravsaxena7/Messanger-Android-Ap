package com.jlcsoftware.bloodbankcommunity.UserDetails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.jlcsoftware.bloodbankcommunity.R;
import com.jlcsoftware.bloodbankcommunity.ValidateUserDetails.ValidateUserDetails;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.DateFormat;
import java.util.Calendar;

public class User_Details extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{



    private TextView blood_group_tv,user_details_date_of_birth_tv;

    private Dialog blood_group_picker_dialog;


    private RadioGroup radioGroup,gender_radio_button;


    private MaterialButton save_user_details_btn_id;

    private String dob_year,dob;

    private MaterialEditText first_name_et,last_name_et,current_address_et,weight_et,work_as_et;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__details);



        blood_group_tv=findViewById(R.id.user_details_blood_group_plus);

        blood_group_picker_dialog=new Dialog(User_Details.this);
        blood_group_picker_dialog.setContentView(R.layout.blood_group_picker_layout);


        radioGroup = blood_group_picker_dialog.findViewById(R.id.blood_radioGroup);




        first_name_et = findViewById(R.id.user_details_first_name_et);
        last_name_et = findViewById(R.id.user_details_last_name_et);
        current_address_et= findViewById(R.id.user_details_current_address_et);
        work_as_et=findViewById(R.id.user_details_work_as_et);
        weight_et = findViewById(R.id.user_details_blood_weight_et);






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


        ValidateUserDetails validateUserDetails = new ValidateUserDetails();


        save_user_details_btn_id = findViewById(R.id.save_user_details_btn_id);
        save_user_details_btn_id.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {


                String first_name = first_name_et.getText().toString().trim();
                String last_name = last_name_et.getText().toString().trim();
                String current_address = current_address_et.getText().toString().trim();
                String weight = weight_et.getText().toString().trim();
                String work_as = work_as_et.getText().toString().trim();



                boolean isValidFirstName=false;
                boolean isValidLastName=false;
                boolean isValidCurrentAddress = false;
                boolean isValidWorkAs=false;
                boolean isValidWeight=false ;

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
                    isValidBB=false;
                    blood_group_tv.setBackground(getDrawable(R.drawable.error_edittext_background));
                    Toast.makeText(User_Details.this, "Choose blood group", Toast.LENGTH_SHORT).show();
                }else{
                    blood_group_tv.setBackground(getDrawable(R.drawable.simple_edit_input_background));
                    isValidBB = true;
                }

                if(user_details_date_of_birth_tv.getText().toString().trim().equals("Date Of Birth.")){
                    isValidDOB=false;
                    user_details_date_of_birth_tv.setBackground(getDrawable(R.drawable.error_edittext_background));
                    Toast.makeText(User_Details.this, "Choose Date of Birth", Toast.LENGTH_SHORT).show();
                }else{

                    user_details_date_of_birth_tv.setBackground(getDrawable(R.drawable.simple_edit_input_background));
                    isValidDOB = true;
                }



                if(isValidCurrentAddress && isValidFirstName && isValidWeight && isValidWorkAs && isValidLastName && isValidBB && isValidDOB){

                    blood_group_tv.setBackground(getDrawable(R.drawable.simple_edit_input_background));
                    user_details_date_of_birth_tv.setBackground(getDrawable(R.drawable.simple_edit_input_background));
                    Toast.makeText(User_Details.this, "okk", Toast.LENGTH_SHORT).show();
                }


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
}