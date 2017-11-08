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
* @apiDescription API used for creating games. Games must not conflict with previous games the user has already created. Valid options for enforced_params are: gender, age
*
* @apiParam {string} jwt Valid JWT
* @apiParam {string} name The name of the game you are creating
* @apiParam {string} type The type of the game you are creating (Serious, casual, ..)
* @apiParam {int} skill_offset The intended skill offset range for this game (0-10)
* @apiParam {int} total_players The total required players for the game
* @apiParam {int} start_time The time the game starts (in unix epoch time)
* @apiParam {int} duration The duration of the game (in seconds as an int)
* @apiParam {point} location The location of the game represented in location point object (lat/lng)
* @apiParam {string} location_notes how to get into the court
* @apiParam {string} description Short description for the game (less than 250 characters)
* @apiParam {string} gender The preferred for the game (if any)
* @apiParam {int[]} age_range The preferred age range for the game (if any)
* @apiParam {string[]]} enforced_params List of parmeters that the creator wants to enforce
*
*
* @apiSuccess {int} gameId The id of the game that has been created
*
* @apiError error The error field has a string with an exact error
*
* @apiExample Example call:
*     {
*       "jwt": Encrypted_JWT_Token,
*       "name": "abode's game",
*       "type": "casual",
*       "skill": 5,
*       "total_players_required": 6,
*       "start_time": 1504272395,
*       "duration": 5400,
*       "location": {lat: 500.50, lng:500.50},
*       "location_notes": "Come around the back and knock on the blue door",
*       "description": "Casual basketball game",
*       "gender": "A",
*       "age_range": "[20, 30]",
*       "enforced_params": ["gender", "age"]
*     }
*
* @apiSuccessExample Success-Response:
* HTTP/1.1 200 OK
* {
*   "game_id": 12
* }
*
* @apiSampleRequest /api/games
*/
router.post('/', function(req, res){
	try{
		var game = requestHelper.validateAndCleanCreateGameRequest(req.body);
		var tok = tokenHelper.verifyToken(req.body.jwt);

		databaseHelper.ensureGameIsValidToBeCreated(game, tok.user_id, (valid) => {
			if(!valid){
				res.status(400).json({'error': strings.invalidGameScheduleConflict});
			} else {
				databaseHelper.getUserSkilllevel(tok.user_id, (userSkill) => {
					var minSkill = ((userSkill - game.skill_offset) < 0) ? 0 : userSkill-game.skill_offset;
					var maxSkill = ((userSkill + game.skill_offset) > 10) ? 10 : userSkill+game.skill_offset;

					databaseHelper.createGame(tok.user_id, game.name, game.type, minSkill, maxSkill,
						game.total_players_required, game.start_time,
						game.duration, game.location, game.location_notes,
						game.description, game.gender, game.age_range, game.enforced_params,
						(game_id) => {
							if(game_id){
								databaseHelper.addGamer(tok.user_id, game_id, (joinSuccess) => {
									if (joinSuccess) {
										res.status(200).json({'game_id': game_id});
										return;
									} else {
										res.status(505).json({'error': strings.problemWithGameCreation});
									}
								});
							} else {
								res.status(400).json({'error': strings.invalidGameCreation});
							}
						});
				})
			}
		});
	}
	catch (err){
		res.status(400).json(requestHelper.jsonError(err)); return;
	}
});

/**
* @api {get} /games/getUsers Get users of a game
* @apiName Get game
* @apiGroup Games
*
* @apiDescription API used for getting the user of a game and if you have a reviewed them before. Game id has to correspond to a game actually created.
*
* @apiParam {int} id of the game
*
* @apiError error The error field has a string with an exact error
*
* @apiSuccessExample Success-Response:
*      HTTP/1.1 200 OK
*     {
*       "user_id":[ "1", "2", "3" ]
*		"ifReviewed":[ "1", "0", "0" ]
*      }
* @apiExample Example call::
*   {
*     "game_id": "1",
*     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMjQwIiwiZW1haWwiOiJhZHNzYWRhQG1haWwuY29tIiwiaWF0IjoxNTA1MTU3NTA3LCJleHAiOjE1MDUxNTg0MDd9.r7h31S_wQTypjiSLh7TgeRZYnRNqJpCJCqUFoSUvxqI"
*   }
*
* @apiSampleRequest /api/games/getUsers
*/
 router.get('/getUsers', function(req, res){
	var gameid = req.query.game_id;
	try {
		var tok = tokenHelper.verifyToken(req.query.jwt);
      databaseHelper.getUsers(gameid , (userids) => {
      	if(userids) {
      		databaseHelper.getIfReviewed(tok.user_id, userids, (ifReviewed)=>{
				if(ifReviewed){
						res.status(200).json(userids, ifReviewed);
					}
				else{
					res.status(400).json("Getting the review status failed.")
				}
			});
      	}else{
      		res.status(400).json({'error': strings.usersFail});
      		return;
      	}
      })
    }
    catch(err){
	 res.status(400).json({'error': strings.invalidJwt});
	 return;
   }
 });


