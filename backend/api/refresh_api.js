var express = require('express');
var router = express.Router();
var databaseHelper = require('../helpers/databaseHelper');
var tokenHelper = require('../helpers/tokenHelper');
var requestHelper = require('../helpers/requestHelper');
var md5 = require('md5');
var crypto = require('crypto');
var strings = require('./universal_strings');

/**
* @api {get} /refresh Refresh your JWT token
* @apiName RefreshToken
* @apiGroup Authorization
*
* @apiParam {String} jwt The JWT you have currently 
* @apiParam {String} refresh The refresh token you were given at signup
*
* @apiSuccess {String} token A JWT token that can be used to authenticate futher API calls
*
* @apiError error The error field has a string with an exact error
*
* @apiSuccessExample Success-Response:
*      HTTP/1.1 200 OK
*     {
*       "jwt": Encrypted_JWT_Token,
*       "refresh": RefreshToken
*     }
*
* @apiSampleRequest /api/refresh
*/
router.get('/', function(req, res){
	if(!(req.query.jwt && req.query.refresh)){
		res.status(400).json({'error': "Please provide your current JWT and refresh tokens"}); return;
	}

	try{
		var user = tokenHelper.getUserFromToken(req.query.jwt);
	} catch(err){
		res.status(400).json(requestHelper.jsonError(err)); return;
	}
	
	databaseHelper.getRefreshToken(user.user_id, req.query.refresh, (success) => {
		if(!success){
			console.log("No refresh token found");
			res.status(400).json({'error' : strings.noRefreshToken}); return;
		}

		var newToken = tokenHelper.createTokenForUser(user.userId, user.email);

		res.status(200).json({'token':newToken, 'user_id':user.userId}); return;

	});
});

/**
* @api {delete} /refresh Delete (revoke) a refresh token
* @apiName RefreshToken Delete
* @apiGroup Authorization
*
* @apiParam {String} jwt The JWT you have currently 
* @apiParam {String} refresh The refresh token you want revoked
*
*
* @apiError error The error field has a string with an exact error
*
* @apiSuccessExample Success-Response:
*      HTTP/1.1 200 OK
*     {
*       "jwt": Encrypted_JWT_Token,
*       "refresh": RefreshToken
*     }
*
* @apiSampleRequest /api/refresh
*/
router.delete('/', function(req, res){
	if(!(req.body.jwt && req.body.refresh)){
		res.status(400).json({'error': "Please provide your current JWT and the refresh token you want revoked"}); return;
	}

	try{
		var user = tokenHelper.getUserFromToken(req.body.jwt);
	} catch(err){
		res.status(400).json(requestHelper.jsonError(err)); return;
	}
	
	databaseHelper.deleteRefreshToken(user.user_id, req.body.refresh, (success) => {
		if(!success){
			console.log("No refresh token found");
			res.status(400).json({'error' : "The refresh token you want to delete does not exist"}); return;
		} else{
			res.status(200).json({'status': "Successful refresh token delete"}); return;	
		}		
	});
});



module.exports = router;