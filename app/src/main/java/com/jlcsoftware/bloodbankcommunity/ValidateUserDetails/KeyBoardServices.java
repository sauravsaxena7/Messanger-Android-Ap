package com.jlcsoftware.bloodbankcommunity.ValidateUserDetails;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyBoardServices {

    public void hideKeyboardMethod(View view , Activity activity){
        try{
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
