var frisby = require('frisby');
var request = require('request');
var strings = require('../api/universal_strings');
var testHelper = require('./testHelper');
const util = require('util');

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

// Joining and leaving game
frisby.create('Joining a game: Creating a user to create a game')
    .post(testHelper.registerEndpoint, testHelper.createGenericUserFixedBirth())
    .expectStatus(200)
    .expectBodyContains('token')
    .afterJSON(function (body) {
        frisby.create('Creating a new game')
            .post(testHelper.createGameEndpoint, testHelper.createGenericGame(body.token, 100, 200))
            .expectStatus(200)
            .expectBodyContains('game_id')
            .afterJSON(function (body) {
                var gameId = body.game_id;
                frisby.create("Creating a new user to join the game")
                .post(testHelper.registerEndpoint, testHelper.createGenericUserFixedBirth())
                .expectStatus(200)
                .expectBodyContains('token')
                .afterJSON(function (body) {
                    frisby.create('Join a game using the API with valid credentials')
                        .put(util.format(testHelper.joinGameEndpoint, gameId, body.token), body.token)
                        .expectStatus(200)
                        .expectBodyContains('token')
                        .expectBodyContains('game_id')
                        .afterJSON(function (body) {
                            frisby.create('Leaving a game using API - valid case')
                                .delete(util.format(testHelper.leaveGameEndpoint, body.game_id, body.token), body.token)
                                .expectStatus(200)
                                .toss();
                        })
                        .toss();
                })
                .toss();
            })
            .toss();
    })
    .toss();


// // Leaving a game that user is actually in
// frisby.create('Leaving a game valid: Creating a user to create a game')
// .post(testHelper.registerEndpoint, testHelper.createGenericUser())
// .expectStatus(200)
// .expectBodyContains('token')
// .afterJSON(function (body) {
//   frisby.create('Creating a new game')
//   .post(testHelper.createGameEndpoint, testHelper.createGenericGame(body.token, 100, 200))
//   .expectStatus(200)
//   .expectBodyContains('game_id')
//   .afterJSON(function (game) {
//     frisby.create("Creating a new user to join the game")
//     .post(testHelper.registerEndpoint, testHelper.createGenericUser())
//     .expectStatus(200)
//     .expectBodyContains('token')
//     .afterJSON(function (newUser) {
//       frisby.create('Join a game using the API with valid credentials')
//       .put(util.format(testHelper.joinGameEndpoint, game.game_id, newUser.token), newUser.token)
//       .expectStatus(200)
//       .expectBodyContains('token')
//       .expectBodyContains('game_id')
//       .afterJSON(function () {
//         frisby.create('Leaving a game using API - valid case')
//         .delete(util.format(testHelper.leaveGameEndpoint, game.game_id, newUser.token), newUser.token)
//         .expectStatus(200)
//         .toss();
//       })
//       .toss();
//     })
//     .toss();
//   })
//   .toss();
// })
// .toss();
//
// Leaving a game that user is not actually in
frisby.create('Leaving a game invalid: Creating a user to create a game')
.post(testHelper.registerEndpoint, testHelper.createGenericUser())
.expectStatus(200)
.expectBodyContains('token')
.afterJSON(function (body) {
  frisby.create('Creating a new game')
  .post(testHelper.createGameEndpoint, testHelper.createGenericGame(body.token, 100, 200))
  .expectStatus(200)
  .expectBodyContains('game_id')
  .afterJSON(function (game) {
    frisby.create("Creating a new user to leave the game")
    .post(testHelper.registerEndpoint, testHelper.createGenericUser())
    .expectStatus(200)
    .expectBodyContains('token')
    .afterJSON(function (newUser) {
      frisby.create('Leaving a game using API - invalid case')
      .delete(util.format(testHelper.leaveGameEndpoint, game.game_id, newUser.token), newUser.token)
      .expectStatus(400)
      .toss();
    })
    .toss();
  })
  .toss();
})
.toss();