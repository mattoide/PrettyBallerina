package com.example.jasmin.pretty_ballerina;

/**
 * Created by Jasmin on 30/08/2017.
 */

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;


public class SessionManager {

    SharedPreferences pref;
    Editor editor;
    Context _context;


    int PRIVATE_MODE = 0;



    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "username";

    // Email address (make variable public to access from outside)
    public static final String KEY_PASSWORD = "password";


    public static final String KEY_NOTIFICA = "notifica";


    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(String.valueOf(R.string.preferenze), PRIVATE_MODE);
        editor = pref.edit();
    }


    public void createLoginSession(String name, String password, Boolean notifica){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing email in pref
        editor.putString(KEY_PASSWORD, HashCode.hashCode(password));

        editor.putBoolean(KEY_NOTIFICA, notifica);
        // commit changes
        editor.commit();
    }



    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));

        // return user
        return user;
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }


    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, Entrata.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, Entrata.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);

        FirebaseMessaging.getInstance().unsubscribeFromTopic("pb");
    }

    public void modificaNotifiche (Boolean notifica){
        editor.putBoolean(KEY_NOTIFICA, notifica);
        // commit changes
        editor.commit();
    }
}
