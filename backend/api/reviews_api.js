var express = require('express');
var router = express.Router();
var requestHelper = require('../helpers/requestHelper');
var databaseHelper = require('../helpers/databaseHelper');


router.post('/setReview', function(req, res){
	try {

    //  tokenHelper.verifyToken(req.headers.token);
}
catch(err){

	res.status(400).json({'error': strings.invalidJwt});
	return;
}




databaseHelper.addReview(userId, gameId, reviewerId, rating, tags, (success) => {
	if(success) {
		res.status(200).json("Review added succesfully.");
		return;
	}else{
		res.status(400).json({'error': strings.userIdFail});
		return;
	}
})




});

module.exports = router;