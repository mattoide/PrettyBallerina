package com.example.jasmin.pretty_ballerina;

/**
 * Created by Jasmin on 08/08/2017.
 */

public class ElementoLista {


    private String citta;
    private String link;
    private String distanza;

    public ElementoLista(String pcitta, String plink, String pdistanza) {
        this.citta = pcitta;
        this.link = plink;
        this.distanza = pdistanza;
    }

    public String getCitta() {
        return this.citta;
    }

    public String getLink() {return this.link;}

    public String getDistanza(){return this.distanza;};



}
