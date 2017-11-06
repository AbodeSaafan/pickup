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
* @apiParam {int} user_Id The user, youre sending a friend request to
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

		var good_input = requestHelper.validateAndCleanFriendId(req.body);
		var friend = good_input.userId;

		databaseHelper.checkIfFriendRequestExists(friend, tok.user_id, (EntryExists) => {
			if (EntryExists) {
				res.status(400).json({'error': strings.FriendRequestExists}); return;
			} else {
			//successfully send the friend invite
			databaseHelper.sendFriendInvite(tok.user_id, friend, (inviteSuccess) => {
				if (inviteSuccess) {
					res.status(200).json({'status': 'invited'}); return;
				} else {
					res.status(400).json({'error': strings.invalidLFriendInvite}); return;
				}
			})
		}
	})
	}
	catch(err) {
		res.status(400).json(requestHelper.jsonError(err)); return;
	}
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


		var good_input = requestHelper.validateAndCleanFriendId(req.query);
		var friend = good_input.userId

		databaseHelper.checkFriendEntryValidationForBlock(tok.user_id, friend, (EntryExists) => {
			if (EntryExists == 'update') {
			//blocking a user after they send a friend request
			//block a friend
			databaseHelper.blockFriendUpdateEntry(tok.user_id, friend, (blockFriendSuccess) => {
				if (blockFriendSuccess) {
					res.status(200).json({'status': 'blocked'}); return;
				} else {
					res.status(400).json({'error': strings.BlockFriendFailed}); return;
				}
			})
		}
	//blocking a user after deleting their request
	else if (EntryExists == 'insert') {
		databaseHelper.blockFriendNewEntry(tok.user_id, friend, (insertSuccess) => {
			if (insertSuccess) {
				res.status(200).json({'status': 'blocked'}); return;
			} else {
				res.status(400).json({'error': strings.BlockFriendFailed}); return;
			}
		})
	}
	else {
		res.status(400).json({'error': strings.BlockFriendFailed}); return;
	}
})
	}
	catch(err) {
		res.status(400).json(requestHelper.jsonError(err)); return;
	}

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

		var good_input = requestHelper.validateAndCleanFriendId(req.query);
		var friend = good_input.userId;

		databaseHelper.checkFriendEntryValidationForDelete(friend, tok.user_id, (friendRequestSuccess) => {
			if (friendRequestSuccess) {
				databaseHelper.declineFriend(tok.user_id, friend, (acceptFriendSuccess) => {
					if (acceptFriendSuccess) {
						res.status(200).json({'status': 'declined'}); return;
					} else {
						res.status(400).json({'error': strings.DeleteFriendFailed}); return;
					}
				})
			} else {
				res.status(400).json({'error': strings.InvalidFriendRequest}); return;
			}
		})
	}
	catch(err) {
		res.status(400).json(requestHelper.jsonError(err)); return;
	}
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

		var good_input = requestHelper.validateAndCleanFriendId(req.query);
		var friend = good_input.userId;

		//check if (friend request entry exists in DB)
		databaseHelper.checkFriendRequestValidation(friend, tok.user_id, (friendRequestSuccess) => {
			if (friendRequestSuccess) {
				databaseHelper.acceptFriendInvite(tok.user_id, friend, (acceptFriendSuccess) => {
					if (acceptFriendSuccess) {
						res.status(200).json({'status': 'accepted'}); return;
					} else {
						res.status(400).json({'error': strings.AcceptFriendFailed}); return;
					}
				})
			} else {
				res.status(400).json({'error': strings.InvalidFriendRequest}); return;
			}
		})
	}
	catch(err) {
		res.status(400).json(requestHelper.jsonError(err)); return;
	}
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
		var friends = [];
		databaseHelper.listAllFriends(tok.user_id, (listUserSuccess) => {
			if (listUserSuccess) {
				console.log(listUserSuccess)
				for (i = 0; i < listUserSuccess.length; i++) {
					var entry = {
						user_id: listUserSuccess[i].user_id,
						fname: listUserSuccess[i].fname,
						lname: listUserSuccess[i].lname
					}
					friends.push(entry)
				}
				console.log(friends)
				res.status(200).json({'friends': friends}); return;
			}
			else {
				res.status(400).json({'error': strings.ListFriendFailed}); return;
			}
		})
	}
	catch(err) {
		res.status(400).json(requestHelper.jsonError(err)); return;
	}
})

/**
* @api {get} /friends Lists all blocked users for a user
* @apiName listBlockedUsers
* @apiGroup Friends
*
* @apiDescription API used for listing blocked users  of a user.
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
		var blockedUsers = [];
		databaseHelper.listAllBlockedUsers(tok.user_id, (listBlockUserSuccess) => {
			if (listBlockUserSuccess) {
				console.log(listBlockUserSuccess)
				for (i = 0; i < listBlockUserSuccess.length; i++) {
					var entry = {
						user_id: listBlockUserSuccess[i].user_id,
						fname: listBlockUserSuccess[i].fname,
						lname: listBlockUserSuccess[i].lname
					}
					blockedUsers.push(entry)
				}
				console.log(blockedUsers)
				res.status(200).json({'blockedUsers': blockedUsers}); return;
			}
			else {
				res.status(400).json({'error': strings.ListBlockUserRequestFailed}); return;
			}
		})
	}
	catch(err) {
		res.status(400).json(requestHelper.jsonError(err)); return;
	}
})

/**
* @api {get} /friends Lists all friend requests for a user
* @apiName listFriendRequest
* @apiGroup Friends
*
* @apiDescription API used for listing friend requests for a user.
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
router.get('/listFriendRequest', function(req, res) {
	try {
		var tok = tokenHelper.verifyToken(req.query.jwt);
		var friendRequestSentToUser = []
		var friendRequestUserSent = []


		databaseHelper.listAllFriendRequests(tok.user_id, (listFriendRequestSuccess) => {
			if (listFriendRequestSuccess) {
				console.log(listFriendRequestSuccess)
				for (i = 0; i < listFriendRequestSuccess.length; i++) {
					if (listFriendRequestSuccess[i].user_1 == tok.user_id) {
						console.log('by user')
						var entry = {
							user_id: listFriendRequestSuccess[i].user_2,
							fname: listFriendRequestSuccess[i].fname,
							lname: listFriendRequestSuccess[i].lname,
							status: listFriendRequestSuccess[i].status
						}
						friendRequestUserSent.push(entry);

					} else {
						console.log('for user')
						var entry = {
							user_id: listFriendRequestSuccess[i].user_1,
							fname: listFriendRequestSuccess[i].fname,
							lname: listFriendRequestSuccess[i].lname,
							status: listFriendRequestSuccess[i].status
						}
						friendRequestSentToUser.push(entry);

					}
			}
			var result =  {
				'ByUser': friendRequestUserSent,
				'ForUser': friendRequestSentToUser
			}
			console.log(result)
			res.status(200).json(result); return;
		}
		else {
			res.status(400).json({'error': strings.listFriendRequestFailed}); return;
		}
	})
	}
	catch(err) {
		res.status(400).json(requestHelper.jsonError(err)); return;
	}
})

module.exports = router;
