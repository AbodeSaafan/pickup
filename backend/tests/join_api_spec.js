var frisby = require('frisby');
var request = require('request');
var strings = require('../api/universal_strings');
var testHelper = require('./testHelper');

var genericUser = testHelper.createGenericUser();

frisby.create('Register a user using the API with valid credentials to use for creating a game')
    .post(testHelper.registerEndpoint, genericUser)
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
            .afterJSON(function (body) {
                frisby.create('Logging in using valid credentials')
                    .post(testHelper.loginEndpoint, {
                        email: genericUser.email,
                        password: genericUser.password
                    })
                    .expectStatus(200)
                    .expectBodyContains('token')
                    .toss();
            })
            .afterJSON(function (body) {
                frisby.create('Join a game using the API with valid credentials')
                    .put(testHelper.createGameEndpoint + "?jwt=" + body.token, body.game_id)
                    .expectStatus(200)
                    .expectBodyContains('token')
                    .expectBodyContains('game_id')
                    .toss();
            })
            .toss();
    })
    .toss();
