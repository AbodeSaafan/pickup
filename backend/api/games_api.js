var express = require('express');
var router = express.Router();
var body = require('body-parser');
var tokenHelper = require('../helpers/tokenHelper');
var requestHelper = require('../helpers/requestHelper');
var databaseHelper = require('../helpers/databaseHelper');
var crypto = require('crypto');
var strings = require('./universal_strings');

/**
* @api {post} /games Create a new game
* @apiName CreateGame
* @apiGroup Games
*
* @apiParam {String} name The name of the game you are creating
* @apiParam {String} type The type of the game you are creating (Serious, casual, ..)
* @apiParam {String} skill The intended skill range for this game (x/100 with toleration)
* @apiParam {int} total_players The total required players for the game
* @apiParam {date_time} start_time The time the game starts
* @apiParam {time} duration The duration of the game
* @apiParam {location} location The location of the game represented in location object (x/y)
* @apiParam {String} description Short description for the game (less than 250 characters)
* @apiParam {String} gender The preferred for the game (if any)
* @apiParam {String} age The preferred age range for the game (if any)
* @apiParam {list} enforced_params List of parmeters that the creator wants to enforce
* 
* @apiSuccess {String} gameId The id of the game that has been created
*
* @apiError error The error field has a string with an exact error
*
* @apiSuccessExample Success-Response:
*      HTTP/1.1 200 OK
*     {
*       "name": "abode's game",
*       "type": "casual",
*       "intended_skill": "50/100 +-10",
*       "total_players_required": 6,
*       "start_time": "25/03/2018 14:30:00 PM EST",
*       "duration": "01h30m00s",
*       "location": {x: 500, y:500},
*       "description": "Casual basketball game",
*       "gender": "A",
*       "age": "20 +-3",
*       "enforced_params": [skill, gender, age]
*     }
*
* @apiSampleRequest /api/games
*/
router.post('/', function(req, res){
	try{
		// var user = requestHelper.validateAndCleanCreateGameRequest(req.body);	
	}
	catch (err){
		res.status(400).json(requestHelper.jsonError(err)); return;
	}
});

module.exports = router;