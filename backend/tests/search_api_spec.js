// Search api testing
var frisby = require("frisby");
var strings = require("../api/universal_strings");
var testHelper = require("./testHelper");

frisby.create("Searching for game using game id/name/type/min_skill/max_skill/total players/location and range/start time/duration: Creating a user to create game")
	.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
	.expectStatus(200)
	.expectBodyContains("token")
	.afterJSON(function (user) {
		var gameDetails = testHelper.createUnrestrictedGame(user.token, 100, 200);
		frisby.create("Creating the game")
			.post(testHelper.createGameEndpoint, gameDetails)
			.expectBodyContains("game_id")
			.afterJSON(function (game) {
				frisby.create("Create a user to search for the game")
					.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
					.expectStatus(200)
					.expectBodyContains("token")
					.afterJSON(function (new_user) {
						frisby.create("Search for the game using game name")
							.get(testHelper.searchEndpoint+"?jwt="+new_user.token+"&search_object=game&results_max=1&game_name=" + gameDetails.name)
							.expectStatus(200)
							.expectJSON("games.0", {
								game_id: game.game_id
							})
							.toss();	
					})
					.afterJSON(function (new_user) {
						frisby.create("Search for the game using game type")
							.get(testHelper.searchEndpoint+"?jwt="+new_user.token+"&search_object=game&results_max=1&game_type="+gameDetails.type+"&game_id="+game.game_id)
							.expectStatus(200)
							.expectJSON("games.0", {
								game_id: game.game_id
							})
							.toss();	
					})
					.afterJSON(function (new_user) {
						frisby.create("Search for the game using game minimum skill level")
							.get(testHelper.searchEndpoint+"?jwt="+new_user.token+"&search_object=game&results_max=1&game_skill_min=0&game_id="+game.game_id)
							.expectStatus(200)
							.expectJSON("games.0", {
								game_id: game.game_id
							})
							.toss();	
					})
					.afterJSON(function (new_user) {
						frisby.create("Search for the game using game maximum skill level")
							.get(testHelper.searchEndpoint+"?jwt="+new_user.token+"&search_object=game&results_max=1&game_skill_max=10&game_id="+game.game_id)
							.expectStatus(200)
							.expectJSON("games.0", {
								game_id: game.game_id
							})
							.toss();	
					})
					.afterJSON(function (new_user) {
						frisby.create("Search for the game using game id")
							.get(testHelper.searchEndpoint+"?jwt="+new_user.token+"&search_object=game&game_id="+game.game_id)
							.expectStatus(200)
							.expectJSON("games.0", {
								game_id: game.game_id
							})
							.toss();	
					})
					.afterJSON(function (new_user) {
						frisby.create("Search for the game using game total players")
							.get(testHelper.searchEndpoint+"?jwt="+new_user.token+"&search_object=game&results_max=1&game_total_players="+gameDetails.total_players_required+"&game_id="+game.game_id)
							.expectStatus(200)
							.expectJSON("games.0", {
								game_id: game.game_id
							})
							.toss();	
					})
					.afterJSON(function (new_user) {
						frisby.create("Search for the game using game duration")
							.get(testHelper.searchEndpoint+"?jwt="+new_user.token+"&search_object=game&results_max=1&game_duration="+gameDetails.duration+"&game_id="+game.game_id)
							.expectStatus(200)
							.expectJSON("games.0", {
								game_id: game.game_id
							})
							.toss();	
					})
					.afterJSON(function (new_user) {
						frisby.create("Search for the game using game location and range")
							.get(testHelper.searchEndpoint+"?jwt="+new_user.token+"&search_object=game&results_max=1&game_location="+encodeURIComponent(JSON.stringify(gameDetails.location))+"&game_location_range=5")
							.expectStatus(200)
							.expectJSON("games.0", {
								game_id: game.game_id
							})
							.toss();	
					})
					.afterJSON(function (new_user) {
						frisby.create("Search for the game using game duration and minimum skill level")
							.get(testHelper.searchEndpoint+"?jwt="+new_user.token+"&search_object=game&results_max=1&game_skill_min=0&game_duration="+gameDetails.duration+"&game_id="+game.game_id)
							.expectStatus(200)
							.expectJSON("games.0", {
								game_id: game.game_id
							})
							.toss();	
					})
					.afterJSON(function (new_user) {
						frisby.create("Search for non-existent game using game location and range")
							.get(testHelper.searchEndpoint+"?jwt="+new_user.token+"&search_object=game&results_max=1&game_location=%7B%22lat%22%3A0.0%2C%22lng%22%3A0.0%7D&game_location_range=1")
							.expectStatus(400)
							.expectJSON({
								error: strings.emptySearchResults
							})
							.toss();	
					})
					.afterJSON(function (new_user) {
						frisby.create("Search for non-existent game using game duration")
							.get(testHelper.searchEndpoint+"?jwt="+new_user.token+"&search_object=game&results_max=1&game_duration=50000")
							.expectStatus(400)
							.expectJSON({
								error: strings.emptySearchResults
							})
							.toss();	
					})
					.afterJSON(function (new_user) {
						frisby.create("Search for non-existent game using game start time")
							.get(testHelper.searchEndpoint+"?jwt="+new_user.token+"&search_object=game&results_max=1&game_start_time=9999999999")
							.expectJSON({
								error: strings.emptySearchResults
							})
							.expectStatus(400)
							.toss();	
					})
					.afterJSON(function (new_user) {
						frisby.create("Search for non-existent game using game total players")
							.get(testHelper.searchEndpoint+"?jwt="+new_user.token+"&search_object=game&results_max=1&results_max=1&game_total_players=532")
							.expectJSON({
								error: strings.emptySearchResults
							})
							.expectStatus(400)
							.toss();	
					})
					.afterJSON(function (new_user) {
						frisby.create("Search for non-existent game using game id")
							.get(testHelper.searchEndpoint+"?jwt="+new_user.token+"&search_object=game&results_max=1&game_id="+game.game_id + 50)
							.expectJSON({
								error: strings.emptySearchResults
							})
							.expectStatus(400)
							.toss();	
					})
					.afterJSON(function (new_user) {
						frisby.create("Search for non-existent game using game minimum skill level")
							.get(testHelper.searchEndpoint+"?jwt="+new_user.token+"&search_object=game&results_max=1&game_skill_min=11")
							.expectJSON({
								error: strings.invalidGameSkill
							})
							.expectStatus(400)
							.toss();	
					})
					.afterJSON(function (new_user) {
						frisby.create("Search for non-existent game using game type")
							.get(testHelper.searchEndpoint+"?jwt="+new_user.token+"&search_object=game&results_max=1&game_type=subdg")
							.expectJSON({
								error: strings.invalidGameType
							})
							.expectStatus(400)
							.toss();	
					})
					.afterJSON(function (new_user) {
						frisby.create("Search for the game using game name")
							.get(testHelper.searchEndpoint+"?jwt="+new_user.token+"&search_object=game&results_max=1&game_id=asd")
							.expectJSON({
								error: strings.invalidGameId
							})
							.expectStatus(400)
							.toss();
					})
					.toss();
			})
			.toss();
	})
	.toss();


