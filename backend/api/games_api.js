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
* @apiParam {int} skill The intended skill range for this game (x/100) (next week)
* @apiParam {int} total_players The total required players for the game
* @apiParam {int} start_time The time the game starts (in unix epoch time)
* @apiParam {int} duration The duration of the game (in seconds as an int)
* @apiParam {location} location The location of the game represented in location object (lat/lng)
* @apiParam {location_notes} string how to get into the court
* @apiParam {String} description Short description for the game (less than 250 characters)
* @apiParam {String} gender The preferred for the game (if any)
* @apiParam {list} age_range The preferred age range for the game (if any)
* @apiParam {list} enforced_params List of parmeters that the creator wants to enforce
* valid options for enforced_params are: gender, age, skill
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
*       "skill": 5,
*       "total_players_required": 6,
*       "start_time": "1504272395",
*       "duration": "5400",
*       "location": {lat: 500.50, lng:500.50},
*		"location_notes": "Come around the back and knock on the blue door"
*       "description": "Casual basketball game",
*       "gender": "A",
*       "age_range": "[20, 30]",
*       "enforced_params": [skill, gender, age]
*     }
*
* @apiSampleRequest /api/games
*/
router.post('/', function(req, res){
	try{
		var game = requestHelper.validateAndCleanCreateGameRequest(req.body);	
	}
	catch (err){
		res.status(400).json(requestHelper.jsonError(err)); return;
	}
});

/**
 * @api {get} /games/:gameid Get users of a game
 * @apiName Get game
 * @apiGroup Games
 *
 * @apiParam {int} id of the game
 *
 * @apiError error The error field has a string with an exact error
 *
 * @apiSuccessExample Success-Response:
 *      HTTP/1.1 200 OK
 *     {
*       
*     }
 *
 * @apiSampleRequest /api/games/:123
 */
router.get('/:game_id', function(req, res){
	var gameid = req.params.game_id;
	try {

      	tokenHelper.verifyToken(req.headers.token);
      	databaseHelper.getUsers(gameid , (user_id) => {
          if(user_id) {
              console.log(user_id);
              res.status(200).json(user_id);
              return;
          }else{
              res.status(400).json({'error': strings.userIdFail});
              return;
          }
        })
	 }
	 catch(err){

       res.status(400).json({'error': strings.invalidJwt});
       return;
    }
    


	});

module.exports = router;