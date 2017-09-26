package com.example.jasmin.pretty_ballerina;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

/**
 * Created by Jasmin on 09/08/2017.
 */

public class Entrata extends Activity {
    Button bn;
    boolean stato = false;
    FragmentTransaction fragmentTransaction;

    SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrata);

        if (InternetConnection.haveInternetConnection(Entrata.this)) {


            FragmentManager fragmentManager = getFragmentManager();
            android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            FragmentLogin f1 = new FragmentLogin();
            fragmentTransaction.replace(R.id.fragment_container, f1).commit();

            stato = true;


            bn = (Button) findViewById(R.id.vai_registrazione);
            bn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fragmentManager = getFragmentManager();
                    //fragmentTransaction = fragmentManager.beginTransaction();


                    android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


                    if (!stato) {

                        FragmentLogin f1 = new FragmentLogin();
                        //android.app.Fragment f1 = new Fragment();
                        fragmentTransaction.replace(R.id.fragment_container, f1).commit();
                        //  fragmentTransaction.add(R.id.fragment_container, f1);
                        // fragmentTransaction.commit();
                        bn.setText("Vai a registrazione");
                        stato = true;
                    } else {
                        FragmentRegister f2 = new FragmentRegister();
                        // android.app.Fragment f2 = new Fragment();
                        fragmentTransaction.replace(R.id.fragment_container, f2).commit();
                        //fragmentTransaction.add(R.id.fragment_container, f2);
                        //fragmentTransaction.commit();
                        bn.setText("Ritorna a Login");
                        stato = false;
                    }

                }
            });


        } else {
            AlertDialog.Builder miaAlert = new AlertDialog.Builder(this);
            miaAlert.setTitle("Connessione a Intenert non presente!");
            miaAlert.setMessage("Senza internet non possiamo cercare le informazioni");
            miaAlert.setCancelable(false);
            AlertDialog alert = miaAlert.create();
            alert.show();
        }

    }


}
