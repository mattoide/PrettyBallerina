<?php

require_once './ConnessioneDB.php';

//non mostro errori di php (esempio se ce qualcosa che non va con il codice html dei siti)
libxml_use_internal_errors(true);

//non do un tempo limite allo script, puo prendersi tutto il tempo che gli serve
set_time_limit(0);


$urlSitoAudizioni = "http://www.danzaeffebi.com/danza-work-cerco-e-trovo-lavoro-evidenza/";

//prendo il contenuto del sito url quindi il codice html
$codiceHTMLSitoAudizioni = file_get_contents($urlSitoAudizioni);


//carico html in questo domdocument
$documentoHTMLSitoAudizioni = new DOMDocument();
$documentoHTMLSitoAudizioni->loadHTML($codiceHTMLSitoAudizioni);

//array che conterra i link delle singole audizioni del sito
$linksAtutteLeAudizioniSingole = array();

//prendo tutti gli elementi (quindi si crea un array) con il tag <a> (so che mi servono quelli perche ho visto il codice del sito)
$Audizioni = $documentoHTMLSitoAudizioni->getElementsByTagName('a');


//scorro l'array degli elementi col tag a
for ($i = 0; $i < $Audizioni->length; $i++) {

    //di ogni elemento prendo il tag href che contiene il link
    $linksAudizioni = $Audizioni->item($i)->getAttribute('href');


    //per leggere umanamente
//    if (!in_array($link . "<br />", $htmlall, true))
//        $htmlall[] = $audizioni->item($i)->getAttribute('href') . "<br />";
//        
//        
    //se il link di quella audizione NON è presente nell'array dove ho salvato i link, allora lo inserisco (faccio questo perche ci sono piu link che portano alla stessa pagina, quindi duplicati)
    if (!in_array($linksAudizioni, $linksAtutteLeAudizioniSingole, true))
        $linksAtutteLeAudizioniSingole[] = $Audizioni->item($i)->getAttribute('href');
}


//array dei luoghi delle audizioni
$luoghiAudizioni = array();

//array dei link delle audizioni
$linksAudizioni = array();

$linkAudizione = '';


//scorro l'array delle singole audizioni (da 24 perche ho visto che i primi 24 link non sono audizioni, ne gli ultimi 4)
//
for ($i = 24; $i < count($linksAtutteLeAudizioniSingole) - 4; $i++) {
//for ($i = 75; $i < 90; $i++) {

    $linkAudizione = $linksAtutteLeAudizioniSingole[$i];
    $html = file_get_contents($linkAudizione);



    $doc = new DOMDocument();
    $doc->loadHTML($html);

    
    //prendo tutti gli elementi (quindi si crea un array) con il tag <p> (so che mi servono quelli perche ho visto il codice del sito)

    $Luoghi = $doc->getElementsByTagName('p');

    for ($j = 0; $j < $Luoghi->length; $j++) {

        //prendo solo gli attributi di tipo class con valore(?) additional_field (guardando il codice del sito ho visto che la sta il luogo)
        if ($Luoghi->item($j)->getAttribute('class') == 'additional_field') {

            //se non c'e nell'array, inserisco  luoghi nel loro array, e i link nel loro array
            if (!in_array($Luoghi->item($j)->textContent, $luoghiAudizioni, true)) {

                $luoghiAudizioni[] = $Luoghi->item($j)->textContent;
                $linksAudizioni[] = $linkAudizione;
            }
        }
    }
}


//array di audizioni complete di luogo e link
$AudizioniConInfo = array();



//scorro l'array dei luoghi (o dei link, tanto combaciano)
for ($i = 0; $i < count($luoghiAudizioni); $i++) {

    $luogoAudizione = $luoghiAudizioni[$i];


    //metto tutto nell'array
    $AudizioniConInfo[] = [
        "luogo" => $luogoAudizione,
        "link" => $linksAudizioni[$i],
        "distanzaText" => null,
        "distanzaValue" => null
    ];
}


//creo l'array delle audizioni da inserire nel db
$audizioniDaInserire = array();


$conn = new ConnessioneDB();

//metto questa variabile  a0 e la usero per contare quante occorennze trovo dalla find (il driver di mongodb per php7 non mi fa fare molte cose)
$numeroAudizioni = 0;

//TODO: rimettere a 0 ogni volta nel for


//se ci sono piu audizioni trovate da internet, che quelle salvate
if ($conn->NumeroAudizioniDB() <= count($AudizioniConInfo)) {

    for ($i = 0; $i < count($AudizioniConInfo); $i++) {

        //verifico la presenza del link (unica cosa certa) di ogni audizione con quelle nel db
        $num = $conn->VerificaPresenza($linksAudizioni[$i]);

        //le conto (a mano, xke non ce una funzione che lo fa x me)
        foreach ($num as $n) {
            $numeroAudizioni++;
        }
        
//se non lha trovata
        if ($numeroAudizioni <= 0) {
            //la inserisce nelle audizioni nuove da inserire nel db
            $audizioniDaInserire[] = $AudizioniConInfo[$i];
        }
    }

    $conn->InserisciNelDB($audizioniDaInserire);
} else {

    //creo array di audizioni da crontrollare
    $audizioniDaControllare = array();

    //salvo tutti i link dentro
    for ($i = 0; $i < count($AudizioniConInfo); $i++) {
        $audizioniDaControllare[] = $AudizioniConInfo[$i]['link'];
    }


    $conn->RimuoviDalDB($AudizioniConInfo);
}
   