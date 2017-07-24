var express = require('express');
var router = express.Router();
var body = require('body-parser');
var tokenHelper = require('../helpers/tokenHelper');
var requestHelper = require('../helpers/requestHelper');
var databaseHelper = require('../helpers/databaseHelper');
var md5 = require('md5');
var crypto = require('crypto');

/////// Error messages ///////
var emailError = "Register failed: Email is not unique";
var registerError = "Failed to register user into the database";

router.post('/', function(req, res){

	var user = requestHelper.validateAndCleanRegisterRequest(req.body);
	databaseHelper.checkEmailUniqueness(user, (emailValid) => {
		if(!emailValid){
			console.log(emailError);
			res.status(400);
			res.json({'error': emailError });
		} else {
			user.salt = generateSalt();
			user.hashedPassword = md5(user.salt + user.password);
			user.password = ""; // Erase plain text password so we don't reuse by accident
	
			databaseHelper.registerUser(user, (callback) => {
				if(callback){
					var token = tokenHelper.createTokenForUser(user.userId, user.email); // Auth token
					// var vtoken = tokenHelper.verifyToken(token); // Verify token (debug)
					res.status(200);
					res.json({'token':token});
				} else {
					console.log(registerError);
					res.status(400);
					res.json({'error': registerError });
				}
			});
		}
		console.log("GET /register has been processed");
	});
});

function generateSalt(){
	return crypto.randomBytes(40).toString('hex');
}

module.exports = router;