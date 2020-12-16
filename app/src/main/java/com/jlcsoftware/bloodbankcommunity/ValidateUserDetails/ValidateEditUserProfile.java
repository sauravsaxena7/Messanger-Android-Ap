package com.jlcsoftware.bloodbankcommunity.ValidateUserDetails;

import android.text.TextUtils;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateEditUserProfile {



    public void setError(TextInputLayout editText, String msg){

        editText.getEditText().setError(msg);
    }



    public  boolean isValidUserName(TextInputLayout editText, String username){

        boolean isValid=false;
        boolean isOnlyDigit = false;
        boolean isCapital = false;
        for(int i=0;i<username.length();i++){
            if(username.charAt(i)>='A' && username.charAt(i)<='Z'){
                isCapital =true;
                break;
            }
        }


        if (TextUtils.isEmpty(username)){

            setError(editText,"Username Important");

        } else {

            String orginalUsername = username;
            username= username.replace('_','u');
            username=username.replace('.','d');



            for(int i=0;i<username.length();i++){

                if (username.charAt(i) >= '0'
                        && username.charAt(i) <= '9') {
                    isOnlyDigit = true;
                }
                else {
                    isOnlyDigit = false;
                    break;
                }
            }


            Pattern my_pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
            Matcher my_match = my_pattern.matcher(username);
            boolean check = my_match.find();

            if ((check || username.toString().contains(" ") ) && !isOnlyDigit){

                isValid = false;
                setError(editText,"You can't take this as username");

            } else if(isOnlyDigit){

                isValid = false;
                setError(editText,"Username cannot not contains only number");

            }else if(isCapital){
                isValid = false;
                setError(editText,"Username cannot not contains Capital letters");

            } else {
                isValid=true;
            }



            if(isValid){

                if(orginalUsername.charAt(orginalUsername.length()-1)=='.'){
                    isValid = false;
                    setError(editText,"Username cannot not ends with dot or .");
                }

            }

        }

        return isValid;
    }




    public boolean validFirstName(TextInputLayout editText , String first_name){

        boolean isValid;
        if(TextUtils.isEmpty(first_name)){
            editText.getEditText().setError("First name cannot be empty");
            isValid =false;
        }else if(first_name.length()>14){
            editText.getEditText().setError("The length first name is not more than 13");
            isValid =false;
        }else{
            isValid=true;
        }

        return isValid;

    }


    public boolean validLastName(TextInputLayout editText , String last_name){

        boolean isValid;
        if(TextUtils.isEmpty(last_name)){
            editText.getEditText().setError("Last name cannot be empty");
            isValid=false;
        }else if(last_name.length()>21){
            editText.getEditText().setError("The length last name is not more than 20");
            isValid=false;
        }else{
            isValid=true;
        }

        return isValid;
    }



    public boolean validCurrentAddress(TextInputLayout editText , String current_address){

        boolean isValid;
        if(TextUtils.isEmpty(current_address)){
            isValid=false;
            editText.getEditText().setError("current address cannot be empty");
        }else if(current_address.length()>51){
            isValid=false;
            editText.getEditText().setError("The length current address is not more than 50");
        }else{
            isValid=true;
        }

        return isValid;

    }



    public boolean validWorkAs(TextInputLayout editText , String work_as){


        boolean isValid;
        if(TextUtils.isEmpty(work_as)){
            isValid=false;
            editText.getEditText().setError("Occupation cannot be empty");
        }else if(work_as.length()>21){
            isValid=false;
            editText.getEditText().setError("The length occupation is not more than 20");
        }else{
            isValid=true;
        }

        return isValid;
    }



    public boolean validWeight(TextInputLayout editText, String weight){

        boolean isValid;
        if(TextUtils.isEmpty(weight)){
            isValid=false;
            editText.getEditText().setError("Weight cannot be empty");
        }else if(weight.contains(".")){
            isValid=false;
            editText.getEditText().setError("not a valid weight");
        }else if(Integer.parseInt(weight)>221){
            editText.getEditText().setError("Your weight is very large please reduce it");
            isValid=false;
        }else {
            isValid=true;
        }

        return isValid;
    }







}


