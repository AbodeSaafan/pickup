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
    .post(testHelper.createGameEndpoint, testHelper.createGenericGame(body.token, 1, 1))
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


// Creating a game that conflicts with another game you have created should fail
frisby.create('Register a user using the API with valid credentials to use for creating a game')
  .post(testHelper.registerEndpoint, testHelper.createGenericUser()) 
  .expectStatus(200)
  .expectHeaderContains('content-type', 'application/json')
  .expectBodyContains('token')
  .expectBodyContains('user_id')
  .expectBodyContains('refresh')
  .afterJSON(function (body) {
    frisby.create('Creating a new game')
    .post(testHelper.createGameEndpoint, testHelper.createGenericGame(body.token, 100, 200))
    .expectStatus(200)
    .expectBodyContains('game_id')
    .afterJSON(function (){
      frisby.create('Creating a conflicting game - 1')
      .post(testHelper.createGameEndpoint, testHelper.createGenericGame(body.token, 100, 200))
      .expectStatus(400)
      .expectJSON({
        error: strings.invalidGameScheduleConflict
      })
      .toss();
    })
    .afterJSON(function (){
      frisby.create('Creating a conflicting game - 2')
      .post(testHelper.createGameEndpoint, testHelper.createGenericGame(body.token, 90, 300))
      .expectStatus(400)
      .expectJSON({
        error: strings.invalidGameScheduleConflict
      })
      .toss();
    })
    .afterJSON(function (){
      frisby.create('Creating a conflicting game - 3')
      .post(testHelper.createGameEndpoint, testHelper.createGenericGame(body.token, 140, 300))
      .expectStatus(400)
      .expectJSON({
        error: strings.invalidGameScheduleConflict
      })
      .toss();
    })
    .afterJSON(function (){
      frisby.create('Creating a conflicting game - 4')
      .post(testHelper.createGameEndpoint, testHelper.createGenericGame(body.token, 180, 300))
      .expectStatus(400)
      .expectJSON({
        error: strings.invalidGameScheduleConflict
      })
      .toss();
    })
    .afterJSON(function (){
      frisby.create('Creating a good game - 1')
      .post(testHelper.createGameEndpoint, testHelper.createGenericGame(body.token, 55000, 300))
      .expectStatus(200)
      .expectBodyContains('game_id')
      .toss();
    })
    .afterJSON(function (){
      frisby.create('Creating a good game - 2')
      .post(testHelper.createGameEndpoint, testHelper.createGenericGame(body.token, 0, 20))
      .expectStatus(200)
      .expectBodyContains('game_id')
      .toss();
    })
    .toss();
  })
.toss();