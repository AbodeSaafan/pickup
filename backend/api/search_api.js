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
* @apiParam {string} jwt Valid JWT
* @apiParam {string} search_object The object you are searching for (game, user)
* @apiParam {int} results_max The maximum number of results you want back (default is 20)
// Game params
* @apiParam {string} game_id The id of the game
* @apiParam {string} game_name The name of the game
* @apiParam {string} game_type The type of the game
* @apiParam {int} game_skill_min The minimum skill of the game
* @apiParam {int} game_skill_max The maximum skill of the game
* @apiParam {int} game_total_players The total players of the game 
* @apiParam {int} game_start_time The time the game starts
* @apiParam {int} game_duration The duration of the game
* @apiParam {point} game_location The location of the game represented in location point object (lat/lng)
* @apiParam {int} game_location_range The range of location in KM
// User params
* @apiParam {string} username The username of the user you are looking for
*
* @apiSuccess {list} results List of results as json objects
*
* @apiError error The error field has a string with an exact error
* 
* @apiExample Example call:
*     {
*       "jwt": Encrypted_JWT_Token,
*       "search_object": "user",
*       "username": "abode25"
*     }
*
* @apiExample Example call:
*     {
*       "jwt": Encrypted_JWT_Token,
*       "search_object": "game",
*       "game_start_time": 1506996322
*       "game_type" : "serious"
*     }
*
* @apiSuccessExample Success-Response:
* HTTP/1.1 200 OK
* {
*   [
*       {
*       "search_object" : "game",
*       "name": "abode's game",
*       "type": "casual",
*       "skill_min": 5,
*       "skill_max": 7,
*       "total_players_required": 6,
*       "start_time": 1504272395,
*       "duration": 5400,
*       "location": {lat: 500.50, lng:500.50},
*       "location_notes": "Come around the back and knock on the blue door",
*       "description": "Casual basketball game",
*       "gender": "A",
*       "age_range": "[20, 30]",
*       "enforced_params": ["gender", "age"]
*       },
*       {
*       "search_object" : "game",
*       "name": "abode's game pt II",
*       "type": "serious",
*       "skill_min": 5,
*       "skill_max": 10,
*       "total_players_required": 10,
*       "start_time": 1504276395,
*       "duration": 5410,
*       "location": {lat: 520.50, lng:509.50},
*       "location_notes": "Come around the back and knock on the red door",
*       "description": "Casual basketball game pt II",
*       "gender": "A",
*       "age_range": "[20, 35]",
*       "enforced_params": ["age"]
*       }
*   ]
* }
*
* @apiSampleRequest /api/search
*/


router.get('/', function(req, res){
	// TODO Filter out "invalid" games that the player can not play
	try{
		var tok = tokenHelper.verifyToken(req.body.jwt);  
		var search_request = requestHelper.validateAndCleanSearchRequest(req.body);

		databaseHelper.searchObjects(search_request, (results) => {
			if(data.search_object == 'game'){
			// Loop through games returned and ensure player can join them

				ensureGameIsJoinableByPlayer
			}	
		});

		

	}
	catch (err){
		res.status(400).json(requestHelper.jsonError(err)); return;
	}
});

module.exports = router;