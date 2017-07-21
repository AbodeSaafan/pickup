var express = require('express');
var router = express.Router();
var body = require('body-parser');
var tokenHelper = require('../helpers/tokenHelper');
var requestHelper = require('../helpers/requestHelper');
var databaseHelper = require('../helpers/databaseHelper');
var md5 = require('md5');

router.post('/', function(req, res){
	console.log(req.body);

	requestHelper.validateRegisterRequest(req.body); // Validate
	databaseHelper.checkEmailUniqueness(req.body.email); // Email uniqueness

	var userId = generateUserId(req.body.username); // Generate user id

	databaseHelper.registerUser(userId, req.body); // Register the user in the db

	var token = tokenHelper.createTokenForUser(userId, req.body.username); // Auth token
	var vtoken = tokenHelper.verifyToken(token); // Verify token (debug)
	res.status(200); // Response code
	res.json({'token':token, 'verified token':vtoken}); // Response data
	console.log("GET /register has been processed"); // Helpful log message
});

function generateUserId(username){
	return md5(new Date().getUTCMilliseconds() + username);
}


module.exports = router;