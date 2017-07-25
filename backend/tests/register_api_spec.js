var frisby = require('frisby');
var crypto = require('crypto');
var request = require('request');

var registerEndpoint = 'http://localhost:3000/api/register';
// Testing creating a user with valid creds
frisby.create('Register a user using the API')
  .post(registerEndpoint, {
    nickname:'abode',
    password:'password123',
    fname:'abode',
    lname:'saafan',
    gender:'m',
    dob:'25/03/1996',
    email:randomEmail()
  }) 
  .expectStatus(200)
  .expectHeaderContains('content-type', 'application/json')
  .expectBodyContains('token')
  .expectBodyContains('user_id')
.toss();

// Testing creating a user with an invalid email
var genericUser = {
    nickname:'abode',
    password:'password123',
    fname:'abode',
    lname:'saafan',
    gender:'m',
    dob:'25/03/1996',
    email:'abode@mail.com'
  };

request.post(registerEndpoint, genericUser);

frisby.create('Register a user using the API')
  .post(registerEndpoint, genericUser)
  .expectStatus(400)
  .expectHeaderContains('content-type', 'application/json')
  .expectBodyContains('error')
.toss();


function randomEmail(){
  return crypto.randomBytes(4).toString('hex') + "@mail.com";
}