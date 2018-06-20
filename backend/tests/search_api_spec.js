var frisby = require("frisby");
var strings = require("../api/universal_strings");
var testHelper = require("./testHelper");

describe("Search api testing", function () {
	it("Searching for game using game id/name/type/min_skill/max_skill/total players/location and range/start time/duration: Creating a user to create game", function (doneFn) {
		var token = "";
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
			.expect("status", 200)
			.expect("bodyContains", "token")
			.then(function (user) {
				user = user.json;
				var startTime = parseInt(Math.floor(Date.now()/1000)) + 3600 + 15872;
				var gameDetails = testHelper.createUnrestrictedGame(user.token, startTime, 100);
				return frisby.post(testHelper.createGameEndpoint, gameDetails)
					.expect("bodyContains", "game_id")
					.then(function (game) {
						game = game.json;
						return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
							.expect("status", 200)
							.expect("bodyContains", "token")
							.then(function (new_user) {
								new_user = new_user.json;
								token = new_user.token;
								return frisby.get(testHelper.searchEndpoint + "?jwt=" + token + "&search_object=game&results_max=1&game_name=" + gameDetails.name)
									.expect("status", 200)
									.expect("json", "games[0].game_id", game.game_id)
									.expect("json", "games[0].player_restricted", false);
							})
							.then(function () {
								return frisby.get(testHelper.searchEndpoint + "?jwt=" + token + "&search_object=game&results_max=1&game_name=" + gameDetails.name.toLowerCase())
									.expect("status", 200)
									.expect("json", "games[0].game_id", game.game_id)
									.expect("json", "games[0].player_restricted", false);
							})
							.then(function () {
								return frisby.get(testHelper.searchEndpoint + "?jwt=" + token + "&search_object=game&results_max=1&game_name=" + gameDetails.name.toUpperCase())
									.expect("status", 200)
									.expect("json", "games[0].game_id", game.game_id)
									.expect("json", "games[0].player_restricted", false);
							})
							.then(function () {
								return frisby.get(testHelper.searchEndpoint + "?jwt=" + token + "&search_object=game&results_max=1&game_name=" + gameDetails.name.split(" ")[0].toUpperCase())
									.expect("status", 200)
									.expect("json", "games[0].game_id", game.game_id)
									.expect("json", "games[0].player_restricted", false);
							})
							.then(function () {
								return frisby.get(testHelper.searchEndpoint + "?jwt=" + token + "&search_object=game&results_max=1&game_type=" + gameDetails.type + "&game_id=" + game.game_id)
									.expect("status", 200)
									.expect("json", "games[0].game_id", game.game_id)
									.expect("json", "games[0].player_restricted", false);
							})
							.then(function () {
								var endTime = startTime + 100;
								return frisby.get(testHelper.searchEndpoint + "?jwt=" + token + "&search_object=game&results_max=1&game_start_time=" + startTime + "&game_end_time=" + endTime)
									.expect("status", 200)
									.expect("json", "games[0].game_id", game.game_id)
									.expect("json", "games[0].player_restricted", false);
							})
							.then(function () {
								return frisby.get(testHelper.searchEndpoint + "?jwt=" + token + "&search_object=game&results_max=1&game_skill_min=0&game_id=" + game.game_id)
									.expect("status", 200)
									.expect("json", "games[0].game_id", game.game_id)
									.expect("json", "games[0].player_restricted", false);
							})
							.then(function () {
								return frisby.get(testHelper.searchEndpoint + "?jwt=" + token + "&search_object=game&results_max=1&game_skill_max=10&game_id=" + game.game_id)
									.expect("status", 200)
									.expect("json", "games[0].game_id", game.game_id)
									.expect("json", "games[0].player_restricted", false);
							})
							.then(function () {
								return frisby.get(testHelper.searchEndpoint + "?jwt=" + token + "&search_object=game&game_id=" + game.game_id)
									.expect("status", 200)
									.expect("json", "games[0].game_id", game.game_id)
									.expect("json", "games[0].player_restricted", false);
							})
							.then(function () {
								return frisby.get(testHelper.searchEndpoint + "?jwt=" + token + "&search_object=game&results_max=1&game_total_players=" + gameDetails.total_players_required + "&game_id=" + game.game_id)
									.expect("status", 200)
									.expect("json", "games[0].game_id", game.game_id)
									.expect("json", "games[0].player_restricted", false);
							})
							.then(function () {
								return frisby.get(testHelper.searchEndpoint + "?jwt=" + token + "&search_object=game&results_max=1&game_duration=" + gameDetails.duration + "&game_id=" + game.game_id)
									.expect("status", 200)
									.expect("json", "games[0].game_id", game.game_id)
									.expect("json", "games[0].player_restricted", false);
							})
							.then(function () {
								return frisby.get(testHelper.searchEndpoint + "?jwt=" + token + "&search_object=game&results_max=1&game_location=" + (JSON.stringify(gameDetails.location)) + "&game_location_range=5")
									.expect("status", 200)
									.expect("json", "games[0].game_id", game.game_id)
									.expect("json", "games[0].player_restricted", false);
							})
							.then(function () {
								return frisby.get(testHelper.searchEndpoint + "?jwt=" + token + "&search_object=game&results_max=1&game_skill_min=0&game_duration=" + gameDetails.duration + "&game_id=" + game.game_id)
									.expect("status", 200)
									.expect("json", "games[0].game_id", game.game_id)
									.expect("json", "games[0].player_restricted", false);
							})
							.then(function () {
								var badLocation = { "lat": gameDetails.location.lat + 10, "lng": gameDetails.location.lng + 10 };
								return frisby.get(testHelper.searchEndpoint + "?jwt=" + token + "&search_object=game&results_max=1&game_location=" + (JSON.stringify(badLocation)) + "&game_location_range=1")
									.expect("status", 400)
									.expect("jsonStrict", {
										error: strings.emptySearchResults
									});
							})
							.then(function () {
								return frisby.get(testHelper.searchEndpoint + "?jwt=" + token + "&search_object=game&results_max=1&game_duration=99999999999")
									.expect("status", 400)
									.expect("jsonStrict", {
										error: strings.emptySearchResults
									});
							})
							.then(function () {
								return frisby.get(testHelper.searchEndpoint + "?jwt=" + token + "&search_object=game&results_max=1&game_start_time=9999999999")
									.expect("jsonStrict", {
										error: strings.emptySearchResults
									})
									.expect("status", 400);
							})
							.then(function () {
								return frisby.get(testHelper.searchEndpoint + "?jwt=" + token + "&search_object=game&results_max=1&game_end_time=1")
									.expect("jsonStrict", {
										error: strings.emptySearchResults
									})
									.expect("status", 400);
							})
							.then(function () {
								return frisby.get(testHelper.searchEndpoint + "?jwt=" + token + "&search_object=game&results_max=1&results_max=1&game_total_players=532")
									.expect("jsonStrict", {
										error: strings.emptySearchResults
									})
									.expect("status", 400);
							})
							.then(function () {
								return frisby.get(testHelper.searchEndpoint + "?jwt=" + token + "&search_object=game&results_max=1&game_id=" + game.game_id + 50)
									.expect("jsonStrict", {
										error: strings.emptySearchResults
									})
									.expect("status", 400);
							})
							.then(function () {
								return frisby.get(testHelper.searchEndpoint + "?jwt=" + token + "&search_object=game&results_max=1&game_skill_min=11")
									.expect("jsonStrict", {
										error: strings.invalidGameSkill
									})
									.expect("status", 400);
							})
							.then(function () {
								return frisby.get(testHelper.searchEndpoint + "?jwt=" + token + "&search_object=game&results_max=1&game_type=subdg")
									.expect("jsonStrict", {
										error: strings.invalidGameType
									})
									.expect("status", 400);
							})
							.then(function () {
								return frisby.get(testHelper.searchEndpoint + "?jwt=" + token + "&search_object=game&results_max=1&game_id=asd")
									.expect("jsonStrict", {
										error: strings.invalidGameId
									})
									.expect("status", 400);
							}).done(doneFn);
					});
			});
	});


	it("Searching for game using game id but player can not join so game should show up but show that the player can not join it", function (doneFn) {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserFemale())
			.expect("status", 200)
			.expect("bodyContains", "token")
			.then(function (user) {
				user = user.json;
				var gameDetails = testHelper.createGenericGame(user.token, parseInt(Math.floor(Date.now()/1000)) + 3600 + 100, 200);
				return frisby.post(testHelper.createGameEndpoint, gameDetails)
					.expect("bodyContains", "game_id")
					.then(function (game) {
						game = game.json;
						return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
							.expect("status", 200)
							.expect("bodyContains", "token")
							.then(function (new_user) {
								new_user = new_user.json;
								return frisby.get(testHelper.searchEndpoint + "?jwt=" + new_user.token + "&search_object=game&game_id=" + game.game_id)
									.expect("status", 200)
									.expect("json", "games[0].game_id", game.game_id)
									.expect("json", "games[0].player_restricted", true)
									.expect("json", "games[0].enforced_params", gameDetails.enforced_params);
							}).done(doneFn);
					});
			});
	});

	it("Should be able to search for a user given the username and a valid jwt", function (doneFn) {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
			.expect("status", 200)
			.expect("bodyContains", "token")
			.then(function (user) {
				user = user.json;
				var search_user_detail = testHelper.createGenericUserMale();
				return frisby.post(testHelper.registerEndpoint, search_user_detail)
					.expect("status", 200)
					.expect("bodyContains", "token")
					.then(function (search_user) {
						search_user = search_user.json;
						return frisby.get(testHelper.searchEndpoint + "?jwt=" + user.token + "&search_object=user&username=" + search_user_detail.username.slice(2))
							.expect("status", 200)
							.expect("jsonStrict", "users.0", {
								user_id: search_user.user_id,
								username: search_user_detail.username,
								fname: search_user_detail.fname
							}).done(doneFn);
					});
			});
	});

	it("A user should not see themselves when searching for users", function (doneFn) {
		var userDetails = testHelper.createGenericUserMale();
		return frisby.post(testHelper.registerEndpoint, userDetails)
			.expect("status", 200)
			.expect("bodyContains", "token")
			.then(function (user) {
				user = user.json;
				return frisby.get(testHelper.searchEndpoint + "?jwt=" + user.token + "&search_object=user&username=" + userDetails.username.slice(2))
					.expect("status", 400).done(doneFn);
			});
	});

});