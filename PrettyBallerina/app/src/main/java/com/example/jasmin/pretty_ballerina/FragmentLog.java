package com.example.jasmin.pretty_ballerina;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


public class FragmentLog extends android.app.Fragment {

    View v;
    Button login;
    Connessione connessione;
    String result;

    String urlc = "http://barboniserver.asuscomm.com:3001/utenti/login"; //casa
//String urlc = "http://192.168.1.21:3001/utenti/login"; //ufficio
//String urlc = "http://192.168.1.21:3001/utenti/login"; //ufficio
    EditText username, password;
    SessionManager session;
    JSONObject utente;

    public FragmentLog() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.v = inflater.inflate(R.layout.fragment_log, container, false);

// Session Manager
        session = new SessionManager(getActivity().getApplication().getApplicationContext());




        username = (EditText) v.findViewById(R.id.Usernamelog);
        password = (EditText) v.findViewById(R.id.Passwordlog);

        login = (Button) v.findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v) {

               IniziaLog();

               if(connessione.GetCode()==5){

                 Toast.makeText(getActivity(),"Problemi con il server", Toast.LENGTH_LONG).show();

               }

               else if(connessione.GetCode()==0){

                 Toast.makeText(getActivity(),"Dati errati",Toast.LENGTH_LONG).show();

               }else{

               try {
                   try {

                   utente = new JSONObject(result);

                } catch (JSONException e) {
                   e.printStackTrace();
               }
                   session.createLoginSession(
                           utente.getString("Username"),
                           utente.getString("Password"),
                           utente.getBoolean("Notifica")
                   );

               } catch (JSONException e) {
                   e.printStackTrace();
               }


               Intent intent = new Intent(getActivity(), MainActivity.class);
               startActivity(intent);
               getActivity().finish();

               }}


        });

        return this.v;
    }

    public void IniziaLog(){



        connessione = new Connessione(urlc, "post", String.valueOf(username.getText()), HashCode.hashCode(String.valueOf(password.getText())),null);
        try {
            result = connessione.execute().get();


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (InternetConnection.haveInternetConnection(getActivity())) {


            String username = getActivity().getApplicationContext().getSharedPreferences(String.valueOf(R.string.preferenze), 0).getString("username", "");
            String password = getActivity().getApplicationContext().getSharedPreferences(String.valueOf(R.string.preferenze), 0).getString("password", "");
            Boolean notifica = getActivity().getApplicationContext().getSharedPreferences(String.valueOf(R.string.preferenze), 0).getBoolean("notifica", false);


            if ((username != "") ||
                    (getActivity().getApplicationContext().getSharedPreferences(String.valueOf(R.string.preferenze), 0)).contains("username") ||
                    (password != "") ||
                    (getActivity().getApplicationContext().getSharedPreferences(String.valueOf(R.string.preferenze), 0)).contains("password") ||
                    (getActivity().getApplicationContext().getSharedPreferences(String.valueOf(R.string.preferenze), 0)).contains("notifica")) {

                session = new SessionManager(getActivity().getApplicationContext());

                session.createLoginSession(username, password, notifica);

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();

            }


        } else{
            AlertDialog.Builder miaAlert = new AlertDialog.Builder(getActivity());
            miaAlert.setTitle("Connessione a Intenert non presente!");
            miaAlert.setMessage("Senza internet non possiamo cercare le informazioni");
            miaAlert.setCancelable(false);
            AlertDialog alert = miaAlert.create();
            alert.show();
        }
    }
}


