var frisby = require('frisby');
var crypto = require('crypto');

frisby.create('Register a user using the API')
  .post('http://localhost:3000/api/register', {
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

function randomEmail(){
  return crypto.randomBytes(4).toString('hex') + "@mail.com";
}