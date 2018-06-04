var frisby = require("frisby");
var testHelper = require("./testHelper");
const util = require("util");

/*
* Set a new review of a user
* */

// Set the review of a user by another user
describe("Review api testing", function () {
	it("Should allow a user to review a game they have played", function(doneFn) {
		var user = testHelper.createGenericUserFixedBirth();
		return frisby.post(testHelper.registerEndpoint, user)
			.expect("status", 200)
			.expect("bodyContains", "token")
			.then(function (userWithTok) {
				user = userWithTok.json;
				return frisby.post(testHelper.createGameEndpoint, testHelper.createUnrestrictedGame(user.token, 100, 200))
					.expect("status", 200)
					.expect("bodyContains", "game_id")
					.then(function (game) {
						game = game.json;
						var gameRealId = game.game_id;
						var joiningUser = testHelper.createGenericUserFixedBirth();
						return frisby.post(testHelper.registerEndpoint, joiningUser)
							.expect("status", 200)
							.then(function (newUser) {
								newUser = newUser.json;
								return frisby.put(util.format(testHelper.joinGameEndpoint, gameRealId, newUser.token))
									.expect("status", 200)
									.expect("bodyContains", "game_id")
									.then(function () {
										return frisby.get(testHelper.getUsersOfGameEndpoint+"?jwt="+ user.token +"&game_id="+gameRealId)
											.expect("jsonStrict", "0",{
												user_id: parseInt(newUser.user_id),
												username: joiningUser.username,
												reviewed: false
											})
											.expect("status", 200)
											.then(function(){
												return frisby.post(testHelper.setReviewEndpoint, {
													gameId : parseInt(gameRealId),
													userId : parseInt(newUser.user_id),
													rating : 2,
													tags : [1 , 2],
													reviewed : false,
													jwt : user.token
												})
													.expect("status", 200).done(doneFn);
											});
									});
							});
					});
			});
	});
});