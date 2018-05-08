var frisby = require("frisby");
var strings = require("../api/universal_strings");
var testHelper = require("./testHelper");
const util = require("util");


// Check if user can view their extended Profile

frisby.create("Register a user using the API with valid credentials to use for extendedProfile testing")
	.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
	.expectStatus(200)
	.expectHeaderContains("content-type", "application/json")
	.expectBodyContains("token")
	.expectBodyContains("user_id")
	.expectBodyContains("refresh")
	.afterJSON(function (body) {
		frisby.create("Get extendedProfile of user")
			.get(testHelper.extendedProfileEndpoint + "?jwt=" + body.token + "&userID=" + body.user_id)
			.expectStatus(200)
			.expectJSON({
				user_id: parseInt(body.user_id),
				age: body.age,
				gender: body.gender,
				skilllevel: 0,
				location: null,
				average_review: 0,
				top_tag: null,
				top_tag_count: null
			})
			.toss();
	})
	.toss();


// Check if user can view another user's extended Profile
frisby.create("Register a user using the API with valid credentials to use for extendedProfile testing")
	.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
	.expectStatus(200)
	.expectHeaderContains("content-type", "application/json")
	.expectBodyContains("token")
	.expectBodyContains("user_id")
	.expectBodyContains("refresh")
	.afterJSON(function (user1) {
		frisby.create("Register another user")
			.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
			.expectStatus(200)
			.expectHeaderContains("content-type", "application/json")
			.expectBodyContains("token")
			.expectBodyContains("user_id")
			.expectBodyContains("refresh")
			.afterJSON(function (user2) {
				frisby.create("Get Extended Profile of User2")
					.get(testHelper.extendedProfileEndpoint + "?jwt=" + user1.token + "&userID=" + user2.user_id)
					.expectStatus(200)
					.expectJSON({
						user_id: parseInt(user2.user_id),
						age: user2.age,
						gender: user2.gender,
						skilllevel: 0,
						location: null,
						average_review: 0,
						top_tag: null,
						top_tag_count: null
					})
					.toss();
			})
			.toss();
	})
	.toss();

// Creating a user with valid creds for testing
frisby.create("Register a user using the API with valid credentials to use for extendedProfile testing")
	.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
	.expectStatus(200)
	.expectHeaderContains("content-type", "application/json")
	.expectBodyContains("token")
	.expectBodyContains("user_id")
	.expectBodyContains("refresh")
	.afterJSON(function (body) {
		frisby.create("Update extendedProfile of user")
			.put(testHelper.extendedProfileEndpoint, testHelper.createGenericExtendedProfile(body.token))
			.expectStatus(200)
			.toss();
	})
	.toss();




// Get an Extended Profile after updating skill_level and location
var newUser = testHelper.createGenericUserMale();

