<?php
//$mainurl = "http://www.danzaeffebi.com/danza-work/elisabeth-malanga-ballerina-classica-professionista-cerca-lavoro/";
$urlSitoAudizioni = "http://www.danzaeffebi.com/danza-work/audizione-junior-company-aloysius-ballet-per-produzione-balletto-don-chisciotte/";
//$mainurl = "http://www.danzaeffebi.com/danza-work-cerco-e-trovo-lavoro-evidenza/";

$codiceHTMLSitoAudizioni = file_get_contents($urlSitoAudizioni);



$documentoHTMLSitoAudizioni = new DOMDocument();

$documentoHTMLSitoAudizioni->loadHTML($codiceHTMLSitoAudizioni);

$luoghiAudizioni = array();

    $Luoghi = $documentoHTMLSitoAudizioni->getElementsByTagName('p');

    for ($j = 0; $j < $Luoghi->length; $j++) {

        if ($Luoghi->item($j)->getAttribute('class') == 'additional_field') {
            $luoghiAudizioni[] = $Luoghi->item($j)->textContent;
        }

    }
    
            print_r($luoghiAudizioni);
            //print_r($luogo->length);
            


?> 
