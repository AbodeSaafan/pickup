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
var userIdFail = "Failed to retrieve user id";

router.post('/', function(req, res){
	
	var user = requestHelper.validateAndCleanRegisterRequest(req.body);

	databaseHelper.checkEmailUniqueness(user, (emailValid) => {
		if(!emailValid){
			console.log(emailError);
			res.status(400).json({'error': emailError});
		} else {
			user.salt = generateSalt();
			user.hashedPassword = md5(user.salt + user.password);
	
			databaseHelper.registerUser(user, (registerSuccess) => {
				if(registerSuccess){
					var token = tokenHelper.createTokenForUser(user.userId, user.email); // Auth token
					// var vtoken = tokenHelper.verifyToken(token); // Verify token (debug)
					databaseHelper.getUserId(user.email, (userId) => {
						if(userId){
							res.status(200).json({'token':token, 'user_id':userId});		
						} else {
							res.status(400).json({'error': userIdFail });
							console.log(userIdFail);
						}
					});
				} else {
					res.status(400).json({'error': registerError });
					console.log(registerError);
				}
			});
			console.log("GET /register has been processed");
		}
	});
});

/////// Register API helpers ///////

function generateSalt(){
	return crypto.randomBytes(40).toString('hex');
}

module.exports = router;