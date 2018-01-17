var express = require("express");
var router = express.Router();
var requestHelper = require("../helpers/requestHelper");
var databaseHelper = require("../helpers/databaseHelper");
var tokenHelper = require("../helpers/tokenHelper");
var strings = require("./universal_strings");

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
* @apiParam {bool} reviewed already or not
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
*     "reviewed": "true",
*     "jwt": Encrypted_JWT_Token
*   }
*
* @apiSampleRequest /api/reviews/setReview
*/
router.post("/setReview", function(req, res){
	try{
		var review = requestHelper.validateAndCleanReviewRequest(req.body);
		try {
			var tok = tokenHelper.verifyToken(req.body.jwt);
		}
		catch(err){
			res.status(400).json({"error": strings.invalidJwt});
			return;
		}

		if(review.reviewed){
			databaseHelper.updateReview(review.userId, review.gameId, tok.reviewerId, review.rating, (reviewId) => {
				if(reviewId) {
					requestHelper.updateTag(reviewId, review.tags, (anyFailure) => {
						if(anyFailure){
							res.status(400).json("Adding tags failed");
							return;
						}
						else{
							res.status(200).json("Review added succesfully.");
							return;
						}
					});
				}else{
					res.status(400).json("Adding review failed");
					return;
				}
			});	
		}
		else{
			databaseHelper.addReview(review.userId, review.gameId, tok.reviewerId, review.rating, (reviewId) => {
				if(reviewId) {
					requestHelper.addTag(reviewId, review.tags, (anyFailure) => {
						if(anyFailure){
							res.status(400).json("Adding tags failed");
							return;
						}
						else{
							res.status(200).json("Review added succesfully.");
							return;
						}
					});
				}else{
					res.status(400).json("Adding review failed");
					return;
				}
			});	
		}
	}
	catch (err){
		res.status(400).json(requestHelper.jsonError(err)); return;
	}
});

module.exports = router;