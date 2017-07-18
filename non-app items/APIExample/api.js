var express = require('express');
var router = express.Router();

router.get('/hello', function(req, res){
	console.log("GET hello request has been requested");
	// Access to parameters is done through req.query (GET and PUT)
	if (req.query.name) {
		var reply = "Hello " + req.query.name + "!";
	} else {
		reply = "Hello World!";
	}
	res.status(222); // Random response codes, do not stand for anything
	res.json({'repsonse':reply});
	console.log("GET hello request has been processed");
});

router.post('/hello', function(req, res){
	// Access to parameters is done through req.query (POST and DELETE)
	if (req.body.name) {
		var reply = "Hello " + req.body.name + "!";
	} else {
		reply = "Hello World!";
	}
	res.status(223);
	res.json({'repsonse':reply});
	console.log("POST hello request has been processed");
});

router.get('/', function(req, res){
	res.sendFile(path.join(__dirname + '/index.html'));
});


module.exports = router;