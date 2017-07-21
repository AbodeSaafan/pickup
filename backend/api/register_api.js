var express = require('express');
var router = express.Router();
var body = require('body-parser');
var tokenHelper = require('../helpers/tokenHelper');
var requestHelper = require('../helpers/requestHelper');

router.post('/', function(req, res){
	// Access to parameters is done through req.query (GET and PUT)
	// Access to parameters is done through req.body (POST and DELETE)
	requestHelper.validateRegisterRequest(req.body);

	var token = tokenHelper.createTokenForUser("abode");
	var vtoken = tokenHelper.verifyToken(token);
	res.status(200); // Response code
	res.json({'token':token, 'verified token':vtoken}); // Response data
	console.log("GET /register has been processed"); // Helpful log message
});


module.exports = router;