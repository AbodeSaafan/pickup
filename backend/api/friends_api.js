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

	databaseHelper.checkIfFriendRequestExists(friend, tok.user_id, (EntryExists) => {
		if (EntryExists) {
			res.status(400).json({'error': strings.FriendRequestExists});
		} else {
			//successfully send the friend invite
			databaseHelper.sendFriendInvite(tok.user_id, friend, (inviteSuccess) => {
					if (inviteSuccess) {
						res.status(200).json({'status': 'invited'});
						return;
					} else {
						res.status(400).json({'error': strings.invalidLFriendInvite});
						return;
					}
			})
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

router.put('/block', function (req, res) {
	try {
	  var tok = tokenHelper.verifyToken(req.query.jwt);
	}
	catch(err) {
	  res.status(400).json(requestHelper.jsonError(err)); return;
	}

	/*TODO verify user_2's ID*/
  var friend = req.query.userID

	databaseHelper.checkFriendEntryValidationForBlock(tok.user_id, friend, (EntryExists) => {
		if (EntryExists == 'update') {
			//blocking a user after they send a friend request
			//block a friend
			databaseHelper.blockFriendUpdateEntry(tok.user_id, friend, (blockFriendSuccess) => {
				if (blockFriendSuccess) {
					console.log('reached here1')
					res.status(200).json({'status': 'blocked'});
					return;
				} else {
					console.log('reached here2');
					res.status(400).json({'error': strings.BlockFriendFailed});
					return;
				}
			})
	}
	//blocking a user after deleting their request
	else if (EntryExists == 'insert') {
		console.log('reached here3')
			databaseHelper.blockFriendNewEntry(tok.user_id, friend, (insertSuccess) => {
				if (insertSuccess) {
					res.status(200).json({'status': 'blocked'});
					return;
				} else {
					res.status(400).json({'error': strings.BlockFriendFailed});
					return;
				}
			})
	}

	else {
		res.status(400).json({'error': strings.BlockFriendFailed});
	}
	})
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

router.delete ('/delete', function (req, res) {
	try {
	  var tok = tokenHelper.verifyToken(req.query.jwt);
	}
	catch(err) {
		console.log('reached here0');
	  res.status(400).json(requestHelper.jsonError(err)); return;
	}

	/*TODO verify user_2's ID*/

  var friend = req.query.userID
	databaseHelper.checkFriendEntryValidationForDelete(friend, tok.user_id, (friendRequestSuccess) => {
		if (friendRequestSuccess) {
			databaseHelper.declineFriend(tok.user_id, friend, (acceptFriendSuccess) => {
				if (acceptFriendSuccess) {
					res.status(200).json({'status': 'declined'});
				} else {
					console.log('reached here1');
					res.status(400).json({'error': strings.DeleteFriendFailed});
				}
			})
		} else {
			console.log('reached here2');
			res.status(400).json({'error': strings.InvalidFriendRequest});
		}
	})


})


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

router.put('/accept', function (req, res) {
	try {
	  var tok = tokenHelper.verifyToken(req.query.jwt);
	}
	catch(err) {
		console.log('reached here0');
	  res.status(400).json(requestHelper.jsonError(err)); return;
	}


	/*TODO verify user_2's ID*/

  var friend = req.query.userID

	//check if (friend request entry exists in DB)
	databaseHelper.checkFriendRequestValidation(friend, tok.user_id, (friendRequestSuccess) => {
		if (friendRequestSuccess) {
			databaseHelper.acceptFriendInvite(tok.user_id, friend, (acceptFriendSuccess) => {
				if (acceptFriendSuccess) {
					res.status(200).json({'status': 'accepted'});
				} else {
					console.log('reached here1');
					console.log(acceptFriendSuccess)
					res.status(400).json({'error': strings.AcceptFriendFailed});
				}
			})
		} else {
			console.log('reached here2');
			res.status(400).json({'error': strings.InvalidFriendRequest});
		}
	})
})

/**
* @api {get} /friends Lists all friends for a user
* @apiName ListFriends
* @apiGroup Friends
*
* @apiDescription API used for listing friends of a user.
*
* @apiParam {string} jwt Valid JWT
*
*
* @apiSuccess {object} JSON The list of users with userId, firstName, lastName
*
* @apiError error The error field has a string with an exact error
*
* @apiExample Example call:
*     {
*       "jwt": Encrypted_JWT_Token,
*     }
*
* @apiSuccessExample Success-Response:
* HTTP/1.1 200 OK
*			{
*				"user_id": 34,
*				"fname": 'Kattie',
*				"lname": 'Katya'
*			}
*
* @apiSampleRequest /api/friends
*/

router.get('/listFriends', function(req, res) {
	try {
	  var tok = tokenHelper.verifyToken(req.query.jwt);
	}
	catch(err) {
	  res.status(400).json(requestHelper.jsonError(err)); return;
	}

	databaseHelper.listAllFriends(tok.user_id, (listUserSuccess) => {
		if (listUserSuccess) {
			console.log(listUserSuccess)
			res.status(200).json({'status': 'success'});
			return;
		}
		else {
			res.status(400).json({'error': strings.ListFriendRequestFailed});
			return;
		}
	})
})

/**
* @api {get} /friends Lists all blocked users for a user
* @apiName listBlockedUsers
* @apiGroup Friends
*
* @apiDescription API used for listing blocked users of a user.
*
* @apiParam {string} jwt Valid JWT
*
*
* @apiSuccess {object} JSON The list of users with userId, firstName, lastName
*
* @apiError error The error field has a string with an exact error
*
* @apiExample Example call:
*     {
*       "jwt": Encrypted_JWT_Token,
*     }
*
* @apiSuccessExample Success-Response:
* HTTP/1.1 200 OK
*			{
*				"user_id": 34,
*				"fname": 'Kattie',
*				"lname": 'Katya'
*			}
*
* @apiSampleRequest /api/friends
*/

router.get('/listBlockedUsers', function(req, res) {
	try {
	  var tok = tokenHelper.verifyToken(req.query.jwt);
	}
	catch(err) {
	  res.status(400).json(requestHelper.jsonError(err)); return;
	}

	databaseHelper.listAllBlockedUsers(tok.user_id, (listBlockUserSuccess) => {
		if (listBlockUserSuccess) {
			console.log(listBlockUserSuccess)
			res.status(200).json({'status': 'success'});
			return;
		}
		else {
			res.status(400).json({'error': strings.ListBlockUserRequestFailed});
			return;
		}
	})
})

module.exports = router;
