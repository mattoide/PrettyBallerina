package com.example.jasmin.pretty_ballerina;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Jasmin on 08/08/2017.
 */

public class CustomAdapter extends ArrayAdapter<ElementoLista> {
    public CustomAdapter(Context context, int textViewResourceId, List<ElementoLista> objects) {
        super(context, textViewResourceId, objects);

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(R.layout.elemento_lista, null);

        TextView Citta = (TextView) convertView.findViewById(R.id.Citta);
        final TextView Distanza = (TextView) convertView.findViewById(R.id.Distanza);
        final ElementoLista elementoLista = getItem(position);
//            Citta.setText(elementoLista.getCitta());
//                   Distanza.setText(String.valueOf(elementoLista.getDistanza()));



        if(elementoLista.getCitta().length()>0) {
            Citta.setText(elementoLista.getCitta());
        } else
        {
            Citta.setText("Nessuna info, vai al link!");
        }
        if(elementoLista.getDistanza() != "null") {
            Distanza.setText(String.valueOf(elementoLista.getDistanza()));
        } else if(elementoLista.getDistanza().length() <=0){

            Distanza.setText("");

        } else
        {
            Distanza.setText("Nessuna info, vai al link!");
        }

        Button vai = (Button) convertView.findViewById(R.id.Vai);
        vai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse(elementoLista.getLink());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                getContext().startActivity(intent);




            }
        });








        return convertView;
    }


   }



