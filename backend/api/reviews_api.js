var express = require('express');
var router = express.Router();
var requestHelper = require('../helpers/requestHelper');
var databaseHelper = require('../helpers/databaseHelper');


/**
* @api {get} /reviews/setReview Set the review of a player for a particular game.
* @apiName Set Review
* @apiGroup Reviews
*
* @apiDescription API used for setting the review of a user of a game.
*
* @apiParam {int} id of the game
* @apiParam {int} id of the user being reviewed
* @apiParam {int} rating of the user
* @apiParam {int} tags describing the user
* @apiParam {string} jwt Valid JWT
*
* @apiError error The error field has a string with an exact error
*
* @apiSuccessExample Success-Response:
*      HTTP/1.1 200 OK
*     {
*       "Review added succesfully.""
*      }
* @apiExample Example call::
*   {
*     "gameId": "1",
*     "userId": "1",
*     "rating": "1",
*     "tags": ["1", "2"],
*     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMjQwIiwiZW1haWwiOiJhZHNzYWRhQG1haWwuY29tIiwiaWF0IjoxNTA1MTU3NTA3LCJleHAiOjE1MDUxNTg0MDd9.r7h31S_wQTypjiSLh7TgeRZYnRNqJpCJCqUFoSUvxqI"
*   }
*
* @apiSampleRequest /api/reviews/setReview
*/
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

		databaseHelper.addReview(review.userId, review.gameId, tok.reviewerId, review.rating, (reviewId) => {
			if(reviewId) {
				databaseHelper.addTag(reviewId, review.tags, (success) => {
					if(success){
						res.status(200).json("Review added succesfully.");
						return;
					}
					else{
						res.status(400).json("Adding tags failed");
					}
				})
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