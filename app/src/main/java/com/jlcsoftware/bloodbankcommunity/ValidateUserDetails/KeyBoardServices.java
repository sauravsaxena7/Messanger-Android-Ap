package com.jlcsoftware.bloodbankcommunity.ValidateUserDetails;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.jlcsoftware.bloodbankcommunity.UserHandle.Login;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

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
