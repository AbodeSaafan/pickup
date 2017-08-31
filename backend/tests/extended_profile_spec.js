var frisby = require('frisby');
var strings = require('../api/universal_strings');
var testHelper = require('./testHelper');

var genericUserDetails = {
  userID: 2,
  skill_level: 4,
  location: 'guelph'
};

// Get User's extended_profile (Valid User)
frisby.create('Attempt to get user extended profile with user_id')
    .get(testHelper.extendedProfileEndpoint+"/"+genericUserDetails.userID)
    .expectStatus(200)
    .expectHeaderContains('content-type', 'application/json')
    .toss();

// Get User's extended_profile (Invalid User)
frisby.create('Attempt to get user extended profile with user_id')
    .get(testHelper.extendedProfileEndpoint+"/0")
    .expectStatus(400)
    .expectHeaderContains('content-type', 'application/json')
    .expectJSON({
      error: strings.userIdFail
    })
    .toss();

//Update skill_level and Location for valid User
frisby.create('Attempt to update skill_level and location for user extended profile')
    .put(testHelper.extendedProfileEndpoint+"/"+genericUserDetails.userID+"?skilllevel=" + genericUserDetails.skill_level + "&location=" + genericUserDetails.location)
    .expectStatus(200)
    .expectHeaderContains('content-type', 'application/json')
    .toss();
