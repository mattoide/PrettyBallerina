<?php

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

        $linksAudizioni = $Audizioni->item($i)->getAttribute('href');


        //per leggere umanamente
//    if (!in_array($link . "<br />", $htmlall, true))
//        $htmlall[] = $audizioni->item($i)->getAttribute('href') . "<br />";

        if (!in_array($linksAudizioni, $linksAtutteLeAudizioniSingole, true))
            $linksAtutteLeAudizioniSingole[] = $Audizioni->item($i)->getAttribute('href');
    }

    $luoghiAudizioni = array();
    $linksAudizioni = array();
    $linkAudizione = '';

for ($i = 24; $i < count($linksAtutteLeAudizioniSingole) - 4; $i++) {
//    for ($l = 24; $l < 30; $l++) {

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
    
    $origine = $cittaOrigine;

    if (isset($distanzaMassima)) {

        for ($i = 0; $i < count($luoghiAudizioni); $i++) {

            $destinazione = urlencode($luoghiAudizioni[$i]);

            $JsonRispostaMAPS = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" . $origine . "&destinations=" . $destinazione . "&key=AIzaSyBQbNwMkfNMGt6rKFBRME-bUvdQC4PaCFM";

            $json = file_get_contents($JsonRispostaMAPS);
            $JsonInfoDistanza = json_decode($json);

            if ($JsonInfoDistanza->rows[0]->elements[0]->distance->value <= $distanzaMassima) {

                $AudizioniConInfo[] = [
                    "luogo" => $JsonInfoDistanza->destination_addresses[0],
                    "link" => $linksAudizioni[$i],
                    "distanzaText" => $JsonInfoDistanza->rows[0]->elements[0]->distance->text,
                    "distanzaValue" => $JsonInfoDistanza->rows[0]->elements[0]->distance->value
                ];



//                $distanze = $distanze . "{".
//                    "luogo: " . $obj->destination_addresses[0].",".
//                    "link: " . $link[$o] . ",".
//                    "distanzaText:" . $obj->rows[0]->elements[0]->distance->text . ",".
//                    "distanzaValue:" . $obj->rows[0]->elements[0]->distance->value.
//                "},";
            }
        }
    } else {

        for ($i = 0; $i < count($luoghiAudizioni); $i++) {

            $destinazione = urlencode($luoghiAudizioni[$i]);

            $JsonRispostaMAPS = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" . $origine . "&destinations=" . $destinazione . "&language=it&key=AIzaSyBQbNwMkfNMGt6rKFBRME-bUvdQC4PaCFM";

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

    $distanzeOrdinate = json_encode($AudizioniConInfo);
    
    print_r($distanzeOrdinate);
    
} else {
    print_r("Immetti una citta di origine");
}



function ordina($q, $w) {
    return $q['distanzaValue'] > $w['distanzaValue'];
}
