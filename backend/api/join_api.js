var express = require('express');
var router = express.Router();
var requestHelper = require('../helpers/requestHelper');
var databaseHelper = require('../helpers/databaseHelper');

/////// Messages ///////
var loginError = "Username or password is not valid";
var loginSuccess = "Log in successful";

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
* 	{
*    "token": "b43a545f90ec60bf5ed2a4bd45d81a711de7ba658faa6899d8240343b857664fc967a76cd622235313db8e2ec053fe34c26c"
*	}
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
router.put('/', function(req, res){
	// Access to parameters is done through req.query (GET and PUT)
    try{
        var user = requestHelper.validateAndCleanJoinRequest(req.body);
    }
    catch (err){
        res.status(400).json(requestHelper.jsonError(err)); return;
    }

    var gameId = user.game_id;
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
            databaseHelper.addGamer(gameId, (querySuccess) => {
                if (querySuccess) {
                    res.status(200).json({'token': token, 'game_id': gameId});
                    return;
                }
            })
        }
        res.status(400).json({'error': loginError});
        return;
    })
});

module.exports = router;