/**
* @api {put} games/:GAMEID/join/ Join a game
* @apiName JoinGame
* @apiGroup Games
*
* @apiDescription API used to join a game.
*
* @apiParam {int} game_id The id of the game.
* @apiParam {string} jwt Valid JWT
*
* @apiError error The error field has a string with an exact error
*
* @apiSuccessExample Success-Response:
*      HTTP/1.1 200 OK
*   {
*    "game_id": "1"
* 	}
*
* @apiExample Example call::
*   {
*     "game_id": "1",
*     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMjQwIiwiZW1haWwiOiJhZHNzYWRhQG1haWwuY29tIiwiaWF0IjoxNTA1MTU3NTA3LCJleHAiOjE1MDUxNTg0MDd9.r7h31S_wQTypjiSLh7TgeRZYnRNqJpCJCqUFoSUvxqI"
*   }
*
*
* @apiSampleRequest /api/games/:GAMEID/join/
*/
router.put('/:game_id/join', function(req, res){
	try {
		var game = requestHelper.validateAndCleanJoinRequest(req.params);
		var gameId = game.game_id;
		var tok = tokenHelper.verifyToken(req.query.jwt);
		var userId = tok.user_id;

		databaseHelper.verifyGameId(gameId, (gameExists) => {
			if (gameExists) {
				databaseHelper.ensureGameIsJoinableByPlayer(gameId, userId, (joinable) => {
					if (joinable) {
						databaseHelper.addGamer(userId, gameId, (playerAdded) => {
							if (playerAdded) {
								res.status(200).json({'game_id': gameId}); return;
							} else {
								res.status(400).json({'error': strings.gamerNotAdded}); return;
							}
						})
					} else {
						res.status(400).json({'error': strings.cannotJoinGame}); return;
					}
				});
			} else {
				res.status(400).json({'error': strings.invalidGame}); return;
			}
		})
	} catch (err){
		res.status(400).json(requestHelper.jsonError(err)); return;
	}
});


/**
* @api {delete} games/:GAMEID/leave/ Leave a game
* @apiName LeaveGame
* @apiGroup Games
*
* @apiDescription API used to leave a game.
*
* @apiParam {int} game_id The id of the game.
* @apiParam {String} jwt The jwt of the current user.
*
* @apiError error The error field has a string with an exact error
*
* @apiSuccessExample Success-Response:
*      HTTP/1.1 200 OK
*   {
* 	}
*
* @apiExample Example call::
*   {
*     "game_id": "1",
*     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMjQwIiwiZW1haWwiOiJhZHNzYWRhQG1haWwuY29tIiwiaWF0IjoxNTA1MTU3NTA3LCJleHAiOjE1MDUxNTg0MDd9.r7h31S_wQTypjiSLh7TgeRZYnRNqJpCJCqUFoSUvxqI"
*   }
*
*
* @apiSampleRequest /api/games/:GAMEID/leave/
*/

router.delete('/:game_id/leave', function(req, res){
	try{
		var tok = tokenHelper.verifyToken(req.query.jwt);

		var game = requestHelper.validateAndCleanLeaveRequest(req.params);
		var gameId = game.game_id;

		databaseHelper.verifyGameId(gameId, (gameExists) => {
			if (gameExists) {
				databaseHelper.leaveGame(tok.user_id, gameId, (hasLeftGame) => {
					if (hasLeftGame) {
						res.status(200).json(); return;
					}
					else {
						res.status(400).json({'error': strings.invalidLeaveGame}); return;
					}
				})
			}
		})
	}
	catch(err){
		res.status(400).json(requestHelper.jsonError(err)); return;
	}
});

module.exports = router;