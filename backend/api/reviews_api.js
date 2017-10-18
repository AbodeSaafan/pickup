var express = require('express');
var router = express.Router();
var requestHelper = require('../helpers/requestHelper');
var databaseHelper = require('../helpers/databaseHelper');


router.post('/setReview', function(req, res){
	try{
		var review = requestHelper.validateAndCleanReviewRequest(req.body);
		try {

			var tok =tokenHelper.verifyToken(req.body.jwt);
		}
		catch(err){

			res.status(400).json({'error': strings.invalidJwt});
			return;
		}

		databaseHelper.addReview(review.userId, review.gameId, tok.reviewerId, review.rating, review.tags, (success) => {
			if(success) {
				res.status(200).json("Review added succesfully.");
				return;
			}else{
				res.status(400).json("Adding review failed");
				return;
			}
		})
	}
catch (err){
	res.status(400).json(requestHelper.jsonError(err)); return;
}
});

module.exports = router;