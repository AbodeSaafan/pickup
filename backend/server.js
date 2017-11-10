var express = require("express");
var path = require("path");
var bodyParse = require("body-parser");

var api = require("./api/api");

var app = express();

app.use(bodyParse.urlencoded({extended: true}));
app.use(bodyParse.json());

app.use(function(req, res, next) {
	res.header("Access-Control-Allow-Origin", "*");
	res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
	next();
});

app.use(express.static(path.join(__dirname, "apidoc")));

app.use(function(req, res, next) {
	// eslint-disable-next-line no-console
	console.log(req.method + " " + req.url);
	// console.log(req) // use only for debugging do not commit uncommented
	next();
});
app.use("/api", api);


app.listen(process.env.PORT || 3000, function(){
	// eslint-disable-next-line no-console
	console.log("Server started on port: " + this.address().port);
});
