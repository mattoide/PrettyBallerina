<?php

require_once './ConnessioneDB.php';

libxml_use_internal_errors(true);
set_time_limit(0);


$cittaOrigine = filter_input(INPUT_GET, 'citta');
$distanzaMassima = filter_input(INPUT_GET, 'maxDist');

if ($distanzaMassima == "") {
    unset($distanzaMassima);
} else {
    $distanzaMassima = $distanzaMassima * 1000;
}

$conn = new ConnessioneDB();

$Audizioni = array();

if ($cittaOrigine != "") {

    if (isset($distanzaMassima)) {

        $AudizioniDB = $conn->ListaAudizioni();

        foreach ($AudizioniDB as $audizioniDB) {

            $destinazione = urlencode($audizioniDB['luogo']);

            $JsonRispostaMAPS = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" . $cittaOrigine . "&destinations=" . $destinazione . "&language=it&key=AIzaSyBQbNwMkfNMGt6rKFBRME-bUvdQC4PaCFM";

            $json = file_get_contents($JsonRispostaMAPS);
            $JsonInfoDistanza = json_decode($json);

            if ($JsonInfoDistanza->rows[0]->elements[0]->distance->value <= $distanzaMassima) {


                $Audizioni[] = [
                    "luogo" => $JsonInfoDistanza->destination_addresses[0],
                    "link" => $audizioniDB['link'],
                    "distanzaText" => $JsonInfoDistanza->rows[0]->elements[0]->distance->text,
                    "distanzaValue" => $JsonInfoDistanza->rows[0]->elements[0]->distance->value
                ];
            }
        }
    } else {

        $AudizioniDB = $conn->ListaAudizioni();

        foreach ($AudizioniDB as $audizioniDB) {

            $destinazione = urlencode($audizioniDB['luogo']);

            $JsonRispostaMAPS = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" . $cittaOrigine . "&destinations=" . $destinazione . "&language=it&key=AIzaSyBQbNwMkfNMGt6rKFBRME-bUvdQC4PaCFM";

            $json = file_get_contents($JsonRispostaMAPS);
            $JsonInfoDistanza = json_decode($json);

            $Audizioni[] = [
                "luogo" => $JsonInfoDistanza->destination_addresses[0],
                "link" => $audizioniDB['link'],
                "distanzaText" => $JsonInfoDistanza->rows[0]->elements[0]->distance->text,
                "distanzaValue" => $JsonInfoDistanza->rows[0]->elements[0]->distance->value
            ];
        }
    }
    usort($Audizioni, "ordina");

    $distanzeOrdinate = json_encode($Audizioni);

    print_r($distanzeOrdinate);
} else {

    $AudizioniDB = $conn->ListaAudizioni();

    foreach ($AudizioniDB as $audizioniDB) {
        $Audizioni[] = [
            "luogo" => $audizioniDB['luogo'],
            "link" => $audizioniDB['link'],
        ];
    }
    print_r(json_encode($Audizioni));
}

function ordina($q, $w) {
    return $q['distanzaValue'] > $w['distanzaValue'];
}
