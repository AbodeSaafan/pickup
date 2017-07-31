var frisby = require('frisby');
var crypto = require('crypto');
var request = require('request');
var strings = require('../api/universal_strings');

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

// Creating a user with valid creds
frisby.create('Register a user using the API with valid credentials')
  .post(registerEndpoint, genericUser) 
  .expectStatus(200)
  .expectHeaderContains('content-type', 'application/json')
  .expectBodyContains('token')
  .expectBodyContains('user_id')
  .expectBodyContains('refresh')
.toss();

// Sending a register request without some params
frisby.create('Attempt to register a user using the API with missing parameters')
  .post(registerEndpoint, {
    password:'password123',
    fname:'abode',
    lname:'saafan',
    gender:'m',
    dob:'25/03/1996',
    email:'abode@mail.com'
  }) 
  .expectStatus(400)
  .expectHeaderContains('content-type', 'application/json')
  .expectJSON({
    error: strings.invalidNickname
  })
  
.toss();

// Invalid since we just created one above and we are trying to re-use it
frisby.create('Register a user using the API with an invalid email')
  .post(registerEndpoint, genericUser)
  .expectStatus(400)
  .expectHeaderContains('content-type', 'application/json')
  .expectBodyContains('error')
  .expectJSON({
    error: strings.emailError
  })
.toss();


function randomEmail(){
  return crypto.randomBytes(4).toString('hex') + "@mail.com";
}