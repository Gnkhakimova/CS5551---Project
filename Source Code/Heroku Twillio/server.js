/**
 * Created by user on 23/10/2016.
 */
var MongoClient = require('mongodb').MongoClient;
var assert = require('assert');
var bodyParser = require("body-parser");
var express = require('express');
var cors = require('cors');
var app = express();
var resultF = "";
var docs ="";
var all ="";
var queue = "";
var deleteF = false;
var inserted = false;
var gotQueue = false;
var port = process.env.PORT || 8081;

var url = 'mongodb://gulnoza:ayajonam1@ds125912.mlab.com:25912/hackaroo2018';

app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

app.post('/add', function (req, res) {
    MongoClient.connect(process.env.MONGOLAB_URI || url, function(err, client) {
        if(err)
        {


        }
        var db = client.db("hackaroo2018");


        insertDocument(db, req.query, function() {
            while (inserted !=true){
                console.log("waiting");
            }
            if(inserted == true){
                res.status(200).send("OK")
            }
            else {

            }
            inserted = false;


        });

    });
})
app.post('/sendSMS', function (req, res) {
    var Number = req.query.phone;
    var Text = req.query.text;

    // Download the helper library from https://www.twilio.com/docs/node/install
// Your Account Sid and Auth Token from twilio.com/console
    var accountSid = 'ACb1e11a5c7843f06d3d78fdaa51d02e3a';
    var authToken = '7a41d1f0227873cbf3a5b567dfede4c6';
    var client = require('twilio')(accountSid, authToken);

    client.messages
        .create({
            body: Text,
            from: '+19132257724',
            to: Number
        })
        .then(message => console.log(message.sid))
.done();
})


app.get('/searchClinic', function (req, res) {
    MongoClient.connect(process.env.MONGOLAB_URI || url, function(err, client) {
        if(err)
        {

        }
        var db = client.db("hackaroo2018");
        var clinic = req.query.clinicname;
        searchClinic(db,clinic,function () {
            if(resultF != "") {
                res.status(200).send(resultF);
            }
            else{

            }

        }  );
    });
})


app.get('/searchDoctor', function (req, res) {
    MongoClient.connect(process.env.MONGOLAB_URI || url, function(err, client) {
        if(err)
        {


        }
        var db = client.db("hackaroo2018");
        var doc = req.query.doctor;
        searchDoctor(db,doc,function () {
            if(docs != "") {
                res.status(200).send(docs);
            }
            else{
res.status(200).send(docs)
            }

        }  );
    });
})
app.get('/getAll', function (req, res) {
    MongoClient.connect(process.env.MONGOLAB_URI || url, function(err, client) {
        if(err)
        {


        }
        var db = client.db("hackaroo2018");
        var temp =req.query.all;
        getAll(db,temp,function () {
            if(all != "") {
                res.status(200).send(all);
            }
            else{
                res.status(200).send(all)
            }

        }  );
    });
})

app.get('/getQueue', function (req, res) {
    MongoClient.connect(process.env.MONGOLAB_URI || url, function(err, client) {
        if(err)
        {


        }
        var db = client.db("hackaroo2018");
        var temp =req.query.all;
        getQueue(db,temp,function () {

            while (gotQueue != true){
                console.log("waiting");
            }
            if(queue != "") {
                res.status(200).send(queue);
            }
            else{
                res.status(200).send(queue)
            }
gotQueue = false;
        }  );
    });
})

var insertDocument = function(db, data, callback) {
    db.collection('Queue').insertOne( data, function(err, result) {
        if (err) throw err;
        inserted = true;
        console.log("Number of records inserted: ");
        callback();
    });
};

var searchClinic = function (db,data,callback) {
    db.collection('Clinics').find({ClinicName: data}).toArray(function(err, result) {
        if (err) throw err;
        resultF = result;
        console.log(result);
        callback();
    });
}
var getAll = function (db,data,callback) {
    db.collection('Clinics').find({ClinicName: {$exists : true}}).toArray(function(err, result) {
        if (err) throw err;
        all = result;
        console.log(result);
        callback();
    });
}
var getQueue = function (db,data,callback) {
    db.collection('Queue').find({DoctorName: {$exists : true}}).toArray(function(err, result) {
        if (err) throw err;
        console.log(result.toString());
        queue = result;
        console.log(result);
        gotQueue = true;
          callback();
    });
}

var searchDoctor = function (db,data,callback) {
if(data == "Dentist"){
    db.collection('Clinics').find({Dentist: {$exists : true}}).toArray(function(err, result) {
        if (err) throw err;
        docs = result;
        console.log(result);
        callback();
    });
}
else if(data == "Physician"){
    db.collection('Clinics').find({Physician: {$exists : true}}).toArray(function(err, result) {
        if (err) throw err;
        docs = result;
        console.log(result);
        callback();
    });
}
else if(data == "Pediatrician"){
    db.collection('Clinics').find({Pediatrician: {$exists : true}}).toArray(function(err, result) {
        if (err) throw err;
        docs = result;
        console.log(result);
        callback();
    });
}
}
app.listen(port, function() {
	console.log('app running')
})
