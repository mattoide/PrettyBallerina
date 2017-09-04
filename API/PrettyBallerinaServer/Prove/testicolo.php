<?php

require_once './ConnessioneDB.php';
require_once './Notifica.php';


libxml_use_internal_errors(true);
set_time_limit(0);


$cittaOrigine = filter_input(INPUT_GET, 'citta');
$distanzaMassima = filter_input(INPUT_GET, 'maxDist');

if ($distanzaMassima == "") {
    unset($distanzaMassima);
} else {
    $distanzaMassima = $distanzaMassima * 1000;
}

if ($cittaOrigine != "") {

    $urlSitoAudizioni = "http://www.danzaeffebi.com/danza-work-cerco-e-trovo-lavoro-evidenza/";

    $codiceHTMLSitoAudizioni = file_get_contents($urlSitoAudizioni);

    $documentoHTMLSitoAudizioni = new DOMDocument();
    $documentoHTMLSitoAudizioni->loadHTML($codiceHTMLSitoAudizioni);

    $linksAtutteLeAudizioniSingole = array();

    $Audizioni = $documentoHTMLSitoAudizioni->getElementsByTagName('a');

    for ($i = 0; $i < $Audizioni->length; $i++) {

        $linkAsingolaAudizione = $Audizioni->item($i)->getAttribute('href');


        //per leggere umanamente
//    if (!in_array($link . "<br />", $htmlall, true))
//        $htmlall[] = $audizioni->item($i)->getAttribute('href') . "<br />";

        if (!in_array($linkAsingolaAudizione, $linksAtutteLeAudizioniSingole, true))
            $linksAtutteLeAudizioniSingole[] = $Audizioni->item($i)->getAttribute('href');
    }

    $luoghiAudizioni = array();
    $linksAudizioni = array();

for ($i = 24; $i < count($linksAtutteLeAudizioniSingole) - 4; $i++) {
//    for ($l = 75; $l < 90; $l++) {

        $linkAudizione = $linksAtutteLeAudizioniSingole[$i];
        $html = file_get_contents($linkAudizione);



        $doc = new DOMDocument();
        $doc->loadHTML($html);

        $Luoghi = $doc->getElementsByTagName('p');

        for ($j = 0; $j < $Luoghi->length; $j++) {

            if ($Luoghi->item($j)->getAttribute('class') == 'additional_field') {

                if (!in_array($Luoghi->item($j)->textContent, $luoghiAudizioni, true)) {

                    $luoghiAudizioni[] = $Luoghi->item($j)->textContent;
                    $linksAudizioni[] = $linkAudizione;
                }
            }
        }
    }

    $AudizioniConInfo = array();


    if (isset($distanzaMassima)) {

        for ($i = 0; $i < count($luoghiAudizioni); $i++) {

            $destinazione = urlencode($luoghiAudizioni[$i]);

            $JsonRispostaMAPS = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" . $cittaOrigine. "&destinations=" . $destinazione . "&key=AIzaSyBQbNwMkfNMGt6rKFBRME-bUvdQC4PaCFM";

            $json = file_get_contents($JsonRispostaMAPS);
            $JsonInfoDistanza = json_decode($json);

            if ($JsonInfoDistanza->rows[0]->elements[0]->distance->value <= $distanzaMassima) {

                $AudizioniConInfo[] = [
                    "luogo" => $JsonInfoDistanza->destination_addresses[0],
                    "link" => $linksAudizioni[$i],
                    "distanzaText" => $JsonInfoDistanza->rows[0]->elements[0]->distance->text,
                    "distanzaValue" => $JsonInfoDistanza->rows[0]->elements[0]->distance->value
                ];
            }
        }
    } else {

        for ($i = 0; $i < count($luoghiAudizioni); $i++) {

            $destinazione = urlencode($luoghiAudizioni[$i]);

            $JsonRispostaMAPS = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" . $cittaOrigine. "&destinations=" . $destinazione . "&language=it&key=AIzaSyBQbNwMkfNMGt6rKFBRME-bUvdQC4PaCFM";

            $json = file_get_contents($JsonRispostaMAPS);
            $JsonInfoDistanza = json_decode($json);


            $AudizioniConInfo[] = [
                "luogo" => $JsonInfoDistanza->destination_addresses[0],
                "link" => $linksAudizioni[$i],
                "distanzaText" => $JsonInfoDistanza->rows[0]->elements[0]->distance->text,
                "distanzaValue" => $JsonInfoDistanza->rows[0]->elements[0]->distance->value
            ];
        }
    }

    usort($AudizioniConInfo, "ordina");

    //$distanzeOrdinate = json_encode($distanze);
    //print_r($distanzeOrdinate);

    print_r($AudizioniConInfo);

    $conn = new ConnessioneDB();
    $conn->InserisciNelDB($AudizioniConInfo);

//conta elementi nel db
//conta elementi nuovi
// se quelle nel db sono di piu, fai il match per ognuna, quella che non c'e ma sta nel db, eliminala dal db
    if ($conn->NumeroAudizioniDB() > count($AudizioniConInfo)) {

        new Notifica();
        
    } else {
//se quelle nel db sono di meno, fai il match, quella che non c'e nel db, aggiungila
        new Notifica();
    }
   
} else {
    print_r("Immetti una citta di origine");
}

function ordina($q, $w) {
    return $q['distanzaValue'] > $w['distanzaValue'];
}