frisby.create("Searching for game using game id but player can not join so game should not show up")
	.post(testHelper.registerEndpoint, testHelper.createGenericUserFemale())
	.expectStatus(200)
	.expectBodyContains("token")
	.afterJSON(function (user) {
		var gameDetails = testHelper.createGenericGame(user.token, 100, 200);
		frisby.create("Creating the game")
			.post(testHelper.createGameEndpoint, gameDetails)
			.expectBodyContains("game_id")
			.afterJSON(function (game) {
				frisby.create("Create a user to search for the game")
					.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
					.expectStatus(200)
					.expectBodyContains("token")
					.afterJSON(function (new_user) {
						frisby.create("Search for the game using game id")
							.get(testHelper.searchEndpoint+"?jwt="+new_user.token+"&search_object=game&game_id="+game.game_id)
							.expectStatus(200)
							.expectJSON({
								games: []
							})
							.toss();	
					})
					.toss();
			})
			.toss();
	})
	.toss();

//Positive and negative case of searching for user by username 
frisby.create("Searching for user using user name: Creating a user that will search for a user")
	.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
	.expectStatus(200)
	.expectBodyContains("token")
	.afterJSON(function (user) {
		var search_user_detail = testHelper.createGenericUserMale();
		frisby.create("Creating a user that will be searched for")
			.post(testHelper.registerEndpoint, search_user_detail )
			.expectStatus(200)
			.expectBodyContains("token")
			.afterJSON(function (search_user) {
				frisby.create("Search for the game using game id")
					.get(testHelper.searchEndpoint+"?jwt="+user.token+"&search_object=user&username="+search_user_detail.username)
					.expectStatus(200)
					.expectJSON("users.0", {
						user_id: search_user.user_id
					})	
					.toss();
			})
			.toss();
	})
	.toss();