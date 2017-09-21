package com.example.jasmin.pretty_ballerina;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


public class FragmentRegister extends android.app.Fragment {

    Button registrati;
    Connessione connessione;

    String urlc = "http://barboniserver.asuscomm.com:3001/utenti/registrazione"; //casa
    //String urlc = "http://192.168.1.21:3001/utenti/registrazione"; //ufficio
//String urlc = "http://192.168.1.21:3001/utenti/registrazione"; //ufficio
    String result;

    EditText username, password, retype;


    View v;

    public FragmentRegister() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        this.v = inflater.inflate(R.layout.fragment_register, container, false);

        username = (EditText) v.findViewById(R.id.Username);
        password = (EditText) v.findViewById(R.id.Password);
        retype = (EditText) v.findViewById(R.id.Retype);


        registrati = (Button) v.findViewById(R.id.reg);
        registrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean reg;
                reg = IniziaReg();

                if (reg != false) {

                    if (connessione.GetCode() == 5) {
                        Toast.makeText(getActivity(), "Problemi con il server", Toast.LENGTH_LONG).show();
                    } else if (connessione.GetCode() == 0) {
                        Toast.makeText(getActivity(), "Username gia usato", Toast.LENGTH_LONG).show();
                    } else {

                        FragmentLog f1 = new FragmentLog();
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container, f1).commit();

                    }
                }
            }


            public boolean IniziaReg() {


                if (!password.getText().toString().equals(retype.getText().toString())) {
                    Toast.makeText(getActivity(), "Le password non combaciano", Toast.LENGTH_LONG).show();

                    return false;

                } else {


                    connessione = new Connessione(urlc, "post", String.valueOf(username.getText()), HashCode.hashCode(String.valueOf(password.getText())), null);

                    try {
                        result = connessione.execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    return true;

                }
            }
        });


        return this.v;


    }


}

