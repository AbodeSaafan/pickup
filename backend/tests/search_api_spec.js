// Search api testing
var frisby = require('frisby');
var request = require('request');
var strings = require('../api/universal_strings');
var testHelper = require('./testHelper');
const util = require('util');

frisby.create('Searching for game using game id/name/type/min_skill/max_skill: Creating a user to create game')
.post(testHelper.registerEndpoint, testHelper.createGenericUser())
.expectStatus(200)
.expectBodyContains('token')
.afterJSON(function (user) {
	var gameDetails = testHelper.createUnrestrictedGame(user.token, 100, 200);
	frisby.create('Creating the game')
	.post(testHelper.createGameEndpoint, gameDetails)
	.expectStatus(200)
	.expectBodyContains('game_id')
	.afterJSON(function (game) {
		frisby.create("Create a user to search for the game")
		.post(testHelper.registerEndpoint, testHelper.createGenericUser())
		.expectStatus(200)
		.expectBodyContains('token')
		.afterJSON(function (new_user) {
			frisby.create("Search for the game using game name")
			.get(testHelper.searchEndpoint+"?jwt="+new_user.token+"&search_object=game&results_max=1&game_name=" + gameDetails.name)
			.expectStatus(200)
			.expectJSON('games.0', {
				game_id: game.game_id
			})
			.toss();	
		})
		.afterJSON(function (new_user) {
			frisby.create("Search for the game using game type")
			.get(testHelper.searchEndpoint+"?jwt="+new_user.token+"&search_object=game&results_max=1&game_type=" + gameDetails.type)
			.expectStatus(200)
			.expectJSON('games.0', {
				game_id: game.game_id
			})
			.toss();	
		})
		.afterJSON(function (new_user) {
			frisby.create("Search for the game using game minimum skill level")
			.get(testHelper.searchEndpoint+"?jwt="+new_user.token+"&search_object=game&results_max=1&game_skill_min=0")
			.expectStatus(200)
			.expectJSON('games.0', {
				game_id: game.game_id
			})
			.toss();	
		})
		.afterJSON(function (new_user) {
			frisby.create("Search for the game using game maximum skill level")
			.get(testHelper.searchEndpoint+"?jwt="+new_user.token+"&search_object=game&results_max=1&game_skill_max=10")
			.expectStatus(200)
			.expectJSON('games.0', {
				game_id: game.game_id
			})
			.toss();	
		})
		.afterJSON(function (new_user) {
			frisby.create("Search for the game using game id")
			.get(testHelper.searchEndpoint+"?jwt="+new_user.token+"&search_object=game&game_id="+game.game_id)
			.expectStatus(200)
			.expectJSON('games.0', {
				game_id: game.game_id
			})
			.toss();	
		})
		.afterJSON(function (new_user) {
			frisby.create("Search for non-existent game using game id")
			.get(testHelper.searchEndpoint+"?jwt="+new_user.token+"&search_object=game&game_id="+game.game_id + 50)
			.expectJSON({
				error: strings.emptySearchResults
			})
			.expectStatus(400)
			.toss();	
		})
		.afterJSON(function (new_user) {
			frisby.create("Search for non-existent game using game minimum skill level")
			.get(testHelper.searchEndpoint+"?jwt="+new_user.token+"&search_object=game&game_skill_min=11")
			.expectJSON({
				error: strings.invalidGameSkill
			})
			.expectStatus(400)
			.toss();	
		})
		.afterJSON(function (new_user) {
			frisby.create("Search for non-existent game using game type")
			.get(testHelper.searchEndpoint+"?jwt="+new_user.token+"&search_object=game&game_type=subdg")
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




//Positive and negative case of searching for user by username 
frisby.create('Searching for user using user name: Creating a user that will search for a user')
.post(testHelper.registerEndpoint, testHelper.createGenericUser())
.expectStatus(200)
.expectBodyContains('token')
.afterJSON(function (user) {
	var search_user_detail = testHelper.createGenericUser()
	frisby.create('Creating a user that will be searched for')
	.post(testHelper.registerEndpoint, search_user_detail )
	.expectStatus(200)
	.expectBodyContains('token')
	.afterJSON(function (search_user) {
		frisby.create("Search for the game using game id")
		.get(testHelper.searchEndpoint+"?jwt="+user.token+"&search_object=user&username="+search_user_detail.username)
		.expectStatus(200)
		.expectJSON('users.0', {
			user_id: search_user.user_id
		})	
		.toss();
	})
	.toss();
})
.toss();