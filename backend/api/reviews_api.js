var express = require('express');
var router = express.Router();
var requestHelper = require('../helpers/requestHelper');
var databaseHelper = require('../helpers/databaseHelper');


router.get('/:game_id', function(req, res){
	var gameid = req.params.game_id;
	try {

      	tokenHelper.verifyToken(req.headers.token);
      	databaseHelper.getUsers(gameid);


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