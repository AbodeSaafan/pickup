// Search api testing
var frisby = require('frisby');
var request = require('request');
var strings = require('../api/universal_strings');
var testHelper = require('./testHelper');
const util = require('util');


//Create a game and search for it using game id
frisby.create('Searching for game using game id: Creating a user to create game')
.post(testHelper.registerEndpoint, testHelper.createGenericUser())
.expectStatus(200)
.expectBodyContains('token')
.afterJSON(function (user) {
	var game = testHelper.createUnrestrictedGame(user.token, 100, 200);
	frisby.create('Creating the game')
	.post(testHelper.createGameEndpoint, game)
	.expectStatus(200)
	.expectBodyContains('game_id')
	.afterJSON(function (game) {
		frisby.create("Create a user to search for the game")
		.post(testHelper.registerEndpoint, testHelper.createGenericUser())
		.expectStatus(200)
		.expectBodyContains('token')
		.afterJSON(function (new_user) {
			frisby.create("Search for the game using game id")
			.get(testHelper.searchEndpoint+"?jwt="+new_user.token+"&search_object=game&game_id="+game.game_id)
			.expectStatus(200)
			.expectJSON('0', {
				game_id: game.game_id
			})
			.inspectJSON()
			.toss();	
		})
		.toss();
	})
	.toss();
})
.toss();