var express = require('express');
var router = express.Router();
var body = require('body-parser');
var tokenHelper = require('../helpers/tokenHelper');
var requestHelper = require('../helpers/requestHelper');
var databaseHelper = require('../helpers/databaseHelper');
var crypto = require('crypto');
var strings = require('./universal_strings');

/**
* @api {post} /friends Send a friend request
* @apiName SendFriendRequest
* @apiGroup Friends
*
* @apiDescription API used for sending friend requests.
*
* @apiParam {string} jwt Valid JWT
* @apiParam {int} user_Id The user, your sending friend request to
*
*
* @apiSuccess {string} status The status of the request should be 'invite' or 'friends'
*
* @apiError error The error field has a string with an exact error
*
* @apiExample Example call:
*     {
*       "jwt": Encrypted_JWT_Token,
*       "user_id": 3,
*     }
*
* @apiSuccessExample Success-Response:
* HTTP/1.1 200 OK
* {
*   "status": "invite"
* }
*
* @apiSampleRequest /api/friends
*/
router.post('/', function(req, res){

	try {
	  var tok = tokenHelper.verifyToken(req.body.jwt);
	}
	catch(err) {
	  res.status(400).json(requestHelper.jsonError(err)); return;
	}

/*TODO verify user_2's ID*/

	var friend = req.body.userID
	databaseHelper.sendFriendInvite(tok.user_id, friend, (inviteSuccess) => {
		if (inviteSuccess) {
			res.status(200).json({'status': 'invite'});
		} else {
			res.status(400).json({'error': strings.invalidLFriendInvite});
		}
	})
});


/**
* @api {put} /friends Block a user
* @apiName BlockFriend
* @apiGroup Friends
*
* @apiDescription API used for blocking users
*
* @apiParam {string} jwt Valid JWT
* @apiParam {int} user_ID The user whome you want to block.
*
*
* @apiSuccess {string} status The status of the request should be 'blocked'
*
* @apiError error The error field has a string with an exact error
*
* @apiExample Example call:
*     {
*       "jwt": Encrypted_JWT_Token,
*       "user_id": 3,
*     }
*
* @apiSuccessExample Success-Response:
* HTTP/1.1 200 OK
* {
*   "status": "blocked"
* }
*
* @apiSampleRequest /api/friends
*/

router.put('/', function (req, res) {
	try {
	  var tok = tokenHelper.verifyToken(req.body.jwt);
	}
	catch(err) {
	  res.status(400).json(requestHelper.jsonError(err)); return;
	}

	/*TODO verify user_2's ID*/
  var friend = req.body.userID

})

/**
* @api {delete} /friends Decline a friend request, cancel a friend request, or delete a friend
* @apiName DeclineFriend
* @apiGroup Friends
*
* @apiDescription API used for declining friend requests, cancelling them, or removing existing friends.
*
* @apiParam {string} jwt Valid JWT
* @apiParam {int} user_Id The user whome you want to remove/decline.
*
*
* @apiSuccess {string} status The status of the request should be 'declined', 'cancelled', or 'removed'
*
* @apiError error The error field has a string with an exact error
*
* @apiExample Example call:
*     {
*       "jwt": Encrypted_JWT_Token,
*       "user_id": 3,
*     }
*
* @apiSuccessExample Success-Response:
* HTTP/1.1 200 OK
* {
*   "status": "removed"
* }
*
* @apiSampleRequest /api/friends
*/



/**
* @api {put} /friends Accept a friend request
* @apiName AcceptFriend
* @apiGroup Friends
*
* @apiDescription API used for accepting friend requests.
*
* @apiParam {string} jwt Valid JWT
* @apiParam {int} user_Id The user whome you want to block.
*
*
* @apiSuccess {string} status The status of the request should be 'accepted'
*
* @apiError error The error field has a string with an exact error
*
* @apiExample Example call:
*     {
*       "jwt": Encrypted_JWT_Token,
*       "user_id": 3,
*     }
*
* @apiSuccessExample Success-Response:
* HTTP/1.1 200 OK
* {
*   "status": "accepted"
* }
*
* @apiSampleRequest /api/friends
*/

module.exports = router;
