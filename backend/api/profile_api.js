var express = require("express");
var router = express.Router();
var tokenHelper = require("../helpers/tokenHelper");
var requestHelper = require("../helpers/requestHelper");
var databaseHelper = require("../helpers/databaseHelper");
var strings = require("./universal_strings");

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
* @apiSampleRequest /api/profile
*/
router.get("/", function (req, res) {
	try {
		var tok = tokenHelper.verifyToken(req.query.jwt);

		databaseHelper.getUserRowById(tok.user_id, (user) => {
			if(user) {
				res.status(200).json(user); return;
			} else{
				res.status(400).json({"error": strings.userIdFail}); return;
			}
		});
	} catch(err){
		res.status(400).json(requestHelper.jsonError(err)); return;
	}
});

/**
* @api {put} profile/ Update private profile
* @apiName UpdateAdminProfile
* @apiGroup Profiles
*
* @apiDescription API used to update your own administrative profile
*
* @apiParam {String} jwt The jwt of the current user.
* @apiParam {String} username The updated  username of the user (if updated)
* @apiParam {String} fname The updated first name of the user (if updated)
* @apiParam {String} lname The updated last name of the user (if updated)
* @apiParam {String} gender The updated gender of the user (F/M/O) (if updated)
* @apiParam {String} dob The updated date of birth of the user (MM/DD/YYYY) (if updated)
* @apiParam {String} email The updated email of the user (if updated)
*
* @apiError error The error field has a string with an exact error
*
* @apiSuccessExample Success-Response:
*      HTTP/1.1 200 OK
*   {
*     "user_id" : 1
*	  	"username" : "rads28"
*	  	"fname" : "radhika"
* 	  "lname" : "krishnan"
*	  	"dob" : 28/11/1996
* 	  "gender" : "F"
* 	  "email" : "rads28@gmail.com"
* 	}
*
* @apiExample Example call::
*   {
*     "jwt": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMjQwIiwiZW1haWwiOiJhZHNzYWRhQG1haWwuY29tIiwiaWF0IjoxNTA1MTU3NTA3LCJleHAiOjE1MDUxNTg0MDd9.r7h31S_wQTypjiSLh7TgeRZYnRNqJpCJCqUFoSUvxqI",
*			"username": "rads28",
*			"fname": "radhika",
*			"lname": "",
*			"dob": "",
*			"gender": ""
*			"email": "rads28@gmail.com"
*   }
*
*
* @apiSampleRequest /api/profile
*/

router.put('/', function (req, res) {

	try {
		var tok = tokenHelper.verifyToken(req.body.jwt);

		var user_details = {
			user_id: parseInt(tok.user_id),
			username: null,
			password: null,
			fname: null,
			lname: null,
			gender: null,
			dob: null,
			email: null
		}

		for (var key in user_details) {
			if (key in req.body) {
				if (!checkNull(req.body[key])) {
					user_details[key] = req.body[key]
				}
			}
		}

		var details = requestHelper.validateAndCleanUpdateAdminRequest(user_details)

		databaseHelper.updateUser(details.user_id, details.username, details.fname, details.lname, details.gender, details.dob, details.email, (update) => {
			if (update) {
					databaseHelper.getUserRowById(tok.user_id, (new_user_details) => {
						if (new_user_details) {
							res.status(200).json(new_user_details); return;
						}
						else {
							res.status(400).json({"error": strings.userIdFail}); return;
						}
					})
				}
					else {
						res.status(400).json({"error": strings.updateUserFailed}); return;
					}
		})

	} catch(err){
		res.status(400).json(requestHelper.jsonError(err)); return;
	}
});

module.exports = router;

function checkNull(str) {
	return (!str || 0 === str.length);
}
