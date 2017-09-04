var express = require('express');

//mi serve x usare il bodyparser per prendere dati inviati in POST
var bodyParser = require('body-parser');


var app = express();


//dico all'app che userà il bodyparser
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

//richiedo la "classe" (?) routes che contiene le cose da fare x ogni path [quella specie di cast finale non so cosa sia]
require('./routes') (app);




app.listen(3001);

console.log("Acolto su porta 3001");

