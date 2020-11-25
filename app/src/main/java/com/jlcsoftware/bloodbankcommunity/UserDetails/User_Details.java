package com.jlcsoftware.bloodbankcommunity.UserDetails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.jlcsoftware.bloodbankcommunity.R;

import java.text.DateFormat;
import java.util.Calendar;

public class User_Details extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{



    private TextView blood_group_tv,user_details_date_of_birth_tv;

    private Dialog blood_group_picker_dialog;


    private RadioGroup radioGroup;

    private RadioButton radioButton;

    private MaterialButton save_user_details_btn_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__details);



        blood_group_tv=findViewById(R.id.user_details_blood_group_plus);

        blood_group_picker_dialog=new Dialog(User_Details.this);
        blood_group_picker_dialog.setContentView(R.layout.blood_group_picker_layout);


        radioGroup = blood_group_picker_dialog.findViewById(R.id.blood_radioGroup);





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



        save_user_details_btn_id = findViewById(R.id.save_user_details_btn_id);
        save_user_details_btn_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



    }





    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        user_details_date_of_birth_tv.setText(currentDateString);

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}