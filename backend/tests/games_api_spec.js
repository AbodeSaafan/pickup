var frisby = require("frisby");
var strings = require("../api/universal_strings");
var testHelper = require("./testHelper");
const util = require("util");

describe("Games api testing", function() {
	it("Should be able to create a game with no restrictions", function(doneFn) {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale()) 
			.expect("status", 200)
			.expect("bodyContains", "token")
			.expect("bodyContains", "user_id")
			.expect("bodyContains", "refresh")
			.then(function (body) {
				body = body.json;
				return frisby.post(testHelper.createGameEndpoint, testHelper.createUnrestrictedGame(body.token, 1, 1))
					.expect("status", 200)
					.expect("bodyContains", "game_id").done(doneFn);
			});
	});

	it("Should be able to create a game with some enforced parameters", function(doneFn) {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
			.expect("status", 200)
			.expect("bodyContains", "token")
			.then(function (body) {
				body = body.json;
				return frisby.post(testHelper.createGameEndpoint, testHelper.createGenericGame(body.token, 1, 1))
					.expect("status", 200)
					.expect("bodyContains", "game_id").done(doneFn);
			});
	});

	it("Should not allow a user to create a game with an invalid jwt token", function(doneFn) {
		return frisby.post(testHelper.createGameEndpoint, testHelper.createUnrestrictedGame({user_id: "1", email: "ab@mail.com"}))
			.expect("status", 400).done(doneFn);
	});


	it("Should not allow a user to create multiple games that conflict", function(doneFn) {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale()) 
			.expect("status", 200)
			.expect("bodyContains", "token")
			.expect("bodyContains", "user_id")
			.expect("bodyContains", "refresh")
			.then(function (body) {
				body = body.json;
				return frisby.post(testHelper.createGameEndpoint, testHelper.createUnrestrictedGame(body.token, 100, 200))
					.expect("status", 200)
					.expect("bodyContains", "game_id")
					.then(function (){
						return frisby.post(testHelper.createGameEndpoint, testHelper.createUnrestrictedGame(body.token, 100, 200))
							.expect("status", 400)
							.expect("jsonStrict", {
								error: strings.invalidGameScheduleConflict
							});
					})
					.then(function (){
						return frisby.post(testHelper.createGameEndpoint, testHelper.createUnrestrictedGame(body.token, 90, 300))
							.expect("status", 400)
							.expect("jsonStrict", {
								error: strings.invalidGameScheduleConflict
							});
					})
					.then(function (){
						return frisby.post(testHelper.createGameEndpoint, testHelper.createUnrestrictedGame(body.token, 140, 300))
							.expect("status", 400)
							.expect("jsonStrict", {
								error: strings.invalidGameScheduleConflict
							});
					})
					.then(function (){
						return frisby.post(testHelper.createGameEndpoint, testHelper.createUnrestrictedGame(body.token, 180, 300))
							.expect("status", 400)
							.expect("jsonStrict", {
								error: strings.invalidGameScheduleConflict
							});
					})
					.then(function (){
						return frisby.post(testHelper.createGameEndpoint, testHelper.createUnrestrictedGame(body.token, 55000, 300))
							.expect("status", 200)
							.expect("bodyContains", "game_id");
					})
					.then(function (){
						return frisby.post(testHelper.createGameEndpoint, testHelper.createUnrestrictedGame(body.token, 0, 20))
							.expect("status", 200)
							.expect("bodyContains", "game_id");
					}).done(doneFn);
			});
	});

	
	it("Should be able to get the users of a game given the game id and a valid jwt", function(doneFn) {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserFixedBirth())
			.expect("status", 200)
			.expect("bodyContains", "token")
			.then(function (user) {
				user = user.json;
				return frisby.post(testHelper.createGameEndpoint, testHelper.createUnrestrictedGame(user.token, 100, 200))
					.expect("status", 200)
					.expect("bodyContains", "game_id")
					.then(function (game) {
						game = game.json;
						var gameId = game.game_id;
						var userDetails = testHelper.createGenericUserFixedBirth();
						return frisby.post(testHelper.registerEndpoint, userDetails)
							.expect("status", 200)
							.then(function (newUser1) {
								newUser1 = newUser1.json;
								return frisby.put(util.format(testHelper.joinGameEndpoint, gameId, newUser1.token))
									.expect("status", 200)
									.expect("bodyContains", "game_id")
									.then(function () {
										var userDetails2 = testHelper.createGenericUserFixedBirth();
										return frisby.post(testHelper.registerEndpoint, userDetails2)
											.expect("status", 200)
											.then(function (newUser2) {
												newUser2 = newUser2.json;
												return frisby.put(util.format(testHelper.joinGameEndpoint, gameId, newUser2.token))
													.expect("status", 200)
													.expect("bodyContains", "game_id")
													.then(function () {
														return frisby.get(testHelper.getUsersOfGameEndpoint + "?jwt=" + user.token + "&game_id=" + gameId)
															.expect("jsonStrict", "?", {
																user_id: newUser2.user_id,
																reviewed: false,
																username: userDetails2.username
															})
															.expect("jsonStrict", "?", {
																user_id: newUser1.user_id,
																reviewed: false,
																username: userDetails.username
															})
															.expect("status", 200).done(doneFn);
													});
											});
									});
							});
					});
			});
	});


	it("Should allow users the ability to join games", function(doneFn) {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserFixedBirth())
			.expect("status", 200)
			.expect("bodyContains", "token")
			.then(function (user) {
				user = user.json;
				return frisby.post(testHelper.createGameEndpoint, testHelper.createUnrestrictedGame(user.token, 100, 200))
					.expect("status", 200)
					.expect("bodyContains", "game_id")
					.then(function (game) {
						game = game.json;
						var gameId = game.game_id;
						return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserFixedBirth())
							.expect("status", 200)
							.then(function (newUser) {
								newUser = newUser.json;
								return frisby.put(util.format(testHelper.joinGameEndpoint, gameId, newUser.token))
									.expect("status", 200)
									.expect("bodyContains", "game_id").done(doneFn);
							});
					});
			});
	});


	it("Should allow users to leave a game they have joined", function(doneFn) {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserFixedBirth())
			.expect("status", 200)
			.expect("bodyContains", "token")
			.then(function (user) {
				user = user.json;
				return frisby.post(testHelper.createGameEndpoint, testHelper.createUnrestrictedGame(user.token, 100, 200))
					.expect("status", 200)
					.expect("bodyContains", "game_id")
					.then(function (game) {
						game = game.json;
						var gameId = game.game_id;
						return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserFixedBirth())
							.expect("status", 200)
							.then(function (newUser) {
								newUser = newUser.json;
								return frisby.put(util.format(testHelper.joinGameEndpoint, gameId, newUser.token))
									.expect("status", 200)
									.expect("bodyContains", "game_id")
									.then(function (joinedGame) {
										joinedGame = joinedGame.json;
										return frisby.del(util.format(testHelper.leaveGameEndpoint, joinedGame.game_id, newUser.token))
											.expect("status", 200).done(doneFn);
									});
							});
					});
			});
	});


	it("Should report a failure when a user tries to leave a game they have not joined", function(doneFn) {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserFixedBirth())
			.expect("status", 200)
			.expect("bodyContains", "token")
			.then(function (user) {
				user = user.json;
				return frisby.post(testHelper.createGameEndpoint, testHelper.createUnrestrictedGame(user.token, 100, 200))
					.expect("status", 200)
					.expect("bodyContains", "game_id")
					.then(function (game) {
						game = game.json;
						return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserFixedBirth())
							.expect("status", 200)
							.then(function (newUser) {
								newUser = newUser.json;
								return frisby.del(util.format(testHelper.leaveGameEndpoint, game.game_id, newUser.token), newUser.token)
									.expect("status", 400).done(doneFn);
							});
					});
			});
	});

	it("Should report a failure when a user trys to create a game with an invalid location", function(doneFn) {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
			.expect("status", 200)
			.expect("bodyContains", "token")
			.then(function (body) {
				body = body.json;
				var game = testHelper.createGenericGame(body.token, 1, 1);
				game.location.lat = 91;
				return frisby.post(testHelper.createGameEndpoint, game)
					.expect("status", 400)
					.expect("jsonStrict", {
						error: strings.invalidGameLocation
					}).then(function () {
						game.location.lat = -91;
						return frisby.post(testHelper.createGameEndpoint, game)
							.expect("status", 400)
							.expect("jsonStrict", {
								error: strings.invalidGameLocation
							}).then(function () {
								game.location.lat = 85;
								game.location.lng = 181;
								return frisby.post(testHelper.createGameEndpoint, game)
									.expect("status", 400)
									.expect("jsonStrict", {
										error: strings.invalidGameLocation
									}).then(function () {
										game.location.lng = -181;
										return frisby.post(testHelper.createGameEndpoint, game)
											.expect("status", 400)
											.expect("jsonStrict", {
												error: strings.invalidGameLocation
											}).done(doneFn);
									});
							});
					});
			});
	});

	it("This test is used for random generation of mock games", function(doneFn) {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale()) 
			.expect("status", 200)
			.expect("bodyContains", "token")
			.expect("bodyContains", "user_id")
			.expect("bodyContains", "refresh")
			.then(function (body) {
				body = body.json;
				return frisby.post(testHelper.createGameEndpoint, testHelper.createMockGtaGame(body.token))
					.expect("status", 200)
					.expect("bodyContains", "game_id").done(doneFn);
			});
	});

});