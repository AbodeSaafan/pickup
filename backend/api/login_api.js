var express = require('express');
var router = express.Router();

router.get('/', function(req, res){
	// Access to parameters is done through req.query (GET and PUT)
	// Access to parameters is done through req.body (POST and DELETE)
	
	// res.status(X); // Response code
	// res.json(X); // Response data if any
	console.log("GET /login has been processed"); // Helpful log message
});


module.exports = router;