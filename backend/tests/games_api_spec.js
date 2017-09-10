var frisby = require('frisby');
var request = require('request');
var strings = require('../api/universal_strings');
var testHelper = require('./testHelper');

 // Creating a user with valid creds for testing
frisby.create('Register a user using the API with valid credentials to use for creating a game')
  .post(testHelper.registerEndpoint, testHelper.createGenericUser()) 
  .expectStatus(200)
  .expectHeaderContains('content-type', 'application/json')
  .expectBodyContains('token')
  .expectBodyContains('user_id')
  .expectBodyContains('refresh')
  .afterJSON(function (body) {
    frisby.create('Creating a new game')
    .post(testHelper.createGameEndpoint, testHelper.createGenericGame(body.token))
    .expectStatus(200)
    .expectBodyContains('game_id')
    .toss();
  })
.toss();

// Using a bad token to create game should fail it
 frisby.create('Creating a new game')
    .post(testHelper.createGameEndpoint, testHelper.createGenericGame({user_id: '1', email: 'ab@mail.com'}))
    .expectStatus(400)
    .toss();