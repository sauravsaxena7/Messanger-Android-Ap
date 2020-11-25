package com.jlcsoftware.bloodbankcommunity.ValidateUserDetails;


import android.text.TextUtils;
import android.widget.EditText;

import com.jlcsoftware.bloodbankcommunity.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUserCredentials {


    public void drawableRight(EditText editText){
        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.green_round_check, 0);
    }




    public void setError(EditText editText, String msg){
        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        editText.setError(msg);
    }





    public  boolean isValidUserName(EditText editText, String username){

        boolean isValid=false;
        boolean isOnlyDigit = false;
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

            }else {
                isValid=true;
                drawableRight(editText);
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



    public  boolean isValidEmailAddress(EditText editText, String email) {
        boolean isValid=false;

        if(TextUtils.isEmpty(email)){
            setError(editText,"Email Address important");
            isValid=false;
        } else {

            Pattern pattern = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
            Matcher matcher = pattern.matcher(email);
            boolean check = matcher.find();
            if(check){
                isValid=true;
                drawableRight(editText);
            }else{
                isValid = false;
                setError(editText,"Pattern of email is not valid");
            }

        }
        return isValid;


    }


    public boolean isValidPassword(EditText editText, String password){
        boolean isValid = false;
        if(TextUtils.isEmpty(password)){
            isValid = false;
            setError(editText,"Password important");
        }else if(password.length()<9){
            isValid = false;
            setError(editText,"Password must be of 8 characters");
        } else{
            isValid = true;
            drawableRight(editText);
        }

        return isValid;
    }







}
