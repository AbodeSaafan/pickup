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
* @apiDescription API used to register for a new account.
*
* @apiParam {String} username The unique username of the user
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
* @apiExample Example call:
*     {
*       "username": "abode_5",
*       "fname": "Abode",
*       "lname": "Saafan",
*       "gender": "M",
*       "dob": "25/03/1996",
*       "email": "abodesaafan@hotmail.com",
*       "password": "password123"
*     }
*
* @apiSuccessExample Success-Response:
*    HTTP/1.1 200 OK
*    {
*   	"token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMjQwIiwiZW1haWwiOiJhZHNzYWRhQG1haWwuY29tIiwiaWF0IjoxNTA1MTU3NTA3LCJleHAiOjE1MDUxNTg0MDd9.r7h31S_wQTypjiSLh7TgeRZYnRNqJpCJCqUFoSUvxqI",
*    	"refresh": "21251e5cc6e6a667f109ccc6f295c1595bc98ecc7cf8733e788fe1aab0ea14eeaf81990bbceb97874a6c4e8a7f5851e1ee89",
*    	"user_id": "240"
* 	 }  
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
			res.status(400).json({'error': strings.uniqueEmailError}); return;
		}
		else {
			databaseHelper.checkUsernameUniqueness(user, (uniqueUsername) => {
				if(!uniqueUsername){
					res.status(400).json({'error': strings.uniqueUsernameError}); return;
				}
				else {
					user.salt = generateSalt();
					user.hashedPassword = md5(user.salt + user.password);

					databaseHelper.registerUser(user, (registerSuccess) => {
						if(!registerSuccess){
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
								res.status(400).json({'error': strings.createRefreshFail}); return;
							}
							res.status(200).json({'token':token, 'refresh':refreshToken, 'user_id':userId}); return;
						});
					} else {
						res.status(400).json({'error': strings.userIdFail }); return;	
					}
				});
					});
				}
			});
			
		}
	});
});

/////// Register API helpers ///////
function generateSalt(){
	return crypto.randomBytes(40).toString('hex');
}

module.exports = router;