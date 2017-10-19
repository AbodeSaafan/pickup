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
* @api {get} profile/ Get private profile
* @apiName GetAdminProfile
* @apiGroup Profiles
*
* @apiDescription API used to get your own administrative profile 
*
* @apiParam {String} jwt The jwt of the current user.
*
* @apiError error The error field has a string with an exact error
*
* @apiSuccessExample Success-Response:
*      HTTP/1.1 200 OK
*   {
*     "user_id" : 1
*	  "username" : "abode1"
*	  "fname" : "abode"
* 	  "lname" : "saafan"
*	  "dob" : 01/01/1996
* 	  "gender" : "M"
* 	  "email" : "abode@mail.com"
* 	}
*
* @apiExample Example call::
*   {
*     "jwt": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMjQwIiwiZW1haWwiOiJhZHNzYWRhQG1haWwuY29tIiwiaWF0IjoxNTA1MTU3NTA3LCJleHAiOjE1MDUxNTg0MDd9.r7h31S_wQTypjiSLh7TgeRZYnRNqJpCJCqUFoSUvxqI"
*   }
*
*
* @apiSampleRequest /api/games/:GAMEID/leave/
*/
router.get('/', function (req, res) {
	try {
		var tok = tokenHelper.verifyToken(req.query.jwt);

		databaseHelper.getUserRowById(tok.user_id, (user) => {
			if(user) {
				res.status(200).json(user); return;
			} else{
				res.status(400).json({'error': strings.userIdFail}); return;
			}
		})
	} catch(err){
		res.status(400).json(requestHelper.jsonError(err)); return;
	}
});

/*router.post('/:user_id', function (req, res) {
	var user_id = req.params.user_id;

	try {
		var user = requestHelper.validateAndCleanUpdateRequest(req.body);
		user['user_id'] = user_id;
		console.log("updating user: "+user);
		tokenHelper.verifyToken(req.headers.token);
		databaseHelper.updateUser(user, (user_id) => {
			if(user_id) {
				console.log(user_id);
				res.status(200).json("user "+ user.user_id+" successfully updated");
				return;
			}else{
				res.status(400).json({'error': strings.userIdFail});
				return;
			}
		});
	}catch(err){
		res.status(400).json({'error': err});
		return;
	}
});
*/



module.exports = router;
