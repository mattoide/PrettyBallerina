package com.example.jasmin.pretty_ballerina;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

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
 * jasmin puzza di cacca a
 */

public class CercaAudizioni extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener {


    private static final int REQUEST_RESOLVE_ERROR = 0;
    private static final String DIALOG_ERROR = null;
    GoogleApiClient googleApiClient;
    private boolean resolvingError;
    private LocationRequest mLocationRequest;

    LocationManager manager;
    Location location;


    double latidude;
    double longitude;

    boolean searchbycity = false;

    EditText CittaE;
    Button InvioB, InvioBLoc;
    TextView nomecitta, km, linkb;

    String urlo = "http://barboniserver.asuscomm.com/PrettyBallerinaServer/PrendiAudizioniDaDB.php";

    URL url = null;
    HttpURLConnection httpURLConnection = null;
    InputStream inputStream = null;

    String risposta;
    JSONArray jsonArray = new JSONArray();
    private JSONObject json_data;


    private ListView list_view_citta;
    private CustomAdapter customAdapter;
    private List<ElementoLista> list;

    ProgressDialog progressDialog;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cerca_audizioni);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


//        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            if (!resolvingError) {
//                googleApiClient.connect();
//            }
//
//        }


        nomecitta = (TextView) findViewById(R.id.textView2);
        km = (TextView) findViewById(R.id.textView3);
        linkb = (TextView) findViewById(R.id.textView4);


        CittaE = (EditText) findViewById(R.id.Città);
        InvioB = (Button) findViewById(R.id.Invio);

        InvioB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchbycity = true;

                try {
                    url = new URL(urlo + "?citta=" + CittaE.getText());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                customAdapter.clear();


                if (InternetConnection.haveInternetConnection(CercaAudizioni.this)) {

                    starta();

                } else {

                    AlertDialog.Builder miaAlert = new AlertDialog.Builder(CercaAudizioni.this);
                    miaAlert.setTitle("Connessione a Intenert non presente!");
                    miaAlert.setMessage("Senza internet non possiamo cercare le informazioni");

                    AlertDialog alert = miaAlert.create();
                    alert.show();

                }

            }
        });
        InvioBLoc = (Button) findViewById(R.id.miapos);

        InvioBLoc.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();
                } else {

                    if (!resolvingError)
                        googleApiClient.connect();

                    if (googleApiClient.isConnected()) {

                        searchbycity = false;

                        Log.d("aaa", String.valueOf(latidude + "  " + longitude));

                        if ((latidude <= 0.0) || (longitude <= 0.0)) {

                            Toast.makeText(getApplicationContext(), "Dammi un attimo per rilevare la tua posizione. Riprova tra un secondo", Toast.LENGTH_SHORT).show();


                        } else {

                            try {
                                url = new URL(urlo + "?citta=" + String.valueOf(latidude) + "," + String.valueOf(longitude));
                                Log.d("AAA", String.valueOf(url));


                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }

                            customAdapter.clear();


                            if (InternetConnection.haveInternetConnection(CercaAudizioni.this)) {

                                starta();

                            } else {

                                AlertDialog.Builder miaAlert = new AlertDialog.Builder(CercaAudizioni.this);
                                miaAlert.setTitle("Connessione a Internet non presente!");
                                miaAlert.setMessage("Senza internet non possiamo cercare le informazioni");

                                AlertDialog alert = miaAlert.create();
                                alert.show();

                            }

                        }

                    }
                }

            }
        });

        list_view_citta = (ListView) findViewById(R.id.list_view);

        list = new LinkedList<ElementoLista>();

        customAdapter = new CustomAdapter(this, R.layout.elemento_lista, list);

        list_view_citta.setAdapter(customAdapter);


        // list.add(0, new ElementoLista("roma","http://www.google.com", "100km"));

        //list.add(1, new ElementoLista("naoli", "www.com","50km"));

        // list.add(2, new ElementoLista("inferno", "www.666","666km"));


    }


    private class richiesta extends AsyncTask<Void, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(CercaAudizioni.this);
            progressDialog.setTitle("Attendi...");
            progressDialog.setMessage("Stiamo ordinando le audizioni per te, abbi pazienza...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            try {

                httpURLConnection = (HttpURLConnection) url.openConnection();


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


            } catch (MalformedURLException e) {
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

            if (risposta == null) {
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

                if ((CittaE.getText().length() <= 0) && (searchbycity == true)) {

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
    public void onLocationChanged(final Location location) {

        latidude = location.getLatitude();
        longitude = location.getLongitude();

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setSmallestDisplacement(5);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);


        try {
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            longitude = location.getLongitude();
            latidude = location.getLatitude();
        } catch (NullPointerException e) {

        }


    }

    public void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        longitude = location.getLongitude();
        latidude = location.getLatitude();
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Devi abilitare il GPS per usare questa funzione. Vuoi abilitarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onConnectionSuspended(int i) {

        googleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


        if (resolvingError)
            return;
        if (connectionResult.hasResolution()) {
            try {
                resolvingError = true;
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                googleApiClient.connect();
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, REQUEST_RESOLVE_ERROR).show();
            resolvingError = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            resolvingError = false;
            if (resultCode == RESULT_OK) {
                if (!googleApiClient.isConnecting() &&
                        !googleApiClient.isConnected()) {
                    googleApiClient.connect();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CercaAudizioni.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        googleApiClient.connect();
    }
}