frisby.create("Register a user using the API with valid credentials to use for extendedProfile testing")
	.post(testHelper.registerEndpoint, newUser)
	.expectStatus(200)
	.expectHeaderContains("content-type", "application/json")
	.expectBodyContains("token")
	.expectBodyContains("user_id")
	.expectBodyContains("refresh")
	.afterJSON(function (body) {
		frisby.create("Update extendedProfile of user")
			.put(testHelper.extendedProfileEndpoint, testHelper.createGenericExtendedProfile(body.token))
			.expectStatus(200)
			.afterJSON(function (result) {
				frisby.create("Get extendedProfile of user")
					.get(testHelper.extendedProfileEndpoint + "?jwt=" + body.token + "&userID=" + body.user_id)
					.expectStatus(200)
					.expectJSON({
						user_id: parseInt(body.user_id),
						age: body.age,
						gender: body.gender,
						skilllevel: parseInt(result.Users_SkillLevel),
						location: result.location,
						average_review: 0,
						top_tag: null,
						top_tag_count: null
					})
					.afterJSON(function() {
						frisby.create("Update skilllevel of user")
							.put(testHelper.extendedProfileEndpoint, testHelper.createGenericExtendedProfileWithSkilllevel(body.token))
							.expectStatus(200)
							.afterJSON(function(result2) {
								frisby.create("Get extendedProfile of user")
									.get(testHelper.extendedProfileEndpoint + "?jwt=" + body.token + "&userID=" + body.user_id)
									.expectStatus(200)
									.expectJSON({
										user_id: parseInt(body.user_id),
										age: body.age,
										gender: body.gender,
										skilllevel: parseInt(result2.Users_SkillLevel),
										location: result.location,
										average_review: 0,
										top_tag: null,
										top_tag_count: null
									})
									.afterJSON(function() {
										frisby.create("Update location of user")
											.put(testHelper.extendedProfileEndpoint, testHelper.createGenericExtendedProfileWithLocation(body.token))
											.expectStatus(200)
											.afterJSON(function(result3) {
												frisby.create("Get extendedProfile of user")
													.get(testHelper.extendedProfileEndpoint + "?jwt=" + body.token + "&userID=" + body.user_id)
													.expectStatus(200)
													.expectJSON({
														user_id: parseInt(body.user_id),
														age: body.age,
														gender: body.gender,
														skilllevel: parseInt(result2.Users_SkillLevel),
														location: result3.location,
														average_review: 0,
														top_tag: null,
														top_tag_count: null
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
	})
	.toss();



// Using a bad token and a non-existing username to getExtendedProfile should fail
frisby.create("Get extendedProfile of user")
	.get(testHelper.extendedProfileEndpoint + "?jwt=**&username=DoesNotExist")
	.expectStatus(400)
	.toss();

// Using no parameter should fail getExtendedProfile
frisby.create("Get extendedProfile of user")
	.get(testHelper.extendedProfileEndpoint + "?jwt=")
	.expectStatus(400)
	.toss();

// Using a useID that does not exist should fail
frisby.create("Register a user using the API with valid credentials to use for extendedProfile testing")
	.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
	.expectStatus(200)
	.expectHeaderContains("content-type", "application/json")
	.expectBodyContains("token")
	.expectBodyContains("user_id")
	.expectBodyContains("refresh")
	.afterJSON(function (body) {
		frisby.create("Update extendedProfile of user")
			.put(testHelper.extendedProfileEndpoint, testHelper.createGenericExtendedProfile(body.token))
			.expectStatus(200)
			.afterJSON(function () {
				frisby.create("Get extendedProfile of user")
					.get(testHelper.extendedProfileEndpoint + "?jwt=" + body.token + "&userID=DoesNotExist")
					.expectStatus(400)
					.toss();
			})
			.toss();
	})
	.toss();

//invalid skill_level
frisby.create("Register a user using the API with valid credentials to use for extendedProfile testing")
	.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
	.expectStatus(200)
	.expectHeaderContains("content-type", "application/json")
	.expectBodyContains("token")
	.expectBodyContains("user_id")
	.expectBodyContains("refresh")
	.afterJSON(function (body) {
		frisby.create("Update extendedProfile of user with invalid skill_level")
			.put(testHelper.extendedProfileEndpoint, testHelper.createInvalidSkillLevelForExtendedProfile(body.token))
			.expectStatus(400)
			.expectJSON({
				error: strings.invalidGameSkill
			})
			.toss();
	})
	.toss();

//invalid location
frisby.create("Register a user using the API with valid credentials to use for extendedProfile testing")
	.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
	.expectStatus(200)
	.expectHeaderContains("content-type", "application/json")
	.expectBodyContains("token")
	.expectBodyContains("user_id")
	.expectBodyContains("refresh")
	.afterJSON(function (body) {
		frisby.create("Update extendedProfile of user with invalid skill_level")
			.put(testHelper.extendedProfileEndpoint, testHelper.createInvalidLocationForExtendedProfile(body.token))
			.expectStatus(400)
			.expectJSON({
				error: strings.invalidGameLocation
			})
			.toss();
	})
	.toss();

//invalid parameters for Update Call
frisby.create("Update extendedProfile of user with invalid skill_level")
	.put(testHelper.extendedProfileEndpoint, testHelper.createInvalidLocationForExtendedProfile(""))
	.expectStatus(400)
	.toss();

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
				frisby.create("Creating a new game")
					.post(testHelper.createGameEndpoint, testHelper.createUnrestrictedGame(user.token, 500, 600))
					.expectStatus(200)
					.expectBodyContains("game_id")
					.afterJSON(function (game1) {
						var gameRealId = game.game_id;
						var gameRealId1 = game1.game_id;
						frisby.create("Creating a new user to join the game")
							.post(testHelper.registerEndpoint, testHelper.createGenericUserFixedBirth())
							.expectStatus(200)
							.afterJSON(function (newUser) {
								frisby.create("Creating a new user to join the game")
									.post(testHelper.registerEndpoint, testHelper.createGenericUserFixedBirth())
									.expectStatus(200)
									.afterJSON(function (newUser1) {
										frisby.create("Join a game using the API with valid credentials")
											.put(util.format(testHelper.joinGameEndpoint, gameRealId, newUser.token), newUser.token)
											.expectStatus(200)
											.expectBodyContains("game_id")
											.afterJSON(function () {
												frisby.create("Join a game using the API with valid credentials")
													.put(util.format(testHelper.joinGameEndpoint, gameRealId1, newUser1.token), newUser.token)
													.expectStatus(200)
													.expectBodyContains("game_id")
													.afterJSON(function () {
														frisby.create("Set the review to the new user")
															.post(testHelper.setReviewEndpoint, {
																gameId: parseInt(gameRealId),
																userId: parseInt(user.user_id),
																rating: 2,
																tags: [1, 2],
																reviewed: false,
																jwt: newUser.token
															})
															.expectStatus(200)
															.afterJSON(function () {
																frisby.create("Set the review to the new user1")
																	.post(testHelper.setReviewEndpoint, {
																		gameId: parseInt(gameRealId1),
																		userId: parseInt(user.user_id),
																		rating: 4,
																		tags: [1],
																		reviewed: false,
																		jwt: newUser1.token
																	})
																	.expectStatus(200)
																	.afterJSON(function () {
																		frisby.create("Update extendedProfile of user")
																			.put(testHelper.extendedProfileEndpoint, testHelper.createGenericExtendedProfile(user.token))
																			.expectStatus(200)
																			.afterJSON(function (result) {
																				frisby.create("Get extendedProfile of user")
																					.get(testHelper.extendedProfileEndpoint + "?jwt=" + user.token + "&userID=" + user.user_id)
																					.expectStatus(200)
																					.expectJSON({
																						user_id: parseInt(user.user_id),
																						age: user.age,
																						gender: user.gender,
																						skilllevel: parseInt(result.Users_SkillLevel),
																						location: "(" + result.Users_Location.lat + "," + result.Users_Location.lng + ")",
																						average_review: 3,
																						top_tag: 1,
																						top_tag_count: 2
																					});
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
							})
							.toss();
					})
					.toss();
			})
			.toss();
	})
	.toss();


// User creates one game, ext profile should reflect that
frisby.create("Register a user using the API with valid credentials to use for creating a game")
	.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
	.expectStatus(200)
	.expectBodyContains("token")
	.expectBodyContains("user_id")
	.afterJSON(function (body) {
		frisby.create("Creating a new game")
			.post(testHelper.createGameEndpoint, testHelper.createUnrestrictedGame(body.token, 1, 1))
			.expectStatus(200)
			.expectBodyContains("game_id")
			.afterJSON(function () {
				frisby.create("Get extendedProfile of user")
					.get(testHelper.extendedProfileEndpoint + "?jwt=" + body.token + "&userID=" + body.user_id)
					.expectStatus(200)
					.expectJSON({
						user_id: parseInt(body.user_id),
						age: body.age,
						gender: body.gender,
						skilllevel: 0,
						location: null,
						average_review: 0,
						top_tag: null,
						top_tag_count: null,
						games_created: 1,
						games_joined: 1
					})
					.toss();
			})
			.toss();
	})
	.toss();


// User creates one game, leaves it, ext profile should reflect that
frisby.create("Register a user using the API with valid credentials to use for creating a game")
	.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
	.expectStatus(200)
	.expectBodyContains("token")
	.expectBodyContains("user_id")
	.afterJSON(function (body) {
		frisby.create("Creating a new game")
			.post(testHelper.createGameEndpoint, testHelper.createUnrestrictedGame(body.token, 1, 1))
			.expectStatus(200)
			.expectBodyContains("game_id")
			.afterJSON(function (joinedGame) {
				frisby.create("Leaving a game using API - valid case")
					.delete(util.format(testHelper.leaveGameEndpoint, joinedGame.game_id, body.token), body.token)
					.expectStatus(200)
					.after(function () {
						frisby.create("Get extendedProfile of user")
							.get(testHelper.extendedProfileEndpoint + "?jwt=" + body.token + "&userID=" + body.user_id)
							.expectStatus(200)
							.expectJSON({
								user_id: parseInt(body.user_id),
								age: body.age,
								gender: body.gender,
								skilllevel: 0,
								location: null,
								average_review: 0,
								top_tag: null,
								top_tag_count: null,
								games_created: 1,
								games_joined: 0
							})
							.toss();
					})
					.toss();
			})
			.toss();
	})
	.toss();


//Update Age and Gender in Private Profile and check if that's reflected in Extended Profile
frisby.create("Register a user using the API with valid credentials to use for profile testing")
	.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
	.expectStatus(200)
	.afterJSON(function (body) {
		frisby.create("Update admin profile of user with valid age and gender")
			.put(testHelper.adminProfileEndpoint, {
				jwt: body.token,
				username:"",
				password:"",
				fname:"",
				lname: "",
				gender:"F",
				dob:"04/30/1996",
				email:""
			})
			.expectStatus(200)
			.afterJSON(function () {
				frisby.create("Get Updated Extended Profile of User")
					.get(testHelper.extendedProfileEndpoint + "?jwt=" + body.token + "&userID=" + body.user_id)
					.expectStatus(200)
					.expectJSON({
						user_id: parseInt(body.user_id),
						age: testHelper.calculateAge("04/30/1996"),
						gender: "F",
						skilllevel: 0,
						location: null,
						average_review: 0,
						top_tag: null,
						top_tag_count: null,
						games_created: 0,
						games_joined: 0
					})
					.toss();
			})
			.toss();
	})
	.toss();
