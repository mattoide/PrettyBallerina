package com.example.jasmin.pretty_ballerina;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    Button Cerca_vicinanzeB;
    Button Cerca_cittàB;
    Button PreferenzeB;
    Button logout;
    Button info;
    Button ok;
    SessionManager session;
    Switch not;
    Connessione connessione;

    String urlc = "http://barboniserver.asuscomm.com:3001/utenti/modificaNotifica"; //casa
    // String urlc = "http://192.168.1.21:3001/utenti/modoficaNotifica";  //ufficio
    // String urlc = "http://192.168.1.21:3001/utenti/modoficaNotifica";  //ufficio

    String result;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        not = (Switch) findViewById(R.id.switch1);

        session = new SessionManager(getApplicationContext());

        if (getApplicationContext().getSharedPreferences(String.valueOf(R.string.preferenze), 0).getBoolean("notifica", false) == true) {

            FirebaseMessaging.getInstance().subscribeToTopic("pb");
            not.setChecked(true);

        } else {

            FirebaseMessaging.getInstance().unsubscribeFromTopic("pb");
            not.setChecked(false);

        }


        not.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                Notswitch(isChecked);
                session.modificaNotifiche(isChecked);

                if (isChecked == true) {

                    FirebaseMessaging.getInstance().subscribeToTopic("pb");
                    Log.d("ddd", String.valueOf(isChecked));

                } else {

                    FirebaseMessaging.getInstance().unsubscribeFromTopic("pb");
                    Log.d("ddd", String.valueOf(isChecked));
                }
            }
        });


        info = (Button) findViewById(R.id.info); // attiva bottone


        info.setOnClickListener(new View.OnClickListener() { // dici che fa sul click
            @Override
            public void onClick(View v) {

                dialog = new Dialog(MainActivity.this);

                dialog.setContentView(R.layout.dialoglay);
                ok = (Button) dialog.findViewById(R.id.ok);

                //apre una dialog box


                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }


        });


        Cerca_cittàB = (Button) findViewById(R.id.Cerca_città);
        Cerca_cittàB.setOnClickListener(new View.OnClickListener() { // dici che fa sul click
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Cerca_incitta.class);
                startActivity(intent);
                finish();
            }


        });


        // session.checkLogin();
        // get user data from session

//        HashMap<String, String> user = session.getUserDetails();
//
//        // name
//        final String name = user.get(SessionManager.KEY_NAME);
//
//        // password
//        String password = user.get(SessionManager.KEY_PASSWORD);


        logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() { // dici che fa sul click
            @Override
            public void onClick(View v) {
                session.logoutUser();


                Intent i = new Intent(getApplication().getApplicationContext(), Entrata.class);
                startActivity(i);
                finish();


            }


        });
    }


    public void Notswitch(Boolean IsCheck) {

        Log.d("AAAAAA", getApplicationContext().getSharedPreferences(String.valueOf(R.string.preferenze), 0).getString("password", ""));
        connessione = new Connessione(urlc, "post", getApplicationContext().getSharedPreferences(String.valueOf(R.string.preferenze), 0).getString("username", ""),
                getApplicationContext().getSharedPreferences(String.valueOf(R.string.preferenze), 0).getString("password", "")
                , IsCheck);
        try {
            result = connessione.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }


}
