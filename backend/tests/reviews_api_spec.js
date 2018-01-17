var frisby = require("frisby");
var strings = require("../api/universal_strings");
var testHelper = require("./testHelper");
const util = require("util");

/*
* Set a new review of a user
* */

// Set the review of a user by another user
frisby.create("Joining a game: Creating a user to create a game")
	.post(testHelper.registerEndpoint, testHelper.createGenericUserFixedBirth())
	.expectStatus(200)
	.expectBodyContains("token")
	.afterJSON(function (user) {
		frisby.create("Creating a new game")
			.post(testHelper.createGameEndpoint, testHelper.createUnrestrictedGame(user.token, 100, 200))
			.expectStatus(200)
			.expectBodyContains("game_id")
			.afterJSON(function (game) {
				var gameId = game.game_id;
				frisby.create("Creating a new user to join the game")
					.post(testHelper.registerEndpoint, testHelper.createGenericUserFixedBirth())
					.expectStatus(200)
					.afterJSON(function (newUser) {
						frisby.create("Join a game using the API with valid credentials")
							.put(util.format(testHelper.joinGameEndpoint, gameId, newUser.token), newUser.token)
							.expectStatus(200)
							.expectBodyContains("game_id")
							.afterJSON(function (gameJoin) {
								frisby.create("Get the users of the game")
									.get(testHelper.getUsersOfGameEndpoint+"?jwt="+ user.token +"&game_id="+gameId)
								    .expectJSON('0',{
								    	user_id: parseInt(user.user_id),
								    	reviewed: false
								    })
								    .expectJSON('1',{
								    	user_id: parseInt(newUser.user_id),
								    	reviewed: false
								    })
									.expectStatus(200)
									.toss();
							})
							.toss();
					})
					.toss();
			})
			.toss();
	})
	.toss();