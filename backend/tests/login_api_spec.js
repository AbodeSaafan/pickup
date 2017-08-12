var frisby = require('frisby');
var strings = require('../api/universal_strings');
var testHelper = require('./testHelper');

// Sending a login request with an incorrect password
frisby.create('Attempt to login a user using the wrong password')
    .post(testHelper.loginEndpoint, {
        email: "1234@mail.com",
        password: 'wrongPassword'
    })
    .expectStatus(400)
    .expectHeaderContains('content-type', 'application/json')
    .expectJSON({
        error: strings.wrongEmailPassword
    })
    .toss();

frisby.create('Log in a user using the API with valid credentials')
    .post(testHelper.loginEndpoint, {
        email:'6209be52@mail.com',
        password: 'fa2568a8dd82c24a6ee22df3f19d642d'
    })
    .expectStatus(200)
    .toss();
