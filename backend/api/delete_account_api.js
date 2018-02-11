var express = require("express");
var router = express.Router();
var requestHelper = require("../helpers/requestHelper");
var databaseHelper = require("../helpers/databaseHelper");
var tokenHelper = require("../helpers/tokenHelper");
var strings = require("./universal_strings");

/**
* @api {delete} /delete Delete account from pickup
* @apiName delete_account
* @apiGroup Account
*
* @apiDescription API used to delete your pickup account
*
* @apiParam {String} jwt User's jwt
* @apiParam {String} password Password of the user
*
* @apiError error The error field has a string with an exact error
*
* @apiSuccessExample Success-Response:
*      HTTP/1.1 200 OK
* 	{
*	}
*
* @apiExample Example call::
*   {
*     "jwt": "b43a545f90ec60bf5ed2a4bd45d81a711de7ba658faa6899d8240343b857664fc967a76cd622235313db8e2ec053fe34c26c",
*     "password": "coolcars23"
*   }
*
* 
* @apiSampleRequest /api/delete
*/
router.delete("/", function(req, res){
	try{
		var tok = tokenHelper.verifyToken(req.query.jwt);
		var user = requestHelper.validateAndCleanDeleteAccountRequest(req.query);

		// Verify password
		databaseHelper.checkPassword(tok.user_id, user.password, (valid) => {
			if (valid) {
			// User verified, ready to delete
				databaseHelper.disableAccount(tok.user_id, (success) => {
					if(success){
						res.status(200).json(); return;
					} else {
						res.status(400).json({"error": strings.deleteFailed}); return; 
					}	
				});
			} else {
				res.status(400).json({"error": strings.loginError}); return;
			}
		});
	}
	catch (err){
		res.status(400).json(requestHelper.jsonError(err)); return;
	}
});


module.exports = router;
