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
var port = process.env.PORT || 8080;

var url = 'mongodb://ika:ikaika1@ds135993.mlab.com:35993/cs5551icp9';

app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.post('/add', function (req, res) {
    MongoClient.connect(process.env.MONGOLAB_URI || url, function(err, client) {
        if(err)
        {
            res.write("Failed, Error while connecting to Database");
            res.end();
        }
        var db = client.db("cs5551icp9");

        insertDocument(db, req.body, function() {
            res.write("Successfully inserted");
            res.end();
        });

    });
})
app.get('/search', function (req, res) {
    MongoClient.connect(process.env.MONGOLAB_URI || url, function(err, client) {
        if(err)
        {
            res.write("Failed, Error while connecting to Database");
            res.end();
        }
        var db = client.db("cs5551icp9");
        var majorS = req.query.plate;
        searchDocument(db,majorS,function () {
            if(resultF != "") {
                res.status(200).send(resultF);
            }
            else{
            }
            res.end();
        }  );
    });
})
var insertDocument = function(db, data, callback) {
    db.collection('Cars').insertOne( data, function(err, result) {
        if(err)
        {
            res.write("Registration Failed, Error While Registering");
            res.end();
        }
        console.log("Number of records inserted: " + res.insertedCount);
        callback();
    });
};
var searchDocument = function (db,data,callback) {
    db.collection('Cars').find({plate: data}).toArray(function(err, result) {
        if (err) throw err;
        resultF = result;
        console.log(result);
        callback();
    });
}
app.listen(port, function() {
	console.log('app running')
})

// var express = require('express')
// var app = express();
//
// var port = process.env.PORT || 8080;
//
// app.use(express.static(__dirname + '/public'));
//
// app.get('/', function(req, res) {
// 	res.render('index');
// })
//
// app.listen(port, function() {
// 	console.log('app running')
// })

/*

Open terminal and execute these commads starting
--->$ git init
--->$ git add .
--->$ heroku --version
--->$ heroku login
Enter your Heroku credentials:
    Email: rnd95@mail.umkc.edu
Password: *********
Logged in as rnd95@mail.umkc.edu
--->$ heroku local web
--->$ git add .
--->$ git commit -am "lab 10"
--->$ heroku create

 git push --set-upstream https://git.heroku.com/rocky-coast-70552.git master


 https://rocky-coast-70552.herokuapp.com/

*/