var frisby = require('frisby');
var request = require('request');
var strings = require('../api/universal_strings');
var testHelper = require('./testHelper');

 // Creating a user with valid creds for testing
frisby.create('Register a user using the API with valid credentials to use for refresh testing')
  .post(testHelper.registerEndpoint, testHelper.createGenericUser()) 
  .expectStatus(200)
  .expectHeaderContains('content-type', 'application/json')
  .expectBodyContains('token')
  .expectBodyContains('user_id')
  .expectBodyContains('refresh')
  .afterJSON(function (body) {
    frisby.create('Get a new jwt token refresh')
    .get(refreshEndpoint+"?jwt="+body.token+"&refresh="+body.refresh)
    .expectBodyContains('token')
    .toss();
  })
.toss();