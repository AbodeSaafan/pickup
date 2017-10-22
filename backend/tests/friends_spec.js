var frisby = require('frisby');
var request = require('request');
var strings = require('../api/universal_strings');
var testHelper = require('./testHelper');
const util = require('util');

//Send a friend request to valid User

frisby.create('Sending a Friend Request: Creating a user to send a friend request')
.post(testHelper.registerEndpoint, testHelper.createGenericUser())
.expectStatus(200)
.expectBodyContains('token')
.afterJSON(function (user) {
  frisby.create('Creating a new user to send the request to')
  .post(testHelper.registerEndpoint, testHelper.createGenericUser())
  .expectStatus(200)
  .expectBodyContains('user_id')
  .afterJSON(function (friend) {
    frisby.create("Send a friend request")
    .post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user.token, friend.user_id))
    .expectStatus(200)
    .toss();
  })
  .toss();
})
.toss();
