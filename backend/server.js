var express = require("express");
var path = require("path");
var bodyParse = require("body-parser");

var api = require("./api/api");
var logger = require("./logger");

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
	logger.info(req.method + " " + req.url);
	logger.verbose(req.method + " " + req.url + ". Params: " + JSON.stringify(req.params) + ". Query: " + JSON.stringify(req.query) + ". Body: " + JSON.stringify(req.body));
	next();
});
app.use("/api", api);


app.listen(process.env.PORT || 3000, function(){
	logger.info("Server started on port: " + this.address().port);
});
