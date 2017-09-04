

var Mongoclient = require('mongodb').MongoClient;
exports.prova = function (req, res) {

    res.send("risposta");

}

exports.registrazione = function (req, res) {

    var username, password;

    if ((req.body.Username == null) || (req.body.Username == "") ||
            (req.body.Password == null) || (req.body.Password == "")) {


        res.status(400);
        res.send("Inserisci tutti i campi!");


    } else {


        username = req.body.Username;
        password = req.body.Password;

        Mongoclient.connect('mongodb://localhost:27017/Pretty_utenti', function (err, database) {

            if (err) {
                console.log(err)
                res.status(400);

                res.send("Connessione al database non riuscita")
            } else {

                database.collection('utenti').find({Username: username}).toArray(function (err, result) {

                    if (result <= 0) { //non c'è nulla quindi il controllo è andato a buon fine
                        database.collection('utenti').insert({
                            'Username': username,
                            'Password': password,
                            'Notifica': false
                        });
                        database.close();
                        res.status(200);
                        res.send("Registrazione riuscita");

                    } else {
                        database.close();
                        res.status(400);
                        res.send("Username già usato");
                    }
                });

            }
        });
    }



};

exports.login = function (req, res) {

    Mongoclient.connect('mongodb://localhost:27017/Pretty_utenti', function (err, database) {
        if (err) {
            console.log(err)
            res.status(400);

            res.send("Connessione al database non riuscita")
        } else {



            database.collection('utenti').findOne({Username: req.body.Username, Password: req.body.Password}).then(function (result) {

                if (result <= 0) {

                    database.close();
                    res.status(400);
                    res.send("Utente non trovato")

                } else {

                    database.close();

                    res.status(200);
                    res.send(result);

                }
            })
        }

    })

};

exports.modificaNotifica = function (req, res) {

    Mongoclient.connect('mongodb://localhost:27017/Pretty_utenti', function (err, database) {
        if (err) {
            console.log(err)
            res.status(400);

            res.send("Connessione al database non riuscita");
        } else {


            console.log(req.body.Username);

            console.log(req.body.Password);


            console.log(req.body.Ischeck);
            database.collection("utenti").findOne({Username: req.body.Username, Password: req.body.Password}).then(function (utente) {


                return utente;
            }).then(function (utente) {

                database.collection('utenti').update({_id: utente._id}, {$set: {Notifica: req.body.Ischeck}});
                database.close();


                res.status(200);
                res.send();

            });



        }

    });
};