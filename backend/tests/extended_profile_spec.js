var frisby = require("frisby");
var strings = require("../api/universal_strings");
var testHelper = require("./testHelper");
const util = require("util");

describe("Extended profile api testing", function () {
	
	it("Should allow a user to get view their own extended profile with a valid jwt", function(doneFn) {
		var user = testHelper.createGenericUserMale();
		return frisby.post(testHelper.registerEndpoint, user)
			.expect("status", 200)
			.expect("bodyContains", "token")
			.expect("bodyContains", "user_id")
			.expect("bodyContains", "refresh")
			.then(function (body) {
				body = body.json;
				return frisby.get(testHelper.extendedProfileEndpoint + "?jwt=" + body.token + "&userID=" + body.user_id)
					.expect("status", 200)
					.expect("jsonStrict", {
						user_id: body.user_id,
						username: user.username,
						age: testHelper.calculateAge(user.dob),
						gender: user.gender,
						skilllevel: 0,
						location: null,
						average_review: 0,
						top_tag: null,
						top_tag_count: null,
						games_created: 0,
						games_joined: 0,
						recentGames: []
					}).done(doneFn);
			});
	});

	it("Should allow a user to get view another user's extended profile with a valid jwt", function(doneFn) {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
			.expect("status", 200)
			.expect("bodyContains", "token")
			.expect("bodyContains", "user_id")
			.expect("bodyContains", "refresh")
			.then(function (user1) {
				user1 = user1.json;
				var user2Details = testHelper.createGenericUserMale();
				return frisby.post(testHelper.registerEndpoint, user2Details)
					.expect("status", 200)
					.expect("bodyContains", "token")
					.expect("bodyContains", "user_id")
					.expect("bodyContains", "refresh")
					.then(function (user2) {
						user2 = user2.json;
						return frisby.get(testHelper.extendedProfileEndpoint + "?jwt=" + user1.token + "&userID=" + user2.user_id)
							.expect("status", 200)
							.expect("jsonStrict", {
								user_id: user2.user_id,
								username: user2Details.username,
								age: testHelper.calculateAge(user2Details.dob),
								gender: user2Details.gender,
								skilllevel: 0,
								location: null,
								average_review: 0,
								top_tag: null,
								top_tag_count: null,
								games_created: 0,
								games_joined: 0,
								recentGames: []
							}).done(doneFn);
					});
			});
	});
	

	it("Should accept a user's update to their own extended profile", function(doneFn) {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
			.expect("status", 200)
			.expect("bodyContains", "token")
			.expect("bodyContains", "user_id")
			.expect("bodyContains", "refresh")
			.then(function (body) {
				body = body.json;
				return frisby.put(testHelper.extendedProfileEndpoint, testHelper.createGenericExtendedProfile(body.token))
					.expect("status", 200);
			}).done(doneFn);
	});

	it("It should properly update a user's extended profile: skill level and location", function(doneFn) {
		var newUser = testHelper.createGenericUserMale();
		return frisby.post(testHelper.registerEndpoint, newUser)
			.expect("status", 200)
			.expect("bodyContains", "token")
			.expect("bodyContains", "user_id")
			.expect("bodyContains", "refresh")
			.then(function (body) {
				body = body.json;
				var userExtProfile = testHelper.createGenericExtendedProfile(body.token);
				return frisby.put(testHelper.extendedProfileEndpoint, userExtProfile)
					.expect("status", 200)
					.then(function () {
						return frisby.get(testHelper.extendedProfileEndpoint + "?jwt=" + body.token + "&userID=" + body.user_id)
							.expect("status", 200)
							.expect("jsonStrict", {
								user_id: body.user_id,
								username: newUser.username,
								age: testHelper.calculateAge(newUser.dob),
								gender: newUser.gender,
								skilllevel: userExtProfile.skill_level,
								location: "(" + userExtProfile.location.lat + "," + userExtProfile.location.lng + ")",
								average_review: 0,
								top_tag: null,
								top_tag_count: null,
								games_created: 0,
								games_joined: 0,
								recentGames: []
							})
							.then(function() {
								var userExtProfileSecondUpdate = testHelper.createGenericExtendedProfileWithSkilllevel(body.token);
								return frisby.put(testHelper.extendedProfileEndpoint, userExtProfileSecondUpdate)
									.expect("status", 200)
									.then(function() {
										return frisby.get(testHelper.extendedProfileEndpoint + "?jwt=" + body.token + "&userID=" + body.user_id)
											.expect("status", 200)
											.expect("jsonStrict", {
												user_id: body.user_id,
												username: newUser.username,
												age: testHelper.calculateAge(newUser.dob),
												gender: newUser.gender,
												skilllevel: userExtProfileSecondUpdate.skill_level,
												location: "(" + userExtProfile.location.lat + "," + userExtProfile.location.lng + ")",
												average_review: 0,
												top_tag: null,
												top_tag_count: null,
												games_created: 0,
												games_joined: 0,
												recentGames: []
											})
											.then(function() {
												var userExtProfileThirdUpdate = testHelper.createGenericExtendedProfileWithLocation(body.token);
												return frisby.put(testHelper.extendedProfileEndpoint, userExtProfileThirdUpdate)
													.expect("status", 200)
													.then(function() {
														return frisby.get(testHelper.extendedProfileEndpoint + "?jwt=" + body.token + "&userID=" + body.user_id)
															.expect("status", 200)
															.expect("jsonStrict", {
																user_id: body.user_id,
																username: newUser.username,
																age: testHelper.calculateAge(newUser.dob),
																gender: newUser.gender,
																skilllevel: userExtProfileSecondUpdate.skill_level,
																location: "(" + userExtProfileThirdUpdate.location.lat + "," + userExtProfileThirdUpdate.location.lng + ")",
																average_review: 0,
																top_tag: null,
																top_tag_count: null,
																games_created: 0,
																games_joined: 0,
																recentGames: []
															}).done(doneFn);
													});
											});
									});
							});
					});
			});
	});
	
	
	it("Should fail getting the extended profile of a user when the jwt is invalid", function(doneFn) {
		return frisby.get(testHelper.extendedProfileEndpoint + "?jwt=**&userID=2")
			.expect("status", 400).done(doneFn);
	});
	
	// Using a useID that does not exist should fail
	it("Should fail getting the extended profile of a user where the userId is invalid/does not exist", function(doneFn) {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
			.expect("status", 200)
			.expect("bodyContains", "token")
			.expect("bodyContains", "user_id")
			.expect("bodyContains", "refresh")
			.then(function (body) {
				body = body.json;
				return frisby.put(testHelper.extendedProfileEndpoint, testHelper.createGenericExtendedProfile(body.token))
					.expect("status", 200)
					.then(function () {
						return frisby.get(testHelper.extendedProfileEndpoint + "?jwt=" + body.token + "&userID=DoesNotExist")
							.expect("status", 400);
					}).done(doneFn);
			});
	});
	
	it("Should fail updating the skill level of a user if the skill level is invalid", function(doneFn) {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
			.expect("status", 200)
			.expect("bodyContains", "token")
			.expect("bodyContains", "user_id")
			.expect("bodyContains", "refresh")
			.then(function (body) {
				body = body.json;
				return frisby.put(testHelper.extendedProfileEndpoint, testHelper.createInvalidSkillLevelForExtendedProfile(body.token))
					.expect("status", 400)
					.expect("jsonStrict", {
						error: strings.invalidGameSkill
					}).done(doneFn);
			});
	});
	
	it("Should fail updating the skill level of a user if the location is invalid", function(doneFn) {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
			.expect("status", 200)
			.expect("bodyContains", "token")
			.expect("bodyContains", "user_id")
			.expect("bodyContains", "refresh")
			.then(function (body) {
				body = body.json;
				return frisby.put(testHelper.extendedProfileEndpoint, testHelper.createInvalidLocationForExtendedProfile(body.token))
					.expect("status", 400)
					.expect("jsonStrict", {
						error: strings.invalidGameLocation
					}).done(doneFn);
			});
	});
	
	// Set the review of a user by another user
	it("Should return recent games properly as well as average review", function(doneFn) {
		var userDetails = testHelper.createGenericUserFixedBirth();
		return frisby.post(testHelper.registerEndpoint, userDetails)
			.expect("status", 200)
			.expect("bodyContains", "token")
			.then(function (user) {
				user = user.json;
				var game1Details = testHelper.createUnrestrictedGame(user.token, 100, 200);
				return frisby.post(testHelper.createGameEndpoint, game1Details)
					.expect("status", 200)
					.expect("bodyContains", "game_id")
					.then(function (game) {
						game = game.json;
						var game2Details = testHelper.createUnrestrictedGame(user.token, 500, 600);
						return frisby.post(testHelper.createGameEndpoint, game2Details)
							.expect("status", 200)
							.expect("bodyContains", "game_id")
							.then(function (game1) {
								game1 = game1.json;
								var gameRealId = game.game_id;
								var gameRealId1 = game1.game_id;
								return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserFixedBirth())
									.expect("status", 200)
									.then(function (newUser) {
										newUser = newUser.json;
										return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserFixedBirth())
											.expect("status", 200)
											.then(function (newUser1) {
												newUser1 = newUser1.json;
												return frisby.put(util.format(testHelper.joinGameEndpoint, gameRealId, newUser.token))
													.expect("status", 200)
													.expect("bodyContains", "game_id")
													.then(function () {
														return frisby.put(util.format(testHelper.joinGameEndpoint, gameRealId1, newUser1.token))
															.expect("status", 200)
															.expect("bodyContains", "game_id")
															.then(function () {
																return frisby.post(testHelper.setReviewEndpoint, {
																	gameId: gameRealId,
																	userId: user.user_id,
																	rating: 2,
																	tags: [1, 2],
																	reviewed: false,
																	jwt: newUser.token
																})
																	.expect("status", 200)
																	.then(function () {
																		return frisby.post(testHelper.setReviewEndpoint, {
																			gameId: gameRealId1,
																			userId: user.user_id,
																			rating: 4,
																			tags: [1],
																			reviewed: false,
																			jwt: newUser1.token
																		})
																			.expect("status", 200)
																			.then(function () {
																				var userExtProfileDetails = testHelper.createGenericExtendedProfile(user.token);
																				return frisby.put(testHelper.extendedProfileEndpoint, userExtProfileDetails)
																					.expect("status", 200)
																					.then(function () {
																						return frisby.get(testHelper.extendedProfileEndpoint + "?jwt=" + user.token + "&userID=" + user.user_id)
																							.expect("status", 200)
																							.expect("bodyContains", "user_id", user.user_id)
																							.expect("bodyContains", "username", userDetails.username)
																							.expect("bodyContains", "age", testHelper.calculateAge(userDetails.dob))
																							.expect("bodyContains", "gender", userDetails.gender)
																							.expect("bodyContains", "skilllevel", userExtProfileDetails.skill_level)
																							.expect("bodyContains", "location", "(" + userExtProfileDetails.location.lat + "," + userExtProfileDetails.location.lng + ")")
																							.expect("bodyContains", "average_review", 3)
																							.expect("bodyContains", "top_tag", 1)
																							.expect("bodyContains", "top_tag_count", 1)
																							.expect("bodyContains", "games_created", 2)
																							.expect("bodyContains", "games_joined", 2)
																							.expect("jsonStrict", "recentGames.?.name", game1Details.name)
																							.expect("jsonStrict", "recentGames.?.name", game2Details.name).done(doneFn);
																					});
																			});
																	});
															});
													});
											});
									});
							});
					});
			});
	});

	
	it("Should reflect the change when a user creates or joins a game", function(doneFn) {
		var userDetails = testHelper.createGenericUserMale();
		return frisby.post(testHelper.registerEndpoint, userDetails)
			.expect("status", 200)
			.expect("bodyContains", "token")
			.expect("bodyContains", "user_id")
			.then(function (body) {
				body = body.json;
				var gameDetails = testHelper.createUnrestrictedGame(body.token, 1, 1);
				return frisby.post(testHelper.createGameEndpoint, gameDetails)
					.expect("status", 200)
					.expect("bodyContains", "game_id")
					.then(function () {
						return frisby.get(testHelper.extendedProfileEndpoint + "?jwt=" + body.token + "&userID=" + body.user_id)
							.expect("status", 200)
							.expect("json", {
								user_id: body.user_id,
								username: userDetails.username,
								age: testHelper.calculateAge(userDetails.dob),
								gender: userDetails.gender,
								skilllevel: 0,
								location: null,
								average_review: 0,
								top_tag: null,
								top_tag_count: null,
								games_created: 1,
								games_joined: 1
							}).done(doneFn);
					});
			});
	});

	it("Should properly reflect the change when a user leaves a game", function(doneFn) {
		var userDetails = testHelper.createGenericUserMale();
		return frisby.post(testHelper.registerEndpoint, userDetails)
			.expect("status", 200)
			.expect("bodyContains", "token")
			.expect("bodyContains", "user_id")
			.then(function (body) {
				body = body.json;
				return frisby.post(testHelper.createGameEndpoint, testHelper.createUnrestrictedGame(body.token, 1, 1))
					.expect("status", 200)
					.expect("bodyContains", "game_id")
					.then(function (joinedGame) {
						joinedGame = joinedGame.json;
						return frisby.del(util.format(testHelper.leaveGameEndpoint, joinedGame.game_id, body.token))
							.expect("status", 200)
							.then(function () {
								return frisby.get(testHelper.extendedProfileEndpoint + "?jwt=" + body.token + "&userID=" + body.user_id)
									.expect("status", 200)
									.expect("jsonStrict", {
										user_id: body.user_id,
										username: userDetails.username,
										age: testHelper.calculateAge(userDetails.dob),
										gender: userDetails.gender,
										skilllevel: 0,
										location: null,
										average_review: 0,
										top_tag: null,
										top_tag_count: null,
										games_created: 1,
										games_joined: 0,
										recentGames: []
									}).done(doneFn);
							});
					});
			});
	});

	it("Should reflect a change in age or gender", function(doneFn) {
		var userDetails = testHelper.createGenericUserMale();
		return frisby.post(testHelper.registerEndpoint, userDetails)
			.expect("status", 200)
			.then(function (body) {
				body = body.json;
				return frisby.put(testHelper.adminProfileEndpoint, {
					jwt: body.token,
					username:"",
					password:"",
					fname:"",
					lname: "",
					gender:"F",
					dob:"04/30/1996",
					email:"",
					recentGames: []
				})
					.expect("status", 200)
					.then(function () {
						return frisby.get(testHelper.extendedProfileEndpoint + "?jwt=" + body.token + "&userID=" + body.user_id)
							.expect("status", 200)
							.expect("jsonStrict", {
								user_id: body.user_id,
								username: userDetails.username,
								age: testHelper.calculateAge("04/30/1996"),
								gender: "F",
								skilllevel: 0,
								location: null,
								average_review: 0,
								top_tag: null,
								top_tag_count: null,
								games_created: 0,
								games_joined: 0,
								recentGames: []
							}).done(doneFn);
					});
			});
	});
});