var frisby = require('frisby');
var strings = require('../api/universal_strings');
var testHelper = require('./testHelper');

// Creating a user with valid creds for testing
frisby.create('Register a user using the API with valid credentials to use for extendedProfile testing')
 .post(testHelper.registerEndpoint, testHelper.createGenericUser())
 .expectStatus(200)
 .expectHeaderContains('content-type', 'application/json')
 .expectBodyContains('token')
 .expectBodyContains('user_id')
 .expectBodyContains('refresh')
 .afterJSON(function (body) {
   frisby.create('Get extendedProfile of user')
   .get(extendedProfileEndpoint+"?jwt="+body.token)
   .expectStatus(200)
   .toss();
 })
.toss();

// Creating a user with valid creds for testing
frisby.create('Register a user using the API with valid credentials to use for extendedProfile testing')
 .post(testHelper.registerEndpoint, testHelper.createGenericUser())
 .expectStatus(200)
 .expectHeaderContains('content-type', 'application/json')
 .expectBodyContains('token')
 .expectBodyContains('user_id')
 .expectBodyContains('refresh')
 .afterJSON(function (body) {
   frisby.create('Update extendedProfile of user')
   .put(extendedProfileEndpoint+"?jwt="+body.token+"&skillevel=5&location=oakville")
   .expectStatus(200)
   .toss();
 })
.toss();
