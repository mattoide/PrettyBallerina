<?php

require 'vendor/autoload.php';
require './Notifica.php';

class ConnessioneDB {

    private $dbName = "PrettyBallerina";
    private $collectionAudizioni = "ElencoAudizioni";
    private $collection;

    public function __construct() {

        $conn = new MongoDB\Client("mongodb://127.0.0.1:27017");
        $db = $conn->selectDatabase($this->dbName);
        $this->collection = $db->selectCollection($this->collectionAudizioni);
        
    }

    public function InserisciNelDB($documentiDaInserire) {

        if (count($documentiDaInserire) > 0) {

            $this->collection->insertMany($documentiDaInserire);
            new Notifica("Nuove audizioni!");
        }
    }

    public function NumeroAudizioniDB() {

        return $this->collection->count();
    }

    public function VerificaPresenza($link) {

        return $this->collection->find(['link' => $link]);
    }

    public function RimuoviDalDB($documentidaControllare) {


        //creo array di audizioni da rimuovere
        $documentiDaRimuovere = array();
        //mi faccio dare tutte le audizioni
        $a = $this->collection->find();

        foreach ($a as $b) {
//per ogni audizione, vedo se il campo link, e presente nell'array delle audizioni da controllare (che contiene solo i link dato che sono univochi)
            if (array_search($b['link'], array_column($documentidaControllare, 'link')) !== false) {

                //se non e nell'array, vuol dire che qyuella audizione non c'e piu quindi la metto nelle audizioni da rimuovere dal db
            } else {
                $documentiDaRimuovere[] = $b['link'];
            }
        }

        //se ci sono audizioni da rimuovere
        if (count($documentiDaRimuovere) > 0) {

            //le rimuovo (cercando da link)
            for ($i = 0; $i < count($documentiDaRimuovere); $i++) {
                $this->collection->deleteOne(["link" => $documentiDaRimuovere[$i]]);
                
            }

            // mando una notifica
            new Notifica("Alcune audizioni sono scadute!");
        }
    }

    public function ListaAudizioni() {
        //return $this->collection->find(["id"=>0]);
        return $this->collection->find([], ["id" => 1]);
    }

}
