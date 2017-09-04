package com.example.jasmin.pretty_ballerina;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Jasmin on 13/08/2017.
 */

//async task perchè deve fare delle operazioni in background quindi serve per forza

public class Connessione extends AsyncTask<Void, Void, String> {


    URL url;
    HttpURLConnection hcon;
    InputStream inputStream;
    String result;
    String tiporichiesta;
    String datidainviare;
    String username, password;
    Boolean Ischek;
    int code;

    public Connessione(String _urlc, String _tiporichiesta, String _username, String _password, @Nullable Boolean  _Ischeck ) {


        this.username = _username;
        this.password = _password;

        try {
            this.url = new URL(_urlc);

        } catch (MalformedURLException ex) {
        }

        this.tiporichiesta = _tiporichiesta;

        this.Ischek = _Ischeck;
    }

    @Override //apri connesione
    protected void onPreExecute() {

        super.onPreExecute();

        if(this.Ischek == null){

        try {
            datidainviare = URLEncoder.encode("Username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&" +
                            URLEncoder.encode("Password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
        } catch (UnsupportedEncodingException e) {

        }}else{
            try {
                datidainviare = URLEncoder.encode("Username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&" +
                        URLEncoder.encode("Password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") + "&" +
                        URLEncoder.encode("Ischeck", "UTF-8")+ "="+ Ischek;
            } catch (UnsupportedEncodingException e) {

            }



        }


    }

    @Override
    protected String doInBackground(Void... params) {



        try {

            hcon = (HttpURLConnection) url.openConnection();
            hcon.setConnectTimeout(5000);

       if (tiporichiesta == "get") {


           hcon.connect();

           inputStream = hcon.getInputStream();
           result = convertinputStreamToString(inputStream);



       }else if (tiporichiesta == "post") {

           hcon.setDoOutput(true);
           OutputStreamWriter outputStreamWriter = new OutputStreamWriter(hcon.getOutputStream());
           outputStreamWriter.write(datidainviare);



           outputStreamWriter.flush();


           inputStream = hcon.getInputStream();
           code = hcon.getResponseCode();
           result = convertinputStreamToString(inputStream);
        }
        } catch (SocketTimeoutException so){
            code = 5;
        } catch (IOException ex){}


        return result;
    }




    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
 Log.d("ccc", String.valueOf(code));

    }


    public static String convertinputStreamToString(InputStream ists) throws IOException {
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


    public int GetCode(){

        return this.code;
    }

}
