var express = require('express');
var router = express.Router();
var tokenHelper = require('../helpers/tokenHelper');
var requestHelper = require('../helpers/requestHelper');
var databaseHelper = require('../helpers/databaseHelper');
var strings = require('./universal_strings');

/**
* @api {get} /extendedProfile Get User's extendedProfile
* @apiName getExtendedProfile
* @apiGroup Profiles
*
* @apiDescription API used for getting a user's extended Profile.
*
* @apiParam {String} jwt The JWT you have currently
*
* @apiSuccess {String} User's extendedProfile entry (userId, skill_level, age, gender, location)
*
* @apiError error An error is given for invalid user_ID
*
* @apiExample Example call:
*     {
*       "jwt": Encrypted_JWT_Token,
*     }
*
* @apiSuccessExample Success-Response:
*      HTTP/1.1 200 OK
*		{
*		  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFkc3NhZGFAbWFpbC5jb20iLCJpYXQiOjE1MDUxNTc2NjQsImV4cCI6MTUwNTE1ODU2NH0.HmhW4y-AZ1D5rMHbQ8RY0eBIGfo-8Lb_sFL1FrruFoc"
*		}
*
* @apiSampleRequest /api/extendedProfile
*/

router.get('/', function (req, res) {
	try {
		var tok = tokenHelper.verifyToken(req.query.jwt);

		databaseHelper.getExtendedProfile(tok.user_id, (user_id) => {
			if(user_id) {
				res.status(200).json(user_id); return;
			}else{
				res.status(400).json({'error': strings.userIdFail}); return;
			}
		})
	}

	catch(err){
		res.status(400).json(requestHelper.jsonError(err)); return;
	}
});

/**
* @api {put} /refresh Update User's Extended Profile
* @apiName updateExtendedUser
* @apiGroup Profiles
*
* @apiDescription API used for updating user's extended profile.
*
* @apiParam {String} jwt The JWT you have currently
* @apiParam {String} skill_level The skill level of the user
* @apiParam {String} location The location of the user
*
* @apiSuccess {String} token A JWT token that can be used to authenticate futher API calls
*
* @apiError error The error field has a string with an exact error
*
* @apiExample Example call:
*     {
*       "jwt": Encrypted_JWT_Token,
		"skill_level": 3,
		"location": Mississauga
*     }
*
* @apiSuccessExample Success-Response:
*      HTTP/1.1 200 OK
*		{
*		  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFkc3NhZGFAbWFpbC5jb20iLCJpYXQiOjE1MDUxNTc2NjQsImV4cCI6MTUwNTE1ODU2NH0.HmhW4y-AZ1D5rMHbQ8RY0eBIGfo-8Lb_sFL1FrruFoc"
*		}
*
* @apiSampleRequest /api/extendedProfile
*/

router.put('/', function (req, res) {
 try {
	 var details = requestHelper.validateAndCleanUpdateExtendedProfileRequest(req.body);

	 console.log(details)

	 try {
 	  var tok = tokenHelper.verifyToken(req.body.jwt);
	} catch(err) {
 	  res.status(400).json(requestHelper.jsonError(err)); return;
 	}


	var userId = tok.user_id;
	console.log(userId)
	var skill_level = details.skill_level;
	var location = details.location;

	databaseHelper.getExtendedProfile(userId, (user_id) => {
		if(user_id) {
			databaseHelper.updateExtendedUser(userId, skill_level, location, (update) => {
				if (update) {
					res.status(200).json(update); return;
				} else {
					console.log('reached here');
					res.status(400).json({'error': strings.userIdFail}); return;
				}
			})
		}else{
			console.log('reached here1');
			res.status(400).json({'error': strings.userIdFail}); return;
		}
	})

 } catch (err) {
	 console.log('reached here2');
	 res.status(400).json(requestHelper.jsonError(err)); return;
 }
});

module.exports = router;
