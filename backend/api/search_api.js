// Coming soon 
var express = require('express');
var router = express.Router();
var body = require('body-parser');
var tokenHelper = require('../helpers/tokenHelper');
var requestHelper = require('../helpers/requestHelper');
var databaseHelper = require('../helpers/databaseHelper');
var crypto = require('crypto');
var strings = require('./universal_strings');

/**
* @api {get} /search Search API
* @apiName Search
* @apiGroup Search
*
* @apiDescription API used searching such as searching for games 
*
* @apiParam {String} jwt Valid JWT
* // All search queries possible
* 	
* @apiSuccess {list} results List of results as json objects
*
* @apiError error The error field has a string with an exact error
* 
* @apiExample Example call:
*     {
*       "jwt": Encrypted_JWT_Token,
*     }
*
* @apiSuccessExample Success-Response:
* HTTP/1.1 200 OK
* {
	
* }
*
* @apiSampleRequest /api/search
*/


// TODO update the docs 

module.exports = router;