var frisby = require("frisby");
var strings = require("../api/universal_strings");
var testHelper = require("./testHelper");
const util = require("util");

// Creating a unrestricted game
frisby.create("Register a user using the API with valid credentials to use for creating a game")
	.post(testHelper.registerEndpoint, testHelper.createGenericUserMale()) 
	.expectStatus(200)
	.expectHeaderContains("content-type", "application/json")
	.expectBodyContains("token")
	.expectBodyContains("user_id")
	.expectBodyContains("refresh")
	.afterJSON(function (body) {
		frisby.create("Creating a new game")
			.post(testHelper.createGameEndpoint, testHelper.createUnrestrictedGame(body.token, 1, 1))
			.expectStatus(200)
			.expectBodyContains("game_id")
			.toss();
	})
	.toss();

// Creating a game with enforced params
frisby.create("Register a user using the API with valid credentials to use for creating a game with enforced params")
	.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
	.expectStatus(200)
	.expectBodyContains("token")
	.afterJSON(function (body) {
		frisby.create("Creating a new game")
			.post(testHelper.createGameEndpoint, testHelper.createGenericGame(body.token, 1, 1))
			.expectStatus(200)
			.expectBodyContains("game_id")
			.toss();
	})
	.toss();

/*
* Create game API tests
* */

// Using a bad token to create game should fail it
frisby.create("Creating a new game")
	.post(testHelper.createGameEndpoint, testHelper.createUnrestrictedGame({user_id: "1", email: "ab@mail.com"}))
	.expectStatus(400)
	.toss();


// Creating a game that conflicts with another game you have created should fail
frisby.create("Register a user using the API with valid credentials to use for creating a game")
	.post(testHelper.registerEndpoint, testHelper.createGenericUserMale()) 
	.expectStatus(200)
	.expectHeaderContains("content-type", "application/json")
	.expectBodyContains("token")
	.expectBodyContains("user_id")
	.expectBodyContains("refresh")
	.afterJSON(function (body) {
		frisby.create("Creating a new game")
			.post(testHelper.createGameEndpoint, testHelper.createUnrestrictedGame(body.token, 100, 200))
			.expectStatus(200)
			.expectBodyContains("game_id")
			.afterJSON(function (){
				frisby.create("Creating a conflicting game - 1")
					.post(testHelper.createGameEndpoint, testHelper.createUnrestrictedGame(body.token, 100, 200))
					.expectStatus(400)
					.expectJSON({
						error: strings.invalidGameScheduleConflict
					})
					.toss();
			})
			.afterJSON(function (){
				frisby.create("Creating a conflicting game - 2")
					.post(testHelper.createGameEndpoint, testHelper.createUnrestrictedGame(body.token, 90, 300))
					.expectStatus(400)
					.expectJSON({
						error: strings.invalidGameScheduleConflict
					})
					.toss();
			})
			.afterJSON(function (){
				frisby.create("Creating a conflicting game - 3")
					.post(testHelper.createGameEndpoint, testHelper.createUnrestrictedGame(body.token, 140, 300))
					.expectStatus(400)
					.expectJSON({
						error: strings.invalidGameScheduleConflict
					})
					.toss();
			})
			.afterJSON(function (){
				frisby.create("Creating a conflicting game - 4")
					.post(testHelper.createGameEndpoint, testHelper.createUnrestrictedGame(body.token, 180, 300))
					.expectStatus(400)
					.expectJSON({
						error: strings.invalidGameScheduleConflict
					})
					.toss();
			})
			.afterJSON(function (){
				frisby.create("Creating a good game - 1")
					.post(testHelper.createGameEndpoint, testHelper.createUnrestrictedGame(body.token, 55000, 300))
					.expectStatus(200)
					.expectBodyContains("game_id")
					.toss();
			})
			.afterJSON(function (){
				frisby.create("Creating a good game - 2")
					.post(testHelper.createGameEndpoint, testHelper.createUnrestrictedGame(body.token, 0, 20))
					.expectStatus(200)
					.expectBodyContains("game_id")
					.toss();
			})
			.toss();
	})
	.toss();

/*
* Get users API test
* */

// Getting users of a game with valid input and successful response
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
							.afterJSON(function () {
								frisby.create("Creating a new user to join the game")
									.post(testHelper.registerEndpoint, testHelper.createGenericUserFixedBirth())
									.expectStatus(200)
									.afterJSON(function (newUser1) {
										frisby.create("Join a game using the API with valid credentials")
											.put(util.format(testHelper.joinGameEndpoint, gameId, newUser1.token), newUser1.token)
											.expectStatus(200)
											.expectBodyContains("game_id")
											.afterJSON(function () {
												frisby.create("Get the users of the game")
													.get(testHelper.getUsersOfGameEndpoint + "?jwt=" + user.token + "&game_id=" + gameId)
													.expectJSON("?", {
														user_id: parseInt(newUser1.user_id),
														reviewed: false,
														username: newUser1.username
													})
													.expectJSON("?", {
														user_id: parseInt(newUser.user_id),
														reviewed: false,
														username: newUser.username
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
			})
			.toss();
	})
	.toss();
/*
* Join game API test
* */

// Joining game with valid input and successful response
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
							.toss();
					})
					.toss();
			})
			.toss();
	})
	.toss();


/*
* Leave game API tests
* */

// Joining and leaving game. Valid response expected
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
							.afterJSON(function (joinedGame) {
								frisby.create("Leaving a game using API - valid case")
									.delete(util.format(testHelper.leaveGameEndpoint, joinedGame.game_id, newUser.token), newUser.token)
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

// Leaving a game the user did not join to begin with
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
				frisby.create("Creating a new user that will not join the game")
					.post(testHelper.registerEndpoint, testHelper.createGenericUserFixedBirth())
					.expectStatus(200)
					.afterJSON(function (newUser) {
						frisby.create("Leaving a game using API - user tries to leave a game the user has not joined")
							.delete(util.format(testHelper.leaveGameEndpoint, game.game_id, newUser.token), newUser.token)
							.expectStatus(400)
							.toss();
					})
					.toss();
			})
			.toss();
	})
	.toss();