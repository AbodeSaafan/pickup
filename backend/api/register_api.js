var express = require('express');
var router = express.Router();
var body = require('body-parser');
var tokenHelper = require('../helpers/tokenHelper');
var requestHelper = require('../helpers/requestHelper');
var databaseHelper = require('../helpers/databaseHelper');
var md5 = require('md5');
var crypto = require('crypto');

router.post('/', function(req, res){
	var user = req.body;

	requestHelper.validateRegisterRequest(user);
	databaseHelper.checkEmailUniqueness(user);

	user.userId = generateUserId(user.email);
	user.salt = generateSalt();
	user.hashedPassword = md5(user.salt + user.userId + user.password);
	
	databaseHelper.registerUser(user);

	var token = tokenHelper.createTokenForUser(user.userId, user.email); // Auth token
	// var vtoken = tokenHelper.verifyToken(token); // Verify token (debug)
	
	res.status(200);
	res.json({'token':token});

	console.log("GET /register has been processed");
});

function generateUserId(email){
	return md5(new Date().getUTCMilliseconds() + email);
}

function generateSalt(){
	return crypto.randomBytes(40).toString('hex');
}

module.exports = router;