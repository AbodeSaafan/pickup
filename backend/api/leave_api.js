var express = require('express');
var router = express.Router();
var requestHelper = require('../helpers/requestHelper');
var databaseHelper = require('../helpers/databaseHelper');

/////// Messages ///////
var loginError = "Username or password is not valid";
var loginSuccess = "Log in successful";


router.delete('/', function(req, res){
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
            databaseHelper.leaveGame(userId, gameId, (querySuccess) => {
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
