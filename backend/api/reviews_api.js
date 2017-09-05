var express = require('express');
var router = express.Router();
var requestHelper = require('../helpers/requestHelper');
var databaseHelper = require('../helpers/databaseHelper');


router.post('/setReview', function(req, res){
	try {

      tokenHelper.verifyToken(req.headers.token);

	 }
	 catch(err){

       res.status(400).json({'error': strings.invalidJwt});
       return;
    }



	});

module.exports = router;