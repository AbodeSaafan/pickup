var express = require("express");
var router = express.Router();
var requestHelper = require("../helpers/requestHelper");
var databaseHelper = require("../helpers/databaseHelper");
var tokenHelper = require("../helpers/tokenHelper");
var strings = require("./universal_strings");

/**
* @api {post} /login Log into the app
* @apiName Login
* @apiGroup Authorization
*
* @apiDescription API used to login and obtain a refresh token.
*
* @apiParam {String} email The email of the user
* @apiParam {String} password The password of the user
*
* @apiError error The error field has a string with an exact error
*
* @apiSuccessExample Success-Response:
*      HTTP/1.1 200 OK
* 	{
*    "token": "b43a545f90ec60bf5ed2a4bd45d81a711de7ba658faa6899d8240343b857664fc967a76cd622235313db8e2ec053fe34c26c",
*    "user_id": "240"
*	}
*
* @apiExample Example call::
*   {
*     "email": "6209be52@mail.com",
*     "password": "fa2568a8dd82c24a6ee22df3f19d642d"
*   }
*
* 
* @apiSampleRequest /api/login
*/
router.post("/", function(req, res){
	try{
		var user = requestHelper.validateAndCleanLoginRequest(req.body);
	}
	catch (err){
		res.status(400).json(requestHelper.jsonError(err)); return;
	}

	databaseHelper.checkPassword(user.email, user.password, (refreshToken, userId) => {
		if (refreshToken != null) {
			var newJwt = tokenHelper.createTokenForUser(userId, user.email);
			res.status(200).json({"token":refreshToken, "user_id":userId, "jwt_token":newJwt});
			return;
		} else {
			res.status(400).json({"error": strings.loginError});
			return;
		}
	});
});


module.exports = router;
