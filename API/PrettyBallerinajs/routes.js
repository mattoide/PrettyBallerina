
var utenti = require ('./controllers/utente');

module.exports = function (app){
    
   // app.post("/utenti/registrazione", utenti.prova);
    app.post("/utenti/registrazione", utenti.registrazione);
    //app.get("/utenti/registrazione", utenti.prova);
    app.post("/utenti/login", utenti.login);
    app.post("/utenti/modificaNotifica", utenti.modificaNotifica);
};


