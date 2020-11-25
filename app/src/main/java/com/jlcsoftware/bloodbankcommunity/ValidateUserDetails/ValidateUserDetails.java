package com.jlcsoftware.bloodbankcommunity.ValidateUserDetails;

import android.text.TextUtils;

import com.rengwuxian.materialedittext.MaterialEditText;

public class ValidateUserDetails {


    public void setError(MaterialEditText editText, String msg){
        editText.setError(msg);
    }




    public boolean validFirstName(MaterialEditText editText , String first_name){

        boolean isValid;
        if(TextUtils.isEmpty(first_name)){
            editText.setError("First name cannot be empty");
            isValid =false;
        }else if(first_name.length()>14){
            editText.setError("The length first name is not more than 13");
            isValid =false;
        }else{
            isValid=true;
        }

        return isValid;

    }




    public boolean validLastName(MaterialEditText editText , String last_name){

        boolean isValid;
        if(TextUtils.isEmpty(last_name)){
            editText.setError("Last name cannot be empty");
            isValid=false;
        }else if(last_name.length()>21){
            editText.setError("The length last name is not more than 20");
            isValid=false;
        }else{
            isValid=true;
        }

        return isValid;
    }



    public boolean validCurrentAddress(MaterialEditText editText , String current_address){

        boolean isValid;
        if(TextUtils.isEmpty(current_address)){
            isValid=false;
            editText.setError("current address cannot be empty");
        }else if(current_address.length()>51){
            isValid=false;
            editText.setError("The length current address is not more than 50");
        }else{
            isValid=true;
        }

        return isValid;

    }



    public boolean validWorkAs(MaterialEditText editText , String work_as){


        boolean isValid;
        if(TextUtils.isEmpty(work_as)){
            isValid=false;
            editText.setError("Occupation cannot be empty");
        }else if(work_as.length()<21){
            isValid=false;
            editText.setError("The length occupation is not more than 20");
        }else{
            isValid=true;
        }

        return isValid;
    }



    public boolean validWeight(MaterialEditText editText, String weight){

        boolean isValid;
        if(TextUtils.isEmpty(weight)){
            isValid=false;
            editText.setError("Weight cannot be empty");
        }else if(weight.length()>4){
            isValid=false;
            editText.setError("The length weight is not more than 3");
        }else if(Integer.parseInt(weight)>221){
            editText.setError("Your weight is very large please reduce it");
            isValid=false;
        }else {
            isValid=true;
        }

        return isValid;
    }





}
