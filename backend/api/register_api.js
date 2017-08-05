var express = require('express');
var router = express.Router();
var body = require('body-parser');
var tokenHelper = require('../helpers/tokenHelper');
var requestHelper = require('../helpers/requestHelper');
var databaseHelper = require('../helpers/databaseHelper');
var md5 = require('md5');
var crypto = require('crypto');
var strings = require('./universal_strings');

/**
* @api {post} /register Register a user account
* @apiName RegisterUser
* @apiGroup Authorization
*
* @apiParam {String} nickname The nickname of the user
* @apiParam {String} fname The first name of the user
* @apiParam {String} lname The last name of the user
* @apiParam {String} gender The gender of the user (F/M/O)
* @apiParam {String} dob The date of birth of the user (DD/MM/YYYY)
* @apiParam {String} email The email of the user
* @apiParam {String} password The password of the user
*
* @apiSuccess {String} token A JWT token that can be used to authenticate futher API calls
* @apiSuccess {String} user_id The id assigned to the user for unique identification
* @apiSuccess {String} refresh A refresh token that can be used to generate JWTs throught the API
*
* @apiError error The error field has a string with an exact error
*
* @apiSuccessExample Success-Response:
*      HTTP/1.1 200 OK
*     {
*       "nickname": "abode",
*       "fname": "Abode",
*       "lname": "Saafan",
*       "gender": "M",
*       "dob": "25/03/1996",
*       "email": "abodesaafan@hotmail.com",
*       "password": "password123"
*     }
*
* @apiSampleRequest /api/register
*/
router.post('/', function(req, res){
	try{
		var user = requestHelper.validateAndCleanRegisterRequest(req.body);	
	}
	catch (err){
		res.status(400).json(requestHelper.jsonError(err)); return;
	}
	

	databaseHelper.checkEmailUniqueness(user, (emailValid) => {
		if(!emailValid){
			console.log(strings.uniqueEmailError);
			res.status(400).json({'error': strings.uniqueEmailError}); return;
		}
		else {
			user.salt = generateSalt();
			user.hashedPassword = md5(user.salt + user.password);

			databaseHelper.registerUser(user, (registerSuccess) => {
				if(!registerSuccess){
					console.log(strings.registerFailError);
					res.status(400).json({'error': strings.registerFailError}); return;
				}

				databaseHelper.getUserId(user.email, (userId) => {
					if(userId){
						user.userId = userId;
						databaseHelper.populateExtendedProfile(user, (populateSuccess) => {
							if(!populateSuccess){
								res.status(400).json({'error': "Unable to populate extended profile database"}); return;
							}
						});

						var token = tokenHelper.createTokenForUser(userId, user.email); // Auth token

						databaseHelper.createRefreshToken(userId, (refreshToken) => {
							if(!refreshToken){
								console.log("token is " + refreshToken);
								res.status(400).json({'error': strings.createRefreshFail}); return;
							}
							res.status(200).json({'token':token, 'refresh':refreshToken, 'user_id':userId}); return;
						});
					} else {
						console.log(strings.userIdFail);
						res.status(400).json({'error': strings.userIdFail }); return;	
					}
				});
			});
		}
	});
});

/////// Register API helpers ///////
function generateSalt(){
	return crypto.randomBytes(40).toString('hex');
}

module.exports = router;