package com.example.jasmin.pretty_ballerina;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jasmin on 07/08/2017.
 * jasmin puzza di cacca
 */

public class Cerca_incitta extends AppCompatActivity{


    EditText CittaE;
    Button InvioB;
    TextView nomecitta, km, linkb;
    String urlo= "http://192.168.0.107/PrettyBallerinaServer/PrendiAudizioniDaDB.php";

    URL url= null;
    HttpURLConnection httpURLConnection =null;
    InputStream inputStream = null;

    String risposta;
    JSONArray jsonArray = new JSONArray();
    private JSONObject json_data;


    private ListView list_view_citta;
    private Custom_adapter customAdapter;
    private List<ElementoLista> list;

    ProgressDialog progressDialog;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cerca_vicinanze);

        nomecitta = (TextView) findViewById(R.id.textView2);
        km=(TextView) findViewById(R.id.textView3);
        linkb = (TextView) findViewById(R.id.textView4);


        CittaE = (EditText) findViewById(R.id.Città);
        InvioB = (Button)findViewById(R.id.Invio);

        InvioB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                customAdapter.clear();


                if (InternetConnection.haveInternetConnection(Cerca_incitta.this)) {

                    starta();

                }else{

                    AlertDialog.Builder miaAlert = new AlertDialog.Builder(Cerca_incitta.this);
                    miaAlert.setTitle("Connessione a Intenert non presente!");
                    miaAlert.setMessage("Senza internet non possiamo cercare le informazioni");

                    AlertDialog alert = miaAlert.create();
                    alert.show();

                }

            }
        });

        list_view_citta = (ListView) findViewById(R.id.list_view);

        list = new LinkedList<ElementoLista>();

        customAdapter = new Custom_adapter(this, R.layout.lista, list);

        list_view_citta.setAdapter(customAdapter);



        // list.add(0, new ElementoLista("roma","http://www.google.com", "100km"));

        //list.add(1, new ElementoLista("naoli", "www.com","50km"));

        // list.add(2, new ElementoLista("inferno", "www.666","666km"));



    }
    private class richiesta extends AsyncTask<Void, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(Cerca_incitta.this);
            progressDialog.setTitle("Attendi...");
            progressDialog.setMessage("Stiamo ordinando le audizioni per te, abbi pazienza...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            try {

                url = new URL(urlo+"?&citta="+CittaE.getText());
                httpURLConnection = (HttpURLConnection) url.openConnection();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }

        }

        @Override
        protected String doInBackground(Void... params) {

            try {

                httpURLConnection.setConnectTimeout(5000);


                httpURLConnection.connect();

                inputStream = httpURLConnection.getInputStream();

                risposta = convertinputStreamToString(inputStream);


            }  catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                httpURLConnection.disconnect();
            } finally {
                httpURLConnection.disconnect();
            }



            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(risposta == null){
                Toast.makeText(getApplicationContext(), "Problemi con il server, riprova tra poco", Toast.LENGTH_SHORT).show();

            } else {


                if (list.size() > 0) {
                    nomecitta.setVisibility(View.VISIBLE);
                    km.setVisibility(View.VISIBLE);
                    linkb.setVisibility(View.VISIBLE);
                } else {
                    nomecitta.setVisibility(View.INVISIBLE);
                    km.setVisibility(View.INVISIBLE);
                    linkb.setVisibility(View.INVISIBLE);

                }


                try {

                    jsonArray = new JSONArray(risposta);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                customAdapter.clear();

                if (CittaE.getText().length() <= 0) {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            json_data = jsonArray.getJSONObject(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            list.add(i, new ElementoLista(json_data.getString("luogo"), json_data.getString("link"), ""));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                } else {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            json_data = jsonArray.getJSONObject(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            list.add(i, new ElementoLista(json_data.getString("luogo"), json_data.getString("link"), json_data.getString("distanzaText")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                //list.add(0, new ElementoLista("luogo", "distText", "link"));

            }

progressDialog.dismiss();

        }
    }


    public static String convertinputStreamToString(InputStream ists)
            throws IOException {
        if (ists != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader r1 = new BufferedReader(new InputStreamReader(
                        ists, "UTF-8"));
                while ((line = r1.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                ists.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }
    public void starta() {
        new richiesta().execute();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Cerca_incitta.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
