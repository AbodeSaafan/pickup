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
   .put(testHelper.extendedProfileEndpoint, testHelper.createGenericExtendedProfile (body.token))
   .expectStatus(200)
   .toss();
 })
.toss();


// Using a bad token to getExtendedProfile should fail
frisby.create('Get extendedProfile of user')
.get(extendedProfileEndpoint+"?jwt=**")
.expectStatus(400)
.toss();

// Using no parameter should fail getExtendedProfile
frisby.create('Get extendedProfile of user')
.get(extendedProfileEndpoint+"?jwt=")
.expectStatus(400)
.toss();

//invalid skill_level
frisby.create('Register a user using the API with valid credentials to use for extendedProfile testing')
 .post(testHelper.registerEndpoint, testHelper.createGenericUser())
 .expectStatus(200)
 .expectHeaderContains('content-type', 'application/json')
 .expectBodyContains('token')
 .expectBodyContains('user_id')
 .expectBodyContains('refresh')
 .afterJSON(function (body) {
   frisby.create('Update extendedProfile of user with invalid skill_level')
   .put(testHelper.extendedProfileEndpoint, testHelper.createInvalidSkillLevelForExtendedProfile (body.token))
   .expectStatus(400)
   .expectJSON({
     error: strings.invalidGameSkill
   })
   .toss();
 })
.toss();

//invalid location
frisby.create('Register a user using the API with valid credentials to use for extendedProfile testing')
 .post(testHelper.registerEndpoint, testHelper.createGenericUser())
 .expectStatus(200)
 .expectHeaderContains('content-type', 'application/json')
 .expectBodyContains('token')
 .expectBodyContains('user_id')
 .expectBodyContains('refresh')
 .afterJSON(function (body) {
   frisby.create('Update extendedProfile of user with invalid skill_level')
   .put(testHelper.extendedProfileEndpoint, testHelper.createInvalidLocationForExtendedProfile (body.token))
   .expectStatus(400)
   .expectJSON({
     error: strings.invalidGameLocation
   })
   .toss();
 })
.toss();

//invalid parameters for Update Call
frisby.create('Update extendedProfile of user with invalid skill_level')
.put(testHelper.extendedProfileEndpoint, testHelper.createInvalidLocationForExtendedProfile (''))
.expectStatus(400)
.toss();
