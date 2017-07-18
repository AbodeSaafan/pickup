var express = require('express');
var path = require('path');
var http = require('http');
var bodyParse = require('body-parser');
var api = require('./api');

var app = express();

app.use(bodyParse.urlencoded({extended: true}));
app.use(bodyParse.json());

app.use(function(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
  next();
});

app.use(express.static(path.join(__dirname, 'public')));
//app.use(express.static(path.join(__dirname,"")));
//console.log(path.join(__dirname,""));
app.use('/api', api);

var port = process.env.PORT || 3000;

app.set('port',port);

var server = http.createServer(app);

app.listen(port, function(){
	console.log("Server started on port: "+port);
});