var frisby = require('frisby');
var crypto = require('crypto');
var request = require('request');
var strings = require('../api/universal_strings');

var refreshEndpoint = 'http://localhost:3000/api/refresh';
var registerEndpoint = 'http://localhost:3000/api/register';

var genericUser = {
    nickname:'abode',
    password:'password123',
    fname:'abode',
    lname:'saafan',
    gender:'m',
    dob:'25/03/1996',
    email:randomEmail()
  };

 request.post(registerEndpoint, genericUser, (res) => {
 	console.log(res.refresh);
 });

 // Creating a user with valid creds for testing
frisby.create('Register a user using the API with valid credentials to use for refresh testing')
  .post(registerEndpoint, genericUser) 
  .expectStatus(200)
  .expectHeaderContains('content-type', 'application/json')
  .expectBodyContains('token')
  .expectBodyContains('user_id')
  .expectBodyContains('refresh')
.toss();



function randomEmail(){
  return crypto.randomBytes(4).toString('hex') + "@mail.com";
}