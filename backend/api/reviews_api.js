var express = require('express');
var router = express.Router();
var requestHelper = require('../helpers/requestHelper');
var databaseHelper = require('../helpers/databaseHelper');

/**
 * @api {get} /reviews/:gameid Get users of a game
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
*       "email": "6209be52@mail.com",
*       "password": "fa2568a8dd82c24a6ee22df3f19d642d"
*     }
 *
 * @apiSampleRequest /api/reviews/:123
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


router.post('/setReview', function(req, res){
	try {

      tokenHelper.verifyToken(req.headers.token);

	 }
	 catch(err){

       res.status(400).json({'error': strings.invalidJwt});
       return;
    }



	});