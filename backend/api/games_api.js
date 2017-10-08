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
    try {
      var tok = tokenHelper.verifyToken(req.body.jwt);  
    }
    catch(err) {
      res.status(400).json(requestHelper.jsonError(err)); return;
    }
    
    databaseHelper.ensureGameIsValidToBeCreated(game, tok.user_id, (valid) => {
      if(!valid){
        res.status(400).json({'error': strings.invalidGameScheduleConflict});
      } else {
        databaseHelper.createGame(tok.user_id, game.name, game.type, game.skill_offset, 
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
      }
    });
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
*       "user_id":[ "1", "2", "3" ]
*      }
 *
 * @apiSampleRequest /api/games/:123
 */
 router.get('/:game_id', function(req, res){
 	var gameid = req.params.game_id;
 	try {

      //	tokenHelper.verifyToken(req.headers.token);
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


/**
* @api {put} games/:GAMEID/join/ Join a game
* @apiName JoinGame
* @apiGroup Games
*
* @apiDescription API used to join a game.
*
* @apiParam {int} game_id The id of the game.
*
* @apiError error The error field has a string with an exact error
*
* @apiSuccessExample Success-Response:
*      HTTP/1.1 200 OK
*   {
*    "token": "b43a545f90ec60bf5ed2a4bd45d81a711de7ba658faa6899d8240343b857664fc967a76cd622235313db8e2ec053fe34c26c"
* }
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
    requestHelper.validateAndCleanJoinRequest(req);
    var gameId = req.params.game_id;
    var token = req.query.jwt;
    var tok = tokenHelper.verifyToken(token);
    var userId = tok.user_id;
    } catch (err){
        res.status(400).json(requestHelper.jsonError(err)); return;
    }

    databaseHelper.verifyGameId(gameId, (gameExists) => {
    if (gameExists) {
        databaseHelper.ensureGameIsJoinableByPlayer(gameId, userId, (joinable) => {
            if (joinable) {
                databaseHelper.addGamer(userId, gameId, (playerAdded) => {
                    if (playerAdded) {
                        var num_players = 0;
                        databaseHelper.updateGame(gameId, num_players, (gameUpdated) => {
                            if (gameUpdated) {
                                res.status(200).json({'token': token, 'game_id': gameId});
                            } else {
                                res.status(400).json({'error': "strings.errorname"});
                            }
                        });
                    } else {
                        res.status(400).json({'error': "strings.errorname"});
                    }
                })

            } else {
                res.status(400).json({'error': "strings.errorname"});
            }
        });
    } else {
        res.status(400).json({'error': "strings.errorname"});
    }
  })
});

router.delete('/:game_id/leave', function(req, res){
  try{
    var user = requestHelper.validateAndCleanJoinRequest(req.body);
  }
  catch (err){
    res.status(400).json(requestHelper.jsonError(err)); return;
  }

  var gameId = req.params.game_id;
  var token = req.query.jwt;
  print(token);
  tokenHelper.verifyToken(token);

  try{
    var userId = tokenHelper.getUserFromToken(token).user_id;
  } catch(err){
    res.status(400).json(requestHelper.jsonError(err)); return;
  }

  databaseHelper.verifyGameId(userId, gameId, (gameExists) => {
    if (gameExists) {
      databaseHelper.leaveGame(userId, gameId, (querySuccess) => {
        if (querySuccess) {
          res.status(200).json({'token': token, 'game_id': gameId});
          return;
        }
      })
    }
    res.status(400).json({'error': "strings.error"});
    return;
  })
});


module.exports = router;